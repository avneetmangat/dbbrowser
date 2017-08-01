package org.dbbrowser.security;

import infrastructure.logging.Log;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.axis.encoding.Base64;

/**
 * Class used to encrypt plaintext using asymmetric encryption
 * @author amangat
 *
 */
public class AsymmetricEncryptionEngine
{
	/**
	 * Uses asymmetric encryption to encrypt clearText into cypherText.  Base64 Encodes
	 * the cypherText and returns the base 64 encoded string 
	 * @param clearText
	 * @param passPhrase
	 * @throws EncryptionEngineException - if there is an error during encryption
	 * @return
	 */
	public static String encrypt(String clearText, String passPhrase)
		throws EncryptionEngineException
	{
		String base64EncodedCipherText = null;
		Log.getInstance().debugMessage("Starting Asymmetric Encryption...", AsymmetricEncryptionEngine.class.getName());
		try
		{
			//Use a password based encryption
			PBEKeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray());
			
			//Get a secret key using the key spec and the secret key factory
			//Generate a key for DES encryption and MD5 checksum using the key spec
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");	
			SecretKey pbeSecretKey = keyFactory.generateSecret(keySpec);
			
			Cipher c = Cipher.getInstance(pbeSecretKey.getAlgorithm());	//Symmetric DES encryption
			//Init using the salt and iteration count
			byte[] salt = new byte[]{(byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8};//8 byte long salt
			PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 20);
			c.init(Cipher.ENCRYPT_MODE, pbeSecretKey, pbeParameterSpec);
			
			byte[] cleartext = clearText.getBytes();

		    // Encrypt the cleartext
		    byte[] ciphertextArray = c.doFinal(cleartext);
		    
		    //Print the encrypted text
		    Log.getInstance().debugMessage("Encrypted text: " + new String(ciphertextArray), AsymmetricEncryptionEngine.class.getName());		
		    
		    base64EncodedCipherText = Base64.encode(ciphertextArray);
		    Log.getInstance().debugMessage("base64EncodedString: " + base64EncodedCipherText, AsymmetricEncryptionEngine.class.getName());		    
		}
		catch(NoSuchAlgorithmException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}
		catch(NoSuchPaddingException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}
		catch(InvalidKeyException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}	
		catch(BadPaddingException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}
		catch(IllegalBlockSizeException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}
		catch(InvalidAlgorithmParameterException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}	
		catch(InvalidKeySpecException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}		
		
		return base64EncodedCipherText;
	}
	
	/**
	 * Uses asymmetric encryption to decrypt cypherText into clearText.  
	 * clearText is a base64 encoded string which is first decoded and then decrypted  
	 * @param clearText
	 * @param passPhrase
	 * @throws EncryptionEngineException
	 * @return
	 */
	public static String decrypt(String cypherTextInBase64Encoding, String passPhrase)
		throws EncryptionEngineException
	{	
		String cleartext = null;
		Log.getInstance().debugMessage("Starting Asymmetric Decryption...", AsymmetricEncryptionEngine.class.getName());
		try
		{
		    byte[] base64DecodedByteArray = Base64.decode(cypherTextInBase64Encoding);
		    Log.getInstance().debugMessage("Base64 decoded string: " + new String(base64DecodedByteArray), AsymmetricEncryptionEngine.class.getName());
		    
			PBEKeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray());
			
			//Get a secret key using the key spec and the secret key factory
			//Generate a key for DES encryption and MD5 checksum using the key spec
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");	
			SecretKey pbeSecretKey = keyFactory.generateSecret(keySpec);
			Cipher c = Cipher.getInstance(pbeSecretKey.getAlgorithm());	//Asymmetric DES encryption			
			byte[] salt = new byte[]{(byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8, (byte)8};//8 byte long salt
			PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 20);
		    
		    c.init(Cipher.DECRYPT_MODE, pbeSecretKey, pbeParameterSpec);

		    // Decrypt the ciphertext
		    byte[] recreatedClearText = c.doFinal(base64DecodedByteArray);	
		    cleartext = new String(recreatedClearText);
		    Log.getInstance().debugMessage("Finished recreating clearText", AsymmetricEncryptionEngine.class.getName());
		}
		catch(InvalidAlgorithmParameterException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}			
		catch(InvalidKeySpecException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}		
		catch(NoSuchAlgorithmException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}
		catch(NoSuchPaddingException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}
		catch(InvalidKeyException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}	
		catch(BadPaddingException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}
		catch(IllegalBlockSizeException exc)
		{
			Log.getInstance().debugMessage(exc.getMessage(), AsymmetricEncryptionEngine.class.getName());
			exc.printStackTrace();
			throw new EncryptionEngineException( exc );
		}	
		
		return cleartext;		
	}
}
