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
name|linq4j
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|EnumerableDefaults
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
name|linq4j
operator|.
name|JoinType
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|linq4j
operator|.
name|function
operator|.
name|Function2
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
name|linq4j
operator|.
name|function
operator|.
name|Functions
operator|.
name|nullsComparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|Arrays
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test validating the order preserving properties of join algorithms in  * {@link org.apache.calcite.linq4j.ExtendedEnumerable}. The correctness of the  * join algorithm is not examined by this set of tests.  *  *<p>To verify that the order of left/right/both input(s) is preserved they  * must be all ordered by at least one column. The inputs are either sorted on  * the join or some other column. For the tests to be meaningful the result of  * the join must not be empty.  *  *<p>Interesting variants that may affect the join output and thus destroy the  * order of one or both inputs is when the join column or the sorted column  * (when join column != sort column) contain nulls or duplicate values.  *  *<p>In addition, the way that nulls are sorted before the join can also play  * an important role regarding the order preserving semantics of the join.  *  *<p>Last but not least, the type of the join (left/right/full/inner/semi/anti)  * has a major impact on the preservation of order for the various joins.  */
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
specifier|final
class|class
name|JoinPreserveOrderTest
block|{
comment|/**    * A description holding which column must be sorted and how.    * @param<T> the type of the input relation    */
specifier|private
specifier|static
class|class
name|FieldCollationDescription
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|final
name|String
name|colName
decl_stmt|;
specifier|private
specifier|final
name|Function1
argument_list|<
name|T
argument_list|,
name|Comparable
argument_list|>
name|colSelector
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isAscending
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isNullsFirst
decl_stmt|;
name|FieldCollationDescription
parameter_list|(
specifier|final
name|String
name|colName
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|T
argument_list|,
name|Comparable
argument_list|>
name|colSelector
parameter_list|,
specifier|final
name|boolean
name|isAscending
parameter_list|,
specifier|final
name|boolean
name|isNullsFirst
parameter_list|)
block|{
name|this
operator|.
name|colName
operator|=
name|colName
expr_stmt|;
name|this
operator|.
name|colSelector
operator|=
name|colSelector
expr_stmt|;
name|this
operator|.
name|isAscending
operator|=
name|isAscending
expr_stmt|;
name|this
operator|.
name|isNullsFirst
operator|=
name|isNullsFirst
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"on='"
operator|+
name|colName
operator|+
literal|"', asc="
operator|+
name|isAscending
operator|+
literal|", nullsFirst="
operator|+
name|isNullsFirst
operator|+
literal|'}'
return|;
block|}
block|}
comment|/**    * An abstraction for a join algorithm which performs an operation on two inputs and produces a    * result.    *    * @param<L> the type of the left input    * @param<R> the type of the right input    * @param<Result> the type of the result    */
specifier|private
interface|interface
name|JoinAlgorithm
parameter_list|<
name|L
parameter_list|,
name|R
parameter_list|,
name|Result
parameter_list|>
block|{
name|Enumerable
argument_list|<
name|Result
argument_list|>
name|join
parameter_list|(
name|Enumerable
argument_list|<
name|L
argument_list|>
name|left
parameter_list|,
name|Enumerable
argument_list|<
name|R
argument_list|>
name|right
parameter_list|)
function_decl|;
block|}
specifier|private
specifier|final
name|FieldCollationDescription
argument_list|<
name|Employee
argument_list|>
name|leftColumn
decl_stmt|;
specifier|private
specifier|final
name|FieldCollationDescription
argument_list|<
name|Department
argument_list|>
name|rightColumn
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Function2
argument_list|<
name|Employee
argument_list|,
name|Department
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|RESULT_SELECTOR
init|=
parameter_list|(
name|emp
parameter_list|,
name|dept
parameter_list|)
lambda|->
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|emp
operator|!=
literal|null
operator|)
condition|?
name|emp
operator|.
name|eid
else|:
literal|null
argument_list|,
operator|(
name|dept
operator|!=
literal|null
operator|)
condition|?
name|dept
operator|.
name|did
else|:
literal|null
argument_list|)
decl_stmt|;
specifier|public
name|JoinPreserveOrderTest
parameter_list|(
specifier|final
name|FieldCollationDescription
argument_list|<
name|Employee
argument_list|>
name|leftColumn
parameter_list|,
specifier|final
name|FieldCollationDescription
argument_list|<
name|Department
argument_list|>
name|rightColumn
parameter_list|)
block|{
name|this
operator|.
name|leftColumn
operator|=
name|leftColumn
expr_stmt|;
name|this
operator|.
name|rightColumn
operator|=
name|rightColumn
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{index}: columnLeft({0}), columnRight({1})"
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
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|empOrderColNames
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"name"
argument_list|,
literal|"deptno"
argument_list|,
literal|"eid"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Comparable
argument_list|>
argument_list|>
name|empOrderColSelectors
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|Employee
operator|::
name|getName
argument_list|,
name|Employee
operator|::
name|getDeptno
argument_list|,
name|Employee
operator|::
name|getEid
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|deptOrderColNames
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"name"
argument_list|,
literal|"deptno"
argument_list|,
literal|"did"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Function1
argument_list|<
name|Department
argument_list|,
name|Comparable
argument_list|>
argument_list|>
name|deptOrderColSelectors
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|Department
operator|::
name|getName
argument_list|,
name|Department
operator|::
name|getDeptno
argument_list|,
name|Department
operator|::
name|getDid
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Boolean
argument_list|>
name|trueFalse
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
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
name|empOrderColNames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|Boolean
name|ascendingL
range|:
name|trueFalse
control|)
block|{
for|for
control|(
name|Boolean
name|nullsFirstL
range|:
name|trueFalse
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|deptOrderColNames
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|Boolean
name|nullsFirstR
range|:
name|trueFalse
control|)
block|{
for|for
control|(
name|Boolean
name|ascendingR
range|:
name|trueFalse
control|)
block|{
name|Object
index|[]
name|params
init|=
operator|new
name|Object
index|[
literal|2
index|]
decl_stmt|;
name|params
index|[
literal|0
index|]
operator|=
operator|new
name|FieldCollationDescription
argument_list|<>
argument_list|(
name|empOrderColNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|empOrderColSelectors
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|ascendingL
argument_list|,
name|nullsFirstL
argument_list|)
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
operator|new
name|FieldCollationDescription
argument_list|<>
argument_list|(
name|deptOrderColNames
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|deptOrderColSelectors
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|ascendingR
argument_list|,
name|nullsFirstR
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
return|return
name|data
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLeftJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|hashJoin
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRightJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|leftColumn
operator|.
name|isNullsFirst
argument_list|)
expr_stmt|;
name|testJoin
argument_list|(
name|hashJoin
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFullJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|leftColumn
operator|.
name|isNullsFirst
argument_list|)
expr_stmt|;
name|testJoin
argument_list|(
name|hashJoin
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInnerJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|hashJoin
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLeftNestedLoopJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|nestedLoopJoin
argument_list|(
name|JoinType
operator|.
name|LEFT
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRightNestedLoopJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|leftColumn
operator|.
name|isNullsFirst
argument_list|)
expr_stmt|;
name|testJoin
argument_list|(
name|nestedLoopJoin
argument_list|(
name|JoinType
operator|.
name|RIGHT
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFullNestedLoopJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|leftColumn
operator|.
name|isNullsFirst
argument_list|)
expr_stmt|;
name|testJoin
argument_list|(
name|nestedLoopJoin
argument_list|(
name|JoinType
operator|.
name|FULL
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInnerNestedLoopJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|nestedLoopJoin
argument_list|(
name|JoinType
operator|.
name|INNER
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLeftCorrelateJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|correlateJoin
argument_list|(
name|JoinType
operator|.
name|LEFT
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInnerCorrelateJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|correlateJoin
argument_list|(
name|JoinType
operator|.
name|INNER
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAntiCorrelateJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|correlateJoin
argument_list|(
name|JoinType
operator|.
name|ANTI
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSemiCorrelateJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|correlateJoin
argument_list|(
name|JoinType
operator|.
name|SEMI
argument_list|)
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSemiDefaultJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|semiJoin
argument_list|()
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAntiDefaultJoinPreservesOrderOfLeftInput
parameter_list|()
block|{
name|testJoin
argument_list|(
name|antiJoin
argument_list|()
argument_list|,
name|AssertOrder
operator|.
name|PRESERVED
argument_list|,
name|AssertOrder
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testJoin
parameter_list|(
name|JoinAlgorithm
argument_list|<
name|Employee
argument_list|,
name|Department
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|joinAlgorithm
parameter_list|,
name|AssertOrder
name|assertLeftInput
parameter_list|,
name|AssertOrder
name|assertRightInput
parameter_list|)
block|{
name|Enumerable
argument_list|<
name|Employee
argument_list|>
name|left
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|EMPS
argument_list|)
operator|.
name|orderBy
argument_list|(
name|leftColumn
operator|.
name|colSelector
argument_list|,
name|nullsComparator
argument_list|(
name|leftColumn
operator|.
name|isNullsFirst
argument_list|,
operator|!
name|leftColumn
operator|.
name|isAscending
argument_list|)
argument_list|)
decl_stmt|;
name|Enumerable
argument_list|<
name|Department
argument_list|>
name|right
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|DEPTS
argument_list|)
operator|.
name|orderBy
argument_list|(
name|rightColumn
operator|.
name|colSelector
argument_list|,
name|nullsComparator
argument_list|(
name|rightColumn
operator|.
name|isNullsFirst
argument_list|,
operator|!
name|rightColumn
operator|.
name|isAscending
argument_list|)
argument_list|)
decl_stmt|;
name|Enumerable
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|joinResult
init|=
name|joinAlgorithm
operator|.
name|join
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|actualIdOrderLeft
init|=
name|joinResult
operator|.
name|select
argument_list|(
name|joinTuple
lambda|->
name|joinTuple
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|expectedIdOrderLeft
init|=
name|left
operator|.
name|select
argument_list|(
name|e
lambda|->
name|e
operator|.
name|eid
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertLeftInput
operator|.
name|check
argument_list|(
name|expectedIdOrderLeft
argument_list|,
name|actualIdOrderLeft
argument_list|,
name|leftColumn
operator|.
name|isNullsFirst
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|actualIdOrderRight
init|=
name|joinResult
operator|.
name|select
argument_list|(
name|joinTuple
lambda|->
name|joinTuple
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|expectedIdOrderRight
init|=
name|right
operator|.
name|select
argument_list|(
name|d
lambda|->
name|d
operator|.
name|did
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertRightInput
operator|.
name|check
argument_list|(
name|expectedIdOrderRight
argument_list|,
name|actualIdOrderRight
argument_list|,
name|rightColumn
operator|.
name|isNullsFirst
argument_list|)
expr_stmt|;
block|}
specifier|private
name|JoinAlgorithm
argument_list|<
name|Employee
argument_list|,
name|Department
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|correlateJoin
parameter_list|(
name|JoinType
name|joinType
parameter_list|)
block|{
return|return
parameter_list|(
name|left
parameter_list|,
name|right
parameter_list|)
lambda|->
name|left
operator|.
name|correlateJoin
argument_list|(
name|joinType
argument_list|,
name|emp
lambda|->
name|right
operator|.
name|where
argument_list|(
name|dept
lambda|->
name|emp
operator|.
name|deptno
operator|!=
literal|null
operator|&&
name|dept
operator|.
name|deptno
operator|!=
literal|null
operator|&&
name|emp
operator|.
name|deptno
operator|.
name|equals
argument_list|(
name|dept
operator|.
name|deptno
argument_list|)
argument_list|)
argument_list|,
name|RESULT_SELECTOR
argument_list|)
return|;
block|}
specifier|private
name|JoinAlgorithm
argument_list|<
name|Employee
argument_list|,
name|Department
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|hashJoin
parameter_list|(
name|boolean
name|generateNullsOnLeft
parameter_list|,
name|boolean
name|generateNullsOnRight
parameter_list|)
block|{
return|return
parameter_list|(
name|left
parameter_list|,
name|right
parameter_list|)
lambda|->
name|left
operator|.
name|hashJoin
argument_list|(
name|right
argument_list|,
name|e
lambda|->
name|e
operator|.
name|deptno
argument_list|,
name|d
lambda|->
name|d
operator|.
name|deptno
argument_list|,
name|RESULT_SELECTOR
argument_list|,
literal|null
argument_list|,
name|generateNullsOnLeft
argument_list|,
name|generateNullsOnRight
argument_list|)
return|;
block|}
specifier|private
name|JoinAlgorithm
argument_list|<
name|Employee
argument_list|,
name|Department
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|nestedLoopJoin
parameter_list|(
name|JoinType
name|joinType
parameter_list|)
block|{
return|return
parameter_list|(
name|left
parameter_list|,
name|right
parameter_list|)
lambda|->
name|EnumerableDefaults
operator|.
name|nestedLoopJoin
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
parameter_list|(
name|emp
parameter_list|,
name|dept
parameter_list|)
lambda|->
name|emp
operator|.
name|deptno
operator|!=
literal|null
operator|&&
name|dept
operator|.
name|deptno
operator|!=
literal|null
operator|&&
name|emp
operator|.
name|deptno
operator|.
name|equals
argument_list|(
name|dept
operator|.
name|deptno
argument_list|)
argument_list|,
name|RESULT_SELECTOR
argument_list|,
name|joinType
argument_list|)
return|;
block|}
specifier|private
name|JoinAlgorithm
argument_list|<
name|Employee
argument_list|,
name|Department
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|semiJoin
parameter_list|()
block|{
return|return
parameter_list|(
name|left
parameter_list|,
name|right
parameter_list|)
lambda|->
name|EnumerableDefaults
operator|.
name|semiJoin
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|emp
lambda|->
name|emp
operator|.
name|deptno
argument_list|,
name|dept
lambda|->
name|dept
operator|.
name|deptno
argument_list|)
operator|.
name|select
argument_list|(
name|emp
lambda|->
name|Arrays
operator|.
name|asList
argument_list|(
name|emp
operator|.
name|eid
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|JoinAlgorithm
argument_list|<
name|Employee
argument_list|,
name|Department
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|antiJoin
parameter_list|()
block|{
return|return
parameter_list|(
name|left
parameter_list|,
name|right
parameter_list|)
lambda|->
name|EnumerableDefaults
operator|.
name|antiJoin
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|emp
lambda|->
name|emp
operator|.
name|deptno
argument_list|,
name|dept
lambda|->
name|dept
operator|.
name|deptno
argument_list|)
operator|.
name|select
argument_list|(
name|emp
lambda|->
name|Arrays
operator|.
name|asList
argument_list|(
name|emp
operator|.
name|eid
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Different assertions for the result of the join.    */
specifier|private
enum|enum
name|AssertOrder
block|{
name|PRESERVED
block|{
annotation|@
name|Override
argument_list|<
name|E
argument_list|>
name|void
name|check
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|expected
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|actual
parameter_list|,
specifier|final
name|boolean
name|nullsFirst
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Order is not preserved. Expected:<"
operator|+
name|expected
operator|+
literal|"> but was:<"
operator|+
name|actual
operator|+
literal|">"
argument_list|,
name|isOrderPreserved
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|nullsFirst
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|DESTROYED
block|{
annotation|@
name|Override
argument_list|<
name|E
argument_list|>
name|void
name|check
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|expected
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|actual
parameter_list|,
specifier|final
name|boolean
name|nullsFirst
parameter_list|)
block|{
name|assertFalse
argument_list|(
literal|"Order is not destroyed. Expected:<"
operator|+
name|expected
operator|+
literal|"> but was:<"
operator|+
name|actual
operator|+
literal|">"
argument_list|,
name|isOrderPreserved
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|nullsFirst
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|IGNORED
block|{
annotation|@
name|Override
argument_list|<
name|E
argument_list|>
name|void
name|check
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|expected
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|actual
parameter_list|,
specifier|final
name|boolean
name|nullsFirst
parameter_list|)
block|{
comment|// Do nothing
block|}
block|}
block|;
specifier|abstract
argument_list|<
name|E
argument_list|>
name|void
name|check
argument_list|(
name|List
argument_list|<
name|E
argument_list|>
name|expected
argument_list|,
name|List
argument_list|<
name|E
argument_list|>
name|actual
argument_list|,
name|boolean
name|nullsFirst
argument_list|)
decl_stmt|;
comment|/**      * Checks that the elements in the list are in the expected order.      */
parameter_list|<
name|E
parameter_list|>
name|boolean
name|isOrderPreserved
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|expected
parameter_list|,
name|List
argument_list|<
name|E
argument_list|>
name|actual
parameter_list|,
name|boolean
name|nullsFirst
parameter_list|)
block|{
name|boolean
name|isPreserved
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|actual
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|E
name|prev
init|=
name|actual
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
name|E
name|next
init|=
name|actual
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|posPrev
init|=
name|prev
operator|==
literal|null
condition|?
operator|(
name|nullsFirst
condition|?
operator|-
literal|1
else|:
name|actual
operator|.
name|size
argument_list|()
operator|)
else|:
name|expected
operator|.
name|indexOf
argument_list|(
name|prev
argument_list|)
decl_stmt|;
name|int
name|posNext
init|=
name|next
operator|==
literal|null
condition|?
operator|(
name|nullsFirst
condition|?
operator|-
literal|1
else|:
name|actual
operator|.
name|size
argument_list|()
operator|)
else|:
name|expected
operator|.
name|indexOf
argument_list|(
name|next
argument_list|)
decl_stmt|;
name|isPreserved
operator|&=
name|posPrev
operator|<=
name|posNext
expr_stmt|;
block|}
return|return
name|isPreserved
return|;
block|}
block|}
comment|/** Department */
specifier|private
specifier|static
class|class
name|Department
block|{
specifier|private
specifier|final
name|int
name|did
decl_stmt|;
specifier|private
specifier|final
name|Integer
name|deptno
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|Department
parameter_list|(
specifier|final
name|int
name|did
parameter_list|,
specifier|final
name|Integer
name|deptno
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|did
operator|=
name|did
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
name|int
name|getDid
parameter_list|()
block|{
return|return
name|did
return|;
block|}
name|Integer
name|getDeptno
parameter_list|()
block|{
return|return
name|deptno
return|;
block|}
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
comment|/** Employee */
specifier|private
specifier|static
class|class
name|Employee
block|{
specifier|private
specifier|final
name|int
name|eid
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Integer
name|deptno
decl_stmt|;
name|Employee
parameter_list|(
specifier|final
name|int
name|eid
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Integer
name|deptno
parameter_list|)
block|{
name|this
operator|.
name|eid
operator|=
name|eid
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
block|}
name|int
name|getEid
parameter_list|()
block|{
return|return
name|eid
return|;
block|}
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
name|Integer
name|getDeptno
parameter_list|()
block|{
return|return
name|deptno
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Employee{eid="
operator|+
name|eid
operator|+
literal|", name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|", deptno="
operator|+
name|deptno
operator|+
literal|'}'
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|Employee
index|[]
name|EMPS
init|=
operator|new
name|Employee
index|[]
block|{
operator|new
name|Employee
argument_list|(
literal|100
argument_list|,
literal|"Stam"
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|110
argument_list|,
literal|"Greg"
argument_list|,
literal|20
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|120
argument_list|,
literal|"Ilias"
argument_list|,
literal|30
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|130
argument_list|,
literal|"Ruben"
argument_list|,
literal|40
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|140
argument_list|,
literal|"Tanguy"
argument_list|,
literal|50
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|150
argument_list|,
literal|"Andrew"
argument_list|,
operator|-
literal|10
argument_list|)
block|,
comment|// Nulls on name
operator|new
name|Employee
argument_list|(
literal|160
argument_list|,
literal|null
argument_list|,
literal|60
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|170
argument_list|,
literal|null
argument_list|,
operator|-
literal|60
argument_list|)
block|,
comment|// Nulls on deptno
operator|new
name|Employee
argument_list|(
literal|180
argument_list|,
literal|"Achille"
argument_list|,
literal|null
argument_list|)
block|,
comment|// Duplicate values on name
operator|new
name|Employee
argument_list|(
literal|190
argument_list|,
literal|"Greg"
argument_list|,
literal|70
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|200
argument_list|,
literal|"Ilias"
argument_list|,
operator|-
literal|70
argument_list|)
block|,
comment|// Duplicates values on deptno
operator|new
name|Employee
argument_list|(
literal|210
argument_list|,
literal|"Sophia"
argument_list|,
literal|40
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|220
argument_list|,
literal|"Alexia"
argument_list|,
operator|-
literal|40
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|230
argument_list|,
literal|"Loukia"
argument_list|,
operator|-
literal|40
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Department
index|[]
name|DEPTS
init|=
operator|new
name|Department
index|[]
block|{
operator|new
name|Department
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|,
literal|"Sales"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|,
literal|"Pre-sales"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|4
argument_list|,
literal|40
argument_list|,
literal|"Support"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|5
argument_list|,
literal|50
argument_list|,
literal|"Marketing"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|6
argument_list|,
literal|60
argument_list|,
literal|"Engineering"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|7
argument_list|,
literal|70
argument_list|,
literal|"Management"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|8
argument_list|,
literal|80
argument_list|,
literal|"HR"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|9
argument_list|,
literal|90
argument_list|,
literal|"Product design"
argument_list|)
block|,
comment|// Nulls on name
operator|new
name|Department
argument_list|(
literal|3
argument_list|,
literal|30
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|)
block|,
comment|// Nulls on deptno
operator|new
name|Department
argument_list|(
literal|11
argument_list|,
literal|null
argument_list|,
literal|"Post-sales"
argument_list|)
block|,
comment|// Duplicate values on name
operator|new
name|Department
argument_list|(
literal|12
argument_list|,
literal|50
argument_list|,
literal|"Support"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|13
argument_list|,
literal|140
argument_list|,
literal|"Support"
argument_list|)
block|,
comment|// Duplicate values on deptno
operator|new
name|Department
argument_list|(
literal|14
argument_list|,
literal|20
argument_list|,
literal|"Board"
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|15
argument_list|,
literal|40
argument_list|,
literal|"Promotions"
argument_list|)
block|,   }
decl_stmt|;
block|}
end_class

begin_comment
comment|// End JoinPreserveOrderTest.java
end_comment

end_unit

