package xyz.dashnetwork.photon.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangUtil {

    public static Property retreiveSkinProperty(UUID account) {
        try {
            String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + account + "?unsigned=false";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            if (connection.getResponseCode() != 200)
                return null;

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
            Property property = new Gson().fromJson(json.getAsJsonArray("properties").get(0), Property.class);

            reader.close();

            return property;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
