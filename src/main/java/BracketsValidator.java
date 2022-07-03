import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

    private final List<Character> openingBrackets = new ArrayList<>();
    Logger logger = Logger.getLogger("BracketsValidator");

    @Override
    public boolean isValid(String initialString) {
        logger.info("Проверяем строку: " + initialString);

        try {
            performValidation(initialString);
        } catch (InvalidString invalidString) {
            logger.info(invalidString.getMessage());
            return false;
        }

        if (openingBrackets.isEmpty()) {
            return true;
        } else {
           logger.info("Строка содержит незакрытые скобки");
            return false;
        }

    }

    private void performValidation(String string) throws InvalidString {
        openingBrackets.clear();

        for (int i = 0; i < string.length(); i++) {
            char letter = string.charAt(i);

            if (possibleBrackets.containsKey(letter)) {
                openingBrackets.add(letter);
            } else if (possibleBrackets.containsValue(letter)) {
                checkClosingLetter(letter);
            }
        }
    }

    private void checkClosingLetter(Character closingBracket) throws InvalidString {
        if (openingBrackets.isEmpty()) {
            throw new InvalidString("Строка содержит избыточную закрывающую скобку");
        }

        Character lastOpeningBracket = openingBrackets.remove(openingBrackets.size() - 1);

        if (possibleBrackets.get(lastOpeningBracket) != closingBracket) {
            throw new InvalidString("Скобки неупорядочены");
        }

    }

}
