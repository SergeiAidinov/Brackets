public class Bracket {

    public enum TypeOfBracket {SIMPLE, CURLY, SQAURE};
    private final TypeOfBracket typeOfBracket;
    private final boolean isOpening;
    private final int position;

    public Bracket(TypeOfBracket typeOfBracket, boolean isOpening, int position) {
        this.typeOfBracket = typeOfBracket;
        this.isOpening = isOpening;
        this.position = position;
    }

    public TypeOfBracket getTypeOfBracket() {
        return typeOfBracket;
    }

    public boolean isOpening() {
        return isOpening;
    }

    public int getPosition() {
        return position;
    }
}
