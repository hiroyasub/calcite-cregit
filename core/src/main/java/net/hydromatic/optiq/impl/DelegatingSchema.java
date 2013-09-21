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
name|linq4j
operator|.
name|QueryProvider
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multimap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * Implementation of {@link net.hydromatic.optiq.Schema} that delegates to an  * underlying schema.  */
end_comment

begin_class
specifier|public
class|class
name|DelegatingSchema
implements|implements
name|Schema
block|{
specifier|protected
specifier|final
name|Schema
name|schema
decl_stmt|;
comment|/**    * Creates a DelegatingSchema.    *    * @param schema Underlying schema    */
specifier|public
name|DelegatingSchema
parameter_list|(
name|Schema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
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
literal|"DelegatingSchema(delegate="
operator|+
name|schema
operator|+
literal|")"
return|;
block|}
specifier|public
name|Schema
name|getParentSchema
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getParentSchema
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|schema
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|String
argument_list|,
name|TableFunctionInSchema
argument_list|>
name|getTableFunctions
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getTableFunctions
argument_list|()
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getExpression
argument_list|()
return|;
block|}
specifier|public
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getQueryProvider
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|TableInSchema
argument_list|>
name|getTables
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getTables
argument_list|()
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Table
argument_list|<
name|T
argument_list|>
name|getTable
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|elementType
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getTable
argument_list|(
name|name
argument_list|,
name|elementType
argument_list|)
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|TableFunctionInSchema
argument_list|>
name|getTableFunctions
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getTableFunctions
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getSubSchemaNames
argument_list|()
return|;
block|}
specifier|public
name|Schema
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getSubSchema
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getTypeFactory
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End DelegatingSchema.java
end_comment

end_unit

