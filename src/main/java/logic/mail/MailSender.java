package logic.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Vladimir Nagornyi
 */

public class MailSender {
    public static void sendMail(String settingsXML, String recipientsXML, String file) throws IOException, MessagingException {
        try(InputStream setIS = new FileInputStream(settingsXML);
            InputStream recIS = new FileInputStream(recipientsXML)) {
            Properties properties = new Properties();
            properties.loadFromXML(setIS);
            Properties recipients = new Properties();
            recipients.loadFromXML(recIS);
            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator(){
                        protected PasswordAuthentication getPasswordAuthentication(){
                            return new PasswordAuthentication(properties.getProperty("username"), properties.getProperty("password"));
                        }
                    });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("m4a1grant@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients.getProperty("0")));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients.getProperty("1")));
            message.setSubject("Testing Subject");
            message.setText("Dear Mail Crawler," +
                    "\n\n No spam to my email, please!");
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.attachFile(file);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            message.setContent(multipart);
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
