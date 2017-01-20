import extraction.OrderItemExtraction;
import extraction.ChatEntryHeaderExtraction;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ChatEntryHeaderExtractionTest {

    @Test
    public void hostMatch() throws Exception {
        for (String input : getPositiveTimeInputs()) {
            String test = input+"Host:";
            boolean res = (Boolean) ChatEntryHeaderExtraction.analyzeEntry(test)[0];
            if(!res) {
                System.out.println(test);
            }
            assertTrue(res);
        }
        for (String input : getPositiveTimeInputs()) {
            String test = input+"Some name here:";
            boolean res = (Boolean) ChatEntryHeaderExtraction.analyzeEntry(test)[0];
            if(res) {
                System.out.println(test);
            }
            assertFalse(res);
        }
        for (String input : getNegativeTimeInputs()) {
            String test = input+"Host:";
            boolean res = (Boolean) ChatEntryHeaderExtraction.analyzeEntry(test)[0];
            if(res) {
                System.out.println(test);
            }
            assertFalse(res);
        }
        for (String input : getNegativeTimeInputs()) {
            String test = input+"Some name here:";
            boolean res = (Boolean) ChatEntryHeaderExtraction.analyzeEntry(test)[0];
            if(res) {
                System.out.println(test);
            }
            assertFalse(res);
        }
    }

    @Test
    public void dateTimeMatch() throws Exception {
        Pattern p = Pattern.compile(ChatEntryHeaderExtraction.TIME);
        for (String input : getPositiveTimeInputs()) {
            Matcher m = p.matcher(input);
            boolean res = m.find();
            if (!res) {
                System.out.println(input);
            }
            assertTrue(res);
        }

        for (String input : getNegativeTimeInputs()) {
            Matcher m = p.matcher(input);
            boolean res = m.find();
            if (res) {
                System.out.println(input);
            }
            assertFalse(m.find());
        }
    }

    private String[] getNegativeTimeInputs() {
        String[] inputs = {
                "25/2015, 10:27 AM - ",
                "//2015, 10:27 AM - ",
                "2015, 10:27 AM - ",
                "2/25/2015,10:27 AM - ",
                "2/25/2015, 10:27 M - ",
                "2/25/2015, 10:27 AM  ",
                "2/25/2015, 10:27 AM -",
        };
        return inputs;
    }

    private String[] getPositiveTimeInputs() {
        String[] inputs = {
                "/25/2015, 10:27 AM - ",
                "/25/2015, 10:27 PM - ",
                "1/25/2015, 10:27 PM - ",
                "01/25/2015, 10:27 PM - ",
                "11/25/2015, 10:27 PM - ",
                "11/5/2015, 10:27 PM - ",
                "11/05/2015, 10:27 PM - ",
                "11/05/2015, 1:27 PM - ",
                "11/05/2015, 01:27 PM - ",
                "11/05/2015, 0:27 PM - ",
                "11/05/2015, 00:27 PM - ",
                "11/05/2015, 00:2 PM - ",
                "11/05/2015, 00:02 PM - ",
                "11/05/2015, 00:02 AM - ",
                "11/05/2015, 0:1 AM - ",
                "11/05/2015, 0:0 AM - ",
        };
        return inputs;
    }



}