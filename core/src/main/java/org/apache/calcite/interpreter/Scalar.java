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
name|interpreter
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
name|DataContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Compiled scalar expression.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Scalar
block|{
annotation|@
name|Nullable
name|Object
name|execute
parameter_list|(
name|Context
name|context
parameter_list|)
function_decl|;
name|void
name|execute
parameter_list|(
name|Context
name|context
parameter_list|,
annotation|@
name|Nullable
name|Object
index|[]
name|results
parameter_list|)
function_decl|;
comment|/** Produces a {@link Scalar} when a query is executed.    *    *<p>Call {@code producer.apply(DataContext)} to get a Scalar. */
interface|interface
name|Producer
extends|extends
name|Function
argument_list|<
name|DataContext
argument_list|,
name|Scalar
argument_list|>
block|{   }
block|}
end_interface

end_unit

