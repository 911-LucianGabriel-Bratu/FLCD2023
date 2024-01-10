package utils;

import java.util.*;
import java.util.stream.Collectors;

public class Tokenizer {
    public static List<String> tokenize(String str, String delimiter){
        return Collections.list(new StringTokenizer(str, delimiter)).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    public static List<String> tokenizeList(List<String> strings, String delimiter){
        List<String> returnList = new ArrayList<>();
        for(String str: strings){
            returnList.addAll(tokenize(str, delimiter));
        }
        return returnList;
    }
}
