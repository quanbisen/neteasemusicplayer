create database neteasemusicplayer;
use neteasemusicplayer;
create table t_songs
(
id bigint primary key auto_increment,
name varchar (255),
singer varchar(255),
album varchar (255),
total_time varchar(50),
size varchar(50),
resource_url varchar (255),
lyric_url varchar (255),
album_url varchar(255)
);
create table t_singers
(
id bigint primary key auto_increment,
name varchar(255),
birthday date,
height float,
weight float,
constellation varchar(50),
description varchar(500),
image_url varchar(255)
);
create table t_users
(
id varchar(50) primary key,
password varchar(50) not null,
name varchar(50),
token varchar(50),
login_time datetime,
description varchar(500),
sex varchar(5) default '保密',
birthday date,
image_url varchar(255)
);
create table t_groups
(
id bigint primary key auto_increment,
name varchar (50),
description varchar(500),
createtime datetime default current_timestamp, 
user_id varchar(50) references t_users(id),
image_url varchar(255)
);
create table t_groupsongs
(
group_id bigint references t_groups(id),
song_id bigint references t_songs(id),
createtime datetime default current_timestamp
);


drop table users;
update users set image_url='http://localhost:8080/neteasemusicplayerserver_war_exploded/image/UserDefaultImage.png' where id=13025583130;
insert into users(id,password) values('13025583130','10010');
select * from users;
update users set name='lollipop' where id='13025583130';

create table songgroups
(
id bigint primary key auto_increment,
name varchar (20),
user_id varchar(20) references users(id)
);

insert into songs(name,singer,album,total_time,size,resource) values('可惜没如果','林俊杰','可惜没如果','04:58',52.6,'http://localhost:8080/neteasemusicplayerserver_war_exploded/localSong/林俊杰-可惜没如果.wav');
insert into songs(name,singer,album,total_time,size,resource) values('爱不会绝迹','林俊杰','爱不会绝迹','04:00',42.4,'http://localhost:8080/neteasemusicplayerserver_war_exploded/localSong/林俊杰-爱不会绝迹.wav');
select * from songs;
drop table songs;



drop table singers;
insert into singers(name,image_url) values('Westlife','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/Westlife.png');
insert into singers(name,image_url) values('周杰伦','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/zhoujielun.png');
insert into singers(name,image_url) values('周柏豪','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/zhoubaihao.png');
insert into singers(name,image_url) values('罗志祥','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/luozhixiang.png');
insert into singers(name,image_url) values('许嵩','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/xusong.png');
insert into singers(name,image_url) values('G.E.M.邓紫棋','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/G.E.M.png');
insert into singers(name,image_url) values('陈柏宇','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/chenbaiyu.png');
insert into singers(name,image_url) values('林俊杰','http://localhost:8080/neteasemusicplayerserver_war_exploded/image/singer/linjunjie.png');
select * from singers;