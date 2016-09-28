package eu.manuelgu.discordmc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class to upload a text to hastebin.com and retrieve the url
 */
public class HastebinUtility {
    private static final String BIN_URL = "http://hastebin.com/documents", USER_AGENT = "Mozilla/5.0";
    private static final Pattern PATTERN = Pattern.compile("\\{\"key\":\"([\\S\\s]*)\"\\}");

    /**
     * Upload a haste to www.hastebin.com
     *
     * @param string Content of the haste
     * @return URL of uploaded haste
     */
    public static String upload(final String string) throws IOException {
        final URL url = new URL(BIN_URL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setDoOutput(true);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(string.getBytes());
            outputStream.flush();
        }

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        Matcher matcher = PATTERN.matcher(response.toString());
        if (matcher.matches()) {
            return "http://hastebin.com/" + matcher.group(1);
        } else {
            throw new RuntimeException("Couldn't read response!");
        }
    }

    public static String upload(final File file) throws IOException {
        final StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i++ < 1000) {
                content.append(line).append("\n");
            }
        }
        return upload(content.toString());
    }
}
