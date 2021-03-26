package com.toptal.backend.query;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class MYSQLQueryTransformTest {

    @Test
    public void getOperationsMappingTest() {
        String[][] expectedMapping = {
                {"eq", "="},
                {"ne", "!="},
                {"lt", "<"},
                {"le", "<="},
                {"gt", ">"},
                {"ge", ">="},
                {"and", "AND"},
                {"or", "OR"},
        };
        String[][] actualMapping = new MYSQLQueryTransform().getOperationsMapping();
        Assert.assertEquals(expectedMapping, actualMapping);
    }

    @DataProvider
    public static Object[][] transformQueryDataProvider() {
        MYSQLQueryTransform mysqlQueryTransform = new MYSQLQueryTransform();
        return new Object[][] {
            {null, null, mysqlQueryTransform},
            {"", "", mysqlQueryTransform},
            {"  ", "  ", mysqlQueryTransform},
            {"gteq", "gteq", mysqlQueryTransform},
            {"GT", "GT", mysqlQueryTransform},
            {"gt", ">", mysqlQueryTransform},
            {"eq", "=", mysqlQueryTransform},
            {"num gt 3 or num le 4", "num > 3 OR num <= 4", mysqlQueryTransform},
        };
    }

    @UseDataProvider("transformQueryDataProvider")
    @Test
    public void transformQueryTest(String data, String expectedResult, MYSQLQueryTransform mysqlQueryTransform) {
        String actualResult = mysqlQueryTransform.transformQuery(data);
        Assert.assertEquals(expectedResult, actualResult);
    }
}
