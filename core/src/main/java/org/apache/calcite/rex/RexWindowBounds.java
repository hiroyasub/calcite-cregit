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
name|sql
operator|.
name|SqlKind
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
name|sql
operator|.
name|SqlNode
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
name|sql
operator|.
name|SqlWindow
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Helpers for {@link RexWindowBound}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RexWindowBounds
block|{
comment|/** UNBOUNDED PRECEDING. */
specifier|public
specifier|static
specifier|final
name|RexWindowBound
name|UNBOUNDED_PRECEDING
init|=
operator|new
name|RexUnboundedWindowBound
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/** UNBOUNDED FOLLOWING. */
specifier|public
specifier|static
specifier|final
name|RexWindowBound
name|UNBOUNDED_FOLLOWING
init|=
operator|new
name|RexUnboundedWindowBound
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/** CURRENT ROW. */
specifier|public
specifier|static
specifier|final
name|RexWindowBound
name|CURRENT_ROW
init|=
operator|new
name|RexCurrentRowWindowBound
argument_list|()
decl_stmt|;
specifier|private
name|RexWindowBounds
parameter_list|()
block|{
block|}
comment|/**    * Creates a window bound from a {@link SqlNode}.    *    * @param node SqlNode of the bound    * @param rexNode offset value when bound is not UNBOUNDED/CURRENT ROW    * @return window bound    */
specifier|public
specifier|static
name|RexWindowBound
name|create
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|RexNode
name|rexNode
parameter_list|)
block|{
if|if
condition|(
name|SqlWindow
operator|.
name|isUnboundedPreceding
argument_list|(
name|node
argument_list|)
condition|)
block|{
return|return
name|UNBOUNDED_PRECEDING
return|;
block|}
if|if
condition|(
name|SqlWindow
operator|.
name|isUnboundedFollowing
argument_list|(
name|node
argument_list|)
condition|)
block|{
return|return
name|UNBOUNDED_FOLLOWING
return|;
block|}
if|if
condition|(
name|SqlWindow
operator|.
name|isCurrentRow
argument_list|(
name|node
argument_list|)
condition|)
block|{
return|return
name|CURRENT_ROW
return|;
block|}
return|return
operator|new
name|RexBoundedWindowBound
argument_list|(
operator|(
name|RexCall
operator|)
name|rexNode
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RexWindowBound
name|following
parameter_list|(
name|RexNode
name|offset
parameter_list|)
block|{
return|return
operator|new
name|RexBoundedWindowBound
argument_list|(
operator|new
name|RexCall
argument_list|(
name|offset
operator|.
name|getType
argument_list|()
argument_list|,
name|SqlWindow
operator|.
name|FOLLOWING_OPERATOR
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|offset
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RexWindowBound
name|preceding
parameter_list|(
name|RexNode
name|offset
parameter_list|)
block|{
return|return
operator|new
name|RexBoundedWindowBound
argument_list|(
operator|new
name|RexCall
argument_list|(
name|offset
operator|.
name|getType
argument_list|()
argument_list|,
name|SqlWindow
operator|.
name|PRECEDING_OPERATOR
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|offset
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Implements UNBOUNDED PRECEDING/FOLLOWING bound.    */
specifier|private
specifier|static
class|class
name|RexUnboundedWindowBound
extends|extends
name|RexWindowBound
block|{
specifier|private
specifier|final
name|boolean
name|preceding
decl_stmt|;
name|RexUnboundedWindowBound
parameter_list|(
name|boolean
name|preceding
parameter_list|)
block|{
name|this
operator|.
name|preceding
operator|=
name|preceding
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUnbounded
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPreceding
parameter_list|()
block|{
return|return
name|preceding
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFollowing
parameter_list|()
block|{
return|return
operator|!
name|preceding
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|preceding
condition|?
literal|"UNBOUNDED PRECEDING"
else|:
literal|"UNBOUNDED FOLLOWING"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOrderKey
parameter_list|()
block|{
return|return
name|preceding
condition|?
literal|0
else|:
literal|2
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|==
name|o
operator|||
name|o
operator|instanceof
name|RexUnboundedWindowBound
operator|&&
name|preceding
operator|==
operator|(
operator|(
name|RexUnboundedWindowBound
operator|)
name|o
operator|)
operator|.
name|preceding
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|preceding
condition|?
literal|1357
else|:
literal|1358
return|;
block|}
block|}
comment|/**    * Implements CURRENT ROW bound.    */
specifier|private
specifier|static
class|class
name|RexCurrentRowWindowBound
extends|extends
name|RexWindowBound
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentRow
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CURRENT ROW"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOrderKey
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|==
name|o
operator|||
name|o
operator|instanceof
name|RexCurrentRowWindowBound
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|123
return|;
block|}
block|}
comment|/**    * Implements XX PRECEDING/FOLLOWING bound where XX is not UNBOUNDED.    */
specifier|private
specifier|static
class|class
name|RexBoundedWindowBound
extends|extends
name|RexWindowBound
block|{
specifier|private
specifier|final
name|SqlKind
name|sqlKind
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|offset
decl_stmt|;
name|RexBoundedWindowBound
parameter_list|(
name|RexCall
name|node
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|node
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|sqlKind
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|node
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RexBoundedWindowBound
parameter_list|(
name|SqlKind
name|sqlKind
parameter_list|,
name|RexNode
name|offset
parameter_list|)
block|{
name|this
operator|.
name|sqlKind
operator|=
name|sqlKind
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPreceding
parameter_list|()
block|{
return|return
name|sqlKind
operator|==
name|SqlKind
operator|.
name|PRECEDING
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFollowing
parameter_list|()
block|{
return|return
name|sqlKind
operator|==
name|SqlKind
operator|.
name|FOLLOWING
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nodeCount
parameter_list|()
block|{
return|return
name|super
operator|.
name|nodeCount
argument_list|()
operator|+
name|offset
operator|.
name|nodeCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
parameter_list|>
name|RexWindowBound
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
name|R
name|r
init|=
name|offset
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|instanceof
name|RexNode
operator|&&
name|r
operator|!=
name|offset
condition|)
block|{
return|return
operator|new
name|RexBoundedWindowBound
argument_list|(
name|sqlKind
argument_list|,
operator|(
name|RexNode
operator|)
name|r
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|offset
operator|+
literal|" "
operator|+
name|sqlKind
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|==
name|o
operator|||
name|o
operator|instanceof
name|RexBoundedWindowBound
operator|&&
name|offset
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|RexBoundedWindowBound
operator|)
name|o
operator|)
operator|.
name|offset
argument_list|)
operator|&&
name|sqlKind
operator|==
operator|(
operator|(
name|RexBoundedWindowBound
operator|)
name|o
operator|)
operator|.
name|sqlKind
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|sqlKind
argument_list|,
name|offset
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
