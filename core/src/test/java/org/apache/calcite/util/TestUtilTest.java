begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for TestUtil  */
end_comment

begin_class
specifier|public
class|class
name|TestUtilTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|javaMajorVersionExceeds6
parameter_list|()
block|{
comment|// shouldn't throw any exceptions (for current JDK)
name|int
name|majorVersion
init|=
name|TestUtil
operator|.
name|getJavaMajorVersion
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"current JavaMajorVersion == "
operator|+
name|majorVersion
operator|+
literal|" is expected to exceed 6"
argument_list|,
name|majorVersion
operator|>
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|majorVersionFromString
parameter_list|()
block|{
name|testJavaVersion
argument_list|(
literal|4
argument_list|,
literal|"1.4.2_03"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|5
argument_list|,
literal|"1.5.0_16"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|6
argument_list|,
literal|"1.6.0_22"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|7
argument_list|,
literal|"1.7.0_65-b20"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|8
argument_list|,
literal|"1.8.0_72-internal"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|8
argument_list|,
literal|"1.8.0_151"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|8
argument_list|,
literal|"1.8.0_141"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"1.9.0_20-b62"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"1.9.0-ea-b19"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9.0.1"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9-ea"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9.0.1"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9.1-ea"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9.1.1-ea"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|9
argument_list|,
literal|"9.1.1-ea+123"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10+456"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10-ea"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10-ea42"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10-ea+555"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10-ea42+555"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10.0.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10.0.0.0.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10.1.2.3.4.5.6.7.8"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10.0.1"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|10
argument_list|,
literal|"10.1.1-foo"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|11
argument_list|,
literal|"11"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|11
argument_list|,
literal|"11+111"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|11
argument_list|,
literal|"11-ea"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|11
argument_list|,
literal|"11.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|12
argument_list|,
literal|"12.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|20
argument_list|,
literal|"20.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|42
argument_list|,
literal|"42"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|100
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|100
argument_list|,
literal|"100.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|1000
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|2000
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|205
argument_list|,
literal|"205.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|2017
argument_list|,
literal|"2017"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|2017
argument_list|,
literal|"2017.0"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|2017
argument_list|,
literal|"2017.12"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|2017
argument_list|,
literal|"2017.12-pre"
argument_list|)
expr_stmt|;
name|testJavaVersion
argument_list|(
literal|2017
argument_list|,
literal|"2017.12.31"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testJavaVersion
parameter_list|(
name|int
name|expectedMajorVersion
parameter_list|,
name|String
name|versionString
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|versionString
argument_list|,
name|expectedMajorVersion
argument_list|,
name|TestUtil
operator|.
name|majorVersionFromString
argument_list|(
name|versionString
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End TestUtilTest.java
end_comment

end_unit
