package suppp.project.socailMedia.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Controller
public class EmailSender {
	
	@Autowired
	JavaMailSender emailSender;
	
	@Autowired
	TemplateEngine engine;
	
	public void sendOTP(int otp, String to, String name) {
		
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		try {
			helper.setTo(to);
			helper.setFrom("suprithhb54@gmail.com","PetPal");
			helper.setSubject("Verify your email");
			
			Context context = new Context();
			context.setVariable("name", name);
			context.setVariable("otp", otp);
			
			String body = engine.process("otpTemplate.html", context);
			helper.setText(body, true);
			
		}catch (Exception e) {
			
		}
		emailSender.send(message);		
	}
}
