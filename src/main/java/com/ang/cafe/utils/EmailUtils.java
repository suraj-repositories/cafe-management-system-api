package com.ang.cafe.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailUtils {

	@Autowired
	private JavaMailSender emailSender;

	private String SENDER = "suraj2002fake@gmail.com";

	public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(SENDER);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		if (list != null && list.size() > 0)
			message.setCc(getCcArray(list));

		log.info("sending email");
		emailSender.send(message);
	}

	private String[] getCcArray(List<String> ccList) {
		String[] cc = new String[ccList.size()];

		for (int i = 0; i < ccList.size(); i++) {
			cc[i] = ccList.get(i);
		}
		return cc;
	}

	public void forgotMail(String to, String subject, String password) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(SENDER);
		helper.setTo(to);
		helper.setSubject(subject);

		String htmlMessage = "<h3>Your Login details are - </h3><p>Email : <b>" + to
				+ "</b></p><p>Password : <b>" + password + "</b></p>";
		message.setContent(htmlMessage, "text/html");
		emailSender.send(message);

	}
}
