package edu.nau.elc.mailers.domain.forcecomplete;

public class Test implements Comparable<Test> {
    private String name;
    private String path;
    private Course parent;

    public Test(String name, String path, Course parent) {
        this.name = name;
        this.path = path;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Course getParent() {
        return parent;
    }

    public void setParent(Course parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        return getName().equals(test.getName())
                && getPath().equals(test.getPath())
                && getParent().equals(test.getParent());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getPath().hashCode();
        result = 31 * result + getParent().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Test{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public int compareTo(Test other) {
        int pathComp = path.compareTo(other.getPath());

        if (pathComp == 0) {
            return name.compareTo(other.getName());
        } else {
            return pathComp;
        }
    }
}
