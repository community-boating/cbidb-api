package Services;

import CbiUtil.PropertiesWrapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class OracleConnectionPoolConstructor implements ConnectionPoolConstructor {
    private PropertiesWrapper pw = null;
    private String mainSchemaName = null;
    private String tempTableSchemaName = null;
    private ComboPooledDataSource mainDataSource = null;
    private ComboPooledDataSource tempTableDataSource = null;

    public String getMainSchemaName() {
        return pw.getProperty("schema");
    }

    public String getTempTableSchemaName() {
        return pw.getProperty("temptableschema");
    }

    public String getMainUserName() {
        return pw.getProperty("username");
    }

    public ComboPooledDataSource getMainDataSource() {
        if (null == mainDataSource) init();
        return mainDataSource;
    }

    public ComboPooledDataSource getTempTableDataSource() {
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
        try {
            this.pw = new PropertiesWrapper(
                    "conf/private/oracle-credentials",
                    new String[] {"username", "password", "host", "port", "sid", "schema", "temptableschema"}
            );
            this.mainSchemaName = pw.getProperty("schema");
            this.tempTableSchemaName = pw.getProperty("temptableschema");
            this.mainDataSource = getPool(this.mainDataSourceConnectionString());
            if (this.mainDataSourceConnectionString().equals(this.tempTableDataSourceConnectionString())) {
                this.tempTableDataSource = this.mainDataSource;
            } else {
                this.tempTableDataSource = getPool(this.tempTableDataSourceConnectionString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private ComboPooledDataSource getPool(String connectionString) {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass( "oracle.jdbc.driver.OracleDriver" ); //loads the jdbc driver
            cpds.setJdbcUrl(connectionString);

            // the settings below are optional -- c3p0 can work with defaults
            cpds.setMinPoolSize(5);
            cpds.setAcquireIncrement(5);
            cpds.setMaxPoolSize(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpds;
    }
}
