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
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * An<code>Aggregation</code> aggregates a set of values into one value.  *  *<p>It is used, via a {@link AggregateCall}, in an {@link AggregateRel}  * relational operator.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Aggregation
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the parameter types accepted by this Aggregation.    *    * @param typeFactory Type factory to create the types    * @return Array of parameter types    */
name|List
argument_list|<
name|RelDataType
argument_list|>
name|getParameterTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
comment|/**    * Returns the type of the result yielded by this Aggregation.    *    * @param typeFactory Type factory to create the type    * @return Result type    */
name|RelDataType
name|getReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
comment|/**    * Returns the name of this Aggregation    *    * @return name of this aggregation    */
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Aggregation.java
end_comment

end_unit

