import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BracketsValidator implements Validator {

    private final static Character SIMPLE_OPENING_BRACKET = '(';
    private final static Character SIMPLE_CLOSING_BRACKET = ')';
    private final static Character SQUARE_OPENING_BRACKET = '[';
    private final static Character SQUARE_CLOSING_BRACKET = ']';
    private final static Character CURLY_OPENING_BRACKET = '{';
    private final static Character CURLY_CLOSING_BRACKET = '}';
    private final static String SIMPLE = "SIMPLE";
    private final static String SQARE = "SQUARE";
    private final static String CURLY = "CURLY";

    private static final Map<Character, Character> possibleBrackets = Map.of(
            SIMPLE_OPENING_BRACKET, SIMPLE_CLOSING_BRACKET,
            SQUARE_OPENING_BRACKET, SQUARE_CLOSING_BRACKET,
            CURLY_OPENING_BRACKET, CURLY_CLOSING_BRACKET
    );

    private final List<Bracket> simpleOpeningBrackets = new ArrayList<>();
    private final List<Bracket> squareOpeningBrackets = new ArrayList<>();
    private final List<Bracket> curlyOpeningBrackets = new ArrayList<>();
    private final List<Pair<Bracket, Bracket>> pairsOfBrackets = new ArrayList<>();

    Map<String, Pair<List<Integer>, List<Integer>>> map =
            Map.of(
                    SIMPLE, Pair.of(new ArrayList<>(), new ArrayList<>()),
                    SQARE, Pair.of(new ArrayList<>(), new ArrayList<>()),
                    CURLY, Pair.of(new ArrayList<>(), new ArrayList<>())
            );

    private final List<Character> openingExamples =
            possibleBrackets.keySet().stream().collect(Collectors.toList());

    private final List<Character> closingExamples = openingExamples.stream()
            .map(bracket -> possibleBrackets.get(bracket)).collect(Collectors.toList());

    private static List<String> subStringList = new ArrayList<>();
    private static List<String> atomicSubStringList = new ArrayList<>();

    Logger logger = Logger.getLogger("BracketsValidator");

    @Override
    public boolean isValid(String string) {
        try {
            procedure(string);
            while (!subStringList.isEmpty()) {
                String subStringToBeDevided = subStringList.remove(0);
                procedure(subStringToBeDevided);
            }
        } catch (InvalidString invalidString) {
            logger.log(Level.INFO, invalidString.getMessage());
            return false;
        }

        Set<Boolean> result = atomicSubStringList.stream().map(atomicSubString -> {
            try {
                return analyzeAtomicSubString(atomicSubString);
            } catch (InvalidString invalidString) {
                invalidString.printStackTrace();
                return false;
            }
        }).collect(Collectors.toSet());

        StringBuilder message = new StringBuilder("Строка " + string + " состоит из следующих подстрок: " + "\n");
        atomicSubStringList.stream().forEach(atomicSubString -> {
            message.append(atomicSubString);
            message.append("\n");
        });

        logger.info(message.toString());

        return !result.contains(false);
    }

    private void procedure(String string) throws InvalidString {

        subStringList = parseString(string);

        List<String> undevidableSubStrings = new ArrayList<>();
        String initialSubString = string;
        for (String subString : subStringList) {
            if (!Helper.mayBeDevidedIntoSmallerSubStrings(subString)) {
                undevidableSubStrings.add(subString);
            }
        }

        atomicSubStringList.addAll(undevidableSubStrings);
        subStringList.removeAll(undevidableSubStrings);
        subStringList.remove(initialSubString);
        System.out.println();
    }


    private List<String> parseString(String string) throws InvalidString {

        populateListsOfBrackets(string);

        if (!checkRedundantOpeningBrackets()) {
            throw new InvalidString("Redundant opening brackets");
        }

        return Helper.devideIntoSubstrings(string, pairsOfBrackets);

    }


    private boolean checkRedundantOpeningBrackets() throws InvalidString {

        if (simpleOpeningBrackets.size() == 0 &&
                squareOpeningBrackets.size() == 0 &&
                curlyOpeningBrackets.size() == 0) {
            return true;
        } else {
            List<Bracket> redundantBrackets = new ArrayList();
            redundantBrackets.addAll(simpleOpeningBrackets);
            redundantBrackets.addAll(squareOpeningBrackets);
            redundantBrackets.addAll(curlyOpeningBrackets);
            StringBuilder message = new StringBuilder("Redundant opening bracket(s) at next positions: ");
            redundantBrackets.stream().forEach(bracket -> {
                message.append(" ");
                message.append(bracket.getPosition());
            });
            throw new InvalidString(message.toString());
        }

    }

    private void populateListsOfBrackets(String string) throws InvalidString {
        simpleOpeningBrackets.clear();
        squareOpeningBrackets.clear();
        curlyOpeningBrackets.clear();
        pairsOfBrackets.clear();

        for (int i = 0; i < string.length(); i++) {
            char letter = string.charAt(i);

            if (!isBracket(letter)) {
                continue;
            }

            if (letter == SIMPLE_OPENING_BRACKET) {
                simpleOpeningBrackets.add(new Bracket(Bracket.TypeOfBracket.SIMPLE, true, i));
            } else if (letter == SIMPLE_CLOSING_BRACKET) {
                compilePairAndAddToList(new Bracket(Bracket.TypeOfBracket.SIMPLE, false, i));
            } else if (letter == SQUARE_OPENING_BRACKET) {
                squareOpeningBrackets.add(new Bracket(Bracket.TypeOfBracket.SQAURE, true, i));
            } else if (letter == SQUARE_CLOSING_BRACKET) {
                compilePairAndAddToList(new Bracket(Bracket.TypeOfBracket.SQAURE, false, i));
            } else if (letter == CURLY_OPENING_BRACKET) {
                curlyOpeningBrackets.add(new Bracket(Bracket.TypeOfBracket.CURLY, true, i));
            } else if (letter == CURLY_CLOSING_BRACKET) {
                compilePairAndAddToList(new Bracket(Bracket.TypeOfBracket.CURLY, false, i));
            }
        }
    }

    private boolean isBracket(char letter) {

        return possibleBrackets.containsKey(letter) || possibleBrackets.containsValue(letter);
    }

    private void compilePairAndAddToList(Bracket closingBracket) throws InvalidString {

        if (closingBracket.isOpening()) {
            logger.log(Level.SEVERE, "В метод ошибочно передана открывающаяся скобка");
            return;
        }

        List<Bracket> currentListOfOpeningBrackets = getLinkToBracketsList(closingBracket);
        if (currentListOfOpeningBrackets.isEmpty()) {
            throw new InvalidString("Redundant closing bracket at position " + closingBracket.getPosition());
        }
        Bracket openingBracket = currentListOfOpeningBrackets
                .remove(currentListOfOpeningBrackets.size() - 1);
        pairsOfBrackets.add(Pair.of(openingBracket, closingBracket));

    }

    private List<Bracket> getLinkToBracketsList(Bracket closingBracket) {
        switch (closingBracket.getTypeOfBracket()) {
            case SIMPLE:
                return simpleOpeningBrackets;
            case CURLY:
                return curlyOpeningBrackets;
            case SQAURE:
                return squareOpeningBrackets;
            default:
                return Collections.EMPTY_LIST;
        }

    }


    boolean analyzeAtomicSubString(String subString) throws InvalidString {
        return possibleBrackets.containsKey(subString.charAt(0)) &&
                subString.charAt(subString.length() - 1) == possibleBrackets.get(subString.charAt(0));
    }


    private boolean compareLists(List<Character> openingBrackets, List<Character> reversedClosingBrackets) {

        if (openingBrackets.size() != reversedClosingBrackets.size()) {
            return false;
        }

        for (int i = 0; i < openingBrackets.size(); i++) {

            if (!reversedClosingBrackets.get(i).equals(possibleBrackets.get(openingBrackets.get(i)))) {
                return false;
            }
        }

        return true;
    }

    private static class Helper {

        public static List<String> devideIntoSubstrings(String string, List<Pair<Bracket, Bracket>> pairsOfBrackets) {

            List<java.lang.String> subStrings = new ArrayList<>();
            java.lang.String finalString = string;
            pairsOfBrackets.stream().forEach(pair -> {
                subStrings.add(finalString.substring(pair.getLeft().getPosition(), pair.getRight().getPosition() + 1));
            });

            return subStrings;
        }

        static boolean mayBeDevidedIntoSmallerSubStrings(String subString) {
            long quantityOfBrackets =
                    subString.chars().filter(c -> possibleBrackets.containsKey((char) c)
                            || possibleBrackets.containsValue((char) c))
                            .count();

            if (quantityOfBrackets > 2 && quantityOfBrackets % 2 == 0) {
                return true;
            }

            return false;
        }
    }
}
