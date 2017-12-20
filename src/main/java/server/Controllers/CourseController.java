package server.Controllers;

import com.google.gson.Gson;
import server.DBWrapper;
import server.models.Course;
import server.utility.Log;

import java.io.IOException;
import java.util.ArrayList;

public class CourseController {
    Gson gson;
    Log log = new Log();

    /**
     *
     * @return c
     * metode for at loade courses
     */

    public ArrayList<Course> getCourses() throws IOException, ClassNotFoundException {

        log.writeLog(this.getClass().getName(), this, "We are now courses", 0);
        DBWrapper db = new DBWrapper();

        ArrayList<Course> c = db.getCourses();
        return c;
    }
}
