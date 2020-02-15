package cloud.chenh.bolt.exception;

public class DownloadStopException extends Exception {

    public DownloadStopException() {
    }

    public DownloadStopException(String message) {
        super(message);
    }

    public DownloadStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadStopException(Throwable cause) {
        super(cause);
    }

    public DownloadStopException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
