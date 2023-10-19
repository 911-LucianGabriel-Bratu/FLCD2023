public class Main {
    public static void main(String[] args) {
        ConstantsSymbolTable constantsSymbolTable = new ConstantsSymbolTable();
        constantsSymbolTable.setRoot(new ConstantsNode(10));
        constantsSymbolTable.insert(constantsSymbolTable.getRoot(), new ConstantsNode(8));
        constantsSymbolTable.insert(constantsSymbolTable.getRoot(), new ConstantsNode("some string"));
        constantsSymbolTable.insert(constantsSymbolTable.getRoot(), new ConstantsNode(4));
        constantsSymbolTable.insert(constantsSymbolTable.getRoot(), new ConstantsNode(2));

        constantsSymbolTable.preOrder(constantsSymbolTable.getRoot());

        System.out.println("\n");

        constantsSymbolTable.setRoot(constantsSymbolTable.buildTree(constantsSymbolTable.getRoot()));
        System.out.println("Preorder traversal of balanced Constants BST is: ");
        constantsSymbolTable.preOrder(constantsSymbolTable.getRoot());

        IdentifiersSymbolTable identifiersSymbolTable = new IdentifiersSymbolTable();
        identifiersSymbolTable.setRoot(new IdentifiersNode(new Pair<>("a", 0)));
        identifiersSymbolTable.insert(identifiersSymbolTable.getRoot(), new IdentifiersNode("b"));
        identifiersSymbolTable.insert(identifiersSymbolTable.getRoot(), new IdentifiersNode("c"));
        identifiersSymbolTable.insert(identifiersSymbolTable.getRoot(), new IdentifiersNode("d"));
        identifiersSymbolTable.insert(identifiersSymbolTable.getRoot(), new IdentifiersNode("e"));

        System.out.println("\n");
        identifiersSymbolTable.preOrder(identifiersSymbolTable.getRoot());
        System.out.println("\n");
        identifiersSymbolTable.setRoot(identifiersSymbolTable.buildTree(identifiersSymbolTable.getRoot()));
        System.out.println("Preorder traversal of balanced Identifiers BST is: ");
        identifiersSymbolTable.preOrder(identifiersSymbolTable.getRoot());

        System.out.println("Value of identifier a is: " + constantsSymbolTable.search(constantsSymbolTable.getRoot(), identifiersSymbolTable.search(identifiersSymbolTable.getRoot(), "a").getValue().getSecond()).getValue().getSecond());
    }
}