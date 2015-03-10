package testing;

import static testing.MailProperties.BODY;
import static testing.MailProperties.FROM;
import static testing.MailProperties.PORT;
import static testing.MailProperties.SUBJECT;
import static testing.MailProperties.TO;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ru.aplana.app.RunServer;

/**
 * 
 * Self testing
 * 
 * @author Maksim Stepanov
 *
 */
public class Test implements Runnable {

	public Test() {

		RunServer.loggerInfo.info("Test stating..");
		
	}

	@Override
	public void run() {

		while (true) {

			try {

				sendMessage(PORT, FROM, SUBJECT, BODY, TO);

			} catch (MessagingException e) {

				RunServer.loggerSevere.severe("[Test] Can't send email! " + e.getMessage());
				
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private  Properties getMailProperties(int port) {
		
		Properties mailProps = new Properties();
		
		mailProps.setProperty("mail.smtp.host", "localhost");
		
		mailProps.setProperty("mail.smtp.port", "" + port);
		
		mailProps.setProperty("mail.smtp.sendpartial", "true");
		
		return mailProps;
	}

	private  void sendMessage(int port, String from,
			String subject,	String body, String to) throws MessagingException {

		Properties mailProps = getMailProperties(port);

		Session session = Session.getInstance(mailProps, null);

		MimeMessage msg = createMessage(session, from, to, subject, body);
		
		Transport.send(msg);
	}

	private  MimeMessage createMessage(Session session,
			String from, String to, String subject, String body)
			throws MessagingException {
		
		MimeMessage msg = new MimeMessage(session);
		
		msg.setFrom(new InternetAddress(from));
		
		msg.setSubject(subject);
		
		msg.setSentDate(new Date());
		
		msg.setText(body);
		
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		
		return msg;
	}

}
