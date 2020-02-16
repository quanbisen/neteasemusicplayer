package dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;
import pojo.Register;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author super lollipop
 * @date 20-2-15
 */
@Repository(value = "registerDao")
public class RegisterDaoImpl implements RegisterDao {

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public int insert(Register register) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int i = sqlSession.insert("model.RegisterMapper.insertRegister",register);
        sqlSession.close();
        return i;
    }

    @Override
    public Register query(Register register) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        register = sqlSession.selectOne("model.RegisterMapper.queryRegister",register);
        sqlSession.close();
        return register;
    }

    @Override
    public int queryById(String id) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int count = sqlSession.selectOne("model.RegisterMapper.queryById",id);
        sqlSession.close();
        return count;
    }

    @Override
    public Date queryDate() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Date date = sqlSession.selectOne("model.RegisterMapper.queryDate");
        sqlSession.close();
        return date;
    }

    @Override
    public int updateDateAndCode(Register register) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int row = sqlSession.update("model.RegisterMapper.updateDateAndCode",register);
        sqlSession.close();
        return row;
    }

}
