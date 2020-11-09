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
name|rel
operator|.
name|RelNode
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
name|SqlDialect
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
name|dialect
operator|.
name|CalciteSqlDialect
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
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|Matchers
operator|.
name|hasTree
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

begin_comment
comment|/**  * Tests for {@code PigRelOpVisitor}.  */
end_comment

begin_class
class|class
name|PigRelOpTest
extends|extends
name|PigRelTestBase
block|{
comment|/**    * SQL dialect for the tests.    */
specifier|private
specifier|static
class|class
name|PigRelSqlDialect
extends|extends
name|SqlDialect
block|{
specifier|static
specifier|final
name|SqlDialect
name|DEFAULT
init|=
operator|new
name|CalciteSqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|CALCITE
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|PigRelSqlDialect
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Contains a Pig script and has various methods to translate and    * run that script and check the results. Each method returns    * this, so that method calls for the same script can be    * chained. */
class|class
name|Fluent
block|{
specifier|private
specifier|final
name|String
name|script
decl_stmt|;
name|Fluent
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
specifier|private
name|Fluent
name|assertRel
parameter_list|(
name|String
name|pigAlias
parameter_list|,
name|boolean
name|optimized
parameter_list|,
name|Matcher
argument_list|<
name|RelNode
argument_list|>
name|relMatcher
parameter_list|)
block|{
try|try
block|{
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|relNodes
init|=
name|converter
operator|.
name|pigQuery2Rel
argument_list|(
name|script
argument_list|,
name|optimized
argument_list|,
literal|true
argument_list|,
name|optimized
argument_list|)
decl_stmt|;
if|if
condition|(
name|pigAlias
operator|==
literal|null
condition|)
block|{
name|rel
operator|=
name|relNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rel
operator|=
name|converter
operator|.
name|getBuilder
argument_list|()
operator|.
name|getRel
argument_list|(
name|pigAlias
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|rel
argument_list|,
name|relMatcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
return|return
name|this
return|;
block|}
specifier|private
name|Fluent
name|assertRel
parameter_list|(
name|Matcher
argument_list|<
name|RelNode
argument_list|>
name|relMatcher
parameter_list|)
block|{
return|return
name|assertRel
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|,
name|relMatcher
argument_list|)
return|;
block|}
specifier|private
name|Fluent
name|assertOptimizedRel
parameter_list|(
name|Matcher
argument_list|<
name|RelNode
argument_list|>
name|relMatcher
parameter_list|)
block|{
return|return
name|assertRel
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|relMatcher
argument_list|)
return|;
block|}
specifier|private
name|Fluent
name|assertSql
parameter_list|(
name|Matcher
argument_list|<
name|String
argument_list|>
name|sqlMatcher
parameter_list|)
block|{
try|try
block|{
specifier|final
name|String
name|sql
init|=
name|converter
operator|.
name|pigToSql
argument_list|(
name|script
argument_list|,
name|PigRelSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sql
argument_list|,
name|sqlMatcher
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
catch|catch
parameter_list|(
name|IOException
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
specifier|private
name|Fluent
name|assertResult
parameter_list|(
name|Matcher
argument_list|<
name|String
argument_list|>
name|resultMatcher
parameter_list|)
block|{
specifier|final
name|RelNode
name|rel
decl_stmt|;
try|try
block|{
name|rel
operator|=
name|converter
operator|.
name|pigQuery2Rel
argument_list|(
name|script
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CalciteHandler
operator|.
name|dump
argument_list|(
name|rel
argument_list|,
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|resultMatcher
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|writeToFile
parameter_list|(
name|File
name|f
parameter_list|,
name|String
index|[]
name|inputData
parameter_list|)
block|{
try|try
init|(
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|input
range|:
name|inputData
control|)
block|{
name|pw
operator|.
name|print
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
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
comment|/** Creates a {@link Fluent} containing a script, that can then be used to    * translate and execute that script. */
specifier|private
name|Fluent
name|pig
parameter_list|(
name|String
name|script
parameter_list|)
block|{
return|return
operator|new
name|Fluent
argument_list|(
name|script
argument_list|)
return|;
block|}
annotation|@
name|Test
name|void
name|testLoadFromFile
parameter_list|()
block|{
specifier|final
name|String
name|datadir
init|=
literal|"/tmp/pigdata"
decl_stmt|;
specifier|final
name|String
name|schema
init|=
literal|"{\"fields\":["
operator|+
literal|"{\"name\":\"x\",\"type\":55,\"schema\":null},"
operator|+
literal|"{\"name\":\"y\",\"type\":10,\"schema\":null},"
operator|+
literal|"{\"name\":\"z\",\"type\":25,\"schema\":null}],"
operator|+
literal|"\"version\":0,\"sortKeys\":[],\"sortKeyOrders\":[]}"
decl_stmt|;
specifier|final
name|File
name|inputDir
init|=
operator|new
name|File
argument_list|(
name|datadir
argument_list|,
literal|"testTable"
argument_list|)
decl_stmt|;
name|inputDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
specifier|final
name|File
name|inputSchemaFile
init|=
operator|new
name|File
argument_list|(
name|inputDir
argument_list|,
literal|".pig_schema"
argument_list|)
decl_stmt|;
name|writeToFile
argument_list|(
name|inputSchemaFile
argument_list|,
operator|new
name|String
index|[]
block|{
name|schema
block|}
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD '"
operator|+
name|inputDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"' using PigStorage();\n"
operator|+
literal|"B = FILTER A BY z> 5.5;\n"
operator|+
literal|"C = GROUP B BY x;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[$0], B=[$1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0}], B=[COLLECT($1)])\n"
operator|+
literal|"    LogicalProject(x=[$0], $f1=[ROW($0, $1, $2)])\n"
operator|+
literal|"      LogicalFilter(condition=[>($2, 5.5E0)])\n"
operator|+
literal|"        LogicalTableScan(table=[[/tmp/pigdata/testTable]])\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLoadWithoutSchema
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|"A = LOAD 'scott.DEPT';"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(10,ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(20,RESEARCH,DALLAS)\n"
operator|+
literal|"(30,SALES,CHICAGO)\n"
operator|+
literal|"(40,OPERATIONS,BOSTON)\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLoadWithSchema
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'testSchema.testTable' as (a:int, b:long, c:float, "
operator|+
literal|"d:double, e:chararray, "
operator|+
literal|"f:bytearray, g:boolean, "
operator|+
literal|"h:datetime, i:biginteger, j:bigdecimal, k1:tuple(), k2:tuple"
operator|+
literal|"(k21:int, k22:float), "
operator|+
literal|"l1:bag{}, "
operator|+
literal|"l2:bag{l21:(l22:int, l23:float)}, m1:map[], m2:map[int], m3:map["
operator|+
literal|"(m3:float)])\n;"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"LogicalTableScan(table=[[testSchema, testTable]])\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script1
init|=
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);"
decl_stmt|;
name|pig
argument_list|(
name|script1
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
literal|"LogicalTableScan(table=[[scott, DEPT]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilter
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
operator|+
literal|"B = FILTER A BY DEPTNO == 10;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalFilter(condition=[=($0, 10)])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|"(10,ACCOUNTING,NEW YORK)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"WHERE DEPTNO = 10"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSample
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
operator|+
literal|"B = SAMPLE A 0.5;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalFilter(condition=[<(RAND(), 5E-1)])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"WHERE RAND()< 0.5"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSplit
parameter_list|()
block|{
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP'as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"SPLIT A INTO B1 IF DEPTNO == 10, B2 IF  DEPTNO == 20;\n"
operator|+
literal|"B = UNION B1, B2;\n"
decl_stmt|;
specifier|final
name|String
name|scan
init|=
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalUnion(all=[true])\n"
operator|+
literal|"  LogicalFilter(condition=[=($7, 10)])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  LogicalFilter(condition=[=($7, 20)])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(7782,CLARK,MANAGER,7839,1981-06-09,2450.00,null,10)\n"
operator|+
literal|"(7839,KING,PRESIDENT,null,1981-11-17,5000.00,null,10)\n"
operator|+
literal|"(7934,MILLER,CLERK,7782,1982-01-23,1300.00,null,10)\n"
operator|+
literal|"(7369,SMITH,CLERK,7902,1980-12-17,800.00,null,20)\n"
operator|+
literal|"(7566,JONES,MANAGER,7839,1981-02-04,2975.00,null,20)\n"
operator|+
literal|"(7788,SCOTT,ANALYST,7566,1987-04-19,3000.00,null,20)\n"
operator|+
literal|"(7876,ADAMS,CLERK,7788,1987-05-23,1100.00,null,20)\n"
operator|+
literal|"(7902,FORD,ANALYST,7566,1981-12-03,3000.00,null,20)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"WHERE DEPTNO = 10\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"WHERE DEPTNO = 20"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
literal|"B1"
argument_list|,
literal|false
argument_list|,
name|hasTree
argument_list|(
literal|"LogicalFilter(condition=[=($7, 10)])\n"
operator|+
name|scan
argument_list|)
argument_list|)
operator|.
name|assertRel
argument_list|(
literal|"B2"
argument_list|,
literal|false
argument_list|,
name|hasTree
argument_list|(
literal|"LogicalFilter(condition=[=($7, 20)])\n"
operator|+
name|scan
argument_list|)
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUdf
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
operator|+
literal|"B = FILTER A BY ENDSWITH(DNAME, 'LES');\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalFilter(condition=[ENDSWITH(PIG_TUPLE($1, 'LES'))])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|"(30,SALES,CHICAGO)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"WHERE ENDSWITH(PIG_TUPLE(DNAME, 'LES'))"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSimpleForEach1
parameter_list|()
block|{
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'testSchema.testTable' as (a:int, b:long, c:float, "
operator|+
literal|"d:double, e:chararray, f:bytearray, g:boolean, "
operator|+
literal|"h:datetime, i:biginteger, j:bigdecimal, k1:tuple(), "
operator|+
literal|"k2:tuple(k21:int, k22:float), l1:bag{}, "
operator|+
literal|"l2:bag{l21:(l22:int, l23:float)}, "
operator|+
literal|"m1:map[], m2:map[int], m3:map[(m3:float)]);\n"
operator|+
literal|"B = FOREACH A GENERATE a, a as a2, b, c, d, e, f, g, h, i, j, k2, "
operator|+
literal|"l2, m2, null as n:chararray;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(a=[$0], a2=[$0], b=[$1], c=[$2], d=[$3], e=[$4], "
operator|+
literal|"f=[$5], g=[$6], h=[$7], i=[$8], j=[$9], k2=[$11], l2=[$13], "
operator|+
literal|"m2=[$15], n=[null:VARCHAR])\n"
operator|+
literal|"  LogicalTableScan(table=[[testSchema, testTable]])\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT a, a AS a2, b, c, d, e, f, g, h, i, j, k2, l2, m2, "
operator|+
literal|"CAST(NULL AS VARCHAR CHARACTER SET ISO-8859-1) AS n\n"
operator|+
literal|"FROM testSchema.testTable"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSimpleForEach2
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = FOREACH A GENERATE DEPTNO + 10 as dept, MGR;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(dept=[+($7, 10)], MGR=[$3])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(30,7902)\n"
operator|+
literal|"(40,7698)\n"
operator|+
literal|"(40,7698)\n"
operator|+
literal|"(30,7839)\n"
operator|+
literal|"(40,7698)\n"
operator|+
literal|"(40,7839)\n"
operator|+
literal|"(20,7839)\n"
operator|+
literal|"(30,7566)\n"
operator|+
literal|"(20,null)\n"
operator|+
literal|"(40,7698)\n"
operator|+
literal|"(30,7788)\n"
operator|+
literal|"(40,7698)\n"
operator|+
literal|"(30,7566)\n"
operator|+
literal|"(20,7782)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT DEPTNO + 10 AS dept, MGR\n"
operator|+
literal|"FROM scott.EMP"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSimpleForEach3
parameter_list|()
block|{
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = FILTER A BY JOB != 'CLERK';\n"
operator|+
literal|"C = GROUP B BY (DEPTNO, JOB);\n"
operator|+
literal|"D = FOREACH C GENERATE flatten(group) as (dept, job), flatten(B);\n"
operator|+
literal|"E = ORDER D BY dept, job;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[ASC])\n"
operator|+
literal|"  LogicalProject(dept=[$0], job=[$1], EMPNO=[$3], ENAME=[$4], "
operator|+
literal|"JOB=[$5], MGR=[$6], HIREDATE=[$7], SAL=[$8], COMM=[$9], "
operator|+
literal|"DEPTNO=[$10])\n"
operator|+
literal|"    LogicalCorrelate(correlation=[$cor0], joinType=[inner], "
operator|+
literal|"requiredColumns=[{2}])\n"
operator|+
literal|"      LogicalProject(dept=[$0.DEPTNO], job=[$0.JOB], B=[$1])\n"
operator|+
literal|"        LogicalProject(group=[ROW($0, $1)], B=[$2])\n"
operator|+
literal|"          LogicalAggregate(group=[{0, 1}], B=[COLLECT($2)])\n"
operator|+
literal|"            LogicalProject(DEPTNO=[$7], JOB=[$2], $f2=[ROW($0, "
operator|+
literal|"$1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"              LogicalFilter(condition=[<>($2, 'CLERK')])\n"
operator|+
literal|"                LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"      Uncollect\n"
operator|+
literal|"        LogicalProject($f0=[$cor0.B])\n"
operator|+
literal|"          LogicalValues(tuples=[[{ 0 }]])\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT $cor1.DEPTNO AS dept, $cor1.JOB AS job, $cor1.EMPNO,"
operator|+
literal|" $cor1.ENAME, $cor1.JOB0 AS JOB, $cor1.MGR, $cor1.HIREDATE,"
operator|+
literal|" $cor1.SAL, $cor1.COMM, $cor1.DEPTNO0 AS DEPTNO\n"
operator|+
literal|"FROM (SELECT DEPTNO, JOB, COLLECT(ROW(EMPNO, ENAME, JOB, MGR, "
operator|+
literal|"HIREDATE, SAL, COMM, DEPTNO)) AS $f2\n"
operator|+
literal|"    FROM scott.EMP\n"
operator|+
literal|"    WHERE JOB<> 'CLERK'\n"
operator|+
literal|"    GROUP BY DEPTNO, JOB) AS $cor1,\n"
operator|+
literal|"  LATERAL UNNEST (SELECT $cor1.$f2 AS $f0\n"
operator|+
literal|"    FROM (VALUES (0)) AS t (ZERO)) AS t3 (EMPNO, ENAME, JOB,"
operator|+
literal|" MGR, HIREDATE, SAL, COMM, DEPTNO) AS t30\n"
operator|+
literal|"ORDER BY $cor1.DEPTNO, $cor1.JOB"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO fix Calcite execution
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(10,7782,CLARK,MANAGER,7839,1981-06-09,2450.00,null,10)\n"
operator|+
literal|"(10,7839,KING,PRESIDENT,null,1981-11-17,5000.00,null,10)\n"
operator|+
literal|"(20,7566,JONES,MANAGER,7839,1981-02-04,2975.00,null,20)\n"
operator|+
literal|"(20,7788,SCOTT,ANALYST,7566,1987-04-19,3000.00,null,20)\n"
operator|+
literal|"(20,7902,FORD,ANALYST,7566,1981-12-03,3000.00,null,20)\n"
operator|+
literal|"(30,7499,ALLEN,SALESMAN,7698,1981-02-20,1600.00,300.00,30)\n"
operator|+
literal|"(30,7521,WARD,SALESMAN,7698,1981-02-22,1250.00,500.00,30)\n"
operator|+
literal|"(30,7654,MARTIN,SALESMAN,7698,1981-09-28,1250.00,1400.00,30)\n"
operator|+
literal|"(30,7698,BLAKE,MANAGER,7839,1981-01-05,2850.00,null,30)\n"
operator|+
literal|"(30,7844,TURNER,SALESMAN,7698,1981-09-08,1500.00,0.00,30)\n"
decl_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testForEachNested
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY DEPTNO;\n"
operator|+
literal|"C = FOREACH B {\n"
operator|+
literal|"      S = FILTER A BY JOB != 'CLERK';\n"
operator|+
literal|"      Y = FOREACH S GENERATE ENAME, JOB, DEPTNO, SAL;\n"
operator|+
literal|"      X = ORDER Y BY SAL;\n"
operator|+
literal|"      GENERATE group, COUNT(X) as cnt, flatten(X), BigDecimalMax(X.SAL);\n"
operator|+
literal|"}\n"
operator|+
literal|"D = ORDER C BY $0;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(group=[$0], cnt=[$1], ENAME=[$4], JOB=[$5], "
operator|+
literal|"DEPTNO=[$6], SAL=[$7], $f3=[$3])\n"
operator|+
literal|"    LogicalCorrelate(correlation=[$cor1], joinType=[inner], "
operator|+
literal|"requiredColumns=[{2}])\n"
operator|+
literal|"      LogicalProject(group=[$0], cnt=[COUNT(PIG_BAG($2))], X=[$2], "
operator|+
literal|"$f3=[BigDecimalMax(PIG_BAG(MULTISET_PROJECTION($2, 3)))])\n"
operator|+
literal|"        LogicalCorrelate(correlation=[$cor0], joinType=[inner], "
operator|+
literal|"requiredColumns=[{1}])\n"
operator|+
literal|"          LogicalProject(group=[$0], A=[$1])\n"
operator|+
literal|"            LogicalAggregate(group=[{0}], A=[COLLECT($1)])\n"
operator|+
literal|"              LogicalProject(DEPTNO=[$7], $f1=[ROW($0, $1, $2, "
operator|+
literal|"$3, $4, $5, $6, $7)])\n"
operator|+
literal|"                LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"          LogicalProject(X=[$1])\n"
operator|+
literal|"            LogicalAggregate(group=[{0}], X=[COLLECT($1)])\n"
operator|+
literal|"              LogicalProject($f0=['all'], $f1=[ROW($0, $1, $2, $3)])\n"
operator|+
literal|"                LogicalSort(sort0=[$3], dir0=[ASC])\n"
operator|+
literal|"                  LogicalProject(ENAME=[$1], JOB=[$2], "
operator|+
literal|"DEPTNO=[$7], SAL=[$5])\n"
operator|+
literal|"                    LogicalFilter(condition=[<>($2, 'CLERK')])\n"
operator|+
literal|"                      Uncollect\n"
operator|+
literal|"                        LogicalProject($f0=[$cor0.A])\n"
operator|+
literal|"                          LogicalValues(tuples=[[{ 0 }]])\n"
operator|+
literal|"      Uncollect\n"
operator|+
literal|"        LogicalProject($f0=[$cor1.X])\n"
operator|+
literal|"          LogicalValues(tuples=[[{ 0 }]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(10,2,CLARK,MANAGER,10,2450.00,5000.00)\n"
operator|+
literal|"(10,2,KING,PRESIDENT,10,5000.00,5000.00)\n"
operator|+
literal|"(20,3,JONES,MANAGER,20,2975.00,3000.00)\n"
operator|+
literal|"(20,3,SCOTT,ANALYST,20,3000.00,3000.00)\n"
operator|+
literal|"(20,3,FORD,ANALYST,20,3000.00,3000.00)\n"
operator|+
literal|"(30,5,WARD,SALESMAN,30,1250.00,2850.00)\n"
operator|+
literal|"(30,5,MARTIN,SALESMAN,30,1250.00,2850.00)\n"
operator|+
literal|"(30,5,TURNER,SALESMAN,30,1500.00,2850.00)\n"
operator|+
literal|"(30,5,ALLEN,SALESMAN,30,1600.00,2850.00)\n"
operator|+
literal|"(30,5,BLAKE,MANAGER,30,2850.00,2850.00)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT $cor5.group, $cor5.cnt, $cor5.ENAME, $cor5.JOB, "
operator|+
literal|"$cor5.DEPTNO, $cor5.SAL, $cor5.$f3\n"
operator|+
literal|"FROM (SELECT $cor4.DEPTNO AS group, "
operator|+
literal|"COUNT(PIG_BAG($cor4.X)) AS cnt, $cor4.X, "
operator|+
literal|"BigDecimalMax(PIG_BAG(MULTISET_PROJECTION($cor4.X, 3))) AS $f3\n"
operator|+
literal|"    FROM (SELECT DEPTNO, COLLECT(ROW(EMPNO, ENAME, JOB, MGR, "
operator|+
literal|"HIREDATE, SAL, COMM, DEPTNO)) AS A\n"
operator|+
literal|"        FROM scott.EMP\n"
operator|+
literal|"        GROUP BY DEPTNO) AS $cor4,\n"
operator|+
literal|"      LATERAL (SELECT COLLECT(ROW(ENAME, JOB, DEPTNO, SAL)) AS X\n"
operator|+
literal|"        FROM (SELECT ENAME, JOB, DEPTNO, SAL\n"
operator|+
literal|"            FROM UNNEST (SELECT $cor4.A AS $f0\n"
operator|+
literal|"                FROM (VALUES (0)) AS t (ZERO)) "
operator|+
literal|"AS t2 (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO)\n"
operator|+
literal|"            WHERE JOB<> 'CLERK'\n"
operator|+
literal|"            ORDER BY SAL) AS t5\n"
operator|+
literal|"        GROUP BY 'all') AS t8) AS $cor5,\n"
operator|+
literal|"  LATERAL UNNEST (SELECT $cor5.X AS $f0\n"
operator|+
literal|"    FROM (VALUES (0)) AS t (ZERO)) "
operator|+
literal|"AS t11 (ENAME, JOB, DEPTNO, SAL) AS t110\n"
operator|+
literal|"ORDER BY $cor5.group"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnionSameSchema
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = FILTER A BY DEPTNO == 10;\n"
operator|+
literal|"C = FILTER A BY DEPTNO == 20;\n"
operator|+
literal|"D = UNION B, C;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalUnion(all=[true])\n"
operator|+
literal|"  LogicalFilter(condition=[=($7, 10)])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  LogicalFilter(condition=[=($7, 20)])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(7782,CLARK,MANAGER,7839,1981-06-09,2450.00,null,10)\n"
operator|+
literal|"(7839,KING,PRESIDENT,null,1981-11-17,5000.00,null,10)\n"
operator|+
literal|"(7934,MILLER,CLERK,7782,1982-01-23,1300.00,null,10)\n"
operator|+
literal|"(7369,SMITH,CLERK,7902,1980-12-17,800.00,null,20)\n"
operator|+
literal|"(7566,JONES,MANAGER,7839,1981-02-04,2975.00,null,20)\n"
operator|+
literal|"(7788,SCOTT,ANALYST,7566,1987-04-19,3000.00,null,20)\n"
operator|+
literal|"(7876,ADAMS,CLERK,7788,1987-05-23,1100.00,null,20)\n"
operator|+
literal|"(7902,FORD,ANALYST,7566,1981-12-03,3000.00,null,20)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"WHERE DEPTNO = 10\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"WHERE DEPTNO = 20"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnionDifferentSchemas1
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
operator|+
literal|"B = FOREACH A GENERATE DEPTNO, DNAME;\n"
operator|+
literal|"C = UNION ONSCHEMA A, B;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalUnion(all=[true])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0], DNAME=[$1], LOC=[null:VARCHAR])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalUnion(all=[true])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0], DNAME=[$1], LOC=[null:VARCHAR])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(10,ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(20,RESEARCH,DALLAS)\n"
operator|+
literal|"(30,SALES,CHICAGO)\n"
operator|+
literal|"(40,OPERATIONS,BOSTON)\n"
operator|+
literal|"(10,ACCOUNTING,null)\n"
operator|+
literal|"(20,RESEARCH,null)\n"
operator|+
literal|"(30,SALES,null)\n"
operator|+
literal|"(40,OPERATIONS,null)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT DEPTNO, DNAME, "
operator|+
literal|"CAST(NULL AS VARCHAR CHARACTER SET ISO-8859-1) AS LOC\n"
operator|+
literal|"FROM scott.DEPT"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnionDifferentSchemas2
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = FILTER A BY DEPTNO == 10;\n"
operator|+
literal|"C = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
operator|+
literal|"D = UNION ONSCHEMA B, C;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalUnion(all=[true])\n"
operator|+
literal|"  LogicalProject(EMPNO=[$0], ENAME=[$1], JOB=[$2], MGR=[$3], "
operator|+
literal|"HIREDATE=[$4], SAL=[$5], COMM=[$6], DEPTNO=[$7], "
operator|+
literal|"DNAME=[null:VARCHAR], LOC=[null:VARCHAR])\n"
operator|+
literal|"    LogicalFilter(condition=[=($7, 10)])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  LogicalProject(EMPNO=[null:INTEGER], ENAME=[null:VARCHAR], "
operator|+
literal|"JOB=[null:VARCHAR], MGR=[null:INTEGER], HIREDATE=[null:DATE], "
operator|+
literal|"SAL=[null:DECIMAL(19, 0)], COMM=[null:DECIMAL(19, 0)], DEPTNO=[$0], "
operator|+
literal|"DNAME=[$1], LOC=[$2])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(7782,CLARK,MANAGER,7839,1981-06-09,2450.00,null,10,null,null)\n"
operator|+
literal|"(7839,KING,PRESIDENT,null,1981-11-17,5000.00,null,10,null,"
operator|+
literal|"null)\n"
operator|+
literal|"(7934,MILLER,CLERK,7782,1982-01-23,1300.00,null,10,null,null)\n"
operator|+
literal|"(null,null,null,null,null,null,null,10,ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(null,null,null,null,null,null,null,20,RESEARCH,DALLAS)\n"
operator|+
literal|"(null,null,null,null,null,null,null,30,SALES,CHICAGO)\n"
operator|+
literal|"(null,null,null,null,null,null,null,40,OPERATIONS,BOSTON)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO, "
operator|+
literal|"CAST(NULL AS VARCHAR CHARACTER SET ISO-8859-1) AS DNAME, "
operator|+
literal|"CAST(NULL AS VARCHAR CHARACTER SET ISO-8859-1) AS LOC\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"WHERE DEPTNO = 10\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT CAST(NULL AS INTEGER) AS EMPNO, "
operator|+
literal|"CAST(NULL AS VARCHAR CHARACTER SET ISO-8859-1) AS ENAME, "
operator|+
literal|"CAST(NULL AS VARCHAR CHARACTER SET ISO-8859-1) AS JOB, "
operator|+
literal|"CAST(NULL AS INTEGER) AS MGR, "
operator|+
literal|"CAST(NULL AS DATE) AS HIREDATE, CAST(NULL AS DECIMAL(19, 0)) AS SAL, "
operator|+
literal|"CAST(NULL AS DECIMAL(19, 0)) AS COMM, DEPTNO, DNAME, LOC\n"
operator|+
literal|"FROM scott.DEPT"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoin2Rels
parameter_list|()
block|{
specifier|final
name|String
name|scanScript
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
decl_stmt|;
specifier|final
name|String
name|scanPlan
init|=
literal|""
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|innerScript
init|=
name|scanScript
operator|+
literal|"C = JOIN A BY DEPTNO, B BY DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[=($7, $8)], joinType=[inner])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|innerSql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"  INNER JOIN scott.DEPT ON EMP.DEPTNO = DEPT.DEPTNO"
decl_stmt|;
name|pig
argument_list|(
name|innerScript
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|innerSql
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|leftScript
init|=
name|scanScript
operator|+
literal|"C = JOIN A BY DEPTNO LEFT OUTER, B BY DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|leftSql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"  LEFT JOIN scott.DEPT ON EMP.DEPTNO = DEPT.DEPTNO"
decl_stmt|;
specifier|final
name|String
name|leftPlan
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[=($7, $8)], joinType=[left])\n"
operator|+
name|scanPlan
decl_stmt|;
name|pig
argument_list|(
name|leftScript
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|leftPlan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|leftSql
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|rightScript
init|=
name|scanScript
operator|+
literal|"C = JOIN A BY DEPTNO RIGHT OUTER, B BY DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|rightSql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"  RIGHT JOIN scott.DEPT ON EMP.DEPTNO = DEPT.DEPTNO"
decl_stmt|;
specifier|final
name|String
name|rightPlan
init|=
literal|"LogicalJoin(condition=[=($7, $8)], joinType=[right])\n"
operator|+
name|scanPlan
decl_stmt|;
name|pig
argument_list|(
name|rightScript
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|rightPlan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|rightSql
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fullScript
init|=
name|scanScript
operator|+
literal|"C = JOIN A BY DEPTNO FULL, B BY DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|fullPlan
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[=($7, $8)], joinType=[full])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|fullSql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"  FULL JOIN scott.DEPT ON EMP.DEPTNO = DEPT.DEPTNO"
decl_stmt|;
specifier|final
name|String
name|fullResult
init|=
literal|""
operator|+
literal|"(7369,SMITH,CLERK,7902,1980-12-17,800.00,null,20,20,"
operator|+
literal|"RESEARCH,DALLAS)\n"
operator|+
literal|"(7499,ALLEN,SALESMAN,7698,1981-02-20,1600.00,300.00,30,30,"
operator|+
literal|"SALES,CHICAGO)\n"
operator|+
literal|"(7521,WARD,SALESMAN,7698,1981-02-22,1250.00,500.00,30,30,"
operator|+
literal|"SALES,CHICAGO)\n"
operator|+
literal|"(7566,JONES,MANAGER,7839,1981-02-04,2975.00,null,20,20,"
operator|+
literal|"RESEARCH,DALLAS)\n"
operator|+
literal|"(7654,MARTIN,SALESMAN,7698,1981-09-28,1250.00,1400.00,30,30,"
operator|+
literal|"SALES,CHICAGO)\n"
operator|+
literal|"(7698,BLAKE,MANAGER,7839,1981-01-05,2850.00,null,30,30,"
operator|+
literal|"SALES,CHICAGO)\n"
operator|+
literal|"(7782,CLARK,MANAGER,7839,1981-06-09,2450.00,null,10,10,"
operator|+
literal|"ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(7788,SCOTT,ANALYST,7566,1987-04-19,3000.00,null,20,20,"
operator|+
literal|"RESEARCH,DALLAS)\n"
operator|+
literal|"(7839,KING,PRESIDENT,null,1981-11-17,5000.00,null,10,10,"
operator|+
literal|"ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(7844,TURNER,SALESMAN,7698,1981-09-08,1500.00,0.00,30,30,"
operator|+
literal|"SALES,CHICAGO)\n"
operator|+
literal|"(7876,ADAMS,CLERK,7788,1987-05-23,1100.00,null,20,20,"
operator|+
literal|"RESEARCH,DALLAS)\n"
operator|+
literal|"(7900,JAMES,CLERK,7698,1981-12-03,950.00,null,30,30,SALES,"
operator|+
literal|"CHICAGO)\n"
operator|+
literal|"(7902,FORD,ANALYST,7566,1981-12-03,3000.00,null,20,20,"
operator|+
literal|"RESEARCH,DALLAS)\n"
operator|+
literal|"(7934,MILLER,CLERK,7782,1982-01-23,1300.00,null,10,10,"
operator|+
literal|"ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(null,null,null,null,null,null,null,null,40,OPERATIONS,"
operator|+
literal|"BOSTON)\n"
decl_stmt|;
name|pig
argument_list|(
name|fullScript
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|fullPlan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|fullSql
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|fullResult
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoin3Rels
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
operator|+
literal|"C = FILTER B BY LOC == 'CHICAGO';\n"
operator|+
literal|"D = JOIN A BY DEPTNO, B BY DEPTNO, C BY DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[=($7, $11)], joinType=[inner])\n"
operator|+
literal|"  LogicalJoin(condition=[=($7, $8)], joinType=[inner])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  LogicalFilter(condition=[=($2, 'CHICAGO')])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"  INNER JOIN scott.DEPT ON EMP.DEPTNO = DEPT.DEPTNO\n"
operator|+
literal|"  INNER JOIN (SELECT *\n"
operator|+
literal|"    FROM scott.DEPT\n"
operator|+
literal|"    WHERE LOC = 'CHICAGO') AS t ON EMP.DEPTNO = t.DEPTNO"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(7499,ALLEN,SALESMAN,7698,1981-02-20,1600.00,300.00,30,30,"
operator|+
literal|"SALES,CHICAGO,30,SALES,"
operator|+
literal|"CHICAGO)\n"
operator|+
literal|"(7521,WARD,SALESMAN,7698,1981-02-22,1250.00,500.00,30,30,"
operator|+
literal|"SALES,CHICAGO,30,SALES,"
operator|+
literal|"CHICAGO)\n"
operator|+
literal|"(7654,MARTIN,SALESMAN,7698,1981-09-28,1250.00,1400.00,30,30,"
operator|+
literal|"SALES,CHICAGO,30,"
operator|+
literal|"SALES,CHICAGO)\n"
operator|+
literal|"(7698,BLAKE,MANAGER,7839,1981-01-05,2850.00,null,30,30,SALES,"
operator|+
literal|"CHICAGO,30,SALES,"
operator|+
literal|"CHICAGO)\n"
operator|+
literal|"(7844,TURNER,SALESMAN,7698,1981-09-08,1500.00,0.00,30,30,"
operator|+
literal|"SALES,CHICAGO,30,SALES,"
operator|+
literal|"CHICAGO)\n"
operator|+
literal|"(7900,JAMES,CLERK,7698,1981-12-03,950.00,null,30,30,SALES,"
operator|+
literal|"CHICAGO,30,SALES,"
operator|+
literal|"CHICAGO)\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script2
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray,\n"
operator|+
literal|"    LOC:CHARARRAY);\n"
operator|+
literal|"C = FILTER B BY LOC == 'CHICAGO';\n"
operator|+
literal|"D = JOIN A BY (DEPTNO, ENAME), B BY (DEPTNO, DNAME),\n"
operator|+
literal|"    C BY (DEPTNO, DNAME);\n"
decl_stmt|;
specifier|final
name|String
name|plan2
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[AND(=($7, $11), =($9, $12))], "
operator|+
literal|"joinType=[inner])\n"
operator|+
literal|"  LogicalJoin(condition=[AND(=($7, $8), =($1, $9))], "
operator|+
literal|"joinType=[inner])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  LogicalFilter(condition=[=($2, 'CHICAGO')])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"  INNER JOIN scott.DEPT ON EMP.DEPTNO = DEPT.DEPTNO "
operator|+
literal|"AND EMP.ENAME = DEPT.DNAME\n"
operator|+
literal|"  INNER JOIN (SELECT *\n"
operator|+
literal|"    FROM scott.DEPT\n"
operator|+
literal|"    WHERE LOC = 'CHICAGO') AS t ON EMP.DEPTNO = t.DEPTNO "
operator|+
literal|"AND DEPT.DNAME = t.DNAME"
decl_stmt|;
name|pig
argument_list|(
name|script2
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan2
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCross
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray,\n"
operator|+
literal|"    LOC:CHARARRAY);\n"
operator|+
literal|"B = FOREACH A GENERATE DEPTNO;\n"
operator|+
literal|"C = FILTER B BY DEPTNO<= 20;\n"
operator|+
literal|"D = CROSS B, C;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  LogicalFilter(condition=[<=($0, 20)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$0])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT DEPTNO\n"
operator|+
literal|"    FROM scott.DEPT) AS t,\n"
operator|+
literal|"    (SELECT DEPTNO\n"
operator|+
literal|"    FROM scott.DEPT\n"
operator|+
literal|"    WHERE DEPTNO<= 20) AS t1"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(10,10)\n"
operator|+
literal|"(10,20)\n"
operator|+
literal|"(20,10)\n"
operator|+
literal|"(20,20)\n"
operator|+
literal|"(30,10)\n"
operator|+
literal|"(30,20)\n"
operator|+
literal|"(40,10)\n"
operator|+
literal|"(40,20)\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script2
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray,"
operator|+
literal|"    LOC:CHARARRAY);\n"
operator|+
literal|"B = FOREACH A GENERATE DEPTNO;\n"
operator|+
literal|"C = FILTER B BY DEPTNO<= 20;\n"
operator|+
literal|"D = FILTER B BY DEPTNO> 20;\n"
operator|+
literal|"E = CROSS B, C, D;\n"
decl_stmt|;
specifier|final
name|String
name|plan2
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"  LogicalJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$0])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"    LogicalFilter(condition=[<=($0, 20)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$0])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  LogicalFilter(condition=[>($0, 20)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$0])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result2
init|=
literal|""
operator|+
literal|"(10,10,30)\n"
operator|+
literal|"(10,10,40)\n"
operator|+
literal|"(10,20,30)\n"
operator|+
literal|"(10,20,40)\n"
operator|+
literal|"(20,10,30)\n"
operator|+
literal|"(20,10,40)\n"
operator|+
literal|"(20,20,30)\n"
operator|+
literal|"(20,20,40)\n"
operator|+
literal|"(30,10,30)\n"
operator|+
literal|"(30,10,40)\n"
operator|+
literal|"(30,20,30)\n"
operator|+
literal|"(30,20,40)\n"
operator|+
literal|"(40,10,30)\n"
operator|+
literal|"(40,10,40)\n"
operator|+
literal|"(40,20,30)\n"
operator|+
literal|"(40,20,40)\n"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|""
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT DEPTNO\n"
operator|+
literal|"    FROM scott.DEPT) AS t,\n"
operator|+
literal|"    (SELECT DEPTNO\n"
operator|+
literal|"    FROM scott.DEPT\n"
operator|+
literal|"    WHERE DEPTNO<= 20) AS t1,\n"
operator|+
literal|"    (SELECT DEPTNO\n"
operator|+
literal|"    FROM scott.DEPT\n"
operator|+
literal|"    WHERE DEPTNO> 20) AS t3"
decl_stmt|;
name|pig
argument_list|(
name|script2
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan2
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result2
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupby
parameter_list|()
block|{
specifier|final
name|String
name|baseScript
init|=
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
decl_stmt|;
specifier|final
name|String
name|basePlan
init|=
literal|"      LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|script
init|=
name|baseScript
operator|+
literal|"B = GROUP A BY DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[$0], A=[$1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0}], A=[COLLECT($1)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$0], $f1=[ROW($0, $1, $2)])\n"
operator|+
name|basePlan
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(20,{(20,RESEARCH,DALLAS)})\n"
operator|+
literal|"(40,{(40,OPERATIONS,BOSTON)})\n"
operator|+
literal|"(10,{(10,ACCOUNTING,NEW YORK)})\n"
operator|+
literal|"(30,{(30,SALES,CHICAGO)})\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT DEPTNO, COLLECT(ROW(DEPTNO, DNAME, LOC)) AS A\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"GROUP BY DEPTNO"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script1
init|=
name|baseScript
operator|+
literal|"B = GROUP A ALL;\n"
decl_stmt|;
specifier|final
name|String
name|plan1
init|=
literal|""
operator|+
literal|"LogicalProject(group=[$0], A=[$1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0}], A=[COLLECT($1)])\n"
operator|+
literal|"    LogicalProject($f0=['all'], $f1=[ROW($0, $1, $2)])\n"
operator|+
name|basePlan
decl_stmt|;
specifier|final
name|String
name|result1
init|=
literal|""
operator|+
literal|"(all,{(10,ACCOUNTING,NEW YORK),(20,RESEARCH,DALLAS),"
operator|+
literal|"(30,SALES,CHICAGO),(40,OPERATIONS,BOSTON)})\n"
decl_stmt|;
name|pig
argument_list|(
name|script1
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result1
argument_list|)
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupby2
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = FOREACH A GENERATE EMPNO, ENAME, JOB, MGR, SAL, COMM, DEPTNO;\n"
operator|+
literal|"C = GROUP B BY (DEPTNO, JOB);\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[ROW($0, $1)], B=[$2])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], B=[COLLECT($2)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$6], JOB=[$2], $f2=[ROW($0, $1, $2, "
operator|+
literal|"$3, $4, $5, $6)])\n"
operator|+
literal|"      LogicalProject(EMPNO=[$0], ENAME=[$1], JOB=[$2], MGR=[$3],"
operator|+
literal|" SAL=[$5], COMM=[$6], DEPTNO=[$7])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"({10, MANAGER},{(7782,CLARK,MANAGER,7839,2450.00,null,10)})\n"
operator|+
literal|"({10, PRESIDENT},{(7839,KING,PRESIDENT,null,5000.00,null,10)})\n"
operator|+
literal|"({20, CLERK},{(7369,SMITH,CLERK,7902,800.00,null,20),"
operator|+
literal|"(7876,ADAMS,CLERK,7788,1100.00,null,20)})\n"
operator|+
literal|"({30, MANAGER},{(7698,BLAKE,MANAGER,7839,2850.00,null,30)})\n"
operator|+
literal|"({20, ANALYST},{(7788,SCOTT,ANALYST,7566,3000.00,null,20),"
operator|+
literal|"(7902,FORD,ANALYST,7566,3000.00,null,20)})\n"
operator|+
literal|"({30, SALESMAN},{(7499,ALLEN,SALESMAN,7698,1600.00,300.00,30),"
operator|+
literal|"(7521,WARD,SALESMAN,7698,1250.00,500.00,30),"
operator|+
literal|"(7654,MARTIN,SALESMAN,7698,1250.00,1400.00,30),"
operator|+
literal|"(7844,TURNER,SALESMAN,7698,1500.00,0.00,30)})\n"
operator|+
literal|"({30, CLERK},{(7900,JAMES,CLERK,7698,950.00,null,30)})\n"
operator|+
literal|"({20, MANAGER},{(7566,JONES,MANAGER,7839,2975.00,null,20)})\n"
operator|+
literal|"({10, CLERK},{(7934,MILLER,CLERK,7782,1300.00,null,10)})\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCubeCube
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = CUBE A BY CUBE(DEPTNO, JOB);\n"
operator|+
literal|"C = FOREACH B GENERATE group, COUNT(cube.ENAME);\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[$0], $f1=[COUNT(PIG_BAG"
operator|+
literal|"(MULTISET_PROJECTION($1, 3)))])\n"
operator|+
literal|"  LogicalProject(group=[ROW($0, $1)], cube=[$2])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1}], "
operator|+
literal|"groups=[[{0, 1}, {0}, {1}, {}]], cube=[COLLECT($2)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], JOB=[$2], "
operator|+
literal|"$f2=[ROW($7, $2, $0, $1, $3, $4, $5, $6)])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[ROW($0, $1)], $f1=[CAST($2):BIGINT])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], "
operator|+
literal|"groups=[[{0, 1}, {0}, {1}, {}]], agg#0=[COUNT($2)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$7], JOB=[$2], ENAME=[$1])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"({30, SALESMAN},4)\n"
operator|+
literal|"({30, null},6)\n"
operator|+
literal|"({10, null},3)\n"
operator|+
literal|"({null, PRESIDENT},1)\n"
operator|+
literal|"({30, MANAGER},1)\n"
operator|+
literal|"({20, MANAGER},1)\n"
operator|+
literal|"({20, ANALYST},2)\n"
operator|+
literal|"({10, MANAGER},1)\n"
operator|+
literal|"({null, CLERK},4)\n"
operator|+
literal|"({null, null},14)\n"
operator|+
literal|"({20, null},5)\n"
operator|+
literal|"({10, PRESIDENT},1)\n"
operator|+
literal|"({null, ANALYST},2)\n"
operator|+
literal|"({null, SALESMAN},4)\n"
operator|+
literal|"({30, CLERK},1)\n"
operator|+
literal|"({10, CLERK},1)\n"
operator|+
literal|"({20, CLERK},2)\n"
operator|+
literal|"({null, MANAGER},3)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT ROW(DEPTNO, JOB) AS group,"
operator|+
literal|" CAST(COUNT(ENAME) AS BIGINT) AS $f1\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY CUBE(DEPTNO, JOB)"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCubeRollup
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = CUBE A BY ROLLUP(DEPTNO, JOB);\n"
operator|+
literal|"C = FOREACH B GENERATE group, COUNT(cube.ENAME);\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[$0], $f1=[COUNT(PIG_BAG"
operator|+
literal|"(MULTISET_PROJECTION($1, 3)))])\n"
operator|+
literal|"  LogicalProject(group=[ROW($0, $1)], cube=[$2])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {1}, {}]],"
operator|+
literal|" cube=[COLLECT($2)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], JOB=[$2], $f2=[ROW($7, $2, $0,"
operator|+
literal|" $1, $3, $4, $5, $6)])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[ROW($0, $1)], $f1=[CAST($2):BIGINT])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {1}, {}]], "
operator|+
literal|"agg#0=[COUNT($2)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$7], JOB=[$2], ENAME=[$1])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"({30, SALESMAN},4)\n"
operator|+
literal|"({null, PRESIDENT},1)\n"
operator|+
literal|"({30, MANAGER},1)\n"
operator|+
literal|"({20, MANAGER},1)\n"
operator|+
literal|"({20, ANALYST},2)\n"
operator|+
literal|"({10, MANAGER},1)\n"
operator|+
literal|"({null, CLERK},4)\n"
operator|+
literal|"({null, null},14)\n"
operator|+
literal|"({10, PRESIDENT},1)\n"
operator|+
literal|"({null, ANALYST},2)\n"
operator|+
literal|"({null, SALESMAN},4)\n"
operator|+
literal|"({30, CLERK},1)\n"
operator|+
literal|"({10, CLERK},1)\n"
operator|+
literal|"({20, CLERK},2)\n"
operator|+
literal|"({null, MANAGER},3)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT ROW(DEPTNO, JOB) AS group, "
operator|+
literal|"CAST(COUNT(ENAME) AS BIGINT) AS $f1\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY ROLLUP(DEPTNO, JOB)"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetProjection
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray,\n"
operator|+
literal|"    LOC:CHARARRAY);\n"
operator|+
literal|"B = GROUP A BY DEPTNO;\n"
operator|+
literal|"C = FOREACH B GENERATE A.(DEPTNO, DNAME);\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject($f0=[MULTISET_PROJECTION($1, 0, 1)])\n"
operator|+
literal|"  LogicalProject(group=[$0], A=[$1])\n"
operator|+
literal|"    LogicalAggregate(group=[{0}], A=[COLLECT($1)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$0], $f1=[ROW($0, $1, $2)])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalProject($f0=[$1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0}], agg#0=[COLLECT($1)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$0], $f2=[ROW($0, $1)])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"({(20,RESEARCH)})\n"
operator|+
literal|"({(40,OPERATIONS)})\n"
operator|+
literal|"({(10,ACCOUNTING)})\n"
operator|+
literal|"({(30,SALES)})\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT COLLECT(ROW(DEPTNO, DNAME)) AS $f0\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"GROUP BY DEPTNO"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrderBy
parameter_list|()
block|{
specifier|final
name|String
name|scan
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray,\n"
operator|+
literal|"    LOC:CHARARRAY);\n"
decl_stmt|;
specifier|final
name|String
name|scanPlan
init|=
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|plan0
init|=
literal|"LogicalSort(sort0=[$1], dir0=[ASC])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|script0
init|=
name|scan
operator|+
literal|"B = ORDER A BY DNAME;\n"
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"ORDER BY DNAME"
decl_stmt|;
specifier|final
name|String
name|result0
init|=
literal|""
operator|+
literal|"(10,ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(40,OPERATIONS,BOSTON)\n"
operator|+
literal|"(20,RESEARCH,DALLAS)\n"
operator|+
literal|"(30,SALES,CHICAGO)\n"
decl_stmt|;
name|pig
argument_list|(
name|script0
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan0
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql0
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result0
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|plan1
init|=
literal|"LogicalSort(sort0=[$1], dir0=[DESC])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|script1
init|=
name|scan
operator|+
literal|"B = ORDER A BY DNAME DESC;\n"
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"ORDER BY DNAME DESC"
decl_stmt|;
name|pig
argument_list|(
name|script1
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan1
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql1
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|plan2
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], sort1=[$0], dir0=[DESC], dir1=[ASC])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|script2
init|=
name|scan
operator|+
literal|"B = ORDER A BY LOC DESC, DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"ORDER BY LOC DESC, DEPTNO"
decl_stmt|;
name|pig
argument_list|(
name|script2
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan2
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|plan3
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$0], sort1=[$1], sort2=[$2], dir0=[ASC], dir1=[ASC], dir2=[ASC])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|script3
init|=
name|scan
operator|+
literal|"B = ORDER A BY *;\n"
decl_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"ORDER BY DEPTNO, DNAME, LOC"
decl_stmt|;
name|pig
argument_list|(
name|script3
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan3
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql3
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|plan4
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$0], sort1=[$1], sort2=[$2], dir0=[DESC], dir1=[DESC], dir2=[DESC])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|script4
init|=
name|scan
operator|+
literal|"B = ORDER A BY * DESC;\n"
decl_stmt|;
specifier|final
name|String
name|result4
init|=
literal|""
operator|+
literal|"(40,OPERATIONS,BOSTON)\n"
operator|+
literal|"(30,SALES,CHICAGO)\n"
operator|+
literal|"(20,RESEARCH,DALLAS)\n"
operator|+
literal|"(10,ACCOUNTING,NEW YORK)\n"
decl_stmt|;
name|pig
argument_list|(
name|script4
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan4
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result4
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRank
parameter_list|()
block|{
specifier|final
name|String
name|base
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = FOREACH A GENERATE EMPNO, JOB, DEPTNO;\n"
decl_stmt|;
specifier|final
name|String
name|basePlan
init|=
literal|""
operator|+
literal|"  LogicalProject(EMPNO=[$0], JOB=[$2], DEPTNO=[$7])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalProject(rank_C=[$3], EMPNO=[$0], JOB=[$1], DEPTNO=[$2])\n"
operator|+
literal|"  LogicalWindow(window#0=[window(order by [2, 1 DESC] "
operator|+
literal|"aggs [RANK()])])\n"
operator|+
literal|"    LogicalProject(EMPNO=[$0], JOB=[$2], DEPTNO=[$7])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|script
init|=
name|base
operator|+
literal|"C = RANK B BY DEPTNO ASC, JOB DESC;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(rank_C=[RANK() OVER (ORDER BY $2, $1 DESC)], "
operator|+
literal|"EMPNO=[$0], JOB=[$1], DEPTNO=[$2])\n"
operator|+
name|basePlan
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(1,7839,PRESIDENT,10)\n"
operator|+
literal|"(2,7782,MANAGER,10)\n"
operator|+
literal|"(3,7934,CLERK,10)\n"
operator|+
literal|"(4,7566,MANAGER,20)\n"
operator|+
literal|"(5,7369,CLERK,20)\n"
operator|+
literal|"(5,7876,CLERK,20)\n"
operator|+
literal|"(7,7788,ANALYST,20)\n"
operator|+
literal|"(7,7902,ANALYST,20)\n"
operator|+
literal|"(9,7499,SALESMAN,30)\n"
operator|+
literal|"(9,7521,SALESMAN,30)\n"
operator|+
literal|"(9,7654,SALESMAN,30)\n"
operator|+
literal|"(9,7844,SALESMAN,30)\n"
operator|+
literal|"(13,7698,MANAGER,30)\n"
operator|+
literal|"(14,7900,CLERK,30)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT RANK() OVER (ORDER BY DEPTNO, JOB DESC RANGE BETWEEN "
operator|+
literal|"UNBOUNDED PRECEDING AND CURRENT ROW) AS rank_C, EMPNO, JOB, DEPTNO\n"
operator|+
literal|"FROM scott.EMP"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script2
init|=
name|base
operator|+
literal|"C = RANK B BY DEPTNO ASC, JOB DESC DENSE;\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan2
init|=
literal|""
operator|+
literal|"LogicalProject(rank_C=[$3], EMPNO=[$0], JOB=[$1], DEPTNO=[$2])\n"
operator|+
literal|"  LogicalWindow(window#0=[window(order by [2, 1 DESC] "
operator|+
literal|"aggs [DENSE_RANK()])"
operator|+
literal|"])\n"
operator|+
literal|"    LogicalProject(EMPNO=[$0], JOB=[$2], DEPTNO=[$7])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|plan2
init|=
literal|""
operator|+
literal|"LogicalProject(rank_C=[DENSE_RANK() OVER (ORDER BY $2, $1 DESC)], "
operator|+
literal|"EMPNO=[$0], JOB=[$1], DEPTNO=[$2])\n"
operator|+
name|basePlan
decl_stmt|;
specifier|final
name|String
name|result2
init|=
literal|""
operator|+
literal|"(1,7839,PRESIDENT,10)\n"
operator|+
literal|"(2,7782,MANAGER,10)\n"
operator|+
literal|"(3,7934,CLERK,10)\n"
operator|+
literal|"(4,7566,MANAGER,20)\n"
operator|+
literal|"(5,7369,CLERK,20)\n"
operator|+
literal|"(5,7876,CLERK,20)\n"
operator|+
literal|"(6,7788,ANALYST,20)\n"
operator|+
literal|"(6,7902,ANALYST,20)\n"
operator|+
literal|"(7,7499,SALESMAN,30)\n"
operator|+
literal|"(7,7521,SALESMAN,30)\n"
operator|+
literal|"(7,7654,SALESMAN,30)\n"
operator|+
literal|"(7,7844,SALESMAN,30)\n"
operator|+
literal|"(8,7698,MANAGER,30)\n"
operator|+
literal|"(9,7900,CLERK,30)\n"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|""
operator|+
literal|"SELECT DENSE_RANK() OVER (ORDER BY DEPTNO, JOB DESC RANGE BETWEEN "
operator|+
literal|"UNBOUNDED PRECEDING AND CURRENT ROW) AS rank_C, EMPNO, JOB, DEPTNO\n"
operator|+
literal|"FROM scott.EMP"
decl_stmt|;
name|pig
argument_list|(
name|script2
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan2
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan2
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result2
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLimit
parameter_list|()
block|{
specifier|final
name|String
name|scan
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray,\n"
operator|+
literal|"    LOC:CHARARRAY);\n"
decl_stmt|;
specifier|final
name|String
name|scanPlan
init|=
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|plan1
init|=
literal|"LogicalSort(sort0=[$1], dir0=[ASC], fetch=[2])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|script1
init|=
name|scan
operator|+
literal|"B = ORDER A BY DNAME;\n"
operator|+
literal|"C = LIMIT B 2;\n"
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"ORDER BY DNAME\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
decl_stmt|;
specifier|final
name|String
name|result1
init|=
literal|""
operator|+
literal|"(10,ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(40,OPERATIONS,BOSTON)\n"
decl_stmt|;
name|pig
argument_list|(
name|script1
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan1
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql1
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result1
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|plan2
init|=
literal|"LogicalSort(fetch=[2])\n"
operator|+
name|scanPlan
decl_stmt|;
specifier|final
name|String
name|script2
init|=
name|scan
operator|+
literal|"B = LIMIT A 2;\n"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM scott.DEPT\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
decl_stmt|;
name|pig
argument_list|(
name|script2
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan2
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDistinct
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = FOREACH A GENERATE DEPTNO;\n"
operator|+
literal|"C = DISTINCT B;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalAggregate(group=[{0}])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$7])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(20)\n"
operator|+
literal|"(10)\n"
operator|+
literal|"(30)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"SELECT DEPTNO\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregate
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY DEPTNO;\n"
operator|+
literal|"C = FOREACH B GENERATE group, COUNT(A), BigDecimalSum(A.SAL);\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[$0], $f1=[COUNT(PIG_BAG($1))], "
operator|+
literal|"$f2=[BigDecimalSum(PIG_BAG(MULTISET_PROJECTION($1, 5)))])\n"
operator|+
literal|"  LogicalProject(group=[$0], A=[$1])\n"
operator|+
literal|"    LogicalAggregate(group=[{0}], A=[COLLECT($1)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], $f1=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalProject(group=[$0], $f1=[CAST($1):BIGINT], $f2=[CAST($2):DECIMAL(19, 0)])\n"
operator|+
literal|"  LogicalAggregate(group=[{0}], agg#0=[COUNT()], agg#1=[SUM($1)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$7], SAL=[$5])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(20,5,10875.00)\n"
operator|+
literal|"(10,3,8750.00)\n"
operator|+
literal|"(30,6,9400.00)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT DEPTNO AS group, CAST(COUNT(*) AS BIGINT) AS $f1, CAST(SUM(SAL) AS "
operator|+
literal|"DECIMAL(19, 0)) AS $f2\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregate2
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY (DEPTNO, MGR, HIREDATE);\n"
operator|+
literal|"C = FOREACH B GENERATE group, COUNT(A), SUM(A.SAL) as salSum;\n"
operator|+
literal|"D = ORDER C BY salSum;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(group=[$0], $f1=[COUNT(PIG_BAG($1))], "
operator|+
literal|"salSum=[BigDecimalSum(PIG_BAG(MULTISET_PROJECTION($1, 5)))])\n"
operator|+
literal|"    LogicalProject(group=[ROW($0, $1, $2)], A=[$3])\n"
operator|+
literal|"      LogicalAggregate(group=[{0, 1, 2}], A=[COLLECT($3)])\n"
operator|+
literal|"        LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], "
operator|+
literal|"$f3=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"          LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(group=[ROW($0, $1, $2)], $f1=[CAST($3):BIGINT], "
operator|+
literal|"salSum=[CAST($4):DECIMAL(19, 0)])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1, 2}], agg#0=[COUNT()], agg#1=[SUM($3)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], SAL=[$5])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"({20, 7902, 1980-12-17},1,800.00)\n"
operator|+
literal|"({30, 7698, 1981-12-03},1,950.00)\n"
operator|+
literal|"({20, 7788, 1987-05-23},1,1100.00)\n"
operator|+
literal|"({30, 7698, 1981-09-28},1,1250.00)\n"
operator|+
literal|"({30, 7698, 1981-02-22},1,1250.00)\n"
operator|+
literal|"({10, 7782, 1982-01-23},1,1300.00)\n"
operator|+
literal|"({30, 7698, 1981-09-08},1,1500.00)\n"
operator|+
literal|"({30, 7698, 1981-02-20},1,1600.00)\n"
operator|+
literal|"({10, 7839, 1981-06-09},1,2450.00)\n"
operator|+
literal|"({30, 7839, 1981-01-05},1,2850.00)\n"
operator|+
literal|"({20, 7839, 1981-02-04},1,2975.00)\n"
operator|+
literal|"({20, 7566, 1981-12-03},1,3000.00)\n"
operator|+
literal|"({20, 7566, 1987-04-19},1,3000.00)\n"
operator|+
literal|"({10, null, 1981-11-17},1,5000.00)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT ROW(DEPTNO, MGR, HIREDATE) AS group, CAST(COUNT(*) AS "
operator|+
literal|"BIGINT) AS $f1, CAST(SUM(SAL) AS DECIMAL(19, 0)) AS salSum\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO, MGR, HIREDATE\n"
operator|+
literal|"ORDER BY CAST(SUM(SAL) AS DECIMAL(19, 0))"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregate2half
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY (DEPTNO, MGR, HIREDATE);\n"
operator|+
literal|"C = FOREACH B GENERATE flatten(group) as (DEPTNO, MGR, HIREDATE),\n"
operator|+
literal|"    COUNT(A), SUM(A.SAL) as salSum, MAX(A.DEPTNO) as maxDep,\n"
operator|+
literal|"    MIN(A.HIREDATE) as minHire;\n"
operator|+
literal|"D = ORDER C BY salSum;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0.DEPTNO], MGR=[$0.MGR], HIREDATE=[$0.HIREDATE], "
operator|+
literal|"$f3=[COUNT(PIG_BAG($1))], salSum=[BigDecimalSum(PIG_BAG(MULTISET_PROJECTION($1, 5)))]"
operator|+
literal|", maxDep=[IntMax(PIG_BAG(MULTISET_PROJECTION($1, 7)))], minHire=[DateTimeMin(PIG_BAG"
operator|+
literal|"(MULTISET_PROJECTION($1, 4)))])\n"
operator|+
literal|"    LogicalProject(group=[ROW($0, $1, $2)], A=[$3])\n"
operator|+
literal|"      LogicalAggregate(group=[{0, 1, 2}], A=[COLLECT($3)])\n"
operator|+
literal|"        LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], "
operator|+
literal|"$f3=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"          LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0], MGR=[$1], HIREDATE=[$2], $f3=[CAST($3):BIGINT], "
operator|+
literal|"salSum=[CAST($4):DECIMAL(19, 0)], maxDep=[CAST($5):INTEGER], minHire=[$6])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1, 2}], agg#0=[COUNT()"
operator|+
literal|"], agg#1=[SUM($3)], agg#2=[MAX($0)], agg#3=[MIN($2)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], SAL=[$5])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script2
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY (DEPTNO, MGR, HIREDATE);\n"
operator|+
literal|"C = FOREACH B GENERATE group.DEPTNO, COUNT(A), SUM(A.SAL) as salSum, "
operator|+
literal|"group.MGR, MAX(A.DEPTNO) as maxDep, MIN(A.HIREDATE) as minHire;\n"
operator|+
literal|"D = ORDER C BY salSum;\n"
decl_stmt|;
specifier|final
name|String
name|plan2
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0.DEPTNO], $f1=[COUNT(PIG_BAG($1))], "
operator|+
literal|"salSum=[BigDecimalSum(PIG_BAG(MULTISET_PROJECTION($1, 5)))], "
operator|+
literal|"MGR=[$0.MGR], maxDep=[IntMax(PIG_BAG(MULTISET_PROJECTION($1, 7)"
operator|+
literal|"))], minHire=[DateTimeMin(PIG_BAG(MULTISET_PROJECTION($1, 4)))])\n"
operator|+
literal|"    LogicalProject(group=[ROW($0, $1, $2)], A=[$3])\n"
operator|+
literal|"      LogicalAggregate(group=[{0, 1, 2}], A=[COLLECT($3)])\n"
operator|+
literal|"        LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], "
operator|+
literal|"$f3=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"          LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan2
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0], $f1=[CAST($3):BIGINT], salSum=[CAST($4):DECIMAL(19, 0)]"
operator|+
literal|", MGR=[$1], maxDep=[CAST($5):INTEGER], minHire=[$6])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1, 2}], agg#0=[COUNT()], agg#1=[SUM($3)], "
operator|+
literal|"agg#2=[MAX($0)], agg#3=[MIN($2)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], SAL=[$5])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|pig
argument_list|(
name|script2
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan2
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregate3
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY (DEPTNO, MGR, HIREDATE);\n"
operator|+
literal|"C = FOREACH B GENERATE group, COUNT(A) + 1, BigDecimalSum(A.SAL) as "
operator|+
literal|"salSum, BigDecimalSum(A.SAL) / COUNT(A) as salAvg;\n"
operator|+
literal|"D = ORDER C BY salSum;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(group=[$0], $f1=[+(COUNT(PIG_BAG($1)), 1)], "
operator|+
literal|"salSum=[BigDecimalSum(PIG_BAG(MULTISET_PROJECTION($1, 5)))], "
operator|+
literal|"salAvg=[/(BigDecimalSum(PIG_BAG(MULTISET_PROJECTION($1, 5))), "
operator|+
literal|"CAST(COUNT(PIG_BAG($1))):DECIMAL(19, 0))])\n"
operator|+
literal|"    LogicalProject(group=[ROW($0, $1, $2)], A=[$3])\n"
operator|+
literal|"      LogicalAggregate(group=[{0, 1, 2}], A=[COLLECT($3)])\n"
operator|+
literal|"        LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], "
operator|+
literal|"$f3=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"          LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(group=[ROW($0, $1, $2)], $f1=[+(CAST($3):BIGINT, 1)], "
operator|+
literal|"salSum=[CAST($4):DECIMAL(19, 0)], salAvg=[/(CAST($4):DECIMAL(19, 0), CAST(CAST($3)"
operator|+
literal|":BIGINT):DECIMAL(19, 0))])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1, 2}], agg#0=[COUNT()], agg#1=[SUM($3)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], SAL=[$5])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"({20, 7902, 1980-12-17},2,800.00,800.00)\n"
operator|+
literal|"({30, 7698, 1981-12-03},2,950.00,950.00)\n"
operator|+
literal|"({20, 7788, 1987-05-23},2,1100.00,1100.00)\n"
operator|+
literal|"({30, 7698, 1981-09-28},2,1250.00,1250.00)\n"
operator|+
literal|"({30, 7698, 1981-02-22},2,1250.00,1250.00)\n"
operator|+
literal|"({10, 7782, 1982-01-23},2,1300.00,1300.00)\n"
operator|+
literal|"({30, 7698, 1981-09-08},2,1500.00,1500.00)\n"
operator|+
literal|"({30, 7698, 1981-02-20},2,1600.00,1600.00)\n"
operator|+
literal|"({10, 7839, 1981-06-09},2,2450.00,2450.00)\n"
operator|+
literal|"({30, 7839, 1981-01-05},2,2850.00,2850.00)\n"
operator|+
literal|"({20, 7839, 1981-02-04},2,2975.00,2975.00)\n"
operator|+
literal|"({20, 7566, 1981-12-03},2,3000.00,3000.00)\n"
operator|+
literal|"({20, 7566, 1987-04-19},2,3000.00,3000.00)\n"
operator|+
literal|"({10, null, 1981-11-17},2,5000.00,5000.00)\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT ROW(DEPTNO, MGR, HIREDATE) AS group, CAST(COUNT(*) AS "
operator|+
literal|"BIGINT) + 1 AS $f1, CAST(SUM(SAL) AS DECIMAL(19, 0)) AS salSum, "
operator|+
literal|"CAST(SUM(SAL) AS DECIMAL(19, 0)) / CAST(CAST(COUNT(*) AS BIGINT) "
operator|+
literal|"AS DECIMAL(19, 0)) AS salAvg\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO, MGR, HIREDATE\n"
operator|+
literal|"ORDER BY CAST(SUM(SAL) AS DECIMAL(19, 0))"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script2
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY (DEPTNO, MGR, HIREDATE);\n"
operator|+
literal|"C = FOREACH B GENERATE group, COUNT(A) + 1, BigDecimalSum(A.SAL) as salSum, "
operator|+
literal|"BigDecimalSum(A.SAL) / COUNT(A) as salAvg, A;\n"
operator|+
literal|"D = ORDER C BY salSum;\n"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|""
operator|+
literal|"SELECT ROW(DEPTNO, MGR, HIREDATE) AS group, CAST(COUNT(*) AS BIGINT) + 1"
operator|+
literal|" AS $f1, CAST(SUM(SAL) AS DECIMAL(19, 0)) AS salSum, CAST(SUM(SAL) AS "
operator|+
literal|"DECIMAL(19, 0)) / CAST(CAST(COUNT(*) AS BIGINT) AS DECIMAL(19, 0)) AS "
operator|+
literal|"salAvg, COLLECT(ROW(EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO)"
operator|+
literal|") AS A\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO, MGR, HIREDATE\n"
operator|+
literal|"ORDER BY CAST(SUM(SAL) AS DECIMAL(19, 0))"
decl_stmt|;
name|pig
argument_list|(
name|script2
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|script3
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY (DEPTNO, MGR, HIREDATE);\n"
operator|+
literal|"C = FOREACH B GENERATE group, A, COUNT(A);\n"
decl_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|""
operator|+
literal|"SELECT ROW(DEPTNO, MGR, HIREDATE) AS group, COLLECT(ROW(EMPNO, ENAME, "
operator|+
literal|"JOB, MGR, HIREDATE, SAL, COMM, DEPTNO)) AS A, CAST(COUNT(*) AS BIGINT) "
operator|+
literal|"AS $f2\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO, MGR, HIREDATE"
decl_stmt|;
name|pig
argument_list|(
name|script3
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregate4
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.EMP' as (EMPNO:int, ENAME:chararray,\n"
operator|+
literal|"    JOB:chararray, MGR:int, HIREDATE:datetime, SAL:bigdecimal,\n"
operator|+
literal|"    COMM:bigdecimal, DEPTNO:int);\n"
operator|+
literal|"B = GROUP A BY (DEPTNO, MGR, HIREDATE);\n"
operator|+
literal|"C = FOREACH B GENERATE FLATTEN(group) as (DEPTNO, MGR, HIREDATE), "
operator|+
literal|"COUNT(A), 1L as newCol, A.COMM as comArray, SUM(A.SAL) as salSum;\n"
operator|+
literal|"D = ORDER C BY salSum;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$6], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0.DEPTNO], MGR=[$0.MGR], HIREDATE=[$0.HIREDATE], "
operator|+
literal|"$f3=[COUNT(PIG_BAG($1))], newCol=[1:BIGINT], comArray=[MULTISET_PROJECTION($1, 6)], "
operator|+
literal|"salSum=[BigDecimalSum(PIG_BAG(MULTISET_PROJECTION($1, 5)))])\n"
operator|+
literal|"    LogicalProject(group=[ROW($0, $1, $2)], A=[$3])\n"
operator|+
literal|"      LogicalAggregate(group=[{0, 1, 2}], A=[COLLECT($3)])\n"
operator|+
literal|"        LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], "
operator|+
literal|"$f3=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"          LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|optimizedPlan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$6], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0], MGR=[$1], HIREDATE=[$2], $f3=[CAST($3):BIGINT], "
operator|+
literal|"newCol=[1:BIGINT], comArray=[$4], salSum=[CAST($5):DECIMAL(19, 0)])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1, 2}], agg#0=[COUNT()], agg#1=[COLLECT($3)], "
operator|+
literal|"agg#2=[SUM($4)])\n"
operator|+
literal|"      LogicalProject(DEPTNO=[$7], MGR=[$3], HIREDATE=[$4], COMM=[$6], SAL=[$5])\n"
operator|+
literal|"        LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT DEPTNO, MGR, HIREDATE, CAST(COUNT(*) AS BIGINT) AS $f3, 1 AS newCol, "
operator|+
literal|"COLLECT(COMM) AS comArray, CAST(SUM(SAL) AS DECIMAL(19, 0)) AS salSum\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO, MGR, HIREDATE\n"
operator|+
literal|"ORDER BY CAST(SUM(SAL) AS DECIMAL(19, 0))"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertOptimizedRel
argument_list|(
name|hasTree
argument_list|(
name|optimizedPlan
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCoGroup
parameter_list|()
block|{
specifier|final
name|String
name|script
init|=
literal|""
operator|+
literal|"A = LOAD 'scott.DEPT' as (DEPTNO:int, DNAME:chararray, LOC:CHARARRAY);\n"
operator|+
literal|"B = FILTER A BY DEPTNO<= 30;\n"
operator|+
literal|"C = FILTER A BY DEPTNO>= 20;\n"
operator|+
literal|"D = GROUP A BY DEPTNO + 10, B BY (int) DEPTNO, C BY (int) DEPTNO;\n"
operator|+
literal|"E = ORDER D BY $0;\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"  LogicalProject(group=[$0], A=[$1], B=[$2], C=[$3])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[CASE(IS NOT NULL($0), $0, $3)], A=[$1], B=[$2], C=[$4])\n"
operator|+
literal|"      LogicalJoin(condition=[=($0, $3)], joinType=[full])\n"
operator|+
literal|"        LogicalProject(DEPTNO=[CASE(IS NOT NULL($0), $0, $2)], A=[$1], B=[$3])\n"
operator|+
literal|"          LogicalJoin(condition=[=($0, $2)], joinType=[full])\n"
operator|+
literal|"            LogicalAggregate(group=[{0}], A=[COLLECT($1)])\n"
operator|+
literal|"              LogicalProject($f0=[+($0, 10)], $f1=[ROW($0, $1, $2)])\n"
operator|+
literal|"                LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"            LogicalAggregate(group=[{0}], B=[COLLECT($1)])\n"
operator|+
literal|"              LogicalProject(DEPTNO=[CAST($0):INTEGER], $f1=[ROW($0, $1, $2)])\n"
operator|+
literal|"                LogicalFilter(condition=[<=($0, 30)])\n"
operator|+
literal|"                  LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"        LogicalAggregate(group=[{0}], C=[COLLECT($1)])\n"
operator|+
literal|"          LogicalProject(DEPTNO=[CAST($0):INTEGER], $f1=[ROW($0, $1, $2)])\n"
operator|+
literal|"            LogicalFilter(condition=[>=($0, 20)])\n"
operator|+
literal|"              LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"(10,{},{(10,ACCOUNTING,NEW YORK)},{})\n"
operator|+
literal|"(20,{(10,ACCOUNTING,NEW YORK)},{(20,RESEARCH,DALLAS)},{(20,RESEARCH,DALLAS)})\n"
operator|+
literal|"(30,{(20,RESEARCH,DALLAS)},{(30,SALES,CHICAGO)},{(30,SALES,CHICAGO)})\n"
operator|+
literal|"(40,{(30,SALES,CHICAGO)},{},{(40,OPERATIONS,BOSTON)})\n"
operator|+
literal|"(50,{(40,OPERATIONS,BOSTON)},{},{})\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT CASE WHEN t4.DEPTNO IS NOT NULL THEN t4.DEPTNO ELSE t7.DEPTNO END "
operator|+
literal|"AS DEPTNO, t4.A, t4.B, t7.C\n"
operator|+
literal|"FROM (SELECT CASE WHEN t0.$f0 IS NOT NULL THEN t0.$f0 ELSE t3.DEPTNO END "
operator|+
literal|"AS DEPTNO, t0.A, t3.B\n"
operator|+
literal|"    FROM (SELECT DEPTNO + 10 AS $f0, "
operator|+
literal|"COLLECT(ROW(DEPTNO, DNAME, LOC)) AS A\n"
operator|+
literal|"        FROM scott.DEPT\n"
operator|+
literal|"        GROUP BY DEPTNO + 10) AS t0\n"
operator|+
literal|"      FULL JOIN (SELECT CAST(DEPTNO AS INTEGER) AS DEPTNO, "
operator|+
literal|"COLLECT(ROW(DEPTNO, DNAME, LOC)) AS B\n"
operator|+
literal|"        FROM scott.DEPT\n"
operator|+
literal|"        WHERE DEPTNO<= 30\n"
operator|+
literal|"        GROUP BY CAST(DEPTNO AS INTEGER)) AS t3 "
operator|+
literal|"ON t0.$f0 = t3.DEPTNO) AS t4\n"
operator|+
literal|"  FULL JOIN (SELECT CAST(DEPTNO AS INTEGER) AS DEPTNO, COLLECT(ROW(DEPTNO, DNAME, "
operator|+
literal|"LOC)) AS C\n"
operator|+
literal|"    FROM scott.DEPT\n"
operator|+
literal|"    WHERE DEPTNO>= 20\n"
operator|+
literal|"    GROUP BY CAST(DEPTNO AS INTEGER)) AS t7 ON t4.DEPTNO = t7.DEPTNO\n"
operator|+
literal|"ORDER BY CASE WHEN t4.DEPTNO IS NOT NULL THEN t4.DEPTNO ELSE t7.DEPTNO END"
decl_stmt|;
name|pig
argument_list|(
name|script
argument_list|)
operator|.
name|assertRel
argument_list|(
name|hasTree
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|assertResult
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
operator|.
name|assertSql
argument_list|(
name|is
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

