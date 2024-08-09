package codesquad.javacafe.common.util;

import codesquad.javacafe.common.exception.ServerErrorCode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public static JsonObject getBody(HttpServletRequest req) {
        try {
            log.debug("input json");
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            try (BufferedReader reader = new  BufferedReader(new InputStreamReader(req.getInputStream()))) {
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
            }

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonBuffer.toString(), JsonObject.class);


            return jsonObject;
        } catch (IOException exception) {
            throw ServerErrorCode.INTERNAL_SERVER_ERROR.customException("exception occurred while reading request body, exception message = "+exception.getMessage());
        }
    }

    public static void sendResponse(HttpServletResponse res, String jsonResponse) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setStatus(HttpServletResponse.SC_CREATED);

        try {
            res.getWriter().write(jsonResponse);
        } catch (IOException exception) {
            throw ServerErrorCode.INTERNAL_SERVER_ERROR.customException(exception.getMessage());
        }
    }
}
