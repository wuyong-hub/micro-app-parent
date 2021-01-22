package com.wysoft.https_auth.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "uaam_users")
public class UaamUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userid")
	private Integer userid;
	private String username;
	private String password;
	private String personname;
	private String gender;
	private String organizecode;
	private String status;
	@ManyToMany(targetEntity = UaamRole.class)
	@JoinTable(name = "uaam_userroles", joinColumns = {
			@JoinColumn(name = "userid", referencedColumnName = "userid") }, inverseJoinColumns = {
					@JoinColumn(name = "roleid", referencedColumnName = "roleid") })
	private List<UaamRole> roles = new ArrayList<>();

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
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

	public String getPersonname() {
		return personname;
	}

	public void setPersonname(String personname) {
		this.personname = personname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getOrganizecode() {
		return organizecode;
	}

	public void setOrganizecode(String organizecode) {
		this.organizecode = organizecode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<UaamRole> getRoles() {
		return roles;
	}

	public void setRoles(List<UaamRole> roles) {
		this.roles = roles;
	}

}
