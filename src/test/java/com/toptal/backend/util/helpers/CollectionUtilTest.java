package com.toptal.backend.util.helpers;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class CollectionUtilTest {

    @DataProvider
    public static Object[][] isNullOrEmptyDataProvider() {
        return new Object[][]{
                {null, true},
                {Arrays.asList(), true},
                {Arrays.asList("test"), false},
        };
    }

    @UseDataProvider("isNullOrEmptyDataProvider")
    @Test
    public void isNullOrEmptyTest(List<String> list, boolean expectedResult) {
        boolean actualResult = CollectionUtil.isNullOrEmpty(list);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @DataProvider
    public static Object[][] isntNullNorEmptyDataProvider() {
        return new Object[][]{
                {null, false},
                {Arrays.asList(), false},
                {Arrays.asList("test"), true},
        };
    }

    @UseDataProvider("isntNullNorEmptyDataProvider")
    @Test
    public void isntNullNorEmptyTest(List<String> list, boolean expectedResult) {
        boolean actualResult = CollectionUtil.isntNullNorEmpty(list);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @DataProvider
    public static Object[][] transformListDataProvider() {
        return new Object[][]{
                {Arrays.asList(1, 2, 3, 4), Arrays.asList("1", "2", "3", "4")},
        };
    }

    @UseDataProvider("transformListDataProvider")
    @Test
    public void transformListTest(List<Integer> list, List<String> expectedResult) {
        List<String> actualResult = CollectionUtil.transformList(list, item -> item.intValue()+"");
        Assert.assertEquals(expectedResult, actualResult);
    }

}
