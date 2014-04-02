package nc.mairie.spring.ws;

public class RadiWSConsumerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6378424967920764491L;

	public RadiWSConsumerException() {
		super();
	}

	public RadiWSConsumerException(String message) {
		super(message);
	}

	public RadiWSConsumerException(String message, Exception innerException) {
		super(message, innerException);
	}
}
