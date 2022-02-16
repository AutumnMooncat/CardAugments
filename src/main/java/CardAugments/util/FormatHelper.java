package CardAugments.util;

public class FormatHelper {
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String capitalize(String str, String match) {
        return str.replace(match, capitalize(match));
    }
}
