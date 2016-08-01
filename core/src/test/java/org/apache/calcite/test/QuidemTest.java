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
name|avatica
operator|.
name|AvaticaUtils
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
name|prepare
operator|.
name|Prepare
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|Schema
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
name|impl
operator|.
name|AbstractSchema
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
name|impl
operator|.
name|AbstractTable
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
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
name|Bug
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
name|TryThreadLocal
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
name|Util
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
name|base
operator|.
name|Function
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
name|Lists
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
name|io
operator|.
name|PatternFilenameFilter
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|quidem
operator|.
name|Quidem
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
name|Parameterized
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|net
operator|.
name|URL
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
name|Collection
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
comment|/**  * Test that runs every Quidem file as a test.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|QuidemTest
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|Method
name|method
decl_stmt|;
specifier|public
name|QuidemTest
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|findMethod
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Method
name|findMethod
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// E.g. path "sql/agg.iq" gives method "testSqlAgg"
name|String
name|methodName
init|=
name|AvaticaUtils
operator|.
name|toCamelCase
argument_list|(
literal|"test_"
operator|+
name|path
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'_'
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\.iq$"
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|Method
name|m
decl_stmt|;
try|try
block|{
name|m
operator|=
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|m
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
comment|/** For {@link org.junit.runners.Parameterized} runner. */
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{index}: quidem({0})"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
comment|// Start with a test file we know exists, then find the directory and list
comment|// its files.
specifier|final
name|String
name|first
init|=
literal|"sql/agg.iq"
decl_stmt|;
comment|// inUrl = "file:/home/fred/calcite/core/target/test-classes/sql/agg.iq"
specifier|final
name|URL
name|inUrl
init|=
name|JdbcTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/"
operator|+
name|first
argument_list|)
decl_stmt|;
name|String
name|x
init|=
name|inUrl
operator|.
name|getFile
argument_list|()
decl_stmt|;
assert|assert
name|x
operator|.
name|endsWith
argument_list|(
name|first
argument_list|)
assert|;
specifier|final
name|String
name|base
init|=
name|File
operator|.
name|separatorChar
operator|==
literal|'\\'
condition|?
name|x
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|x
operator|.
name|length
argument_list|()
operator|-
name|first
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
else|:
name|x
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|x
operator|.
name|length
argument_list|()
operator|-
name|first
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|File
name|firstFile
init|=
operator|new
name|File
argument_list|(
name|x
argument_list|)
decl_stmt|;
specifier|final
name|File
name|dir
init|=
name|firstFile
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|PatternFilenameFilter
argument_list|(
literal|".*\\.iq$"
argument_list|)
argument_list|)
control|)
block|{
assert|assert
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|base
argument_list|)
operator|:
literal|"f: "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"; base: "
operator|+
name|base
assert|;
name|paths
operator|.
name|add
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|substring
argument_list|(
name|base
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Lists
operator|.
name|transform
argument_list|(
name|paths
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
index|[]
name|apply
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|Object
index|[]
block|{
name|path
block|}
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkRun
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|File
name|inFile
decl_stmt|;
specifier|final
name|File
name|outFile
decl_stmt|;
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
comment|// e.g. path = "/tmp/foo.iq"
name|inFile
operator|=
name|f
expr_stmt|;
name|outFile
operator|=
operator|new
name|File
argument_list|(
name|path
operator|+
literal|".out"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// e.g. path = "sql/outer.iq"
comment|// inUrl = "file:/home/fred/calcite/core/target/test-classes/sql/outer.iq"
specifier|final
name|URL
name|inUrl
init|=
name|JdbcTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/"
operator|+
name|n2u
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|x
init|=
name|u2n
argument_list|(
name|inUrl
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|x
operator|.
name|endsWith
argument_list|(
name|path
argument_list|)
operator|:
literal|"x: "
operator|+
name|x
operator|+
literal|"; path: "
operator|+
name|path
assert|;
name|x
operator|=
name|x
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|x
operator|.
name|length
argument_list|()
operator|-
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|x
operator|.
name|endsWith
argument_list|(
name|u2n
argument_list|(
literal|"/test-classes/"
argument_list|)
argument_list|)
assert|;
name|x
operator|=
name|x
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|x
operator|.
name|length
argument_list|()
operator|-
name|u2n
argument_list|(
literal|"/test-classes/"
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|inFile
operator|=
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|u2n
argument_list|(
literal|"/test-classes/"
argument_list|)
operator|+
name|path
argument_list|)
expr_stmt|;
name|outFile
operator|=
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|u2n
argument_list|(
literal|"/surefire/"
argument_list|)
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
name|Util
operator|.
name|discard
argument_list|(
name|outFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FileReader
name|fileReader
init|=
operator|new
name|FileReader
argument_list|(
name|inFile
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|bufferedReader
init|=
operator|new
name|BufferedReader
argument_list|(
name|fileReader
argument_list|)
decl_stmt|;
specifier|final
name|FileWriter
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|outFile
argument_list|)
decl_stmt|;
operator|new
name|Quidem
argument_list|(
name|bufferedReader
argument_list|,
name|writer
argument_list|,
name|env
argument_list|()
argument_list|,
operator|new
name|QuidemConnectionFactory
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
specifier|final
name|String
name|diff
init|=
name|DiffTestCase
operator|.
name|diff
argument_list|(
name|inFile
argument_list|,
name|outFile
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Files differ: "
operator|+
name|outFile
operator|+
literal|" "
operator|+
name|inFile
operator|+
literal|"\n"
operator|+
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Converts a path from Unix to native. On Windows, converts    * forward-slashes to back-slashes; on Linux, does nothing. */
specifier|private
specifier|static
name|String
name|u2n
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|File
operator|.
name|separatorChar
operator|==
literal|'\\'
condition|?
name|s
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'\\'
argument_list|)
else|:
name|s
return|;
block|}
specifier|private
specifier|static
name|String
name|n2u
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|File
operator|.
name|separatorChar
operator|==
literal|'\\'
condition|?
name|s
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
else|:
name|s
return|;
block|}
specifier|private
name|Function
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|env
parameter_list|()
block|{
return|return
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|String
name|varName
parameter_list|)
block|{
switch|switch
condition|(
name|varName
condition|)
block|{
case|case
literal|"jdk18"
case|:
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1.8"
argument_list|)
return|;
case|case
literal|"fixed"
case|:
return|return
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|String
name|v
parameter_list|)
block|{
switch|switch
condition|(
name|v
condition|)
block|{
case|case
literal|"calcite1045"
case|:
return|return
name|Bug
operator|.
name|CALCITE_1045_FIXED
return|;
case|case
literal|"calcite1048"
case|:
return|return
name|Bug
operator|.
name|CALCITE_1048_FIXED
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|method
operator|.
name|invoke
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Override settings for "sql/misc.iq". */
specifier|public
name|void
name|testSqlMisc
parameter_list|()
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|CalciteAssert
operator|.
name|DB
condition|)
block|{
case|case
name|ORACLE
case|:
comment|// There are formatting differences (e.g. "4.000" vs "4") when using
comment|// Oracle as the JDBC data source.
return|return;
block|}
try|try
init|(
specifier|final
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Override settings for "sql/scalar.iq". */
specifier|public
name|void
name|testSqlScalar
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
specifier|final
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Runs the dummy script "sql/dummy.iq", which is checked in empty but    * which you may use as scratch space during development. */
comment|// Do not add disable this test; just remember not to commit changes to dummy.iq
specifier|public
name|void
name|testSqlDummy
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
specifier|final
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Quidem connection factory for Calcite's built-in test schemas. */
specifier|private
specifier|static
class|class
name|QuidemConnectionFactory
implements|implements
name|Quidem
operator|.
name|NewConnectionFactory
block|{
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|connect
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|reference
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"foodmart"
argument_list|)
condition|)
block|{
specifier|final
name|ConnectionSpec
name|db
init|=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
operator|.
name|foodmart
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|db
operator|.
name|url
argument_list|,
name|db
operator|.
name|username
argument_list|,
name|db
operator|.
name|password
argument_list|)
decl_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"foodmart"
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
return|return
literal|null
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"hr"
argument_list|)
condition|)
block|{
return|return
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|connect
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"foodmart"
argument_list|)
condition|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|connect
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"scott"
argument_list|)
condition|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|SCOTT
argument_list|)
operator|.
name|connect
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"jdbc_scott"
argument_list|)
condition|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_SCOTT
argument_list|)
operator|.
name|connect
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"post"
argument_list|)
condition|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|POST
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"POST"
argument_list|)
operator|.
name|connect
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"catchall"
argument_list|)
condition|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withSchema
argument_list|(
literal|"s"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|ReflectiveSchemaTest
operator|.
name|CatchallSchema
argument_list|()
argument_list|)
argument_list|)
operator|.
name|connect
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"orinoco"
argument_list|)
condition|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|ORINOCO
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"ORINOCO"
argument_list|)
operator|.
name|connect
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"seq"
argument_list|)
condition|)
block|{
specifier|final
name|Connection
name|connection
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withSchema
argument_list|(
literal|"s"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
operator|.
name|connect
argument_list|()
decl_stmt|;
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
operator|.
name|getRootSchema
argument_list|()
operator|.
name|getSubSchema
argument_list|(
literal|"s"
argument_list|)
operator|.
name|add
argument_list|(
literal|"my_seq"
argument_list|,
operator|new
name|AbstractTable
argument_list|()
block|{
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"$seq"
argument_list|,
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|SEQUENCE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown connection '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End QuidemTest.java
end_comment

end_unit

