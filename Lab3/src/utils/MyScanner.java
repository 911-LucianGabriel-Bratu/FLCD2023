package utils;

import finite_automata.FiniteAutomata;
import symbol_table.BSTNode;
import symbol_table.SymbolTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class MyScanner {
    private TreeMap<String, List<String>> tokens = new TreeMap<>();
    private List<Pair<String, Integer>> PIF = new ArrayList<>();
    private SymbolTable symbolTable = new SymbolTable();

    private final FiniteAutomata identifiersFA = new FiniteAutomata("FAIdentifiers.in");

    private final FiniteAutomata integersFA = new FiniteAutomata("FAIntegers.in");

    private final FileHandler fileHandler = new FileHandler();

    public void scanForTokens(String fileName) throws IOException {
        List<String> lines = fileHandler.readFile(fileName);
        List<String> res;
        for(String line: lines){
            res = Tokenizer.tokenize(line.trim(), "$");
            if(res.size() > 0){
                tokens.put(res.get(0), Tokenizer.tokenize(res.get(1), ", "));
            }
            res.clear();
        }
    }

    public List<String> removeSeparators(List<String> line){
        List<String> separators = this.tokens.get("separators");
        for(String separator : separators){
            line = Tokenizer.tokenizeList(line, separator);
        }
        line = Tokenizer.tokenizeList(line, " ");

        return line;
    }

    public List<String> removeDelimiters(List<String> line){
        List<String> delimiters = this.tokens.get("delimiters");
        for(String delimiter : delimiters){
            line = Tokenizer.tokenizeList(line, delimiter);
        }
        return line;
    }
    public boolean isIdentifier(String str){
        int i = 0;
        List<String> allLetters = new ArrayList<>();
        int index = 0;
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            allLetters.add(String.valueOf(letter));
        }
        for (char letter = 'a'; letter <= 'z'; letter++) {
            allLetters.add(String.valueOf(letter));
        }
        while(i < str.length()){
            if(isNumeric(String.valueOf(str.charAt(i)))){
                return false;
            }
            if(!allLetters.contains(String.valueOf(str.charAt(i)))){
                return false;
            }
            i += 1;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public boolean isToken(String entry) {
        return (tokens.get("reserved words").contains(entry) ||
                tokens.get("arithmetic operators").contains(entry) ||
                tokens.get("assignment operators").contains(entry) ||
                tokens.get("logical operators").contains(entry) ||
                tokens.get("relational operators").contains(entry) ||
                tokens.get("delimiters").contains(entry));
    }

    public List<String> separateDelimiters(List<String> line){
        List<String> result = new ArrayList<>();

        for (String str : line) {
            StringBuilder token = new StringBuilder();
            for (char c : str.toCharArray()) {
                if (!tokens.get("delimiters").contains(String.valueOf(c))
                && !tokens.get("separators").contains(String.valueOf(c))) {
                    token.append(c);
                } else {
                    if (token.length() > 0) {
                        result.add(token.toString());
                        token = new StringBuilder();
                    }
                    result.add(String.valueOf(c));
                }
            }
            if (token.length() > 0) {
                result.add(token.toString());
            }
        }

        return result;
    }

    public void scanProgram(String fileName) throws Exception {
        integersFA.scanFAFile();
        identifiersFA.scanFAFile();
        List<String> lines = fileHandler.readFile(fileName);
        List<String> res;
        int lineIndex = 1;
        for(String line: lines){
            res = Tokenizer.tokenize(line.trim(), " ");
            res = separateDelimiters(res);
            boolean string_constant = false;
            StringBuilder string_constant_var = new StringBuilder();
            if(res.size() > 0){
                for(String entry: res){
                    if(!string_constant){
                        if(tokens.get("reserved words").contains(entry)){
                            PIF.add(new Pair<>(entry, -1));
                        } else if (tokens.get("arithmetic operators").contains(entry)) {
                            PIF.add(new Pair<>(entry, -1));
                        } else if (tokens.get("assignment operators").contains(entry)) {
                            PIF.add(new Pair<>(entry, -1));
                        } else if (tokens.get("logical operators").contains(entry)) {
                            PIF.add(new Pair<>(entry, -1));
                        } else if (tokens.get("relational operators").contains(entry)) {
                            PIF.add(new Pair<>(entry, -1));
                        } else if (tokens.get("delimiters").contains(entry)) {
                            PIF.add(new Pair<>(entry, -1));
                        } else if (Objects.equals(entry, ";")) {
                            PIF.add(new Pair<>(entry, -1));
                        } else if (Objects.equals(entry, ":")) {
                            PIF.add(new Pair<>(entry, -1));
                        }
                        else if (integersFA.isDFAValid(entry)) {
                            if(symbolTable.getRoot() == null){
                                BSTNode bstNode = new BSTNode(Integer.parseInt(entry));
                                PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                symbolTable.setRoot(bstNode);
                            }
                            else {
                                if(symbolTable.searchForValue(symbolTable.getRoot(), Integer.parseInt(entry)) == null){
                                    BSTNode bstNode = new BSTNode(Integer.parseInt(entry));
                                    PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                    symbolTable.insert(symbolTable.getRoot(), bstNode);
                                }
                                else{
                                    BSTNode bstNode = symbolTable.searchForValue(symbolTable.getRoot(), Integer.parseInt(entry));
                                    PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                }
                            }
                        }
                        else if(Objects.equals(String.valueOf(entry.charAt(0)), "\"")){
                            string_constant = true;
                            string_constant_var.append(entry);
                            string_constant_var = new StringBuilder(string_constant_var).deleteCharAt(0);
                        }
                        else{
                            if(identifiersFA.isDFAValid(entry)){
                                if(symbolTable.getRoot() == null){
                                    BSTNode bstNode = new BSTNode(entry);
                                    PIF.add(new Pair<>("identifier", bstNode.getValue().getFirst()));
                                    symbolTable.setRoot(bstNode);
                                }
                                else {
                                    if(symbolTable.searchForValue(symbolTable.getRoot(), entry) == null){
                                        BSTNode bstNode = new BSTNode(entry);
                                        PIF.add(new Pair<>("identifier", bstNode.getValue().getFirst()));
                                        symbolTable.insert(symbolTable.getRoot(), bstNode);
                                    }
                                    else{
                                        BSTNode bstNode = symbolTable.searchForValue(symbolTable.getRoot(), entry);
                                        PIF.add(new Pair<>("identifier", bstNode.getValue().getFirst()));
                                    }
                                }
                            }
                            else {
                                if(Objects.equals(String.valueOf(entry.charAt(entry.length()-1)), ";")){
                                    String entry_sliced = entry.substring(0, entry.length()-1);
                                    if(identifiersFA.isDFAValid(entry_sliced)){
                                        if(symbolTable.getRoot() == null){
                                            BSTNode bstNode = new BSTNode(entry_sliced);
                                            PIF.add(new Pair<>("identifier", bstNode.getValue().getFirst()));
                                            symbolTable.setRoot(bstNode);
                                        }
                                        else {
                                            if(symbolTable.searchForValue(symbolTable.getRoot(), entry_sliced) == null){
                                                BSTNode bstNode = new BSTNode(entry_sliced);
                                                PIF.add(new Pair<>("identifier", bstNode.getValue().getFirst()));
                                                symbolTable.insert(symbolTable.getRoot(), bstNode);
                                            }
                                            else{
                                                BSTNode bstNode = symbolTable.searchForValue(symbolTable.getRoot(), entry_sliced);
                                                PIF.add(new Pair<>("identifier", bstNode.getValue().getFirst()));
                                            }
                                        }
                                        PIF.add(new Pair<>(";", -1));
                                    }
                                    else if (integersFA.isDFAValid(entry_sliced)) {
                                        if(symbolTable.getRoot() == null){
                                            BSTNode bstNode = new BSTNode(Integer.parseInt(entry_sliced));
                                            PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                            symbolTable.setRoot(bstNode);
                                        }
                                        else {
                                            if(symbolTable.searchForValue(symbolTable.getRoot(), Integer.parseInt(entry_sliced)) == null){
                                                BSTNode bstNode = new BSTNode(Integer.parseInt(entry_sliced));
                                                PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                                symbolTable.insert(symbolTable.getRoot(), bstNode);
                                            }
                                            else{
                                                BSTNode bstNode = symbolTable.searchForValue(symbolTable.getRoot(), Integer.parseInt(entry_sliced));
                                                PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                            }
                                        }
                                        PIF.add(new Pair<>(";", -1));
                                    }
                                    else{
                                        throw new Exception("Program is lexically incorrect. Mistake on line " + lineIndex + ". Token is: " + entry_sliced);
                                    }
                                }
                                else{
                                    throw new Exception("Program is lexically incorrect. Mistake on line " + lineIndex + ". Token is: " + entry);
                                }
                            }
                        }
                    }
                    else{
                        if(Objects.equals(String.valueOf(entry.charAt(entry.length()-1)), "\"")){
                            string_constant = false;
                            string_constant_var.append(" ").append(entry);
                            string_constant_var = new StringBuilder(string_constant_var).deleteCharAt(string_constant_var.length()-1);
                            if(symbolTable.getRoot() == null){
                                BSTNode bstNode = new BSTNode(string_constant_var);
                                PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                symbolTable.setRoot(bstNode);
                            }
                            else {
                                if(symbolTable.searchForValue(symbolTable.getRoot(), string_constant_var) == null){
                                    BSTNode bstNode = new BSTNode(string_constant_var);
                                    PIF.add(new Pair<>("constant", bstNode.getValue().getFirst()));
                                    symbolTable.insert(symbolTable.getRoot(), bstNode);
                                }
                            }
                        }
                        else{
                            string_constant_var.append(" ").append(entry);
                        }
                    }
                }
            }
            res.clear();
            lineIndex += 1;
        }
    }

    public TreeMap<String, List<String>> getTokens() {
        return tokens;
    }

    public void setTokens(TreeMap<String, List<String>> tokens) {
        this.tokens = tokens;
    }

    public List<Pair<String, Integer>> getPIF() {
        return PIF;
    }

    public String PIFToString(){
        StringBuilder str = new StringBuilder();
        for(Pair<String, Integer> pair: this.PIF){
            str.append("(").append(pair.getFirst()).append(", ").append(pair.getSecond()).append(")").append("\n");
        }
        return str.toString();
    }

    public void setPIF(List<Pair<String, Integer>> PIF) {
        this.PIF = PIF;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }
}
