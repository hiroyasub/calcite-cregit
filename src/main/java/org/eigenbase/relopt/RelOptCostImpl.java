begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_comment
comment|/**  * RelOptCostImpl provides a default implementation for the {@link RelOptCost}  * interface. It it defined in terms of a single scalar quantity; somewhat  * arbitrarily, it returns this scalar for rows processed and zero for both CPU  * and I/O.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RelOptCostImpl
implements|implements
name|RelOptCost
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|double
name|value
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelOptCostImpl
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptCost
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|double
name|getIo
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|double
name|getCpu
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|boolean
name|isInfinite
parameter_list|()
block|{
return|return
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|boolean
name|isLe
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
return|return
name|getRows
argument_list|()
operator|<=
name|other
operator|.
name|getRows
argument_list|()
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|boolean
name|isLt
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
return|return
name|getRows
argument_list|()
operator|<
name|other
operator|.
name|getRows
argument_list|()
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|boolean
name|equals
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
return|return
name|getRows
argument_list|()
operator|==
name|other
operator|.
name|getRows
argument_list|()
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|boolean
name|isEqWithEpsilon
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
return|return
name|Math
operator|.
name|abs
argument_list|(
name|getRows
argument_list|()
operator|-
name|other
operator|.
name|getRows
argument_list|()
argument_list|)
operator|<
name|RelOptUtil
operator|.
name|EPSILON
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|RelOptCost
name|minus
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
return|return
operator|new
name|RelOptCostImpl
argument_list|(
name|getRows
argument_list|()
operator|-
name|other
operator|.
name|getRows
argument_list|()
argument_list|)
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|RelOptCost
name|plus
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
return|return
operator|new
name|RelOptCostImpl
argument_list|(
name|getRows
argument_list|()
operator|+
name|other
operator|.
name|getRows
argument_list|()
argument_list|)
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|RelOptCost
name|multiplyBy
parameter_list|(
name|double
name|factor
parameter_list|)
block|{
return|return
operator|new
name|RelOptCostImpl
argument_list|(
name|getRows
argument_list|()
operator|*
name|factor
argument_list|)
return|;
block|}
specifier|public
name|double
name|divideBy
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
name|RelOptCostImpl
name|that
init|=
operator|(
name|RelOptCostImpl
operator|)
name|cost
decl_stmt|;
return|return
name|this
operator|.
name|getRows
argument_list|()
operator|/
name|that
operator|.
name|getRows
argument_list|()
return|;
block|}
comment|// implement RelOptCost
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|value
operator|==
name|Double
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
literal|"huge"
return|;
block|}
else|else
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelOptCostImpl.java
end_comment

end_unit

