package service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dharmendra on 12-Jan-17.
 */
public class UnitSynonym {
    private static final Map<String, String> SYNONYMS;
    static {
        Map<String, String>tempMap = new HashMap<String, String>();
        tempMap.put("judi", "bunch");
        tempMap.put("bundle", "bunch");
        tempMap.put("bnch", "bunch");
        tempMap.put("bunch", "bunch");
        tempMap.put("pc", "piece");
        tempMap.put("pcs", "piece");
        tempMap.put("piece", "piece");
        tempMap.put("kg", "kilo");
        tempMap.put("kilogram", "kilo");
        tempMap.put("gm", "gram");
        tempMap.put("gms", "gram");
        tempMap.put("grm", "gram");
        tempMap.put("gram", "gram");
        tempMap.put("dozen", "dozen");
        tempMap.put("", "piece");
        SYNONYMS = Collections.unmodifiableMap(tempMap);
    }

    public static String get(String input) {
        return SYNONYMS.containsKey(input) ? SYNONYMS.get(input) :input;
    }

    public static boolean contains(String input) {
        return SYNONYMS.containsKey(input);
    }
}
