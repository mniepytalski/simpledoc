package pl.cbr.notebook.diary;

import java.util.ArrayList;
import java.util.List;

public class DiffUtils {

    static final String LINE_BREAK = "\n";
    static final String COLON = ":";
    static final String EMPTY_STRING = "";

    public static String changesToString(List<DiffOp> ops) {
        StringBuilder sb = new StringBuilder();
        for (DiffOp op : ops) {
            sb.append(op.getLineNumber())
                    .append(COLON)
                    .append(op.getType().getValue())
                    .append(COLON)
                    .append(escape(op.getContent()))
                    .append(LINE_BREAK);
        }
        return sb.toString();
    }


    public static List<DiffOp> changesFromString(String text) {
        List<DiffOp> ops = new ArrayList<>();

        for (String line : text.split(LINE_BREAK)) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(COLON, 3);
            int lineNum = Integer.parseInt(parts[0].trim());

            String sign = parts[1].trim();   // "+", "-", "+ :", "- :" etc.
            boolean insert = sign.contains(DiffType.INSERT.getValue());

            String content = unescape(parts[2].trim());

            ops.add(new DiffOp(
                    insert ? DiffType.INSERT : DiffType.DELETE,
                    lineNum,
                    content
            ));
        }
        return ops;
    }

    private static String unescape(String s) {
        return s.replace("\\n", LINE_BREAK)
                .replace("\\\\", "\\");
    }

    private static String escape(String s) {
        if (s == null) return EMPTY_STRING;
        return s.replace("\\", "\\\\")
                .replace(LINE_BREAK, "\\n");
    }
}
