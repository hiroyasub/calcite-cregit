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
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|CalciteSystemProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Ordering
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
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
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Util class which needs to be in the same package as {@link CalciteAssert}  * due to package-private visibility.  */
end_comment

begin_class
specifier|public
class|class
name|MongoAssertions
block|{
specifier|private
name|MongoAssertions
parameter_list|()
block|{
block|}
comment|/**    * Similar to {@link CalciteAssert#checkResultUnordered}, but filters strings    * before comparing them.    *    * @param lines Expected expressions    * @return validation function    */
specifier|public
specifier|static
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|checkResultUnordered
parameter_list|(
specifier|final
name|String
modifier|...
name|lines
parameter_list|)
block|{
return|return
name|resultSet
lambda|->
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedList
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|immutableSortedCopy
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|lines
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|toStringList
argument_list|(
name|resultSet
argument_list|,
name|actualList
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|actualList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|actualList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|actualList
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\\.0;"
argument_list|,
literal|";"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\.0$"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|immutableSortedCopy
argument_list|(
name|actualList
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedList
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|;
block|}
comment|/**    * Whether to run Mongo integration tests. Enabled by default, however test is only    * included if "it" profile is activated ({@code -Pit}). To disable,    * specify {@code -Dcalcite.test.mongodb=false} on the Java command line.    *    * @return Whether current tests should use an external mongo instance    */
specifier|public
specifier|static
name|boolean
name|useMongo
parameter_list|()
block|{
return|return
name|CalciteSystemProperty
operator|.
name|INTEGRATION_TEST
operator|.
name|value
argument_list|()
operator|&&
name|CalciteSystemProperty
operator|.
name|TEST_MONGODB
operator|.
name|value
argument_list|()
return|;
block|}
comment|/**    * Checks wherever tests should use Fongo instead of Mongo. Opposite of {@link #useMongo()}.    *    * @return Whether current tests should use embedded    *<a href="https://github.com/fakemongo/fongo">Fongo</a> instance    */
specifier|public
specifier|static
name|boolean
name|useFongo
parameter_list|()
block|{
return|return
operator|!
name|useMongo
argument_list|()
return|;
block|}
comment|/**    * Used to skip tests if current instance is not mongo. Some functionalities    * are not available in fongo.    *    * @see<a href="https://github.com/fakemongo/fongo/issues/152">Aggregation with $cond (172)</a>    */
specifier|public
specifier|static
name|void
name|assumeRealMongoInstance
parameter_list|()
block|{
name|assumeTrue
argument_list|(
literal|"Expect mongo instance"
argument_list|,
name|useMongo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MongoAssertions.java
end_comment

end_unit

