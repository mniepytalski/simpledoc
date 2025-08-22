package pl.cbr.notebook.ui.swing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class KeywordTest {

    @Test
    void analiseTest() {
        String text = "\n" +
                "1. test 1\n" +
                "2. test 2\n" +
                "3. test 3\n" +
                "4. test 4\n" +
                "5. test 5\n" +
                "\n" +
                "#wakacje";
        List<DocumentPart> analise = Keyword.analise(text);

        System.out.println(analise.size());
    }

    @Test
    void testTest() {
        String text = "test\r\ntest\r\ntest";
        String s = text.replaceAll("\r+\n", "\r\n");
        System.out.println(s.length());
    }

    @Test
    void search() {
        Set<String> search = Keyword.TAG.search("test #tag, tag, tag hej ho #Zespol #nowy_tag12");
        Assertions.assertEquals(3, search.size());
    }
}
