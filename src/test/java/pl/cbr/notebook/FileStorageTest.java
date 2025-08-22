package pl.cbr.notebook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileStorageTest {

    public static final String FILE_NAME = "test.txt";
    public static final String FILE_CONTENT = "data";


    @Test
    @Order(1)
    void save() {
        System.out.println("save");
        boolean status = FileStorage.save(FILE_NAME, FILE_CONTENT);
        Assertions.assertTrue(status);
    }

    @Test
    @Order(2)
    void load() throws IOException {
        System.out.println("load");
        FileData fileData = FileStorage.load(FILE_NAME);
        Assertions.assertEquals(FILE_CONTENT, fileData.getData().trim());
    }

    @Test
    @Order(3)
    void delete() {
        System.out.println("delete");
        boolean status = FileStorage.delete(FILE_NAME);
        Assertions.assertTrue(status);
    }
}