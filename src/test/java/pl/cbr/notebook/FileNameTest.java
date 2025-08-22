package pl.cbr.notebook;

import org.junit.jupiter.api.Test;

class FileNameTest {

    @Test
    void testGetFileId()  {
        String id = FileName.get();
        System.out.println(id);
    }
}