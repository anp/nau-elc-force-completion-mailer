package edu.nau.elc.mailers.domain.forcecomplete;

import java.util.ArrayList;
import java.util.List;

public class Course implements Comparable<Course> {
    private String courseID;
    private String courseName;
    private Instructor instructor;
    private List<Test> tests;

    public Course(String courseID, String courseName, Instructor instructor) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.instructor = instructor;

        this.tests = new ArrayList<>();
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public List<Test> getTests() {
        return tests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return getCourseID().equals(course.getCourseID())
                && getCourseName().equals(course.getCourseName())
                && getInstructor().equals(course.getInstructor());

    }

    @Override
    public int hashCode() {
        int result = getCourseID().hashCode();
        result = 31 * result + getCourseName().hashCode();
        result = 31 * result + getInstructor().hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();

        build.append("Course{");
        build.append("courseID='");
        build.append(courseID);
        build.append("', courseName='");
        build.append(courseName);
        build.append("', tests={");

        for (Test t : tests) {
            build.append("\n\t\t");
            build.append(t.toString());
        }


        return build.toString();
    }

    @Override
    public int compareTo(Course other) {
        return courseID.compareTo(other.getCourseID());
    }
}
