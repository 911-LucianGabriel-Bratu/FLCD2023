public class Main {
    public static void main(String[] args) {
        ConstantsSymbolTable constantsSymbolTable = new ConstantsSymbolTable();
        constantsSymbolTable.setRoot(new ConstantsNode(10));
        constantsSymbolTable.getRoot().setLeftChild(new ConstantsNode(8));
        constantsSymbolTable.getRoot().getLeftChild().setLeftChild(new ConstantsNode(6));
        constantsSymbolTable.getRoot().getLeftChild().getLeftChild().setLeftChild(new ConstantsNode(4));
        constantsSymbolTable.getRoot().getLeftChild().getLeftChild().getLeftChild().setLeftChild(new ConstantsNode(2));

        constantsSymbolTable.preOrder(constantsSymbolTable.getRoot());

        System.out.println("\n");

        constantsSymbolTable.setRoot(constantsSymbolTable.buildTree(constantsSymbolTable.getRoot()));
        System.out.println("Preorder traversal of balanced BST is: ");
        constantsSymbolTable.preOrder(constantsSymbolTable.getRoot());
    }
}