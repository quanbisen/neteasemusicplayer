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

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112358%E6%92%AD%E6%94%BE%E5%99%A8%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%9B%BE.jpg)

##### 1. 基本功能

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112351%E5%9F%BA%E6%9C%AC%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0%E5%9B%BE.jpg)

##### 2. 用户标识功能

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112434%E7%94%A8%E6%88%B7%E6%A0%87%E8%AF%86%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0%E5%9B%BE.jpg)

##### 3. 搜索功能模块

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112354%E6%90%9C%E7%B4%A2%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0%E5%9B%BE.jpg)

##### 4. 最近播放功能模块

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112402%E6%9C%80%E8%BF%91%E6%92%AD%E6%94%BE%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0%E5%9B%BE.jpg)

##### 5. 歌单管理功能模块

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112409%E6%AD%8C%E5%8D%95%E7%AE%A1%E7%90%86%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0%E5%9B%BE.jpg)

##### 6. 本地音乐功能模块

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112406%E6%9C%AC%E5%9C%B0%E9%9F%B3%E4%B9%90%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0%E5%9B%BE.jpg)

##### 7. 歌词显示功能模块

![](https://images.cnblogs.com/cnblogs_com/quanbisen/1779769/o_200603112425%E6%AD%8C%E8%AF%8D%E6%98%BE%E7%A4%BA%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0%E5%9B%BE.jpg)
