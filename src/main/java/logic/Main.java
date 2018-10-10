package logic;

import logic.excel.CreatorXLS;
import logic.mail.MailSender;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * @author Vladimir Nagornyi
 */
public class Main {
    private static final String FIRST_DATE = "01.01.2016";
    private static final String LAST_DATE = "10.01.2016";
    private static final String SENDER_SETTINGS = "settings\\SenderSettings.xml";
    private static final String RECIPIENTS = "settings\\Recipients.xml";
    private static final String FILE = "Result.xlsx";

    public static void main(String[] args) throws IOException, MessagingException {
        CreatorXLS creator = new CreatorXLS();
        creator.createXLS(FIRST_DATE, LAST_DATE, FILE);
        MailSender.sendMail(SENDER_SETTINGS, RECIPIENTS, FILE);
    }
}
