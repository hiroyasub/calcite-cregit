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
operator|.
name|rules
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
name|RelOptRuleCall
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
name|RelRule
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
name|Aggregate
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
name|core
operator|.
name|Project
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
name|type
operator|.
name|RelDataTypeFactory
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|tools
operator|.
name|RelBuilder
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
name|immutables
operator|.
name|value
operator|.
name|Value
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
comment|/**  * Planner rule that recognizes  a {@link org.apache.calcite.rel.core.Aggregate}  * on top of a {@link org.apache.calcite.rel.core.Project} where the aggregate's group set  * contains literals (true, false, DATE, chars, etc), and removes the literals from the  * group keys by joining with a dummy table of literals.  *  *<pre>{@code  * select avg(sal)  * from emp  * group by true, DATE '2022-01-01';  * }</pre>  * becomes  *<pre>{@code  * select avg(sal)  * from emp, (select true x, DATE '2022-01-01' d) dummy  * group by dummy.x, dummy.d;  * }</pre>  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
specifier|final
class|class
name|AggregateProjectConstantToDummyJoinRule
extends|extends
name|RelRule
argument_list|<
name|AggregateProjectConstantToDummyJoinRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates an AggregateProjectConstantToDummyJoinRule. */
specifier|private
name|AggregateProjectConstantToDummyJoinRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Aggregate
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
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|groupKey
range|:
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|asList
argument_list|()
control|)
block|{
if|if
condition|(
name|groupKey
operator|>=
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|RexNode
name|groupKeyProject
init|=
name|project
operator|.
name|getProjects
argument_list|()
operator|.
name|get
argument_list|(
name|groupKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupKeyProject
operator|instanceof
name|RexLiteral
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
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
name|Aggregate
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
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|RexBuilder
name|rexBuilder
init|=
name|builder
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|project
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|RelDataTypeFactory
operator|.
name|Builder
name|valuesType
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|literals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
init|=
name|project
operator|.
name|getProjects
argument_list|()
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
name|projects
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|node
init|=
name|projects
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|RexLiteral
condition|)
block|{
name|literals
operator|.
name|add
argument_list|(
operator|(
name|RexLiteral
operator|)
name|node
argument_list|)
expr_stmt|;
name|valuesType
operator|.
name|add
argument_list|(
name|project
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|values
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|literals
argument_list|)
argument_list|,
name|valuesType
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|newProjects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|literalCounter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RexNode
name|exp
range|:
name|project
operator|.
name|getProjects
argument_list|()
control|)
block|{
if|if
condition|(
name|exp
operator|instanceof
name|RexLiteral
condition|)
block|{
name|newProjects
operator|.
name|add
argument_list|(
name|builder
operator|.
name|field
argument_list|(
name|offset
operator|+
name|literalCounter
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newProjects
operator|.
name|add
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|project
argument_list|(
name|newProjects
argument_list|)
expr_stmt|;
name|builder
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getGroupSets
argument_list|()
argument_list|)
argument_list|,
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|ImmutableAggregateProjectConstantToDummyJoinRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandFor
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
name|Project
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|AggregateProjectConstantToDummyJoinRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|AggregateProjectConstantToDummyJoinRule
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|default
name|Config
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|aggregateClass
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|projectClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

