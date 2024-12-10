package report.models.user;

/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 20:25 2021/12/7 0007
 */
public class User {

    /**
     * Default constructor
     */
    public User() {
    }

    public User(String loginName, String pwd) {
        this.loginName = loginName;
        this.pwd = pwd;
    }

    public User(String userName, String loginName, String pwd, int userRoleId) {
        this.loginName = loginName;
        this.userName = userName;
        this.pwd = pwd;
        this.userRoleId = userRoleId;
    }

    public User(String loginName) {
        this.loginName = loginName;
    }

    /**
     *user id
     */
    private int uid;

    /**
     *user loginName
     */
    private String loginName;

    /**
     *user name
     */
    private String userName;

    /**
     *user password
     */
    private String pwd;

    /**
     *user Role Id
     */
    private int userRoleId;

    /**
     * Gets the value of the field named uid
     *
     * @return the value of the field named uid,the type is int
     */
    public int getUid() {
        return uid;
    }

    /**
     * Sets the field named uid
     * <p>You can use getUid() to get the value of uid</p>
     * * @param field type:int -- field name:uid
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * Gets the value of the field named loginName
     *
     * @return the value of the field named loginName,the type is java.lang.String
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * Sets the field named loginName
     * <p>You can use getLoginName() to get the value of loginName</p>
     * * @param field type:java.lang.String -- field name:loginName
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * Gets the value of the field named userName
     *
     * @return the value of the field named userName,the type is java.lang.String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the field named userName
     * <p>You can use getUserName() to get the value of userName</p>
     * * @param field type:java.lang.String -- field name:userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the value of the field named pwd
     *
     * @return the value of the field named pwd,the type is java.lang.String
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * Sets the field named pwd
     * <p>You can use getPwd() to get the value of pwd</p>
     * * @param field type:java.lang.String -- field name:pwd
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * Gets the value of the field named userRoleId
     *
     * @return the value of the field named userRoleId,the type is int
     */
    public int getUserRoleId() {
        return userRoleId;
    }

    /**
     * Sets the field named userRoleId
     * <p>You can use getUserRoleId() to get the value of userRoleId</p>
     * * @param field type:int -- field name:userRoleId
     */
    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }
}
