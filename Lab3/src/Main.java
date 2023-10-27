import symbol_table.BSTNode;
import symbol_table.SymbolTable;
import utils.MyScanner;

public class Main {
    public static void main(String[] args) throws Exception {
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