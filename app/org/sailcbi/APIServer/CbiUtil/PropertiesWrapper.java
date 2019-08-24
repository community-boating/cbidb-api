package org.sailcbi.APIServer.CbiUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesWrapper extends Properties {

	private static final long serialVersionUID = 1;

	public PropertiesWrapper(String fileLocation) throws IOException {
		super();
		File f = new File(fileLocation);
		if (!f.exists()) {
			throw new IOException("Unable to location Oracle conf file");
		}
		FileInputStream fileInput;
		fileInput = new FileInputStream(f);
		super.load(fileInput);
		fileInput.close();
	}

	public PropertiesWrapper(String fileLocation, String[] requiredProperties) throws IOException {
		this(fileLocation);
		for (String s : requiredProperties) {
			if (!this.containsKey(s)) {
				throw new IOException("Property file missing required property " + s);
			}
		}
	}

}