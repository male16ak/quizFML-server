package server.security;

import com.google.gson.Gson;
import server.utility.Globals;

public class XORController {



        /**
         Kryptering skal kunne slås til og fra i configfilen
         Fremgansmåde:
         Klienten sender et krypteret Json objekt til serveren.
         Krypteringen hos klienten medfører, at objektet ikke længere er JSON, men blot en ciffertekst.
         Derfor skal cifferteksten parses til JSON, således at serveren kan modtage det.
         Det modtagede JSON objekt unparses fra JSON til ciffertekst, således at det kan dekrypteres.
         Efter objektet er dekrypteret er det igen JSON.
         Herefter unparses objektet fra JSON igen, således at vi kan bruge objektet i serveren.
         */

    /**
     *
     * @param
     * @return willBeEncrypted
     * Denne metode modtager en string der skal krypteres (willBeEncrypted) og tjekker om Globals.config.getEncryptions Boolean
     * er slået til. Hvis den er, laves en StringBuilder med chars K, O, C og H. Derefter køres stringen igennem er for-loop der
     * krypterer stringen. Derefter returneres det krypterede som string. Hvis Globals.config.getEncryption er deaktiveret,
     * returneres stringen blot ukrypteret.
     */
    // Samzme metode skal laves på klienten for at dekryptere!!
    /*   public static String encryptDecryptXOR(String willBeEncrypted) {
            //If: HER SKAL VI HAVE json.get("ENCRYPTION").getAsBoolean();
            if(Globals.config.getEncryption()) {
                //Vi vælger selv værdierne til nøglen
                char[] key = {'K', 'O', 'C', 'H'};
                //En StringBuilder er en klasse, der gør det muligt at ændre en string
                StringBuilder thisIsEncrypted = new StringBuilder();

                for (int i = 0; i < willBeEncrypted.length(); i++) {
                    thisIsEncrypted.append((char) (willBeEncrypted.charAt(i) ^ key[i % key.length]));
                }



                return thisIsEncrypted.toString();
            } else {
                return willBeEncrypted;
            }

        } */
    public String encryptXOR(String toBeEncrypted) {

        //check if encryption is true in the Config file
        if (Globals.config.getEncryption()) {
            //Vi vælger selv værdierne til nøglen
            char[] key = {'K', 'O', 'C', 'H'};
            //En StringBuilder er en klasse, der gør det muligt at ændre en string
            StringBuilder isBeingEncrypted = new StringBuilder();

            for (int i = 0; i < toBeEncrypted.length(); i++) {
                isBeingEncrypted.append((char) (toBeEncrypted.charAt(i) ^ key[i % key.length]));
            }

            //convert StringBuilder to String and parse as Json
            String isEncrypted = new Gson().toJson(isBeingEncrypted.toString());

            //reuturn encrypted String
            return isEncrypted;
        }else {
            //if encryption is false in the Config file return default value
            return toBeEncrypted;
        }

    }

    //method to decrypt a string parsed as Json

    /**
     * Method responsible for decrypting an encrypted string that has been encrypted by XOR
     * @param toBeDecrypted encrypted string to be decrypted
     * @return the plaintext string
     */
    public String decryptXOR(String toBeDecrypted) {

        //check if encryption is true in the Config file
        if (Globals.config.getEncryption()) {
            //Parse Json with encrypted Json object to a String with the Encrypted Object thats no longer a Json ( {"rewqr"} => rewqr )
            //then Decrypt the object and assign it as a Object in Json format ( rewqr => {"username":"..." }
            toBeDecrypted = new Gson().fromJson(toBeDecrypted, String.class);

            //Vi vælger selv værdierne til nøglen
            char[] key = {'K', 'O', 'C', 'H'};
            //En StringBuilder er en klasse, der gør det muligt at ændre en string
            StringBuilder beingDecrypted = new StringBuilder();

            for (int i = 0; i < toBeDecrypted.length(); i++) {
                beingDecrypted.append((char) (toBeDecrypted.charAt(i) ^ key[i % key.length]));
            }

            //convert StringBuilder to String
            String isDecrypted = beingDecrypted.toString();

            //return String
            return isDecrypted;
        }else {
            //if encryption is false in the Config file return default value
            return toBeDecrypted;
        }

    }



}
