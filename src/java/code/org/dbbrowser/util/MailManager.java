package org.dbbrowser.util;

import java.security.Security;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class used to send email
 * @author amangat
 *
 */
public class MailManager
{
	/**
	 * Send an email
	 * @param from
	 * @param subject
	 * @param message
	 * @throws MailException
	 */
	public static void sendEmail(String from, String subject, String message)
		throws MailException
	{
		///Set up SSL provider as GMail requires SSL
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		
		//Set the properties
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		//Create the session
		Session session = Session.getDefaultInstance(props,	new javax.mail.Authenticator() 
				{
					protected PasswordAuthentication getPasswordAuthentication() 
					{
						return new PasswordAuthentication("dbbrowser@googlemail.com", "amkette");
					}
				});
	
		session.setDebug(true);
	
		Message msg = new MimeMessage(session);
		try
		{
			//If from is not blank, set sender address
			if( from != null && (!"".equals(from)) )
			{
				msg.setFrom( new InternetAddress(from) );
			}
		
			InternetAddress[] recipient = new InternetAddress[1];
			recipient[0] = new InternetAddress("dbbrowser@googlemail.com");
	
			msg.setRecipients(Message.RecipientType.TO, recipient);
	
			msg.setSubject(subject);
			msg.setContent(message, "text/plain");
			
			//Send message
			Transport.send(msg);
		}
		catch(AddressException exc)
		{
			throw new MailException( exc.getMessage() );
		}
		catch(MessagingException exc)
		{
			throw new MailException( exc.getMessage() );
		}		
	}
}
