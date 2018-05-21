package a2.thesis.com.caketory.Utils;

/**
 * Created by Amin on 24/01/2018.
 */

public class Constants {

    private static String Localhost = "http://192.168.1.101:85";
    private static String RaspberryPi = "http://192.168.1.85";
    private static String ServerAddress = Localhost;

    private static String rootAddress = ServerAddress + "/Caketory/admin";
    public static String imagesDirectory = rootAddress + "/upload/images/";

    public static final String headerImageAPI = rootAddress + "/api/get-all-header-images.php";
    public static final String productAPI = rootAddress + "/api/get-all-products.php";
    public static final String categoryAPI = rootAddress + "/api/get-all-categories.php";
    public static final String catProductAPI = rootAddress + "/api/get-a-category-products.php";

    public static final String addOrderAPI = rootAddress + "/api/order-add.php";
    public static final String cancelOrderAPI = rootAddress + "/api/order-cancel.php";
    public static final String getOrderAPI = rootAddress + "/api/order-get.php";

    public static final String SEND_SMS = rootAddress + "/api/authentication/send_sms.php";
    public static final String VERIFY_OTP = rootAddress + "/api/authentication/verify_otp.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    public static final String SMS_ORIGIN = "50002015242298";
}
