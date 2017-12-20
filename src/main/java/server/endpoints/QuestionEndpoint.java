package server.endpoints;
import com.google.gson.Gson;
import server.utility.Log;
import server.Controllers.QuestionController;
import server.models.Question;
import server.security.XORController;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;


@Path("/question")
public class QuestionEndpoint {
    Log log = new Log();
    QuestionController controller = new QuestionController();
    XORController crypter = new XORController();


    /**
     *
     * @param quizId
     * @return Response
     * Henter alle spørgsmål efter quizId
     */

    @GET
    @Path ("{quizId}")
    public Response getQuestions(@PathParam("quizId") int quizId) throws IOException, ClassNotFoundException {
        log.writeLog(this.getClass().getName(), this, "We are now getting questions", 2);

        ArrayList<Question> question = controller.getQuestions(quizId);
        String output = new Gson().toJson(question);
        String encryptedOutput = crypter.encryptXOR(output);





        return Response
                .status(200)
                .type("application/json")
                .entity(encryptedOutput)
                .build();

    }

    /**
     *
     * @param jsonQuestion
     * @return Response
     * Metode til at oprette et spørgsmål i databasen ved at få en json streng af et spørgsmål
     */

    @POST
    public Response createQuestion(String question) throws Exception {

        log.writeLog(this.getClass().getName(), this, "We are now creating a question", 2);

        System.out.println("Vi dekryptere: "+question);

        String decryptedQuestion = crypter.decryptXOR(question);

        System.out.println("Vi dekryptere: "+decryptedQuestion);

        Question newQuestion = controller.createQuestion(decryptedQuestion);

        String output = new Gson().toJson(newQuestion);

        String encryptedOutput = crypter.encryptXOR(output);


        return Response
                .status(200)
                .type("application/json")
                .entity(encryptedOutput)
                .build();

    }
}
