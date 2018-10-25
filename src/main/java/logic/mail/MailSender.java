package logic.mail;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * @author Vladimir Nagornyi
 */




public class MailSender {

    /**
     * This method use smtp-protocol with ssl-authorization to send mail.
     * Settings in xml-file must meet requirement of this protocol.
     * Working with another protocols not guaranteed.
     *
     * @param settingsXML - Sender settings file address
     * @param recipientsXML - Recipients file address
     * @param file - attached file address
     * @throws IOException
     * @throws MessagingException
     */

    public static void sendMail(String settingsXML, String recipientsXML, String file) throws IOException, MessagingException{
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
            message.setFrom(new InternetAddress(properties.getProperty("username")));
            for (String mail : recipients.stringPropertyNames()){
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients.getProperty(mail)));
            }
            message.setSubject("Exchange rates 2016-2017");
            message.setText("Attached file consist PrivatBank exchange rates for available currencies.");
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.attachFile(file);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            message.setContent(multipart);
            Transport.send(message);

            System.out.println("Done!");

        } catch (FileNotFoundException e){
            System.out.println("Settings file not found. Please, check settings directory ");
        } catch (AddressException e) {
            System.out.println("Username or password incorrect");
        }
    }
}
