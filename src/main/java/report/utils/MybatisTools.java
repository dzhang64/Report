package report.utils;

import org.apache.ibatis.session.SqlSession;

/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 21:36 2021/12/7 0007
 */
public class MybatisTools {

    public static<T>  Object[] getDao(Class<T> cls){
        SqlSession sqlSession = StaticStorage.sessionFactory.openSession();
        T mapper = sqlSession.getMapper(cls);
        Object[] o = {mapper,sqlSession};
        return o;
    }


}
