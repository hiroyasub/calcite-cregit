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
name|model
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * JSON schema element that represents a custom schema.  *  *<p>Like the base class {@link JsonSchema},  * occurs within {@link JsonRoot#schemas}.  *  * @see org.apache.calcite.model.JsonRoot Description of schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonCustomSchema
extends|extends
name|JsonMapSchema
block|{
comment|/** Name of the factory class for this schema.    *    *<p>Required. Must implement interface    * {@link org.apache.calcite.schema.SchemaFactory} and have a public default    * constructor.    */
specifier|public
name|String
name|factory
decl_stmt|;
comment|/** Contains attributes to be passed to the factory.    *    *<p>May be a JSON object (represented as Map) or null.    */
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
decl_stmt|;
specifier|public
name|void
name|accept
parameter_list|(
name|ModelHandler
name|handler
parameter_list|)
block|{
name|handler
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JsonCustomSchema(name="
operator|+
name|name
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

