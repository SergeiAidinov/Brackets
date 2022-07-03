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

    private static final Map<Character, Character> possibleBrackets = Map.of(
            SIMPLE_OPENING_BRACKET, SIMPLE_CLOSING_BRACKET,
            SQUARE_OPENING_BRACKET, SQUARE_CLOSING_BRACKET,
            CURLY_OPENING_BRACKET, CURLY_CLOSING_BRACKET
    );

    private final List<Bracket> openingBrackets = new ArrayList<>();
    private final List<Pair<Bracket, Bracket>> pairsOfBrackets = new ArrayList<>();
    private static List<String> subStringList = new ArrayList<>();
    private static final Set<String> atomicSubStringList = new HashSet<>();

    Logger logger = Logger.getLogger("BracketsValidator");

    @Override
    public boolean isValid(String initialString) {
        logger.info("Checking the string: " + initialString);
        try {
            procedure(initialString);
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

        StringBuilder message = new StringBuilder("String \"" + initialString + "\" has got next substrings: " + "\n");
        atomicSubStringList.stream().forEach(atomicSubString -> {
            message.append(atomicSubString);
            message.append("\n");
        });

        atomicSubStringList.clear();
        subStringList.clear();
        logger.info(message.toString());

        return !result.contains(false);
    }

    private void procedure(String string) throws InvalidString {

        subStringList = parseString(string);

        List<String> undevidableSubStrings = new ArrayList<>();
        for (String subString : subStringList) {
            if (!Helper.mayBeDividedIntoSmallerSubStrings(subString)) {
                undevidableSubStrings.add(subString);
            }
        }

        atomicSubStringList.addAll(undevidableSubStrings);
        subStringList.removeAll(undevidableSubStrings);
        subStringList.remove(string);
    }

    private List<String> parseString(String string) throws InvalidString {

        populateListsOfBrackets(string);

        if (!checkRedundantOpeningBrackets()) {
            throw new InvalidString("Redundant opening brackets");
        }

        return Helper.devideIntoSubstrings(string, pairsOfBrackets);

    }

    private boolean checkRedundantOpeningBrackets() throws InvalidString {

        if (openingBrackets.size() == 0) {
            return true;
        } else {
            List<Bracket> redundantBrackets = new ArrayList(openingBrackets);
            StringBuilder message = new StringBuilder("Redundant opening bracket(s) at next positions: ");
            redundantBrackets.stream().forEach(bracket -> {
                message.append(" ");
                message.append(bracket.getPosition());
            });
            throw new InvalidString(message.toString());
        }

    }

    private void populateListsOfBrackets(String string) throws InvalidString {
        openingBrackets.clear();
        pairsOfBrackets.clear();

        for (int i = 0; i < string.length(); i++) {
            char letter = string.charAt(i);

            if (!isBracket(letter)) {
                continue;
            }

            if (letter == SIMPLE_OPENING_BRACKET) {
                openingBrackets.add(new Bracket(Bracket.TypeOfBracket.SIMPLE, true, i));
            }
            if (letter == SIMPLE_CLOSING_BRACKET) {
                compilePairAndAddToList(new Bracket(Bracket.TypeOfBracket.SIMPLE, false, i));
            }
            if (letter == SQUARE_OPENING_BRACKET) {
                openingBrackets.add(new Bracket(Bracket.TypeOfBracket.SQAURE, true, i));
            }
            if (letter == SQUARE_CLOSING_BRACKET) {
                compilePairAndAddToList(new Bracket(Bracket.TypeOfBracket.SQAURE, false, i));
            }
            if (letter == CURLY_OPENING_BRACKET) {
                openingBrackets.add(new Bracket(Bracket.TypeOfBracket.CURLY, true, i));
            }
            if (letter == CURLY_CLOSING_BRACKET) {
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

        if (!doCompileAPair(openingBracket, closingBracket)){
            throw new InvalidString("Disordered brackets near position " + closingBracket.getPosition());
        }

        pairsOfBrackets.add(Pair.of(openingBracket, closingBracket));

    }

    private boolean doCompileAPair(Bracket openingBracket, Bracket closingBracket) {

        return openingBracket.getTypeOfBracket().equals(closingBracket.getTypeOfBracket());

    }

    private List<Bracket> getLinkToBracketsList(Bracket closingBracket) {
        switch (closingBracket.getTypeOfBracket()) {
            case SIMPLE:
                return openingBrackets;
            case CURLY:
                return openingBrackets;
            case SQAURE:
                return openingBrackets;
            default:
                return Collections.EMPTY_LIST;
        }

    }

    boolean analyzeAtomicSubString(String subString) throws InvalidString {
        return possibleBrackets.containsKey(subString.charAt(0)) &&
                subString.charAt(subString.length() - 1) == possibleBrackets.get(subString.charAt(0));
    }

    private static class Helper {

        public static List<String> devideIntoSubstrings(String string, List<Pair<Bracket, Bracket>> pairsOfBrackets) {

            List<java.lang.String> subStrings = new ArrayList<>();
            for (Pair<Bracket, Bracket> pair : pairsOfBrackets) {
                subStrings.add(string.substring(pair.getLeft().getPosition(), pair.getRight().getPosition() + 1));
            }

            return subStrings;
        }

        static boolean mayBeDividedIntoSmallerSubStrings(String subString) {
            long quantityOfBrackets =
                    subString.chars().filter(c -> possibleBrackets.containsKey((char) c)
                            || possibleBrackets.containsValue((char) c))
                            .count();

            return quantityOfBrackets > 2 && quantityOfBrackets % 2 == 0;
        }
    }
}
