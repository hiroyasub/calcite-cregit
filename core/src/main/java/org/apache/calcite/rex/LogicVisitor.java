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
name|rex
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
name|RelOptUtil
operator|.
name|Logic
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
name|Iterables
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Visitor pattern for traversing a tree of {@link RexNode} objects.  */
end_comment

begin_class
specifier|public
class|class
name|LogicVisitor
implements|implements
name|RexBiVisitor
argument_list|<
name|Logic
argument_list|,
name|Logic
argument_list|>
block|{
specifier|private
specifier|final
name|RexNode
name|seek
decl_stmt|;
specifier|private
specifier|final
name|Collection
argument_list|<
name|Logic
argument_list|>
name|logicCollection
decl_stmt|;
comment|/** Creates a LogicVisitor. */
specifier|private
name|LogicVisitor
parameter_list|(
name|RexNode
name|seek
parameter_list|,
name|Collection
argument_list|<
name|Logic
argument_list|>
name|logicCollection
parameter_list|)
block|{
name|this
operator|.
name|seek
operator|=
name|seek
expr_stmt|;
name|this
operator|.
name|logicCollection
operator|=
name|logicCollection
expr_stmt|;
block|}
comment|/** Finds a suitable logic for evaluating {@code seek} within a list of    * expressions.    *    *<p>Chooses a logic that is safe (that is, gives the right    * answer) with the fewest possibilities (that is, we prefer one that    * returns [true as true, false as false, unknown as false] over one that    * distinguishes false from unknown).    */
specifier|public
specifier|static
name|Logic
name|find
parameter_list|(
name|Logic
name|logic
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|,
name|RexNode
name|seek
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|Logic
argument_list|>
name|set
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Logic
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|LogicVisitor
name|visitor
init|=
operator|new
name|LogicVisitor
argument_list|(
name|seek
argument_list|,
name|set
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|accept
argument_list|(
name|visitor
argument_list|,
name|logic
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|set
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"not found: "
operator|+
name|seek
argument_list|)
throw|;
case|case
literal|1
case|:
return|return
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|set
argument_list|)
return|;
default|default:
return|return
name|Logic
operator|.
name|TRUE_FALSE_UNKNOWN
return|;
block|}
block|}
specifier|public
specifier|static
name|void
name|collect
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|RexNode
name|seek
parameter_list|,
name|Logic
name|logic
parameter_list|,
name|Collection
argument_list|<
name|Logic
argument_list|>
name|logicCollection
parameter_list|)
block|{
name|node
operator|.
name|accept
argument_list|(
operator|new
name|LogicVisitor
argument_list|(
name|seek
argument_list|,
name|logicCollection
argument_list|)
argument_list|,
name|logic
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Logic
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|Logic
name|logic
parameter_list|)
block|{
specifier|final
name|Logic
name|arg0
init|=
name|logic
decl_stmt|;
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|IS_NOT_NULL
case|:
case|case
name|IS_NULL
case|:
name|logic
operator|=
name|Logic
operator|.
name|TRUE_FALSE_UNKNOWN
expr_stmt|;
break|break;
case|case
name|IS_TRUE
case|:
case|case
name|IS_NOT_TRUE
case|:
name|logic
operator|=
name|Logic
operator|.
name|UNKNOWN_AS_FALSE
expr_stmt|;
break|break;
case|case
name|IS_FALSE
case|:
case|case
name|IS_NOT_FALSE
case|:
name|logic
operator|=
name|Logic
operator|.
name|UNKNOWN_AS_TRUE
expr_stmt|;
break|break;
case|case
name|NOT
case|:
name|logic
operator|=
name|logic
operator|.
name|negate
argument_list|()
expr_stmt|;
break|break;
case|case
name|CASE
case|:
name|logic
operator|=
name|Logic
operator|.
name|TRUE_FALSE_UNKNOWN
expr_stmt|;
break|break;
block|}
switch|switch
condition|(
name|logic
condition|)
block|{
case|case
name|TRUE
case|:
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|AND
case|:
break|break;
default|default:
name|logic
operator|=
name|Logic
operator|.
name|TRUE_FALSE
expr_stmt|;
block|}
block|}
for|for
control|(
name|RexNode
name|operand
range|:
name|call
operator|.
name|operands
control|)
block|{
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|logic
argument_list|)
expr_stmt|;
block|}
return|return
name|end
argument_list|(
name|call
argument_list|,
name|arg0
argument_list|)
return|;
block|}
specifier|private
name|Logic
name|end
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
if|if
condition|(
name|RexUtil
operator|.
name|eq
argument_list|(
name|node
argument_list|,
name|seek
argument_list|)
condition|)
block|{
name|logicCollection
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
return|return
name|arg
return|;
block|}
specifier|public
name|Logic
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|inputRef
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|localRef
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|literal
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|over
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitCorrelVariable
parameter_list|(
name|RexCorrelVariable
name|correlVariable
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|correlVariable
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitDynamicParam
parameter_list|(
name|RexDynamicParam
name|dynamicParam
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|dynamicParam
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitRangeRef
parameter_list|(
name|RexRangeRef
name|rangeRef
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|rangeRef
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|fieldAccess
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Logic
name|visitSubQuery
parameter_list|(
name|RexSubQuery
name|subQuery
parameter_list|,
name|Logic
name|arg
parameter_list|)
block|{
if|if
condition|(
operator|!
name|subQuery
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|)
block|{
if|if
condition|(
name|arg
operator|==
name|Logic
operator|.
name|TRUE_FALSE_UNKNOWN
condition|)
block|{
name|arg
operator|=
name|Logic
operator|.
name|TRUE_FALSE
expr_stmt|;
block|}
block|}
return|return
name|end
argument_list|(
name|subQuery
argument_list|,
name|arg
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LogicVisitor.java
end_comment

end_unit

