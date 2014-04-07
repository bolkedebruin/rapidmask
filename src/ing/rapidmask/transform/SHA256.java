/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask.transform;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author bolke
 */
public class SHA256 {
    public static String hash(String org) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (org == null)
            return null;
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        byte[] digest = md.digest(org.getBytes("UTF-16LE"));
        
        String hex = Hex.encodeHexString(digest).toUpperCase();
        
        return hex;
        
    } 
}
