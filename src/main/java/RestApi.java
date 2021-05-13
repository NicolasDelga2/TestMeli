
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sun.tools.javac.util.Constants.format;
import static java.net.http.HttpClient.Version.HTTP_2;

import java.util.logging.SimpleFormatter;

public class RestApi {
    public static void main(String[] args) {
        // This is the variable for the site_id
        String site_id = "MLA";

        // This is the variable for the seller_id
        String seller_id = "179571326";

        // calling the method.
        getSellerItems(site_id, seller_id);

    }

    /***
     * This method is to generate the file with the seller's items.
     * @param site_id
     * @param seller_id
     */
    public static void getSellerItems(String site_id, String seller_id) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HTTP_2).build();
        String url = "https://api.mercadolibre.com/sites/" + site_id + "/search?seller_id=" + seller_id;

        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        try {
            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            try {
                JSONObject json = new JSONObject(response.body());
                JSONArray itemsArray = json.getJSONArray("results");

                String mensaje = "";
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject entry = itemsArray.getJSONObject(i);
                    String id = entry.getString("id");
                    String title = entry.getString("title");
                    String category_id = entry.getString("category_id");
                    String category_name = getCategory(category_id);
                    mensaje += "" +
                            id + " del ítem, " + title + " del item," + category_id + " donde está" +
                            " publicado, " + category_name + " de la categoría \n \n";

                }
                 writeLog(System.getProperty("user.home") + "/Desktop/", mensaje);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(RestApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is for create the Log file.
     * @param filePath path of the file destination.
     * @param message here the message for the file.
     */
    public static void writeLog(String filePath, String message) {
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(filePath + "itemsList" + ".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info(message);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * @param category_id insert the category_id of the product.
     * @return returns a String with the category name.
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getCategory(String category_id) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HTTP_2).build();
        String url = "https://api.mercadolibre.com/categories/" + category_id;

        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        JSONObject json = null;
        try {
            json = new JSONObject(response.body());
            String categoryName = json.getString("name");
            return categoryName;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
