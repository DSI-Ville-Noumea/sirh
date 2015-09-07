package nc.mairie.spring.ws;

public class BaseWsConsumerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6378424967920764491L;

	public BaseWsConsumerException() {
		super();
	}

	public BaseWsConsumerException(String message) {
		super(message);
	}

	public BaseWsConsumerException(String message, Exception innerException) {
		super(message, innerException);
	}
}
