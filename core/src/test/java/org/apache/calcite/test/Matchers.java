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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|CustomTypeSafeMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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

begin_comment
comment|/**  * Matchers for testing SQL queries.  */
end_comment

begin_class
specifier|public
class|class
name|Matchers
block|{
specifier|private
name|Matchers
parameter_list|()
block|{
block|}
comment|/** Allows passing the actual result from the {@code matchesSafely} method to    * the {@code describeMismatchSafely} method that will show the difference. */
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Object
argument_list|>
name|THREAD_ACTUAL
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Creates a matcher that matches if the examined result set returns the    * given collection of rows in some order.    *    *<p>Closes the result set after reading.    *    *<p>For example:    *<pre>assertThat(statement.executeQuery("select empno from emp"),    *   returnsUnordered("empno=1234", "empno=100"));</pre>    */
specifier|public
specifier|static
name|Matcher
argument_list|<
name|?
super|super
name|ResultSet
argument_list|>
name|returnsUnordered
parameter_list|(
name|String
modifier|...
name|lines
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|lines
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expectedList
argument_list|)
expr_stmt|;
return|return
operator|new
name|CustomTypeSafeMatcher
argument_list|<
name|ResultSet
argument_list|>
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|lines
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|describeMismatchSafely
parameter_list|(
name|ResultSet
name|item
parameter_list|,
name|Description
name|description
parameter_list|)
block|{
specifier|final
name|Object
name|value
init|=
name|THREAD_ACTUAL
operator|.
name|get
argument_list|()
decl_stmt|;
name|THREAD_ACTUAL
operator|.
name|remove
argument_list|()
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"was "
argument_list|)
operator|.
name|appendValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
name|CalciteAssert
operator|.
name|toStringList
argument_list|(
name|resultSet
argument_list|,
name|actualList
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
name|THREAD_ACTUAL
operator|.
name|set
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|equals
init|=
name|actualList
operator|.
name|equals
argument_list|(
name|expectedList
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|equals
condition|)
block|{
name|THREAD_ACTUAL
operator|.
name|set
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
block|}
return|return
name|equals
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End Matchers.java
end_comment

end_unit

