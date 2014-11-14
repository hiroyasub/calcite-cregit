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
name|Functions
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
annotation|@
name|Test
specifier|public
name|void
name|testSemiJoin
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Enumerables
operator|.
name|semiJoin
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

