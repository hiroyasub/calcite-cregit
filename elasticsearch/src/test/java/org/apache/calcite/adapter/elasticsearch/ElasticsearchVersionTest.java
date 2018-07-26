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
name|adapter
operator|.
name|elasticsearch
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
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Basic tests for parsing ES version in different formats  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchVersionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|versions
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
literal|"2.3.4"
argument_list|)
argument_list|,
name|ElasticsearchVersion
operator|.
name|ES2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
literal|"2.0.0"
argument_list|)
argument_list|,
name|ElasticsearchVersion
operator|.
name|ES2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
literal|"5.6.1"
argument_list|)
argument_list|,
name|ElasticsearchVersion
operator|.
name|ES5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
literal|"6.0.1"
argument_list|)
argument_list|,
name|ElasticsearchVersion
operator|.
name|ES6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
literal|"7.0.1"
argument_list|)
argument_list|,
name|ElasticsearchVersion
operator|.
name|ES7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
literal|"111.0.1"
argument_list|)
argument_list|,
name|ElasticsearchVersion
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
literal|"2020.12.12"
argument_list|)
argument_list|,
name|ElasticsearchVersion
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|".1.2"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"1.2"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"a.b"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"aa"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"a.b.c"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"2.2"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"a.2"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"2.2.0a"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
literal|"2a.2.0"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertFails
parameter_list|(
name|String
name|version
parameter_list|)
block|{
try|try
block|{
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Should fail for version %s"
argument_list|,
name|version
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ignore
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

begin_comment
comment|// End ElasticsearchVersionTest.java
end_comment

end_unit
