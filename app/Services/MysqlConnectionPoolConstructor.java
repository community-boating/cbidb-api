package Services;

import CbiUtil.PropertiesWrapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MysqlConnectionPoolConstructor implements ConnectionPoolConstructor {
    private ComboPooledDataSource pool = null;

    public ComboPooledDataSource getMainDataSource() {
        if (null == this.pool) init();
        return this.pool;
    }

    public ComboPooledDataSource getTempTableDataSource() {
        return getMainDataSource();
    }

    public void closePools() {
        pool.close();
        System.out.println("  ************    Shutting down!  Closing mysql pool!!  *************  ");
    }

    private ComboPooledDataSource init() {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            //mysql -u root -p -e "GRANT ALL PRIVILEGES ON $schema.* TO $user@localhost IDENTIFIED BY '$db_pass'"
            PropertiesWrapper props = new PropertiesWrapper(
                    "conf/private/mysql-credentials",
                    new String[] {"username", "password", "host", "port", "schema"}
            );

            String connectionString = "jdbc:mysql://" + props.getProperty("host") + ":" + props.getProperty("port")
                    + "/" + props.getProperty("schema")
                    + "?user=" + props.getProperty("username")
                    + "&password=" + props.getProperty("password")
                    + "&useSSL=false";


            cpds.setDriverClass( "com.mysql.jdbc.Driver" ); //loads the jdbc driver
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
