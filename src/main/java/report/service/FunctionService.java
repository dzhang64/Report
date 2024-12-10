package report.service;

import org.apache.ibatis.session.SqlSession;
import report.dao.FunctionDao;
import report.models.function.Function;
import report.utils.MybatisTools;

import java.util.List;

public class FunctionService {

    private Object[] objects = MybatisTools.getDao(FunctionDao.class);

    private FunctionDao functionDao = (FunctionDao)objects[0];

    private SqlSession session = (SqlSession) objects[1];

    public Function getFunction(Function function) {
        return functionDao.findFunction(function);
    }



    public List<Function> getALLFunction(){
        return functionDao.findAllFunction();
    }

    public void insertFunction(Function function){
        try{
            functionDao.insertFuncion(function);
            session.commit();
        }catch (Exception e){
            session.rollback();
        }
    }

    public void deleteFunctione(Function function) {
        try{
            functionDao.deleteFunction(function);
            session.commit();
        }catch (Exception e){
            session.rollback();
        }
    }

    public void updateFunction(Function function) {
        try{
            functionDao.updateFunction(function);
            session.commit();
        }catch (Exception e){
            session.rollback();
        }
    }
}
