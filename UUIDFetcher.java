//package

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UUIDFetcher {

    // URL for UUID Fetching
    private static final String URL = "https://playerdb.co/api/player/minecraft/%s";

    /**
     * fetch the UUID of a players name
     *
     * @param name of the player
     * @return the uuid of the player
     */
    public static UUID getUUID(String name) {
        name = name.toLowerCase(); // Had some issues with upper-case letters in the username, so I added this to make sure that doesn't happen.

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(String.format(URL, name)).openConnection();

            // Connection parameters needs to be set otherwise the API won't accept the connection
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0");
            connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            connection.addRequestProperty("Pragma", "no-cache");
            connection.setReadTimeout(5000);

            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) response.append(line);

                final JsonElement parsed = new JsonParser().parse(response.toString());

                if (parsed == null || !parsed.isJsonObject()) {
                    return null;
                }

                JsonObject data = parsed.getAsJsonObject(); // Read the returned JSON data.


                return UUID.fromString(
                        data.get("data")
                                .getAsJsonObject()
                                .get("player")
                                .getAsJsonObject()
                                .get("id") // Grab the UUID.
                                .getAsString());
            }
        } catch (Exception ignored) {
            // not needed because it only throws an exception if username doesn't exist
        }
        return null;
    }
}

