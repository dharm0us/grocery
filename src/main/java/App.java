import com.google.gson.Gson;
import extraction.ChatEntryHeaderExtraction;
import extraction.OrderItemExtraction;
import utils.FileUtils;

import java.util.List;
import java.util.Map;


public class App {
    public static void main(String[] args) throws Exception {

        String filePath = "data/Sanitized30.rtf";
        String lines[] = FileUtils.getLines(filePath);

        for (String line : lines) {
            Object[] analyzed = ChatEntryHeaderExtraction.analyzeEntry(line);
            Boolean isHost = (Boolean) analyzed[0];
            if(!isHost) {
                String chatEntry = (String) analyzed[1];
                List<Map<String, String>> orderItems = OrderItemExtraction.extractItems(chatEntry);
                if(orderItems.size()>0) {
                    System.out.println("matching input = " + chatEntry);
                    orderItems.forEach(oi-> System.out.println(new Gson().toJson(oi)));
                    System.out.println("--------------------------------------");
                }
            }
        }
    }

}
