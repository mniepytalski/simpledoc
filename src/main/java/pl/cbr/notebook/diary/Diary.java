package pl.cbr.notebook.diary;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class Diary {

    public static final String SEPARATOR_CHUNK = "-+-+-+-0123456789-+-+-+-\n";
    private List<DiaryChange> changes = new ArrayList<>();

    public void addDiff(String diff) {
        changes.add(DiaryChange.of(diff, new Date()));
    }

    public void toLogger() {
        System.out.println();
        System.out.println("================");
        System.out.println(serialize());
        System.out.println("----------------");
    }

    public String serialize() {
        return changes.stream().map(data -> SEPARATOR_CHUNK + data).collect(Collectors.joining());
    }

    public List<String> deserialize(String data) {
        return Arrays.stream(data.split(Pattern.quote(SEPARATOR_CHUNK)))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
