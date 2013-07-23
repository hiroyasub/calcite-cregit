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
name|impl
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link net.hydromatic.optiq.Schema.TableFunctionInSchema}  * where all properties are held in fields.  */
end_comment

begin_class
specifier|public
class|class
name|TableFunctionInSchemaImpl
extends|extends
name|Schema
operator|.
name|TableFunctionInSchema
block|{
specifier|private
specifier|final
name|TableFunction
name|tableFunction
decl_stmt|;
comment|/** Creates a TableFunctionInSchemaImpl. */
specifier|public
name|TableFunctionInSchemaImpl
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|TableFunction
name|tableFunction
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableFunction
operator|=
name|tableFunction
expr_stmt|;
block|}
specifier|public
name|TableFunction
name|getTableFunction
parameter_list|()
block|{
return|return
name|tableFunction
return|;
block|}
specifier|public
name|boolean
name|isMaterialization
parameter_list|()
block|{
return|return
name|tableFunction
operator|instanceof
name|MaterializedViewTable
operator|.
name|MaterializedViewTableFunction
return|;
block|}
block|}
end_class

begin_comment
comment|// End TableFunctionInSchemaImpl.java
end_comment

end_unit

