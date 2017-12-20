package server.Controllers;


import com.google.gson.Gson;
import server.DBWrapper;
import server.models.Quiz;
import server.utility.Log;

import java.io.IOException;
import java.util.ArrayList;



public class QuizController {

    Log log = new Log();
    Gson gson;
    DBWrapper db = new DBWrapper();
    ArrayList<Quiz> quizzes;

    public QuizController() {
        this.gson = gson;
    }

    /**
     *
     * @param quiz
     * @return quizCreated
     * @throws Exception
     * Denne metode sender et Quiz-objekt videre til DBWrapper, for at lagre objektet i databasen.
     * Derefter returneres objektet (quizCreated)
     */
    public Quiz createQuiz (Quiz quiz) throws Exception {
        log.writeLog(this.getClass().getName(), this, "We are now creating a quiz", 0);

        Quiz quizCreated = db.createQuiz(quiz);

        return quizCreated;
    }

    /**
     *
     * @param courseId
     * @return allQuizzes
     * @throws IOException
     * @throws ClassNotFoundException
     * Denne metode laver, på baggrund af courseId, en arrayList der indeholder alle quizzer. Derefter returneres (allQuizzes)
     */

    public ArrayList<Quiz> getQuizzes(int courseId) throws IOException, ClassNotFoundException {

        log.writeLog(this.getClass().getName(), this, "We are now getting quizzes", 0);
        ArrayList<Quiz> allQuizzes = db.getQuizzes(courseId);
        return allQuizzes;

    }

    /**
     *
     * @param quizId
     * @return db.deleteQuiz(quizId)
     * @throws Exception
     * Denne metode sletter en quiz, via DBWrapper, udfra quizzens ID.
     */

    public Boolean deleteQuiz (int quizId) throws Exception {
        log.writeLog(this.getClass().getName(), this, "We are deleting a quiz", 0);
       return db.deleteQuiz(quizId);
    }
}