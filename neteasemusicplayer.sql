create database neteasemusicplayer;

create table users
(
id varchar(20) primary key,
password varchar (20) not null ,
name varchar (30) default '空',
image varchar(100)
);
create table songgroups
(
id int primary key auto_increment,
name varchar (20),
user_id varchar(20) references users(id)
);
create table songs 
(
id int(5) zerofill primary key auto_increment,
name varchar (25),
singer varchar(25),
album varchar (25),
total_time varchar(10),
size float,
resource varchar (150),
lyrics varchar (150)
)