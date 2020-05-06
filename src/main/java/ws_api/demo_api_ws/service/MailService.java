package ws_api.demo_api_ws.service;


import lombok.extern.log4j.Log4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class MailService {
    private JavaMailSender javaMailSender;

    private static final Log log = LogFactory.getLog(MailService.class);
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public boolean sendEmail(String email,String code) throws MailException, MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            message.setContent(code, "text/html");
            helper.setTo(email);
            helper.setSubject("Mã xác nhận");

            this.javaMailSender.send(message);
            return true;
        }catch (MessagingException e){
            log.error(e);
            return false;
        }
    }

}
