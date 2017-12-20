package server.security;
import java.security.MessageDigest;

public class Digester {

private String salt;

private static MessageDigest digester;

static{

    try {
        digester = MessageDigest.getInstance("MD5");
    } catch (Exception e){
        e.printStackTrace();
    }

}

public void setSalt(String salt){
    this.salt = salt;
}

public static String hash(String password){
    return Digester.performHashing(password);
}

    /**
     * Metode til at hash'e password'et med salt
     * @param password
     * @param username
     * @param createdTime
     * @return En salted og hashet string
     */

    //Vi benytter createtime til vores salt, som er fundet ved linket:
    // https://stackoverflow.com/questions/12663710/android-datetime-to-11-digit-unix-timestamp

    public static String hashWithSalt(String password, String username, Long createdTime){
    String salt = Digester.performHashing(username+createdTime.toString());
    password = password + salt;
    return Digester.performHashing(password);
}

    /**
     * Metode der bruges til at hashe en String
     * @param str
     * @return hashed string
     */
    private static String performHashing(String str){
        digester.update(str.getBytes());
        byte[] hash = digester.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte aHash : hash) {
            if ((0xff & aHash) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & aHash)));
            } else {
                hexString.append(Integer.toHexString(0xFF & aHash));
            }
        }
        System.out.printf(hexString.toString());
        return hexString.toString();
    }

}
