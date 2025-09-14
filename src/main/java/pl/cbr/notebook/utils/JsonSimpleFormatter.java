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
                    // sprawdzamy, czy cudzysłów nie jest poprzedzony przez '\'
                    boolean escaped = i > 0 && json.charAt(i - 1) == '\\';
                    if (!escaped) {
                        inQuotes = !inQuotes;
                    }
                    formatted.append(c);
                    break;

                case '{':
                case '[':
                    formatted.append(c);
                    if (!inQuotes) {
                        formatted.append("\n");
                        indent++;
                        appendIndent(formatted, indent);
                    }
                    break;

                case '}':
                case ']':
                    if (!inQuotes) {
                        formatted.append("\n");
                        indent--;
                        appendIndent(formatted, indent);
                        formatted.append(c);
                    } else {
                        formatted.append(c);
                    }
                    break;

                case ',':
                    formatted.append(c);
                    if (!inQuotes) {
                        formatted.append("\n");
                        appendIndent(formatted, indent);
                    }
                    break;

                case ':':
                    formatted.append(c);
                    if (!inQuotes) {
                        formatted.append(" ");
                    }
                    break;

                default:
                    formatted.append(c);
                    break;
            }
        }
//        return formatted.toString();
        return formatted.toString().replaceAll("([ \\t]*\\r?\\n)+", "\n");
    }

    private static void appendIndent(StringBuilder sb, int indent) {
        sb.append("    ".repeat(Math.max(0, indent))); // 4 spacje na wcięcie
    }
}
