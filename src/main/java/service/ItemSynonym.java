package service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dharmendra on 12-Jan-17.
 */
public class ItemSynonym {
    private static final Map<String, String> SYNONYMS;
    static {
        Map<String, String>tempMap = new HashMap<String, String>();
        tempMap.put("bhindi", "ladies finger");
        SYNONYMS = Collections.unmodifiableMap(tempMap);
    }

    public static String get(String input) {
        return SYNONYMS.containsKey(input) ? SYNONYMS.get(input) :input;
    }
}
