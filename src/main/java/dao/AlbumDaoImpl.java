package dao;

import model.Album;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileFilter;

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
            String imageURL210 = "http://114.116.240.232:8080/neteasemusicplayerserver/image/album" + path;
            String imageURL58 = "http://114.116.240.232:8080/neteasemusicplayerserver/image/album/58px" + path.substring(0,path.indexOf(".")) + "_58" + path.substring(path.indexOf("."));
            System.out.println(albumName);
            System.out.println(imageURL210);
            System.out.println(imageURL58);
            Album album = new Album(null,albumName,imageURL210,imageURL58);
            applicationContext.getBean(AlbumDao.class).insert(album);
        }
    }

    @Test
    public void findAlbumByNameTest(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Album album = applicationContext.getBean(AlbumDao.class).queryAlbumByName("18");
        System.out.println(album.getImageURL58());
    }
}
