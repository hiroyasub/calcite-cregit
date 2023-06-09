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
name|sql
operator|.
name|validate
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
name|enumerable
operator|.
name|EnumerableConvention
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
name|enumerable
operator|.
name|EnumerableProject
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
name|Lex
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
name|plan
operator|.
name|RelTraitDef
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
name|plan
operator|.
name|RelTraitSet
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
name|calcite
operator|.
name|sql
operator|.
name|SqlNode
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
name|parser
operator|.
name|SqlParser
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
name|parser
operator|.
name|SqlParser
operator|.
name|Config
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
name|test
operator|.
name|CalciteAssert
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
name|tools
operator|.
name|FrameworkConfig
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
name|tools
operator|.
name|Frameworks
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
name|tools
operator|.
name|Planner
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
name|tools
operator|.
name|Program
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
name|tools
operator|.
name|Programs
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
name|tools
operator|.
name|ValidationException
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
name|util
operator|.
name|List
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
name|anyOf
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
name|instanceOf
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
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertThrows
import|;
end_import

begin_comment
comment|/**  * Testing {@link SqlValidator} and {@link Lex}.  */
end_comment

begin_class
class|class
name|LexCaseSensitiveTest
block|{
specifier|private
specifier|static
name|Planner
name|getPlanner
parameter_list|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
parameter_list|,
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|,
name|Program
modifier|...
name|programs
parameter_list|)
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|parserConfig
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|HR
argument_list|)
argument_list|)
operator|.
name|traitDefs
argument_list|(
name|traitDefs
argument_list|)
operator|.
name|programs
argument_list|(
name|programs
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|config
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|runProjectQueryWithLex
parameter_list|(
name|Lex
name|lex
parameter_list|,
name|String
name|sql
parameter_list|)
throws|throws
name|Exception
block|{
name|Config
name|javaLex
init|=
name|SqlParser
operator|.
name|config
argument_list|()
operator|.
name|withLex
argument_list|(
name|lex
argument_list|)
decl_stmt|;
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|,
name|javaLex
argument_list|,
name|Programs
operator|.
name|ofRules
argument_list|(
name|Programs
operator|.
name|RULE_SET
argument_list|)
argument_list|)
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|SqlNode
name|validate
init|=
name|planner
operator|.
name|validate
argument_list|(
name|parse
argument_list|)
decl_stmt|;
name|RelNode
name|convert
init|=
name|planner
operator|.
name|rel
argument_list|(
name|validate
argument_list|)
operator|.
name|rel
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|convert
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|RelNode
name|transform
init|=
name|planner
operator|.
name|transform
argument_list|(
literal|0
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|transform
argument_list|,
name|instanceOf
argument_list|(
name|EnumerableProject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|transform
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|fieldNames
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|lex
operator|.
name|caseSensitive
condition|)
block|{
name|assertThat
argument_list|(
name|fieldNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"EMPID"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"empid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|fieldNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|"-"
operator|+
name|fieldNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|anyOf
argument_list|(
name|is
argument_list|(
literal|"EMPID-empid0"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"EMPID0-empid"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseOracle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select \"empid\" as EMPID, \"empid\" from\n"
operator|+
literal|" (select \"empid\" from \"emps\" order by \"emps\".\"deptno\")"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|ORACLE
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseOracleException
parameter_list|()
block|{
name|assertThrows
argument_list|(
name|ValidationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
comment|// Oracle is case sensitive, so EMPID should not be found.
name|String
name|sql
init|=
literal|"select EMPID, \"empid\" from\n"
operator|+
literal|" (select \"empid\" from \"emps\" order by \"emps\".\"deptno\")"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|ORACLE
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseMySql
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select empid as EMPID, empid from (\n"
operator|+
literal|"  select empid from emps order by `EMPS`.DEPTNO)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseMySqlNoException
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select EMPID, empid from\n"
operator|+
literal|" (select empid from emps order by emps.deptno)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseMySqlAnsi
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select empid as EMPID, empid from (\n"
operator|+
literal|"  select empid from emps order by EMPS.DEPTNO)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|MYSQL_ANSI
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseMySqlAnsiNoException
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select EMPID, empid from\n"
operator|+
literal|" (select empid from emps order by emps.deptno)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|MYSQL_ANSI
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseSqlServer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select empid as EMPID, empid from (\n"
operator|+
literal|"  select empid from emps order by EMPS.DEPTNO)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|SQL_SERVER
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseSqlServerNoException
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select EMPID, empid from\n"
operator|+
literal|" (select empid from emps order by emps.deptno)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|SQL_SERVER
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseJava
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select empid as EMPID, empid from (\n"
operator|+
literal|"  select empid from emps order by emps.deptno)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseJavaException
parameter_list|()
block|{
name|assertThrows
argument_list|(
name|ValidationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
comment|// JAVA is case sensitive, so EMPID should not be found.
name|String
name|sql
init|=
literal|"select EMPID, empid from\n"
operator|+
literal|" (select empid from emps order by emps.deptno)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseJoinOracle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select t.\"empid\" as EMPID, s.\"empid\" from\n"
operator|+
literal|"(select * from \"emps\" where \"emps\".\"deptno\"> 100) t join\n"
operator|+
literal|"(select * from \"emps\" where \"emps\".\"deptno\"< 200) s\n"
operator|+
literal|"on t.\"empid\" = s.\"empid\""
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|ORACLE
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseJoinMySql
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select t.empid as EMPID, s.empid from\n"
operator|+
literal|"(select * from emps where emps.deptno> 100) t join\n"
operator|+
literal|"(select * from emps where emps.deptno< 200) s on t.empid = s.empid"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseJoinMySqlAnsi
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select t.empid as EMPID, s.empid from\n"
operator|+
literal|"(select * from emps where emps.deptno> 100) t join\n"
operator|+
literal|"(select * from emps where emps.deptno< 200) s on t.empid = s.empid"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|MYSQL_ANSI
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseJoinSqlServer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select t.empid as EMPID, s.empid from\n"
operator|+
literal|"(select * from emps where emps.deptno> 100) t join\n"
operator|+
literal|"(select * from emps where emps.deptno< 200) s on t.empid = s.empid"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|SQL_SERVER
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseJoinJava
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select t.empid as EMPID, s.empid from\n"
operator|+
literal|"(select * from emps where emps.deptno> 100) t join\n"
operator|+
literal|"(select * from emps where emps.deptno< 200) s on t.empid = s.empid"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseBigQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select empid as EMPID, empid from (\n"
operator|+
literal|"  select empid from emps order by EMPS.DEPTNO)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|BIG_QUERY
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCalciteCaseBigQueryNoException
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select EMPID, empid from\n"
operator|+
literal|" (select empid from emps order by emps.deptno)"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|BIG_QUERY
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5291">[CALCITE-5291]    * Make BigQuery lexical policy case insensitive</a>.    *    *<p>In reality, BigQuery treats table names as case-sensitive, column names    * and aliases as case-insensitive. Calcite cannot currently support a hybrid    * policy, so it treats table names as case-insensitive. */
annotation|@
name|Test
name|void
name|testCalciteCaseJoinBigQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select t.empid as EMPID, s.empid from\n"
operator|+
literal|"(select * from emps where emps.deptno> 100) t join\n"
operator|+
literal|"(select * from emps where emps.deptno< 200) s on t.empid = s.empid"
decl_stmt|;
name|runProjectQueryWithLex
argument_list|(
name|Lex
operator|.
name|BIG_QUERY
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

