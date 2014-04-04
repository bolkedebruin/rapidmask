/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask.opencsv;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author bolke
 */
public class CSVColumnReader extends CSVReader {
    protected String[] columnNames;
    
    public CSVColumnReader(Reader reader, char c, char c1, boolean firstRowHasColumnNames) throws IOException {
        super(reader, c, c1);
        
        if (firstRowHasColumnNames) {
            columnNames = readNext();
        }
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
}
