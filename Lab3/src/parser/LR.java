package parser;

import com.jakewharton.fliptables.FlipTable;
import grammar.Grammar;
import utils.FileHandler;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LR {
    private final Grammar grammar;

    public LR(String fileName) throws IOException {
        this.grammar = new Grammar(fileName);
        this.grammar.scanGrammar();
        enrichGrammar();
    }

    private class Entry {
        public List<String> currentS;

        public int parentS = -1;
        public int previousSIndex = -1;

        String X;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Entry otherEntry = (Entry) obj;
            return currentS.equals(otherEntry.currentS) && previousSIndex == otherEntry.previousSIndex
                    && X.equals(otherEntry.X);
        }
    }

    public Grammar getGrammar() {
        return grammar;
    }

    private void enrichGrammar(){
        String production = "S'->. " + this.grammar.getStartingSymbol();
        this.grammar.setStartingSymbol("S'");
        this.grammar.getNonTerminals().add("S'");
        this.grammar.getProductions().add(production);
    }

    public List<String> closure(List<String> I) throws Exception {
        List<String> C = new ArrayList<>();
        boolean hasAdded = true;
        C.addAll(I);
        while(hasAdded){
            List<String> newProductions = new ArrayList<>();
            hasAdded = false;
            for(String production: C){
                int index = production.indexOf(".");
                if (index < production.length() - 2) {
                    String[] rightSideTokens = production.substring(index + 2).split(" ");
                    String possibleNonTerminal = rightSideTokens[0];
                    if (grammar.getNonTerminals().contains(possibleNonTerminal)) {
                        for (String foundProduction : grammar.productionsForAGivenNonTerminal(possibleNonTerminal)) {
                            String possibleProduction;
                            int arrowIndex = foundProduction.indexOf("->");
                            arrowIndex += 2;
                            possibleProduction = foundProduction.substring(0, arrowIndex).concat(". ").concat(foundProduction.substring(arrowIndex));
                            if (!C.contains(possibleProduction) && !newProductions.contains(possibleProduction)) {
                                newProductions.add(possibleProduction);
                                hasAdded = true;
                            }
                        }
                    }
                }
            }
            C.addAll(newProductions);
        }
        return C;
    }

    public List<String> goto_(List<String> s, String X) throws Exception {
        List<String> analysisElements = new ArrayList<>();

        for (String production : s) {
            int arrowIndex = production.indexOf("->");
            arrowIndex += 2;
            String rightSide = production.substring(arrowIndex);
            String[] rightSideToken = rightSide.split(" ");
            for (int index = 0; index < rightSideToken.length; index++) {
                if (rightSideToken[index].equals(X)) {
                    if (index > 0) {
                        if (rightSideToken[index - 1].equals(".")) {
                            String newProduction = production.substring(0, arrowIndex);
                            for (int subindex = 0; subindex < index - 1; subindex++) {
                                newProduction += rightSideToken[subindex] + " ";
                            }
                            newProduction += X;
                            newProduction += " .";
                            for (int subindex = index + 1; subindex < rightSideToken.length; subindex++) {
                                newProduction += " " + rightSideToken[subindex];
                            }
                            analysisElements.add(newProduction);
                            break;
                        }
                    }
                }
            }
        }

        return closure(analysisElements);
    }

    public List<Entry> DetermineCanonicalCollection() throws Exception {
        List<Entry> canonicalCollection = new ArrayList<>();

        String initialProduction = "S'->. " + grammar.getInitialStartingSymbol();
        List<String> initialElement = new ArrayList<>();
        initialElement.add(initialProduction);
        Entry initialEntry = new Entry();
        initialEntry.currentS = closure(initialElement);
        canonicalCollection.add(initialEntry);

        List<String> allSymbols = new ArrayList<>();
        allSymbols.addAll(grammar.getNonTerminals());
        allSymbols.addAll(grammar.getTerminals());

        List<Entry> pendingAdditions = new ArrayList<>();
        pendingAdditions.add(initialEntry);
        while (!pendingAdditions.isEmpty()) {
            List<Entry> newPendingAdditions = new ArrayList<>();
            for(int i = 0; i < pendingAdditions.size(); i++) {
                Entry entry = pendingAdditions.get(i);
                if(entry.parentS == -1) {
                    for (String X : allSymbols) {
                        List<String> gotoCollection = goto_(entry.currentS, X);
                        if (!gotoCollection.isEmpty()) {
                            Entry newEntry = new Entry();
                            newEntry.currentS = gotoCollection;
                            newEntry.previousSIndex = canonicalCollection.size() - pendingAdditions.size() + i;
                            newEntry.X = X;
                            if(canonicalCollection.stream().noneMatch(s -> s.equals(newEntry))
                                    && newPendingAdditions.stream().noneMatch(s -> s.equals(newEntry))) {
//                                if (canonicalCollection.size() > 7) {
//                                    if (X.equals("b") && i == 4) {
//                                        boolean are_the_same = canonicalCollection.get(4).equals(newEntry);
//                                        System.out.println(are_the_same);
//                                    }
//                                }
                                for (int j = 0; j < canonicalCollection.size(); j++) {
                                    if (canonicalCollection.get(j).currentS.equals(gotoCollection)) {
                                        newEntry.parentS = j;
                                    }
                                }
                                for (int j = 0; j < newPendingAdditions.size(); j++) {
                                    if (newPendingAdditions.get(j).currentS.equals(gotoCollection)) {
                                        newEntry.parentS = j + canonicalCollection.size();
                                    }
                                }
                                newPendingAdditions.add(newEntry);
                            }
                        }
                    }
                }
            }
            canonicalCollection.addAll(newPendingAdditions);
            pendingAdditions = newPendingAdditions;
        }

        return canonicalCollection;
    }

    private boolean isDotAtEnd(String production){
        boolean isAtEnd = false;

        int index = production.indexOf(".");
        if(index == production.length() - 1){
            isAtEnd = true;
        }

        return isAtEnd;
    }

    private String checkAction(List<String> s){
        String action = "shift";
        String acceptProduction = "S'->S .";
        String start = "S'";
        for(String production: s){
            if(production.compareTo(acceptProduction) == 0){
                action = "accept";
                break;
            } else if (isDotAtEnd(production)) {
                int arrowIndex = production.indexOf("->");
                String leftSide = production.substring(0, arrowIndex);
                if(!(leftSide.compareTo(start) == 0)){
                    action = "reduce";
                    break;
                }
            }
        }
        return action;
    }

    public void printParsingTable() throws Exception {
        FileHandler fileHandler = new FileHandler();
        List<Entry> canonicalCollection = this.DetermineCanonicalCollection();
        String[] headers = {" ", "action"};
        List<String> symbols = new ArrayList<>();
        symbols.addAll(Arrays.stream(headers).toList());
        symbols.addAll(grammar.getNonTerminals().stream().toList());
        symbols.addAll(grammar.getTerminals().stream().toList());
        String[][] data = new String[canonicalCollection.size()][symbols.size()];
        int rowIndex = 0;
        for(Entry entry: canonicalCollection){
            List<String> s = entry.currentS;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for(int i = 0; i < s.size()-1; i++){
                stringBuilder.append(s.get(i));
                stringBuilder.append(", ");
            }
            stringBuilder.append(s.get(s.size()-1));
            stringBuilder.append("]");
            data[rowIndex][0] = stringBuilder.toString();
            data[rowIndex][1] = checkAction(s);
            for(int j = 2; j < symbols.size(); j ++){
                data[rowIndex][j] = " ";
                for(int k = 0; k < canonicalCollection.size(); k++){
                    Entry otherEntry = canonicalCollection.get(k);
                    if(otherEntry.previousSIndex == rowIndex){
                        if(otherEntry.X.compareTo(symbols.get(j)) == 0){
                            if (otherEntry.parentS != -1) {
                                k = otherEntry.parentS;
                            }
                            data[rowIndex][j] = "s" + k;
                            break;
                        }
                    }
                }
            }
            data[rowIndex][0] += " (s" + rowIndex + ")";
            rowIndex += 1;
        }
        String parsingTable = FlipTable.of(symbols.toArray(new String[0]), data);
        fileHandler.writeToFile("parsingTable.txt", parsingTable);
        System.out.println(parsingTable);
    }

    public String productionsToString(){
        return this.grammar.productionsToString();
    }

    public List<String> getProductionsForNonTerminal(String nonTerminal) throws Exception{
        return this.grammar.productionsForAGivenNonTerminal(nonTerminal);
    }
}
