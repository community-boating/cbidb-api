package Services.Shell.Exception;

public class ProcessTimeoutException extends Exception {
	private static final long serialVersionUID = 1;
	
	public ProcessTimeoutException(String command, Integer maxWaitMillis) {
		super("The following command timed out after " + maxWaitMillis + " milliseconds: \n" + command);
	}
}
