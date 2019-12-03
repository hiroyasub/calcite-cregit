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
name|adapter
operator|.
name|java
operator|.
name|ReflectiveSchema
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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcCatalogSchema
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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcSchema
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
name|jdbc
operator|.
name|CalciteConnection
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
name|jdbc
operator|.
name|CalciteJdbc41Factory
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
name|jdbc
operator|.
name|CalciteSchema
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
name|jdbc
operator|.
name|Driver
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
name|schema
operator|.
name|SchemaPlus
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
name|dbcp2
operator|.
name|BasicDataSource
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
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|fail
import|;
end_import

begin_comment
comment|/** Test case for joining tables from two different JDBC databases. */
end_comment

begin_class
specifier|public
class|class
name|MultiJdbcSchemaJoinTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
comment|// Create two databases
comment|// It's two times hsqldb, but imagine they are different rdbms's
specifier|final
name|String
name|db1
init|=
name|TempDb
operator|.
name|INSTANCE
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|Connection
name|c1
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|db1
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Statement
name|stmt1
init|=
name|c1
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt1
operator|.
name|execute
argument_list|(
literal|"create table table1(id varchar(10) not null primary key, "
operator|+
literal|"field1 varchar(10))"
argument_list|)
expr_stmt|;
name|stmt1
operator|.
name|execute
argument_list|(
literal|"insert into table1 values('a', 'aaaa')"
argument_list|)
expr_stmt|;
name|c1
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|String
name|db2
init|=
name|TempDb
operator|.
name|INSTANCE
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|Connection
name|c2
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|db2
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Statement
name|stmt2
init|=
name|c2
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt2
operator|.
name|execute
argument_list|(
literal|"create table table2(id varchar(10) not null primary key, "
operator|+
literal|"field1 varchar(10))"
argument_list|)
expr_stmt|;
name|stmt2
operator|.
name|execute
argument_list|(
literal|"insert into table2 values('a', 'aaaa')"
argument_list|)
expr_stmt|;
name|c2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Connect via calcite to these databases
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
specifier|final
name|DataSource
name|ds1
init|=
name|JdbcSchema
operator|.
name|dataSource
argument_list|(
name|db1
argument_list|,
literal|"org.hsqldb.jdbcDriver"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"DB1"
argument_list|,
name|JdbcSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"DB1"
argument_list|,
name|ds1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DataSource
name|ds2
init|=
name|JdbcSchema
operator|.
name|dataSource
argument_list|(
name|db2
argument_list|,
literal|"org.hsqldb.jdbcDriver"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"DB2"
argument_list|,
name|JdbcSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"DB2"
argument_list|,
name|ds2
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Statement
name|stmt3
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|rs
init|=
name|stmt3
operator|.
name|executeQuery
argument_list|(
literal|"select table1.id, table1.field1 "
operator|+
literal|"from db1.table1 join db2.table2 on table1.id = table2.id"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|rs
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"ID=a; FIELD1=aaaa\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Makes sure that {@link #test} is re-entrant.    * Effectively a test for {@code TempDb}. */
annotation|@
name|Test
specifier|public
name|void
name|test2
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|test
argument_list|()
expr_stmt|;
block|}
comment|/** Tests {@link org.apache.calcite.adapter.jdbc.JdbcCatalogSchema}. */
annotation|@
name|Test
specifier|public
name|void
name|test3
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
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
name|TempDb
operator|.
name|INSTANCE
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setUsername
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setPassword
argument_list|(
literal|""
argument_list|)
expr_stmt|;
specifier|final
name|JdbcCatalogSchema
name|schema
init|=
name|JdbcCatalogSchema
operator|.
name|create
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
name|dataSource
argument_list|,
literal|"PUBLIC"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|schema
operator|.
name|getSubSchemaNames
argument_list|()
argument_list|,
name|is
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"INFORMATION_SCHEMA"
argument_list|,
literal|"PUBLIC"
argument_list|,
literal|"SYSTEM_LOBS"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CalciteSchema
name|rootSchema0
init|=
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|,
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|Driver
name|driver
init|=
operator|new
name|Driver
argument_list|()
decl_stmt|;
specifier|final
name|CalciteJdbc41Factory
name|factory
init|=
operator|new
name|CalciteJdbc41Factory
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c from information_schema.schemata"
decl_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|factory
operator|.
name|newConnection
argument_list|(
name|driver
argument_list|,
name|factory
argument_list|,
literal|"jdbc:calcite:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|,
name|rootSchema0
argument_list|,
literal|null
argument_list|)
init|;
name|Statement
name|stmt3
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|rs
init|=
name|stmt3
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|rs
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"C=3\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Connection
name|setup
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// Create a jdbc database& table
specifier|final
name|String
name|db
init|=
name|TempDb
operator|.
name|INSTANCE
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|Connection
name|c1
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|db
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Statement
name|stmt1
init|=
name|c1
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// This is a table we can join with the emps from the hr schema
name|stmt1
operator|.
name|execute
argument_list|(
literal|"create table table1(id integer not null primary key, "
operator|+
literal|"field1 varchar(10))"
argument_list|)
expr_stmt|;
name|stmt1
operator|.
name|execute
argument_list|(
literal|"insert into table1 values(100, 'foo')"
argument_list|)
expr_stmt|;
name|stmt1
operator|.
name|execute
argument_list|(
literal|"insert into table1 values(200, 'bar')"
argument_list|)
expr_stmt|;
name|c1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make a Calcite schema with both a jdbc schema and a non-jdbc schema
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"DB"
argument_list|,
name|JdbcSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"DB"
argument_list|,
name|JdbcSchema
operator|.
name|dataSource
argument_list|(
name|db
argument_list|,
literal|"org.hsqldb.jdbcDriver"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJdbcWithEnumerableHashJoin
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// This query works correctly
name|String
name|query
init|=
literal|"select t.id, t.field1 "
operator|+
literal|"from db.table1 t join \"hr\".\"emps\" e on e.\"empid\" = t.id"
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|runQuery
argument_list|(
name|setup
argument_list|()
argument_list|,
name|query
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEnumerableWithJdbcJoin
parameter_list|()
throws|throws
name|SQLException
block|{
comment|//  * compared to testJdbcWithEnumerableHashJoin, the join order is reversed
comment|//  * the query fails with a CannotPlanException
name|String
name|query
init|=
literal|"select t.id, t.field1 "
operator|+
literal|"from \"hr\".\"emps\" e join db.table1 t on e.\"empid\" = t.id"
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|runQuery
argument_list|(
name|setup
argument_list|()
argument_list|,
name|query
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEnumerableWithJdbcJoinWithWhereClause
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// Same query as above but with a where condition added:
comment|//  * the good: this query does not give a CannotPlanException
comment|//  * the bad: the result is wrong: there is only one emp called Bill.
comment|//             The query plan shows the join condition is always true,
comment|//             afaics, the join condition is pushed down to the non-jdbc
comment|//             table. It might have something to do with the cast that
comment|//             is introduced in the join condition.
name|String
name|query
init|=
literal|"select t.id, t.field1 "
operator|+
literal|"from \"hr\".\"emps\" e join db.table1 t on e.\"empid\" = t.id"
operator|+
literal|" where e.\"name\" = 'Bill'"
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|runQuery
argument_list|(
name|setup
argument_list|()
argument_list|,
name|query
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Set
argument_list|<
name|Integer
argument_list|>
name|runQuery
parameter_list|(
name|Connection
name|calciteConnection
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// Print out the plan
name|Statement
name|stmt
init|=
name|calciteConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
name|ResultSet
name|rs
decl_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|)
block|{
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"explain plan for "
operator|+
name|query
argument_list|)
expr_stmt|;
name|rs
operator|.
name|next
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Run the actual query
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSchemaConsistency
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a database
specifier|final
name|String
name|db
init|=
name|TempDb
operator|.
name|INSTANCE
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|Connection
name|c1
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|db
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Statement
name|stmt1
init|=
name|c1
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt1
operator|.
name|execute
argument_list|(
literal|"create table table1(id varchar(10) not null primary key, "
operator|+
literal|"field1 varchar(10))"
argument_list|)
expr_stmt|;
comment|// Connect via calcite to these databases
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
specifier|final
name|DataSource
name|ds
init|=
name|JdbcSchema
operator|.
name|dataSource
argument_list|(
name|db
argument_list|,
literal|"org.hsqldb.jdbcDriver"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"DB"
argument_list|,
name|JdbcSchema
operator|.
name|create
argument_list|(
name|rootSchema
argument_list|,
literal|"DB"
argument_list|,
name|ds
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Statement
name|stmt3
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|rs
decl_stmt|;
comment|// fails, table does not exist
try|try
block|{
name|rs
operator|=
name|stmt3
operator|.
name|executeQuery
argument_list|(
literal|"select * from db.table2"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|rs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Object 'TABLE2' not found within 'DB'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stmt1
operator|.
name|execute
argument_list|(
literal|"create table table2(id varchar(10) not null primary key, "
operator|+
literal|"field1 varchar(10))"
argument_list|)
expr_stmt|;
name|stmt1
operator|.
name|execute
argument_list|(
literal|"insert into table2 values('a', 'aaaa')"
argument_list|)
expr_stmt|;
name|PreparedStatement
name|stmt2
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select * from db.table2"
argument_list|)
decl_stmt|;
name|stmt1
operator|.
name|execute
argument_list|(
literal|"alter table table2 add column field2 varchar(10)"
argument_list|)
expr_stmt|;
comment|// "field2" not visible to stmt2
name|rs
operator|=
name|stmt2
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|rs
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"ID=a; FIELD1=aaaa\n"
argument_list|)
argument_list|)
expr_stmt|;
comment|// "field2" visible to a new query
name|rs
operator|=
name|stmt3
operator|.
name|executeQuery
argument_list|(
literal|"select * from db.table2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|rs
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"ID=a; FIELD1=aaaa; FIELD2=null\n"
argument_list|)
argument_list|)
expr_stmt|;
name|c1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Pool of temporary databases. */
specifier|static
class|class
name|TempDb
block|{
specifier|public
specifier|static
specifier|final
name|TempDb
name|INSTANCE
init|=
operator|new
name|TempDb
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|id
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TempDb
parameter_list|()
block|{
block|}
comment|/** Allocates a URL for a new Hsqldb database. */
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
literal|"jdbc:hsqldb:mem:db"
operator|+
name|id
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

