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
name|function
operator|.
name|Function1
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
name|impl
operator|.
name|jdbc
operator|.
name|JdbcQueryProvider
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|test
operator|.
name|SqlOperatorTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|test
operator|.
name|SqlToRelConverterTest
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
name|Bug
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
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Fluid DSL for testing Optiq connections and queries.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|OptiqAssert
block|{
specifier|public
specifier|static
name|AssertThat
name|assertThat
parameter_list|()
block|{
return|return
operator|new
name|AssertThat
argument_list|(
name|Config
operator|.
name|REGULAR
argument_list|)
return|;
block|}
comment|/** Returns a {@link junit} suite of all Optiq tests. */
specifier|public
specifier|static
name|TestSuite
name|suite
parameter_list|()
block|{
name|TestSuite
name|testSuite
init|=
operator|new
name|TestSuite
argument_list|()
decl_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|JdbcTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|LinqFrontJdbcBackTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|JdbcFrontLinqBackTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|JdbcFrontJdbcBackLinqMiddleTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|JdbcFrontJdbcBackTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|SqlToRelConverterTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|SqlFunctionsTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|SqlOperatorTest
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|TodoFixed
condition|)
block|{
comment|// 96 failures currently
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|OptiqSqlOperatorTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|testSuite
return|;
block|}
specifier|static
name|Function1
argument_list|<
name|Throwable
argument_list|,
name|Void
argument_list|>
name|checkException
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|Throwable
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|Throwable
name|p0
parameter_list|)
block|{
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
decl_stmt|;
name|p0
operator|.
name|printStackTrace
argument_list|(
name|printWriter
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|stack
init|=
name|stringWriter
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stack
argument_list|,
name|stack
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
specifier|static
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|checkResult
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|p0
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|p0
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|checkResultContains
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|p0
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|p0
argument_list|,
name|p0
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
specifier|static
name|void
name|assertQuery
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|String
name|sql
parameter_list|,
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|resultChecker
parameter_list|,
name|Function1
argument_list|<
name|Throwable
argument_list|,
name|Void
argument_list|>
name|exceptionChecker
parameter_list|)
throws|throws
name|Exception
block|{
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
decl_stmt|;
try|try
block|{
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
if|if
condition|(
name|exceptionChecker
operator|!=
literal|null
condition|)
block|{
name|exceptionChecker
operator|.
name|apply
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|exceptionChecker
operator|!=
literal|null
condition|)
block|{
name|exceptionChecker
operator|.
name|apply
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
if|if
condition|(
name|exceptionChecker
operator|!=
literal|null
condition|)
block|{
name|exceptionChecker
operator|.
name|apply
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
throw|throw
name|e
throw|;
block|}
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|n
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnLabel
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|resultSet
operator|.
name|getObject
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|n
condition|)
block|{
break|break;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|resultChecker
operator|!=
literal|null
condition|)
block|{
name|resultChecker
operator|.
name|apply
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Result of calling {@link OptiqAssert#assertThat}.      */
specifier|public
specifier|static
class|class
name|AssertThat
block|{
specifier|private
specifier|final
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|AssertThat
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ConfigConnectionFactory
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|AssertThat
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|AssertThat
name|with
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
return|return
operator|new
name|AssertThat
argument_list|(
name|config
argument_list|)
return|;
block|}
specifier|public
name|AssertThat
name|with
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
block|{
return|return
operator|new
name|AssertThat
argument_list|(
name|connectionFactory
argument_list|)
return|;
block|}
specifier|public
name|AssertQuery
name|query
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sql
argument_list|)
expr_stmt|;
return|return
operator|new
name|AssertQuery
argument_list|(
name|connectionFactory
argument_list|,
name|sql
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|doWithConnection
parameter_list|(
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|T
argument_list|>
name|fn
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|fn
operator|.
name|apply
argument_list|(
operator|(
name|OptiqConnection
operator|)
name|connection
argument_list|)
return|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|AssertThat
name|withSchema
parameter_list|(
name|String
name|schema
parameter_list|)
block|{
return|return
operator|new
name|AssertThat
argument_list|(
operator|new
name|SchemaConnectionFactory
argument_list|(
name|connectionFactory
argument_list|,
name|schema
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|public
interface|interface
name|ConnectionFactory
block|{
name|OptiqConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
specifier|private
specifier|static
class|class
name|ConfigConnectionFactory
implements|implements
name|ConnectionFactory
block|{
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
specifier|public
name|ConfigConnectionFactory
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|OptiqConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|config
condition|)
block|{
case|case
name|REGULAR
case|:
return|return
name|JdbcTest
operator|.
name|getConnection
argument_list|(
literal|"hr"
argument_list|,
literal|"foodmart"
argument_list|)
return|;
case|case
name|REGULAR_PLUS_METADATA
case|:
return|return
name|JdbcTest
operator|.
name|getConnection
argument_list|(
literal|"hr"
argument_list|,
literal|"foodmart"
argument_list|,
literal|"metadata"
argument_list|)
return|;
case|case
name|JDBC_FOODMART2
case|:
return|return
name|JdbcTest
operator|.
name|getConnection
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|JDBC_FOODMART
case|:
return|return
name|JdbcTest
operator|.
name|getConnection
argument_list|(
name|JdbcQueryProvider
operator|.
name|INSTANCE
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|FOODMART_CLONE
case|:
return|return
name|JdbcTest
operator|.
name|getConnection
argument_list|(
name|JdbcQueryProvider
operator|.
name|INSTANCE
argument_list|,
literal|true
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|config
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|DelegatingConnectionFactory
implements|implements
name|ConnectionFactory
block|{
specifier|private
specifier|final
name|ConnectionFactory
name|factory
decl_stmt|;
specifier|public
name|DelegatingConnectionFactory
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
specifier|public
name|OptiqConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|SchemaConnectionFactory
extends|extends
name|DelegatingConnectionFactory
block|{
specifier|private
specifier|final
name|String
name|schema
decl_stmt|;
specifier|public
name|SchemaConnectionFactory
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|,
name|String
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|OptiqConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|OptiqConnection
name|connection
init|=
name|super
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
name|schema
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|AssertQuery
block|{
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|AssertQuery
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|connectionFactory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|void
name|returns
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
name|returns
argument_list|(
name|checkResult
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|returns
parameter_list|(
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|checker
parameter_list|)
block|{
try|try
block|{
name|assertQuery
argument_list|(
name|createConnection
argument_list|()
argument_list|,
name|sql
argument_list|,
name|checker
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception while executing ["
operator|+
name|sql
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|throws_
parameter_list|(
name|String
name|message
parameter_list|)
block|{
try|try
block|{
name|assertQuery
argument_list|(
name|createConnection
argument_list|()
argument_list|,
name|sql
argument_list|,
literal|null
argument_list|,
name|checkException
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception while executing ["
operator|+
name|sql
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|runs
parameter_list|()
block|{
try|try
block|{
name|assertQuery
argument_list|(
name|createConnection
argument_list|()
argument_list|,
name|sql
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception while executing ["
operator|+
name|sql
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
enum|enum
name|Config
block|{
comment|/**          * Configuration that creates a connection with two in-memory data sets:          * {@link net.hydromatic.optiq.test.JdbcTest.HrSchema} and          * {@link net.hydromatic.optiq.test.JdbcTest.FoodmartSchema}.          */
name|REGULAR
block|,
comment|/**          * Configuration that creates a connection to a MySQL server. Tables          * such as "customer" and "sales_fact_1997" are available. Queries          * are processed by generating Java that calls linq4j operators          * such as          * {@link net.hydromatic.linq4j.Enumerable#where(net.hydromatic.linq4j.function.Predicate1)}.          */
name|JDBC_FOODMART
block|,
name|JDBC_FOODMART2
block|,
comment|/** Configuration that contains an in-memory clone of the FoodMart          * database. */
name|FOODMART_CLONE
block|,
comment|/** Configuration that includes the metadata schema. */
name|REGULAR_PLUS_METADATA
block|,     }
block|}
end_class

begin_comment
comment|// End OptiqAssert.java
end_comment

end_unit

