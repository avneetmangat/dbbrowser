package org.dbbrowser;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.axis.encoding.Base64;

public class TestEncryption
{
	public static void main(String[] args)
	{
		(new TestEncryption()).runWithDES();
		//(new TestEncryption()).runWithPBE();
	}
	
	public void runWithPBE()
	{
		System.out.println("Starting TestEncryption.runWithPBE...");
		try
		{
			//Use a password based encryption
			PBEKeySpec keySpec = new PBEKeySpec("MasterPassword".toCharArray());
			
			//Get a secret key using the key spec and the secret key factory
			//Generate a key for DES encryption and MD5 checksum using the key spec
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");	
			SecretKey pbeSecretKey = keyFactory.generateSecret(keySpec);
			
			Cipher c = Cipher.getInstance(pbeSecretKey.getAlgorithm());	//Symmetric DES encryption
			//Init using the salt and iteration count
			byte[] salt = new byte[]{(byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8};//8 byte long salt
			PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 20);
			c.init(Cipher.ENCRYPT_MODE, pbeSecretKey, pbeParameterSpec);
			
			byte[] cleartext = "aPassword".getBytes();

		    // Encrypt the cleartext
		    byte[] ciphertext = c.doFinal(cleartext);
		    
		    //Print the encrypted text
		    System.out.println("Encrypted text: " + new String(ciphertext));
		    
		    //-----------------------------------------------------------------
		    //Start decryption
		    //Convert the byte[] to string using base64 encoding
		    String base64EncodedString = Base64.encode(ciphertext);
		    System.out.println("base64EncodedString: " + base64EncodedString);
		    
		    //Decode the base64 encoded string
		    byte[] base64DecodedByteArray = Base64.decode(base64EncodedString);
		    System.out.println("Base64 decoded string: " + new String(base64DecodedByteArray));

		    // Initialize the same cipher for decryption
		    c.init(Cipher.DECRYPT_MODE, pbeSecretKey, pbeParameterSpec);

		    // Decrypt the ciphertext
		    byte[] recreatedClearText = c.doFinal(base64DecodedByteArray);	
		    
		    System.out.println(new String(cleartext) + " = " + new String(recreatedClearText));
		}
		catch(NoSuchAlgorithmException exc)
		{
			System.out.println(exc.getMessage());
		}
		catch(NoSuchPaddingException exc)
		{
			System.out.println(exc.getMessage());
		}
		catch(InvalidKeyException exc)
		{
			System.out.println(exc.getMessage());
		}	
		catch(BadPaddingException exc)
		{
			System.out.println(exc.getMessage());
		}
		catch(IllegalBlockSizeException exc)
		{
			System.out.println(exc.getMessage());
		}	
		catch(InvalidKeySpecException exc)
		{
			System.out.println(exc.getMessage());
		}
		catch(InvalidAlgorithmParameterException exc)
		{
			System.out.println(exc.getMessage());
		}			
	}	
	
	public void runWithDES()
	{
		System.out.println("Starting TestEncryption.runWithDES...");
		try
		{
			KeyGenerator keygen = KeyGenerator.getInstance("DES");
		    SecretKey desKey = keygen.generateKey();
		    
			Cipher c = Cipher.getInstance("DES");	//Symmetric encryption
			c.init(Cipher.ENCRYPT_MODE, desKey);
			
			byte[] cleartext = "portal".getBytes();

		    // Encrypt the cleartext
		    byte[] ciphertext = c.doFinal(cleartext);

		    // Initialize the same cipher for decryption
		    c.init(Cipher.DECRYPT_MODE, desKey);

		    // Decrypt the ciphertext
		    byte[] cleartext1 = c.doFinal(ciphertext);	
		    
		    System.out.println("Cypher text: " + new String(ciphertext));
		    System.out.println(new String(cleartext) + " = " + new String(cleartext1));
		}
		catch(NoSuchAlgorithmException exc)
		{
			System.out.println(exc.getMessage());
		}
		catch(NoSuchPaddingException exc)
		{
			System.out.println(exc.getMessage());
		}
		catch(InvalidKeyException exc)
		{
			System.out.println(exc.getMessage());
		}	
		catch(BadPaddingException exc)
		{
			System.out.println(exc.getMessage());
		}
		catch(IllegalBlockSizeException exc)
		{
			System.out.println(exc.getMessage());
		}			
	}

}
