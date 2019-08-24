package org.sailcbi.APIServer.Services;

import org.sailcbi.APIServer.CbiUtil.PropertiesWrapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

public class OracleConnectionPoolConstructor implements ConnectionPoolConstructor {
	private PropertiesWrapper pw = null;
	private HikariDataSource mainDataSource = null;
	private HikariDataSource tempTableDataSource = null;

	public String getMainSchemaName() {
		return pw.getProperty("schema");
	}

	public String getTempTableSchemaName() {
		return pw.getProperty("temptableschema");
	}

	public String getMainUserName() {
		return pw.getProperty("username");
	}

	public HikariDataSource getMainDataSource() {
		if (null == mainDataSource) init();
		return mainDataSource;
	}

	public HikariDataSource getTempTableDataSource() {
		if (null == tempTableDataSource) init();
		return tempTableDataSource;
	}

	public void closePools() {
		mainDataSource.close();
		System.out.println("  ************    Shutting down!  Closing pool!!  *************  ");
		if (tempTableDataSource != mainDataSource) {
			tempTableDataSource.close();
			System.out.println(" *$*$*$*$*$*$*$*$*$*$    CLOSING TEMP TABLE CONNECTION POOL   *$*$*$*$*$*$*$*$*$*$");
		}
	}

	private void init() {
		String devLocation = "conf/private/oracle-credentials";
		String prodLocation = "../conf/private/oracle-credentials";
		File devFile = new File(devLocation);
		File prodFile = new File(prodLocation);
		String locationToUse = null;

		try {
			if (devFile.exists()) {
				locationToUse = devLocation;
			} else if (prodFile.exists()) {
				locationToUse = prodLocation;
			} else throw new Exception("Unable to location Oracle conf file");
			this.pw = new PropertiesWrapper(
					locationToUse,
					new String[]{"username", "password", "host", "port", "sid", "schema", "temptableschema"}
			);
			Class.forName("oracle.jdbc.driver.OracleDriver");
			this.mainDataSource = getPool(getMainDataSourceConfig());
			this.tempTableDataSource = getPool(getTempTableDataSourceConfig());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/*
    private String mainDataSourceConnectionString() {
        return "jdbc:oracle:thin:" + pw.getProperty("username") + "/" +
                pw.getProperty("password") + "@" +
                pw.getProperty("host") + ":" +
                pw.getProperty("port") + ":" +
                pw.getProperty("sid");
    }

    private String tempTableDataSourceConnectionString() {
        if (this.mainSchemaName.equals(this.tempTableSchemaName)) {
            return mainDataSourceConnectionString();
        } else {
            return "jdbc:oracle:thin:" + pw.getProperty("temptableusername") + "/" +
                    pw.getProperty("temptablepassword") + "@" +
                    pw.getProperty("host") + ":" +
                    pw.getProperty("port") + ":" +
                    pw.getProperty("sid");
        }
    }
*/

	private HikariConfig getMainDataSourceConfig() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:oracle:thin:@" + pw.getProperty("host") + ":" + pw.getProperty("port") + ":" + pw.getProperty("sid"));
		config.setUsername(pw.getProperty("username"));
		config.setPassword(pw.getProperty("password"));

		return config;
	}

	private HikariConfig getTempTableDataSourceConfig() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:oracle:thin:@" + pw.getProperty("host") + ":" + pw.getProperty("port") + ":" + pw.getProperty("sid"));
		config.setUsername(pw.getProperty("temptableusername"));
		config.setPassword(pw.getProperty("temptablepassword"));
		return config;
	}

	private HikariDataSource getPool(HikariConfig config) {
		config.setMaximumPoolSize(10);
		config.setLeakDetectionThreshold(30 * 1000);
		//hikari.setIdleTimeout(30000);
		return new HikariDataSource(config);
	}
}
