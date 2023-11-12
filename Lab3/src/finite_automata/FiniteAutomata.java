package finite_automata;

import utils.FileHandler;
import utils.Pair;
import utils.Tokenizer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FiniteAutomata {
    private Map<Pair<String, String>, String> transitions;
    private Set<String> alphabet;
    private String initialState;
    private Set<String> finalStates;
    private Set<String> states;
    private String fileName;
    private FileHandler fileHandler;

    public FiniteAutomata(String fileName){
        this.fileName = fileName;
        this.transitions = new HashMap<>();
        this.states = new HashSet<>();
        this.alphabet = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.initialState = "";
        this.fileHandler = new FileHandler();
    }

    public void scanFAFile() throws IOException {
        String setOfStatesSymbol = "";
        String alphabetSymbol = "";
        String transitionFunctionSymbol = "";
        String initialStateSymbol = "";
        String finalStateSymbol = "";

        List<String> lines = this.fileHandler.readFile(this.fileName);
        for(String line: lines){
            List<String> res = Tokenizer.tokenize(line, "=");
            if(res.get(0).compareTo("M") == 0){
                String allSymbols = res.get(1);
                List<String> allSymbolsTokenized = Tokenizer.tokenizeList(Tokenizer.tokenizeList(Tokenizer.tokenize(allSymbols, "("), ")"), ",");
                if(allSymbolsTokenized.size() == 5){
                    setOfStatesSymbol = allSymbolsTokenized.get(0);
                    alphabetSymbol = allSymbolsTokenized.get(1);
                    transitionFunctionSymbol = allSymbolsTokenized.get(2);
                    initialStateSymbol = allSymbolsTokenized.get(3);
                    finalStateSymbol = allSymbolsTokenized.get(4);
                    this.initialState = initialStateSymbol;
                }
            }
            else{
                if(res.get(0).compareTo(setOfStatesSymbol) == 0){
                    String allStates = res.get(1);
                    List<String> allStatesTokenized = Tokenizer.tokenizeList(Tokenizer.tokenizeList(Tokenizer.tokenize(allStates, "{"), "}"), ",");
                    this.states.addAll(allStatesTokenized);
                } else if (res.get(0).compareTo(alphabetSymbol) == 0) {
                    String allAlphabet = res.get(1);
                    List<String> allAlphabetTokenized = Tokenizer.tokenizeList(Tokenizer.tokenizeList(Tokenizer.tokenize(allAlphabet, "{"), "}"), ",");
                    this.alphabet.addAll(allAlphabetTokenized);
                } else if (res.get(0).compareTo(finalStateSymbol) == 0) {
                    String allFinalStates = res.get(1);
                    List<String> allFinalStatesTokenized = Tokenizer.tokenizeList(Tokenizer.tokenizeList(Tokenizer.tokenize(allFinalStates, "{"), "}"), ",");
                    this.finalStates.addAll(allFinalStatesTokenized);
                }
                else{
                    if(res.get(0).contains(transitionFunctionSymbol)){
                        String left_side = Tokenizer.tokenize(res.get(0), transitionFunctionSymbol).get(0);
                        String right_side = res.get(1);
                        List<String> leftSideTokenized = Tokenizer.tokenizeList(Tokenizer.tokenizeList(Tokenizer.tokenize(left_side, "("), ")"), ",");
                        this.transitions.put(new Pair<>(leftSideTokenized.get(0), leftSideTokenized.get(1)), right_side);
                    }
                }
            }
        }
    }

    public String finalStatesToString(){
        StringBuilder res = new StringBuilder("Final States: {");
        for(String state: this.finalStates){
            res.append(state).append(", ");
        }
        res = new StringBuilder(res.substring(0, res.length() - 2));
        res.append("}\n");
        return res.toString();
    }

    public String initialStateToString(){
        return "Initial State: " + this.initialState + "\n";
    }

    public String alphabetToString(){
        StringBuilder res = new StringBuilder("Alphabet: {");
        for(String state: this.alphabet){
            res.append(state).append(", ");
        }
        res = new StringBuilder(res.substring(0, res.length() - 2));
        res.append("}\n");
        return res.toString();
    }

    public boolean dfaContainsOnlyAlphabetElements(String dfa){
        boolean isOk = true;
        for(char c: dfa.toCharArray()){
            if (!alphabet.contains(String.valueOf(c))) {
                isOk = false;
                break;
            }
        }
        return isOk;
    }

    public boolean isDFAValid(String dfa){
        if(!dfaContainsOnlyAlphabetElements(dfa)){
            return false;
        }
        String currentState = initialState;

        for (char symbol : dfa.toCharArray()) {
            String nextState = transitions.get(new Pair<>(currentState, String.valueOf(symbol)));
            if (nextState == null) {
                return false;
            }
            currentState = nextState;
        }
        return finalStates.contains(currentState);
    }

    public String statesToString(){
        StringBuilder res = new StringBuilder("States: {");
        for(String state: this.states){
            res.append(state).append(", ");
        }
        res = new StringBuilder(res.substring(0, res.length() - 2));
        res.append("}\n");
        return res.toString();

    }

    public String transitionsToString(){
        StringBuilder res = new StringBuilder("Transitions:\n");
        for (Map.Entry<Pair<String, String>, String> entry : this.transitions.entrySet()) {
            Pair<String, String> transitionKey = entry.getKey();
            String transition = transitionKey.getFirst() + "--" + transitionKey.getSecond() + "-->" + entry.getValue();
            res.append(transition).append("\n");
        }
        return res.toString();
    }
}
