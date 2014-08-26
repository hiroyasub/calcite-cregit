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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
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
name|BitSet
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
name|RexBuilder
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
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|ImmutableIntList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|mapping
operator|.
name|IntPair
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
name|util
operator|.
name|BitSets
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/** An analyzed join condition.  *  *<p>It is useful for the many algorithms that care whether a join is an  * equi-join.  *  *<p>You can create one using {@link #of}, or call {@link JoinRelBase#analyzeCondition()};  * many kinds of join cache their join info, especially those that are  * equi-joins and sub-class {@link org.eigenbase.rel.rules.EquiJoinRel}.</p>  *  * @see JoinRelBase#analyzeCondition() */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JoinInfo
block|{
specifier|public
specifier|final
name|ImmutableIntList
name|leftKeys
decl_stmt|;
specifier|public
specifier|final
name|ImmutableIntList
name|rightKeys
decl_stmt|;
comment|/** Creates a JoinInfo. */
specifier|protected
name|JoinInfo
parameter_list|(
name|ImmutableIntList
name|leftKeys
parameter_list|,
name|ImmutableIntList
name|rightKeys
parameter_list|)
block|{
name|this
operator|.
name|leftKeys
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|leftKeys
argument_list|)
expr_stmt|;
name|this
operator|.
name|rightKeys
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|rightKeys
argument_list|)
expr_stmt|;
assert|assert
name|leftKeys
operator|.
name|size
argument_list|()
operator|==
name|rightKeys
operator|.
name|size
argument_list|()
assert|;
block|}
comment|/** Creates a {@code JoinInfo} by analyzing a condition. */
specifier|public
specifier|static
name|JoinInfo
name|of
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
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
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|RexNode
name|remaining
init|=
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
decl_stmt|;
if|if
condition|(
name|remaining
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
return|return
operator|new
name|EquiJoinInfo
argument_list|(
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|leftKeys
argument_list|)
argument_list|,
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|rightKeys
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NonEquiJoinInfo
argument_list|(
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|leftKeys
argument_list|)
argument_list|,
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|rightKeys
argument_list|)
argument_list|,
name|remaining
argument_list|)
return|;
block|}
block|}
comment|/** Creates an equi-join. */
specifier|public
specifier|static
name|JoinInfo
name|of
parameter_list|(
name|ImmutableIntList
name|leftKeys
parameter_list|,
name|ImmutableIntList
name|rightKeys
parameter_list|)
block|{
return|return
operator|new
name|EquiJoinInfo
argument_list|(
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
return|;
block|}
comment|/** Returns whether this is an equi-join. */
specifier|public
specifier|abstract
name|boolean
name|isEqui
parameter_list|()
function_decl|;
comment|/** Returns a list of (left, right) key ordinals. */
specifier|public
name|List
argument_list|<
name|IntPair
argument_list|>
name|pairs
parameter_list|()
block|{
return|return
name|IntPair
operator|.
name|zip
argument_list|(
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
return|;
block|}
specifier|public
name|BitSet
name|leftSet
parameter_list|()
block|{
return|return
name|BitSets
operator|.
name|of
argument_list|(
name|leftKeys
argument_list|)
return|;
block|}
specifier|public
name|BitSet
name|rightSet
parameter_list|()
block|{
return|return
name|BitSets
operator|.
name|of
argument_list|(
name|rightKeys
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|RexNode
name|getRemaining
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
function_decl|;
specifier|public
name|RexNode
name|getEquiCondition
parameter_list|(
specifier|final
name|RelNode
name|left
parameter_list|,
specifier|final
name|RelNode
name|right
parameter_list|,
specifier|final
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|leftTypes
init|=
name|RelOptUtil
operator|.
name|getFieldTypeList
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|rightTypes
init|=
name|RelOptUtil
operator|.
name|getFieldTypeList
argument_list|(
name|right
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
operator|new
name|AbstractList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|int
name|leftKey
init|=
name|leftKeys
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|rightKey
init|=
name|rightKeys
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|leftTypes
operator|.
name|get
argument_list|(
name|leftKey
argument_list|)
argument_list|,
name|leftKey
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|rightTypes
operator|.
name|get
argument_list|(
name|rightKey
argument_list|)
argument_list|,
name|leftTypes
operator|.
name|size
argument_list|()
operator|+
name|rightKey
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|leftKeys
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** JoinInfo that represents an equi-join. */
specifier|private
specifier|static
class|class
name|EquiJoinInfo
extends|extends
name|JoinInfo
block|{
specifier|protected
name|EquiJoinInfo
parameter_list|(
name|ImmutableIntList
name|leftKeys
parameter_list|,
name|ImmutableIntList
name|rightKeys
parameter_list|)
block|{
name|super
argument_list|(
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEqui
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|getRemaining
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
comment|/** JoinInfo that represents a non equi-join. */
specifier|private
specifier|static
class|class
name|NonEquiJoinInfo
extends|extends
name|JoinInfo
block|{
specifier|public
specifier|final
name|RexNode
name|remaining
decl_stmt|;
specifier|protected
name|NonEquiJoinInfo
parameter_list|(
name|ImmutableIntList
name|leftKeys
parameter_list|,
name|ImmutableIntList
name|rightKeys
parameter_list|,
name|RexNode
name|remaining
parameter_list|)
block|{
name|super
argument_list|(
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|remaining
operator|.
name|isAlwaysTrue
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEqui
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|getRemaining
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
return|return
name|remaining
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JoinInfo.java
end_comment

end_unit

