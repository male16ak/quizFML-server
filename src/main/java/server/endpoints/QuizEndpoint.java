package server.endpoints;
import com.google.gson.Gson;
import server.Controllers.QuizController;
import server.models.Quiz;
import server.security.XORController;
import server.utility.Log;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;




@Path("/quiz")
public class QuizEndpoint {

    Log log = new Log();

    QuizController controller = new QuizController();

    /**
     *
     * @param courseId
     * @return output (allQuizzes)
     * @throws IOException
     * @throws ClassNotFoundException
     * Denne metode opretter en arrayList der indeholder alle quizzer, ved at hente quizzes via QuizController på CourseId
     * Derefter krypteres outputtet og ændres fra Gson til Json. Derefter returneres dette outout (alle quizzer)
     */
    @GET
    @Path("{id}")
    public Response getQuizzes(@PathParam("id") int courseId) throws IOException, ClassNotFoundException {

        log.writeLog(this.getClass().getName(), this, "We are getting quizzes", 2);

        ArrayList<Quiz> allQuizzes = controller.getQuizzes(courseId);
        String output = new Gson().toJson(allQuizzes);


        output = XORController.encryptDecryptXOR(output);

        return Response
                .status(200)
                .type("application/json")
                .entity(new Gson().toJson(output))
                .build();
    }

    /**
     *
     * @param quiz
     * @return EncryptedOutput (foundQuiz)
     * @throws Exception
     * Denne metode laver et objekt (foundQuiz) ved at køre QuizController med stringen quiz. Derefter laves outputtet om
     * til Json fra Gson, og dette XOR-krypteres. Derefter returneres outputtet.
     */
    @POST
    public Response createQuiz(String quiz) throws Exception {

        log.writeLog(this.getClass().getName(), this, "We are creating a quiz", 0);

        Quiz foundQuiz = controller.createQuiz(new Gson().fromJson(quiz, Quiz.class));

        String output = new Gson().toJson(foundQuiz);
        String encryptedOutput = XORController.encryptDecryptXOR(output);
        encryptedOutput = new Gson().toJson(encryptedOutput);

        return Response
                .status(200)
                .type("application/json")
                .entity(encryptedOutput)
                .build();
    }

    /**
     *
     * @param quizID
     * @return enten en besked om at quizzen er slettet korrekt, eller ikke er slettet korrekt.
     * @throws Exception
     * Denne metode laver en Boolean og kører QuizController.deleteQuiz med parametret quizID. Hvis Boolean returnerer 'true'
     * er quizzen slettet. Ellers er quizzen ikke slettet.
     */

    @DELETE
    @Path("{id}")
    public Response deleteQuiz(@PathParam("id") int quizID) throws Exception {

        log.writeLog(this.getClass().getName(), this, "We are now in process of deleting a quiz", 2);

        Boolean deleteQuiz = controller.deleteQuiz(quizID);


        if (deleteQuiz == true) {
            log.writeLog(this.getClass().getName(), this, "Quiz bliver slettet", 2);
            return Response
                    .status(200)
                    .type("application/json")
                    .entity(new Gson().toJson("Den burde vÊre slettet korrekt"))
                    .build();
        } else {
            log.writeLog(this.getClass().getName(), this, "Quizzen er ikke slettet korrekt", 2);
            return Response
                    .status(200)
                    .type("application/json")
                    .entity(new Gson().toJson("Den er vidst ikke slettet korrekt"))
                    .build();
        }

}}

