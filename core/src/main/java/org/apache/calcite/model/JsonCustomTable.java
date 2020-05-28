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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonCreator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
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
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Custom table schema element.  *  *<p>Like base class {@link JsonTable},  * occurs within {@link JsonMapSchema#tables}.  *  * @see JsonRoot Description of schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonCustomTable
extends|extends
name|JsonTable
block|{
comment|/** Name of the factory class for this table.    *    *<p>Required. Must implement interface    * {@link org.apache.calcite.schema.TableFactory} and have a public default    * constructor.    */
specifier|public
specifier|final
name|String
name|factory
decl_stmt|;
comment|/** Contains attributes to be passed to the factory.    *    *<p>May be a JSON object (represented as Map) or null.    */
specifier|public
specifier|final
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|JsonCustomTable
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"name"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
name|String
name|name
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"stream"
argument_list|)
name|JsonStream
name|stream
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"factory"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
name|String
name|factory
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"operand"
argument_list|)
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|requireNonNull
argument_list|(
name|factory
argument_list|,
literal|"factory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|operand
operator|=
name|operand
expr_stmt|;
block|}
annotation|@
name|Override
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
block|}
end_class

end_unit

