drop database testdb if not exists;
create database testdb;
create table users(
  id int unsigned auto_increment,
  name varchar(64) unique not null,
  password varchar(128) not null,
  primary key(id)
) engine=innodb;
create table groups(
  id int unsigned auto_increment,
  name varchar(64) unique not null,
  primary key(id)
) engine=innodb;
create table usergroup(
  user_id int unsigned,
  group_id int unsigned,
  foreign key (user_id) references users(id) on delete cascade on update cascade,
  foreign key (group_id) references groups(id) on delete cascade on update cascade,
  primary key (user_id,group_id)
) engine=innodb;