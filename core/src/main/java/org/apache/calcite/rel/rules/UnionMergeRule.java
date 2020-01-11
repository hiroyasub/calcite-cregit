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
name|Intersect
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
name|Minus
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
name|RelFactories
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
name|SetOp
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
name|Union
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
name|tools
operator|.
name|RelBuilder
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
name|Util
import|;
end_import

begin_comment
comment|/**  * UnionMergeRule implements the rule for combining two  * non-distinct {@link org.apache.calcite.rel.core.SetOp}s  * into a single {@link org.apache.calcite.rel.core.SetOp}.  *  *<p>Originally written for {@link Union} (hence the name),  * but now also applies to {@link Intersect}.  */
end_comment

begin_class
specifier|public
class|class
name|UnionMergeRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|UnionMergeRule
name|INSTANCE
init|=
operator|new
name|UnionMergeRule
argument_list|(
name|LogicalUnion
operator|.
name|class
argument_list|,
literal|"UnionMergeRule"
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|UnionMergeRule
name|INTERSECT_INSTANCE
init|=
operator|new
name|UnionMergeRule
argument_list|(
name|LogicalIntersect
operator|.
name|class
argument_list|,
literal|"IntersectMergeRule"
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|UnionMergeRule
name|MINUS_INSTANCE
init|=
operator|new
name|UnionMergeRule
argument_list|(
name|LogicalMinus
operator|.
name|class
argument_list|,
literal|"MinusMergeRule"
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates a UnionMergeRule. */
specifier|public
name|UnionMergeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SetOp
argument_list|>
name|unionClazz
parameter_list|,
name|String
name|description
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|unionClazz
argument_list|,
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|UnionMergeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Union
argument_list|>
name|unionClazz
parameter_list|,
name|RelFactories
operator|.
name|SetOpFactory
name|setOpFactory
parameter_list|)
block|{
name|this
argument_list|(
name|unionClazz
argument_list|,
literal|null
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|setOpFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
comment|// It avoids adding the rule match to the match queue in case the rule is known to be a no-op
specifier|final
name|SetOp
name|topOp
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|SetOp
argument_list|>
name|setOpClass
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|SetOp
argument_list|>
operator|)
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMatchedClass
argument_list|()
decl_stmt|;
specifier|final
name|SetOp
name|bottomOp
decl_stmt|;
if|if
condition|(
name|setOpClass
operator|.
name|isInstance
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|&&
operator|!
name|Minus
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|setOpClass
argument_list|)
condition|)
block|{
name|bottomOp
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|setOpClass
operator|.
name|isInstance
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|bottomOp
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|topOp
operator|.
name|all
operator|&&
operator|!
name|bottomOp
operator|.
name|all
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|SetOp
name|topOp
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|SetOp
argument_list|>
name|setOpClass
init|=
operator|(
name|Class
operator|)
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMatchedClass
argument_list|()
decl_stmt|;
comment|// For Union and Intersect, we want to combine the set-op that's in the
comment|// second input first.
comment|//
comment|// For example, we reduce
comment|//    Union(Union(a, b), Union(c, d))
comment|// to
comment|//    Union(Union(a, b), c, d)
comment|// in preference to
comment|//    Union(a, b, Union(c, d))
comment|//
comment|// But for Minus, we can only reduce the left input. It is not valid to
comment|// reduce
comment|//    Minus(a, Minus(b, c))
comment|// to
comment|//    Minus(a, b, c)
comment|//
comment|// Hence, that's why the rule pattern matches on generic RelNodes rather
comment|// than explicit sub-classes of SetOp.  By doing so, and firing this rule
comment|// in a bottom-up order, it allows us to only specify a single
comment|// pattern for this rule.
specifier|final
name|SetOp
name|bottomOp
decl_stmt|;
if|if
condition|(
name|setOpClass
operator|.
name|isInstance
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|&&
operator|!
name|Minus
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|setOpClass
argument_list|)
condition|)
block|{
name|bottomOp
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|setOpClass
operator|.
name|isInstance
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|bottomOp
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
comment|// Can only combine (1) if all operators are ALL,
comment|// or (2) top operator is DISTINCT (i.e. not ALL).
comment|// In case (2), all operators become DISTINCT.
if|if
condition|(
name|topOp
operator|.
name|all
operator|&&
operator|!
name|bottomOp
operator|.
name|all
condition|)
block|{
return|return;
block|}
comment|// Combine the inputs from the bottom set-op with the other inputs from
comment|// the top set-op.
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|setOpClass
operator|.
name|isInstance
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|&&
operator|!
name|Minus
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|setOpClass
argument_list|)
condition|)
block|{
name|relBuilder
operator|.
name|push
argument_list|(
name|topOp
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|pushAll
argument_list|(
name|bottomOp
operator|.
name|getInputs
argument_list|()
argument_list|)
expr_stmt|;
comment|// topOp.getInputs().size() may be more than 2
for|for
control|(
name|int
name|index
init|=
literal|2
init|;
name|index
operator|<
name|topOp
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|relBuilder
operator|.
name|push
argument_list|(
name|topOp
operator|.
name|getInput
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|relBuilder
operator|.
name|pushAll
argument_list|(
name|bottomOp
operator|.
name|getInputs
argument_list|()
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|pushAll
argument_list|(
name|Util
operator|.
name|skip
argument_list|(
name|topOp
operator|.
name|getInputs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|n
init|=
name|bottomOp
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|topOp
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|topOp
operator|instanceof
name|Union
condition|)
block|{
name|relBuilder
operator|.
name|union
argument_list|(
name|topOp
operator|.
name|all
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|topOp
operator|instanceof
name|Intersect
condition|)
block|{
name|relBuilder
operator|.
name|intersect
argument_list|(
name|topOp
operator|.
name|all
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|topOp
operator|instanceof
name|Minus
condition|)
block|{
name|relBuilder
operator|.
name|minus
argument_list|(
name|topOp
operator|.
name|all
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

