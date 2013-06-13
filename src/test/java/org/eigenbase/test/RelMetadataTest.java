begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|test
package|;
end_package

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
name|metadata
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
comment|/**  * Unit test for {@link DefaultRelMetadataProvider}. See {@link  * SqlToRelTestBase} class comments for details on the schema used. Note that no  * optimizer rules are fired on the translation of the SQL into relational  * algebra (e.g. join conditions in the WHERE clause will look like filters), so  * it's necessary to phrase the SQL carefully.  */
end_comment

begin_class
specifier|public
class|class
name|RelMetadataTest
extends|extends
name|SqlToRelTestBase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|double
name|EPSILON
init|=
literal|1.0e-5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_EQUAL_SELECTIVITY
init|=
literal|0.15
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_EQUAL_SELECTIVITY_SQUARED
init|=
name|DEFAULT_EQUAL_SELECTIVITY
operator|*
name|DEFAULT_EQUAL_SELECTIVITY
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_COMP_SELECTIVITY
init|=
literal|0.5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_NOTNULL_SELECTIVITY
init|=
literal|0.9
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_SELECTIVITY
init|=
literal|0.25
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|EMP_SIZE
init|=
literal|1000.0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|DEPT_SIZE
init|=
literal|100.0
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|// ----------------------------------------------------------------------
comment|// Tests for getPercentageOriginalRows
comment|// ----------------------------------------------------------------------
specifier|private
name|RelNode
name|convertSql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|RelNode
name|rel
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|DefaultRelMetadataProvider
name|provider
init|=
operator|new
name|DefaultRelMetadataProvider
argument_list|()
decl_stmt|;
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|setMetadataProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
return|return
name|rel
return|;
block|}
specifier|private
name|void
name|checkPercentageOriginalRows
parameter_list|(
name|String
name|sql
parameter_list|,
name|double
name|expected
parameter_list|)
block|{
name|checkPercentageOriginalRows
argument_list|(
name|sql
argument_list|,
name|expected
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkPercentageOriginalRows
parameter_list|(
name|String
name|sql
parameter_list|,
name|double
name|expected
parameter_list|,
name|double
name|epsilon
parameter_list|)
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|RelMetadataQuery
operator|.
name|getPercentageOriginalRows
argument_list|(
name|rel
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|epsilon
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsTableOnly
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select * from dept"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsAgg
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select deptno from dept group by deptno"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsOneFilter
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select * from dept where deptno = 20"
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsTwoFilters
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select * from (select * from dept where name='X')"
operator|+
literal|" where deptno = 20"
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY_SQUARED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsRedundantFilter
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select * from (select * from dept where deptno=20)"
operator|+
literal|" where deptno = 20"
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsJoin
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select * from emp inner join dept on emp.deptno=dept.deptno"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsJoinTwoFilters
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select * from (select * from emp where deptno=10) e"
operator|+
literal|" inner join (select * from dept where deptno=10) d"
operator|+
literal|" on e.deptno=d.deptno"
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY_SQUARED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsUnionNoFilter
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select name from dept union all select ename from emp"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsUnionLittleFilter
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select name from dept where deptno=20"
operator|+
literal|" union all select ename from emp"
argument_list|,
operator|(
operator|(
name|DEPT_SIZE
operator|*
name|DEFAULT_EQUAL_SELECTIVITY
operator|)
operator|+
name|EMP_SIZE
operator|)
operator|/
operator|(
name|DEPT_SIZE
operator|+
name|EMP_SIZE
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPercentageOriginalRowsUnionBigFilter
parameter_list|()
block|{
name|checkPercentageOriginalRows
argument_list|(
literal|"select name from dept"
operator|+
literal|" union all select ename from emp where deptno=20"
argument_list|,
operator|(
operator|(
name|EMP_SIZE
operator|*
name|DEFAULT_EQUAL_SELECTIVITY
operator|)
operator|+
name|DEPT_SIZE
operator|)
operator|/
operator|(
name|DEPT_SIZE
operator|+
name|EMP_SIZE
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// ----------------------------------------------------------------------
comment|// Tests for getColumnOrigins
comment|// ----------------------------------------------------------------------
specifier|private
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|checkColumnOrigin
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
name|RelMetadataQuery
operator|.
name|getColumnOrigins
argument_list|(
name|rel
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkNoColumnOrigin
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|result
init|=
name|checkColumnOrigin
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|checkColumnOrigin
parameter_list|(
name|RelColumnOrigin
name|rco
parameter_list|,
name|String
name|expectedTableName
parameter_list|,
name|String
name|expectedColumnName
parameter_list|,
name|boolean
name|expectedDerived
parameter_list|)
block|{
name|RelOptTable
name|actualTable
init|=
name|rco
operator|.
name|getOriginTable
argument_list|()
decl_stmt|;
name|String
index|[]
name|actualTableName
init|=
name|actualTable
operator|.
name|getQualifiedName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|actualTableName
index|[
name|actualTableName
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|expectedTableName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|actualTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
index|[
name|rco
operator|.
name|getOriginColumnOrdinal
argument_list|()
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|expectedColumnName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rco
operator|.
name|isDerived
argument_list|()
argument_list|,
name|expectedDerived
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkSingleColumnOrigin
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedTableName
parameter_list|,
name|String
name|expectedColumnName
parameter_list|,
name|boolean
name|expectedDerived
parameter_list|)
block|{
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|result
init|=
name|checkColumnOrigin
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RelColumnOrigin
name|rco
init|=
name|result
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|checkColumnOrigin
argument_list|(
name|rco
argument_list|,
name|expectedTableName
argument_list|,
name|expectedColumnName
argument_list|,
name|expectedDerived
argument_list|)
expr_stmt|;
block|}
comment|// WARNING:  this requires the two table names to be different
specifier|private
name|void
name|checkTwoColumnOrigin
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedTableName1
parameter_list|,
name|String
name|expectedColumnName1
parameter_list|,
name|String
name|expectedTableName2
parameter_list|,
name|String
name|expectedColumnName2
parameter_list|,
name|boolean
name|expectedDerived
parameter_list|)
block|{
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|result
init|=
name|checkColumnOrigin
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RelColumnOrigin
name|rco
range|:
name|result
control|)
block|{
name|RelOptTable
name|actualTable
init|=
name|rco
operator|.
name|getOriginTable
argument_list|()
decl_stmt|;
name|String
index|[]
name|actualTableName
init|=
name|actualTable
operator|.
name|getQualifiedName
argument_list|()
decl_stmt|;
name|String
name|actualUnqualifiedName
init|=
name|actualTableName
index|[
name|actualTableName
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|actualUnqualifiedName
operator|.
name|equals
argument_list|(
name|expectedTableName1
argument_list|)
condition|)
block|{
name|checkColumnOrigin
argument_list|(
name|rco
argument_list|,
name|expectedTableName1
argument_list|,
name|expectedColumnName1
argument_list|,
name|expectedDerived
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkColumnOrigin
argument_list|(
name|rco
argument_list|,
name|expectedTableName2
argument_list|,
name|expectedColumnName2
argument_list|,
name|expectedDerived
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsTableOnly
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select name as dname from dept"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsExpression
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select upper(name) as dname from dept"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsDyadicExpression
parameter_list|()
block|{
name|checkTwoColumnOrigin
argument_list|(
literal|"select name||ename from dept,emp"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|"EMP"
argument_list|,
literal|"ENAME"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsConstant
parameter_list|()
block|{
name|checkNoColumnOrigin
argument_list|(
literal|"select 'Minstrelsy' as dname from dept"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsFilter
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select name as dname from dept where deptno=10"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsJoinLeft
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select ename from emp,dept"
argument_list|,
literal|"EMP"
argument_list|,
literal|"ENAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsJoinRight
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select name as dname from emp,dept"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsJoinOuter
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select name as dname from emp left outer join dept"
operator|+
literal|" on emp.deptno = dept.deptno"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsJoinFullOuter
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select name as dname from emp full outer join dept"
operator|+
literal|" on emp.deptno = dept.deptno"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsAggKey
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select name,count(deptno) from dept group by name"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsAggMeasure
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select count(deptno),name from dept group by name"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"DEPTNO"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsAggCountStar
parameter_list|()
block|{
name|checkNoColumnOrigin
argument_list|(
literal|"select count(*),name from dept group by name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsValues
parameter_list|()
block|{
name|checkNoColumnOrigin
argument_list|(
literal|"values(1,2,3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsUnion
parameter_list|()
block|{
name|checkTwoColumnOrigin
argument_list|(
literal|"select name from dept union all select ename from emp"
argument_list|,
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|"EMP"
argument_list|,
literal|"ENAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnOriginsSelfUnion
parameter_list|()
block|{
name|checkSingleColumnOrigin
argument_list|(
literal|"select ename from emp union all select ename from emp"
argument_list|,
literal|"EMP"
argument_list|,
literal|"ENAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkRowCount
parameter_list|(
name|String
name|sql
parameter_list|,
name|double
name|expected
parameter_list|)
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testRowCountEmp
parameter_list|()
block|{
name|checkRowCount
argument_list|(
literal|"select * from emp"
argument_list|,
name|EMP_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testRowCountDept
parameter_list|()
block|{
name|checkRowCount
argument_list|(
literal|"select * from dept"
argument_list|,
name|DEPT_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testRowCountCartesian
parameter_list|()
block|{
name|checkRowCount
argument_list|(
literal|"select * from emp,dept"
argument_list|,
name|EMP_SIZE
operator|*
name|DEPT_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testRowCountJoin
parameter_list|()
block|{
name|checkRowCount
argument_list|(
literal|"select * from emp inner join dept on emp.deptno = dept.deptno"
argument_list|,
name|EMP_SIZE
operator|*
name|DEPT_SIZE
operator|*
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testRowCountUnion
parameter_list|()
block|{
name|checkRowCount
argument_list|(
literal|"select ename from emp union all select name from dept"
argument_list|,
name|EMP_SIZE
operator|+
name|DEPT_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testRowCountFilter
parameter_list|()
block|{
name|checkRowCount
argument_list|(
literal|"select * from emp where ename='Mathilda'"
argument_list|,
name|EMP_SIZE
operator|*
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testRowCountSort
parameter_list|()
block|{
name|checkRowCount
argument_list|(
literal|"select * from emp order by ename"
argument_list|,
name|EMP_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkFilterSelectivity
parameter_list|(
name|String
name|sql
parameter_list|,
name|double
name|expected
parameter_list|)
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|RelMetadataQuery
operator|.
name|getSelectivity
argument_list|(
name|rel
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivityIsNotNullFilter
parameter_list|()
block|{
name|checkFilterSelectivity
argument_list|(
literal|"select * from emp where deptno is not null"
argument_list|,
name|DEFAULT_NOTNULL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivityComparisonFilter
parameter_list|()
block|{
name|checkFilterSelectivity
argument_list|(
literal|"select * from emp where deptno> 10"
argument_list|,
name|DEFAULT_COMP_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivityAndFilter
parameter_list|()
block|{
name|checkFilterSelectivity
argument_list|(
literal|"select * from emp where ename = 'foo' and deptno = 10"
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY_SQUARED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivityOrFilter
parameter_list|()
block|{
name|checkFilterSelectivity
argument_list|(
literal|"select * from emp where ename = 'foo' or deptno = 10"
argument_list|,
name|DEFAULT_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkRelSelectivity
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|double
name|expected
parameter_list|)
block|{
name|Double
name|result
init|=
name|RelMetadataQuery
operator|.
name|getSelectivity
argument_list|(
name|rel
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivityRedundantFilter
parameter_list|()
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
literal|"select * from emp where deptno = 10"
argument_list|)
decl_stmt|;
name|checkRelSelectivity
argument_list|(
name|rel
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivitySort
parameter_list|()
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
literal|"select * from emp where deptno = 10"
operator|+
literal|"order by ename"
argument_list|)
decl_stmt|;
name|checkRelSelectivity
argument_list|(
name|rel
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivityUnion
parameter_list|()
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
literal|"select * from (select * from emp union all select * from emp) "
operator|+
literal|"where deptno = 10"
argument_list|)
decl_stmt|;
name|checkRelSelectivity
argument_list|(
name|rel
argument_list|,
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectivityAgg
parameter_list|()
block|{
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
literal|"select deptno, count(*) from emp where deptno> 10 "
operator|+
literal|"group by deptno having count(*) = 0"
argument_list|)
decl_stmt|;
name|checkRelSelectivity
argument_list|(
name|rel
argument_list|,
name|DEFAULT_COMP_SELECTIVITY
operator|*
name|DEFAULT_EQUAL_SELECTIVITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctRowCountTable
parameter_list|()
block|{
comment|// no unique key information is available so return null
name|RelNode
name|rel
init|=
name|convertSql
argument_list|(
literal|"select * from emp where deptno = 10"
argument_list|)
decl_stmt|;
name|BitSet
name|groupKey
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|Double
name|result
init|=
name|RelMetadataQuery
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
argument_list|,
name|groupKey
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelMetadataTest.java
end_comment

end_unit

