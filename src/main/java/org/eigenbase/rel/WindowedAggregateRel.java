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
name|rel
package|;
end_package

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
name|eigenbase
operator|.
name|reltype
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
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A relational expression representing a set of window aggregates.  *  *<p>Rules:  *  *<ul>  *<li>Created by {@link org.eigenbase.rel.rules.WindowedAggSplitterRule}.  *<li>Triggers {@link net.sf.farrago.fennel.rel.FennelWindowRule}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|WindowedAggregateRel
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|RexProgram
name|program
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a WindowedAggregateRel.      *      * @param cluster Cluster      * @param traitSet Trait set      * @param child Input relational expression      * @param program Program containing an array of expressions. The program      * must not have a condition, and each expression must be either a {@link      * RexLocalRef}, or a {@link RexOver} whose arguments are all {@link      * RexLocalRef}.      * @param rowType Row type      */
specifier|public
name|WindowedAggregateRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|program
operator|=
name|program
expr_stmt|;
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
operator|!
name|program
operator|.
name|isValid
argument_list|(
name|fail
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|program
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"Agg program must not have condition"
assert|;
return|return
literal|false
return|;
block|}
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|RexNode
name|agg
range|:
name|program
operator|.
name|getExprList
argument_list|()
control|)
block|{
operator|++
name|i
expr_stmt|;
if|if
condition|(
name|agg
operator|instanceof
name|RexOver
condition|)
block|{
name|RexOver
name|over
init|=
operator|(
name|RexOver
operator|)
name|agg
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|over
operator|.
name|operands
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|RexNode
name|operand
init|=
name|over
operator|.
name|operands
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|operand
operator|instanceof
name|RexLocalRef
operator|)
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"aggs["
operator|+
name|i
operator|+
literal|"].operand["
operator|+
name|j
operator|+
literal|"] is not a RexLocalRef"
assert|;
return|return
literal|false
return|;
block|}
block|}
block|}
if|else if
condition|(
name|agg
operator|instanceof
name|RexInputRef
condition|)
block|{
empty_stmt|;
block|}
else|else
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"aggs["
operator|+
name|i
operator|+
literal|"] is a "
operator|+
name|agg
operator|.
name|getClass
argument_list|()
operator|+
literal|", expecting RexInputRef or RexOver"
assert|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|RexProgram
name|getProgram
parameter_list|()
block|{
return|return
name|program
return|;
block|}
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
return|return
name|program
operator|.
name|explainCalc
argument_list|(
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
argument_list|)
return|;
block|}
specifier|public
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
operator|new
name|WindowedAggregateRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|program
argument_list|,
name|rowType
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End WindowedAggregateRel.java
end_comment

end_unit

