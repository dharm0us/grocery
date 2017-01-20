package extraction;

import service.Constants;
import service.ItemSynonym;
import service.UnitSynonym;
import utils.FileUtils;
import utils.Spelling;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderItemExtraction {

    private static final Spelling itemSpellChecker = getSpellChecker("itemlist.txt");
    private static final Spelling unitSpellChecker = getSpellChecker("unitlist.txt");

    private static final String QUANTITY_PARTIAL = "((\\d|\\bhalf\\b|\\bone\\b))";
    public static final String QUANTITY = "("+QUANTITY_PARTIAL+"{1,3}(\\.|/)*"+QUANTITY_PARTIAL+"{0,3})";
    public static final String QUANTITY_OPTIONAL = QUANTITY + "?";
    public static final String QUANTITY_END = "(?<quantityend>" + QUANTITY + ")";
    public static final String QUANTITY_BEG = "(?<quantitybeg>" + QUANTITY + ")";
    public static final String QUANTITY_END_OPTIONAL = "(?<quantityendoptional>" + QUANTITY_OPTIONAL + ")";
    public static final String QUANTITY_BEG_OPTIONAL = "(?<quantitybegoptional>" + QUANTITY_OPTIONAL + ")";

    public static final String UNITS = "((k\\.?\\s*g\\.?|rs\\.?|kilo|dozen|kilograms?|grms?|gms?|grams?|pcs?|pieces?|judis?|packs?|bundles?|bunch(s|es)?))";
    public static final String UNITS_OPTIONAL = UNITS + "?";
    public static final String UNITS_NAMED = "(?<units>" + UNITS + ")";
    public static final String UNITS_END_OPTIONAL = "(?<unitsendoptional>" + UNITS_OPTIONAL + ")";
    public static final String UNITS_BEG_OPTIONAL = "(?<unitsbegoptional>" + UNITS_OPTIONAL + ")";
    public static final String UNITS_END = "(?<unitsend>" + UNITS + ")";
    public static final String UNITS_BEG = "(?<unitsbeg>" + UNITS + ")";

    public static final String SEP = "(\\s)*";

    public static final String ITEMNAME_PARTIAL = "(([a-z|\\(|\\)]{3,}\\s*){1,3}";
    public static final String ITEMNAME_END = "(?<itemend>" + ITEMNAME_PARTIAL + "(\\.|,|\\s)+)" + ")";
    public static final String ITEMNAME_END_1 = "(?<itemend1>" + ITEMNAME_PARTIAL + "(\\.|,|\\s)+)" + ")";
    public static final String ITEMNAME_BEG = "(?<itembeg>" + ITEMNAME_PARTIAL + "(\\s)+)" + ")";
    public static final String ITEMNAME_BEG_1 = "(?<itembeg1>" + ITEMNAME_PARTIAL + "(\\s)+)" + ")";

    public static final String ORDERITEM_WITH_UNITS_OPTIONAL = "(" + "(" + QUANTITY_BEG + SEP + UNITS_BEG_OPTIONAL + SEP + ITEMNAME_END + ")" + "|" + "(" + ITEMNAME_BEG + SEP + QUANTITY_END + SEP + UNITS_END_OPTIONAL + ")" + ")";
    public static final String ORDERITEM_WITH_QUANTITY_OPTIONAL = "(" + "(" + QUANTITY_BEG_OPTIONAL + SEP + UNITS_BEG + SEP + ITEMNAME_END_1 + ")" + "|" + "(" + ITEMNAME_BEG_1 + SEP + QUANTITY_END_OPTIONAL + SEP + UNITS_END + ")" + ")";
    public static final String ORDERITEM = "(" + "(" + ORDERITEM_WITH_UNITS_OPTIONAL+ ")" + "|" + "(" + ORDERITEM_WITH_QUANTITY_OPTIONAL + ")" + ")";

    public static List<Map<String, String>> extractItems(String input) {

        String chatEntry = massageChatEntry(input);
        Pattern p = Pattern.compile(ORDERITEM_WITH_UNITS_OPTIONAL);
        Matcher m = p.matcher(chatEntry);
        List<Map<String, String>> matches = new ArrayList<Map<String, String>>();
        while (m.find()) {

            Map<String, String> currMatch = new LinkedHashMap<String, String>();

            currMatch.put(Constants.QUANTITY_TOKEN, getQuantity(m));
            currMatch.put(Constants.UNITS_TOKEN, getUnits(m));
            String item = getItem(m);
            if(item.isEmpty()) continue;;
            currMatch.put(Constants.ITEM_NAME_TOKEN, getItem(m));
            currMatch.put(Constants.FULL_MATCH_TOKEN, sanitize(m.group()));
            matches.add(currMatch);
        }
        return matches;
    }

    private static String massageChatEntry(String input) {
        String chatEntry = input.toLowerCase().replaceAll("-|and|\\bn\\b", ",");//replace and & n with ,
        chatEntry = chatEntry.replaceAll(QUANTITY_BEG + "(\\s*)" + UNITS_NAMED, " ${quantitybeg} ${units} ");//replace 1kg or 1   kg with 1 kg
        chatEntry = chatEntry.replaceAll(QUANTITY_BEG + "(\\s*)" + ITEMNAME_END, " ${quantitybeg} ${itemend} ");//replace 1mango or 1  mango with 1 mango
        chatEntry = chatEntry.replaceAll(getStopWordsPattern(), ""); //remove stop words
        chatEntry = chatEntry.replaceAll(",|;", ", "); //add space after comma
        chatEntry = chatEntry.replaceAll("\\(\\)", "");//remove empty ()
        chatEntry += ",";
        return chatEntry;
    }

    private static String getStopWordsPattern() {
        return "\\b(today|between|there|till|price|bucks?|change|but|available|which|what|why|rate|text|list|pay|last|cash|post|thought|was|do|before|home|until|code|collect|money|add|also|ask|him|wait|be|been|by|can|come|deliver|i|in|is|me|now|or|of|ok|okay|only|order|please|price|pls?|some|someone|say|send|tell|the|then|to|u|want|will|you)\\b";
    }

    private static String getUnits(Matcher m) {

        String[] groups = {"unitsbegoptional","unitsendoptional"};//,"unitsbegoptional","unitsendoptional"};
        String units = getFirstMatch(m,groups);
        if(units != null && !units.isEmpty()) {
            units = unitSpellChecker.correct(units);
        }
        units = UnitSynonym.get(units);
        return units;
    }

    private static String getQuantity(Matcher m) {
        String[] groupNames = {"quantitybeg","quantityend"};//,"quantitybegoptional","quantityendoptional"};
        return getFirstMatch(m,groupNames);
    }

    private static String getItem(Matcher m) {
        String[] groupNames = {"itembeg","itemend"};//,"itembeg1","itemend1"};
        String item = getFirstMatch(m,groupNames);
        item = sanitize(item);
        item = itemSpellChecker.correct(item);
        item = ItemSynonym.get(item);
        if(UnitSynonym.contains(item)) item = "";
        return item;
    }

    private static String getFirstMatch(Matcher m,String[] groups) {
        String ret = null;
        for(String g:groups) {
            ret = m.group(g);
            if(ret != null) break;
        }
        return sanitize(ret);
    }

    private static String sanitize(String input) {
        if (input == null) return "";
        input = input.trim();
        input = input.replaceAll("(\\s|,|\\.)+$", "");//remove from end
        input = input.replaceAll(" +", " ");//replace consecutive spaces with single one
        return input;
    }

    private static Spelling getSpellChecker(String filename) {
        try {
            Spelling spellfixer = new Spelling(Paths.get(FileUtils.getOSAppropriatePath(filename)));
            return spellfixer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
