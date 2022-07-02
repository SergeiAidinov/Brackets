public class InvalidString extends Throwable{

    private final String message;


    public InvalidString(String message) {
        this.message = message;
    }

    public InvalidString(){
        message = "Invalid string";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
