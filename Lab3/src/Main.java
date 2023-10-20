public class Main {
    public static void main(String[] args) {
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