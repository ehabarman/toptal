package com.toptal.backend.service.data;

import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.errors.CustomExceptions.BadWhereGrammerException;
import org.springframework.jdbc.BadSqlGrammarException;

/**
 * Contains common methods used in Service classes
 *
 * @author ehab
 */
public abstract class BaseService {

    protected void badSqlGrammerCustomHandle(Exception e) {
        if (e instanceof BadSqlGrammarException) {
            String msg = e.getCause().getMessage();
            if (msg.contains("You have an error in your SQL syntax")) {
                throw new BadWhereGrammerException("Make sure your query filter clause is correct");
            }
            throw new BadWhereGrammerException(msg.replaceAll("column", "field").replace(" clause", ""));
        }
    }

    protected String appendPagingClause(String query, Integer pageNumber, Integer pageSize, boolean doPaging) {
        if (doPaging) {
            return StringUtil.format("%s LIMIT %s, %s", query, pageNumber * pageSize, pageSize);
        }
        return query;
    }

    protected String appendWhereClause(String query, String whereClause) {
        if (StringUtil.isntNullNorEmpty(whereClause)) {
            return StringUtil.format("%s WHERE %s", query, whereClause);
        }
        return query;
    }
}
