package edu.nau.elc.mailers;

import edu.nau.elc.mailers.domain.forcecomplete.Course;
import edu.nau.elc.mailers.domain.forcecomplete.Instructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ForceCompleteMailer {

    private static final Logger log = LoggerFactory.getLogger(ForceCompleteMailer.class);

    public static void main(String[] args) {

        File reportFile = new File(args[0]);

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


    }
}
