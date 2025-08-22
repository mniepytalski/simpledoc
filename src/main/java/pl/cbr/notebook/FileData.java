package pl.cbr.notebook;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class FileData {
    private String  data;
    private String  fileName;
    private Date    modificationDate;

    public static FileData empty() {
        return new FileData("", null, new Date(System.currentTimeMillis()));
    }
}
