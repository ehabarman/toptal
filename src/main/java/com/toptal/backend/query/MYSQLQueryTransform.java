package com.toptal.backend.query;

/**
 * Query transformer for mysql database
 *
 * @author ehab
 */
public class MYSQLQueryTransform extends QueryTransform {

    @Override
    public String[][] getOperationsMapping() {
        return new String[][]{
                {"eq", "="},
                {"ne", "!="},
                {"lt", "<"},
                {"le", "<="},
                {"gt", ">"},
                {"ge", ">="},
                {"and", "AND"},
                {"or", "OR"},
        };
    }
}
