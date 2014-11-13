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
comment|/**  * Equivalent fo {@link Mapper} for bi-values.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BiMapper
parameter_list|<
name|T
parameter_list|,
name|U
parameter_list|,
name|V
parameter_list|>
block|{
name|V
name|map
parameter_list|(
name|T
name|t
parameter_list|,
name|U
name|u
parameter_list|)
function_decl|;
parameter_list|<
name|W
parameter_list|>
name|BiMapper
argument_list|<
name|T
argument_list|,
name|U
argument_list|,
name|W
argument_list|>
name|compose
parameter_list|(
name|Mapper
argument_list|<
name|?
super|super
name|V
argument_list|,
name|?
extends|extends
name|W
argument_list|>
name|after
parameter_list|)
function_decl|;
comment|// default:
comment|// throw new UnsupportedOperationException("Not yet implemented.");
block|}
end_interface

begin_comment
comment|// End BiMapper.java
end_comment

end_unit

