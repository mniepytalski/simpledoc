package pl.cbr.notebook.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JsonFormatter {

    public String jsonFormatter(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Object obj = null;
        try {
            obj = mapper.readValue(fixJson(json), Object.class);
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            return writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Prosta metoda "naprawiania" JSON-a
    private static String fixJson(String json) {
        int openCurly = countChar(json, '{');
        int closeCurly = countChar(json, '}');
        int openSquare = countChar(json, '[');
        int closeSquare = countChar(json, ']');

        StringBuilder sb = new StringBuilder(json);

        // Dodaj brakujące zamykające klamry
        for (int i = 0; i < openCurly - closeCurly; i++) {
            sb.append('}');
        }

        // Dodaj brakujące zamykające nawiasy kwadratowe
        for (int i = 0; i < openSquare - closeSquare; i++) {
            sb.append(']');
        }

        return sb.toString();
    }

    private static int countChar(String str, char c) {
        return (int) str.chars().filter(ch -> ch == c).count();
    }
}
