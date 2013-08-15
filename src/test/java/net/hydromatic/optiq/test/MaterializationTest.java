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
name|optiq
operator|.
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|materialize
operator|.
name|MaterializationService
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
name|relopt
operator|.
name|SubstitutionVisitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
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
name|math
operator|.
name|BigDecimal
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
comment|/**  * Unit test for the materialized view rewrite mechanism. Each test has a  * query and one or more materializations (what Oracle calls materialized views)  * and checks that the materialization is used.  */
end_comment

begin_class
specifier|public
class|class
name|MaterializationTest
block|{
specifier|final
name|JavaTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
block|{
try|try
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
name|withMaterializations
argument_list|(
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select * from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"empid\" + 1 from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableAccessRel(table=[[hr, m0]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|MaterializationService
operator|.
name|INSTANCE
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView
parameter_list|()
block|{
try|try
block|{
name|Prepare
operator|.
name|TRIM
operator|=
literal|true
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
name|REGULAR
argument_list|)
operator|.
name|withMaterializations
argument_list|(
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableAccessRel(table=[[hr, m0]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|MaterializationService
operator|.
name|INSTANCE
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Prepare
operator|.
name|TRIM
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|)
block|{
try|try
block|{
name|Prepare
operator|.
name|TRIM
operator|=
literal|true
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
name|REGULAR
argument_list|)
operator|.
name|withMaterializations
argument_list|(
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|,
literal|"m0"
argument_list|,
name|materialize
argument_list|)
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableAccessRel(table=[[hr, m0]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|MaterializationService
operator|.
name|INSTANCE
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Prepare
operator|.
name|TRIM
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/** Checks that a given query CAN NOT use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkNoMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|)
block|{
try|try
block|{
name|Prepare
operator|.
name|TRIM
operator|=
literal|true
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
name|REGULAR
argument_list|)
operator|.
name|withMaterializations
argument_list|(
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|,
literal|"m0"
argument_list|,
name|materialize
argument_list|)
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableAccessRel(table=[[hr, emps]])"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|MaterializationService
operator|.
name|INSTANCE
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Prepare
operator|.
name|TRIM
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/** Runs the same test as {@link #testFilterQueryOnProjectView()} but more    * concisely.*/
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView0
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView()} but with extra column in    * materialized view.*/
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView()} but with extra column in both    * materialized view and query.*/
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView()} but materialized view contains    * an expression and query.*/
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView3
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" - 10 = 0"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView3()} but materialized view cannot    * be used because it does not contain required expression.*/
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView4
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" + 10 = 20"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10 and \"empid\"< 150"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * view. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView3
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10 or \"empid\"< 150"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testSwapJoin
parameter_list|()
block|{
name|String
name|q1
init|=
literal|"select count(*) as c from \"foodmart\".\"sales_fact_1997\" as s join \"foodmart\".\"time_by_day\" as t on s.\"time_id\" = t.\"time_id\""
decl_stmt|;
name|String
name|q2
init|=
literal|"select count(*) as c from \"foodmart\".\"time_by_day\" as t join \"foodmart\".\"sales_fact_1997\" as s on t.\"time_id\" = s.\"time_id\""
decl_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testDifferentColumnNames
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testDifferentType
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPartialUnion
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testNonDisjointUnion
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testMaterializationReferencesTableInOtherSchema
parameter_list|()
block|{
block|}
comment|/** Unit test for logic functions    * {@link org.eigenbase.relopt.SubstitutionVisitor#mayBeSatisfiable} and    * {@link org.eigenbase.relopt.SubstitutionVisitor#simplify}. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testSatisfiable
parameter_list|()
block|{
comment|// TRUE may be satisfiable
name|checkSatisfiable
argument_list|(
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// FALSE is not satisfiable
name|checkNotSatisfiable
argument_list|(
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// The expression "$0 = 1".
specifier|final
name|RexNode
name|i0_eq_0
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|equalsOperator
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
comment|// "$0 = 1" may be satisfiable
name|checkSatisfiable
argument_list|(
name|i0_eq_0
argument_list|,
literal|"=($0, 0)"
argument_list|)
expr_stmt|;
comment|// "$0 = 1 AND TRUE" may be satisfiable
specifier|final
name|RexNode
name|e0
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e0
argument_list|,
literal|"=($0, 0)"
argument_list|)
expr_stmt|;
comment|// "$0 = 1 AND FALSE" is not satisfiable
specifier|final
name|RexNode
name|e1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotSatisfiable
argument_list|(
name|e1
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND NOT $0 = 0" is not satisfiable
specifier|final
name|RexNode
name|e2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotSatisfiable
argument_list|(
name|e2
argument_list|)
expr_stmt|;
comment|// "TRUE AND NOT $0 = 0" may be satisfiable. Can simplify.
specifier|final
name|RexNode
name|e3
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e3
argument_list|,
literal|"NOT(=($0, 0))"
argument_list|)
expr_stmt|;
comment|// The expression "$1 = 1".
specifier|final
name|RexNode
name|i1_eq_1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|equalsOperator
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
comment|// "$0 = 0 AND $1 = 1 AND NOT $0 = 0" is not satisfiable
specifier|final
name|RexNode
name|e4
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i1_eq_1
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotSatisfiable
argument_list|(
name|e4
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND NOT $1 = 1" may be satisfiable. Can't simplify.
specifier|final
name|RexNode
name|e5
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|i1_eq_1
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e5
argument_list|,
literal|"AND(=($0, 0), NOT(=($1, 1)))"
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND NOT ($0 = 0 AND $1 = 1)" may be satisfiable. Can simplify.
specifier|final
name|RexNode
name|e6
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|i1_eq_1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e6
argument_list|,
literal|"AND(=($0, 0), NOT(AND(=($0, 0), =($1, 1))))"
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND $1 = 1 NOT ($0 = 0)" may be satisfiable. Can simplify.
specifier|final
name|RexNode
name|e7
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i1_eq_1
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e7
argument_list|,
literal|"AND(=($0, 0), NOT(AND(=($0, 0), =($1, 1))))"
argument_list|)
expr_stmt|;
comment|// The expression "$2".
specifier|final
name|RexInputRef
name|i2
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// The expression "$3".
specifier|final
name|RexInputRef
name|i3
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// The expression "$4".
specifier|final
name|RexInputRef
name|i4
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|// "$0 = 0 AND $2 AND $3 AND NOT ($2 AND $3 AND $4) AND NOT ($2 AND $4)" may
comment|// be satisfiable. Can't simplify.
specifier|final
name|RexNode
name|e8
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i2
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i3
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|i2
argument_list|,
name|i3
argument_list|,
name|i4
argument_list|)
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
name|i4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e8
argument_list|,
literal|"AND(AND(AND(AND(=($0, 0), $2), $3), NOT(AND($2, $3, $4))), NOT($4))"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkNotSatisfiable
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|SubstitutionVisitor
operator|.
name|mayBeSatisfiable
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|simple
init|=
name|SubstitutionVisitor
operator|.
name|simplify
argument_list|(
name|rexBuilder
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|RexLiteral
operator|.
name|booleanValue
argument_list|(
name|simple
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkSatisfiable
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|SubstitutionVisitor
operator|.
name|mayBeSatisfiable
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|simple
init|=
name|SubstitutionVisitor
operator|.
name|simplify
argument_list|(
name|rexBuilder
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|simple
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MaterializationTest.java
end_comment

end_unit

