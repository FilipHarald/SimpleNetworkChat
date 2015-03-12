package exceptions;

public class NameInUseException extends RuntimeException {

	public NameInUseException(String message) {
		super(message);
	}

	public NameInUseException() {
		super();
	}
}