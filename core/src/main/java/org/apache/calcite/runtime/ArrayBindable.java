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
name|runtime
package|;
end_package

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

begin_comment
comment|/**  * Extension to {@link Bindable} that returns rows that are arrays of objects.  *  *<p>It also implements {@link Typed}; the {@link #getElementType()} method  * must return {@code Object[].class}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ArrayBindable
extends|extends
name|Bindable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
extends|,
name|Typed
block|{
comment|// override
annotation|@
name|Override
name|Class
argument_list|<
name|Object
index|[]
argument_list|>
name|getElementType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

