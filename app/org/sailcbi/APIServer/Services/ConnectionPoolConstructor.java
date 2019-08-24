package org.sailcbi.APIServer.Services;


import com.zaxxer.hikari.HikariDataSource;

public interface ConnectionPoolConstructor {
	public HikariDataSource getMainDataSource();

	public HikariDataSource getTempTableDataSource();

	public void closePools();

	public String getMainSchemaName();

	public String getTempTableSchemaName();

	public String getMainUserName();
}
