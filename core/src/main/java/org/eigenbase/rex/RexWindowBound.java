begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|sql
operator|.
name|SqlLiteral
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
name|SqlNode
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
name|SqlWindow
import|;
end_import

begin_comment
comment|/**  * Abstracts "XX PRECEDING/FOLLOWING" and "CURRENT ROW" bounds for windowed  * aggregates.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RexWindowBound
block|{
comment|/**    * Creates window bound.    * @param node SqlNode of the bound    * @param rexNode offset value when bound is not UNBOUNDED/CURRENT ROW    * @return window bound    */
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
operator|||
name|SqlWindow
operator|.
name|isUnboundedFollowing
argument_list|(
name|node
argument_list|)
condition|)
block|{
return|return
operator|new
name|RexWindowBoundUnbounded
argument_list|(
name|node
argument_list|)
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
operator|new
name|RexWindowBoundCurrentRow
argument_list|()
return|;
block|}
return|return
operator|new
name|RexWindowBoundBounded
argument_list|(
name|rexNode
argument_list|)
return|;
block|}
comment|/**    * Returns if the bound is unbounded.    * @return if the bound is unbounded    */
specifier|public
name|boolean
name|isUnbounded
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns if the bound is PRECEDING.    * @return if the bound is PRECEDING    */
specifier|public
name|boolean
name|isPreceding
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns if the bound is FOLLOWING.    * @return if the bound is FOLLOWING    */
specifier|public
name|boolean
name|isFollowing
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns if the bound is CURRENT ROW.    * @return if the bound is CURRENT ROW    */
specifier|public
name|boolean
name|isCurrentRow
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns offset from XX PRECEDING/FOLLOWING.    *    * @return offset from XX PRECEDING/FOLLOWING    */
specifier|public
name|RexNode
name|getOffset
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Returns relative sort offset when known at compile time.    * For instance, UNBOUNDED PRECEDING is less than CURRENT ROW.    *    * @return relative order or -1 when order is not known    */
specifier|public
name|int
name|getOrderKey
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Transforms the bound via {@link org.eigenbase.rex.RexVisitor}.    * @param visitor visitor to accept    * @param<R> return type of the visitor    * @return transformed bound    */
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
return|return
name|this
return|;
block|}
comment|/**    * Implements UNBOUNDED PRECEDING/FOLLOWING bound.    */
specifier|private
specifier|static
class|class
name|RexWindowBoundUnbounded
extends|extends
name|RexWindowBound
block|{
specifier|private
specifier|final
name|SqlNode
name|node
decl_stmt|;
specifier|public
name|RexWindowBoundUnbounded
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
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
name|SqlWindow
operator|.
name|isUnboundedPreceding
argument_list|(
name|node
argument_list|)
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
name|SqlWindow
operator|.
name|isUnboundedFollowing
argument_list|(
name|node
argument_list|)
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
operator|(
operator|(
name|SqlLiteral
operator|)
name|node
operator|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
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
name|isPreceding
argument_list|()
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
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RexWindowBoundUnbounded
name|that
init|=
operator|(
name|RexWindowBoundUnbounded
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|equals
argument_list|(
name|that
operator|.
name|node
argument_list|)
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|node
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/**    * Implements CURRENT ROW bound.    */
specifier|private
specifier|static
class|class
name|RexWindowBoundCurrentRow
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
name|obj
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|==
name|obj
operator|.
name|getClass
argument_list|()
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
name|RexWindowBoundBounded
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
specifier|public
name|RexWindowBoundBounded
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
assert|assert
name|node
operator|instanceof
name|RexCall
operator|:
literal|"RexWindowBoundBounded window bound should be either 'X preceding'"
operator|+
literal|" or 'X following' call. Actual type is "
operator|+
name|node
assert|;
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
name|this
operator|.
name|offset
operator|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|sqlKind
operator|=
name|call
operator|.
name|getKind
argument_list|()
expr_stmt|;
assert|assert
name|this
operator|.
name|offset
operator|!=
literal|null
operator|:
literal|"RexWindowBoundBounded offset should not be"
operator|+
literal|" null"
assert|;
block|}
specifier|private
name|RexWindowBoundBounded
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
name|RexWindowBoundBounded
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
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|sqlKind
operator|.
name|toString
argument_list|()
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
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RexWindowBoundBounded
name|that
init|=
operator|(
name|RexWindowBoundBounded
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|offset
operator|.
name|equals
argument_list|(
name|that
operator|.
name|offset
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|sqlKind
operator|!=
name|that
operator|.
name|sqlKind
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|sqlKind
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|offset
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexWindowBound.java
end_comment

end_unit

