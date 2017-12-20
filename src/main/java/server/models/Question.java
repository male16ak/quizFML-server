package server.models;

public class Question {

    private int quizId;
    private int questionId;
    private String questionTitle;


    public Question(int questionId, String questionTitle, int quizId) {

        this.quizId = quizId;
        this.questionTitle = questionTitle;
        this.questionId = questionId;

    }

    public Question() {

    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

}