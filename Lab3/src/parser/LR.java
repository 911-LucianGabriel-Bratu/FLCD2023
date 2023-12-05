package parser;

import grammar.Grammar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LR {
    private final Grammar grammar;

    public LR(String fileName) throws IOException {
        this.grammar = new Grammar(fileName);
        this.grammar.scanGrammar();
        enrichGrammar();
    }
    private void enrichGrammar(){
        String production = "S'->. " + this.grammar.getStartingSymbol();
        this.grammar.setStartingSymbol("S'");
        this.grammar.getNonTerminals().add("S'");
        this.grammar.getProductions().add(production);
    }

    public List<String> closure(String I) throws Exception {
        List<String> C = new ArrayList<>();
        boolean hasAdded = true;
        C.add(I);
        while(hasAdded){
            List<String> newProductions = new ArrayList<>();
            hasAdded = false;
            for(String production: C){
                int index = production.indexOf(".");
                String possibleNonTerminal = String.valueOf(production.charAt(index + 2));
                if(grammar.getNonTerminals().contains(possibleNonTerminal)){
                    for(String foundProduction: grammar.productionsForAGivenNonTerminal(possibleNonTerminal)){
                        String possibleProduction;
                        int arrowIndex = foundProduction.indexOf("->");
                        arrowIndex += 2;
                        possibleProduction = foundProduction.substring(0, arrowIndex).concat(". ").concat(foundProduction.substring(arrowIndex));
                        if(!C.contains(possibleProduction)){
                            newProductions.add(possibleProduction);
                            hasAdded = true;
                        }
                    }
                }
            }
            C.addAll(newProductions);
        }
        return C;
    }

    public String productionsToString(){
        return this.grammar.productionsToString();
    }

    public List<String> getProductionsForNonTerminal(String nonTerminal) throws Exception{
        return this.grammar.productionsForAGivenNonTerminal(nonTerminal);
    }
}
