package report.service;

import org.apache.ibatis.session.SqlSession;
import report.dao.UserDao;
import report.models.user.User;
import report.utils.MybatisTools;

/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 20:36 2021/12/7 0007
 */
public class UserService {

    private Object[] objects = MybatisTools.getDao(UserDao.class);

    private UserDao userDao = (UserDao)objects[0];

    private SqlSession session = (SqlSession) objects[1];

    public User getUser(User user) {
        return userDao.findUserByLoginNameAndPassword(user);
    }

    public void insertUser(User user){
        try{
            userDao.insertUser(user);
            session.commit();
        }catch (Exception e){
            session.rollback();
        }
    }

    public User getUserByloginName(User user){
        return userDao.findUserByloginName(user);
    }

    public void deleteUserByloginName(User user) {
        try{
            userDao.deleteUserByloginName(user);
            session.commit();
        }catch (Exception e){
            session.rollback();
        }
    }

    public void updateUser(User user) {
        try{
            userDao.updateUser(user);
            session.commit();
        }catch (Exception e){
            session.rollback();
        }
    }
}
