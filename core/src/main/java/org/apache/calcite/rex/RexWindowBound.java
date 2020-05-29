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
name|SqlNode
import|;
end_import

begin_comment
comment|/**  * Abstracts "XX PRECEDING/FOLLOWING" and "CURRENT ROW" bounds for windowed  * aggregates.  *  * @see RexWindowBounds  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RexWindowBound
block|{
comment|/** Use {@link RexWindowBounds#create(SqlNode, RexNode)}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
return|return
name|RexWindowBounds
operator|.
name|create
argument_list|(
name|node
argument_list|,
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
comment|/**    * Transforms the bound via {@link org.apache.calcite.rex.RexVisitor}.    *    * @param visitor visitor to accept    * @param<R> return type of the visitor    * @return transformed bound    */
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
comment|/**    * Transforms the bound via {@link org.apache.calcite.rex.RexBiVisitor}.    *    * @param visitor visitor to accept    * @param arg Payload    * @param<R> return type of the visitor    * @return transformed bound    */
specifier|public
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|RexWindowBound
name|accept
parameter_list|(
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
name|visitor
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**    * Returns the number of nodes in this bound.    *    * @see RexNode#nodeCount()    */
specifier|public
name|int
name|nodeCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

