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
name|rel
operator|.
name|metadata
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
name|RelNode
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
name|util
operator|.
name|ControlFlowException
import|;
end_import

begin_comment
comment|/**  * Provides {@link MetadataHandler} call sites for  * {@link RelMetadataQuery}. The handlers provided are responsible for  * updating the cache stored in {@link RelMetadataQuery}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MetadataHandlerProvider
block|{
comment|/**    * Provide a handler for the requested metadata class.    * @param handlerClass The handler interface expected    * @param<MH> The metadata type the handler relates to.    * @return The handler implementation.    */
parameter_list|<
name|MH
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
parameter_list|>
name|MH
name|handler
parameter_list|(
name|Class
argument_list|<
name|MH
argument_list|>
name|handlerClass
parameter_list|)
function_decl|;
comment|/** Re-generates the handler for a given kind of metadata.  */
comment|/**    * Revise the handler for a given kind of metadata.    *    *<p>Should be invoked if the existing handler throws a {@link NoHandler} exception.    *    * @param handlerClass The type of class to revise.    * @param<MH> The type metadata the handler provides.    * @return A new handler that should be used instead of any previous handler provided.    */
specifier|default
parameter_list|<
name|MH
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
parameter_list|>
name|MH
name|revise
parameter_list|(
name|Class
argument_list|<
name|MH
argument_list|>
name|handlerClass
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This provider doesn't support handler revision."
argument_list|)
throw|;
block|}
comment|/** Exception that indicates there there should be a handler for    * this class but there is not. The action is probably to    * re-generate the handler class. */
class|class
name|NoHandler
extends|extends
name|ControlFlowException
block|{
specifier|public
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
decl_stmt|;
specifier|public
name|NoHandler
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
parameter_list|)
block|{
name|this
operator|.
name|relClass
operator|=
name|relClass
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

