package nc.mairie.spring.ws;

public class SirhKiosqueWSConsumerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6378424967920764491L;

	public SirhKiosqueWSConsumerException() {
		super();
	}

	public SirhKiosqueWSConsumerException(String message) {
		super(message);
	}

	public SirhKiosqueWSConsumerException(String message, Exception innerException) {
		super(message, innerException);
	}
}
