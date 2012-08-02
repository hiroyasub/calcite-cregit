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
name|*
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
name|*
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
name|Types
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
name|Function1
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
name|*
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
name|java
operator|.
name|JavaTypeFactory
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
name|java
operator|.
name|MapSchema
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
name|JdbcSchema
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
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|dbcp
operator|.
name|BasicDataSource
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
name|SqlDialect
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import static
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|OptiqAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for using Optiq via JDBC.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|JdbcTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
specifier|final
name|Method
name|LINQ4J_AS_ENUMERABLE_METHOD
init|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|Linq4j
operator|.
name|class
argument_list|,
literal|"asEnumerable"
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Method
name|GENERATE_STRINGS_METHOD
init|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|JdbcTest
operator|.
name|class
argument_list|,
literal|"generateStrings"
argument_list|,
name|Integer
operator|.
name|TYPE
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Method
name|STRING_UNION_METHOD
init|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|JdbcTest
operator|.
name|class
argument_list|,
literal|"stringUnion"
argument_list|,
name|Queryable
operator|.
name|class
argument_list|,
name|Queryable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
name|String
name|toString
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
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
name|String
name|sep
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|sep
argument_list|)
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
name|sep
operator|=
literal|"; "
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
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Test that uses a JDBC connection as a linq4j {@link QueryProvider}.      *      * @throws Exception on error      */
specifier|public
name|void
name|testQueryProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|getConnectionWithHrFoodmart
argument_list|()
decl_stmt|;
name|QueryProvider
name|queryProvider
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|QueryProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ParameterExpression
name|e
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Employee
operator|.
name|class
argument_list|,
literal|"e"
argument_list|)
decl_stmt|;
comment|// "Enumerable<T> asEnumerable(final T[] ts)"
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|list
init|=
name|queryProvider
operator|.
name|createQuery
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|Types
operator|.
name|of
argument_list|(
name|Enumerable
operator|.
name|class
argument_list|,
name|Employee
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
name|LINQ4J_AS_ENUMERABLE_METHOD
argument_list|,
name|Arrays
operator|.
expr|<
name|Expression
operator|>
name|asList
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
operator|new
name|HrSchema
argument_list|()
operator|.
name|emps
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"asQueryable"
argument_list|,
name|Collections
operator|.
expr|<
name|Expression
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|,
name|Employee
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
name|Employee
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
name|e
argument_list|,
literal|"empid"
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|160
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
operator|.
name|where
argument_list|(
name|Expressions
operator|.
expr|<
name|Predicate1
argument_list|<
name|Employee
argument_list|>
operator|>
name|lambda
argument_list|(
name|Expressions
operator|.
name|greaterThan
argument_list|(
name|Expressions
operator|.
name|field
argument_list|(
name|e
argument_list|,
literal|"empid"
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|140
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
operator|.
name|select
argument_list|(
name|Expressions
operator|.
expr|<
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Object
index|[]
argument_list|>
operator|>
name|lambda
argument_list|(
name|Expressions
operator|.
name|new_
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|,
name|Arrays
operator|.
expr|<
name|Expression
operator|>
name|asList
argument_list|(
name|Expressions
operator|.
name|field
argument_list|(
name|e
argument_list|,
literal|"empid"
argument_list|)
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|field
argument_list|(
name|e
argument_list|,
literal|"name"
argument_list|)
argument_list|,
literal|"toUpperCase"
argument_list|,
name|Collections
operator|.
expr|<
name|Expression
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|150
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"SEBASTIAN"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testQueryProviderSingleColumn
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|getConnectionWithHrFoodmart
argument_list|()
decl_stmt|;
name|QueryProvider
name|queryProvider
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|QueryProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ParameterExpression
name|e
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Employee
operator|.
name|class
argument_list|,
literal|"e"
argument_list|)
decl_stmt|;
comment|// "Enumerable<T> asEnumerable(final T[] ts)"
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|queryProvider
operator|.
name|createQuery
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|Types
operator|.
name|of
argument_list|(
name|Enumerable
operator|.
name|class
argument_list|,
name|Employee
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
name|LINQ4J_AS_ENUMERABLE_METHOD
argument_list|,
name|Arrays
operator|.
expr|<
name|Expression
operator|>
name|asList
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
operator|new
name|HrSchema
argument_list|()
operator|.
name|emps
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"asQueryable"
argument_list|,
name|Collections
operator|.
expr|<
name|Expression
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|,
name|Employee
operator|.
name|class
argument_list|)
operator|.
name|select
argument_list|(
name|Expressions
operator|.
expr|<
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
operator|>
name|lambda
argument_list|(
name|Expressions
operator|.
name|new_
argument_list|(
name|AnInt
operator|.
name|class
argument_list|,
name|Arrays
operator|.
expr|<
name|Expression
operator|>
name|asList
argument_list|(
name|Expressions
operator|.
name|field
argument_list|(
name|e
argument_list|,
literal|"empid"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|,
literal|150
argument_list|)
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests a relation that is accessed via method syntax.      * The function returns a {@link Queryable}.      */
specifier|public
name|void
name|testFunction
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|JavaTypeFactory
name|typeFactory
init|=
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|MapSchema
name|schema
init|=
operator|new
name|MapSchema
argument_list|(
name|optiqConnection
argument_list|,
name|typeFactory
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|addTableFunction
argument_list|(
literal|"GenerateStrings"
argument_list|,
name|Schemas
operator|.
name|methodMember
argument_list|(
name|GENERATE_STRINGS_METHOD
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
expr_stmt|;
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|addSchema
argument_list|(
literal|"s"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(s.GenerateStrings(5))\n"
operator|+
literal|"where char_length(s)> 3"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|stringUnion
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|q0
parameter_list|,
name|Queryable
argument_list|<
name|T
argument_list|>
name|q1
parameter_list|)
block|{
return|return
name|q0
operator|.
name|concat
argument_list|(
name|q1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Queryable
argument_list|<
name|IntString
argument_list|>
name|generateStrings
parameter_list|(
specifier|final
name|int
name|count
parameter_list|)
block|{
return|return
operator|new
name|BaseQueryable
argument_list|<
name|IntString
argument_list|>
argument_list|(
literal|null
argument_list|,
name|IntString
operator|.
name|class
argument_list|,
literal|null
argument_list|)
block|{
specifier|public
name|Enumerator
argument_list|<
name|IntString
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|Enumerator
argument_list|<
name|IntString
argument_list|>
argument_list|()
block|{
specifier|static
specifier|final
name|String
name|z
init|=
literal|"abcdefghijklm"
decl_stmt|;
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
name|IntString
name|o
decl_stmt|;
specifier|public
name|IntString
name|current
parameter_list|()
block|{
return|return
name|o
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
name|i
operator|<
name|count
operator|-
literal|1
condition|)
block|{
name|o
operator|=
operator|new
name|IntString
argument_list|(
name|i
argument_list|,
name|z
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
operator|%
name|z
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|i
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/**      * Tests a relation that is accessed via method syntax.      * The function returns a {@link Queryable}.      */
specifier|public
name|void
name|testOperator
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|JavaTypeFactory
name|typeFactory
init|=
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|MapSchema
name|schema
init|=
operator|new
name|MapSchema
argument_list|(
name|optiqConnection
argument_list|,
name|typeFactory
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|addTableFunction
argument_list|(
literal|"GenerateStrings"
argument_list|,
name|Schemas
operator|.
name|methodMember
argument_list|(
name|GENERATE_STRINGS_METHOD
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addTableFunction
argument_list|(
literal|"StringUnion"
argument_list|,
name|Schemas
operator|.
name|methodMember
argument_list|(
name|STRING_UNION_METHOD
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
expr_stmt|;
name|MutableSchema
name|rootSchema
init|=
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|addSchema
argument_list|(
literal|"s"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|rootSchema
operator|.
name|addReflectiveSchema
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(s.StringUnion(\n"
operator|+
literal|"  GenerateStrings(5),\n"
operator|+
literal|"  cursor (select name from emps)))\n"
operator|+
literal|"where char_length(s)> 3"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests a view.      */
specifier|public
name|void
name|testView
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|JavaTypeFactory
name|typeFactory
init|=
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|MapSchema
name|schema
init|=
operator|new
name|MapSchema
argument_list|(
name|optiqConnection
argument_list|,
name|typeFactory
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|addTableFunction
argument_list|(
literal|"emps_view"
argument_list|,
name|viewFunction
argument_list|(
name|typeFactory
argument_list|,
literal|"emps_view"
argument_list|,
literal|"select * from \"hr\".\"emps\""
argument_list|)
argument_list|)
expr_stmt|;
name|MutableSchema
name|rootSchema
init|=
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|addSchema
argument_list|(
literal|"s"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|rootSchema
operator|.
name|addReflectiveSchema
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"emps_view\""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|TableFunction
argument_list|<
name|T
argument_list|>
name|viewFunction
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
name|String
name|viewSql
parameter_list|)
block|{
return|return
operator|new
name|TableFunction
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|Parameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
comment|// TODO: return type returned from validation
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Table
argument_list|<
name|T
argument_list|>
name|apply
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
comment|// TODO: return a Queryable that wraps the parse tree
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
specifier|static
name|Connection
name|getConnectionWithHrFoodmart
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|MutableSchema
name|rootSchema
init|=
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|addReflectiveSchema
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
expr_stmt|;
name|rootSchema
operator|.
name|addReflectiveSchema
argument_list|(
literal|"foodmart"
argument_list|,
operator|new
name|FoodmartSchema
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
comment|/**      * Creates a connection with a given query provider. If provider is null,      * uses the connection as its own provider. The connection contains a      * schema called "foodmart" backed by a JDBC connection to MySQL.      *      * @param queryProvider Query provider      * @return Connection      * @throws ClassNotFoundException      * @throws SQLException      */
specifier|static
name|OptiqConnection
name|getConnection
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"com.mysql.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|BasicDataSource
name|dataSource
init|=
operator|new
name|BasicDataSource
argument_list|()
decl_stmt|;
name|dataSource
operator|.
name|setUrl
argument_list|(
literal|"jdbc:mysql://localhost"
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setUsername
argument_list|(
literal|"foodmart"
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setPassword
argument_list|(
literal|"foodmart"
argument_list|)
expr_stmt|;
comment|// FIXME: Sub-schema should not need to build its own expression.
specifier|final
name|Expression
name|expression
init|=
name|Expressions
operator|.
name|call
argument_list|(
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|getExpression
argument_list|()
argument_list|,
literal|"getSubSchema"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|"foodmart"
argument_list|)
argument_list|)
decl_stmt|;
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|addSchema
argument_list|(
literal|"foodmart"
argument_list|,
operator|new
name|JdbcSchema
argument_list|(
name|queryProvider
operator|==
literal|null
condition|?
name|optiqConnection
else|:
name|queryProvider
argument_list|,
name|dataSource
argument_list|,
name|JdbcSchema
operator|.
name|createDialect
argument_list|(
name|dataSource
argument_list|)
argument_list|,
literal|"foodmart"
argument_list|,
literal|""
argument_list|,
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|expression
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|optiqConnection
return|;
block|}
specifier|public
specifier|static
class|class
name|HrSchema
block|{
specifier|public
specifier|final
name|Employee
index|[]
name|emps
init|=
block|{
operator|new
name|Employee
argument_list|(
literal|100
argument_list|,
literal|10
argument_list|,
literal|"Bill"
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|200
argument_list|,
literal|20
argument_list|,
literal|"Eric"
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|150
argument_list|,
literal|10
argument_list|,
literal|"Sebastian"
argument_list|)
block|,         }
decl_stmt|;
specifier|public
specifier|final
name|Department
index|[]
name|depts
init|=
block|{
operator|new
name|Department
argument_list|(
literal|10
argument_list|,
literal|"Sales"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|30
argument_list|,
literal|"Marketing"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|40
argument_list|,
literal|"HR"
argument_list|)
block|,         }
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|Employee
block|{
specifier|public
specifier|final
name|int
name|empid
decl_stmt|;
specifier|public
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|Employee
parameter_list|(
name|int
name|empid
parameter_list|,
name|int
name|deptno
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|empid
operator|=
name|empid
expr_stmt|;
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|Department
block|{
specifier|public
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|Department
parameter_list|(
name|int
name|deptno
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|FoodmartSchema
block|{
specifier|public
specifier|final
name|SalesFact
index|[]
name|sales_fact_1997
init|=
block|{
operator|new
name|SalesFact
argument_list|(
literal|100
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|SalesFact
argument_list|(
literal|150
argument_list|,
literal|20
argument_list|)
block|,         }
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|FoodmartJdbcSchema
extends|extends
name|JdbcSchema
block|{
specifier|public
name|FoodmartJdbcSchema
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|DataSource
name|dataSource
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|queryProvider
argument_list|,
name|dataSource
argument_list|,
name|dialect
argument_list|,
name|catalog
argument_list|,
name|schema
argument_list|,
name|typeFactory
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|Table
argument_list|<
name|Customer
argument_list|>
name|customer
init|=
name|getTable
argument_list|(
literal|"customer"
argument_list|,
name|Customer
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|Customer
block|{
specifier|public
specifier|final
name|int
name|customer_id
decl_stmt|;
specifier|public
name|Customer
parameter_list|(
name|int
name|customer_id
parameter_list|)
block|{
name|this
operator|.
name|customer_id
operator|=
name|customer_id
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|SalesFact
block|{
specifier|public
specifier|final
name|int
name|cust_id
decl_stmt|;
specifier|public
specifier|final
name|int
name|prod_id
decl_stmt|;
specifier|public
name|SalesFact
parameter_list|(
name|int
name|cust_id
parameter_list|,
name|int
name|prod_id
parameter_list|)
block|{
name|this
operator|.
name|cust_id
operator|=
name|cust_id
expr_stmt|;
name|this
operator|.
name|prod_id
operator|=
name|prod_id
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|AnInt
block|{
specifier|public
specifier|final
name|int
name|n
decl_stmt|;
specifier|public
name|AnInt
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|IntString
block|{
specifier|public
specifier|final
name|int
name|n
decl_stmt|;
specifier|public
specifier|final
name|String
name|s
decl_stmt|;
specifier|public
name|IntString
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{n="
operator|+
name|n
operator|+
literal|", s="
operator|+
name|s
operator|+
literal|"}"
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JdbcTest.java
end_comment

end_unit

