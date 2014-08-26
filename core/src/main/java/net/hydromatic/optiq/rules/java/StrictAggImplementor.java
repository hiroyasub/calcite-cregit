begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
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
name|RelDataType
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
name|RexNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The base implementation of strict aggregate function.  * @see net.hydromatic.optiq.rules.java.RexImpTable.CountImplementor  * @see net.hydromatic.optiq.rules.java.RexImpTable.SumImplementor  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|StrictAggImplementor
implements|implements
name|AggImplementor
block|{
specifier|private
name|boolean
name|needTrackEmptySet
decl_stmt|;
specifier|private
name|boolean
name|trackNullsPerRow
decl_stmt|;
specifier|private
name|int
name|stateSize
decl_stmt|;
specifier|protected
name|boolean
name|nonDefaultOnEmptySet
parameter_list|(
name|AggContext
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|returnRelType
argument_list|()
operator|.
name|isNullable
argument_list|()
return|;
block|}
specifier|protected
specifier|final
name|int
name|getStateSize
parameter_list|()
block|{
return|return
name|stateSize
return|;
block|}
specifier|protected
specifier|final
name|void
name|accAdvance
parameter_list|(
name|AggAddContext
name|add
parameter_list|,
name|Expression
name|acc
parameter_list|,
name|Expression
name|next
parameter_list|)
block|{
name|add
operator|.
name|currentBlock
argument_list|()
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|acc
argument_list|,
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|acc
operator|.
name|type
argument_list|,
name|next
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|List
argument_list|<
name|Type
argument_list|>
name|getStateType
parameter_list|(
name|AggContext
name|info
parameter_list|)
block|{
name|List
argument_list|<
name|Type
argument_list|>
name|subState
init|=
name|getNotNullState
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|stateSize
operator|=
name|subState
operator|.
name|size
argument_list|()
expr_stmt|;
name|needTrackEmptySet
operator|=
name|nonDefaultOnEmptySet
argument_list|(
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|needTrackEmptySet
condition|)
block|{
return|return
name|subState
return|;
block|}
name|boolean
name|hasNullableArgs
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RelDataType
name|type
range|:
name|info
operator|.
name|parameterRelTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|hasNullableArgs
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|trackNullsPerRow
operator|=
operator|!
operator|(
name|info
operator|instanceof
name|WinAggContext
operator|)
operator|||
name|hasNullableArgs
expr_stmt|;
name|List
argument_list|<
name|Type
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|Type
argument_list|>
argument_list|(
name|subState
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|res
operator|.
name|addAll
argument_list|(
name|subState
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// has not nulls
return|return
name|res
return|;
block|}
specifier|public
name|List
argument_list|<
name|Type
argument_list|>
name|getNotNullState
parameter_list|(
name|AggContext
name|info
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|Primitive
operator|.
name|unbox
argument_list|(
name|info
operator|.
name|returnType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|void
name|implementReset
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggResetContext
name|reset
parameter_list|)
block|{
if|if
condition|(
name|trackNullsPerRow
condition|)
block|{
name|List
argument_list|<
name|Expression
argument_list|>
name|acc
init|=
name|reset
operator|.
name|accumulator
argument_list|()
decl_stmt|;
name|Expression
name|flag
init|=
name|acc
operator|.
name|get
argument_list|(
name|acc
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|BlockBuilder
name|block
init|=
name|reset
operator|.
name|currentBlock
argument_list|()
decl_stmt|;
name|block
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|flag
argument_list|,
name|RexImpTable
operator|.
name|getDefaultValue
argument_list|(
name|flag
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|implementNotNullReset
argument_list|(
name|info
argument_list|,
name|reset
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|implementNotNullReset
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggResetContext
name|reset
parameter_list|)
block|{
name|BlockBuilder
name|block
init|=
name|reset
operator|.
name|currentBlock
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|accumulator
init|=
name|reset
operator|.
name|accumulator
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
name|getStateSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|exp
init|=
name|accumulator
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|block
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|exp
argument_list|,
name|RexImpTable
operator|.
name|getDefaultValue
argument_list|(
name|exp
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
name|void
name|implementAdd
parameter_list|(
name|AggContext
name|info
parameter_list|,
specifier|final
name|AggAddContext
name|add
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|args
init|=
name|add
operator|.
name|rexArguments
argument_list|()
decl_stmt|;
name|RexToLixTranslator
name|translator
init|=
name|add
operator|.
name|rowTranslator
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|conditions
init|=
name|translator
operator|.
name|translateList
argument_list|(
name|args
argument_list|,
name|RexImpTable
operator|.
name|NullAs
operator|.
name|IS_NOT_NULL
argument_list|)
decl_stmt|;
name|Expression
name|condition
init|=
name|Expressions
operator|.
name|foldAnd
argument_list|(
name|conditions
argument_list|)
decl_stmt|;
if|if
condition|(
name|Expressions
operator|.
name|constant
argument_list|(
literal|false
argument_list|)
operator|.
name|equals
argument_list|(
name|condition
argument_list|)
condition|)
block|{
return|return;
block|}
name|boolean
name|argsNotNull
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|true
argument_list|)
operator|.
name|equals
argument_list|(
name|condition
argument_list|)
decl_stmt|;
specifier|final
name|BlockBuilder
name|thenBlock
init|=
name|argsNotNull
condition|?
name|add
operator|.
name|currentBlock
argument_list|()
else|:
operator|new
name|BlockBuilder
argument_list|(
literal|true
argument_list|,
name|add
operator|.
name|currentBlock
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|trackNullsPerRow
condition|)
block|{
name|List
argument_list|<
name|Expression
argument_list|>
name|acc
init|=
name|add
operator|.
name|accumulator
argument_list|()
decl_stmt|;
name|thenBlock
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|acc
operator|.
name|get
argument_list|(
name|acc
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|argsNotNull
condition|)
block|{
name|implementNotNullAdd
argument_list|(
name|info
argument_list|,
name|add
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Boolean
argument_list|>
name|nullables
init|=
operator|new
name|HashMap
argument_list|<
name|RexNode
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|arg
range|:
name|args
control|)
block|{
if|if
condition|(
name|translator
operator|.
name|isNullable
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|nullables
operator|.
name|put
argument_list|(
name|arg
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|add
operator|.
name|nestBlock
argument_list|(
name|thenBlock
argument_list|,
name|nullables
argument_list|)
expr_stmt|;
name|implementNotNullAdd
argument_list|(
name|info
argument_list|,
name|add
argument_list|)
expr_stmt|;
name|add
operator|.
name|exitBlock
argument_list|()
expr_stmt|;
name|add
operator|.
name|currentBlock
argument_list|()
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThen
argument_list|(
name|condition
argument_list|,
name|thenBlock
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|void
name|implementNotNullAdd
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggAddContext
name|add
parameter_list|)
function_decl|;
specifier|public
specifier|final
name|Expression
name|implementResult
parameter_list|(
name|AggContext
name|info
parameter_list|,
specifier|final
name|AggResultContext
name|result
parameter_list|)
block|{
if|if
condition|(
operator|!
name|needTrackEmptySet
condition|)
block|{
return|return
name|RexToLixTranslator
operator|.
name|convert
argument_list|(
name|implementNotNullResult
argument_list|(
name|info
argument_list|,
name|result
argument_list|)
argument_list|,
name|info
operator|.
name|returnType
argument_list|()
argument_list|)
return|;
block|}
name|String
name|tmpName
init|=
name|result
operator|.
name|accumulator
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"ar"
else|:
operator|(
name|result
operator|.
name|accumulator
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|"$Res"
operator|)
decl_stmt|;
name|ParameterExpression
name|res
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
literal|0
argument_list|,
name|info
operator|.
name|returnType
argument_list|()
argument_list|,
name|result
operator|.
name|currentBlock
argument_list|()
operator|.
name|newName
argument_list|(
name|tmpName
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|acc
init|=
name|result
operator|.
name|accumulator
argument_list|()
decl_stmt|;
specifier|final
name|BlockBuilder
name|thenBlock
init|=
name|result
operator|.
name|nestBlock
argument_list|()
decl_stmt|;
name|Expression
name|nonNull
init|=
name|RexToLixTranslator
operator|.
name|convert
argument_list|(
name|implementNotNullResult
argument_list|(
name|info
argument_list|,
name|result
argument_list|)
argument_list|,
name|info
operator|.
name|returnType
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|exitBlock
argument_list|()
expr_stmt|;
name|thenBlock
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|res
argument_list|,
name|nonNull
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BlockStatement
name|thenBranch
init|=
name|thenBlock
operator|.
name|toBlock
argument_list|()
decl_stmt|;
name|Expression
name|seenNotNullRows
init|=
name|trackNullsPerRow
condition|?
name|acc
operator|.
name|get
argument_list|(
name|acc
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
else|:
operator|(
operator|(
name|WinAggResultContext
operator|)
name|result
operator|)
operator|.
name|hasRows
argument_list|()
decl_stmt|;
if|if
condition|(
name|thenBranch
operator|.
name|statements
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|Expressions
operator|.
name|condition
argument_list|(
name|seenNotNullRows
argument_list|,
name|nonNull
argument_list|,
name|RexImpTable
operator|.
name|getDefaultValue
argument_list|(
name|res
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
name|result
operator|.
name|currentBlock
argument_list|()
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|res
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|currentBlock
argument_list|()
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThenElse
argument_list|(
name|seenNotNullRows
argument_list|,
name|thenBranch
argument_list|,
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|res
argument_list|,
name|RexImpTable
operator|.
name|getDefaultValue
argument_list|(
name|res
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
specifier|protected
name|Expression
name|implementNotNullResult
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggResultContext
name|result
parameter_list|)
block|{
return|return
name|result
operator|.
name|accumulator
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

