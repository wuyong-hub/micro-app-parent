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
@Table(name = "uaam_roles")
public class UaamRole {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "roleid")
	private Integer roleid;
	private String rolename;
	private String remark;
	@ManyToMany(targetEntity = UaamResource.class)
	@JoinTable(name = "uaam_roleresources", joinColumns = {
			@JoinColumn(name = "roleid", referencedColumnName = "roleid") }, inverseJoinColumns = {
					@JoinColumn(name = "resid", referencedColumnName = "resid") })
	private List<UaamResource> resources = new ArrayList<>();

	public Integer getRoleid() {
		return roleid;
	}

	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<UaamResource> getResources() {
		return resources;
	}

	public void setResources(List<UaamResource> resources) {
		this.resources = resources;
	}

}
