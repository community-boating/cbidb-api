package Services;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public interface ConnectionPoolConstructor {
    public ComboPooledDataSource getMainDataSource();
    public ComboPooledDataSource getTempTableDataSource();
    public void closePools();
    //public String getSchemaName();
    //public String getTempTableSchemaName();
}
