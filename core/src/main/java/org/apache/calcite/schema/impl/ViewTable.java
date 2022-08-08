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
name|jdbc
operator|.
name|CalciteSchema
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
name|plan
operator|.
name|RelOptUtil
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
name|RelRoot
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
name|RelShuttleImpl
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
name|TableScan
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
name|TranslatableTable
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
name|ImmutableList
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
name|List
import|;
end_import

begin_comment
comment|/**  * Table whose contents are defined using an SQL statement.  *  *<p>It is not evaluated; it is expanded during query planning.</p>  */
end_comment

begin_class
specifier|public
class|class
name|ViewTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
block|{
specifier|private
specifier|final
name|String
name|viewSql
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
decl_stmt|;
specifier|private
specifier|final
name|RelProtoDataType
name|protoRowType
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
decl_stmt|;
specifier|public
name|ViewTable
parameter_list|(
name|Type
name|elementType
parameter_list|,
name|RelProtoDataType
name|rowType
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|)
block|{
name|super
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
name|this
operator|.
name|viewSql
operator|=
name|viewSql
expr_stmt|;
name|this
operator|.
name|schemaPath
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|schemaPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|protoRowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|viewPath
operator|=
name|viewPath
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|viewPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|ViewTableMacro
name|viewMacro
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
specifier|final
name|String
name|viewSql
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
block|{
return|return
name|viewMacro
argument_list|(
name|schema
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|,
literal|null
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|ViewTableMacro
name|viewMacro
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|modifiable
parameter_list|)
block|{
return|return
name|viewMacro
argument_list|(
name|schema
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|,
literal|null
argument_list|,
name|modifiable
argument_list|)
return|;
block|}
comment|/** Table macro that returns a view.    *    * @param schema Schema the view will belong to    * @param viewSql SQL query    * @param schemaPath Path of schema    * @param modifiable Whether view is modifiable, or null to deduce it    */
specifier|public
specifier|static
name|ViewTableMacro
name|viewMacro
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|modifiable
parameter_list|)
block|{
return|return
operator|new
name|ViewTableMacro
argument_list|(
name|CalciteSchema
operator|.
name|from
argument_list|(
name|schema
argument_list|)
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|,
name|viewPath
argument_list|,
name|modifiable
argument_list|)
return|;
block|}
comment|/** Returns the view's SQL definition. */
specifier|public
name|String
name|getViewSql
parameter_list|()
block|{
return|return
name|viewSql
return|;
block|}
comment|/** Returns the schema path of the view. */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSchemaPath
parameter_list|()
block|{
return|return
name|schemaPath
return|;
block|}
comment|/** Returns the path of the view. */
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|getViewPath
parameter_list|()
block|{
return|return
name|viewPath
return|;
block|}
annotation|@
name|Override
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|VIEW
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
name|queryProvider
operator|.
name|createQuery
argument_list|(
name|getExpression
argument_list|(
name|schema
argument_list|,
name|tableName
argument_list|,
name|Queryable
operator|.
name|class
argument_list|)
argument_list|,
name|elementType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
return|return
name|expandView
argument_list|(
name|context
argument_list|,
name|relOptTable
operator|.
name|getRowType
argument_list|()
argument_list|,
name|viewSql
argument_list|)
operator|.
name|rel
return|;
block|}
specifier|private
name|RelRoot
name|expandView
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|String
name|queryString
parameter_list|)
block|{
try|try
block|{
specifier|final
name|RelRoot
name|root
init|=
name|context
operator|.
name|expandView
argument_list|(
name|rowType
argument_list|,
name|queryString
argument_list|,
name|schemaPath
argument_list|,
name|viewPath
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|root
operator|.
name|rel
argument_list|,
name|rowType
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Expand any views
specifier|final
name|RelNode
name|rel2
init|=
name|rel
operator|.
name|accept
argument_list|(
operator|new
name|RelShuttleImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableScan
name|scan
parameter_list|)
block|{
specifier|final
name|RelOptTable
name|table
init|=
name|scan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|TranslatableTable
name|translatableTable
init|=
name|table
operator|.
name|unwrap
argument_list|(
name|TranslatableTable
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|translatableTable
operator|!=
literal|null
condition|)
block|{
return|return
name|translatableTable
operator|.
name|toRel
argument_list|(
name|context
argument_list|,
name|table
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|scan
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|root
operator|.
name|withRel
argument_list|(
name|rel2
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error while parsing view definition: "
operator|+
name|queryString
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

