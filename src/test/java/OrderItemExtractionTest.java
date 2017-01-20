import extraction.OrderItemExtraction;
import org.junit.Test;
import service.Constants;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dharmendra on 11-Jan-17.
 */

public class OrderItemExtractionTest {

    @Test
    public void unitsMatch() throws Exception {
        Pattern p = Pattern.compile(OrderItemExtraction.UNITS);
        String[] inputs = {"kg", "kg.", "k.g.", "kilo", "kiloo", "bunch", "bunches", "bunchs", "bunchees", "gms", "gm", "gram", "grams"};
        String[] outputs = {"kg", "kg.", "k.g.", "kilo", "kilo", "bunch", "bunches", "bunchs", "bunch", "gms", "gm", "gram", "grams"};
        for (int i = 0; i < inputs.length; i++) {
            Matcher m = p.matcher(inputs[i]);
            boolean res = m.find();
            if (!res) {
                System.out.println("didn't match " + inputs[i]);
            }
            assertTrue(res);
            assertEquals(outputs[i], m.group());
        }
    }


    @Test
    public void quantityMatch() throws Exception {
        Pattern p = Pattern.compile(OrderItemExtraction.QUANTITY);
        String[] inputs = {"1.5", " half ", " 1/2 "};
        String[] outputs = {"1.5", "half", "1/2"};
        for (int i = 0; i < inputs.length; i++) {
            Matcher m = p.matcher(inputs[i]);
            boolean res = m.find();
            if (!res) {
                System.out.println("didn't match " + inputs[i]);
            }
            assertTrue(res);
            assertEquals(outputs[i], m.group());
        }
    }

    @Test
    public void catalogue() throws Exception {
        Map<String, String> testData = getTestData();
        testData.forEach((input, expected) -> {
                    List<String> onlyFullMatches = new ArrayList<String>();
                    List<Map<String, String>> structuredList = OrderItemExtraction.extractItems(input);
                    for (Map<String, String> item : structuredList) {
                        onlyFullMatches.add(item.get(Constants.FULL_MATCH_TOKEN));
                    }
                    System.out.println(expected + " = " + onlyFullMatches.toString());
                    assertEquals(expected, onlyFullMatches.toString());
                }
        );
    }

