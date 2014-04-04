/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask.opencsv;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import ing.rapidmask.transform.SHA256;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashSet;

/**
 *
 * @author bolke
 */
public class HashWriter extends CSVWriter {
    protected ResultSetHelper resultService;
    protected HashSet maskColumns;

    public HashWriter(Writer writer, char c, char c1, char c2, String string, HashSet maskColumns) throws ClassNotFoundException, SQLException, PropertyVetoException {
        super(writer, c, c1, c2, string);
        
        this.maskColumns = maskColumns;
        resultService = new ResultSetHelper(maskColumns);
    }
    
    public void writeAll(CSVColumnReader reader, boolean includeColumnNames) throws IOException, NoSuchAlgorithmException {
        writeAll(reader, reader.getColumnNames(), includeColumnNames);
    }
    
    public void writeAll(CSVReader reader, String[] columnNames, boolean includeColumnNames) throws IOException, NoSuchAlgorithmException {
        if (includeColumnNames) 
            writeNext(columnNames);
                
        String[] line;
        while ((line = reader.readNext()) != null) {
            // TODO: use hash set or to mask
            
            for (int i=0; i<line.length; i++) {
                if (maskColumns.contains(i) || maskColumns.contains(getColumnName(columnNames, i))) {
                    line[i] = SHA256.hash(line[i]);
                }
            }
            writeNext(line);
        }
    }
    
    private String getColumnName(String[] columnNames, int index) {
        if (columnNames == null) 
            return null;
        
        if (index > columnNames.length)
            return null;
        
        return columnNames[index];
    }
    
    @Override
    public void writeAll(java.sql.ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        if (includeColumnNames) {
            writeColumnNames(rs);
        }
        
        while (rs.next()) {
            writeNext(resultService.getColumnValues(rs));
        }
    }
}
