package edu.nau.elc.mailers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import edu.nau.elc.mailers.domain.forcecomplete.Course;
import edu.nau.elc.mailers.domain.forcecomplete.Instructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ForceCompleteMailer {

    private static final Logger log = LoggerFactory.getLogger(ForceCompleteMailer.class);

    public static void main(String[] args) {

        if (args.length < 3 || args.length > 4) {
            System.err.println("USAGE: java -jar force-completion-mailer.jar mail_server:port report_file_path default_address [--dry-run]");
        }

        File reportFile = new File(args[1]);

        if (!reportFile.exists()) {
            log.error("Report file does not exist. Exiting.");
            return;
        } else if (!reportFile.canRead()) {
            log.error("Cannot read report file. Exiting.");
            return;
        } else if (reportFile.isDirectory()) {
            log.error("Path provided is a directory, not a file. Exiting.");
            return;
        }

        if (reportFile.isHidden()) {
            log.warn("Report file is marked as hidden. Make sure you've got the right file before proceeding.");
        }

        String defaultAddressStr = args[2];

        boolean dryRun = args.length == 4 && args[3].equals("--dry-run");
        if (dryRun) {
            log.info("DRY RUN: Will send all mail to " + defaultAddressStr);
        }

        List<Instructor> forceCompleteInstructors = ForceCompleteReportParser.parseForceCompleteReportFile(reportFile);

        int numCourses = 0;
        int numTests = 0;

        for (Instructor i : forceCompleteInstructors) {
            numCourses += i.getCourses().size();

            for (Course c : i.getCourses()) {
                numTests += c.getTests().size();
            }
        }

        log.info("Done parsing report.");
        log.info("Report has " + forceCompleteInstructors.size() + " instructors.");
        log.info("Report has " + numCourses + " courses.");
        log.info("Report has " + numTests + " tests.");

        ForceCompleteMailer mailer = new ForceCompleteMailer();

        try {

            TemplateLoader loader = new ClassPathTemplateLoader("/templates", ".hbs");
            Handlebars bars = new Handlebars(loader);

            Template template = bars.compile("force-completion-email");

            String smtpServer = args[0].split(":")[0];
            String smtpPort = args[0].split(":")[1];

            Properties props = new Properties();
            props.put("mail.smtp.auth", "false");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", smtpPort);

            Session session = Session.getDefaultInstance(props);

            InternetAddress fromAddress = new InternetAddress("elc-help@nau.edu");
            fromAddress.setPersonal("e-Learning Center");

            InternetAddress defaultAddress = new InternetAddress(defaultAddressStr);

            String subject = "Bb Learn Force Completion Notification";

            log.info("Sending emails...");

            int emailCount = 0;
            for (Instructor i : forceCompleteInstructors) {
                MimeMessage msg = new MimeMessage(session);


                //TODO change this to i.getEmail() for the final clause when ready to send
                String toAddressString = (dryRun) ? defaultAddressStr : defaultAddressStr;

                InternetAddress toAddress = new InternetAddress(toAddressString);
                toAddress.setPersonal(i.getFirstName() + " " + i.getLastName());

                msg.setFrom(fromAddress);
                msg.setRecipient(Message.RecipientType.TO, toAddress);

                //TODO only set this if not dry-running
                msg.setRecipient(Message.RecipientType.BCC, defaultAddress);
                msg.setSubject(subject);
                msg.setText(template.apply(i), "utf-8", "html");

                Transport.send(msg);

                log.debug("Message successfully sent to " + i.getEmail());

                emailCount++;
            }

            log.info(emailCount + " emails sent.");
        } catch (IOException ioe) {
            log.error("Error reading template file.", ioe);
        } catch (NullPointerException npe) {
            log.error("Unable to load template.", npe);
        } catch (MessagingException me) {
            log.error("Error sending email.", me);
        }
    }
}
