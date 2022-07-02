import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class Main {

    private static String DEFAULT_STRING = "as(hjkhkjh[fsdf{rt5647}rbtreybert]wrtv)";

    public static void main(String[] args) {

        Validator bracketsValidator = new BracketsValidator();
        String string = args.length == 0 ? DEFAULT_STRING : args[0];
        System.out.println("String " + string + " is valid: " + bracketsValidator.isValid(string));

    }
}
