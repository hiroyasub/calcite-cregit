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
name|server
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
name|Enumerator
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|QueryProvider
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
name|linq4j
operator|.
name|Queryable
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
name|RelDataType
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
name|Schemas
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
name|Wrapper
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
name|impl
operator|.
name|AbstractTableQueryable
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
name|sql2rel
operator|.
name|InitializerExpressionFactory
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
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

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
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/** Table backed by a Java list. */
end_comment

begin_class
class|class
name|MutableArrayTable
extends|extends
name|AbstractModifiableTable
implements|implements
name|Wrapper
block|{
specifier|final
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
name|RelProtoDataType
name|protoStoredRowType
decl_stmt|;
specifier|private
specifier|final
name|RelProtoDataType
name|protoRowType
decl_stmt|;
specifier|private
specifier|final
name|InitializerExpressionFactory
name|initializerExpressionFactory
decl_stmt|;
comment|/** Creates a MutableArrayTable.    *    * @param name Name of table within its schema    * @param protoStoredRowType Prototype of row type of stored columns (all    *     columns except virtual columns)    * @param protoRowType Prototype of row type (all columns)    * @param initializerExpressionFactory How columns are populated    */
name|MutableArrayTable
parameter_list|(
name|String
name|name
parameter_list|,
name|RelProtoDataType
name|protoStoredRowType
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|,
name|InitializerExpressionFactory
name|initializerExpressionFactory
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|protoStoredRowType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|protoStoredRowType
argument_list|,
literal|"protoStoredRowType"
argument_list|)
expr_stmt|;
name|this
operator|.
name|protoRowType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|protoRowType
argument_list|,
literal|"protoRowType"
argument_list|)
expr_stmt|;
name|this
operator|.
name|initializerExpressionFactory
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|initializerExpressionFactory
argument_list|,
literal|"initializerExpressionFactory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
name|getModifiableCollection
parameter_list|()
block|{
return|return
name|rows
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|new
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Enumerator
argument_list|<
name|T
argument_list|>
operator|)
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|rows
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Schemas
operator|.
name|tableExpression
argument_list|(
name|schema
argument_list|,
name|getElementType
argument_list|()
argument_list|,
name|tableName
argument_list|,
name|clazz
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|C
extends|extends
name|Object
parameter_list|>
annotation|@
name|Nullable
name|C
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|C
argument_list|>
name|aClass
parameter_list|)
block|{
if|if
condition|(
name|aClass
operator|.
name|isInstance
argument_list|(
name|initializerExpressionFactory
argument_list|)
condition|)
block|{
return|return
name|aClass
operator|.
name|cast
argument_list|(
name|initializerExpressionFactory
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|unwrap
argument_list|(
name|aClass
argument_list|)
return|;
block|}
block|}
end_class

end_unit

