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
name|rel
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
name|Ord
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
name|TableFunctionScan
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
name|TableScan
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
name|LogicalAggregate
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
name|LogicalCalc
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
name|LogicalCorrelate
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
name|LogicalExchange
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
name|LogicalFilter
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
name|LogicalIntersect
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
name|LogicalMatch
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
name|LogicalMinus
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
name|LogicalProject
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
name|LogicalSort
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
name|LogicalTableModify
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
name|LogicalUnion
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
name|LogicalValues
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
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
name|Deque
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
comment|/**  * Basic implementation of {@link RelShuttle} that calls  * {@link RelNode#accept(RelShuttle)} on each child, and  * {@link RelNode#copy(org.apache.calcite.plan.RelTraitSet, java.util.List)} if  * any children change.  */
end_comment

begin_class
specifier|public
class|class
name|RelShuttleImpl
implements|implements
name|RelShuttle
block|{
specifier|protected
specifier|final
name|Deque
argument_list|<
name|RelNode
argument_list|>
name|stack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Visits a particular child of a parent.    */
specifier|protected
name|RelNode
name|visitChild
parameter_list|(
name|RelNode
name|parent
parameter_list|,
name|int
name|i
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
name|stack
operator|.
name|push
argument_list|(
name|parent
argument_list|)
expr_stmt|;
try|try
block|{
name|RelNode
name|child2
init|=
name|child
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|child2
operator|!=
name|child
condition|)
block|{
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|parent
operator|.
name|getInputs
argument_list|()
argument_list|)
decl_stmt|;
name|newInputs
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|child2
argument_list|)
expr_stmt|;
return|return
name|parent
operator|.
name|copy
argument_list|(
name|parent
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newInputs
argument_list|)
return|;
block|}
return|return
name|parent
return|;
block|}
finally|finally
block|{
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|RelNode
name|visitChildren
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
for|for
control|(
name|Ord
argument_list|<
name|RelNode
argument_list|>
name|input
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|rel
operator|.
name|getInputs
argument_list|()
argument_list|)
control|)
block|{
name|rel
operator|=
name|visitChild
argument_list|(
name|rel
argument_list|,
name|input
operator|.
name|i
argument_list|,
name|input
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|rel
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalAggregate
name|aggregate
parameter_list|)
block|{
return|return
name|visitChild
argument_list|(
name|aggregate
argument_list|,
literal|0
argument_list|,
name|aggregate
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalMatch
name|match
parameter_list|)
block|{
return|return
name|visitChild
argument_list|(
name|match
argument_list|,
literal|0
argument_list|,
name|match
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableScan
name|scan
parameter_list|)
block|{
return|return
name|scan
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableFunctionScan
name|scan
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|scan
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalValues
name|values
parameter_list|)
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalFilter
name|filter
parameter_list|)
block|{
return|return
name|visitChild
argument_list|(
name|filter
argument_list|,
literal|0
argument_list|,
name|filter
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalCalc
name|calc
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|calc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalProject
name|project
parameter_list|)
block|{
return|return
name|visitChild
argument_list|(
name|project
argument_list|,
literal|0
argument_list|,
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalJoin
name|join
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|join
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalCorrelate
name|correlate
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|correlate
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalUnion
name|union
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|union
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalIntersect
name|intersect
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|intersect
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalMinus
name|minus
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|minus
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalSort
name|sort
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|sort
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalExchange
name|exchange
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|exchange
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalTableModify
name|modify
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|modify
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|RelNode
name|other
parameter_list|)
block|{
return|return
name|visitChildren
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
end_class

end_unit

