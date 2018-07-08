/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/testData/debugger/fileRanking")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class FileRankingTestGenerated extends AbstractFileRankingTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, TargetBackend.ANY, testDataFilePath);
    }

    public void testAllFilesPresentInFileRanking() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/debugger/fileRanking"), Pattern.compile("^(.+)\\.kt$"), TargetBackend.ANY, true);
    }

    @TestMetadata("differentFlags.kt")
    public void testDifferentFlags() throws Exception {
        runTest("idea/testData/debugger/fileRanking/differentFlags.kt");
    }

    @TestMetadata("init.kt")
    public void testInit() throws Exception {
        runTest("idea/testData/debugger/fileRanking/init.kt");
    }

    @TestMetadata("lambdas.kt")
    public void testLambdas() throws Exception {
        runTest("idea/testData/debugger/fileRanking/lambdas.kt");
    }

    @TestMetadata("multilinePrimaryConstructor.kt")
    public void testMultilinePrimaryConstructor() throws Exception {
        runTest("idea/testData/debugger/fileRanking/multilinePrimaryConstructor.kt");
    }

    @TestMetadata("multilinePrimaryConstructorWithBody.kt")
    public void testMultilinePrimaryConstructorWithBody() throws Exception {
        runTest("idea/testData/debugger/fileRanking/multilinePrimaryConstructorWithBody.kt");
    }

    @TestMetadata("propertyDelegates.kt")
    public void testPropertyDelegates() throws Exception {
        runTest("idea/testData/debugger/fileRanking/propertyDelegates.kt");
    }

    @TestMetadata("sameClassName.kt")
    public void testSameClassName() throws Exception {
        runTest("idea/testData/debugger/fileRanking/sameClassName.kt");
    }

    @TestMetadata("sameClassNameDifferentMethodNames.kt")
    public void testSameClassNameDifferentMethodNames() throws Exception {
        runTest("idea/testData/debugger/fileRanking/sameClassNameDifferentMethodNames.kt");
    }

    @TestMetadata("simple.kt")
    public void testSimple() throws Exception {
        runTest("idea/testData/debugger/fileRanking/simple.kt");
    }

    @TestMetadata("topLevel.kt")
    public void testTopLevel() throws Exception {
        runTest("idea/testData/debugger/fileRanking/topLevel.kt");
    }
}
