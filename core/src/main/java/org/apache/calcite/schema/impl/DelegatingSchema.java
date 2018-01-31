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
name|schema
operator|.
name|impl
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|rel
operator|.
name|type
operator|.
name|RelProtoDataType
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
name|schema
operator|.
name|Function
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
name|schema
operator|.
name|Schema
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
name|schema
operator|.
name|SchemaPlus
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
name|schema
operator|.
name|SchemaVersion
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
name|schema
operator|.
name|Table
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.schema.Schema} that delegates to  * an underlying schema.  */
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
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
name|schema
operator|.
name|isMutable
argument_list|()
return|;
block|}
specifier|public
name|Schema
name|snapshot
parameter_list|(
name|SchemaVersion
name|version
parameter_list|)
block|{
return|return
name|schema
operator|.
name|snapshot
argument_list|(
name|version
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getExpression
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getTable
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTableNames
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getTableNames
argument_list|()
return|;
block|}
specifier|public
name|RelProtoDataType
name|getType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getType
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTypeNames
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getTypeNames
argument_list|()
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|Function
argument_list|>
name|getFunctions
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getFunctions
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getFunctionNames
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getFunctionNames
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
name|Set
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
block|}
end_class

begin_comment
comment|// End DelegatingSchema.java
end_comment

end_unit

