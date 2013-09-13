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
name|clone
operator|.
name|CloneSchema
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
name|generate
operator|.
name|RangeTable
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
name|java
operator|.
name|ReflectiveSchema
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
name|jdbc
operator|.
name|Driver
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
name|prepare
operator|.
name|Prepare
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
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
name|Pair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
comment|/**  * Tests for using Optiq via JDBC.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcTest
block|{
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
specifier|public
specifier|static
specifier|final
name|String
name|FOODMART_SCHEMA
init|=
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'foodmart',\n"
operator|+
literal|"       jdbcUser: 'foodmart',\n"
operator|+
literal|"       jdbcPassword: 'foodmart',\n"
operator|+
literal|"       jdbcUrl: 'jdbc:mysql://localhost',\n"
operator|+
literal|"       jdbcCatalog: 'foodmart',\n"
operator|+
literal|"       jdbcSchema: null\n"
operator|+
literal|"     }\n"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FOODMART_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'foodmart',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|FOODMART_SCHEMA
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HR_SCHEMA
init|=
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'hr',\n"
operator|+
literal|"       factory: '"
operator|+
name|ReflectiveSchema
operator|.
name|Factory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         class: '"
operator|+
name|HrSchema
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }\n"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HR_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'hr',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|HR_SCHEMA
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
comment|/**    * Tests a relation that is accessed via method syntax.    * The function returns a {@link Queryable}.    */
annotation|@
name|Ignore
annotation|@
name|Test
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
name|MutableSchema
name|rootSchema
init|=
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|MapSchema
name|schema
init|=
name|MapSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"s"
argument_list|)
decl_stmt|;
specifier|final
name|TableFunction
argument_list|<
name|Object
argument_list|>
name|tableFunction
init|=
name|Schemas
operator|.
name|methodMember
argument_list|(
name|GENERATE_STRINGS_METHOD
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
name|rootSchema
operator|.
name|addTableFunction
argument_list|(
operator|new
name|TableFunctionInSchemaImpl
argument_list|(
name|schema
argument_list|,
literal|"GenerateStrings"
argument_list|,
name|tableFunction
argument_list|)
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
literal|"from table(s.\"GenerateStrings\"(5)) as t(c)\n"
operator|+
literal|"where char_length(c)> 3"
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
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/** Tests {@link Handler#onConnectionClose}    * and  {@link Handler#onStatementClose}. */
annotation|@
name|Test
specifier|public
name|void
name|testOnConnectionClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
index|[]
name|closeCount
init|=
block|{
literal|0
block|}
decl_stmt|;
specifier|final
name|int
index|[]
name|statementCloseCount
init|=
block|{
literal|0
block|}
decl_stmt|;
name|HandlerDriver
operator|.
name|HANDLERS
operator|.
name|set
argument_list|(
operator|new
name|HandlerImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onConnectionClose
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|)
block|{
operator|++
name|closeCount
index|[
literal|0
index|]
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onStatementClose
parameter_list|(
name|OptiqStatement
name|statement
parameter_list|)
block|{
operator|++
name|statementCloseCount
index|[
literal|0
index|]
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|HandlerDriver
name|driver
init|=
operator|new
name|HandlerDriver
argument_list|()
decl_stmt|;
name|OptiqConnection
name|connection
init|=
operator|(
name|OptiqConnection
operator|)
name|driver
operator|.
name|connect
argument_list|(
literal|"jdbc:optiq:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|MutableSchema
name|rootSchema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|ReflectiveSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"hr"
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"emps\""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Close statement. It throws SQLException, but statement is still closed.
try|try
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expecting error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Close connection. It throws SQLException, but connection is still closed.
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expecting error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Close a closed connection. Handler is not called again.
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|HandlerDriver
operator|.
name|HANDLERS
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/** Tests {@link java.sql.Statement#closeOnCompletion()}. */
annotation|@
name|Test
specifier|public
name|void
name|testStatementCloseOnCompletion
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Driver
name|driver
init|=
operator|new
name|Driver
argument_list|()
decl_stmt|;
name|OptiqConnection
name|connection
init|=
operator|(
name|OptiqConnection
operator|)
name|driver
operator|.
name|connect
argument_list|(
literal|"jdbc:optiq:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|MutableSchema
name|rootSchema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|ReflectiveSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"hr"
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
operator|(
name|Boolean
operator|)
name|OptiqAssert
operator|.
name|call
argument_list|(
name|statement
argument_list|,
literal|"isCloseOnCompletion"
argument_list|)
argument_list|)
expr_stmt|;
name|OptiqAssert
operator|.
name|call
argument_list|(
name|statement
argument_list|,
literal|"closeOnCompletion"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|Boolean
operator|)
name|OptiqAssert
operator|.
name|call
argument_list|(
name|statement
argument_list|,
literal|"isCloseOnCompletion"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"emps\""
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|statement
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|connection
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
comment|// when result set is closed, statement is closed automatically
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|statement
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|connection
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|statement
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|connection
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * The example in the README.    */
annotation|@
name|Test
specifier|public
name|void
name|testReadme
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
name|ReflectiveSchema
operator|.
name|create
argument_list|(
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
argument_list|,
literal|"hr"
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
name|optiqConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select d.\"deptno\", min(e.\"empid\")\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"join \"hr\".\"depts\" as d\n"
operator|+
literal|"  on e.\"deptno\" = d.\"deptno\"\n"
operator|+
literal|"group by d.\"deptno\"\n"
operator|+
literal|"having count(*)> 1"
argument_list|)
decl_stmt|;
name|OptiqAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
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
block|}
comment|/**    * Make sure that the properties look sane.    */
annotation|@
name|Test
specifier|public
name|void
name|testVersion
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
specifier|final
name|DatabaseMetaData
name|metaData
init|=
name|optiqConnection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Optiq JDBC Driver"
argument_list|,
name|metaData
operator|.
name|getDriverName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|driverVersion
init|=
name|metaData
operator|.
name|getDriverVersion
argument_list|()
decl_stmt|;
specifier|final
name|int
name|driverMajorVersion
init|=
name|metaData
operator|.
name|getDriverMajorVersion
argument_list|()
decl_stmt|;
specifier|final
name|int
name|driverMinorVersion
init|=
name|metaData
operator|.
name|getDriverMinorVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|driverMajorVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|driverMinorVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Optiq"
argument_list|,
name|metaData
operator|.
name|getDatabaseProductName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|databaseProductVersion
init|=
name|metaData
operator|.
name|getDatabaseProductVersion
argument_list|()
decl_stmt|;
specifier|final
name|int
name|databaseMajorVersion
init|=
name|metaData
operator|.
name|getDatabaseMajorVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|driverMajorVersion
argument_list|,
name|databaseMajorVersion
argument_list|)
expr_stmt|;
specifier|final
name|int
name|databaseMinorVersion
init|=
name|metaData
operator|.
name|getDatabaseMinorVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|driverMinorVersion
argument_list|,
name|databaseMinorVersion
argument_list|)
expr_stmt|;
comment|// Check how version is composed of major and minor version. Note that
comment|// version is stored in pom.xml; major and minor version are
comment|// stored in net-hydromatic-optiq-jdbc.properties.
if|if
condition|(
operator|!
name|driverVersion
operator|.
name|endsWith
argument_list|(
literal|"-SNAPSHOT"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|driverVersion
operator|.
name|startsWith
argument_list|(
literal|"0."
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|split
init|=
name|driverVersion
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|split
operator|.
name|length
operator|>=
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|driverVersion
operator|.
name|startsWith
argument_list|(
name|driverMajorVersion
operator|+
literal|"."
operator|+
name|driverMinorVersion
operator|+
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|databaseProductVersion
operator|.
name|endsWith
argument_list|(
literal|"-SNAPSHOT"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|databaseProductVersion
operator|.
name|startsWith
argument_list|(
literal|"0."
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|split
init|=
name|databaseProductVersion
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|split
operator|.
name|length
operator|>=
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|databaseProductVersion
operator|.
name|startsWith
argument_list|(
name|databaseMajorVersion
operator|+
literal|"."
operator|+
name|databaseMinorVersion
operator|+
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests driver's implementation of {@link DatabaseMetaData#getColumns}. */
annotation|@
name|Test
specifier|public
name|void
name|testMetaDataColumns
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Connection
name|connection
init|=
name|OptiqAssert
operator|.
name|getConnection
argument_list|(
literal|"hr"
argument_list|,
literal|"foodmart"
argument_list|)
decl_stmt|;
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|metaData
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
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
comment|// there's something
name|String
name|name
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|int
name|type
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|typeName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|int
name|columnSize
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|int
name|decimalDigits
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|9
argument_list|)
decl_stmt|;
name|int
name|numPrecRadix
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
name|charOctetLength
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|String
name|isNullable
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|18
argument_list|)
decl_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests driver's implementation of {@link DatabaseMetaData#getColumns}. */
annotation|@
name|Test
specifier|public
name|void
name|testResultSetMetaData
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Connection
name|connection
init|=
name|OptiqAssert
operator|.
name|getConnection
argument_list|(
literal|"hr"
argument_list|,
literal|"foodmart"
argument_list|)
decl_stmt|;
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
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select \"empid\", \"deptno\" as x, 1 as y\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
decl_stmt|;
name|ResultSetMetaData
name|metaData
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|metaData
operator|.
name|getColumnCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"empid"
argument_list|,
name|metaData
operator|.
name|getColumnLabel
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"empid"
argument_list|,
name|metaData
operator|.
name|getColumnName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"emps"
argument_list|,
name|metaData
operator|.
name|getTableName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"X"
argument_list|,
name|metaData
operator|.
name|getColumnLabel
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deptno"
argument_list|,
name|metaData
operator|.
name|getColumnName
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"emps"
argument_list|,
name|metaData
operator|.
name|getTableName
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Y"
argument_list|,
name|metaData
operator|.
name|getColumnLabel
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Y"
argument_list|,
name|metaData
operator|.
name|getColumnName
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metaData
operator|.
name|getTableName
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests some queries that have expedited processing because connection pools    * like to use them to check whether the connection is alive.    */
annotation|@
name|Test
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCloneSchema
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
specifier|final
name|OptiqConnection
name|connection
init|=
name|OptiqAssert
operator|.
name|getConnection
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Schema
name|foodmart
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
name|CloneSchema
operator|.
name|create
argument_list|(
name|connection
operator|.
name|getRootSchema
argument_list|()
argument_list|,
literal|"foodmart2"
argument_list|,
name|foodmart
argument_list|)
expr_stmt|;
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
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select count(*) from \"foodmart2\".\"time_by_day\""
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
name|assertEquals
argument_list|(
literal|730
argument_list|,
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCloneGroupBy
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"the_year\", count(*) as c, min(\"the_month\") as m\n"
operator|+
literal|"from \"foodmart2\".\"time_by_day\"\n"
operator|+
literal|"group by \"the_year\"\n"
operator|+
literal|"order by 1, 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"the_year=1997; C=365; M=April\n"
operator|+
literal|"the_year=1998; C=365; M=April\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testCloneGroupBy2
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_by_day\".\"the_year\" as \"c0\", \"time_by_day\".\"quarter\" as \"c1\", \"product_class\".\"product_family\" as \"c2\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"product_class\" as \"product_class\", \"product\" as \"product\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\" and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" group by \"time_by_day\".\"the_year\", \"time_by_day\".\"quarter\", \"product_class\".\"product_family\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997; c1=Q2; c2=Drink; m0=5895.0000\n"
operator|+
literal|"c0=1997; c1=Q1; c2=Food; m0=47809.0000\n"
operator|+
literal|"c0=1997; c1=Q3; c2=Drink; m0=6065.0000\n"
operator|+
literal|"c0=1997; c1=Q4; c2=Drink; m0=6661.0000\n"
operator|+
literal|"c0=1997; c1=Q4; c2=Food; m0=51866.0000\n"
operator|+
literal|"c0=1997; c1=Q1; c2=Drink; m0=5976.0000\n"
operator|+
literal|"c0=1997; c1=Q3; c2=Non-Consumable; m0=12343.0000\n"
operator|+
literal|"c0=1997; c1=Q4; c2=Non-Consumable; m0=13497.0000\n"
operator|+
literal|"c0=1997; c1=Q2; c2=Non-Consumable; m0=11890.0000\n"
operator|+
literal|"c0=1997; c1=Q2; c2=Food; m0=44825.0000\n"
operator|+
literal|"c0=1997; c1=Q3; c2=Food; m0=47440.0000\n"
operator|+
literal|"c0=1997; c1=Q1; c2=Non-Consumable; m0=12506.0000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests plan for a query with 4 tables, 3 joins. */
annotation|@
name|Test
specifier|public
name|void
name|testCloneGroupBy2Plan
parameter_list|()
block|{
comment|// NOTE: Plan is nowhere near optimal yet.
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"explain plan for select \"time_by_day\".\"the_year\" as \"c0\", \"time_by_day\".\"quarter\" as \"c1\", \"product_class\".\"product_family\" as \"c2\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"product_class\" as \"product_class\", \"product\" as \"product\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\" and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" group by \"time_by_day\".\"the_year\", \"time_by_day\".\"quarter\", \"product_class\".\"product_family\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EnumerableAggregateRel(group=[{0, 1, 2}], m0=[SUM($3)])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0..37=[{inputs}], c0=[$t19], c1=[$t23], c2=[$t37], unit_sales=[$t32])\n"
operator|+
literal|"    EnumerableJoinRel(condition=[AND(=($25, $1), =($0, $33))], joinType=[inner])\n"
operator|+
literal|"      EnumerableTableAccessRel(table=[[foodmart2, product]])\n"
operator|+
literal|"      EnumerableJoinRel(condition=[true], joinType=[inner])\n"
operator|+
literal|"        EnumerableJoinRel(condition=[=($11, $0)], joinType=[inner])\n"
operator|+
literal|"          EnumerableCalcRel(expr#0..9=[{inputs}], expr#10=[CAST($t4):INTEGER], expr#11=[1997], expr#12=[=($t10, $t11)], proj#0..9=[{exprs}], $condition=[$t12])\n"
operator|+
literal|"            EnumerableTableAccessRel(table=[[foodmart2, time_by_day]])\n"
operator|+
literal|"          EnumerableTableAccessRel(table=[[foodmart2, sales_fact_1997]])\n"
operator|+
literal|"        EnumerableTableAccessRel(table=[[foodmart2, product_class]])\n"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByCase
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_by_day\".\"the_year\" as \"c0\" from \"time_by_day\" as \"time_by_day\" group by \"time_by_day\".\"the_year\" order by CASE WHEN \"time_by_day\".\"the_year\" IS NULL THEN 1 ELSE 0 END, \"time_by_day\".\"the_year\" ASC"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997\n"
operator|+
literal|"c0=1998\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|queries
init|=
block|{
literal|"select count(*) from (select 1 as \"c0\" from \"salary\" as \"salary\") as \"init\""
block|,
literal|"EXPR$0=21252\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"salary\" as \"salary2\") as \"init\""
block|,
literal|"EXPR$0=21252\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"department\" as \"department\") as \"init\""
block|,
literal|"EXPR$0=12\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"employee\" as \"employee\") as \"init\""
block|,
literal|"EXPR$0=1155\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"employee_closure\" as \"employee_closure\") as \"init\""
block|,
literal|"EXPR$0=7179\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"position\" as \"position\") as \"init\""
block|,
literal|"EXPR$0=18\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"promotion\" as \"promotion\") as \"init\""
block|,
literal|"EXPR$0=1864\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"store\" as \"store\") as \"init\""
block|,
literal|"EXPR$0=25\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"product\" as \"product\") as \"init\""
block|,
literal|"EXPR$0=1560\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"product_class\" as \"product_class\") as \"init\""
block|,
literal|"EXPR$0=110\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"time_by_day\" as \"time_by_day\") as \"init\""
block|,
literal|"EXPR$0=730\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"customer\" as \"customer\") as \"init\""
block|,
literal|"EXPR$0=10281\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"sales_fact_1997\" as \"sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86837\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"inventory_fact_1997\" as \"inventory_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=4070\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"warehouse\" as \"warehouse\") as \"init\""
block|,
literal|"EXPR$0=24\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_c_special_sales_fact_1997\" as \"agg_c_special_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86805\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_pl_01_sales_fact_1997\" as \"agg_pl_01_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86829\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_l_05_sales_fact_1997\" as \"agg_l_05_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86154\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_g_ms_pcat_sales_fact_1997\" as \"agg_g_ms_pcat_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=2637\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_c_14_sales_fact_1997\" as \"agg_c_14_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86805\n"
block|,
literal|"select \"time_by_day\".\"the_year\" as \"c0\" from \"time_by_day\" as \"time_by_day\" group by \"time_by_day\".\"the_year\" order by \"time_by_day\".\"the_year\" ASC"
block|,
literal|"c0=1997\n"
operator|+
literal|"c0=1998\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\" from \"store\" as \"store\" where UPPER(\"store\".\"store_country\") = UPPER('USA') group by \"store\".\"store_country\" order by \"store\".\"store_country\" ASC"
block|,
literal|"c0=USA\n"
block|,
literal|"select \"store\".\"store_state\" as \"c0\" from \"store\" as \"store\" where (\"store\".\"store_country\" = 'USA') and UPPER(\"store\".\"store_state\") = UPPER('CA') group by \"store\".\"store_state\" order by \"store\".\"store_state\" ASC"
block|,
literal|"c0=CA\n"
block|,
literal|"select \"store\".\"store_city\" as \"c0\", \"store\".\"store_state\" as \"c1\" from \"store\" as \"store\" where (\"store\".\"store_state\" = 'CA' and \"store\".\"store_country\" = 'USA') and UPPER(\"store\".\"store_city\") = UPPER('Los Angeles') group by \"store\".\"store_city\", \"store\".\"store_state\" order by \"store\".\"store_city\" ASC"
block|,
literal|"c0=Los Angeles; c1=CA\n"
block|,
literal|"select \"customer\".\"country\" as \"c0\" from \"customer\" as \"customer\" where UPPER(\"customer\".\"country\") = UPPER('USA') group by \"customer\".\"country\" order by \"customer\".\"country\" ASC"
block|,
literal|"c0=USA\n"
block|,
literal|"select \"customer\".\"state_province\" as \"c0\", \"customer\".\"country\" as \"c1\" from \"customer\" as \"customer\" where (\"customer\".\"country\" = 'USA') and UPPER(\"customer\".\"state_province\") = UPPER('CA') group by \"customer\".\"state_province\", \"customer\".\"country\" order by \"customer\".\"state_province\" ASC"
block|,
literal|"c0=CA; c1=USA\n"
block|,
literal|"select \"customer\".\"city\" as \"c0\", \"customer\".\"country\" as \"c1\", \"customer\".\"state_province\" as \"c2\" from \"customer\" as \"customer\" where (\"customer\".\"country\" = 'USA' and \"customer\".\"state_province\" = 'CA' and \"customer\".\"country\" = 'USA' and \"customer\".\"state_province\" = 'CA' and \"customer\".\"country\" = 'USA') and UPPER(\"customer\".\"city\") = UPPER('Los Angeles') group by \"customer\".\"city\", \"customer\".\"country\", \"customer\".\"state_province\" order by \"customer\".\"city\" ASC"
block|,
literal|"c0=Los Angeles; c1=USA; c2=CA\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\" from \"store\" as \"store\" where UPPER(\"store\".\"store_country\") = UPPER('Gender') group by \"store\".\"store_country\" order by \"store\".\"store_country\" ASC"
block|,
literal|""
block|,
literal|"select \"store\".\"store_type\" as \"c0\" from \"store\" as \"store\" where UPPER(\"store\".\"store_type\") = UPPER('Gender') group by \"store\".\"store_type\" order by \"store\".\"store_type\" ASC"
block|,
literal|""
block|,
literal|"select \"product_class\".\"product_family\" as \"c0\" from \"product\" as \"product\", \"product_class\" as \"product_class\" where \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" and UPPER(\"product_class\".\"product_family\") = UPPER('Gender') group by \"product_class\".\"product_family\" order by \"product_class\".\"product_family\" ASC"
block|,
literal|""
block|,
literal|"select \"promotion\".\"media_type\" as \"c0\" from \"promotion\" as \"promotion\" where UPPER(\"promotion\".\"media_type\") = UPPER('Gender') group by \"promotion\".\"media_type\" order by \"promotion\".\"media_type\" ASC"
block|,
literal|""
block|,
literal|"select \"promotion\".\"promotion_name\" as \"c0\" from \"promotion\" as \"promotion\" where UPPER(\"promotion\".\"promotion_name\") = UPPER('Gender') group by \"promotion\".\"promotion_name\" order by \"promotion\".\"promotion_name\" ASC"
block|,
literal|""
block|,
literal|"select \"promotion\".\"media_type\" as \"c0\" from \"promotion\" as \"promotion\" where UPPER(\"promotion\".\"media_type\") = UPPER('No Media') group by \"promotion\".\"media_type\" order by \"promotion\".\"media_type\" ASC"
block|,
literal|"c0=No Media\n"
block|,
literal|"select \"promotion\".\"media_type\" as \"c0\" from \"promotion\" as \"promotion\" group by \"promotion\".\"media_type\" order by \"promotion\".\"media_type\" ASC"
block|,
literal|"c0=Bulk Mail\n"
operator|+
literal|"c0=Cash Register Handout\n"
operator|+
literal|"c0=Daily Paper\n"
operator|+
literal|"c0=Daily Paper, Radio\n"
operator|+
literal|"c0=Daily Paper, Radio, TV\n"
operator|+
literal|"c0=In-Store Coupon\n"
operator|+
literal|"c0=No Media\n"
operator|+
literal|"c0=Product Attachment\n"
operator|+
literal|"c0=Radio\n"
operator|+
literal|"c0=Street Handout\n"
operator|+
literal|"c0=Sunday Paper\n"
operator|+
literal|"c0=Sunday Paper, Radio\n"
operator|+
literal|"c0=Sunday Paper, Radio, TV\n"
operator|+
literal|"c0=TV\n"
block|,
literal|"select count(distinct \"the_year\") from \"time_by_day\""
block|,
literal|"EXPR$0=2\n"
block|,
literal|"select \"time_by_day\".\"the_year\" as \"c0\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 group by \"time_by_day\".\"the_year\""
block|,
literal|"c0=1997; m0=266773.0000\n"
block|,
literal|"select \"time_by_day\".\"the_year\" as \"c0\", \"promotion\".\"media_type\" as \"c1\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"promotion\" as \"promotion\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"promotion_id\" = \"promotion\".\"promotion_id\" group by \"time_by_day\".\"the_year\", \"promotion\".\"media_type\""
block|,
literal|"c0=1997; c1=Bulk Mail; m0=4320.0000\n"
operator|+
literal|"c0=1997; c1=Radio; m0=2454.0000\n"
operator|+
literal|"c0=1997; c1=Street Handout; m0=5753.0000\n"
operator|+
literal|"c0=1997; c1=TV; m0=3607.0000\n"
operator|+
literal|"c0=1997; c1=No Media; m0=195448.0000\n"
operator|+
literal|"c0=1997; c1=In-Store Coupon; m0=3798.0000\n"
operator|+
literal|"c0=1997; c1=Sunday Paper, Radio, TV; m0=2726.0000\n"
operator|+
literal|"c0=1997; c1=Product Attachment; m0=7544.0000\n"
operator|+
literal|"c0=1997; c1=Daily Paper; m0=7738.0000\n"
operator|+
literal|"c0=1997; c1=Cash Register Handout; m0=6697.0000\n"
operator|+
literal|"c0=1997; c1=Daily Paper, Radio; m0=6891.0000\n"
operator|+
literal|"c0=1997; c1=Daily Paper, Radio, TV; m0=9513.0000\n"
operator|+
literal|"c0=1997; c1=Sunday Paper, Radio; m0=5945.0000\n"
operator|+
literal|"c0=1997; c1=Sunday Paper; m0=4339.0000\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\", sum(\"inventory_fact_1997\".\"supply_time\") as \"m0\" from \"store\" as \"store\", \"inventory_fact_1997\" as \"inventory_fact_1997\" where \"inventory_fact_1997\".\"store_id\" = \"store\".\"store_id\" group by \"store\".\"store_country\""
block|,
literal|"c0=USA; m0=10425\n"
block|,
literal|"select \"sn\".\"desc\" as \"c0\" from (SELECT * FROM (VALUES (1, 'SameName')) AS \"t\" (\"id\", \"desc\")) as \"sn\" group by \"sn\".\"desc\" order by \"sn\".\"desc\" ASC NULLS LAST"
block|,
literal|"c0=SameName\n"
block|,
literal|"select \"the_year\", count(*) as c, min(\"the_month\") as m\n"
operator|+
literal|"from \"foodmart2\".\"time_by_day\"\n"
operator|+
literal|"group by \"the_year\"\n"
operator|+
literal|"order by 1, 2"
block|,
literal|"the_year=1997; C=365; M=April\n"
operator|+
literal|"the_year=1998; C=365; M=April\n"
block|,
literal|"select\n"
operator|+
literal|" \"store\".\"store_state\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"store_sales\") as \"m1\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"store\".\"store_state\" in ('DF', 'WA')\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"store\".\"store_state\", \"time_by_day\".\"the_year\""
block|,
literal|"c0=WA; c1=1997; m0=124366.0000; m1=263793.2200\n"
block|,
literal|"select count(distinct \"product_id\") from \"product\""
block|,
literal|"EXPR$0=1560\n"
block|,
literal|"select \"store\".\"store_name\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"store_sales\") as \"m0\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"store\".\"store_name\" in ('Store 1', 'Store 10', 'Store 11', 'Store 15', 'Store 16', 'Store 24', 'Store 3', 'Store 7')\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"store\".\"store_name\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\"\n"
block|,
literal|"c0=Store 7; c1=1997; m0=54545.2800\n"
operator|+
literal|"c0=Store 24; c1=1997; m0=54431.1400\n"
operator|+
literal|"c0=Store 16; c1=1997; m0=49634.4600\n"
operator|+
literal|"c0=Store 3; c1=1997; m0=52896.3000\n"
operator|+
literal|"c0=Store 15; c1=1997; m0=52644.0700\n"
operator|+
literal|"c0=Store 11; c1=1997; m0=55058.7900\n"
block|,
literal|"select \"customer\".\"yearly_income\" as \"c0\","
operator|+
literal|" \"customer\".\"education\" as \"c1\" \n"
operator|+
literal|"from \"customer\" as \"customer\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|" and ((not (\"customer\".\"yearly_income\" in ('$10K - $30K', '$50K - $70K'))\n"
operator|+
literal|" or (\"customer\".\"yearly_income\" is null)))\n"
operator|+
literal|"group by \"customer\".\"yearly_income\",\n"
operator|+
literal|" \"customer\".\"education\"\n"
operator|+
literal|"order by \"customer\".\"yearly_income\" ASC NULLS LAST,\n"
operator|+
literal|" \"customer\".\"education\" ASC NULLS LAST"
block|,
literal|"c0=$110K - $130K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$110K - $130K; c1=Graduate Degree\n"
operator|+
literal|"c0=$110K - $130K; c1=High School Degree\n"
operator|+
literal|"c0=$110K - $130K; c1=Partial College\n"
operator|+
literal|"c0=$110K - $130K; c1=Partial High School\n"
operator|+
literal|"c0=$130K - $150K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$130K - $150K; c1=Graduate Degree\n"
operator|+
literal|"c0=$130K - $150K; c1=High School Degree\n"
operator|+
literal|"c0=$130K - $150K; c1=Partial College\n"
operator|+
literal|"c0=$130K - $150K; c1=Partial High School\n"
operator|+
literal|"c0=$150K +; c1=Bachelors Degree\n"
operator|+
literal|"c0=$150K +; c1=Graduate Degree\n"
operator|+
literal|"c0=$150K +; c1=High School Degree\n"
operator|+
literal|"c0=$150K +; c1=Partial College\n"
operator|+
literal|"c0=$150K +; c1=Partial High School\n"
operator|+
literal|"c0=$30K - $50K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$30K - $50K; c1=Graduate Degree\n"
operator|+
literal|"c0=$30K - $50K; c1=High School Degree\n"
operator|+
literal|"c0=$30K - $50K; c1=Partial College\n"
operator|+
literal|"c0=$30K - $50K; c1=Partial High School\n"
operator|+
literal|"c0=$70K - $90K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$70K - $90K; c1=Graduate Degree\n"
operator|+
literal|"c0=$70K - $90K; c1=High School Degree\n"
operator|+
literal|"c0=$70K - $90K; c1=Partial College\n"
operator|+
literal|"c0=$70K - $90K; c1=Partial High School\n"
operator|+
literal|"c0=$90K - $110K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$90K - $110K; c1=Graduate Degree\n"
operator|+
literal|"c0=$90K - $110K; c1=High School Degree\n"
operator|+
literal|"c0=$90K - $110K; c1=Partial College\n"
operator|+
literal|"c0=$90K - $110K; c1=Partial High School\n"
block|,
literal|"select \"time_by_day\".\"the_year\" as \"c0\", \"product_class\".\"product_family\" as \"c1\", \"customer\".\"state_province\" as \"c2\", \"customer\".\"city\" as \"c3\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"product_class\" as \"product_class\", \"product\" as \"product\", \"customer\" as \"customer\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\" and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" and \"product_class\".\"product_family\" = 'Drink' and \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\" and \"customer\".\"state_province\" = 'WA' and \"customer\".\"city\" in ('Anacortes', 'Ballard', 'Bellingham', 'Bremerton', 'Burien', 'Edmonds', 'Everett', 'Issaquah', 'Kirkland', 'Lynnwood', 'Marysville', 'Olympia', 'Port Orchard', 'Puyallup', 'Redmond', 'Renton', 'Seattle', 'Sedro Woolley', 'Spokane', 'Tacoma', 'Walla Walla', 'Yakima') group by \"time_by_day\".\"the_year\", \"product_class\".\"product_family\", \"customer\".\"state_province\", \"customer\".\"city\""
block|,
literal|"c0=1997; c1=Drink; c2=WA; c3=Sedro Woolley; m0=58.0000\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"store_cost\") as \"m0\",\n"
operator|+
literal|" count(\"sales_fact_1997\".\"product_id\") as \"m1\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"customer_id\") as \"m2\",\n"
operator|+
literal|" sum((case when \"sales_fact_1997\".\"promotion_id\" = 0 then 0\n"
operator|+
literal|"     else \"sales_fact_1997\".\"store_sales\" end)) as \"m3\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"store\".\"store_country\", \"time_by_day\".\"the_year\""
block|,
literal|"c0=USA; c1=1997; m0=225627.2336; m1=86837; m2=5581; m3=151211.2100\n"
block|,   }
decl_stmt|;
comment|/** Test case for    *<a href="https://github.com/julianhyde/optiq/issues/35">issue #35</a>. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testJoinJoin
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select\n"
operator|+
literal|"   \"product_class\".\"product_family\" as \"c0\",\n"
operator|+
literal|"   \"product_class\".\"product_department\" as \"c1\",\n"
operator|+
literal|"   \"customer\".\"country\" as \"c2\",\n"
operator|+
literal|"   \"customer\".\"state_province\" as \"c3\",\n"
operator|+
literal|"   \"customer\".\"city\" as \"c4\"\n"
operator|+
literal|"from\n"
operator|+
literal|"   \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"join (\"product\" as \"product\"\n"
operator|+
literal|"     join \"product_class\" as \"product_class\"\n"
operator|+
literal|"     on \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\")\n"
operator|+
literal|"on  \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"join \"customer\" as \"customer\"\n"
operator|+
literal|"on  \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|"join \"promotion\" as \"promotion\"\n"
operator|+
literal|"on \"sales_fact_1997\".\"promotion_id\" = \"promotion\".\"promotion_id\"\n"
operator|+
literal|"where (\"promotion\".\"media_type\" = 'Radio'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'TV'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Sunday Paper'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Street Handout')\n"
operator|+
literal|" and (\"product_class\".\"product_family\" = 'Drink')\n"
operator|+
literal|" and (\"customer\".\"country\" = 'USA' and \"customer\".\"state_province\""
operator|+
literal|" = 'WA' and \"customer\".\"city\" = 'Bellingham')\n"
operator|+
literal|"group by \"product_class\".\"product_family\",\n"
operator|+
literal|"   \"product_class\".\"product_department\",\n"
operator|+
literal|"   \"customer\".\"country\",\n"
operator|+
literal|"   \"customer\".\"state_province\",\n"
operator|+
literal|"   \"customer\".\"city\"\n"
operator|+
literal|"order by ISNULL(\"product_class\".\"product_family\") ASC,   \"product_class\".\"product_family\" ASC,\n"
operator|+
literal|"   ISNULL(\"product_class\".\"product_department\") ASC,   \"product_class\".\"product_department\" ASC,\n"
operator|+
literal|"   ISNULL(\"customer\".\"country\") ASC,   \"customer\".\"country\" ASC,\n"
operator|+
literal|"   ISNULL(\"customer\".\"state_province\") ASC,   \"customer\".\"state_province\" ASC,\n"
operator|+
literal|"   ISNULL(\"customer\".\"city\") ASC,   \"customer\".\"city\" ASC"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| c0    | c1                  | c2  | c3   | c4         |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| Drink | Alcoholic Beverages | USA | WA   | Bellingham |\n"
operator|+
literal|"| Drink | Dairy               | USA | WA   | Bellingham |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+"
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a list of (query, expected) pairs. The expected result is    * sometimes null. */
specifier|public
specifier|static
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getFoodmartQueries
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|queries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|query
init|=
name|queries
index|[
name|i
index|]
decl_stmt|;
name|String
name|expected
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|queries
operator|.
name|length
operator|&&
name|queries
index|[
name|i
operator|+
literal|1
index|]
operator|!=
literal|null
operator|&&
operator|!
name|queries
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"select"
argument_list|)
condition|)
block|{
name|expected
operator|=
name|queries
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|query
argument_list|,
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/** A selection of queries generated by Mondrian. */
annotation|@
name|Test
specifier|public
name|void
name|testCloneQueries
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|AssertThat
name|with
init|=
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|queries
init|=
name|getFoodmartQueries
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|query
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|queries
argument_list|)
control|)
block|{
try|try
block|{
comment|// uncomment to run specific queries:
comment|//      if (query.i != queries.size() - 1) continue;
specifier|final
name|String
name|sql
init|=
name|query
operator|.
name|e
operator|.
name|left
decl_stmt|;
specifier|final
name|String
name|expected
init|=
name|query
operator|.
name|e
operator|.
name|right
decl_stmt|;
specifier|final
name|OptiqAssert
operator|.
name|AssertQuery
name|query1
init|=
name|with
operator|.
name|query
argument_list|(
name|sql
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sql
operator|.
name|contains
argument_list|(
literal|"order by"
argument_list|)
condition|)
block|{
name|query1
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query1
operator|.
name|returnsUnordered
argument_list|(
name|expected
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|query1
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while running query #"
operator|+
name|query
operator|.
name|i
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFoo
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store\".\"store_country\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"store_cost\") as \"m0\",\n"
operator|+
literal|" count(\"sales_fact_1997\".\"product_id\") as \"m1\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"customer_id\") as \"m2\",\n"
operator|+
literal|" sum((case when \"sales_fact_1997\".\"promotion_id\" = 0 then 0\n"
operator|+
literal|"     else \"sales_fact_1997\".\"store_sales\" end)) as \"m3\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"store\".\"store_country\", \"time_by_day\".\"the_year\""
argument_list|)
comment|//        .explainContains("xxx")
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** There was a bug representing a nullable timestamp using a {@link Long}    * internally. */
annotation|@
name|Test
specifier|public
name|void
name|testNullableTimestamp
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"hire_date\" from \"employee\" where \"employee_id\" = 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"hire_date=1994-12-01T08:00:00Z\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValues
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|query
argument_list|(
literal|"values (1), (2)"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1\n"
operator|+
literal|"EXPR$0=2\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValuesAlias
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"desc\" from (VALUES ROW(1, 'SameName')) AS \"t\" (\"id\", \"desc\")"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"desc=SameName\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValuesMinus
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|query
argument_list|(
literal|"values (-2-1)"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=-3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a table constructor that has multiple rows and multiple columns.    *    *<p>Note that the character literals become CHAR(3) and that the first is    * correctly rendered with trailing spaces: 'a  '. If we were inserting    * into a VARCHAR column the behavior would be different; the literals    * would be converted into VARCHAR(3) values and the implied cast from    * CHAR(1) to CHAR(3) that appends trailing spaces does not occur. See    * "contextually typed value specification" in the SQL spec.</p>    */
annotation|@
name|Test
specifier|public
name|void
name|testValuesComposite
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|query
argument_list|(
literal|"values (1, 'a'), (2, 'abc')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1; EXPR$1=a  \n"
operator|+
literal|"EXPR$0=2; EXPR$1=abc\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInnerJoinValues
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|LINGUAL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, desc from sales.emps,\n"
operator|+
literal|"  (SELECT * FROM (VALUES (10, 'SameName')) AS t (id, desc)) as sn\n"
operator|+
literal|"where emps.deptno = sn.id and sn.desc = 'SameName' group by empno, desc"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=1; DESC=SameName\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
literal|"select \"time_by_day\".\"the_year\" as \"c0\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 group by \"time_by_day\".\"the_year\""
decl_stmt|;
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|s
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997; m0=266773.0000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** A difficult query: an IN list so large that the planner promotes it    * to a semi-join against a VALUES relation. */
annotation|@
name|Test
specifier|public
name|void
name|testIn
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_by_day\".\"the_year\" as \"c0\",\n"
operator|+
literal|" \"product_class\".\"product_family\" as \"c1\",\n"
operator|+
literal|" \"customer\".\"country\" as \"c2\",\n"
operator|+
literal|" \"customer\".\"state_province\" as \"c3\",\n"
operator|+
literal|" \"customer\".\"city\" as \"c4\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\"\n"
operator|+
literal|"from \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\", \"customer\" as \"customer\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"and \"product_class\".\"product_family\" = 'Drink'\n"
operator|+
literal|"and \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|"and \"customer\".\"country\" = 'USA'\n"
operator|+
literal|"and \"customer\".\"state_province\" = 'WA'\n"
operator|+
literal|"and \"customer\".\"city\" in ('Anacortes', 'Ballard', 'Bellingham', 'Bremerton', 'Burien', 'Edmonds', 'Everett', 'Issaquah', 'Kirkland', 'Lynnwood', 'Marysville', 'Olympia', 'Port Orchard', 'Puyallup', 'Redmond', 'Renton', 'Seattle', 'Sedro Woolley', 'Spokane', 'Tacoma', 'Walla Walla', 'Yakima') group by \"time_by_day\".\"the_year\", \"product_class\".\"product_family\", \"customer\".\"country\", \"customer\".\"state_province\", \"customer\".\"city\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997; c1=Drink; c2=USA; c3=WA; c4=Sedro Woolley; m0=58.0000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Query that uses parenthesized JOIN. */
annotation|@
name|Test
specifier|public
name|void
name|testSql92JoinParenthesized
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|TodoFixed
condition|)
block|{
return|return;
block|}
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select\n"
operator|+
literal|"   \"product_class\".\"product_family\" as \"c0\",\n"
operator|+
literal|"   \"product_class\".\"product_department\" as \"c1\",\n"
operator|+
literal|"   \"customer\".\"country\" as \"c2\",\n"
operator|+
literal|"   \"customer\".\"state_province\" as \"c3\",\n"
operator|+
literal|"   \"customer\".\"city\" as \"c4\"\n"
operator|+
literal|"from\n"
operator|+
literal|"   \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"join (\"product\" as \"product\"\n"
operator|+
literal|"     join \"product_class\" as \"product_class\"\n"
operator|+
literal|"     on \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\")\n"
operator|+
literal|"on  \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"join \"customer\" as \"customer\"\n"
operator|+
literal|"on  \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|"join \"promotion\" as \"promotion\"\n"
operator|+
literal|"on \"sales_fact_1997\".\"promotion_id\" = \"promotion\".\"promotion_id\"\n"
operator|+
literal|"where (\"promotion\".\"media_type\" = 'Radio'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'TV'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Sunday Paper'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Street Handout')\n"
operator|+
literal|" and (\"product_class\".\"product_family\" = 'Drink')\n"
operator|+
literal|" and (\"customer\".\"country\" = 'USA' and \"customer\".\"state_province\""
operator|+
literal|" = 'WA' and \"customer\".\"city\" = 'Bellingham')\n"
operator|+
literal|"group by \"product_class\".\"product_family\",\n"
operator|+
literal|"   \"product_class\".\"product_department\",\n"
operator|+
literal|"   \"customer\".\"country\",\n"
operator|+
literal|"   \"customer\".\"state_province\",\n"
operator|+
literal|"   \"customer\".\"city\"\n"
operator|+
literal|"order by \"product_class\".\"product_family\" ASC,\n"
operator|+
literal|"   \"product_class\".\"product_department\" ASC,\n"
operator|+
literal|"   \"customer\".\"country\" ASC,\n"
operator|+
literal|"   \"customer\".\"state_province\" ASC,\n"
operator|+
literal|"   \"customer\".\"city\" ASC"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| c0    | c1                  | c2  | c3   | c4         |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| Drink | Alcoholic Beverages | USA | WA   | Bellingham |\n"
operator|+
literal|"| Drink | Dairy               | USA | WA   | Bellingham |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... DESC NULLS FIRST. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByDescNullsFirst
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 desc nulls first"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0; grocery_sqft=null\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... NULLS FIRST. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByNullsFirst
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 nulls first"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0; grocery_sqft=null\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... DESC NULLS LAST. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByDescNullsLast
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 desc nulls last"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=0; grocery_sqft=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... NULLS LAST. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByNullsLast
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 nulls last"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=0; grocery_sqft=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... FETCH. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByFetch
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 10\n"
operator|+
literal|"order by 1 fetch first 5 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0; grocery_sqft=null\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=3; grocery_sqft=24390\n"
operator|+
literal|"store_id=4; grocery_sqft=16844\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... OFFSET ... FETCH. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOffsetFetch
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 10\n"
operator|+
literal|"order by 1 offset 2 rows fetch next 5 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=3; grocery_sqft=24390\n"
operator|+
literal|"store_id=4; grocery_sqft=16844\n"
operator|+
literal|"store_id=5; grocery_sqft=15012\n"
operator|+
literal|"store_id=6; grocery_sqft=15337\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests FETCH with no ORDER BY. */
annotation|@
name|Test
specifier|public
name|void
name|testFetch
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"empid\" from \"hr\".\"emps\"\n"
operator|+
literal|"fetch first 2 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100\n"
operator|+
literal|"empid=200\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFetchStar
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"fetch first 2 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests composite GROUP BY where one of the columns has NULL values. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByNull
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"deptno\", \"commission\", sum(\"salary\") s\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by \"deptno\", \"commission\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"deptno=10; commission=null; S=7000.0\n"
operator|+
literal|"deptno=20; commission=500; S=8000.0\n"
operator|+
literal|"deptno=10; commission=1000; S=10000.0\n"
operator|+
literal|"deptno=10; commission=250; S=11500.0\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests sorting by a column that is already sorted. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOnSortedTable
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"time_by_day\"\n"
operator|+
literal|"order by \"time_id\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableSortRel(sort0=[$0], dir0=[Ascending])\n"
operator|+
literal|"  EnumerableTableAccessRel(table=[[foodmart2, time_by_day]])\n\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests sorting by a column that is already sorted. */
annotation|@
name|Ignore
argument_list|(
literal|"fix output for timezone"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOnSortedTable2
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_id\", \"the_date\" from \"time_by_day\"\n"
operator|+
literal|"where \"time_id\"< 370\n"
operator|+
literal|"order by \"time_id\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"time_id=367; the_date=1997-01-01 00:00:00.0\n"
operator|+
literal|"time_id=368; the_date=1997-01-02 00:00:00.0\n"
operator|+
literal|"time_id=369; the_date=1997-01-03 00:00:00.0\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableSortRel(sort0=[$0], dir0=[Ascending])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0..9=[{inputs}], expr#10=[370], expr#11=[<($t0, $t10)], proj#0..1=[{exprs}], $condition=[$t11])\n"
operator|+
literal|"    EnumerableTableAccessRel(table=[[foodmart2, time_by_day]])\n\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests windowed aggregation. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAgg
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select sum(\"salary\" + \"empid\") over w as s,\n"
operator|+
literal|" 5 as five,\n"
operator|+
literal|" min(\"salary\") over w as m,\n"
operator|+
literal|" count(*) over w as c,\n"
operator|+
literal|" \"deptno\",\n"
operator|+
literal|" \"empid\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"window w as (partition by \"deptno\" order by \"empid\" rows 1 preceding)"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[S REAL, FIVE INTEGER NOT NULL, M REAL, C BIGINT, deptno INTEGER NOT NULL, empid INTEGER NOT NULL]"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalcRel(expr#0..7=[{inputs}], expr#8=[0], expr#9=[>($t4, $t8)], expr#10=[null], expr#11=[CASE($t9, $t5, $t10)], expr#12=[CAST($t11):JavaType(class java.lang.Float)], expr#13=[5], expr#14=[CAST($t6):JavaType(class java.lang.Float)], expr#15=[CAST($t7):BIGINT], S=[$t12], FIVE=[$t13], M=[$t14], C=[$t15], deptno=[$t1], empid=[$t0])\n"
operator|+
literal|"  EnumerableWindowRel(window#0=[window(partition {1} order by [0 Ascending] rows between 1 PRECEDING and CURRENT ROW aggs [COUNT($3), $SUM0($3), MIN($2), COUNT()])])\n"
operator|+
literal|"    EnumerableCalcRel(expr#0..4=[{inputs}], expr#5=[+($t3, $t0)], proj#0..1=[{exprs}], salary=[$t3], $3=[$t5])\n"
operator|+
literal|"      EnumerableTableAccessRel(table=[[hr, emps]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"S=8200.0; FIVE=5; M=8000.0; C=1; deptno=20; empid=200\n"
operator|+
literal|"S=10100.0; FIVE=5; M=10000.0; C=1; deptno=10; empid=100\n"
operator|+
literal|"S=23220.0; FIVE=5; M=11500.0; C=2; deptno=10; empid=110\n"
operator|+
literal|"S=14300.0; FIVE=5; M=7000.0; C=2; deptno=10; empid=150\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests windowed aggregation with multiple windows.    * One window straddles the current row.    * Some windows have no PARTITION BY clause. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAgg2
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select sum(\"salary\" + \"empid\") over w as s,\n"
operator|+
literal|" 5 as five,\n"
operator|+
literal|" min(\"salary\") over w as m,\n"
operator|+
literal|" count(*) over w as c,\n"
operator|+
literal|" count(*) over w2 as c2,\n"
operator|+
literal|" count(*) over w11 as c11,\n"
operator|+
literal|" count(*) over w11dept as c11dept,\n"
operator|+
literal|" \"deptno\",\n"
operator|+
literal|" \"empid\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"window w as (order by \"empid\" rows 1 preceding),\n"
operator|+
literal|" w2 as (order by \"empid\" rows 2 preceding),\n"
operator|+
literal|" w11 as (order by \"empid\" rows between 1 preceding and 1 following),\n"
operator|+
literal|" w11dept as (partition by \"deptno\" order by \"empid\" rows between 1 preceding and 1 following)"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[S REAL, FIVE INTEGER NOT NULL, M REAL, C BIGINT, C2 BIGINT, C11 BIGINT, C11DEPT BIGINT, deptno INTEGER NOT NULL, empid INTEGER NOT NULL]"
argument_list|)
comment|// Check that optimizes for window whose PARTITION KEY is empty
operator|.
name|planContains
argument_list|(
literal|"tempList.size()"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"S=16400.0; FIVE=5; M=8000.0; C=2; C2=3; C11=2; C11DEPT=1; deptno=20; empid=200\n"
operator|+
literal|"S=10100.0; FIVE=5; M=10000.0; C=1; C2=1; C11=2; C11DEPT=2; deptno=10; empid=100\n"
operator|+
literal|"S=23220.0; FIVE=5; M=11500.0; C=2; C2=2; C11=3; C11DEPT=3; deptno=10; empid=110\n"
operator|+
literal|"S=14300.0; FIVE=5; M=7000.0; C=2; C2=3; C11=3; C11DEPT=2; deptno=10; empid=150\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for RANK and ORDER BY ... DESCENDING, NULLS FIRST, NULLS LAST. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggRank
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|" \"commission\",\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"commission\" desc nulls first) as rcnf,\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"commission\" desc nulls last) as rcnl,\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"empid\") as r,\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"empid\" desc) as rd\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, commission INTEGER, RCNF INTEGER, RCNL INTEGER, R INTEGER, RD INTEGER]"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"deptno=20; empid=200; commission=500; RCNF=1; RCNL=1; R=1; RD=1\n"
operator|+
literal|"deptno=10; empid=150; commission=null; RCNF=1; RCNL=3; R=3; RD=1\n"
operator|+
literal|"deptno=10; empid=110; commission=250; RCNF=3; RCNL=2; R=2; RD=2\n"
operator|+
literal|"deptno=10; empid=100; commission=1000; RCNF=2; RCNL=1; R=1; RD=3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests WHERE comparing a nullable integer with an integer literal. */
annotation|@
name|Test
specifier|public
name|void
name|testWhereNullable
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"where \"commission\"> 800"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the LIKE operator. */
annotation|@
name|Test
specifier|public
name|void
name|testLike
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"where \"name\" like '%i__'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests array index. */
annotation|@
name|Test
specifier|public
name|void
name|testArray
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"deptno\", \"employees\"[1] as e from \"hr\".\"depts\"\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"deptno=10; E=Employee [empid: 100, deptno: 10, name: Bill]\n"
operator|+
literal|"deptno=30; E=null\n"
operator|+
literal|"deptno=40; E=Employee [empid: 200, deptno: 20, name: Eric]\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the NOT IN operator. Problems arose in code-generation because    * the column allows nulls. */
annotation|@
name|Test
specifier|public
name|void
name|testNotIn
parameter_list|()
block|{
name|predicate
argument_list|(
literal|"\"name\" not in ('a', 'b') or \"name\" is null"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
argument_list|)
expr_stmt|;
comment|// And some similar combinations...
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b') or \"name\" is null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b', null) or \"name\" is null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b') or \"name\" is not null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b', null) or \"name\" is not null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" not in ('a', 'b', null) or \"name\" is not null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" not in ('a', 'b', null) and \"name\" is not null"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|OptiqAssert
operator|.
name|AssertQuery
name|predicate
parameter_list|(
name|String
name|foo
parameter_list|)
block|{
return|return
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"where "
operator|+
name|foo
argument_list|)
operator|.
name|runs
argument_list|()
return|;
block|}
comment|/** Tests the TABLES table in the information schema. */
annotation|@
name|Test
specifier|public
name|void
name|testMetaTables
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR_PLUS_METADATA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"metadata\".TABLES"
argument_list|)
operator|.
name|returns
argument_list|(
name|OptiqAssert
operator|.
name|checkResultContains
argument_list|(
literal|"tableSchem=metadata; tableName=COLUMNS; tableType=SYSTEM_TABLE; "
argument_list|)
argument_list|)
expr_stmt|;
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR_PLUS_METADATA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(distinct \"tableSchem\") as c\n"
operator|+
literal|"from \"metadata\".TABLES"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that {@link java.sql.Statement#setMaxRows(int)} is honored. */
annotation|@
name|Test
specifier|public
name|void
name|testSetMaxRows
parameter_list|()
throws|throws
name|Exception
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|OptiqConnection
name|a0
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Statement
name|statement
init|=
name|a0
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
name|statement
operator|.
name|setMaxRows
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"illegal maxRows value: -1"
argument_list|)
expr_stmt|;
block|}
name|statement
operator|.
name|setMaxRows
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|statement
operator|.
name|getMaxRows
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"hr\".\"emps\""
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
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
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
return|return
literal|null
return|;
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model (a single schema based on    * a JDBC database). */
annotation|@
name|Test
specifier|public
name|void
name|testModel
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withModel
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"time_by_day\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=730\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Defines a materialized view and tests that the query is rewritten to use    * it, and that the query produces the same result with and without it. There    * are more comprehensive tests in {@link MaterializationTest}. */
annotation|@
name|Ignore
argument_list|(
literal|"until JdbcSchema can define materialized views"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testModelWithMaterializedView
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withModel
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
literal|false
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"sales_fact_1997\" join \"foodmart\".\"time_by_day\" using (\"time_id\")"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=86837\n"
argument_list|)
expr_stmt|;
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|FOODMART_MODEL
argument_list|,
literal|"agg_c_10_sales_fact_1997"
argument_list|,
literal|"select t.`month_of_year`, t.`quarter`, t.`the_year`, sum(s.`store_sales`) as `store_sales`, sum(s.`store_cost`), sum(s.`unit_sales`), count(distinct s.`customer_id`), count(*) as `fact_count` from `time_by_day` as t join `sales_fact_1997` as s using (`time_id`) group by t.`month_of_year`, t.`quarter`, t.`the_year`"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select t.\"month_of_year\", t.\"quarter\", t.\"the_year\", sum(s.\"store_sales\") as \"store_sales\", sum(s.\"store_cost\"), sum(s.\"unit_sales\"), count(distinct s.\"customer_id\"), count(*) as \"fact_count\" from \"time_by_day\" as t join \"sales_fact_1997\" as s using (\"time_id\") group by t.\"month_of_year\", t.\"quarter\", t.\"the_year\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"JdbcTableScan(table=[[foodmart, agg_c_10_sales_fact_1997]])"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|false
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"JdbcTableScan(table=[[foodmart, sales_fact_1997]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains custom    * tables. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomTable
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withModel
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|EmpDeptTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': 1, 'bar': [345, 357] }\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"adhoc\".EMPLOYEES where \"deptno\" = 10"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains custom    * tables. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomTable2
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withModel
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'MATH',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'INTEGERS',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|RangeTable
operator|.
name|Factory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'column': 'N', 'start': 3, 'end': 7 }\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from math.integers"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"N=3\n"
operator|+
literal|"N=4\n"
operator|+
literal|"N=5\n"
operator|+
literal|"N=6\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a custom    * schema. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomSchema
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OptiqAssert
operator|.
name|AssertThat
name|that
init|=
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withModel
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'adhoc',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'empty'\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'adhoc',\n"
operator|+
literal|"      type: 'custom',\n"
operator|+
literal|"      factory: '"
operator|+
name|MySchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"      operand: {'tableName': 'ELVIS'}\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
decl_stmt|;
comment|// check that the specified 'defaultSchema' was used
name|that
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|)
block|{
try|try
block|{
name|assertEquals
argument_list|(
literal|"adhoc"
argument_list|,
name|connection
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
block|}
block|}
argument_list|)
expr_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select * from \"adhoc\".ELVIS where \"deptno\" = 10"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
argument_list|)
expr_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select * from \"adhoc\".EMPLOYEES"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Table 'adhoc.EMPLOYEES' not found"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that an immutable schema in a model cannot contain a view. */
annotation|@
name|Test
specifier|public
name|void
name|testModelImmutableSchemaCannotContainView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OptiqAssert
operator|.
name|AssertThat
name|that
init|=
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withModel
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'adhoc',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'empty'\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'adhoc',\n"
operator|+
literal|"      type: 'custom',\n"
operator|+
literal|"      tables: [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          name: 'v',\n"
operator|+
literal|"          type: 'view',\n"
operator|+
literal|"          sql: 'values (1)'\n"
operator|+
literal|"        }\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      factory: '"
operator|+
name|MySchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"      operand: {\n"
operator|+
literal|"           'tableName': 'ELVIS',\n"
operator|+
literal|"           'mutable': false\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
decl_stmt|;
name|that
operator|.
name|connectThrows
argument_list|(
literal|"Cannot define view; parent schema "
operator|+
literal|"DelegatingSchema(delegate=ReflectiveSchema(target=HrSchema)) is "
operator|+
literal|"not mutable"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a view. */
annotation|@
name|Test
specifier|public
name|void
name|testModelView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OptiqAssert
operator|.
name|AssertThat
name|with
init|=
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|withModel
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|EmpDeptTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': true, 'bar': 345}\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'V',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: 'select * from \"EMPLOYEES\" where \"deptno\" = 10'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"adhoc\".V order by \"name\" desc"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
comment|// Make sure that views appear in metadata.
name|with
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|OptiqConnection
name|a0
parameter_list|)
block|{
try|try
block|{
specifier|final
name|DatabaseMetaData
name|metaData
init|=
name|a0
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
comment|// all table types
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=EMPLOYEES; TABLE_TYPE=TABLE; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; TABLE_TYPE=VIEW; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
argument_list|,
name|OptiqAssert
operator|.
name|toString
argument_list|(
name|metaData
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// views only
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; TABLE_TYPE=VIEW; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
argument_list|,
name|OptiqAssert
operator|.
name|toString
argument_list|(
name|metaData
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
name|Schema
operator|.
name|TableType
operator|.
name|VIEW
operator|.
name|name
argument_list|()
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// columns
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=empid; DATA_TYPE=4; TYPE_NAME=JavaType(int) NOT NULL; COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=0; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=1; IS_NULLABLE=NO; SCOPE_CATALOG=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=null; IS_GENERATEDCOLUMN=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=deptno; DATA_TYPE=4; TYPE_NAME=JavaType(int) NOT NULL; COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=0; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=2; IS_NULLABLE=NO; SCOPE_CATALOG=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=null; IS_GENERATEDCOLUMN=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=name; DATA_TYPE=12; TYPE_NAME=JavaType(class java.lang.String); COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=1; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=3; IS_NULLABLE=YES; SCOPE_CATALOG=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=null; IS_GENERATEDCOLUMN=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=salary; DATA_TYPE=7; TYPE_NAME=JavaType(float) NOT NULL; COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=0; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=4; IS_NULLABLE=NO; SCOPE_CATALOG=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=null; IS_GENERATEDCOLUMN=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=commission; DATA_TYPE=4; TYPE_NAME=JavaType(class java.lang.Integer); COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=1; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=5; IS_NULLABLE=YES; SCOPE_CATALOG=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=null; IS_GENERATEDCOLUMN=null\n"
argument_list|,
name|OptiqAssert
operator|.
name|toString
argument_list|(
name|metaData
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|,
literal|"V"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests saving query results into temporary tables, per    * {@link net.hydromatic.optiq.jdbc.Handler.ResultSink}. */
annotation|@
name|Test
specifier|public
name|void
name|testAutomaticTemporaryTable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|objects
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OptiqAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
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
operator|(
name|OptiqConnection
operator|)
operator|new
name|AutoTempDriver
argument_list|(
name|objects
argument_list|)
operator|.
name|connect
argument_list|(
literal|"jdbc:optiq:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|MutableSchema
name|rootSchema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|ReflectiveSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"hr"
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
argument_list|)
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|OptiqConnection
name|a0
parameter_list|)
block|{
try|try
block|{
name|a0
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"hr\".\"emps\" "
operator|+
literal|"where \"deptno\" = 10"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|objects
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExplain
parameter_list|()
block|{
specifier|final
name|OptiqAssert
operator|.
name|AssertThat
name|with
init|=
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EnumerableValuesRel(tuples=[[{ 1, 'ab' }]])\n\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan with implementation for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EnumerableValuesRel(tuples=[[{ 1, 'ab' }]])\n\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan without implementation for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=ValuesRel(tuples=[[{ 1, 'ab' }]])\n\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan with type for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EXPR$0 INTEGER NOT NULL,\n"
operator|+
literal|"EXPR$1 CHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for bug where if two tables have different element classes    * but those classes have identical fields, Optiq would generate code to use    * the wrong element class; a {@link ClassCastException} would ensue. */
annotation|@
name|Test
specifier|public
name|void
name|testDifferentTypesSameFields
parameter_list|()
throws|throws
name|Exception
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
specifier|final
name|MutableSchema
name|rootSchema
init|=
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|ReflectiveSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"TEST"
argument_list|,
operator|new
name|MySchema
argument_list|()
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
name|optiqConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"SELECT \"myvalue\" from TEST.\"mytable2\""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"myvalue=2\n"
argument_list|,
name|OptiqAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
specifier|public
specifier|static
class|class
name|HrSchema
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HrSchema"
return|;
block|}
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
argument_list|,
literal|10000
argument_list|,
literal|1000
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
argument_list|,
literal|8000
argument_list|,
literal|500
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
argument_list|,
literal|7000
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|110
argument_list|,
literal|10
argument_list|,
literal|"Theodore"
argument_list|,
literal|11500
argument_list|,
literal|250
argument_list|)
block|,     }
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
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|emps
index|[
literal|0
index|]
argument_list|,
name|emps
index|[
literal|2
index|]
argument_list|)
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|30
argument_list|,
literal|"Marketing"
argument_list|,
name|Collections
operator|.
expr|<
name|Employee
operator|>
name|emptyList
argument_list|()
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|40
argument_list|,
literal|"HR"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|emps
index|[
literal|1
index|]
argument_list|)
argument_list|)
block|,     }
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
specifier|final
name|float
name|salary
decl_stmt|;
specifier|public
specifier|final
name|Integer
name|commission
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
parameter_list|,
name|float
name|salary
parameter_list|,
name|Integer
name|commission
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
name|this
operator|.
name|salary
operator|=
name|salary
expr_stmt|;
name|this
operator|.
name|commission
operator|=
name|commission
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Employee [empid: "
operator|+
name|empid
operator|+
literal|", deptno: "
operator|+
name|deptno
operator|+
literal|", name: "
operator|+
name|name
operator|+
literal|"]"
return|;
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
specifier|final
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
decl_stmt|;
specifier|public
name|Department
parameter_list|(
name|int
name|deptno
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
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
name|this
operator|.
name|employees
operator|=
name|employees
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Department [deptno: "
operator|+
name|deptno
operator|+
literal|", name: "
operator|+
name|name
operator|+
literal|", employees: "
operator|+
name|employees
operator|+
literal|"]"
return|;
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
block|,     }
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|LingualSchema
block|{
specifier|public
specifier|final
name|LingualEmp
index|[]
name|EMPS
init|=
block|{
operator|new
name|LingualEmp
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|LingualEmp
argument_list|(
literal|2
argument_list|,
literal|30
argument_list|)
block|}
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|LingualEmp
block|{
specifier|public
specifier|final
name|int
name|EMPNO
decl_stmt|;
specifier|public
specifier|final
name|int
name|DEPTNO
decl_stmt|;
specifier|public
name|LingualEmp
parameter_list|(
name|int
name|EMPNO
parameter_list|,
name|int
name|DEPTNO
parameter_list|)
block|{
name|this
operator|.
name|EMPNO
operator|=
name|EMPNO
expr_stmt|;
name|this
operator|.
name|DEPTNO
operator|=
name|DEPTNO
expr_stmt|;
block|}
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
name|MutableSchema
name|parentSchema
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
name|parentSchema
operator|.
name|getQueryProvider
argument_list|()
argument_list|,
name|parentSchema
argument_list|,
literal|"FoodMart"
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
specifier|public
specifier|static
specifier|abstract
class|class
name|AbstractModifiableTable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractTable
argument_list|<
name|T
argument_list|>
implements|implements
name|ModifiableTable
argument_list|<
name|T
argument_list|>
block|{
specifier|protected
name|AbstractModifiableTable
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|RelDataType
name|relDataType
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|elementType
argument_list|,
name|relDataType
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TableModificationRelBase
name|toModificationRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|TableModificationRelBase
operator|.
name|Operation
name|operation
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|updateColumnList
parameter_list|,
name|boolean
name|flattened
parameter_list|)
block|{
return|return
operator|new
name|TableModificationRel
argument_list|(
name|cluster
argument_list|,
name|table
argument_list|,
name|catalogReader
argument_list|,
name|child
argument_list|,
name|operation
argument_list|,
name|updateColumnList
argument_list|,
name|flattened
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|EmpDeptTableFactory
implements|implements
name|TableFactory
argument_list|<
name|Table
argument_list|>
block|{
specifier|public
name|Table
name|create
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|final
name|Object
index|[]
name|array
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"EMPLOYEES"
argument_list|)
condition|)
block|{
name|clazz
operator|=
name|Employee
operator|.
name|class
expr_stmt|;
name|array
operator|=
operator|new
name|HrSchema
argument_list|()
operator|.
name|emps
expr_stmt|;
block|}
else|else
block|{
name|clazz
operator|=
name|Department
operator|.
name|class
expr_stmt|;
name|array
operator|=
operator|new
name|HrSchema
argument_list|()
operator|.
name|depts
expr_stmt|;
block|}
return|return
operator|new
name|AbstractTable
argument_list|(
name|schema
argument_list|,
name|clazz
argument_list|,
name|schema
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createType
argument_list|(
name|clazz
argument_list|)
argument_list|,
name|name
argument_list|)
block|{
specifier|public
name|Enumerator
name|enumerator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|array
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|MySchemaFactory
implements|implements
name|SchemaFactory
block|{
specifier|public
name|Schema
name|create
parameter_list|(
name|MutableSchema
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
specifier|final
name|ReflectiveSchema
name|schema
init|=
name|ReflectiveSchema
operator|.
name|create
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
decl_stmt|;
comment|// Mine the EMPS table and add it under another name e.g. ELVIS
specifier|final
name|Table
name|table
init|=
name|schema
operator|.
name|getTable
argument_list|(
literal|"emps"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|tableName
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"tableName"
argument_list|)
decl_stmt|;
name|schema
operator|.
name|addTable
argument_list|(
operator|new
name|TableInSchemaImpl
argument_list|(
name|schema
argument_list|,
name|tableName
argument_list|,
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
argument_list|,
name|table
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Boolean
name|mutable
init|=
operator|(
name|Boolean
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"mutable"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mutable
operator|==
literal|null
operator|||
name|mutable
condition|)
block|{
return|return
name|schema
return|;
block|}
else|else
block|{
comment|// Wrap the schema in DelegatingSchema so that it does not
comment|// implement MutableSchema.
return|return
operator|new
name|DelegatingSchema
argument_list|(
name|schema
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Mock driver that has a handler that stores the results of each query in    * a temporary table. */
specifier|public
specifier|static
class|class
name|AutoTempDriver
extends|extends
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|Driver
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|results
decl_stmt|;
name|AutoTempDriver
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|results
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|results
operator|=
name|results
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Handler
name|createHandler
parameter_list|()
block|{
return|return
operator|new
name|HandlerImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onStatementExecute
parameter_list|(
name|OptiqStatement
name|statement
parameter_list|,
name|ResultSink
name|resultSink
parameter_list|)
block|{
name|super
operator|.
name|onStatementExecute
argument_list|(
name|statement
argument_list|,
name|resultSink
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|resultSink
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
comment|/** Mock driver that a given {@link Handler}. */
specifier|public
specifier|static
class|class
name|HandlerDriver
extends|extends
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|Driver
block|{
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Handler
argument_list|>
name|HANDLERS
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Handler
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|HandlerDriver
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|protected
name|Handler
name|createHandler
parameter_list|()
block|{
return|return
name|HANDLERS
operator|.
name|get
argument_list|()
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|MyTable
block|{
specifier|public
name|String
name|mykey
init|=
literal|"foo"
decl_stmt|;
specifier|public
name|Integer
name|myvalue
init|=
literal|1
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|MyTable2
block|{
specifier|public
name|String
name|mykey
init|=
literal|"foo"
decl_stmt|;
specifier|public
name|Integer
name|myvalue
init|=
literal|2
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|MySchema
block|{
specifier|public
name|MyTable
index|[]
name|mytable
init|=
block|{
operator|new
name|MyTable
argument_list|()
block|}
decl_stmt|;
specifier|public
name|MyTable2
index|[]
name|mytable2
init|=
block|{
operator|new
name|MyTable2
argument_list|()
block|}
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcTest.java
end_comment

end_unit

