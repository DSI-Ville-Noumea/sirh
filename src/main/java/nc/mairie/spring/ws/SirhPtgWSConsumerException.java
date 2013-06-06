package nc.mairie.spring.ws;

public class SirhPtgWSConsumerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6378424967920764491L;

	public SirhPtgWSConsumerException() {
		super();
	}

	public SirhPtgWSConsumerException(String message) {
		super(message);
	}

	public SirhPtgWSConsumerException(String message, Exception innerException) {
		super(message, innerException);
	}
}
