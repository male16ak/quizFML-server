package server.endpoints;
import com.google.gson.Gson;
import server.Controllers.CourseController;
import server.security.XORController;
import server.utility.Log;
import server.models.Course;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;


@Path("/courses")
public class CourseEndpoint {

    Log log = new Log();

    String demoJson = new Gson().toJson("Courses");

    /**
     *
     * @return henter alle courses
     *
     */

    @GET
    public Response getCourses() throws IOException, ClassNotFoundException {
        CourseController courseController = new  CourseController();
        log.writeLog(this.getClass().getName(), this, "We are now getting courses", 2);

        ArrayList<Course> courses = courseController.getCourses();
        String output = new Gson().toJson(courses);
        String encryptedOutput = XORController.encryptDecryptXOR(output);
        encryptedOutput = new Gson().toJson(encryptedOutput);

        return Response
                .status(200)
                .type("application/json")
                .entity(encryptedOutput)
                .build();
    }
}

