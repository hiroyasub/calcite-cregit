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
name|runtime
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
name|Functions
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
name|Predicate2
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
name|Arrays
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
name|equalTo
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link org.apache.calcite.runtime.Enumerables}.  */
end_comment

begin_class
specifier|public
class|class
name|EnumerablesTest
block|{
specifier|private
specifier|static
specifier|final
name|Enumerable
argument_list|<
name|Emp
argument_list|>
name|EMPS
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Emp
argument_list|(
literal|10
argument_list|,
literal|"Fred"
argument_list|)
argument_list|,
operator|new
name|Emp
argument_list|(
literal|20
argument_list|,
literal|"Theodore"
argument_list|)
argument_list|,
operator|new
name|Emp
argument_list|(
literal|20
argument_list|,
literal|"Sebastian"
argument_list|)
argument_list|,
operator|new
name|Emp
argument_list|(
literal|30
argument_list|,
literal|"Joe"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Enumerable
argument_list|<
name|Dept
argument_list|>
name|DEPTS
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Dept
argument_list|(
literal|20
argument_list|,
literal|"Sales"
argument_list|)
argument_list|,
operator|new
name|Dept
argument_list|(
literal|15
argument_list|,
literal|"Marketing"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Function2
argument_list|<
name|Emp
argument_list|,
name|Dept
argument_list|,
name|String
argument_list|>
name|EMP_DEPT_TO_STRING
init|=
operator|new
name|Function2
argument_list|<
name|Emp
argument_list|,
name|Dept
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|Emp
name|v0
parameter_list|,
name|Dept
name|v1
parameter_list|)
block|{
return|return
literal|"{"
operator|+
operator|(
name|v0
operator|==
literal|null
condition|?
literal|null
else|:
name|v0
operator|.
name|name
operator|)
operator|+
literal|", "
operator|+
operator|(
name|v0
operator|==
literal|null
condition|?
literal|null
else|:
name|v0
operator|.
name|deptno
operator|)
operator|+
literal|", "
operator|+
operator|(
name|v1
operator|==
literal|null
condition|?
literal|null
else|:
name|v1
operator|.
name|deptno
operator|)
operator|+
literal|", "
operator|+
operator|(
name|v1
operator|==
literal|null
condition|?
literal|null
else|:
name|v1
operator|.
name|name
operator|)
operator|+
literal|"}"
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Predicate2
argument_list|<
name|Emp
argument_list|,
name|Dept
argument_list|>
name|EQUAL_DEPTNO
init|=
operator|new
name|Predicate2
argument_list|<
name|Emp
argument_list|,
name|Dept
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Emp
name|v0
parameter_list|,
name|Dept
name|v1
parameter_list|)
block|{
return|return
name|v0
operator|.
name|deptno
operator|==
name|v1
operator|.
name|deptno
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSemiJoin
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|semiJoin
argument_list|(
name|EMPS
argument_list|,
name|DEPTS
argument_list|,
operator|new
name|Function1
argument_list|<
name|Emp
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Emp
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|.
name|deptno
return|;
block|}
block|}
argument_list|,
operator|new
name|Function1
argument_list|<
name|Dept
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Dept
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|.
name|deptno
return|;
block|}
block|}
argument_list|,
name|Functions
operator|.
expr|<
name|Integer
operator|>
name|identityComparer
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[Emp(20, Theodore), Emp(20, Sebastian)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMergeJoin
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|mergeJoin
argument_list|(
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Emp
argument_list|(
literal|10
argument_list|,
literal|"Fred"
argument_list|)
argument_list|,
operator|new
name|Emp
argument_list|(
literal|20
argument_list|,
literal|"Theodore"
argument_list|)
argument_list|,
operator|new
name|Emp
argument_list|(
literal|20
argument_list|,
literal|"Sebastian"
argument_list|)
argument_list|,
operator|new
name|Emp
argument_list|(
literal|30
argument_list|,
literal|"Joe"
argument_list|)
argument_list|,
operator|new
name|Emp
argument_list|(
literal|30
argument_list|,
literal|"Greg"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Dept
argument_list|(
literal|15
argument_list|,
literal|"Marketing"
argument_list|)
argument_list|,
operator|new
name|Dept
argument_list|(
literal|20
argument_list|,
literal|"Sales"
argument_list|)
argument_list|,
operator|new
name|Dept
argument_list|(
literal|30
argument_list|,
literal|"Research"
argument_list|)
argument_list|,
operator|new
name|Dept
argument_list|(
literal|30
argument_list|,
literal|"Development"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Function1
argument_list|<
name|Emp
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Emp
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|.
name|deptno
return|;
block|}
block|}
argument_list|,
operator|new
name|Function1
argument_list|<
name|Dept
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Dept
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|.
name|deptno
return|;
block|}
block|}
argument_list|,
operator|new
name|Function2
argument_list|<
name|Emp
argument_list|,
name|Dept
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|Emp
name|v0
parameter_list|,
name|Dept
name|v1
parameter_list|)
block|{
return|return
name|v0
operator|+
literal|", "
operator|+
name|v1
return|;
block|}
block|}
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[Emp(20, Theodore), Dept(20, Sales),"
operator|+
literal|" Emp(20, Sebastian), Dept(20, Sales),"
operator|+
literal|" Emp(30, Joe), Dept(30, Research),"
operator|+
literal|" Emp(30, Joe), Dept(30, Development),"
operator|+
literal|" Emp(30, Greg), Dept(30, Research),"
operator|+
literal|" Emp(30, Greg), Dept(30, Development)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMergeJoin2
parameter_list|()
block|{
comment|// Matching keys at start
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[1, 4]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Matching key at start and end of right, not of left
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[1, 4]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Matching key at start and end of left, not right
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[1, 4]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Matching key not at start or end of left or right
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[3, 4]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMergeJoin3
parameter_list|()
block|{
comment|// No overlap
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Left empty
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
expr|<
name|Integer
operator|>
name|newArrayList
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Right empty
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|3
argument_list|,
literal|7
argument_list|)
argument_list|,
name|Lists
operator|.
expr|<
name|Integer
operator|>
name|newArrayList
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Both empty
name|assertThat
argument_list|(
name|intersect
argument_list|(
name|Lists
operator|.
expr|<
name|Integer
operator|>
name|newArrayList
argument_list|()
argument_list|,
name|Lists
operator|.
expr|<
name|Integer
operator|>
name|newArrayList
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|Enumerable
argument_list|<
name|T
argument_list|>
name|intersect
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list0
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|list1
parameter_list|)
block|{
return|return
name|EnumerableDefaults
operator|.
name|mergeJoin
argument_list|(
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|list0
argument_list|)
argument_list|,
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|list1
argument_list|)
argument_list|,
name|Functions
operator|.
expr|<
name|T
operator|>
name|identitySelector
argument_list|()
argument_list|,
name|Functions
operator|.
expr|<
name|T
operator|>
name|identitySelector
argument_list|()
argument_list|,
operator|new
name|Function2
argument_list|<
name|T
argument_list|,
name|T
argument_list|,
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|T
name|apply
parameter_list|(
name|T
name|v0
parameter_list|,
name|T
name|v1
parameter_list|)
block|{
return|return
name|v0
return|;
block|}
block|}
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThetaJoin
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|thetaJoin
argument_list|(
name|EMPS
argument_list|,
name|DEPTS
argument_list|,
name|EQUAL_DEPTNO
argument_list|,
name|EMP_DEPT_TO_STRING
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{Theodore, 20, 20, Sales}, {Sebastian, 20, 20, Sales}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThetaLeftJoin
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|thetaJoin
argument_list|(
name|EMPS
argument_list|,
name|DEPTS
argument_list|,
name|EQUAL_DEPTNO
argument_list|,
name|EMP_DEPT_TO_STRING
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{Fred, 10, null, null}, {Theodore, 20, 20, Sales}, "
operator|+
literal|"{Sebastian, 20, 20, Sales}, {Joe, 30, null, null}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThetaRightJoin
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|thetaJoin
argument_list|(
name|EMPS
argument_list|,
name|DEPTS
argument_list|,
name|EQUAL_DEPTNO
argument_list|,
name|EMP_DEPT_TO_STRING
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{Theodore, 20, 20, Sales}, {Sebastian, 20, 20, Sales}, "
operator|+
literal|"{null, null, 15, Marketing}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThetaFullJoin
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|thetaJoin
argument_list|(
name|EMPS
argument_list|,
name|DEPTS
argument_list|,
name|EQUAL_DEPTNO
argument_list|,
name|EMP_DEPT_TO_STRING
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{Fred, 10, null, null}, {Theodore, 20, 20, Sales}, "
operator|+
literal|"{Sebastian, 20, 20, Sales}, {Joe, 30, null, null}, "
operator|+
literal|"{null, null, 15, Marketing}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThetaFullJoinLeftEmpty
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|thetaJoin
argument_list|(
name|EMPS
operator|.
name|take
argument_list|(
literal|0
argument_list|)
argument_list|,
name|DEPTS
argument_list|,
name|EQUAL_DEPTNO
argument_list|,
name|EMP_DEPT_TO_STRING
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|.
name|orderBy
argument_list|(
name|Functions
operator|.
expr|<
name|String
operator|>
name|identitySelector
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{null, null, 15, Marketing}, {null, null, 20, Sales}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThetaFullJoinRightEmpty
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|thetaJoin
argument_list|(
name|EMPS
argument_list|,
name|DEPTS
operator|.
name|take
argument_list|(
literal|0
argument_list|)
argument_list|,
name|EQUAL_DEPTNO
argument_list|,
name|EMP_DEPT_TO_STRING
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{Fred, 10, null, null}, {Theodore, 20, null, null}, "
operator|+
literal|"{Sebastian, 20, null, null}, {Joe, 30, null, null}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThetaFullJoinBothEmpty
parameter_list|()
block|{
name|assertThat
argument_list|(
name|EnumerableDefaults
operator|.
name|thetaJoin
argument_list|(
name|EMPS
operator|.
name|take
argument_list|(
literal|0
argument_list|)
argument_list|,
name|DEPTS
operator|.
name|take
argument_list|(
literal|0
argument_list|)
argument_list|,
name|EQUAL_DEPTNO
argument_list|,
name|EMP_DEPT_TO_STRING
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Employee record. */
specifier|private
specifier|static
class|class
name|Emp
block|{
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
name|Emp
parameter_list|(
name|int
name|deptno
parameter_list|,
name|String
name|name
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
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Emp("
operator|+
name|deptno
operator|+
literal|", "
operator|+
name|name
operator|+
literal|")"
return|;
block|}
block|}
comment|/** Department record. */
specifier|private
specifier|static
class|class
name|Dept
block|{
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
name|Dept
parameter_list|(
name|int
name|deptno
parameter_list|,
name|String
name|name
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
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Dept("
operator|+
name|deptno
operator|+
literal|", "
operator|+
name|name
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End EnumerablesTest.java
end_comment

end_unit

