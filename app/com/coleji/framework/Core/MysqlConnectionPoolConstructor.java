package com.coleji.framework.Core;

import com.coleji.framework.Util.PropertiesWrapper;
import com.zaxxer.hikari.HikariDataSource;

public class MysqlConnectionPoolConstructor implements ConnectionPoolConstructor {
	private PropertiesWrapper pw = null;
	private HikariDataSource pool = null;

	public String getMainSchemaName() {
		return pw.getProperty("schema");
	}

	public String getTempTableSchemaName() {
		return pw.getProperty("schema");
	}

	public String getMainUserName() {
		return pw.getProperty("username");
	}

	public HikariDataSource getMainDataSource() {
		if (null == this.pool) init();
		return this.pool;
	}

	public HikariDataSource getTempTableDataSource() {
		return getMainDataSource();
	}

	public void closePools() {
		pool.close();
		System.out.println("  ************    Shutting down!  Closing mysql pool!!  *************  ");
	}

	private HikariDataSource init() {
		HikariDataSource ds = new HikariDataSource();
        /*try {
            //mysql -u root -p -e "GRANT ALL PRIVILEGES ON $schema.* TO $user@localhost IDENTIFIED BY '$db_pass'"
            this.pw = new PropertiesWrapper(
                    "conf/private/mysql-credentials",
                    new String[] {"username", "password", "host", "port", "schema"}
            );

            String connectionString = "jdbc:mysql://" + this.pw.getProperty("host") + ":" + this.pw.getProperty("port")
                    + "/" + this.pw.getProperty("schema")
                    + "?user=" + this.pw.getProperty("username")
                    + "&password=" + this.pw.getProperty("password")
                    + "&useSSL=false";


            cpds.setDriverClass( "com.mysql.jdbc.Driver" ); //loads the jdbc driver
            cpds.setJdbcUrl(connectionString );


            // the settings below are optional -- c3p0 can work with defaults
            cpds.setMinPoolSize(5);
            cpds.setAcquireIncrement(5);
            cpds.setMaxPoolSize(20);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
		return ds;
	}
}
