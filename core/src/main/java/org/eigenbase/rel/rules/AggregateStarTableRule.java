begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|rules
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|AggregateRelBase
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
name|ProjectRelBase
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
name|relopt
operator|.
name|RelOptCluster
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
name|RelOptLattice
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
name|RelOptRule
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
name|RelOptRuleCall
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
name|RelOptRuleOperand
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
name|RelOptTable
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|StarTable
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
name|OptiqSchema
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
name|RelOptTableImpl
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

begin_comment
comment|/**  * Planner rule that matches an {@link org.eigenbase.rel.AggregateRelBase} on  * top of a {@link net.hydromatic.optiq.impl.StarTable.StarTableScan}.  *  *<p>This pattern indicates that an aggregate table may exist. The rule asks  * the star table for an aggregate table at the required level of aggregation.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateStarTableRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateStarTableRule
name|INSTANCE
init|=
operator|new
name|AggregateStarTableRule
argument_list|(
name|operand
argument_list|(
name|AggregateRelBase
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|StarTable
operator|.
name|StarTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"AggregateStarTableRule"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|AggregateStarTableRule
name|INSTANCE2
init|=
operator|new
name|AggregateStarTableRule
argument_list|(
name|operand
argument_list|(
name|AggregateRelBase
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|ProjectRelBase
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|StarTable
operator|.
name|StarTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"AggregateStarTableRule:project"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|AggregateRelBase
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|ProjectRelBase
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|StarTable
operator|.
name|StarTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|AggregateProjectMergeRule
operator|.
name|apply
argument_list|(
name|aggregate
argument_list|,
name|project
argument_list|)
decl_stmt|;
specifier|final
name|AggregateRelBase
name|aggregate2
decl_stmt|;
specifier|final
name|ProjectRelBase
name|project2
decl_stmt|;
if|if
condition|(
name|rel
operator|instanceof
name|AggregateRelBase
condition|)
block|{
name|project2
operator|=
literal|null
expr_stmt|;
name|aggregate2
operator|=
operator|(
name|AggregateRelBase
operator|)
name|rel
expr_stmt|;
block|}
if|else if
condition|(
name|rel
operator|instanceof
name|ProjectRelBase
condition|)
block|{
name|project2
operator|=
operator|(
name|ProjectRelBase
operator|)
name|rel
expr_stmt|;
name|aggregate2
operator|=
operator|(
name|AggregateRelBase
operator|)
name|project2
operator|.
name|getChild
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
name|apply
argument_list|(
name|call
argument_list|,
name|project2
argument_list|,
name|aggregate2
argument_list|,
name|scan
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
name|AggregateStarTableRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|AggregateRelBase
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|StarTable
operator|.
name|StarTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|apply
argument_list|(
name|call
argument_list|,
literal|null
argument_list|,
name|aggregate
argument_list|,
name|scan
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|apply
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|ProjectRelBase
name|postProject
parameter_list|,
name|AggregateRelBase
name|aggregate
parameter_list|,
name|StarTable
operator|.
name|StarTableScan
name|scan
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|scan
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|table
init|=
name|scan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|RelOptLattice
name|lattice
init|=
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|getLattice
argument_list|(
name|table
argument_list|)
decl_stmt|;
name|OptiqSchema
operator|.
name|TableEntry
name|aggregateTable
init|=
name|lattice
operator|.
name|getAggregate
argument_list|(
name|call
operator|.
name|getPlanner
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregateTable
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|double
name|rowCount
init|=
name|aggregate
operator|.
name|getRows
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|aggregateRelOptTable
init|=
name|RelOptTableImpl
operator|.
name|create
argument_list|(
name|table
operator|.
name|getRelOptSchema
argument_list|()
argument_list|,
name|aggregateTable
operator|.
name|getTable
argument_list|()
operator|.
name|getRowType
argument_list|(
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
argument_list|,
name|aggregateTable
argument_list|,
name|rowCount
argument_list|)
decl_stmt|;
name|RelNode
name|rel
init|=
name|aggregateRelOptTable
operator|.
name|toRel
argument_list|(
name|RelOptUtil
operator|.
name|getContext
argument_list|(
name|cluster
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|postProject
operator|!=
literal|null
condition|)
block|{
name|rel
operator|=
name|postProject
operator|.
name|copy
argument_list|(
name|postProject
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateStarTableRule.java
end_comment

end_unit

