/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask.json;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author bolke
 */
public class Config {
    private String driverName;
    private String connectionString;
    
    public Config(String path) {
        this.driverName = "com.mysql.jdbc.Driver";
        this.connectionString = "jdbc:mysql://localhost/adnr2013?user=root&password=nk8udv";
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
    
    
}
