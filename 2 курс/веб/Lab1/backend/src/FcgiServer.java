package backend.src;

import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

public class FcgiServer {
    private final Logger log = LoggerConfig.getLogger(FcgiServer.class.getName());
    private final FCGIInterface fcgi;
    private static final String HTTP_RESPONSE = """
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;

    public FcgiServer() {
        this.fcgi = new FCGIInterface();
        Locale.setDefault(Locale.US);
    }

    private void sendResponse(String jsonResponse) {
        var response = String.format(HTTP_RESPONSE, jsonResponse.getBytes(StandardCharsets.UTF_8).length, jsonResponse);

        log.info("\n" + response);
        System.out.println(response);
    }

    private HashMap<String, String> parseRequest(String request) {
        String[] equalities = request.split("&");
        HashMap<String, String> parsedValues = new HashMap<>();

        log.info(Arrays.toString(equalities));

        for (String equality : equalities) {
            String[] keyValue = equality.split("=");
            log.info(keyValue[0] + "    " + keyValue[1]);

            parsedValues.put(keyValue[0], keyValue[1]);
            log.info(parsedValues.toString());
        }
        return parsedValues;
    }

    public void run(){
        log.info("Server started");
        log.info("Waiting for request...");

        while (fcgi.FCGIaccept() >= 0){
            var executionStart = System.nanoTime();
            log.info("Some request has been detected");
            String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");

            if (!method.equals("GET")){
                sendResponse("""
                {
                "error": "invalid request method"
                }
                """);
                log.info("Error response sent");

            } else {
                String request = FCGIInterface.request.params.getProperty("QUERY_STRING");
                log.info("\n" + request);

                var valuesMap = parseRequest(request);
                var x = Integer.parseInt(valuesMap.get("x"));
                var y = Float.parseFloat(valuesMap.get("y"));
                var r = Float.parseFloat(valuesMap.get("r"));

                if (!(MathFunctions.hitCheck(x, y, r))){
                    sendResponse("""
                {
                "x": %d,
                "y": %.2f,
                "r": %.2f,
                "execution_time": "%tS",
                "result": "Miss"
                }
                """.formatted(x, y, r, System.nanoTime() - executionStart));
                    log.info("Miss response sent");

                } else {
                    sendResponse("""
                {
                "x": %d,
                "y": %.2f,
                "r": %.2f,
                "execution_time": "%tS",
                "result": "Hit"
                }
                """.formatted(x, y, r, System.nanoTime() - executionStart));
                    log.info("Hit response sent");
                }
            }
        }
    }
}
