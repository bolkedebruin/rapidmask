/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask;

import au.com.bytecode.opencsv.CSVReader;
import ing.rapidmask.opencsv.HashWriter;
import ing.rapidmask.json.Config;
import ing.rapidmask.opencsv.CSVColumnReader;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author bolke
 */
public class RapidMask {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Config config = new Config("..");
        
        try {
            /*ConnectionManager cm = new ConnectionManager(config.getDriverName(), config.getConnectionString());
            GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File("/tmp/myfile.gzip")));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
            HashWriter extractor = new HashWriter(writer, '|', '\0','"', "\n", cm.getConnection());
            extractor.extract();
            writer.close();
            zip.close();*/
            
            FileOutputStream fos = new FileOutputStream(new File("/tmp/star.gzip"));
            //GZIPOutputStream zip = new GZIPOutputStream(fos);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            CSVColumnReader reader = new CSVColumnReader(new FileReader("/tmp/star2002-full.csv"), ',', '\0', false);
            HashSet maskColumns = new HashSet();
            maskColumns.add(2);
            maskColumns.add(4);
            maskColumns.add(6);
            HashWriter hashWriter = new HashWriter(writer, '|', '\0','"', "\n", maskColumns);
            hashWriter.writeAll(reader, true);
            writer.close();
            reader.close();
            //zip.close();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RapidMask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(RapidMask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RapidMask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(RapidMask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RapidMask.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
