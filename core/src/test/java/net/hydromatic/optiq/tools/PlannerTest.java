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
name|config
operator|.
name|Lex
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
name|jdbc
operator|.
name|JdbcRules
operator|.
name|JdbcProjectRel
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
name|rules
operator|.
name|java
operator|.
name|JavaRules
operator|.
name|EnumerableProjectRel
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
name|*
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
name|convert
operator|.
name|ConverterRule
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
name|rel
operator|.
name|rules
operator|.
name|RemoveSortRule
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
name|reltype
operator|.
name|RelDataTypeFactory
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
name|sql
operator|.
name|parser
operator|.
name|impl
operator|.
name|SqlParserImpl
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
name|type
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
name|util
operator|.
name|ChainedSqlOperatorTable
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
name|util
operator|.
name|ListSqlOperatorTable
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
name|validate
operator|.
name|SqlValidator
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
name|validate
operator|.
name|SqlValidatorScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql2rel
operator|.
name|StandardConvertletTable
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
specifier|public
specifier|static
specifier|final
name|Function1
argument_list|<
name|SchemaPlus
argument_list|,
name|Schema
argument_list|>
name|HR_FACTORY
init|=
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
decl_stmt|;
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
argument_list|(
literal|null
argument_list|)
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
name|Util
operator|.
name|toLinux
argument_list|(
name|parse
operator|.
name|toString
argument_list|()
argument_list|)
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
name|Util
operator|.
name|toLinux
argument_list|(
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
argument_list|(
literal|null
argument_list|)
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
argument_list|(
literal|null
argument_list|)
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
name|Util
operator|.
name|toLinux
argument_list|(
name|parse
operator|.
name|toString
argument_list|()
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testValidateUserDefinedAggregate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlStdOperatorTable
name|stdOpTab
init|=
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
decl_stmt|;
name|SqlOperatorTable
name|opTab
init|=
operator|new
name|ChainedSqlOperatorTable
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|stdOpTab
argument_list|,
operator|new
name|ListSqlOperatorTable
argument_list|(
name|ImmutableList
operator|.
expr|<
name|SqlOperator
operator|>
name|of
argument_list|(
operator|new
name|MyCountAggFunction
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Planner
name|planner
init|=
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|Lex
operator|.
name|ORACLE
argument_list|,
name|SqlParserImpl
operator|.
name|FACTORY
argument_list|,
name|HR_FACTORY
argument_list|,
name|opTab
argument_list|,
literal|null
argument_list|,
name|StandardConvertletTable
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select \"deptno\", my_count(\"empid\") from \"emps\"\n"
operator|+
literal|"group by \"deptno\""
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|parse
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"SELECT `deptno`, `MY_COUNT`(`empid`)\n"
operator|+
literal|"FROM `emps`\n"
operator|+
literal|"GROUP BY `deptno`"
argument_list|)
argument_list|)
expr_stmt|;
comment|// MY_COUNT is recognized as an aggregate function, and therefore it is OK
comment|// that its argument empid is not in the GROUP BY clause.
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
name|assertThat
argument_list|(
name|validate
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// The presence of an aggregate function in the SELECT clause causes it
comment|// to become an aggregate query. Non-aggregate expressions become illegal.
name|planner
operator|.
name|close
argument_list|()
expr_stmt|;
name|planner
operator|.
name|reset
argument_list|()
expr_stmt|;
name|parse
operator|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select \"deptno\", count(1) from \"emps\""
argument_list|)
expr_stmt|;
try|try
block|{
name|validate
operator|=
name|planner
operator|.
name|validate
argument_list|(
name|parse
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
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
name|containsString
argument_list|(
literal|"Expression 'deptno' is not being grouped"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Planner
name|getPlanner
parameter_list|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
parameter_list|,
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
name|Lex
operator|.
name|ORACLE
argument_list|,
name|SqlParserImpl
operator|.
name|FACTORY
argument_list|,
name|HR_FACTORY
argument_list|,
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
name|traitDefs
argument_list|,
name|StandardConvertletTable
operator|.
name|INSTANCE
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
argument_list|(
literal|null
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
literal|null
argument_list|,
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
comment|/** Unit test that parses, validates, converts and    * plans for query using order by */
annotation|@
name|Test
specifier|public
name|void
name|testSortPlan
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
name|RemoveSortRule
operator|.
name|INSTANCE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
decl_stmt|;
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|,
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
literal|"select * from \"emps\" "
operator|+
literal|"order by \"emps\".\"deptno\""
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
literal|"EnumerableSortRel(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"  EnumerableProjectRel(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])\n"
operator|+
literal|"    EnumerableTableAccessRel(table=[[hr, emps]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test that parses, validates, converts and    * plans for query using two duplicate order by.    * The duplicate order by should be removed by RemoveSortRule*/
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateSortPlan
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
name|RemoveSortRule
operator|.
name|INSTANCE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
decl_stmt|;
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|,
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
literal|"select \"empid\" from ( "
operator|+
literal|"select * "
operator|+
literal|"from \"emps\" "
operator|+
literal|"order by \"emps\".\"deptno\") "
operator|+
literal|"order by \"deptno\""
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
literal|"EnumerableProjectRel(empid=[$0])\n"
operator|+
literal|"  EnumerableProjectRel(empid=[$0], deptno=[$1])\n"
operator|+
literal|"    EnumerableSortRel(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"      EnumerableProjectRel(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])\n"
operator|+
literal|"        EnumerableTableAccessRel(table=[[hr, emps]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test that parses, validates, converts and    * plans for query using two duplicate order by.*/
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateSortPlanWORemoveSortRule
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
name|JavaRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
decl_stmt|;
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|,
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
literal|"select \"empid\" from ( "
operator|+
literal|"select * "
operator|+
literal|"from \"emps\" "
operator|+
literal|"order by \"emps\".\"deptno\") "
operator|+
literal|"order by \"deptno\""
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
literal|"EnumerableProjectRel(empid=[$0])\n"
operator|+
literal|"  EnumerableSortRel(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    EnumerableProjectRel(empid=[$0], deptno=[$1])\n"
operator|+
literal|"      EnumerableSortRel(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"        EnumerableProjectRel(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])\n"
operator|+
literal|"          EnumerableTableAccessRel(table=[[hr, emps]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test that parses, validates, converts and plans. Planner is    * provided with a list of RelTraitDefs to register. */
annotation|@
name|Test
specifier|public
name|void
name|testPlanWithExplicitTraitDefs
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
specifier|final
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelTraitDef
argument_list|>
argument_list|()
decl_stmt|;
name|traitDefs
operator|.
name|add
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|traitDefs
operator|.
name|add
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
name|traitDefs
argument_list|,
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
comment|/** Unit test that calls {@link Planner#transform} twice. */
annotation|@
name|Test
specifier|public
name|void
name|testPlanTransformTwice
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
literal|null
argument_list|,
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
name|RelNode
name|transform2
init|=
name|planner
operator|.
name|transform
argument_list|(
literal|0
argument_list|,
name|traitSet
argument_list|,
name|transform
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|toString
argument_list|(
name|transform2
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
argument_list|(
literal|null
argument_list|)
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
name|Util
operator|.
name|toLinux
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
argument_list|)
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
comment|/** Unit test that calls {@link Planner#transform} twice,    * with different rule sets, with different conventions.    *    *<p>{@link net.hydromatic.optiq.impl.jdbc.JdbcConvention} is different    * from the typical convention in that it is not a singleton. Switching to    * a different instance causes problems unless planner state is wiped clean    * between calls to {@link Planner#transform}. */
annotation|@
name|Test
specifier|public
name|void
name|testPlanTransformWithDiffRuleSetAndConvention
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleSet
name|ruleSet0
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
name|JdbcConvention
name|out
init|=
operator|new
name|JdbcConvention
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"myjdbc"
argument_list|)
decl_stmt|;
name|RuleSet
name|ruleSet1
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
operator|new
name|MockJdbcProjectRule
argument_list|(
name|out
argument_list|)
argument_list|,
operator|new
name|MockJdbcTableRule
argument_list|(
name|out
argument_list|)
argument_list|)
decl_stmt|;
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|,
name|ruleSet0
argument_list|,
name|ruleSet1
argument_list|)
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select T1.\"name\" from \"emps\" as T1 "
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
name|traitSet0
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
name|RelTraitSet
name|traitSet1
init|=
name|planner
operator|.
name|getEmptyTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
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
name|traitSet0
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|RelNode
name|transform2
init|=
name|planner
operator|.
name|transform
argument_list|(
literal|1
argument_list|,
name|traitSet1
argument_list|,
name|transform
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|toString
argument_list|(
name|transform2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"JdbcProjectRel(name=[$2])\n"
operator|+
literal|"  MockJdbcTableScan(table=[[hr, emps]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Rule to convert a {@link EnumerableProjectRel} to an    * {@link JdbcProjectRel}.    */
specifier|private
class|class
name|MockJdbcProjectRule
extends|extends
name|ConverterRule
block|{
specifier|private
name|MockJdbcProjectRule
parameter_list|(
name|JdbcConvention
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|EnumerableProjectRel
operator|.
name|class
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
name|out
argument_list|,
literal|"MockJdbcProjectRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|EnumerableProjectRel
name|project
init|=
operator|(
name|EnumerableProjectRel
operator|)
name|rel
decl_stmt|;
return|return
operator|new
name|JdbcProjectRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|getOutConvention
argument_list|()
argument_list|)
argument_list|,
name|convert
argument_list|(
name|project
operator|.
name|getChild
argument_list|()
argument_list|,
name|project
operator|.
name|getChild
argument_list|()
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|getOutConvention
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|,
name|ProjectRelBase
operator|.
name|Flags
operator|.
name|BOXED
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link JavaRules.EnumerableTableAccessRel} to an    * {@link MockJdbcTableScan}.    */
specifier|private
class|class
name|MockJdbcTableRule
extends|extends
name|ConverterRule
block|{
specifier|private
name|MockJdbcTableRule
parameter_list|(
name|JdbcConvention
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|JavaRules
operator|.
name|EnumerableTableAccessRel
operator|.
name|class
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
name|out
argument_list|,
literal|"MockJdbcTableRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|JavaRules
operator|.
name|EnumerableTableAccessRel
name|scan
init|=
operator|(
name|JavaRules
operator|.
name|EnumerableTableAccessRel
operator|)
name|rel
decl_stmt|;
return|return
operator|new
name|MockJdbcTableScan
argument_list|(
name|scan
operator|.
name|getCluster
argument_list|()
argument_list|,
name|scan
operator|.
name|getTable
argument_list|()
argument_list|,
operator|(
name|JdbcConvention
operator|)
name|getOutConvention
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Relational expression representing a "mock" scan of a table in a    * JDBC data source.    */
specifier|private
class|class
name|MockJdbcTableScan
extends|extends
name|TableAccessRelBase
implements|implements
name|JdbcRel
block|{
specifier|public
name|MockJdbcTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|JdbcConvention
name|jdbcConvention
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|jdbcConvention
argument_list|)
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
operator|new
name|MockJdbcTableScan
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|table
argument_list|,
operator|(
name|JdbcConvention
operator|)
name|getConvention
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
specifier|final
name|JdbcConvention
name|out
init|=
operator|(
name|JdbcConvention
operator|)
name|getConvention
argument_list|()
decl_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|JdbcRules
operator|.
name|rules
argument_list|(
name|out
argument_list|)
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|JdbcImplementor
operator|.
name|Result
name|implement
parameter_list|(
name|JdbcImplementor
name|implementor
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** User-defined aggregate function. */
specifier|public
specifier|static
class|class
name|MyCountAggFunction
extends|extends
name|SqlAggFunction
block|{
specifier|public
name|MyCountAggFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"MY_COUNT"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|BIGINT
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|ANY
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|RelDataType
argument_list|>
name|getParameterTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
comment|// Check for COUNT(*) function.  If it is we don't
comment|// want to try and derive the "*"
if|if
condition|(
name|call
operator|.
name|isCountStar
argument_list|()
condition|)
block|{
return|return
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End PlannerTest.java
end_comment

end_unit

