package Services.Shell;

public class ShellManager {
	private static ShellManager self;
	
	private ShellManager() {
		// Singleton class: cannot be instantiated except by getInstance()
	}
	
	public static ShellManager getInstance() {
		if (self == null) {
			return new ShellManager();
		} else {
			return self;
		}
	}
	
	public CommandWrapper execute(String command) {
		return new CommandWrapper(command, null, null, null, null);
	}
	
	public CommandWrapper execute(String command, String cleanupCommand, Integer mainMaxWaitMillis, Integer cleanupMaxWaitMillis, String stdintext) {
		return new CommandWrapper(command, cleanupCommand, mainMaxWaitMillis, cleanupMaxWaitMillis, stdintext);
	}
}
