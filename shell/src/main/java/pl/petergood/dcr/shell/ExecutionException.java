package pl.petergood.dcr.shell;

public class ExecutionException extends RuntimeException {

    private Exception wrappedException;

    public ExecutionException(Exception wrappedException) {
        this.wrappedException = wrappedException;
    }

    public Exception getWrappedException() {
        return wrappedException;
    }

    @Override
    public String getMessage() {
        return wrappedException.getMessage();
    }
}
