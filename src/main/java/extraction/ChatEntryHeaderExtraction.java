package extraction;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatEntryHeaderExtraction {

    private static String DATE_MONTH = "((\\d{1,2})?/\\d{1,2}/)";
    private static String YEAR = "\\d{4}";
    private static String HOUR_MINUTE = "(\\d{1,2}):(\\d{1,2})\\s(AM|PM)\\s-\\s";
    public static String TIME = DATE_MONTH + YEAR + ",\\s" + HOUR_MINUTE;
    public static String TIME_WITH_USER = TIME + "(?<user>.*?):";

    public static Object[] analyzeEntry(String input) {
        Pattern p = Pattern.compile(TIME_WITH_USER);
        Matcher m = p.matcher(input);
        String user = null;
        if (m.find()) {
            user = m.group("user");
        }
        if (user == null) user = "";
        Boolean isHost = user.toLowerCase().equals("host");
        String chatEntry = input.replaceFirst(TIME_WITH_USER, "");
        Object[] resp = {isHost, chatEntry};
        return resp;
    }

}
