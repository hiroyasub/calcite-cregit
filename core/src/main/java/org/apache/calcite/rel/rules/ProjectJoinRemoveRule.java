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
name|RelOptRule
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
name|RelOptUtil
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
name|core
operator|.
name|Join
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|rex
operator|.
name|RexUtil
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
name|RelBuilderFactory
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
name|util
operator|.
name|ImmutableBitSet
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Planner rule that matches an {@link Project}  * on a {@link Join} and removes the join provided that the join is a left join  * or right join and the join keys are unique.  *  *<p>For instance,</p>  *  *<blockquote>  *<pre>select s.product_id from  * sales as s  * left join product as p  * on s.product_id = p.product_id</pre></blockquote>  *  *<p>becomes  *  *<blockquote>  *<pre>select s.product_id from sales as s</pre></blockquote>  *  */
end_comment

begin_class
specifier|public
class|class
name|ProjectJoinRemoveRule
extends|extends
name|RelOptRule
implements|implements
name|SubstitutionRule
block|{
comment|/** @deprecated Use {@link CoreRules#PROJECT_JOIN_REMOVE}. */
annotation|@
name|Deprecated
comment|// to be removed before 1.25
specifier|public
specifier|static
specifier|final
name|ProjectJoinRemoveRule
name|INSTANCE
init|=
name|CoreRules
operator|.
name|PROJECT_JOIN_REMOVE
decl_stmt|;
comment|/** Creates a ProjectJoinRemoveRule. */
specifier|public
name|ProjectJoinRemoveRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|projectClass
argument_list|,
name|operandJ
argument_list|(
name|joinClass
argument_list|,
literal|null
argument_list|,
name|join
lambda|->
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|LEFT
operator|||
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|RIGHT
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
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
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isLeftJoin
init|=
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|LEFT
decl_stmt|;
name|int
name|lower
init|=
name|isLeftJoin
condition|?
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|-
literal|1
else|:
literal|0
decl_stmt|;
name|int
name|upper
init|=
name|isLeftJoin
condition|?
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
else|:
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
comment|// Check whether the project uses columns whose index is between
comment|// lower(included) and upper(excluded).
for|for
control|(
name|RexNode
name|expr
range|:
name|project
operator|.
name|getProjects
argument_list|()
control|)
block|{
if|if
condition|(
name|RelOptUtil
operator|.
name|InputFinder
operator|.
name|bits
argument_list|(
name|expr
argument_list|)
operator|.
name|asList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|i
lambda|->
name|i
operator|>=
name|lower
operator|&&
name|i
operator|<
name|upper
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|rightKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|leftKeys
argument_list|,
name|rightKeys
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|joinKeys
init|=
name|isLeftJoin
condition|?
name|rightKeys
else|:
name|leftKeys
decl_stmt|;
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|columns
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|joinKeys
operator|.
name|forEach
argument_list|(
name|key
lambda|->
name|columns
operator|.
name|set
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|isLeftJoin
condition|?
name|join
operator|.
name|getRight
argument_list|()
else|:
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|columns
operator|.
name|build
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|RelNode
name|node
decl_stmt|;
if|if
condition|(
name|isLeftJoin
condition|)
block|{
name|node
operator|=
name|project
operator|.
name|copy
argument_list|(
name|project
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
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
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|offset
init|=
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|newExprs
init|=
name|project
operator|.
name|getProjects
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|expr
lambda|->
name|RexUtil
operator|.
name|shift
argument_list|(
name|expr
argument_list|,
operator|-
name|offset
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|=
name|project
operator|.
name|copy
argument_list|(
name|project
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|newExprs
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

