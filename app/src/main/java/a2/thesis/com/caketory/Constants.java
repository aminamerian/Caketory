package a2.thesis.com.caketory;

/**
 * Created by Amin on 24/01/2018.
 */

public class Constants {

    static String ServerAddress = "http://192.168.1.4:85";
    private static String rootAddress = ServerAddress + "/Caketory/admin";

    static String productAPI = rootAddress + "/api/get-all-products.php";

    static String AccessKey = "12345";

    static String imagesDirectory = rootAddress + "/";
    //TODO change above line to below, after modifying product_image field in database to just the name of images, not directory
    //static String imagesDirectory = rootAddress + "/upload/images";

}
