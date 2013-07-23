begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * JSON object representing a schema whose tables are explicitly specified.  *  * @see JsonRoot Description of JSON schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonMapSchema
extends|extends
name|JsonSchema
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|JsonTable
argument_list|>
name|tables
init|=
operator|new
name|ArrayList
argument_list|<
name|JsonTable
argument_list|>
argument_list|()
decl_stmt|;
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
annotation|@
name|Override
specifier|public
name|void
name|visitChildren
parameter_list|(
name|ModelHandler
name|modelHandler
parameter_list|)
block|{
name|super
operator|.
name|visitChildren
argument_list|(
name|modelHandler
argument_list|)
expr_stmt|;
for|for
control|(
name|JsonTable
name|jsonTable
range|:
name|tables
control|)
block|{
name|jsonTable
operator|.
name|accept
argument_list|(
name|modelHandler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JsonMapSchema.java
end_comment

end_unit

