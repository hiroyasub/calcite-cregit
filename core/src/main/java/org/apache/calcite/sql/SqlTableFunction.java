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
name|sql
operator|.
name|type
operator|.
name|SqlReturnTypeInference
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

begin_comment
comment|/**  * A function that returns a table.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlTableFunction
block|{
comment|/**    * Returns the record type of the table yielded by this function when    * applied to given arguments. Only literal arguments are passed,    * non-literal are replaced with default values (null, 0, false, etc).    *    * @return strategy to infer the row type of a call to this function    */
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
function_decl|;
comment|/**    * Returns the table parameter characteristics for<code>ordinal</code>th    * parameter to this table function.    *    *<p>Returns<code>null</code> if the<code>ordinal</code>th argument is    * not table parameter or the<code>ordinal</code> is smaller than 0 or    * the<code>ordinal</code> is greater than or equals to the number of    * parameters.    */
specifier|default
annotation|@
name|Nullable
name|TableCharacteristic
name|tableCharacteristic
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_interface

end_unit

