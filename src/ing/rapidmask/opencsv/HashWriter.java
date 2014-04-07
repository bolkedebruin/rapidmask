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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bolke
 */
public class HashWriter extends CSVWriter {

    protected ResultSetHelper resultService;
    protected HashSet maskColumns;

    protected Writer writer;
    
    protected final char separator;
    protected final char quotechar;
    protected final String lineEnd;
    protected final char escapechar;
 

    public HashWriter(Writer writer, char c, char c1, char c2, String string, HashSet maskColumns) throws ClassNotFoundException, SQLException, PropertyVetoException {
        super(writer, c, c1, c2, string);

        this.writer = writer;
        this.maskColumns = maskColumns;
        
        this.separator = c;
        this.quotechar = c1;
        this.escapechar = c2;
        this.lineEnd = string;
        
        resultService = new ResultSetHelper(maskColumns);
    }

    public void writeAll(CSVColumnReader reader, boolean includeColumnNames) throws IOException, NoSuchAlgorithmException {
        writeAll(reader, reader.getColumnNames(), includeColumnNames);
    }

    public void writeAll(CSVReader reader, String[] columnNames, boolean includeColumnNames) throws IOException, NoSuchAlgorithmException {
        if (includeColumnNames) {
            writeNext(columnNames);
        }

        // Make searching for columns faster. Don't remove numeric columns
        for (int i=0; (columnNames != null && i<columnNames.length); i++) {
            if (maskColumns.contains(columnNames[i]))
                maskColumns.add(i);
        }
        
 
        ExecutorService pool = Executors.newCachedThreadPool();
        String[] line;
        while ((line = reader.readNext()) != null) {
            // TODO: use hash set or to mask

            /*for (int i=0; i<line.length; i++) {
             if (maskColumns.contains(i) || maskColumns.contains(getColumnName(columnNames, i))) {
             line[i] = SHA256.hash(line[i]);
             }
             }
             writeNext(line);*/
            pool.execute(new HashWorker(writer, maskColumns, line, separator, quotechar, escapechar, lineEnd));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
            Logger.getLogger(HashWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class HashWorker implements Runnable {

        private final String[] line;
        private final Writer writer;
        private final char separator;
        private final char quotechar;
        private final String lineEnd;
        private final char escapechar;
        private HashSet maskColumns;
        
        public HashWorker(Writer wr, HashSet maskColumns, String[] line, char separator, char quotechar, char escapechar, String lineEnd) {
            this.line = line;
            this.writer = wr;
            this.separator = separator;
            this.quotechar = quotechar;
            this.lineEnd = lineEnd;
            this.escapechar = escapechar;
            
            this.maskColumns = maskColumns;
        }

        @Override
        public void run() {
            try {
                writeNext(line);
            } catch (IOException ex) {
                Logger.getLogger(HashWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private String getColumnName(String[] columnNames, int index) {
            if (columnNames == null) {
                return null;
            }

            if (index > columnNames.length) {
                return null;
            }

            return columnNames[index];
        }

        /**
         * Writes the next line to the file.
         *
         * @param nextLine a string array with each comma-separated element as a
         * separate entry.
         */
        public void writeNext(String[] nextLine) throws IOException {

            if (nextLine == null) {
                return;
            }

            StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
            for (int i = 0; i < nextLine.length; i++) {

                if (i != 0) {
                    sb.append(separator);
                }

                String nextElement = nextLine[i];
                if (nextElement == null) {
                    continue;
                }
                if (quotechar != NO_QUOTE_CHARACTER) {
                    sb.append(quotechar);
                }

                if (maskColumns.contains(i)) {
                    try {
                        sb.append(SHA256.hash(nextElement));
                    } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                        Logger.getLogger(HashWriter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (quotechar != NO_QUOTE_CHARACTER && escapechar != NO_ESCAPE_CHARACTER)
                        sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);
                    else
                        sb.append(nextElement);
                }
                
                if (quotechar != NO_QUOTE_CHARACTER) {
                    sb.append(quotechar);
                }
            }

            sb.append(lineEnd);
            writer.write(sb.toString());

        }

        private boolean stringContainsSpecialCharacters(String line) {
            return line.indexOf(quotechar) != -1 || line.indexOf(escapechar) != -1;
        }

        // TODO: this is slow an can be optimized
        protected StringBuilder processLine(String nextElement) {
            StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);
                if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                    sb.append(escapechar).append(nextChar);
                } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
                    sb.append(escapechar).append(nextChar);
                } else {
                    sb.append(nextChar);
                }
            }

            return sb;
        }

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
