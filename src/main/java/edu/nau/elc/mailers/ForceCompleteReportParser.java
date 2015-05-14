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

        try {
            BufferedReader rdr = new BufferedReader(new FileReader(report));

            String line = rdr.readLine();
            log.info("Beginning parsing of force completion report.");
            while ((line = rdr.readLine()) != null) {
                String[] elems = line.split("\t");

                String uid = elems[uidIndex];

                if (!instructors.containsKey(uid)) {
                    instructors.put(uid, new Instructor(elems[uidIndex],
                            elems[firstNameIndex], elems[lastNameIndex], elems[emailIndex]));
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

        return instructors.entrySet().stream()
                .map(Map.Entry::getValue).sorted().collect(Collectors.toList());
    }
}
