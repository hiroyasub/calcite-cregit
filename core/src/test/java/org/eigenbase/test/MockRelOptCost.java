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
name|test
package|;
end_package

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

begin_comment
comment|/**  * MockRelOptCost is a mock implementation of the {@link RelOptCost} interface.  * TODO: constructors for various scenarios  */
end_comment

begin_class
specifier|public
class|class
name|MockRelOptCost
implements|implements
name|RelOptCost
block|{
comment|//~ Methods ----------------------------------------------------------------
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
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|MockRelOptCost
operator|&&
name|equals
argument_list|(
operator|(
name|MockRelOptCost
operator|)
name|obj
argument_list|)
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
literal|1
return|;
block|}
specifier|public
name|double
name|getCpu
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isInfinite
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|double
name|getIo
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isLe
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isLt
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isEqWithEpsilon
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|RelOptCost
name|minus
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|RelOptCost
name|multiplyBy
parameter_list|(
name|double
name|factor
parameter_list|)
block|{
return|return
name|this
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
return|return
literal|1
return|;
block|}
specifier|public
name|RelOptCost
name|plus
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MockRelOptCost(0)"
return|;
block|}
block|}
end_class

begin_comment
comment|// End MockRelOptCost.java
end_comment

end_unit

