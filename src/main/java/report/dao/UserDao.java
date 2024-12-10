package report.dao;


import report.models.user.User;

/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 20:43 2021/12/7 0007
 */
public interface UserDao {

    void insertUser(User user);

    User findUserByLoginNameAndPassword(User user);

    User findUserByloginName(User user);

    void deleteUserByloginName(User user);

    void updateUser(User user);
}
