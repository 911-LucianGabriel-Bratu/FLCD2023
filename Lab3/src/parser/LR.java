package parser;

import grammar.Grammar;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class LR {
    private final Grammar grammar;

    public LR(String fileName) throws IOException {
        this.grammar = new Grammar(fileName);
        this.grammar.scanGrammar();
        enrichGrammar();
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

    public List<List<String>> DetermineCanonicalCollection() throws Exception {
        List<List<String>> canonicalCollection = new ArrayList<>();

        String initialProduction = "S'->. " + grammar.getInitialStartingSymbol();
        List<String> initialElement = new ArrayList<>();
        initialElement.add(initialProduction);
        canonicalCollection.add(closure(initialElement));

        List<String> allSymbols = new ArrayList<>();
        allSymbols.addAll(grammar.getNonTerminals());
        allSymbols.addAll(grammar.getTerminals());

        boolean hasAdded = true;
        while (hasAdded) {
            hasAdded = false;
            List<List<String>> pendingAdditions = new ArrayList<>();

            for (List<String> s : canonicalCollection) {
                for (String X : allSymbols) {
                    List<String> gotoCollection = goto_(s, X);
                    if (!gotoCollection.isEmpty() && !canonicalCollection.contains(gotoCollection)
                        && !pendingAdditions.contains(gotoCollection)) {
                        pendingAdditions.add(gotoCollection);
                        hasAdded = true;
                    }
                }
            }
            canonicalCollection.addAll(pendingAdditions);
        }

        return canonicalCollection;
    }

    public String productionsToString(){
        return this.grammar.productionsToString();
    }

    public List<String> getProductionsForNonTerminal(String nonTerminal) throws Exception{
        return this.grammar.productionsForAGivenNonTerminal(nonTerminal);
    }
}
