package pl.cbr.notebook.utils;

import org.junit.jupiter.api.Test;

public class JsonFormatterTest {

    @Test
    void jsonFormatterTest() {
        String json = "{\"name\":\"Jan\",\"age\":30,\"city\":\"Warszawa\"}";

        String prettyJson = JsonFormatter.jsonFormatter(json);

        System.out.println(prettyJson);
    }
}
