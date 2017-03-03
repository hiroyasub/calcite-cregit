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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Enumeration of join types.  */
end_comment

begin_enum
specifier|public
enum|enum
name|JoinRelType
block|{
name|INNER
block|,
name|LEFT
block|,
name|RIGHT
block|,
name|FULL
block|;
comment|/** Lower-case name. */
specifier|public
specifier|final
name|String
name|lowerName
init|=
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|/**    * Returns whether a join of this type may generate NULL values on the    * right-hand side.    */
specifier|public
name|boolean
name|generatesNullsOnRight
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|LEFT
operator|)
operator|||
operator|(
name|this
operator|==
name|FULL
operator|)
return|;
block|}
comment|/**    * Returns whether a join of this type may generate NULL values on the    * left-hand side.    */
specifier|public
name|boolean
name|generatesNullsOnLeft
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|RIGHT
operator|)
operator|||
operator|(
name|this
operator|==
name|FULL
operator|)
return|;
block|}
comment|/**    * Swaps left to right, and vice versa.    */
specifier|public
name|JoinRelType
name|swap
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|LEFT
case|:
return|return
name|RIGHT
return|;
case|case
name|RIGHT
case|:
return|return
name|LEFT
return|;
default|default:
return|return
name|this
return|;
block|}
block|}
comment|/** Returns whether this join type generates nulls on side #{@code i}. */
specifier|public
name|boolean
name|generatesNullsOn
parameter_list|(
name|int
name|i
parameter_list|)
block|{
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
return|return
name|generatesNullsOnLeft
argument_list|()
return|;
case|case
literal|1
case|:
return|return
name|generatesNullsOnRight
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid: "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
comment|/** Returns a join type similar to this but that does not generate nulls on    * the left. */
specifier|public
name|JoinRelType
name|cancelNullsOnLeft
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|RIGHT
case|:
return|return
name|INNER
return|;
case|case
name|FULL
case|:
return|return
name|LEFT
return|;
default|default:
return|return
name|this
return|;
block|}
block|}
comment|/** Returns a join type similar to this but that does not generate nulls on    * the right. */
specifier|public
name|JoinRelType
name|cancelNullsOnRight
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|LEFT
case|:
return|return
name|INNER
return|;
case|case
name|FULL
case|:
return|return
name|RIGHT
return|;
default|default:
return|return
name|this
return|;
block|}
block|}
block|}
end_enum

begin_comment
comment|// End JoinRelType.java
end_comment

end_unit

