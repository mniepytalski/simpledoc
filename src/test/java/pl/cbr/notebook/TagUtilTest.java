package pl.cbr.notebook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class TagUtilTest {

    @Test
    void search() {
        Set<String> search = TagUtil.search("test #tag, tag, tag hej ho #Zespol #nowy_tag12");
        Assertions.assertEquals(3, search.size());
    }
}