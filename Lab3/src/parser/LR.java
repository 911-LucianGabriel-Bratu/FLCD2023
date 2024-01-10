package parser;

import com.jakewharton.fliptables.FlipTable;
import grammar.Grammar;
import utils.FileHandler;
import utils.Pair;
import utils.Tokenizer;

import java.io.IOException;
import java.sql.Array;
import java.util.*;

public class LR {
    private final Grammar grammar;

    public LR(String fileName) throws IOException {
        this.grammar = new Grammar(fileName);
        this.grammar.scanGrammar();
        enrichGrammar();
    }

    public class Entry {
        public List<String> currentS;

        public int parentS = -1;
        public int previousSIndex = -1;

        String X;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Entry otherEntry = (Entry) obj;
            return currentS.equals(otherEntry.currentS) && previousSIndex == otherEntry.previousSIndex
                    && X.equals(otherEntry.X);
        }
    }

    public class ParserRow {
        public int index;
        public String symbol;

        public int parent;
        public int rightSibling;

        public ParserRow(int index, String nonTerminal, int parent, int rightSibling) {
            this.index = index;
            this.symbol = nonTerminal;
            this.parent = parent;
            this.rightSibling = rightSibling;
        }

        public ParserRow() {}
    }

    public class ParserOutput {
        public List<ParserRow> parserRows;

        public ParserOutput() {
            parserRows = new ArrayList<>();
        }

        public String toString(){
            StringBuilder builder = new StringBuilder();
            for (ParserRow row : parserRows) {
                builder.append("(").append(row.index).append(", ").append(row.symbol)
                        .append(", ").append(row.parent).append(", ").append(row.rightSibling)
                        .append(")\n");
            }
            return builder.toString();
        }
    }

    public class CanonicalCollection {
        public int getRealParent(int parentS) {
            int realParent = parentS;
            while (parentS != -1) {
                realParent = parentS;
                parentS = allEntries.get(parentS).parentS;
            }
            return realParent;
        }

        public int getRealIndex(int entryIndex) {
            return entryIndex - decrementCount.get(entryIndex);
        }

        List<Entry> validEntries;
        List<Integer> decrementCount;
        List<Entry> allEntries;
    }

    public enum State {
        SHIFT,
        REDUCE,
        SHIFT_REDUCE,
        REDUCE_REDUCE,
        ACCEPT
    }

    public class ParsingTableRow {
        public State state;
        public Map<String, Integer> goto_map;
        public String reductionProduction;
    }

    public class ParsingTable {
        public List<ParsingTableRow> rows;

        public String toString() {
            String string = new String();

            for (int index = 0; index < rows.size(); index++) {
                ParsingTableRow row = rows.get(index);
                string += index + " | " + row.state.toString() + " | " + row.goto_map.toString() + " | " + row.reductionProduction + "\n";
            }

            return string;
        }
    }

    public Grammar getGrammar() {
        return grammar;
    }

    private void enrichGrammar(){
        String production = "S'->. " + this.grammar.getStartingSymbol();
        this.grammar.setStartingSymbol("S'");
        this.grammar.getNonTerminals().add("S'");
        this.grammar.getProductions().add(production);
    }

    public List<String> closure(List<String> I) throws Exception {
        List<String> C = new ArrayList<>();
        boolean hasAdded = true;
        C.addAll(I);
        while(hasAdded){
            List<String> newProductions = new ArrayList<>();
            hasAdded = false;
            for(String production: C){
                int index = production.indexOf(".");
                if (index < production.length() - 2) {
                    String[] rightSideTokens = production.substring(index + 2).split(" ");
                    String possibleNonTerminal = rightSideTokens[0];
                    if (grammar.getNonTerminals().contains(possibleNonTerminal)) {
                        for (String foundProduction : grammar.productionsForAGivenNonTerminal(possibleNonTerminal)) {
                            String possibleProduction;
                            int arrowIndex = foundProduction.indexOf("->");
                            arrowIndex += 2;
                            possibleProduction = foundProduction.substring(0, arrowIndex).concat(". ").concat(foundProduction.substring(arrowIndex));
                            if (!C.contains(possibleProduction) && !newProductions.contains(possibleProduction)) {
                                newProductions.add(possibleProduction);
                                hasAdded = true;
                            }
                        }
                    }
                }
            }
            C.addAll(newProductions);
        }
        return C;
    }

    public List<String> goto_(List<String> s, String X) throws Exception {
        List<String> analysisElements = new ArrayList<>();

        for (String production : s) {
            int arrowIndex = production.indexOf("->");
            arrowIndex += 2;
            String rightSide = production.substring(arrowIndex);
            String[] rightSideToken = rightSide.split(" ");
            for (int index = 0; index < rightSideToken.length; index++) {
                if (rightSideToken[index].equals(X)) {
                    if (index > 0) {
                        if (rightSideToken[index - 1].equals(".")) {
                            String newProduction = production.substring(0, arrowIndex);
                            for (int subindex = 0; subindex < index - 1; subindex++) {
                                newProduction += rightSideToken[subindex] + " ";
                            }
                            newProduction += X;
                            newProduction += " .";
                            for (int subindex = index + 1; subindex < rightSideToken.length; subindex++) {
                                newProduction += " " + rightSideToken[subindex];
                            }
                            analysisElements.add(newProduction);
                            break;
                        }
                    }
                }
            }
        }

        return closure(analysisElements);
    }

    public CanonicalCollection DetermineCanonicalCollection() throws Exception {
        List<Entry> allEntries = new ArrayList<>();

        String initialProduction = "S'->. " + grammar.getInitialStartingSymbol();
        List<String> initialElement = new ArrayList<>();
        initialElement.add(initialProduction);
        Entry initialEntry = new Entry();
        initialEntry.currentS = closure(initialElement);
        allEntries.add(initialEntry);

        List<String> allSymbols = new ArrayList<>();
        allSymbols.addAll(grammar.getNonTerminals());
        allSymbols.addAll(grammar.getTerminals());

        List<Entry> pendingAdditions = new ArrayList<>();
        pendingAdditions.add(initialEntry);
        while (!pendingAdditions.isEmpty()) {
            List<Entry> newPendingAdditions = new ArrayList<>();
            for(int i = 0; i < pendingAdditions.size(); i++) {
                Entry entry = pendingAdditions.get(i);
                if(entry.parentS == -1) {
                    for (String X : allSymbols) {
                        List<String> gotoCollection = goto_(entry.currentS, X);
                        if (!gotoCollection.isEmpty()) {
                            Entry newEntry = new Entry();
                            newEntry.currentS = gotoCollection;
                            newEntry.previousSIndex = allEntries.size() - pendingAdditions.size() + i;
                            newEntry.X = X;
                            if(allEntries.stream().noneMatch(s -> s.equals(newEntry))
                                    && newPendingAdditions.stream().noneMatch(s -> s.equals(newEntry))) {
                                for (int j = 0; j < allEntries.size(); j++) {
                                    if (allEntries.get(j).currentS.equals(gotoCollection)) {
                                        newEntry.parentS = j;
                                    }
                                }
                                for (int j = 0; j < newPendingAdditions.size(); j++) {
                                    if (newPendingAdditions.get(j).currentS.equals(gotoCollection)) {
                                        newEntry.parentS = j + allEntries.size();
                                    }
                                }
                                newPendingAdditions.add(newEntry);
                            }
                        }
                    }
                }
            }
            allEntries.addAll(newPendingAdditions);
            pendingAdditions = newPendingAdditions;
        }

        CanonicalCollection canonicalCollection = new CanonicalCollection();
        canonicalCollection.allEntries = allEntries;
        canonicalCollection.validEntries = new ArrayList<Entry>();
        canonicalCollection.decrementCount = new ArrayList<Integer>();
        List<Integer> previousIndexDecrementCount = new ArrayList<Integer>();
        List<Integer> parentSDecrementCount = new ArrayList<Integer>();
        for (int i = 0; i < allEntries.size(); i++) {
            canonicalCollection.decrementCount.add(0);
            previousIndexDecrementCount.add(0);
            parentSDecrementCount.add(0);
        }
        for (int i = 0; i < allEntries.size(); i++) {
            if (allEntries.get(i).parentS != -1) {
                for (int j = i + 1; j < allEntries.size(); j++) {
                    Entry jEntry = allEntries.get(j);
                    if (jEntry.parentS != -1 && jEntry.parentS > i) {
                        Integer jValue = parentSDecrementCount.get(j);
                        jValue++;
                        parentSDecrementCount.set(j, jValue);
                    }
                    else if (jEntry.parentS == -1) {
                        Integer value = canonicalCollection.decrementCount.get(j);
                        value++;
                        canonicalCollection.decrementCount.set(j, value);
                    }
                    if (jEntry.previousSIndex > i) {
                        Integer jValue = previousIndexDecrementCount.get(j);
                        jValue++;
                        previousIndexDecrementCount.set(j, jValue);
                    }
                }
            }
            else {
                Entry addEntry = allEntries.get(i);
                canonicalCollection.validEntries.add(addEntry);
            }
        }

        for (int i = 0; i < allEntries.size(); i++) {
            Entry entry = allEntries.get(i);
            entry.previousSIndex -= previousIndexDecrementCount.get(i);
        }
        return canonicalCollection;
    }

    private boolean isDotAtEnd(String production){
        boolean isAtEnd = false;

        int index = production.indexOf(".");
        if(index == production.length() - 1){
            isAtEnd = true;
        }

        return isAtEnd;
    }

    private String checkAction(List<String> s){
        String action = "shift";
        String acceptProduction = "S'->" + grammar.getInitialStartingSymbol() + " .";
        String start = "S'";
        boolean hasShift = false;
        for(String production: s){
            if(production.compareTo(acceptProduction) == 0){
                action = "accept";
            } else if (isDotAtEnd(production)) {
                int arrowIndex = production.indexOf("->");
                String leftSide = production.substring(0, arrowIndex);
                if(!(leftSide.compareTo(start) == 0)){
                    if (action.equals("reduce") || hasShift) {
                        return hasShift ? "shift-reduce conflict" : "reduce-reduce conflict";
                    }
                    action = "reduce";
                }
            }
            else {
                if (action.equals("reduce")) {
                    return "shift-reduce conflict";
                }
                hasShift = true;
            }
        }
        return action;
    }

    public State getStateEnum(String string) throws Exception {
        if (string.compareTo("shift") == 0) {
            return State.SHIFT;
        }
        else if (string.compareTo("reduce") == 0) {
            return State.REDUCE;
        }
        else if (string.compareTo("shift-reduce conflict") == 0) {
            return State.SHIFT_REDUCE;
        }
        else if (string.compareTo("reduce-reduce conflict") == 0) {
            return State.REDUCE_REDUCE;
        }
        else if (string.compareTo("accept") == 0) {
            return State.ACCEPT;
        }
        else {
            throw new Exception("LR state has farted");
        }
    }

    public ParsingTable getParsingTable() throws Exception {
        ParsingTable table = new ParsingTable();
        table.rows = new ArrayList<ParsingTableRow>();

        CanonicalCollection canonicalCollection = this.DetermineCanonicalCollection();
        List<String> symbols = new ArrayList<>();
        symbols.addAll(grammar.getNonTerminals().stream().toList());
        symbols.addAll(grammar.getTerminals().stream().toList());

        for (int rowIndex = 0; rowIndex < canonicalCollection.validEntries.size(); rowIndex++){
            Entry currentEntry = canonicalCollection.validEntries.get(rowIndex);
            ParsingTableRow row = new ParsingTableRow();
            row.state = getStateEnum(checkAction(currentEntry.currentS));
            row.goto_map = new HashMap<>();
            for (int j = 0; j < symbols.size(); j++) {
                for (int k = 0; k < canonicalCollection.allEntries.size(); k++) {
                    Entry otherEntry = canonicalCollection.allEntries.get(k);
                    if (otherEntry.previousSIndex == rowIndex) {
                        if (otherEntry.X.compareTo(symbols.get(j)) == 0) {
                            int realParent = canonicalCollection.getRealParent(otherEntry.parentS);
                            if (realParent != -1) {
                                k = realParent;
                            }
                            k = canonicalCollection.getRealIndex(k);
                            row.goto_map.put(symbols.get(j), k);
                            break;
                        }
                    }
                }
            }
            if (row.state == State.REDUCE) {
                String dotProduction = currentEntry.currentS.get(0);
                row.reductionProduction = dotProduction.substring(0, dotProduction.length() - 2);
            }
            else {
                row.reductionProduction = "";
            }
            table.rows.add(row);
        }

        return table;
    }

    public void printParsingTable() throws Exception {
        FileHandler fileHandler = new FileHandler();
        CanonicalCollection canonicalCollection = this.DetermineCanonicalCollection();
        String[] headers = {" ", "action"};
        List<String> symbols = new ArrayList<>();
        symbols.addAll(Arrays.stream(headers).toList());
        symbols.addAll(grammar.getNonTerminals().stream().toList());
        symbols.addAll(grammar.getTerminals().stream().toList());
        int rowIndex = 0;

        boolean youWillBeHelped = false;
        String[][] data = new String[canonicalCollection.validEntries.size()][symbols.size()];
        for(Entry entry: canonicalCollection.validEntries){
            List<String> s = entry.currentS;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for(int i = 0; i < s.size()-1; i++){
                stringBuilder.append(s.get(i));
                stringBuilder.append(", ");
            }
            stringBuilder.append(s.get(s.size()-1));
            stringBuilder.append("]");
            data[rowIndex][0] = stringBuilder.toString();
            String actionString = checkAction(s);
            boolean isShiftReduceConflict = false;
            boolean isShift = false;
            boolean isReduce = false;
            if (actionString.contains(" ")) {
                System.out.println("LR(0) " + actionString + " on row " + rowIndex);
                if (actionString.equals("shift-reduce conflict")) {
                    isShiftReduceConflict = true;
                }
                youWillBeHelped = true;
            }
            else {
                if (actionString.equals("shift")) {
                    isShift = true;
                }
                else if (actionString.equals("reduce")) {
                    isReduce = true;
                }
            }
            data[rowIndex][1] = actionString;
            for(int j = 2; j < symbols.size(); j ++) {
                data[rowIndex][j] = " ";
                if (!isShiftReduceConflict) {
                    for (int k = 0; k < canonicalCollection.allEntries.size(); k++) {
                        Entry otherEntry = canonicalCollection.allEntries.get(k);
                        if (otherEntry.previousSIndex == rowIndex) {
                            if (otherEntry.X.compareTo(symbols.get(j)) == 0) {
                                int realParent = canonicalCollection.getRealParent(otherEntry.parentS);
                                if (realParent != -1) {
                                    k = realParent;
                                }
                                k = canonicalCollection.getRealIndex(k);
                                data[rowIndex][j] = "s" + k;
                                break;
                            }
                        }
                    }
                }
            }
            boolean hasItem = false;
            for (int i = 2; i < data[rowIndex].length; i++) {
                if (!data[rowIndex][i].equals(" ")) {
                    hasItem = true;
                }
            }
            if (isShift) {
                if (!hasItem) {
                    System.out.println("Line " + rowIndex + " is trolling with shift");
                }
            }
            else if (isReduce) {
                if (hasItem) {
                    System.out.println("Line " + rowIndex + " is trolling with reduce");
                }
            }
            data[rowIndex][0] += " (s" + rowIndex + ")";
            rowIndex += 1;
        }
        String parsingTable = FlipTable.of(symbols.toArray(new String[0]), data);
        fileHandler.writeToFile("parsingTable.txt", parsingTable);
        System.out.println(parsingTable);

        if (youWillBeHelped) {
            System.out.println("We will be helped to solve the conflicts :)");
        }
        else{
            System.out.println("We were helped :)");
        }
    }

    public String productionsToString(){
        return this.grammar.productionsToString();
    }

    public List<String> getProductionsForNonTerminal(String nonTerminal) throws Exception{
        return this.grammar.productionsForAGivenNonTerminal(nonTerminal);
    }

    public ParserOutput parseSequence(List<String> sequence) throws Exception {
        ParserOutput parserOutput = new ParserOutput();

//        List<Pair<String, String>> fakeNonTerminals = new ArrayList<>();
//        for (String production : grammar.getProductions()) {
//            List<String> split = Tokenizer.tokenize(production, "->");
//            if (split.size() > 1) {
//                if (Tokenizer.tokenize(split.get(1), " ").size() == 1) {
//                    if (!grammar.getNonTerminals().contains(split.get(1))) {
//                        fakeNonTerminals.add(new Pair<>(split.get(1), split.get(0)));
//                    }
//                }
//            }
//        }

//        List<String> sequenceRemapped = new ArrayList<>(sequence);
//        for (int index = 0; index < sequenceRemapped.size(); index++) {
//            String currentString = sequenceRemapped.get(index);
//            for (int subindex = 0; subindex < fakeNonTerminals.size(); subindex++) {
//                Pair<String, String> fakeEntry = fakeNonTerminals.get(subindex);
//                if (currentString.compareTo(fakeEntry.getFirst()) == 0) {
//                    sequenceRemapped.set(index, fakeEntry.getSecond());
//                    break;
//                }
//            }
//        }
//        sequence = sequenceRemapped;

        ParsingTable table = getParsingTable();

        List<Pair<String, String>> splitProductions = new ArrayList<>();
        List<String> epsilonProductions = new ArrayList<>();
        for (String production : grammar.getProductions()) {
            List<String> part = Tokenizer.tokenize(production, "->");
            if (part.size() == 1) {
                splitProductions.add(new Pair<>(part.get(0), ""));
                epsilonProductions.add(part.get(0));
            }
            else {
                splitProductions.add(new Pair<String, String>(part.get(0), part.get(1)));
            }
        }

        Stack<Pair<Integer, String>> workStack = new Stack<>();
        List<String> outputStack = new ArrayList<>();

        int lastSIndex = 0;

        int sequenceIndex = 0;
        while (!workStack.isEmpty() || sequenceIndex == 0) {
            ParsingTableRow row = table.rows.get(lastSIndex);
            if (row.state == State.ACCEPT) {
                int outputRowIndex = 0;
                int processingIndex = 0;
                Stack<Integer> unprocessedProductions = new Stack<>();

                String initialProduction = outputStack.get(outputStack.size() - 1);
                List<String> split = Tokenizer.tokenize(initialProduction, "->");
                String left = split.get(0);
                String right = split.get(1);
                List<String> rightSymbols = Tokenizer.tokenize(right, " ");

                ParserRow initialOutputRow = new ParserRow();
                initialOutputRow.index = outputRowIndex;
                initialOutputRow.parent = -1;
                initialOutputRow.rightSibling = -1;
                initialOutputRow.symbol = left;
                parserOutput.parserRows.add(initialOutputRow);
                outputRowIndex++;
                for (int j = 0; j < rightSymbols.size(); j++) {
                    ParserRow childRow = new ParserRow();
                    childRow.index = outputRowIndex;
                    childRow.symbol = rightSymbols.get(j);
                    childRow.parent = 0;
                    if (j < rightSymbols.size() - 1) {
                        childRow.rightSibling = outputRowIndex + 1;
                    }
                    else {
                        childRow.rightSibling = -1;
                    }
                    parserOutput.parserRows.add(childRow);
                    unprocessedProductions.add(childRow.index);
                    outputRowIndex++;
                }

                processingIndex++;

                while (!unprocessedProductions.isEmpty()) {
                    Integer element = unprocessedProductions.pop();
                    String symbol = parserOutput.parserRows.get(element).symbol;
                    if (grammar.getNonTerminals().contains(symbol)) {
                        String production = outputStack.get(outputStack.size() - 1 - processingIndex);
                        split = Arrays.stream(production.split("->")).toList();
                        left = split.get(0);
                        if (left.compareTo(symbol) == 0) {
                            right = split.get(1);
                            rightSymbols = Tokenizer.tokenize(right, " ");

                            for (int j = 0; j < rightSymbols.size(); j++) {
                                ParserRow childRow = new ParserRow();
                                childRow.index = outputRowIndex;
                                childRow.symbol = rightSymbols.get(j);
                                childRow.parent = element;
                                if (j < rightSymbols.size() - 1) {
                                    childRow.rightSibling = outputRowIndex + 1;
                                } else {
                                    childRow.rightSibling = -1;
                                }
                                parserOutput.parserRows.add(childRow);
                                unprocessedProductions.add(childRow.index);
                                outputRowIndex++;
                            }
                            processingIndex++;
                        }
                    }
                }
                return parserOutput;
            }
            else if (row.state == State.SHIFT) {
                String nonTerminal = sequence.get(sequenceIndex);
                Integer possibleLastSIndex = row.goto_map.get(nonTerminal);
                if (possibleLastSIndex != null) {
                    workStack.add(new Pair<>(lastSIndex, nonTerminal));
                    lastSIndex = possibleLastSIndex;
                    sequenceIndex++;
                }
                else {
                    possibleLastSIndex = row.goto_map.get("epsilon");
                    if (possibleLastSIndex != null) {
                        workStack.add(new Pair<>(lastSIndex, "epsilon"));
                        lastSIndex = possibleLastSIndex;
                    }
                    else {
                        throw new Exception("LR 0 fart while shifting");
                    }
                }
            }
            else if (row.state == State.REDUCE) {
                //List<String> splitProduction = Tokenizer.tokenize(row.reductionProduction, "->");
                List<String> splitProduction = Arrays.stream(row.reductionProduction.split("->")).toList();
                String rightSide = splitProduction.get(1);
                List<String> rsTokenized = Tokenizer.tokenize(rightSide, " ");
                if(rsTokenized.size() > workStack.size()){
                    throw new Exception("This LR is doodoo");
                }
                int i = rsTokenized.size();
                boolean iterationStop = false;
                String concatenatedToken = "";
                for (int j = 0; j < i; j++) {
                    Pair<Integer, String> entry = workStack.get(workStack.size() - i + j);
                    concatenatedToken = concatenatedToken.concat(entry.getSecond() + " ");
                }

                concatenatedToken = concatenatedToken.substring(0, concatenatedToken.length() - 1);

                if(concatenatedToken.compareTo(rightSide) != 0){
                    throw new Exception("This subject sucks");
                }

                outputStack.add(table.rows.get(lastSIndex).reductionProduction);

                for (int j = 0; j < i - 1; j++) {
                    workStack.pop();
                }
                Pair<Integer, String> reduceSEntry = workStack.pop();
                lastSIndex = reduceSEntry.getFirst();

                workStack.add(new Pair<>(lastSIndex, splitProduction.get(0)));
                ParsingTableRow reduceRow = table.rows.get(lastSIndex);
                Integer possibleLastSIndex = reduceRow.goto_map.get(splitProduction.get(0));
                if (possibleLastSIndex == null) {
                    possibleLastSIndex = reduceRow.goto_map.get("epsilon");
                }

                if (possibleLastSIndex != null) {
                    lastSIndex = possibleLastSIndex;
                } else {
                    throw new Exception("Yet another LR 0 fart");
                }
            }
            else {
                throw new Exception("LR 0 farts again man");
            }
        }

//        while (!workStack.isEmpty()) {
//            ParsingTableRow lastSIndexRow = table.rows.get(lastSIndex);
//            if (lastSIndexRow.state == State.ACCEPT) {
//
//            }
//
//            boolean iterationStop = false;
//            for (int i = 1; i <= workStack.size() && !iterationStop; i++) {
//                String concatenatedToken = "";
//                for (int j = 0; j < i; j++) {
//                    Pair<Integer, String> entry = workStack.get(workStack.size() - i + j);
//                    concatenatedToken = concatenatedToken.concat(entry.getSecond() + " ");
//                }
//                concatenatedToken = concatenatedToken.substring(0, concatenatedToken.length() - 1);
//                for (Pair<String, String> splitProduction : splitProductions) {
//                    if (concatenatedToken.compareTo(splitProduction.getSecond()) == 0) {
//                        outputStack.add(table.rows.get(lastSIndex).reductionProduction);
//
//                        for (int j = 0; j < i - 1; j++) {
//                            workStack.pop();
//                        }
//                        Pair<Integer, String> reduceSEntry = workStack.pop();
//                        lastSIndex = reduceSEntry.getFirst();
//
//                        workStack.add(new Pair<>(lastSIndex, splitProduction.getFirst()));
//                        ParsingTableRow reduceRow = table.rows.get(lastSIndex);
//                        Integer possibleLastSIndex = reduceRow.goto_map.get(splitProduction.getFirst());
//                        if (possibleLastSIndex == null) {
//                            for (String gotoString : reduceRow.goto_map.keySet()) {
//                                if (epsilonProductions.contains(gotoString)) {
//                                    possibleLastSIndex = reduceRow.goto_map.get(gotoString);
//                                    break;
//                                }
//                            }
//                        }
//                        lastSIndex = possibleLastSIndex;
//                        iterationStop = true;
//                        break;
//                    }
//                }
//            }
//        }
        throw new Exception("This fart is too big to be ignored");
    }
}
