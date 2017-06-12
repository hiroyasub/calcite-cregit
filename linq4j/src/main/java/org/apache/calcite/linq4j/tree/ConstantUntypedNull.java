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
operator|.
name|tree
package|;
end_package

begin_comment
comment|/**  * Represents a constant null of unknown type  * Java allows type inference for such nulls, thus "null" cannot always be  * replaced to (Object)null and vise versa.  *  *<p>{@code ConstantExpression(null, Object.class)} is not equal to  * {@code ConstantUntypedNull} However, optimizers might treat all the nulls  * equal (e.g. in case of comparison).  */
end_comment

begin_class
specifier|public
class|class
name|ConstantUntypedNull
extends|extends
name|ConstantExpression
block|{
specifier|public
specifier|static
specifier|final
name|ConstantExpression
name|INSTANCE
init|=
operator|new
name|ConstantUntypedNull
argument_list|()
decl_stmt|;
specifier|private
name|ConstantUntypedNull
parameter_list|()
block|{
name|super
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|int
name|lprec
parameter_list|,
name|int
name|rprec
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
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
name|o
operator|==
name|INSTANCE
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
name|ConstantUntypedNull
operator|.
name|class
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End ConstantUntypedNull.java
end_comment

end_unit

