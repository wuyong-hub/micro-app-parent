package com.wysoft.https_note.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.wysoft.https_base.action.BaseService;
import com.wysoft.https_base.annotation.RemoteMethod;
import com.wysoft.https_base.util.JSONUtil;

import net.sf.json.JSONObject;

/**
 * 邮件发送.
 * @author Wuyong
 *
 */
@Service("mailService")
public class MailService extends BaseService {

	@Autowired
	private JavaMailSender mailSender; // 框架自带的

	@Value("${spring.mail.username}") // 发送人的邮箱 比如155156641XX@163.com
	private String from;

	/**
	 * 发送邮件.
	 * @param json 输入字段title,email,content
	 * @return JSONObject
	 */
	@RemoteMethod
	public JSONObject sendMail(JSONObject json) {
		String title = JSONUtil.getString(json, "title"),
				email = JSONUtil.getString(json, "email"),
				content = JSONUtil.getString(json, "content");
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from); // 发送人的邮箱
		message.setSubject(title); // 标题
		message.setTo(email); // 发给谁 对方邮箱
		message.setText(content); // 内容
		mailSender.send(message); // 发送
		return JSONUtil.getResult();
	}

}