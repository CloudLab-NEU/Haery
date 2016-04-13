package Exception;

public class NotSupportedNumberOfDimensionsException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4571688432927981110L;

	public NotSupportedNumberOfDimensionsException() {
		super();
	}

	/*public NotSupportedNumberOfDimensionsException(String message,
			Throwable throwable, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}*/

	public NotSupportedNumberOfDimensionsException(String message,
			Throwable throwable) {
		super(message, throwable);
	}

	public NotSupportedNumberOfDimensionsException(String message) {
		super(message);
	}

	public NotSupportedNumberOfDimensionsException(Throwable throwable) {
		super(throwable);
	}

}
