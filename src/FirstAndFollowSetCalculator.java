import java.util.*;
import java.util.Map.Entry;

public class FirstAndFollowSetCalculator {
    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<String>> productions;
    private String startSymbol;

    public FirstAndFollowSetCalculator() {
        firstSets = new HashMap<>();
        followSets = new HashMap<>();
        productions = new HashMap<>();
    }

    public void addProduction(String nonTerminal, List<String> production) {
        productions.put(nonTerminal, production);
    }

    public void setStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
    }

    public void calculateFirstSets() {
        for (String nonTerminal : productions.keySet()) {
            calculateFirstSet(nonTerminal);
        }
    }

    public void calculateFollowSets() {
        followSets.put(startSymbol, new HashSet<>(Collections.singletonList("$"))); // Add the end-of-input symbol

        for (String nonTerminal : productions.keySet()) {
            calculateFollowSet(nonTerminal);
        }
    }

    private Set<String> calculateFirstSet(String nonTerminal) {
        if (firstSets.containsKey(nonTerminal)) {
            return firstSets.get(nonTerminal);
        }

        Set<String> firstSet = new HashSet<>();

        for (String production : productions.get(nonTerminal)) {
            char firstChar = production.charAt(0);
            if (Character.isUpperCase(firstChar)) { // If it's a non-terminal
                Set<String> subFirstSet = calculateFirstSet(String.valueOf(firstChar));
                firstSet.addAll(subFirstSet);
            } else if (firstChar != 'ε') { // If it's a terminal (excluding ε)
                firstSet.add(String.valueOf(firstChar));
            } else { // If it's ε
                firstSet.add("ε");
            }
        }

        firstSets.put(nonTerminal, firstSet);
        return firstSet;
    }

    private void calculateFollowSet(String nonTerminal) {
        if (followSets.containsKey(nonTerminal)) {
            return;
        }

        Set<String> followSet = new HashSet<>();

        for (Entry<String, List<String>> entry : productions.entrySet()) {
            String leftHandSide = entry.getKey();
            List<String> rightHandSide = entry.getValue();

            for (String production : rightHandSide) {
                String[] productionArray = production.split(" ");
                int indexOfNonTerminal = -1;
                int sizeOfNonTerminal = -1;
                for (int i=0; i<productionArray.length; i++) {
                    if (productionArray[i].equals(nonTerminal)) {
                        indexOfNonTerminal = i;
                        sizeOfNonTerminal = productionArray[i].length();
                        break;
                    }
                }

                if (indexOfNonTerminal != -1) {
                    if (indexOfNonTerminal < production.length() - 1) { // If it's not the last character
                        String nextChar = "ε";
                        if ((indexOfNonTerminal + sizeOfNonTerminal) < productionArray.length ) {
                            nextChar = productionArray[indexOfNonTerminal+1];
                            if (Character.isUpperCase(nextChar.charAt(0))) {
                                Set<String> firstOfNext = calculateFirstSet(String.valueOf(nextChar));
                                Set<String> copy = new HashSet<>(firstOfNext);
                                if (copy.contains("ε")) {
                                    copy.remove("ε");
                                    followSet.addAll(followSets.get(leftHandSide));
                                }
        
                                followSet.addAll(copy);
                            } else {
                                followSet.add(String.valueOf(nextChar));
                            }

                        } else {
                            followSet.add("$");
                        }
                    } else { // If it's the last character
                        if (followSets.get(leftHandSide) != null) {
                            followSet.addAll(followSets.get(leftHandSide));
                        } else {
                            followSet.add("$");
                        }
                    }
                }
            }
        }

        followSets.put(nonTerminal, followSet);
    }

    public void printFirstAndFollowSets() {
        System.out.println("First Sets:");
        for (Entry<String, Set<String>> entry : firstSets.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nFollow Sets:");
        for (Entry<String, Set<String>> entry : followSets.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public Map<String, Set<String>> getFirstSets() {
        return firstSets;
    }

    public Map<String, Set<String>> getFollowSets() {
        return followSets;
    }

    public Map<String, List<String>> getProductions() {
        return productions;
    }
}
