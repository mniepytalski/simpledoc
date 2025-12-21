package pl.cbr.notebook.utils;

public class JsonSimpleFormatter {
    public static String format(String inputJson) {
        StringBuilder formatted = new StringBuilder();
        int indent = 0;
        boolean inQuotes = false;

        String json = inputJson.replaceAll("(\"[ \t]+)+", "\"");
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            switch (c) {
                case '"':
                    if (!checkBeckSlashBefore(i, json)) {
                        inQuotes = !inQuotes;
                    }
                    formatted.append(c);
                    break;
                case '{' | '[':
                    formatted.append(c);
                    if (!inQuotes) {
                        formatted.append(StringUtil.ENTER);
                        indent++;
                        appendIndent(formatted, indent);
                    }
                    break;
                case '}' | ']':
                    if (!inQuotes) {
                        formatted.append(StringUtil.ENTER);
                        indent--;
                        appendIndent(formatted, indent);
                    }
                    formatted.append(c);
                    break;
                case ',':
                    formatted.append(c);
                    if (!inQuotes) {
                        formatted.append(StringUtil.ENTER);
                        appendIndent(formatted, indent);
                    }
                    break;
                case ':':
                    formatted.append(c);
                    if (!inQuotes) {
                        formatted.append(StringUtil.SPACE);
                    }
                    break;
                default:
                    formatted.append(c);
                    break;
            }
        }
        return formatted.toString()
                .replaceAll(":[ ]+", ": ")
                .replaceAll("([ \\t]*\\r?\\n)+", StringUtil.ENTER);
    }

    private static boolean checkBeckSlashBefore(int i, String json) {
        return i > 0 && json.charAt(i - 1) == '\\';
    }

    private static void appendIndent(StringBuilder sb, int indent) {
        sb.append("    ".repeat(Math.max(0, indent))); // 4 spacje na wcięcie
    }
}
