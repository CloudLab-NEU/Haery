package Exception;

public class NotPositiveException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3223604861869706611L;

	public NotPositiveException() {
		super();
	}

	/*public NotPositiveException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}*/

	public NotPositiveException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotPositiveException(String message) {
		super(message);
	}

	public NotPositiveException(Throwable cause) {
		super(cause);
	}

}
