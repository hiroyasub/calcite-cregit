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
name|tools
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
name|SchemaPlus
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
name|jdbc
operator|.
name|ConnectionConfig
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
name|rules
operator|.
name|java
operator|.
name|EnumerableConvention
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
name|rules
operator|.
name|java
operator|.
name|JavaRules
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
name|test
operator|.
name|JdbcTest
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
name|RelNode
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
name|rules
operator|.
name|MergeFilterRule
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
name|RelOptUtil
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
name|RelTraitSet
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
name|*
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|parser
operator|.
name|SqlParseException
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|*
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
comment|/**  * Unit tests for {@link Planner}.  */
end_comment

begin_class
specifier|public
class|class
name|PlannerTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testParseAndConvert
parameter_list|()
throws|throws
name|Exception
block|{
name|Planner
name|planner
init|=
name|getPlanner
argument_list|()
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"emps\" where \"name\" like '%e%'"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `emps`\n"
operator|+
literal|"WHERE `name` LIKE '%e%'"
argument_list|)
argument_list|)
expr_stmt|;
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
name|rel
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|toString
argument_list|(
name|rel
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"ProjectRel(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])\n"
operator|+
literal|"  FilterRel(condition=[LIKE($2, '%e%')])\n"
operator|+
literal|"    EnumerableTableAccessRel(table=[[hr, emps]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|toString
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|""
argument_list|,
name|rel
argument_list|,
literal|false
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParseFails
parameter_list|()
throws|throws
name|SqlParseException
block|{
name|Planner
name|planner
init|=
name|getPlanner
argument_list|()
decl_stmt|;
try|try
block|{
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * * from \"emps\""
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|parse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Encountered \"*\" at line 1, column 10."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidateFails
parameter_list|()
throws|throws
name|SqlParseException
block|{
name|Planner
name|planner
init|=
name|getPlanner
argument_list|()
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"emps\" where \"Xname\" like '%e%'"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `emps`\n"
operator|+
literal|"WHERE `Xname` LIKE '%e%'"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
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
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|validate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|Util
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"Column 'Xname' not found in any table"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ok
block|}
block|}
specifier|private
name|Planner
name|getPlanner
parameter_list|(
name|RuleSet
modifier|...
name|ruleSets
parameter_list|)
block|{
return|return
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|ConnectionConfig
operator|.
name|Lex
operator|.
name|ORACLE
argument_list|,
operator|new
name|Function1
argument_list|<
name|SchemaPlus
argument_list|,
name|Schema
argument_list|>
argument_list|()
block|{
specifier|public
name|Schema
name|apply
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|)
block|{
return|return
operator|new
name|ReflectiveSchema
argument_list|(
name|parentSchema
argument_list|,
literal|"hr"
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|,
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
name|ruleSets
argument_list|)
return|;
block|}
comment|/** Tests that planner throws an error if you pass to    * {@link Planner#convert(org.eigenbase.sql.SqlNode)}    * a {@link org.eigenbase.sql.SqlNode} that has been parsed but not    * validated. */
annotation|@
name|Test
specifier|public
name|void
name|testConvertWithoutValidateFails
parameter_list|()
throws|throws
name|Exception
block|{
name|Planner
name|planner
init|=
name|getPlanner
argument_list|()
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"emps\""
argument_list|)
decl_stmt|;
try|try
block|{
name|RelNode
name|rel
init|=
name|planner
operator|.
name|convert
argument_list|(
name|parse
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|rel
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"cannot move from STATE_3_PARSED to STATE_4_VALIDATED"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Unit test that parses, validates, converts and plans. */
annotation|@
name|Test
specifier|public
name|void
name|testPlan
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleSet
name|ruleSet
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|MergeFilterRule
operator|.
name|INSTANCE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_FILTER_RULE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|)
decl_stmt|;
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
name|ruleSet
argument_list|)
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"emps\""
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
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|planner
operator|.
name|getEmptyTraitSet
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
name|toString
argument_list|(
name|transform
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"EnumerableProjectRel(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])\n"
operator|+
literal|"  EnumerableTableAccessRel(table=[[hr, emps]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that Hive dialect does not generate "AS". */
annotation|@
name|Test
specifier|public
name|void
name|testHiveDialect
parameter_list|()
throws|throws
name|SqlParseException
block|{
name|Planner
name|planner
init|=
name|getPlanner
argument_list|()
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from (select * from \"emps\") as t\n"
operator|+
literal|"where \"name\" like '%e%'"
argument_list|)
decl_stmt|;
specifier|final
name|SqlDialect
name|hiveDialect
init|=
operator|new
name|SqlDialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|HIVE
argument_list|,
literal|"Hive"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parse
operator|.
name|toSqlString
argument_list|(
name|hiveDialect
argument_list|)
operator|.
name|getSql
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM emps) T\n"
operator|+
literal|"WHERE name LIKE '%e%'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PlannerTest.java
end_comment

end_unit

