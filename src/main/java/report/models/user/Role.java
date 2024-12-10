package report.models.user;

/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 20:31 2021/12/7 0007
 */
public enum Role {

    Admin(1),
    User(2);

    private int code;

    Role(int code) {
        this.code = code;
    }

    Role() {
    }

    /**
     * Gets the value of the field named code
     *
     * @return the value of the field named code,the type is int
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the field named code
     * <p>You can use getCode() to get the value of code</p>
     * * @param field type:int -- field name:code
     */
    public void setCode(int code) {
        this.code = code;
    }

    public static Role getRole(int code){
        if(code==1){
            return Admin;
        }
        return User;
    }

    public String getRoleStr(){
        if(code==1){
            return "admin";
        }
       return "user";
    }
}
