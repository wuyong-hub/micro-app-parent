package com.wysoft.https_auth.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "uaam_auth_login")
public class UaamAuthLogin {
	@Id
	@GeneratedValue(generator = "c-assigned")  
	@GenericGenerator(name = "c-assigned", strategy = "assigned")
	@Column(name = "authuuid")
	private String authuuid;
	private String appid;
	private String username;
	private String password;
	private Date authtime;
	private Integer loginstatus;

	public String getAuthuuid() {
		return authuuid;
	}

	public void setAuthuuid(String authuuid) {
		this.authuuid = authuuid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getAuthtime() {
		return authtime;
	}

	public void setAuthtime(Date authtime) {
		this.authtime = authtime;
	}

	public Integer getLoginstatus() {
		return loginstatus;
	}

	public void setLoginstatus(Integer loginstatus) {
		this.loginstatus = loginstatus;
	}

}
