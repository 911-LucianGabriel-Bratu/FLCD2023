package grammar;

import utils.FileHandler;
import utils.Tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grammar {
    private Set<String> terminals;
    private Set<String> nonTerminals;

    private Set<String> productions;
    private String productionSymbol;
    private String initialState;
    private FileHandler fileHandler;
    private String fileName;

    public Grammar(String fileName){
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.productions = new HashSet<>();
        this.fileHandler = new FileHandler();
        this.fileName = fileName;
        this.productionSymbol = "";
        this.initialState = "";
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

    private void storeProductions(String productions){
        List<String> productionsTokenized = Tokenizer.tokenize(productions, ",");
        this.productions.addAll(productionsTokenized);
    }

    public void scanGrammar() throws IOException {
        String nonTerminals;
        String terminals;
        List<String> lines = this.fileHandler.readFile(this.fileName);
        for(String line: lines){
            if(line.charAt(0) == 'G'){
                String contents = Tokenizer.tokenize(line, "=").get(1);
                contents = contents.substring(1, contents.length()-1);
                List<String> tokenizedContents = Tokenizer.tokenize(contents, ";");
                nonTerminals = tokenizedContents.get(0);
                terminals = tokenizedContents.get(1);
                this.productionSymbol = tokenizedContents.get(2);
                this.initialState = tokenizedContents.get(3);
                storeNonTerminals(nonTerminals);
                storeTerminals(terminals);
            }
            else if(line.charAt(0) == this.productionSymbol.charAt(0)){
                String productions = Tokenizer.tokenize(line, ":").get(1);
                storeProductions(productions);
            }
        }
    }

    public String nonTerminalsToString(){
        return this.nonTerminals.toString();
    }

    public boolean isCFG(){
        boolean isCFG = true;
        for(String production: this.productions){
            String leftSide = Tokenizer.tokenize(production, "->").get(0);
            if(!nonTerminals.contains(leftSide)){
                isCFG = false;
            }
        }
        return isCFG;
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
                if(production.charAt(0) == nonTerminal.charAt(0)){
                    foundProductions.add(production);
                }
            }
        }
        return foundProductions;
    }
}
