package dao;

import pojo.Album;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * @author super lollipop
 * @date 20-2-10
 */
@Repository(value = "albumDao")
public class AlbumDaoImpl implements AlbumDao{

    /**注入MyBatis的SqlSessionFactory对象*/
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void insert(Album album) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        System.out.println(sqlSession.insert("model.AlbumMapper.insertAlbum",album));
        sqlSession.close();
    }

    @Override
    public Album queryAlbumByName(String name) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Album album = sqlSession.selectOne("model.AlbumMapper.findAlbumByName",name);
        sqlSession.close();
        return album;
    }

    @Override
    public Map<String, String> queryAlbumMap(List<String> albumList) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
//        Map<String, HashMap<String,String>> map = sqlSession.selectMap("model.AlbumMapper.queryAlbumMap",albumList,"name");
//        sqlSession.close();
//        Map<String,String> singerMap = new HashMap<>();
//        map.keySet().forEach(s -> {
//            singerMap.put(s,map.get(s).get("image_url_58"));
//        });
        Map<String,String> singerMap = new HashMap<>();
        albumList.forEach(name->{
            try {
                Album album = sqlSession.selectOne("model.AlbumMapper.findAlbumByName",name);
                System.out.println(album.toString());
                if (album!=null){
                    singerMap.put(name,album.getImageURL());
                }
            }catch (Exception e){e.printStackTrace();}

        });
        sqlSession.close();
        return singerMap;
    }

    @Test
    public void testQueryAlbumMap(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        List<String> list = new ArrayList<>();
        list.add("不如吃茶去");
        list.add("18");
        Map<String,String> singerListMap = applicationContext.getBean(AlbumDao.class).queryAlbumMap(list);
        Set<String> set = singerListMap.keySet();
        set.forEach(s -> {
            System.out.println(s + "-->" + singerListMap.get(s));
        });
    }

    @Test
    public void insertTest(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        File file = new File("/media/ubuntu/Documents/IntelliJProject/neteasemusicplayerserver/web/image/album");
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        for (int i = 0; i < files.length; i++) {
            String path = files[i].getPath();
            path = path.substring(path.lastIndexOf("/"));
            String albumName = path.substring(path.lastIndexOf("/")+1,path.indexOf("."));
            String imageURL = "http://114.116.240.232:8080/neteasemusicplayerserver/image/album" + path;
            System.out.println(albumName);
            Album album = new Album(null,albumName,imageURL);
            applicationContext.getBean(AlbumDao.class).insert(album);
        }
    }

    @Test
    public void findAlbumByNameTest(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Album album = applicationContext.getBean(AlbumDao.class).queryAlbumByName("18");
        System.out.println(album.getImageURL());
    }
}
