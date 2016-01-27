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
name|adapter
operator|.
name|enumerable
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
name|plan
operator|.
name|Convention
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
name|RelOptCluster
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
name|InvalidRelException
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
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|core
operator|.
name|JoinInfo
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
name|core
operator|.
name|JoinRelType
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
name|logical
operator|.
name|LogicalJoin
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

begin_comment
comment|/** Planner rule that converts a  * {@link org.apache.calcite.rel.logical.LogicalJoin} relational expression  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
class|class
name|EnumerableJoinRule
extends|extends
name|ConverterRule
block|{
name|EnumerableJoinRule
parameter_list|()
block|{
name|super
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"EnumerableJoinRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|LogicalJoin
name|join
init|=
operator|(
name|LogicalJoin
operator|)
name|rel
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|join
operator|.
name|getInputs
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|input
operator|.
name|getConvention
argument_list|()
operator|instanceof
name|EnumerableConvention
operator|)
condition|)
block|{
name|input
operator|=
name|convert
argument_list|(
name|input
argument_list|,
name|input
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
argument_list|)
expr_stmt|;
block|}
name|newInputs
operator|.
name|add
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelOptCluster
name|cluster
init|=
name|join
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|join
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
specifier|final
name|RelNode
name|left
init|=
name|newInputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|right
init|=
name|newInputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|JoinInfo
name|info
init|=
name|JoinInfo
operator|.
name|of
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|info
operator|.
name|isEqui
argument_list|()
operator|&&
name|join
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
comment|// EnumerableJoinRel only supports equi-join. We can put a filter on top
comment|// if it is an inner join.
try|try
block|{
return|return
operator|new
name|EnumerableThetaJoin
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|join
operator|.
name|getVariablesSet
argument_list|()
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidRelException
name|e
parameter_list|)
block|{
name|EnumerableRules
operator|.
name|LOGGER
operator|.
name|debug
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
name|RelNode
name|newRel
decl_stmt|;
try|try
block|{
name|newRel
operator|=
operator|new
name|EnumerableJoin
argument_list|(
name|cluster
argument_list|,
name|join
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
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|info
operator|.
name|getEquiCondition
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
argument_list|,
name|info
operator|.
name|leftKeys
argument_list|,
name|info
operator|.
name|rightKeys
argument_list|,
name|join
operator|.
name|getVariablesSet
argument_list|()
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidRelException
name|e
parameter_list|)
block|{
name|EnumerableRules
operator|.
name|LOGGER
operator|.
name|debug
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|info
operator|.
name|isEqui
argument_list|()
condition|)
block|{
name|newRel
operator|=
operator|new
name|EnumerableFilter
argument_list|(
name|cluster
argument_list|,
name|newRel
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newRel
argument_list|,
name|info
operator|.
name|getRemaining
argument_list|(
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newRel
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableJoinRule.java
end_comment

end_unit

