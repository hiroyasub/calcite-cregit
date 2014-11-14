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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * Contains the parts of the {@link java.lang.reflect.Field} class needed  * for code generation, but might be implemented differently.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PseudoField
block|{
name|String
name|getName
parameter_list|()
function_decl|;
name|Type
name|getType
parameter_list|()
function_decl|;
name|int
name|getModifiers
parameter_list|()
function_decl|;
name|Object
name|get
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IllegalAccessException
function_decl|;
name|Type
name|getDeclaringClass
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End PseudoField.java
end_comment

end_unit