    private Map<String, String> getTestData() {
        Map<String, String> data = new LinkedHashMap<String, String>();
        //file2
        data.put("Please send 2kg potatoes", "[2 kg potatoes]");
        data.put("Please also add 6 elaichi bananas", "[6 elaichi bananas]");
        String input = "Can you send 1 kg potatoes, 1 kg onions, 250gm small brinjal, 2 bharta baigan, 1 kg bhindi, 1kg tomatoes, half kg paneer.";
        data.put(input,
                "[1 kg potatoes, 1 kg onions, 250 gm small brinjal, 2 bharta baigan, 1 kg bhindi, 1 kg tomatoes, half kg paneer]");
/*        input = "Pls also add one cauliflower in the order.";
        data.put(input, "[one cauliflower]" );*/ //this is not mathing right now

//file3 -- all these entries are also matching 71 Kalpataru Estate
        input = " 1 kg tomatoes,1 kg potatoes,0.5 kg pumpkin,250 gm frenchbeans,0.5 kg cauliflower,0.5 kg bharta brinjal, 0.5 kg gheeya,0.5 kg cabbage,0.5 kg ladyfinger ";
        data.put(input, "[1 kg tomatoes, 1 kg potatoes, 0.5 kg pumpkin, 250 gm frenchbeans, 0.5 kg cauliflower, 0.5 kg bharta brinjal, 0.5 kg gheeya, 0.5 kg cabbage, 0.5 kg ladyfinger]");

        input = " 1 kg tomatoes,1 kg potatoes,1 kg onions,0.5 kg pumpkin,250 gm frenchbeans,0.5 kg bharta brinjal,0.5 kg ladyfinger,250 gm carrot,0.5 kg cucumber,250 gm capsicum,250 gm paneer,100 gm green chillies,100 gm ginger, 1 small bunch pudina, corriander,0.5 kg greenpeas,1 bunch methi,1 bunch palak";
        data.put(input, "[1 kg tomatoes, 1 kg potatoes, 1 kg onions, 0.5 kg pumpkin, 250 gm frenchbeans, 0.5 kg bharta brinjal, 0.5 kg ladyfinger, 250 gm carrot, 0.5 kg cucumber, 250 gm capsicum, 250 gm paneer, 100 gm green chillies, 100 gm ginger, 1 small bunch pudina, 0.5 kg greenpeas, 1 bunch methi, 1 bunch palak]");
//corriander should have matched in the above input

        input = " 1 kg tomatoes,1 kg potatoes,1 kg onions,0.5 kg bharta brinjal,0.5 kg ladyfinger,250 gm carrot,0.5 kg cucumber,250 gm capsicum,250 gm paneer, corriander,0.5 kg greenpeas,1 bunch methi,250 gm paneer,1bunch dhaniya (small)";
        data.put(input, "[1 kg tomatoes, 1 kg potatoes, 1 kg onions, 0.5 kg bharta brinjal, 0.5 kg ladyfinger, 250 gm carrot, 0.5 kg cucumber, 250 gm capsicum, 250 gm paneer, 0.5 kg greenpeas, 1 bunch methi, 250 gm paneer, 1 bunch dhaniya (small)]");

        input = " 2 kg potato, 1 kg tomato, 250gms capsicum, 6 lemon, dhaniapatti, 100 gms ginger, half kg cucumber, 1 brown coconut, 1 small ready papaya, half kg indian apple.";
        data.put(input, "[2 kg potato, 1 kg tomato, 250 gms capsicum, 6 lemon, 100 gms ginger, half kg cucumber, 1 brown coconut, 1 small ready papaya, half kg indian apple]");

        input = " Pls send 1/2 kg cucumber,1kg potato,1/2 bhindi..";
        data.put(input, "[1/2 kg cucumber, 1 kg potato, 1/2 bhindi]");

        input = " 2kg onion,1kg tomato,2kg potato,1kg cauliflower,6regular banana";
        data.put(input, "[2 kg onion, 1 kg tomato, 2 kg potato, 1 kg cauliflower, 6 regular banana]");

        input = " 1705 ,yarrow";
        data.put(input, "[]");

        input = " Can u pls send 1kg tomato n 250grm paneer";
        data.put(input, "[1 kg tomato, 250 grm paneer]");

        input = " N 5 lemons";
        data.put(input, "[5 lemons]");

        input = " Can u pls send 1/2 kg dhodhi,1/2 cucumber,1kg tomato,1/2 carot,1/2 capcicum,1/2 cauliflower";
        data.put(input, "[1/2 kg dhodhi, 1/2 cucumber, 1 kg tomato, 1/2 carot, 1/2 capcicum, 1/2 cauliflower]");

        //file7
        input = " Please send cauliflower 1 piece";
        data.put(input, "[cauliflower 1 piece]");
        input = " Beans 1/2 kg";
        data.put(input, "[beans 1/2 kg]");
        input = " Carrot 2 kg";
        data.put(input, "[carrot 2 kg]");
        input = " Apple 1 kg";
        data.put(input, "[apple 1 kg]");
        input = " Suran 1/2 ";
        data.put(input, "[suran 1/2]");
        input = " Please send cauliflower 1 piece, tendli 1/2 kg";
        data.put(input, "[cauliflower 1 piece, tendli 1/2 kg]");
        input = " Please send 1 cauliflower, 1kg tomato, 1 kg onion, 2 kg carrot, 200 gms lehsun,1/2 kg capsicum";
        data.put(input, "[1 cauliflower, 1 kg tomato, 1 kg onion, 2 kg carrot, 200 gms lehsun, 1/2 kg capsicum]");
        input = " 4 lemon";
        data.put(input, "[4 lemon]");
        input = " Please send 6 lemons";
        data.put(input, "[6 lemons]");
        input = " Please send papdi 1/2 kg,  aloo 1kg, drumstick 2 pieces, cabbage 1, beans 1/2 kg, arbi 1/2 kg,, 1/2 kg safed bhopla fresh, 1/4 kg red bhopla, 1/4 kg suran";
        data.put(input, "[papdi 1/2 kg, aloo 1 kg, drumstick 2 pieces, cabbage 1, beans 1/2 kg, arbi 1/2 kg, 1/2 kg safed bhopla fresh, 1/4 kg red bhopla, 1/4 kg suran]");
        input = " Please send someone to collect amt of last bill. Also send 2kg carrots and 1/2 kg cucumber";
        data.put(input, "[2 kg carrots, 1/2 kg cucumber]");
        input = " Please send 1 dozen mosambi, 1/2 kilo gavar, 1 cauliflower,  bhendi1/2 kilo, 1/2 kilo capsicum , 1/2 kilo tendli";
        data.put(input, "[1 dozen mosambi, 1/2 kilo gavar, 1 cauliflower, bhendi 1/2 kilo, 1/2 kilo capsicum, 1/2 kilo tendli]");
        input = " 12 eggs also";
        data.put(input, "[12 eggs]");
        input = " And karela 1/2 kg";
        data.put(input, "[karela 1/2 kg]");
        input = " Please send 2 kg apples, 6 lemons, 1 dozen mosambi";
        data.put(input, "[2 kg apples, 6 lemons, 1 dozen mosambi]");
        input = " Please send 1 cauliflower,  1kg potato, 1 kg onion, 250 gms garlic, 1/2 capsicum, 1 cabbage, 1 kg carrot,  1/2 kg suran, 1 kg arbi,1/2 kg papdi, 12 eggs";
        data.put(input, "[1 cauliflower, 1 kg potato, 1 kg onion, 250 gms garlic, 1/2 capsicum, 1 cabbage, 1 kg carrot, 1/2 kg suran, 1 kg arbi, 1/2 kg papdi, 12 eggs]");
        input = " Please add 1/2 kg cucumber also";
        data.put(input, "[1/2 kg cucumber]");
        input = " Please send half kilo bhindi,  2 kg carrot,1 dozen mosambi.";
        data.put(input, "[half kilo bhindi, 2 kg carrot, 1 dozen mosambi]");
        input = " Please send 1/4 kg safed bhopla,1 mooli, 100 gms papdi, 1 drumstick, red bhopla 1/4kg to Tivoli 27";
        data.put(input, "[1/4 kg safed bhopla, 1 mooli, 100 gms papdi, 1 drumstick, red bhopla 1/4 kg, tivoli 27]");
        input = " TIVOLI 27";
        data.put(input, "[tivoli 27]");
        input = " Please send 1 cauliflower,  1kg potato, 1/2 tendli";
        data.put(input, "[1 cauliflower, 1 kg potato, 1/2 tendli]");
        input = " Please send 3 kg apple, elaichi kela 3, cauliflower 1, potato 1 kg, lemon 6, beans 1/2 kg, mosambi 2 kg (please tell the price) drumstick 2 pieces arbi 1kg";
        data.put(input, "[3 kg apple, elaichi kela 3, cauliflower 1, potato 1 kg, lemon 6, beans 1/2 kg, mosambi 2 kg, drumstick 2 pieces, arbi 1 kg]");
        input = " Mosambi 2 dozen";
        data.put(input, "[mosambi 2 dozen]");
        input = " Please add cucumber 1/2 kg";
        data.put(input, "[cucumber 1/2 kg]");
        input = " Please send 1 cauliflower, 1/2 tendli, 1 cabbage, 1/2 capsicum, 1/2 bhendi, 1 kg aloo, 2 piece drumstick;  1/2 kg cucumber, 1/2 kg lamba podwal";
        data.put(input, "[1 cauliflower, 1/2 tendli, 1 cabbage, 1/2 capsicum, 1/2 bhendi, 1 kg aloo, 2 piece drumstick, 1/2 kg cucumber, 1/2 kg lamba podwal]");
        input = " Red pumkin 1/4 kg and white pumpkin 1/4 kg";
        data.put(input, "[red pumkin 1/4 kg, white pumpkin 1/4 kg]");
        input = " Please send 12 eggs, 1 kg onion";
        data.put(input, "[12 eggs, 1 kg onion]");
        input = " Please send 6 lemon, 2kg carrots, 1/2 kg beans, 1/2kg safed bhopla, 1/2 kg bhendi, mosambi 2 dozen, palak ";
        data.put(input, "[6 lemon, 2 kg carrots, 1/2 kg beans, 1/2 kg safed bhopla, 1/2 kg bhendi, mosambi 2 dozen]");
        input = " Please send 1 kg tomato, 1 kg potato,  cauliflower 1 piece, cabbage1 piece,, tendli half kilo gavar half kilo capsicum one kilo bhindi half kilo";
        data.put(input, "[1 kg tomato, 1 kg potato, cauliflower 1 piece, cabbage 1 piece, tendli half kilo, gavar half kilo, capsicum one kilo, bhindi half kilo]");
        input = " Please send 12 eggs";
        data.put(input, "[12 eggs]");
        input = " Please send 1 cauliflower, 1/2 kg beans, potato 1 kg, onions 1 kg, methi small ";
        data.put(input, "[1 cauliflower, 1/2 kg beans, potato 1 kg, onions 1 kg]");
        input = " Please adjust the amt in my next purchase....Please send 1 kg carrot, 2 drumstick, 2 dozen mosambi, 1 cabbage, 1/2 kg lamba podwal, 1/2 kd tendli ";
        data.put(input, "[1 kg carrot, 2 drumstick, 2 dozen mosambi, 1 cabbage, 1/2 kg lamba podwal]");
        input = " Please send 1 cauliflower,  6 lemon, 2 kg apples, beans 1/2 kg tomato 1 kg, carrors 1kg";
        data.put(input, "[1 cauliflower, 6 lemon, 2 kg apples, beans 1/2 kg, tomato 1 kg, carrors 1 kg]");
        return data;
    }

}
