package report.dao;

import report.models.function.Function;

import java.util.List;

public interface FunctionDao {

    void insertFuncion(Function f);

    void updateFunction(Function f);

    void deleteFunction(Function f);

    Function findFunction(Function f);

    List<Function> findAllFunction();
}
