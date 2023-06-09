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
name|sql
operator|.
name|validate
operator|.
name|implicit
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidator
import|;
end_import

begin_comment
comment|/**  * Factory class for type coercion instantiation of different sql dialects.  */
end_comment

begin_class
specifier|public
class|class
name|TypeCoercions
block|{
specifier|private
name|TypeCoercions
parameter_list|()
block|{
block|}
comment|/** Creates a default type coercion instance. */
specifier|public
specifier|static
name|TypeCoercion
name|createTypeCoercion
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlValidator
name|validator
parameter_list|)
block|{
return|return
operator|new
name|TypeCoercionImpl
argument_list|(
name|typeFactory
argument_list|,
name|validator
argument_list|)
return|;
block|}
block|}
end_class

end_unit

