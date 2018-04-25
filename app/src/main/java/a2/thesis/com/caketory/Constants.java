package a2.thesis.com.caketory;

/**
 * Created by Amin on 24/01/2018.
 */

public class Constants {

    static String ServerAddress = "http://192.168.1.101:85";
    private static String rootAddress = ServerAddress + "/Caketory/admin";

    private static String ACCESS_KEY = "12345";
    static String accessKey = "?access=" + ACCESS_KEY;

    static String productAPI = rootAddress + "/api/get-all-products.php" + accessKey;
    static String categoryAPI = rootAddress + "/api/get-all-categories.php" + accessKey;

    static String imagesDirectory = rootAddress + "/upload/images/";


    public static final String SEND_SMS = rootAddress + "/api/authentication/send_sms.php";
    public static final String VERIFY_OTP = rootAddress + "/api/authentication/verify_otp.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    public static final String SMS_ORIGIN = "50002015242298";
}
