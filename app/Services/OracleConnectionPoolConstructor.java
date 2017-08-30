package Services;

import CbiUtil.PropertiesWrapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class OracleConnectionPoolConstructor implements ConnectionPoolConstructor {
    public ComboPooledDataSource getPool() {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            PropertiesWrapper props = new PropertiesWrapper(
                    "conf/private/oracle-credentials",
                    new String[] {"username", "password", "host", "port", "sid"}
            );
            String connectionString = "jdbc:oracle:thin:" + props.getProperty("username") + "/" +
                    props.getProperty("password") + "@" +
                    props.getProperty("host") + ":" +
                    props.getProperty("port") + ":" +
                    props.getProperty("sid");


            cpds.setDriverClass( "oracle.jdbc.driver.OracleDriver" ); //loads the jdbc driver
            cpds.setJdbcUrl(connectionString );


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
