package exceptions;

/**
 * Exception used when client tries to connect with an already in use username
 * @author Albert
 *
 */
public class NameInUseException extends RuntimeException {

	public NameInUseException(String message) {
		super(message);
	}

	public NameInUseException() {
		super();
	}
}