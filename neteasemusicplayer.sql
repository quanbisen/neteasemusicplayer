create database neteasemusicplayer;
use neteasemusicplayer;
create table users
(
id varchar(20) primary key,
password varchar (20) not null ,
name varchar (30) default '空',
image varchar(100) default 'http://192.168.1.104:8080/neteasemusicplayerserver_war_exploded/image/UserDefaultImage.png'
);

insert into users(id,password) values('13025583130','10010');
select * from users;
update users set name='lollipop' where id='13025583130';

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