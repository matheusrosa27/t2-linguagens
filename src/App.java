import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        File file = new File("src/input.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String> list = new ArrayList<String>();
        String st;
        while ((st = br.readLine()) != null) {
            list.add(st);
        }
 
        // Print the string
        br.close();

        FirstAndFollowSetCalculator calculator = new FirstAndFollowSetCalculator();

        // Add the productions of the grammar
        for (String prod : list) {
            String[] parts = prod.split("->");
            String left = parts[0].trim();
            String[] rightArr = parts[1].trim().split("\\|");
            ArrayList<String> rightList = new ArrayList<String>();

            for (String r : rightArr) {
                r = r.trim();
                String rightProd = "";
                for (int i=0; i<r.length(); i++) {
                    if (i+1 < r.length()) {
                        if (r.charAt(i+1) == '\'') {
                            rightProd += Character.toString(r.charAt(i)) + Character.toString(r.charAt(i+1));
                            i++;
                        } else {
                            rightProd += r.charAt(i);
                        }
                        if (i < r.length()-1) {
                            rightProd += " ";
                        }
                    } else {
                        rightProd += r.charAt(i);
                    }
                }
                rightList.add(rightProd);
            }
            calculator.addProduction(left, rightList);
        }

        // Set the start symbol
        calculator.setStartSymbol(list.get(0).split("->")[0].trim());

        // Calculate FIRST and FOLLOW sets
        calculator.calculateFirstSets();
        calculator.calculateFollowSets();

        // Print the FIRST and FOLLOW sets
        calculator.printFirstAndFollowSets();

        // Create the predictive parser table
        PredictiveParserTable parserTable = new PredictiveParserTable(calculator.getFirstSets(), calculator.getFollowSets(), calculator.getProductions());
        
        parserTable.buildParsingTable();
        System.out.println("\n");
        // Print the parsing table
        parserTable.printParsingTable();
    }
}
