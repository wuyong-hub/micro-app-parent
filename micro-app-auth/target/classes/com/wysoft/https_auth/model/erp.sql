/**========ERP系统表============*/


/**=====客户地址信息======*/
drop table if EXISTS erp_customer_address;
create table erp_customer_address(
	addr_id varchar(70) primary key,
  user_id int(10) not null,
  name varchar(30),
  phone varchar(20),
  addr varchar(200)
);

alter table erp_customer_address
add CONSTRAINT pk_eca_user_id
FOREIGN key(user_id) REFERENCES uaam_users(userid);

/**===客户订单====*/
drop table if EXISTS erp_order;
create table erp_order(
	order_id varchar(70) primary key,
  user_id int(10) not null,
  addr_id varchar(70) not null,
  total float(10,2) not null,
  order_time TIMESTAMP,
  order_status int(1) default 0
);

alter table erp_order
add CONSTRAINT pk_eo_user_id
FOREIGN key(user_id) REFERENCES uaam_users(userid);

alter table erp_order
add CONSTRAINT fk_eo_addr_id
FOREIGN KEY (addr_id) REFERENCES erp_customer_address(addr_id);

/**====产品表=====*/
drop table if exists erp_product;
create table erp_product(
	product_id varchar(70) primary key,
	product_name varchar(200) not null,
  price float(10,2) not null,
  product_info text
);


/**====订单——物品表=====*/
drop table if EXISTS erp_order_item;
create table erp_order_item(
	item_id varchar(70) primary key,
  product_id varchar(70),
  order_id varchar(70),
  item_count int(5)
);

alter table erp_order_item
add CONSTRAINT fk_eoi_product_id
FOREIGN key(product_id) REFERENCES erp_product(product_id);

alter table erp_order_item
add CONSTRAINT fk_eoi_order_id
FOREIGN key(order_id) REFERENCES erp_order(order_id);


