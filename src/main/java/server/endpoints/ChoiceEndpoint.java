package server.endpoints;
//

import com.google.gson.Gson;
import server.Controllers.ChoiceController;
import server.utility.Log;
import server.models.Choice;
import server.security.XORController;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;



@Path("/choice")
public class ChoiceEndpoint {

    Log log = new Log();
    ChoiceController cController = new ChoiceController();
    XORController crypter = new XORController();


    /**
     * @param questionID
     * @return Denne Metode laver et objekt GetChoiceBYID ved at køre questionID. Derefter laves outputtet om.
     * Til Json fra Gson og dette XOR-krypteres. Derefter returneres outputet.
     * @throws IOException
     */


    @GET
    @Path("/{id}")
    public Response getChoiceById(@PathParam("id") int questionID) throws IOException{

        log.writeLog(this.getClass().getName(), this, "We are now getting Choice by Id parameter", 0);
        ArrayList<Choice> choices = cController.getChoices(questionID);

        String output = new Gson().toJson(choices);
        String encryptedOutput = crypter.encryptXOR(output);

        //Choice foundChoice =
        return Response
                .status(200)
                .type("application/json")
                .entity(encryptedOutput)
                .build();
    }

    /**
     * @param
     * @return Metoden indenholder en arraylist med alle choice. Ved at hente choisecontroller ved choiseID.
     *  Derefter krypteres outputtet og ændres fra Gson til Json. Tilsidst retuneres dette output.
     *  @throws Exception
     */
    @POST
    public Response createChoice(String choice) throws Exception {
        log.writeLog(this.getClass().getName(), this, "We are now creating choice", 2);

        System.out.println("Vi dekryptere: "+choice);

        String decryptedChoice = crypter.decryptXOR(choice);

        System.out.println("Vi kryptere: "+decryptedChoice);

        Choice newChoice = cController.createChoice(decryptedChoice);

        String output = new Gson().toJson(newChoice);

        String encryptedOutput = crypter.encryptXOR(output);

        return Response
                .status(200)
                .type("application/json")
                .entity(encryptedOutput)
                .build();


    }


}
