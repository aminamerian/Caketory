package a2.thesis.com.caketory;

/**
 * Created by Amin on 24/01/2018.
 */

public class Constants {

    static String ServerAddress = "http://192.168.1.4:85";
    private static String rootAddress = ServerAddress + "/Caketory/admin";

    private static String ACCESS_KEY = "12345";
    static String accessKey = "?access=" + ACCESS_KEY;

    static String productAPI = rootAddress + "/api/get-all-products.php" + accessKey;
    static String categoryAPI = rootAddress + "/api/get-all-categories.php" + accessKey;


    static String imagesDirectoryR = rootAddress + "/";
    //TODO change above line to below, after modifying product_image field in database to just the name of images, not directory
    static String imagesDirectory = rootAddress + "/upload/images/";

}
