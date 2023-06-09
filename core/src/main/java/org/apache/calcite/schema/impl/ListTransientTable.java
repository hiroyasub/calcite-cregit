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
name|DataContext
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
name|adapter
operator|.
name|java
operator|.
name|AbstractQueryableTable
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
name|AbstractEnumerable
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
name|Enumerable
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
name|function
operator|.
name|Experimental
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
name|plan
operator|.
name|RelOptCluster
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
name|plan
operator|.
name|RelOptTable
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
name|prepare
operator|.
name|Prepare
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
name|rel
operator|.
name|core
operator|.
name|TableModify
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
name|logical
operator|.
name|LogicalTableModify
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
name|rex
operator|.
name|RexNode
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
name|ModifiableTable
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
name|ScannableTable
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
name|TransientTable
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * {@link TransientTable} backed by a Java list. It will be automatically added to the  * current schema when {@link #scan(DataContext)} method gets called.  *  *<p>NOTE: The current API is experimental and subject to change without notice.</p>  */
end_comment

begin_class
annotation|@
name|Experimental
specifier|public
class|class
name|ListTransientTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TransientTable
implements|,
name|ModifiableTable
implements|,
name|ScannableTable
block|{
specifier|private
specifier|static
specifier|final
name|Type
name|TYPE
init|=
name|Object
index|[]
operator|.
name|class
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
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
block|{
literal|"unused"
block|,
literal|"FieldCanBeLocal"
block|}
argument_list|)
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|protoRowType
decl_stmt|;
specifier|public
name|ListTransientTable
parameter_list|(
name|String
name|name
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|(
name|TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|protoRowType
operator|=
name|rowType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TableModify
name|toModificationRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|TableModify
operator|.
name|Operation
name|operation
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|updateColumnList
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|RexNode
argument_list|>
name|sourceExpressionList
parameter_list|,
name|boolean
name|flattened
parameter_list|)
block|{
return|return
name|LogicalTableModify
operator|.
name|create
argument_list|(
name|table
argument_list|,
name|catalogReader
argument_list|,
name|child
argument_list|,
name|operation
argument_list|,
name|updateColumnList
argument_list|,
name|sourceExpressionList
argument_list|,
name|flattened
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
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
name|Enumerable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
specifier|final
name|AtomicBoolean
name|cancelFlag
init|=
name|DataContext
operator|.
name|Variable
operator|.
name|CANCEL_FLAG
operator|.
name|get
argument_list|(
name|root
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractEnumerable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|Enumerator
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|private
specifier|final
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|rows
argument_list|)
decl_stmt|;
specifier|private
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
comment|// TODO cleaner way to handle non-array objects?
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
name|Object
name|current
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
return|return
name|current
operator|!=
literal|null
operator|&&
name|current
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|?
operator|(
name|Object
index|[]
operator|)
name|current
else|:
operator|new
name|Object
index|[]
block|{
name|current
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
name|cancelFlag
operator|!=
literal|null
operator|&&
name|cancelFlag
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|++
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|i
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
block|}
return|;
block|}
block|}
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
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
name|elementType
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
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|copyType
argument_list|(
name|protoRowType
argument_list|)
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
name|TYPE
return|;
block|}
block|}
end_class

end_unit

