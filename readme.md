## 毕业设计项目：基于JavaFX+Spring+Mybatis的音乐播放器的设计与实现

#### 项目组成（三部分）

1. [播放器客户端](https://github.com/quanbisen/neteasemusicplayer)
2. [资源管理客户端](https://github.com/quanbisen/playermanager)
3. [服务器端](https://github.com/quanbisen/playerserver)

### 播放器客户端

#### 运行

1. 克隆代码

```shell
git clone https://github.com/quanbisen/neteasemusicplayer.git
```

2. 下载JDK1.8
3. 用IDEA IntelliJ打开，导入Maven管理的依赖包后运行

###### 注意：如果是Ubuntu系统，需要安装ffmpeg解码，不然无法播放音乐

```shell
sudo apt install ubuntu-restricted-extras ffmpeg
```

#### 功能结构及实现结果

##### 功能结构图

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112358播放器客户端功能模块图.jpg'></img></div>

##### 1. 基本功能

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112351基本功能模块实现图.jpg'></img></div>

##### 2. 用户标识功能

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112434用户标识功能模块实现图.jpg'></img></div>

##### 3. 搜索功能模块

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112354搜索功能模块实现图.jpg'></img></div>

##### 4. 最近播放功能模块

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112402最近播放功能模块实现图.jpg'></img></div>

##### 5. 歌单管理功能模块

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112409歌单管理功能模块实现图.jpg'></img></div>

##### 6. 本地音乐功能模块

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603114249本地音乐功能模块实现图.jpg'></img></div>

##### 7. 歌词显示功能模块

<div align='center'><img src='https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112425歌词显示功能模块实现图.jpg'></img></div>
