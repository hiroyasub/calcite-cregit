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
name|lambda
operator|.
name|functions
package|;
end_package

begin_comment
comment|/**  * BinaryOperator.  *  *<p>Based on {@code java.util.functions.BinaryOperator}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|BinaryOperator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Combiner
argument_list|<
name|T
argument_list|,
name|T
argument_list|,
name|T
argument_list|>
block|{
name|T
name|operate
parameter_list|(
name|T
name|left
parameter_list|,
name|T
name|right
parameter_list|)
function_decl|;
name|T
name|combine
parameter_list|(
name|T
name|t1
parameter_list|,
name|T
name|t2
parameter_list|)
function_decl|;
comment|// default:
comment|// return operate(t1, t2);
block|}
end_interface

begin_comment
comment|// End BinaryOperator.java
end_comment

end_unit

