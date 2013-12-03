package nc.mairie.spring.ws;

public class SirhAbsWSConsumerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6378424967920764491L;

	public SirhAbsWSConsumerException() {
		super();
	}

	public SirhAbsWSConsumerException(String message) {
		super(message);
	}

	public SirhAbsWSConsumerException(String message, Exception innerException) {
		super(message, innerException);
	}
}
