package server.models;

public class Course {

    private int courseId;
    private String courseTitel;

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitel() {
        return courseTitel;
    }

    public void setCourseTitel(String courseTitel) {
        this.courseTitel = courseTitel;
    }

    public Course(int courseId, String courseTitel) {
        this.courseId = courseId;
        this.courseTitel = courseTitel;
    }

}
