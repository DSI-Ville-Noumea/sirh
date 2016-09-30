package nc.mairie.spring.ws;

public class SirhEaeWSConsumerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5868967722847643278L;

	public SirhEaeWSConsumerException() {
		super();
	}

	public SirhEaeWSConsumerException(String message) {
		super(message);
	}

	public SirhEaeWSConsumerException(String message, Exception innerException) {
		super(message, innerException);
	}

}
