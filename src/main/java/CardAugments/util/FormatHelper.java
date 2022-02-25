package CardAugments.util;

public class FormatHelper {
    private static final StringBuilder newMsg = new StringBuilder();
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String capitalize(String str, String match) {
        return str.replace(match, capitalize(match));
    }

    public static String prefixWords(String input, String prefix) {
        newMsg.setLength(0);
        for (String word : input.split(" ")) {
            newMsg.append(prefix).append(word).append(' ');
        }

        return newMsg.toString().trim();
    }
}
