package utils;
import extraction.ChatEntryHeaderExtraction;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dharmendra on 11-Jan-17.
 */
public class FileUtils {
    public static String[] getLines(String input) throws IOException, TikaException {

        File file = new File(getOSAppropriatePath(input));//

        Tika tika = new Tika();
        String contents = tika.parseToString(file);
        contents = contents.replaceAll("\\r?\\n"," ");
        contents = fixFileContents(contents);

        String[] lines = Arrays.stream(contents.split("\\r?\\n")).filter(line -> !line.isEmpty()).toArray(String[]::new);
        return lines;
    }

    public static String getOSAppropriatePath(String input) {
        String filePath = ClassLoader.getSystemResource(input).getPath();
        String osAppropriatePath = System.getProperty( "os.name" ).contains( "indow" ) ? filePath.substring(1) : filePath;
        return osAppropriatePath;
    }

    private static String fixFileContents(String input) {
        Pattern p = Pattern.compile(ChatEntryHeaderExtraction.TIME);
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "\n" + m.group());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
