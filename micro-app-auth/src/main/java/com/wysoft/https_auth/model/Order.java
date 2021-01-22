package com.wysoft.https_auth.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_order")
public class Order {
	@Id
	@GeneratedValue(generator="jpa-uuid")
    @GenericGenerator(name="jpa-uuid", strategy = "uuid")
	@Column(name = "order_id")
	private String orderId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UaamUser user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "addr_id")
	private CustomerAddr addr;
	private Double total;
	@Column(name = "order_time")
	private Timestamp orderTime;
	@Column(name = "order_status")
	private Integer status;

	@OneToMany(targetEntity = OrderItem.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id")
	private List<OrderItem> items;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public UaamUser getUser() {
		return user;
	}

	public void setUser(UaamUser user) {
		this.user = user;
	}

	public CustomerAddr getAddr() {
		return addr;
	}

	public void setAddr(CustomerAddr addr) {
		this.addr = addr;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Timestamp getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Timestamp orderTime) {
		this.orderTime = orderTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

}
