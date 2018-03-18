package Services.Shell;

import java.io.IOException;
import java.io.OutputStream;

import Services.Shell.Exception.ProcessTimeoutException;

public class ProcessWrapper {
	private Process p;
	
	// Execute a shell command and manage the resulting process
	public ProcessWrapper(String command, Integer maxWaitMillis, String stdintext) throws ProcessTimeoutException {
	//	StringBuffer output = new StringBuffer();
		try {
			this.p = Runtime.getRuntime().exec(command);
			if (stdintext != null) {
				try {
					OutputStream stdin = this.p.getOutputStream();
					stdin.write(stdintext.getBytes());
					stdin.write(10); // newline
					stdin.write(3);  // EOT, must be on its own line
					stdin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (maxWaitMillis == null) {
				// wait forever
				p.waitFor();
			} else {
				// poll every 1ms to see if its done yet
				long start = System.currentTimeMillis();
				long end = start + maxWaitMillis;
				while (System.currentTimeMillis() < end && this.isRunning()) {
					Thread.sleep(1);
				}
				if (this.isRunning()) {
					// exceeded our timeout and its still running; kill it
					this.destroy();
					throw new ProcessTimeoutException(command, maxWaitMillis);
				}
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isRunning() {
		// @SuppressIncrediblyGhettoWarning
		try {
			p.exitValue();
		} catch (Exception e) {
			return true;
		}
		return false;
	}
	
	private void destroy() {
		p.destroy();
	}
	
	public int getExitValue() {
		return p.exitValue();
	}
	
	public Process unwrap() { return p; }
}
