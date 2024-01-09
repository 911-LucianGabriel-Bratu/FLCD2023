package grammar;

import utils.FileHandler;
import utils.Tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Grammar {
    private Set<String> terminals;
    private Set<String> nonTerminals;

    private Set<String> productions;
    private String productionSymbol;
    private String startingSymbol;
    private String initialStartingSymbol;
    private FileHandler fileHandler;
    private String fileName;

    public Grammar(String fileName){
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.productions = new HashSet<>();
        this.fileHandler = new FileHandler();
        this.fileName = fileName;
        this.productionSymbol = "";
        this.startingSymbol = "";
        this.initialStartingSymbol = "";
    }

    private void storeNonTerminals(String nonTerminals){
        nonTerminals = nonTerminals.substring(1, nonTerminals.length()-1);
        List<String> nonTerminalsTokenized = Tokenizer.tokenize(nonTerminals, ",");
        this.nonTerminals.addAll(nonTerminalsTokenized);
    }

    private void storeTerminals(String terminals){
        terminals = terminals.substring(1, terminals.length()-1);
        List<String> terminalsTokenized = Tokenizer.tokenize(terminals, ",");
        this.terminals.addAll(terminalsTokenized);
    }

    public void scanGrammar() throws IOException {
        String nonTerminals;
        String terminals;
        List<String> lines = this.fileHandler.readFile(this.fileName);
        if(lines.get(0).charAt(0) == 'G'){
            String line = lines.get(0);
            String contents = line.substring(2);
            contents = contents.substring(1, contents.length()-1);
            List<String> tokenizedContents = Tokenizer.tokenize(contents, ";");
            nonTerminals = tokenizedContents.get(0);
            terminals = tokenizedContents.get(1);
            this.productionSymbol = tokenizedContents.get(2);
            this.startingSymbol = tokenizedContents.get(3);
            this.initialStartingSymbol = this.startingSymbol;
            storeNonTerminals(nonTerminals);
            storeTerminals(terminals);
        }
        if(lines.get(1).charAt(0) == this.productionSymbol.charAt(0)){
            List<String> remainingLines = lines.subList(2, lines.size());
            this.productions.addAll(remainingLines.stream().map(line -> {
                        List<String> tokenized = Tokenizer.tokenize(line, "->");
                        if(tokenized.size() == 2){
                            if(tokenized.get(1).compareTo("epsilon") == 0){
                                //return tokenized.get(0).concat("->");
                            }
                        }
                        return line;
                    }

            ).toList());
        }
    }

    public String nonTerminalsToString(){
        return this.nonTerminals.toString();
    }

    public boolean isCFG(){
        boolean isCFG = true;
        boolean containsStartingSymbolProd = false;
        for(String production: this.productions){
            String leftSide = Tokenizer.tokenize(production, "->").get(0);
            if(!nonTerminals.contains(leftSide)){
                isCFG = false;
            }
            if(leftSide.compareTo(startingSymbol) == 0){
                containsStartingSymbolProd = true;
            }
        }
        return isCFG && containsStartingSymbolProd;
    }

    public String terminalsToString(){
        return this.terminals.toString();
    }

    public String productionsToString(){
        return this.productions.toString();
    }

    public String productionsForGivenNonTerminalToString(String nonTerminal) throws Exception {
        return productionsForAGivenNonTerminal(nonTerminal).toString();
    }

    public List<String> productionsForAGivenNonTerminal(String nonTerminal) throws Exception {
        List<String> foundProductions = new ArrayList<>();
        if(!this.nonTerminals.contains(nonTerminal)){
            throw new Exception("The set of nonTerminals does not contain given nonTerminal!");
        }
        else{
            for(String production: this.productions){
                int index = production.indexOf("->");
                String leftSide = production.substring(0, index);
                if(leftSide.compareTo(nonTerminal) == 0){
                    foundProductions.add(production);
                }
            }
        }
        return foundProductions;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(Set<String> terminals) {
        this.terminals = terminals;
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(Set<String> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public String getProductionSymbol() {
        return productionSymbol;
    }

    public void setProductionSymbol(String productionSymbol) {
        this.productionSymbol = productionSymbol;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public String getInitialStartingSymbol() {
        return initialStartingSymbol;
    }

    public void setStartingSymbol(String startingSymbol) {
        this.startingSymbol = startingSymbol;
    }

    public Set<String> getProductions() {
        return productions;
    }

    public void setProductions(Set<String> productions) {
        this.productions = productions;
    }
}
