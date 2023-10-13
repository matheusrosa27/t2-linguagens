import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

        // Allow the user to enter a sentence for validation
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter a sentence for validation:");
        String inputSentence = scanner.nextLine();
        
        // Start symbol is obtained from the calculator
        String startSymbol = calculator.getStartSymbol();
        Deque<String> stack = new LinkedList<>();
        stack.push(startSymbol);
        
        // Tokenize the input sentence (assuming space-separated terminals)
        String[] inputTokens = inputSentence.split(" ");
        int inputTokenIndex = 0;
        
        boolean valid = true; // Assume the sentence is valid until proven otherwise
        
        while (!stack.isEmpty()) {
            String top = stack.pop();
            if (top.equals("$")) {
                // Reached the end of the stack, should also be at the end of the input sentence
                if (inputTokenIndex == inputTokens.length) {
                    break; // Parsing is successful
                } else {
                    valid = false; // Extra input tokens, not valid
                    break;
                }
            } else if (isNonTerminal(top)) {
                // Lookup in the parsing table to get the production
                String nextTerminal = inputTokenIndex < inputTokens.length ? inputTokens[inputTokenIndex] : "$";
                String production = parserTable.getProduction(top, nextTerminal);
                if (production != null) {
                    // Push the production onto the stack in reverse order
                    String[] productionSymbols = production.split(" ");
                    for (int i = productionSymbols.length - 1; i >= 0; i--) {
                        if (!productionSymbols[i].equals("ε")) {
                            stack.push(productionSymbols[i]);
                        }
                    }
                } else {
                    valid = false; // No valid production in the parsing table
                    break;
                }
            } else if (isTerminal(top)) {
                // Terminal symbols on the stack should match input tokens
                if (inputTokenIndex < inputTokens.length && top.equals(inputTokens[inputTokenIndex])) {
                    inputTokenIndex++;
                } else {
                    valid = false; // Mismatched terminal symbols
                    break;
                }
            } else {
                valid = false; // Invalid symbol on the stack
                break;
            }
        }

        if (valid && inputTokenIndex == inputTokens.length) {
            System.out.println("Input sentence is valid in the grammar.");
        } else {
            System.out.println("Input sentence is not valid in the grammar.");
        }

        scanner.close();
    }

    private static boolean isNonTerminal(String symbol) {
        // Implement your logic to check if a symbol is a non-terminal
        return symbol.matches("[A-Z][A-Za-z']*");
    }

    private static boolean isTerminal(String symbol) {
        // Implement your logic to check if a symbol is a terminal
        return symbol.matches("[a-z][A-Za-z']*");
    }
}
