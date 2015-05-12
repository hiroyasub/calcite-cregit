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
name|core
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
name|RelOptCost
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
name|RelOptPlanner
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
name|prepare
operator|.
name|CalcitePrepareImpl
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
name|RelInput
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
name|RelWriter
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
name|SingleRel
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
name|RexCall
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
name|RexChecker
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
name|RexCorrelVariable
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
name|RexLocalRef
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
name|RexProgram
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
name|RexShuttle
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
name|rex
operator|.
name|RexVisitor
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
name|RexVisitorImpl
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
name|Util
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Relational expression that iterates over its input  * and returns elements for which<code>condition</code> evaluates to  *<code>true</code>.  *  *<p>If the condition allows nulls, then a null value is treated the same as  * false.</p>  *  * @see org.apache.calcite.rel.logical.LogicalFilter  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Filter
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RexNode
name|condition
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a filter.    *    * @param cluster   Cluster that this relational expression belongs to    * @param traits    the traits of this rel    * @param child     input relational expression    * @param condition boolean expression which determines whether a row is    *                  allowed to pass    */
specifier|protected
name|Filter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|)
expr_stmt|;
assert|assert
name|condition
operator|!=
literal|null
assert|;
assert|assert
name|RexUtil
operator|.
name|isFlat
argument_list|(
name|condition
argument_list|)
operator|:
name|condition
assert|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
comment|// Too expensive for everyday use:
assert|assert
operator|!
name|CalcitePrepareImpl
operator|.
name|DEBUG
operator|||
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
comment|/**    * Creates a Filter by parsing serialized output.    */
specifier|protected
name|Filter
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|input
operator|.
name|getExpression
argument_list|(
literal|"condition"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|Filter
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexNode
name|condition
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|condition
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RexShuttle
name|shuttle
parameter_list|)
block|{
name|RexNode
name|condition
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|condition
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|condition
operator|==
name|condition
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|getInput
argument_list|()
argument_list|,
name|condition
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
comment|/**    * Check if any of the operands of the filter contains a    * correlation variable    */
specifier|public
name|boolean
name|hasCorrelation
parameter_list|()
block|{
if|if
condition|(
name|condition
operator|instanceof
name|RexCall
condition|)
block|{
try|try
block|{
name|RexVisitor
argument_list|<
name|Void
argument_list|>
name|visitor
init|=
operator|new
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|true
argument_list|)
block|{
specifier|public
name|Void
name|visitCorrelVariable
parameter_list|(
name|RexCorrelVariable
name|var
parameter_list|)
block|{
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|var
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|condition
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
name|RexUtil
operator|.
name|isNullabilityCast
argument_list|(
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|condition
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"Cast for just nullability not allowed"
assert|;
return|return
literal|false
return|;
block|}
specifier|final
name|RexChecker
name|checker
init|=
operator|new
name|RexChecker
argument_list|(
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|fail
argument_list|)
decl_stmt|;
name|condition
operator|.
name|accept
argument_list|(
name|checker
argument_list|)
expr_stmt|;
if|if
condition|(
name|checker
operator|.
name|getFailureCount
argument_list|()
operator|>
literal|0
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|double
name|dRows
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|double
name|dCpu
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|dIo
init|=
literal|0
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
argument_list|)
return|;
block|}
comment|// override RelNode
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|estimateFilteredRows
argument_list|(
name|getInput
argument_list|()
argument_list|,
name|condition
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|double
name|estimateFilteredRows
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
comment|// convert the program's RexLocalRef condition to an expanded RexNode
name|RexLocalRef
name|programCondition
init|=
name|program
operator|.
name|getCondition
argument_list|()
decl_stmt|;
name|RexNode
name|condition
decl_stmt|;
if|if
condition|(
name|programCondition
operator|==
literal|null
condition|)
block|{
name|condition
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|condition
operator|=
name|program
operator|.
name|expandLocalRef
argument_list|(
name|programCondition
argument_list|)
expr_stmt|;
block|}
return|return
name|estimateFilteredRows
argument_list|(
name|child
argument_list|,
name|condition
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|double
name|estimateFilteredRows
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|child
argument_list|)
operator|*
name|RelMetadataQuery
operator|.
name|getSelectivity
argument_list|(
name|child
argument_list|,
name|condition
argument_list|)
return|;
block|}
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"condition"
argument_list|,
name|condition
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Filter.java
end_comment

end_unit

