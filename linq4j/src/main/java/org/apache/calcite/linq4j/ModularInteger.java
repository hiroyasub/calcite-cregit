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
name|linq4j
package|;
end_package

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
comment|/**  * Represents an integer in modular arithmetic.  * Its {@code value} is between 0 and {@code m - 1} for some modulus {@code m}.  *  *<p>This object is immutable; all operations create a new object.  */
end_comment

begin_class
class|class
name|ModularInteger
block|{
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
specifier|private
specifier|final
name|int
name|modulus
decl_stmt|;
comment|/** Creates a ModularInteger. */
name|ModularInteger
parameter_list|(
name|int
name|value
parameter_list|,
name|int
name|modulus
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|value
operator|>=
literal|0
operator|&&
name|value
operator|<
name|modulus
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|modulus
operator|=
name|modulus
expr_stmt|;
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
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|ModularInteger
operator|&&
name|value
operator|==
operator|(
operator|(
name|ModularInteger
operator|)
name|obj
operator|)
operator|.
name|value
operator|&&
name|modulus
operator|==
operator|(
operator|(
name|ModularInteger
operator|)
name|obj
operator|)
operator|.
name|modulus
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// 8191 is prime and, as 2^13 - 1, can easily be multiplied by bit-shifting
return|return
name|value
operator|+
literal|8191
operator|*
name|modulus
return|;
block|}
specifier|public
name|int
name|get
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
specifier|public
name|ModularInteger
name|plus
parameter_list|(
name|int
name|operand
parameter_list|)
block|{
if|if
condition|(
name|operand
operator|<
literal|0
condition|)
block|{
return|return
name|minus
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|operand
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|ModularInteger
argument_list|(
operator|(
name|value
operator|+
name|operand
operator|)
operator|%
name|modulus
argument_list|,
name|modulus
argument_list|)
return|;
block|}
specifier|public
name|ModularInteger
name|minus
parameter_list|(
name|int
name|operand
parameter_list|)
block|{
assert|assert
name|operand
operator|>=
literal|0
assert|;
name|int
name|r
init|=
name|value
operator|-
name|operand
decl_stmt|;
while|while
condition|(
name|r
operator|<
literal|0
condition|)
block|{
name|r
operator|=
name|r
operator|+
name|modulus
expr_stmt|;
block|}
return|return
operator|new
name|ModularInteger
argument_list|(
name|r
argument_list|,
name|modulus
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
name|value
operator|+
literal|" mod "
operator|+
name|modulus
return|;
block|}
block|}
end_class

begin_comment
comment|// End ModularInteger.java
end_comment

end_unit

