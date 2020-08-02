package cloud.chenh.bolt.exception;

public class BannedException extends Exception{

    public BannedException() {
    }

    public BannedException(String message) {
        super(message);
    }

    public BannedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BannedException(Throwable cause) {
        super(cause);
    }

    public BannedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
