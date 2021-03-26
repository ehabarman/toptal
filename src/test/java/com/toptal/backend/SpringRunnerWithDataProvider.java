package com.toptal.backend;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderFilter;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.java.junit.dataprovider.internal.TestGenerator;
import com.tngtech.java.junit.dataprovider.internal.TestValidator;

/**
 * A custom runner for JUnit that allows the usage of data providers + spring runner.
 *
 * @Author Ehab Arman
 */
public class SpringRunnerWithDataProvider extends SpringJUnit4ClassRunner {

    // Junit helpers
    protected DataConverter dataConverter;
    protected TestGenerator testGenerator;
    protected TestValidator testValidator;
    List<FrameworkMethod> computedTestMethods;

    /**
     * Constructor copied from {@link SpringRunner}
     *
     * @param clazz
     * @throws InitializationError
     */
    public SpringRunnerWithDataProvider(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        initializeHelpers();
        super.collectInitializationErrors(errors);
    }

    protected void initializeHelpers() {
        dataConverter = new DataConverter();
        testGenerator = new TestGenerator(dataConverter);
        testValidator = new TestValidator(dataConverter);
    }

    @Override
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);
        if (errors.isEmpty() && computeTestMethods().size() == 0) {
            errors.add(new Exception("No runnable methods"));
        }
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(Test.class)) {
            testValidator.validateTestMethod(testMethod, errors);
        }
        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(UseDataProvider.class)) {
            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
            if (dataProviderMethod == null) {
                errors.add(new Exception(String.format("No valid dataprovider found for test %s.", testMethod.getName())));

            } else {
                DataProvider dataProvider = dataProviderMethod.getAnnotation(DataProvider.class);
                if (dataProvider == null) {
                    throw new IllegalStateException(String.format("@%s annotaion not found on dataprovider method %s",
                            DataProvider.class.getSimpleName(), dataProviderMethod.getName()));
                }
                testValidator.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);
            }
        }
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        if (computedTestMethods == null) {
            computedTestMethods = generateExplodedTestMethodsFor(super.computeTestMethods());
        }
        return computedTestMethods;
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        super.filter(new DataProviderFilter(filter));
    }

    TestClass getTestClassInt() {
        return getTestClass();
    }

    List<FrameworkMethod> generateExplodedTestMethodsFor(List<FrameworkMethod> testMethods) {
        List<FrameworkMethod> result = new ArrayList<>();
        if (testMethods == null) {
            return result;
        }
        for (FrameworkMethod testMethod : testMethods) {
            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
            result.addAll(testGenerator.generateExplodedTestMethodsFor(testMethod, dataProviderMethod));
        }
        return result;
    }

    FrameworkMethod getDataProviderMethod(FrameworkMethod testMethod) {
        UseDataProvider useDataProvider = testMethod.getAnnotation(UseDataProvider.class);
        if (useDataProvider == null) {
            return null;
        }

        TestClass dataProviderLocation = findDataProviderLocation(useDataProvider);
        List<FrameworkMethod> dataProviderMethods = dataProviderLocation.getAnnotatedMethods(DataProvider.class);
        return findDataProviderMethod(dataProviderMethods, useDataProvider.value(), testMethod.getName());
    }

    TestClass findDataProviderLocation(UseDataProvider useDataProvider) {
        if (useDataProvider.location().length == 0) {
            return getTestClassInt();
        }
        return new TestClass(useDataProvider.location()[0]);
    }

    private FrameworkMethod findDataProviderMethod(List<FrameworkMethod> dataProviderMethods, String useDataProviderValue, String testMethodName) {
        if (!UseDataProvider.DEFAULT_VALUE.equals(useDataProviderValue)) {
            return findMethod(dataProviderMethods, useDataProviderValue);
        }
        return findMethod(dataProviderMethods, testMethodName);
    }

    private FrameworkMethod findMethod(List<FrameworkMethod> methods, String methodName) {
        for (FrameworkMethod method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}