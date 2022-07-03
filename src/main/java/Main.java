public class Main {

    private static String DEFAULT_STRING = "Техника те({хник})ой, но лифт ([ломается]) чаще, {чем} лестница";

    public static void main(String[] args) {

        Validator bracketsValidator = new BracketsValidator();
        String string = args.length == 0 ? DEFAULT_STRING : args[0];
        System.out.println("String " + string + " is valid: " + bracketsValidator.isValid(string));

    }
}
