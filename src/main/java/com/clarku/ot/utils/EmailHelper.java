package com.clarku.ot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.EmailVO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class EmailHelper {

	@Autowired
	JavaMailSenderImpl mailer;

	@Autowired
	TemplateEngine templateEngine;

	public void sendEMail(EmailVO emailVO) throws GlobalException {
		MimeMessage mimeMessage = mailer.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
		Context context = new Context();
		if (!emailVO.getVariables().isEmpty()) {
			emailVO.getVariables().forEach(context::setVariable);
		}

		try {
			helper.setTo(emailVO.getSendTo());
			helper.setSubject(emailVO.getSubject());
			String htmlContent = templateEngine.process(emailVO.getTemplateName(), context);
			helper.setText(htmlContent, true);
			mailer.send(mimeMessage);
		} catch (MessagingException exp) {
			log.error("Failed to send the Notification with message : \n {} \n with root cause:\n {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("Failed to send the Notification with message : \n {} \n with root cause:\n {}", exp.getMessage(), exp.getCause());
		}
	}
	
	public void sendEMailMultipleBCC(EmailVO emailVO) throws GlobalException {
		MimeMessage mimeMessage = mailer.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
		Context context = new Context();
		if (!emailVO.getVariables().isEmpty()) {
			emailVO.getVariables().forEach(context::setVariable);
		}

		try {
			helper.setBcc(emailVO.getSendMultipleBCCTo().toArray(new String[0]));
			helper.setSubject(emailVO.getSubject());
			String htmlContent = templateEngine.process(emailVO.getTemplateName(), context);
			helper.setText(htmlContent, true);
			mailer.send(mimeMessage);
		} catch (MessagingException exp) {
			log.error("Failed to send the Notification" + exp.getMessage());
		} catch (Exception exp) {
			log.error("Failed to send the Notification" + exp.getMessage());
		}
	}

}