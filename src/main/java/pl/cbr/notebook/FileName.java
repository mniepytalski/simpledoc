package pl.cbr.notebook;

import lombok.experimental.UtilityClass;
import pl.cbr.notebook.utils.DateUtil;

import java.util.Date;

@UtilityClass
public class FileName {
    private static int counter;

    public String get() {
        String prefix = DateUtil.format(new Date(System.currentTimeMillis()), "yyMMddHHmmss");
        return translate(prefix) + (counter++) % 10 + StorageConfig.FILE_POSTFIX;
    }

    private String translate(String data) {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < data.length(); i += 2) {
            int value = Integer.parseInt(data.substring(i, i + 2));
            id.append(getChar((char) value));
        }
        return id.toString();
    }

    private char getChar(char index) {
        if ( index < 10) {
            return (char)(index + '0');
        }
        if ( index < 36) {
            return (char) ('A' + (index - 10));
        }
        if ( index < 62) {
            return (char) ('a' + (index - 36));
        }
        return '_';
    }
}
