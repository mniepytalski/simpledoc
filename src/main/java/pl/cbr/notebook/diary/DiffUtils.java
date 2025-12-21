package pl.cbr.notebook.diary;

import pl.cbr.notebook.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DiffUtils {

    public static String changesToString(List<DiffOp> ops) {
        StringBuilder sb = new StringBuilder();
        for (DiffOp op : ops) {
            sb.append(op.getLineNumber())
                    .append(StringUtil.COLON)
                    .append(op.getType().getValue())
                    .append(StringUtil.COLON)
                    .append(escape(op.getContent()))
                    .append(StringUtil.ENTER);
        }
        return sb.toString();
    }


    public static List<DiffOp> changesFromString(String text) {
        List<DiffOp> ops = new ArrayList<>();

        for (String line : text.split(StringUtil.ENTER)) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(StringUtil.COLON, 3);
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
        return s.replace("\\n", StringUtil.ENTER)
                .replace("\\\\", "\\");
    }

    private static String escape(String s) {
        if (s == null) return StringUtil.EMPTY;
        return s.replace("\\", "\\\\")
                .replace(StringUtil.ENTER, "\\n");
    }
}
