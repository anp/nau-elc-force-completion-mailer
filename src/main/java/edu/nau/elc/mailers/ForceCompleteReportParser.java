package edu.nau.elc.mailers;

import edu.nau.elc.mailers.domain.forcecomplete.Course;
import edu.nau.elc.mailers.domain.forcecomplete.Instructor;
import edu.nau.elc.mailers.domain.forcecomplete.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForceCompleteReportParser {

    private static final Logger log = LoggerFactory.getLogger(ForceCompleteReportParser.class);

    private static int uidIndex = 0;
    private static int firstNameIndex = 1;
    private static int lastNameIndex = 2;
    private static int emailIndex = 3;
    private static int courseIDIndex = 4;
    private static int courseNameIndex = 5;
    private static int testNameIndex = 6;
    private static int pathIndex = 7;

    public static List<Instructor> parseForceCompleteReportFile(File report) {
        Map<String, Instructor> instructors = new HashMap<>();

        LocalDate date = LocalDate.now().minus(1, ChronoUnit.DAYS);
        String dayBeforeToday = date.format(DateTimeFormatter.ofPattern("E, MM/dd/yyyy"));
        String termCode = null;

        try {
            BufferedReader rdr = new BufferedReader(new FileReader(report));

            String line = rdr.readLine();
            log.info("Beginning parsing of force completion report.");
            while ((line = rdr.readLine()) != null) {
                String[] elems = line.split("\t");

                if (termCode == null) {
                    termCode = parseTermCode(elems[courseIDIndex].substring(0, 4));
                }

                String uid = elems[uidIndex];

                if (!instructors.containsKey(uid)) {
                    instructors.put(uid, new Instructor(elems[uidIndex],
                            elems[firstNameIndex], elems[lastNameIndex], elems[emailIndex], termCode, dayBeforeToday));
                }

                Instructor instructor = instructors.get(uid);

                List<Course> courses = instructor.getCourses();

                Course currentCourse = new Course(elems[courseIDIndex], elems[courseNameIndex], instructor);

                int currentCourseIndex = courses.indexOf(currentCourse);

                if (currentCourseIndex > -1) {
                    currentCourse = courses.get(currentCourseIndex);
                } else {
                    courses.add(currentCourse);
                }

                Test test = new Test(elems[testNameIndex], elems[pathIndex], currentCourse);

                currentCourse.getTests().add(test);
            }


        } catch (IOException ioe) {
            log.error("Problem reading the report file.", ioe);
        }


        List<Instructor> returnedInstructors = instructors.entrySet().stream()
                .map(Map.Entry::getValue).sorted().collect(Collectors.toList());

        for (Instructor i : returnedInstructors) {
            Collections.sort(i.getCourses());
            for (Course c : i.getCourses()) {
                Collections.sort(c.getTests());
            }
        }

        return returnedInstructors;
    }

    private static String parseTermCode(String termCode) {
        char[] chars = termCode.toCharArray();

        //this shouldn't happen as long as the report is well-formed
        if (chars.length != 4) throw new IllegalArgumentException("Bad term code!");

        String year = "" + chars[1] + chars[2];

        String term;

        switch (chars[3]) {
            case '1':
                term = "Spring";
                break;
            case '4':
                term = "Summer";
                break;
            case '7':
                term = "Fall";
                break;
            case '8':
                term = "Winter";
                break;
            default:
                term = "INVALID";
                break;
        }
        return term + " 20" + year;
    }
}
