package life.banana4.util.resourcebags;

public class BrokenResourceBagException extends RuntimeException {
    public BrokenResourceBagException(Throwable throwable) {
        super(throwable);
    }

    public BrokenResourceBagException(String s) {
        super(s);
    }
}
