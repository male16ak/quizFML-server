package server.models;

public class Quiz {

    private int quizId;
    private String quizTitle;
    private int courseId;

    // constructor
    public Quiz (int quizId, String quizTitle, int courseId){
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.courseId = courseId;
    }

    // tom constructor
    public Quiz() {}

    //get metoder
    public int getQuizId(){
        return quizId;
    }

    public String getQuizTitle(){
        return quizTitle;
    }

    public int getCourseId(){
        return courseId;
    }


    //set metoder
    public void setQuizId(int quizId){
        this.quizId = quizId;
    }

    public void setQuizTitle (String quizTitle){
        this.quizTitle=quizTitle;
    }
    public void setCourseId(int courseId){
        this.courseId = courseId;
    }
}
