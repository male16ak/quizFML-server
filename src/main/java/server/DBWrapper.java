package server;

import server.Controllers.Config;
import server.models.*;
import server.security.Digester;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DBWrapper {


    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private static Connection connection = null;

    /**
     * Her oprettes forbindelsen til vores amazon database.
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public static Connection getConnection() throws SQLException, IOException, ClassNotFoundException {


        try {
            try {
                Class.forName(JDBC_DRIVER).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            connection = DriverManager.getConnection("jdbc:mysql://" + Config.getDatabaseHost() + ":" + Config.getDatabasePort() + "/" + Config.getDatabaseName(), Config.getDatabaseUsername(), Config.getDatabasePassword());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Denne metode bruges når vi logger ind.
     * Den tager to String objekter, og undersøger om disse stemmer overens med data fra vores database.
     * Til sidst returnere den et user objekt - hvis dette eksisterer, som matcher de to strings.
     *
     * @param username
     * @param password
     * @return userFound_Final
     * @throws Exception
     */
    public static User authorizeUser(String username, String password) throws Exception {
        Connection connection = getConnection();
        User userFound = null;
        User userFound_final = null;


        //Get user by username
        PreparedStatement getUserByUserName = connection.prepareStatement("select * from user where username  = ? ORDER BY createdTime DESC LIMIT 1");
        getUserByUserName.setString(1, username);
        ResultSet resultSet = getUserByUserName.executeQuery();

        String saltet_password = "";
        while (resultSet.next()) {
            userFound = new User();
            userFound.setUsername(resultSet.getString("userName"));
            userFound.setCreatedTime(resultSet.getLong("createdTime"));
            saltet_password = Digester.hashWithSalt(password, resultSet.getString("userName"), resultSet.getLong("createdTime"));
        }

        try {
            PreparedStatement authenticate = connection.prepareStatement("select * from user where username = ? AND password = ?");
            authenticate.setString(1, username);
            authenticate.setString(2, saltet_password);

            ResultSet resultSet2 = authenticate.executeQuery();

            while (resultSet2.next()) {
                try {

                    userFound_final = new User();
                    userFound_final.setUsername(resultSet2.getString("userName"));
                    userFound_final.setPassword(resultSet2.getString("password"));
                    userFound_final.setUserId(resultSet2.getInt("id"));
                    userFound_final.setFirstName(resultSet2.getString("firstName"));
                    userFound_final.setLastName(resultSet2.getString("lastName"));
                    userFound_final.setType(resultSet2.getInt("type"));
                    userFound_final.setCreatedTime(resultSet2.getLong("createdTime"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userFound_final;
    }

    /**
     * Denne metode opretter en ny bruger i vores database.
     * Den modtager et User objekt når det bliver kaldt, og dette User objekt bliver derefter
     * oprettet og gemt i vores database. Hvis brugeren bliver oprettet uden fejl, retunerer metoden objektet.
     * @param createUser
     * @return createUser
     */

    public static User createUser(User createUser) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        //String PS = "INSERT INTO user (firstName, lastName, userName, password, type) VALUES (?,?,?,?,?)";

        //String PS = "INSERT INTO user (firstName) VALUES (" + createUser.getFirstName()+")";
        try {
            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement("INSERT INTO user (firstName, lastName, userName, password, type, createdTime) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, createUser.getFirstName());
            preparedStatement.setString(2, createUser.getLastName());
            preparedStatement.setString(3, createUser.getUsername());
            preparedStatement.setString(4, Digester.hashWithSalt(createUser.getPassword(), createUser.getUsername(), createUser.getCreatedTime()));
            preparedStatement.setInt(5, createUser.getType());
            preparedStatement.setLong(6, createUser.getCreatedTime());

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                createUser.setUserId(rs.getInt(1));
            }

            return createUser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(conn);
            close(preparedStatement);
        }

    }

    /**Denne metode opretter en ny quiz i vores database.
     * Den modtager et Quiz objekt når det bliver kaldt, og dette Quiz objekt bliver derefter
     * oprettet og gemt i vores database. Hvis quiz'en bliver oprettet uden fejl, retunerer metoden objektet.
     * @param quiz
     * @return quiz
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */


    public static Quiz createQuiz(Quiz quiz) throws SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        conn = DBWrapper.getConnection();
        try {
            PreparedStatement createQuiz = conn.prepareStatement("INSERT INTO fmldb.quiz (quizTitle, course_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            createQuiz.setString(1, quiz.getQuizTitle());
            createQuiz.setInt(2, quiz.getCourseId());

            int rowsAffected = createQuiz.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet rs = createQuiz.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    int autoIncrementedId = rs.getInt(1);
                    quiz.setQuizId(autoIncrementedId);
                } else {
                    quiz = null;
                }
                return quiz;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn);

        }
        return null;
    }


    public static Boolean deleteUser(int userId) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String PS = "DELETE FROM fmldb.user WHERE fmldb.user.id = ?";
        int check = 0;
        try {
            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement(PS);
            preparedStatement.setInt(1, userId);
            check = preparedStatement.executeUpdate();

            if (check == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn);
            close(preparedStatement);
        }
        return false;
    }
    /**
     * Denne metode bruges når Quiz objekter skal slettes fra vores database.
     * Metoden er boolean, da den skal returnere true, hvis quiz'en bliver slettet
     * og false hvis det ikke lykkedes. Metoden skal indholde en int, som repræsenterer den
     * respektive quiz'. Denne int bruges til at finde den quiz der skal slettes i databasen.
     * @param quizId
     * @return true or false
     * @throws Exception
     */

    public static Boolean deleteQuiz(int quizId) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String PS = "DELETE FROM fmldb.quiz WHERE fmldb.quiz.id = ?";
        int check = 0;
        try {
            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement(PS);
            preparedStatement.setInt(1, quizId);
            check = preparedStatement.executeUpdate();

            if (check == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn);
            close(preparedStatement);
        }
        return false;
    }

    /**Denne metode opretter et nyt Question i vores database.
     * Den modtager et Question objekt når det bliver kaldt, og dette Question objekt bliver derefter
     * oprettet og gemt i vores database. Hvis Question'et bliver oprettet uden fejl, retunerer metoden objektet.
     * @param question
     * @return question
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Question createQuestion(Question question) throws SQLException, IOException, ClassNotFoundException {
        Connection conn = DBWrapper.getConnection();



        try {

            //preparedStatement = conn.prepareStatement("INSERT INTO question (questionTitle, quiz_id) VALUES (?, ?)");
            PreparedStatement createQuestion = conn.prepareStatement("INSERT INTO fmldb.question (questionTitle, quiz_id)\n" +
                    "VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            System.out.println("title: " + question.getQuestionTitle() + " quizId: " + question.getQuizId());
            createQuestion.setString(1, question.getQuestionTitle());
            createQuestion.setInt(2, question.getQuizId());


            int rowsAffected = createQuestion.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet rs = createQuestion.getGeneratedKeys();
                if(rs != null && rs.next()){
                    int id = rs.getInt(1);
                    question.setQuestionId(id);
                } else {
                    question = null;
                }
                return question;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn);

        }
        return null;
    }





    /**
     * Denne metode opretter et nyt Choice i vores database.
     * Den modtager et Choice objekt når det bliver kaldt, og dette Choice objekt bliver derefter
     * oprettet og gemt i vores database. Hvis Choice'et bliver oprettet uden fejl, retunerer metoden objektet.
     * @param choice
     * @return choice
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Choice createChoice(Choice choice) throws IllegalArgumentException, SQLException, IOException, ClassNotFoundException {
             Connection conn = DBWrapper.getConnection();

        try {
            PreparedStatement createChoice = conn.prepareStatement("INSERT INTO fmldb.choice (choiceTitle, answer, question_id) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            createChoice.setString(1, choice.getChoiceTitle());
            createChoice.setInt(2, choice.isAnswer());
            createChoice.setInt(3, choice.getQuestionId());

            int rowsAffected = createChoice.executeUpdate();
            if(rowsAffected == 1) {
                ResultSet rs = createChoice.getGeneratedKeys();
                if(rs != null && rs.next()) {
                    int id = rs.getInt(1);
                    choice.setChoiceId(id);
                } else {
                    choice = null;
                }
                return choice;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            close(conn);
        }
        return null;
    }




    /**
     * Denne metode bruges når vi skal have en liste af vores User objektet.
     * Metoden opretter et arrayList og fylder det med vores User objektet fra databasen.
     * Til sidst retuneres det fulde arrayList.
     * @return allUsers
     */
    public static ArrayList<User> getUsers() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        ArrayList<User> allUsers = new ArrayList<>();
        try {
            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement("SELECT * FROM user");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("userName"), rs.getString("password"), rs.getString("firstName"), rs.getString("lastName"), rs.getInt("type"), rs.getLong("createdTime"));
                allUsers.add(user);

            }
            return allUsers;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn);
            close(rs);
            close(preparedStatement);
        }
        return null;
    }

    /**
     * Denne metode bruges når vi skal have en liste af vores Course objektet.
     * Metoden opretter et arrayList og fylder det med vores Course objektet fra databasen.
     * Til sidst retuneres det fulde arrayList.
     * @return allCourses
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public static ArrayList<Course> getCourses() throws IOException, ClassNotFoundException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Course> allCourses = new ArrayList<>();
        try {
            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement("SELECT * FROM fmldb.course");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Course course = new Course(rs.getInt(1), rs.getString(2));
                allCourses.add(course);
            }
            return allCourses;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn);
            close(rs);
            close(preparedStatement);
        }
        return null;
    }

    /**
     * Metoden her bruges til at få en liste af Quiz objekter, som relaterer til det specifikke fag (Course),
     * i vores database. Metoden skal have en int, som bruges til at søge efter courseId. Faget med det respektive
     * id, bliver derefter fundet og alle Quiz'es der relaterer til dette fag, puttes i et arrayList.
     * Dette arrayList bliver så til sidst returneret.
     * @param courseId
     * @return allQuizzes or null
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public static ArrayList<Quiz> getQuizzes(int courseId) throws IOException, ClassNotFoundException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Quiz> allQuizzes = new ArrayList<>();
        try {
            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement("SELECT q.* FROM fmldb.quiz q INNER JOIN fmldb.course c ON q.course_id = c.id WHERE q.course_id =" + courseId + ";");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Quiz quiz = new Quiz(rs.getInt(1), rs.getString(2), rs.getInt(3));
                allQuizzes.add(quiz);
            }
            return allQuizzes;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn);
            close(rs);
            close(preparedStatement);
        }
        return null;
    }

    /**
     *  Metoden her bruges til at få en liste af Question objekter, som relaterer til den specifikke quiz,
     * i vores database. Metoden skal have en int, som bruges til at søge efter quizId. Quiz'en med det respektive
     * id, bliver derefter fundet og alle Question objekter der relaterer til denne Quiz, puttes i et arrayList.
     * Dette arrayList bliver så til sidst returneret.
     *
     * @param quizId
     * @return allQuestions or null
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public static ArrayList<Question> getQuestions(int quizId) throws IOException, ClassNotFoundException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Question> allQuestions = new ArrayList<>();
        try {
            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement("SELECT q.* FROM fmldb.question q INNER JOIN fmldb.quiz qz ON q.quiz_id = qz.id WHERE q.quiz_id = " + quizId + ";");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Question question = new Question(rs.getInt(1), rs.getString(2), rs.getInt(3));
                allQuestions.add(question);
            }
            return allQuestions;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn);
            close(rs);
            close(preparedStatement);
        }
        return null;
    }

    /** Metoden her bruges til at få en liste af Choice objekter, som relaterer til den specifikke Question,
     * i vores database. Metoden skal have en int, som bruges til at søge efter questionId. Question objektet med det respektive
     * id, bliver derefter fundet og alle Choice objekter der relaterer til dette question, puttes i et arrayList.
     * Dette arrayList bliver så til sidst returneret.
     *
     * @param questionID
     * @return allChoices or null
     * @throws IOException
     */
    public static ArrayList<Choice> getChoices(int questionID) throws IOException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Choice> allChoices = new ArrayList<>();
        try {

            conn = DBWrapper.getConnection();
            preparedStatement = conn.prepareStatement("SELECT c.* FROM fmldb.choice c INNER JOIN fmldb.question q ON c.question_id = q.id WHERE c.question_id =" + questionID + ";");

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Choice choice = new Choice(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
                allChoices.add(choice);
            }
            return allChoices;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn);
            close(rs);
            close(preparedStatement);
        }
        return null;
    }


    /**Disse metoder bruges til at lukke den respektive for de forskellige "forbindelser" som bliver åbnet i
     * dbwrapperen. Vi har en metode til at lukke Connection, Statement og ResultSet.
     * @param connection
     */



    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
