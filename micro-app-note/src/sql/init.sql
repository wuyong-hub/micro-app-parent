drop table if exists  simple_note;
create table simple_note(
	id int not null auto_increment primary key,
  title varchar(200) comment '标题',
  keywords varchar(200) comment '关键词',
  content text comment '内容',
  record_time DATETIME comment '记录时间',
  uid varchar(50) comment '用户标识',
  location varchar(50) comment '发生位置',
  note_type varchar(20) comment '所属分类'
) ENGINE=INNODB   default character set = 'utf8'  comment '便捷笔记';
