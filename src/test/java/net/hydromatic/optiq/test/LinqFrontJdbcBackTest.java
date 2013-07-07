begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expressions
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|ParameterExpression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Predicate1
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|Schema
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Util
import|;
end_import

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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|JUnit4
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests for a linq4j front-end and JDBC back-end.  */
end_comment

begin_class
specifier|public
class|class
name|LinqFrontJdbcBackTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testTableWhere
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
specifier|final
name|OptiqConnection
name|connection
init|=
name|JdbcTest
operator|.
name|getConnection
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Schema
name|schema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|getSubSchema
argument_list|(
literal|"foodmart"
argument_list|)
decl_stmt|;
name|ParameterExpression
name|c
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|JdbcTest
operator|.
name|Customer
operator|.
name|class
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|schema
operator|.
name|getTable
argument_list|(
literal|"customer"
argument_list|,
name|JdbcTest
operator|.
name|Customer
operator|.
name|class
argument_list|)
operator|.
name|where
argument_list|(
name|Expressions
operator|.
expr|<
name|Predicate1
argument_list|<
name|JdbcTest
operator|.
name|Customer
argument_list|>
operator|>
name|lambda
argument_list|(
name|Expressions
operator|.
name|lessThan
argument_list|(
name|Expressions
operator|.
name|field
argument_list|(
name|c
argument_list|,
literal|"customer_id"
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|,
name|c
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End LinqFrontJdbcBackTest.java
end_comment

end_unit

