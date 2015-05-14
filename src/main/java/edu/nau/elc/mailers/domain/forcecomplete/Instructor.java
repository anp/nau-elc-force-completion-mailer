package edu.nau.elc.mailers.domain.forcecomplete;

import java.util.ArrayList;
import java.util.List;

public class Instructor implements Comparable<Instructor> {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private List<Course> courses;

    public Instructor(String uid, String firstName, String lastName, String email) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        courses = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Course> getCourses() {
        return courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instructor that = (Instructor) o;

        if (getUid() != null ? !getUid().equals(that.getUid()) : that.getUid() != null) return false;
        if (getFirstName() != null ? !getFirstName().equals(that.getFirstName()) : that.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(that.getLastName()) : that.getLastName() != null)
            return false;
        return !(getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null);

    }

    @Override
    public int hashCode() {
        int result = getUid() != null ? getUid().hashCode() : 0;
        result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Instructor{");
        builder.append("uid='");
        builder.append(uid);
        builder.append("', firstName='");
        builder.append(firstName);
        builder.append("', lastName='");
        builder.append(lastName);
        builder.append("', email='");
        builder.append(email);
        builder.append("', courses={'");

        for (Course c : courses) {
            builder.append("\n\t");
            builder.append(c.toString());
        }

        builder.append("\n}}");

        return builder.toString();
    }

    @Override
    public int compareTo(Instructor other) {
        return this.uid.compareTo(other.getUid());
    }
}
