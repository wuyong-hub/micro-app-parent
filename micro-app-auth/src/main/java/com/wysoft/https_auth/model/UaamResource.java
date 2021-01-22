package com.wysoft.https_auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "uaam_resources")
public class UaamResource {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resid")
	private Integer resid;
	private Integer parentid;
	private String restype;
	private String resname;
	private Integer reslevel;
	private String resurl;
	private String icon;

	public Integer getResid() {
		return resid;
	}

	public void setResid(Integer resid) {
		this.resid = resid;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public String getRestype() {
		return restype;
	}

	public void setRestype(String restype) {
		this.restype = restype;
	}

	public String getResname() {
		return resname;
	}

	public void setResname(String resname) {
		this.resname = resname;
	}

	public Integer getReslevel() {
		return reslevel;
	}

	public void setReslevel(Integer reslevel) {
		this.reslevel = reslevel;
	}

	public String getResurl() {
		return resurl;
	}

	public void setResurl(String resurl) {
		this.resurl = resurl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
