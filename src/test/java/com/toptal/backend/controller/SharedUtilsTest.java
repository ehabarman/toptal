package com.toptal.backend.controller;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class SharedUtilsTest {

    @DataProvider
    public static Object[][] isValidForPaginationDataProvider() {
        return new Object[][]{
                {null, null, false},
                {null, 10, false},
                {10, null, false},
                {-5, 5, false},
                {5, -5, false},
                {0, 0, false},
                {0, 10, false},
                {1, 0, true},
                {5, 5, true},
        };
    }

    @UseDataProvider("isValidForPaginationDataProvider")
    @Test
    public void isValidForPaginationTest(Integer pageSize, Integer pageNumber, boolean expected) {
        boolean actualResult = SharedUtils.isValidForPagination(pageSize, pageNumber);
        Assert.assertEquals(expected, actualResult);
    }

    @DataProvider
    public static Object[][] processWhereFilterToMYSQLWhereClauseDataProvider() {
        return new Object[][]{
                {null, null},
                {"", ""},
                {"test gt 5", "test > 5"},
                {"test ge 5", "test >= 5"},
                {"test lt 5", "test < 5"},
                {"test le 5", "test <= 5"},
                {"test eq 5", "test = 5"},
                {"test ne 5", "test != 5"},
                {"(test > 5)", "(test > 5)"},
        };
    }

    @UseDataProvider("processWhereFilterToMYSQLWhereClauseDataProvider")
    @Test
    public void processWhereFilterToMYSQLWhereClauseTest(String data, String expected) {
        String actualResult = SharedUtils.processWhereFilterToMYSQLWhereClause(data);
        Assert.assertEquals(expected, actualResult);
    }
}
