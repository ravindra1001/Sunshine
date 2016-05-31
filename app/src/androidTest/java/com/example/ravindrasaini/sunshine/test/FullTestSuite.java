package com.example.ravindrasaini.sunshine.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by ravindrasaini on 5/20/16.
 */
public class FullTestSuite extends TestSuite{
    public static Test suite(){
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }
    public FullTestSuite(){
        super();
    }
}
