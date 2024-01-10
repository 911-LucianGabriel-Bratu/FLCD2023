import finite_automata.FiniteAutomata;
import grammar.Grammar;
import parser.LR;
import symbol_table.BSTNode;
import symbol_table.SymbolTable;
import utils.MyScanner;
import utils.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        //ScannerDemo();
        //LRParserDemo();
        //parseG1();
        parseG2("p1.txt");
    }

    public static void parseG1() throws Exception{
        LR lr = new LR("g1.txt");
        List<String> sequence = new ArrayList<>();
        sequence.add("a");
        sequence.add("b");
        sequence.add("b");
        sequence.add("c");
        System.out.println(lr.parseSequence(sequence).toString());
    }

    public static void parseG2(String file) throws Exception{
        MyScanner myScanner = new MyScanner();
        myScanner.scanForTokens("token.in");
        try{
            myScanner.scanProgram(file);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        List<String> tokens = myScanner.getPIF().stream()
                .map(Pair::getFirst)
                .toList();
        LR lr = new LR("g2.txt");
        System.out.print(lr.parseSequence(tokens).toString());
    }

    public static void LRParserDemo() throws Exception {
        LR lr = new LR("g2.txt");
        //System.out.println(lr.closure(lr.getProductionsForNonTerminal("S'")).toString());
        //System.out.println(lr.goto_(lr.getGrammar().getProductions().stream().toList(), "factor"));
        //System.out.println(lr.DetermineCanonicalCollection());
        lr.printParsingTable();
    }

    public static void GrammarDemo() throws IOException{
        Grammar grammar = new Grammar("g2.txt");
        grammar.scanGrammar();
        int option = -1;

        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.println("Select the desired operation:");
            System.out.println("1. Print the set of nonTerminals");
            System.out.println("2. Print the set of terminals");
            System.out.println("3. Print the set of productions");
            System.out.println("4. Print the set of productions for a given nonTerminal");
            System.out.println("5. CFG check");
            System.out.println("6. Exit");
            System.out.println("Enter the operation number:");
            if(scanner.hasNext()){
                option = scanner.nextInt();
                scanner.nextLine();
            }
            switch (option){
                case 1:
                    System.out.println(grammar.nonTerminalsToString());
                    break;
                case 2:
                    System.out.println(grammar.terminalsToString());
                    break;
                case 3:
                    System.out.println(grammar.productionsToString());
                    break;
                case 4:
                    System.out.println("Enter nonTerminal: ");
                    if(scanner.hasNext()){
                        String nonTerminal = scanner.next();
                        try{
                            System.out.println(grammar.productionsForGivenNonTerminalToString(nonTerminal));
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 5:
                    System.out.println(grammar.isCFG());
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Operation not recognized.");
            }
        }
    }

    public static void FADemo() throws IOException{
        FiniteAutomata finiteAutomata = new FiniteAutomata("FAIdentifiers.in");
        finiteAutomata.scanFAFile();
        int option = -1;

        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.println("Select the desired operation:");
            System.out.println("1. Print the set of states");
            System.out.println("2. Print the alphabet");
            System.out.println("3. Print the transitions");
            System.out.println("4. Print the initial state");
            System.out.println("5. Print the final states");
            System.out.println("6. Verify DFA");
            System.out.println("7. Exit");
            System.out.println("Enter the operation number:");
            if(scanner.hasNext()){
                option = scanner.nextInt();
                scanner.nextLine();
            }
            switch (option){
                case 1:
                    System.out.println(finiteAutomata.statesToString());
                    break;
                case 2:
                    System.out.println(finiteAutomata.alphabetToString());
                    break;
                case 3:
                    System.out.println(finiteAutomata.transitionsToString());
                    break;
                case 4:
                    System.out.println(finiteAutomata.initialStateToString());
                    break;
                case 5:
                    System.out.println(finiteAutomata.finalStatesToString());
                    break;
                case 6:
                    System.out.println("Enter dfa: ");
                    if(scanner.hasNext()){
                        String dfa = scanner.next();
                        System.out.println(finiteAutomata.isDFAValid(dfa));
                    }
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Operation not recognized.");
            }
        }
    }

    public static void ScannerDemo() throws IOException {
        MyScanner myScanner = new MyScanner();
        myScanner.scanForTokens("token.in");
        try{
            myScanner.scanProgram("p1.txt");
            System.out.println("Tokens: \n" + myScanner.getTokens() + "\n");
            myScanner.getFileHandler().writeToFile("PIF.out", myScanner.PIFToString());
            myScanner.getFileHandler().writeToFile("ST.out", myScanner.getSymbolTable().preOrderStr(
                    myScanner.getSymbolTable().getRoot()));
            System.out.println("\nProgram is lexically correct.");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            myScanner.getFileHandler().writeToFile("PIF.out", "");
            myScanner.getFileHandler().writeToFile("ST.out", "");
        }
    }
    public static void STDemo(){
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.setRoot(new BSTNode("a"));
        symbolTable.insert(symbolTable.getRoot(), new BSTNode("b"));
        symbolTable.insert(symbolTable.getRoot(), new BSTNode("c"));
        symbolTable.insert(symbolTable.getRoot(), new BSTNode("some string"));
        symbolTable.insert(symbolTable.getRoot(), new BSTNode(1));
        symbolTable.insert(symbolTable.getRoot(), new BSTNode(30));
        symbolTable.insert(symbolTable.getRoot(), new BSTNode(50.50));

        symbolTable.setRoot(symbolTable.buildTree(symbolTable.getRoot()));

        symbolTable.preOrder(symbolTable.getRoot());

        System.out.println("\n");

        System.out.println("Value with key 2 is: " + symbolTable.findValue(2));
    }
}