package org.dbbrowser.security;

import infrastructure.logging.Log;
import junit.framework.TestCase;

public class AsymmetricEncryptionEngineTest extends TestCase
{
    public AsymmetricEncryptionEngineTest(String name)
    {
        super(name);    
    }
    
	/**
	 * Uses asymmetric encryption to encrypt clearText into cypherText.  Base64 Encodes
	 * the cypherText and returns the base 64 encoded string 
	 * @param clearText
	 * @param passPhrase
	 * @throws EncryptionEngineException - if there is an error during encryption
	 * @return
	 */
	public void testEncryptionEngine()
	{
		try
		{
			String clearText = "Hello";
			String password = "password";
			Log.getInstance().debugMessage("Starting encryption using clearText " + clearText + "...", this.getClass().getName());
			String encryptedAndBASE64EncodedString = AsymmetricEncryptionEngine.encrypt(clearText, password);
			
			Log.getInstance().debugMessage("encryptedAndBASE64EncodedString is: " + encryptedAndBASE64EncodedString, this.getClass().getName());
			
			Log.getInstance().debugMessage("Starting decryption...", this.getClass().getName());

			String decryptedString = AsymmetricEncryptionEngine.decrypt(encryptedAndBASE64EncodedString, password);
			
			Log.getInstance().debugMessage("Clear text is: " + decryptedString, this.getClass().getName());
			
			assertEquals("AsymmetricEncryptionEngineTest.testEncryptionEngine failed", clearText, decryptedString);
		}
		catch(EncryptionEngineException exc)
		{
			fail(exc.getMessage());
		}
	}
}
