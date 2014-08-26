begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Queryable
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
name|AbstractQueryableTable
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Util
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
name|Collections
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
import|import static
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|MaterializedViewTable
operator|.
name|MATERIALIZATION_CONNECTION
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
block|}
comment|/** Table macro that returns a view. */
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
operator|new
name|ViewTableMacro
argument_list|(
name|OptiqSchema
operator|.
name|from
argument_list|(
name|schema
argument_list|)
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|)
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
return|;
block|}
specifier|private
name|RelNode
name|expandView
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|preparingStmt
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
name|RelNode
name|rel
init|=
name|preparingStmt
operator|.
name|expandView
argument_list|(
name|rowType
argument_list|,
name|queryString
argument_list|,
name|schemaPath
argument_list|)
decl_stmt|;
name|rel
operator|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|rel
argument_list|,
name|rowType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//rel = viewExpander.flattenTypes(rel, false);
return|return
name|rel
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Error while parsing view definition:  "
operator|+
name|queryString
argument_list|)
throw|;
block|}
block|}
comment|/** Table function that implements a view. It returns the operator    * tree of the view's SQL query. */
specifier|static
class|class
name|ViewTableMacro
implements|implements
name|TableMacro
block|{
specifier|protected
specifier|final
name|String
name|viewSql
decl_stmt|;
specifier|protected
specifier|final
name|OptiqSchema
name|schema
decl_stmt|;
comment|/** Typically null. If specified, overrides the path of the schema as the      * context for validating {@code viewSql}. */
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
decl_stmt|;
name|ViewTableMacro
parameter_list|(
name|OptiqSchema
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
parameter_list|)
block|{
name|this
operator|.
name|viewSql
operator|=
name|viewSql
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|schemaPath
operator|=
name|schemaPath
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|schemaPath
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|TranslatableTable
name|apply
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
name|OptiqPrepare
operator|.
name|ParseResult
name|parsed
init|=
name|Schemas
operator|.
name|parse
argument_list|(
name|MATERIALIZATION_CONNECTION
argument_list|,
name|schema
argument_list|,
name|schemaPath
argument_list|,
name|viewSql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath1
init|=
name|schemaPath
operator|!=
literal|null
condition|?
name|schemaPath
else|:
name|schema
operator|.
name|path
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|parsed
operator|.
name|typeFactory
decl_stmt|;
return|return
operator|new
name|ViewTable
argument_list|(
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|parsed
operator|.
name|rowType
argument_list|)
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|parsed
operator|.
name|rowType
argument_list|)
argument_list|,
name|viewSql
argument_list|,
name|schemaPath1
argument_list|)
return|;
block|}
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
comment|/** Returns the the schema path of the view. */
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
block|}
end_class

begin_comment
comment|// End ViewTable.java
end_comment

end_unit

