package com.coriant.AutoClient;
/*
===============================================================================
FileName: SimCryptor.java
Project: AutoClient
Author(s):
Description: This class is identical with the one in UC code
Notes:  Encryption Algorithm
History:
Date  Name  Modification
----------  --------------- -------------------------------
10/2003  Haishan Wang Initial version
06/2004     Lucia Leung     Modify the encryption method
===============================================================================
*/

import com.tellabs.ucc.sm.*;
import com.tellabs.ucc.util.*;
import com.tellabs.ucc.main.UCProto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * This class is used for Encrypting the password before we send the
 * password to the SM server. Currently we are using a simple EX-OR method
 * to encrypt, however this may change in future if SM deploys some of the
 * more advanced encryption method.
 *
 * @version 0.1, 3/29/2000
 * 11/2001  MD5 encrption is used
 */
public class SimCryptor
{

	private static final String AES = "AES";
    private static final SecureRandom prng = new SecureRandom();

    private SimCryptor()
    {}

    /**
     * Converts byte array into hexidecimal string
     * 
     */
    private static String toHex(byte[] number)
    {
        StringBuffer buf = new StringBuffer(number.length * 2);

        for (int j = 0; j < number.length; j++)
        {
            if (((int) number[j] & 0xff) < 0x10)
            {
                buf.append("0");
            }
            buf.append(Long.toString((int) number[j] & 0xff, 16));
        }

        return buf.toString();
    }

    public static String calcMD5(String data)
    {
        String result = "";

        try
        {
            result = hash(data, "MD5");
        }
        catch (UCSecurityException e)
        {}

        return result;
    }

    /**
     * Hashes data using default hashing algorithm and converts
     * the message digest to hexidecimal form
     * 
     * @param data the String to be hashed
     * @return the hexidecimal representation of the message digest
     * @throws UCSecurityException If hashing algorithm is not available 
     */
    public static String hash(String data) throws UCSecurityException
    {
        return hash(data.getBytes(), "MD5");
    }

    /**
     * Hashes data using specified hashing algorithm and converts
     * the message digest to hexidecimal form
     * 
     * @param data the String to be hashed
     * @param algorithm the requested hashing algorithm
     * @return the hexidecimal representation of the message digest
     * @throws UCSecurityException If hashing algorithm is not available 
     */
    public static String hash(String data, String algorithm) throws UCSecurityException
    {
        return hash(data.getBytes(), algorithm);
    }

    /**
     * Hashes data using established hashing algorithm and converts
     * the message digest to hexidecimal form
     * 
     * @param data the byte array to be hashed
     * #param algorithm the requested hashing algorithm
     * @return the hexidecimal representation of the message digest
     * @throws UCSecurityException If hashing algorithm is not available 
     */
    public static String hash(byte[] data, String algorithm) throws UCSecurityException
    {
        String result = "";
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance(algorithm);
            md.update(data);
            byte[] byteResult = md.digest();
            result = toHex(byteResult);
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw new UCSecurityException(UCProto.UC_RESOURCES.getString("SM11"), ex);
        }

        return result;
    }
    
    public static String encrypt(String input) throws UCSecurityException{
    	String s = hash(input);
        return s;
    }

    public static String generateAuthenticationToken(String password, String nonce) throws UCSecurityException
    {
//        String sRval = "";
//
        String s = encryptAES(password);
//        s = s + nonce;
//        sRval = encryptAES(s);
        
//        System.out.println("PASSWORD: " + password + " encrypted: " + s);

        return s; //sRval;
    }
    
    public static String encryptAES(String password) throws UCSecurityException
    {
        try
        {
            //KeyGenerator kgen = KeyGenerator.getInstance(AES);
            //kgen.init(128); // 128, 192 or 256
            
            Cipher cipher = Cipher.getInstance(AES);
            //cipher.init(Cipher.ENCRYPT_MODE, 
            //        new SecretKeySpec(kgen.generateKey().getEncoded(), AES));
            cipher.init(Cipher.ENCRYPT_MODE, 
                    new SecretKeySpec(("zifheers8592#(.@").getBytes(), AES));

            byte[] encryptedBytes = cipher.doFinal(password.getBytes());

            return toHex(encryptedBytes);
        } 
        catch (Exception e)
        {
            ErrorLogger.logError(ErrorLogger.CRITICAL, Cryptor.class.getName() + ".encryptAES", 
                    "Error encrypting password: " + e);
            e.printStackTrace();
            throw new UCSecurityException(UCProto.UC_RESOURCES.getString("SM11"), e);
            //throw e;
        } 
    }

    /**
     * Returns a cryptographically secure random number
     * @return - the random number
     */
    public static String nextRandom(int numBytes)
    {
        byte[] result = new byte[numBytes];
        prng.nextBytes(result);

        return toHex(result);
    }
}
