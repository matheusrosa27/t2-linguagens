import java.util.*;
import java.util.Map.Entry;

public class PredictiveParserTable {
    private Map<String, Map<String, String>> parsingTable;
    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<String>> productions;

    public PredictiveParserTable(Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets, Map<String, List<String>> productions) {
        parsingTable = new HashMap<>();
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.productions = productions;
    }

    public void buildParsingTable() {
        for (Entry<String, List<String>> entry : productions.entrySet()) {
            String nonTerminal = entry.getKey();
            for (String production : entry.getValue()) {
                Set<String> firstSet = calculateFirst(production);
                for (String terminal : firstSet) {
                    if (!terminal.equals("ε")) {
                        addEntryToParsingTable(nonTerminal, terminal, production);
                    }
                }
                if (firstSet.contains("ε")) {
                    for (String followTerminal : followSets.get(nonTerminal)) {
                        addEntryToParsingTable(nonTerminal, followTerminal, "ε");
                    }
                }
            }
        }
    }

    public Map<String, Map<String, String>> getParsingTable() {
        return parsingTable;
    }

    private Set<String> calculateFirst(String production) {
        Set<String> firstSet = new HashSet<>();
        String[] symbols = production.split(" ");
        for (String symbol : symbols) {
            if (firstSets.containsKey(symbol)) {
                firstSet.addAll(firstSets.get(symbol));
                if (!firstSet.contains("ε")) {
                    break;
                }
            } else {
                firstSet.add(symbol);
                break;
            }
        }
        return firstSet;
    }

    private void addEntryToParsingTable(String nonTerminal, String terminal, String production) {
        if (!parsingTable.containsKey(nonTerminal)) {
            parsingTable.put(nonTerminal, new HashMap<>());
        }
        parsingTable.get(nonTerminal).put(terminal, production);
    }

    public void printParsingTable() {
      System.out.println("Predictive Parsing Table:");
      Set<String> allNonTerminals = new HashSet<>(parsingTable.keySet());
      Set<String> allTerminals = new HashSet<>();
  
      for (Map<String, String> row : parsingTable.values()) {
        allTerminals.addAll(row.keySet());
      }
  
      for (String nonTerminal : allNonTerminals) {
        for (String terminal : allTerminals) {
          String production = parsingTable.get(nonTerminal).get(terminal);
          if (production != null) {
            System.out.printf("M[%s,%s] = %s -> %s%n", nonTerminal, terminal, nonTerminal, production);
          }
        }
      }
    }
}
