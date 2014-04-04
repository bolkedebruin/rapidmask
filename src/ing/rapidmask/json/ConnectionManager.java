/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask.json;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author bolke
 */
public class ConnectionManager {
    private ComboPooledDataSource cpds;

    public ConnectionManager(String driverName, String url) throws PropertyVetoException {
        cpds = new ComboPooledDataSource();
        cpds.setDriverClass(driverName);
        cpds.setJdbcUrl(url);
        cpds.setMaxStatements(180);
    }
    
    public Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }
    
    public void close() {
        cpds.close();
    }
}
