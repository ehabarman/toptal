package com.toptal.backend.controller;

import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.query.MYSQLQueryTransform;

/**
 * Shared functionalities and constants between the controllers
 */
public class SharedUtils {

    /**
     * Validate pagination values
     */
    public static boolean isValidForPagination(Integer pageSize, Integer pageNumber) {
        return pageSize != null && pageSize > 0 && pageNumber != null && pageNumber >= 0;
    }

    /**
     * Process where filter to MYSQL where clause syntax
     */
    public static String processWhereFilterToMYSQLWhereClause(String whereFilter) {
        if (StringUtil.isntNullNorEmpty(whereFilter)) {
            MYSQLQueryTransform mysqlQueryTransform = new MYSQLQueryTransform();
            return mysqlQueryTransform.transformQuery(whereFilter);
        }
        return whereFilter;
    }
}
