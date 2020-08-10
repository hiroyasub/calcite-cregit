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
name|test
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
name|JavaTypeFactory
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
name|CalcitePrepare
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
name|jdbc
operator|.
name|ContextSqlValidator
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
name|RelDataTypeImpl
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
name|TranslatableTable
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
name|schema
operator|.
name|impl
operator|.
name|ViewTable
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
name|ViewTableMacro
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
name|server
operator|.
name|DdlExecutor
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
name|server
operator|.
name|DdlExecutorImpl
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
name|sql
operator|.
name|SqlIdentifier
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
name|sql
operator|.
name|SqlNode
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
name|sql
operator|.
name|SqlUtil
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
name|sql
operator|.
name|dialect
operator|.
name|CalciteSqlDialect
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
name|sql
operator|.
name|parser
operator|.
name|SqlAbstractParserImpl
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
name|sql
operator|.
name|parser
operator|.
name|SqlParseException
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
name|sql
operator|.
name|parser
operator|.
name|SqlParserImplFactory
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
name|sql
operator|.
name|parser
operator|.
name|parserextensiontesting
operator|.
name|ExtensionSqlParserImpl
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
name|sql
operator|.
name|parser
operator|.
name|parserextensiontesting
operator|.
name|SqlCreateTable
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
name|sql
operator|.
name|pretty
operator|.
name|SqlPrettyWriter
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidator
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
name|tools
operator|.
name|FrameworkConfig
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
name|tools
operator|.
name|Frameworks
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
name|tools
operator|.
name|Planner
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
name|tools
operator|.
name|RelConversionException
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
name|tools
operator|.
name|ValidationException
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
name|io
operator|.
name|Reader
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
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/** Executes the few DDL commands supported by  * {@link ExtensionSqlParserImpl}. */
end_comment

begin_class
specifier|public
class|class
name|ExtensionDdlExecutor
extends|extends
name|DdlExecutorImpl
block|{
specifier|static
specifier|final
name|ExtensionDdlExecutor
name|INSTANCE
init|=
operator|new
name|ExtensionDdlExecutor
argument_list|()
decl_stmt|;
comment|/** Parser factory. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// used via reflection
specifier|public
specifier|static
specifier|final
name|SqlParserImplFactory
name|PARSER_FACTORY
init|=
operator|new
name|SqlParserImplFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SqlAbstractParserImpl
name|getParser
parameter_list|(
name|Reader
name|stream
parameter_list|)
block|{
return|return
name|ExtensionSqlParserImpl
operator|.
name|FACTORY
operator|.
name|getParser
argument_list|(
name|stream
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DdlExecutor
name|getDdlExecutor
parameter_list|()
block|{
return|return
name|ExtensionDdlExecutor
operator|.
name|INSTANCE
return|;
block|}
block|}
decl_stmt|;
comment|/** Executes a {@code CREATE TABLE} command. Called via reflection. */
specifier|public
name|void
name|execute
parameter_list|(
name|SqlCreateTable
name|create
parameter_list|,
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|)
block|{
specifier|final
name|CalciteSchema
name|schema
init|=
name|Schemas
operator|.
name|subSchema
argument_list|(
name|context
operator|.
name|getRootSchema
argument_list|()
argument_list|,
name|context
operator|.
name|getDefaultSchemaPath
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
name|context
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|queryRowType
decl_stmt|;
if|if
condition|(
name|create
operator|.
name|query
operator|!=
literal|null
condition|)
block|{
comment|// A bit of a hack: pretend it's a view, to get its row type
specifier|final
name|String
name|sql
init|=
name|create
operator|.
name|query
operator|.
name|toSqlString
argument_list|(
name|CalciteSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
specifier|final
name|ViewTableMacro
name|viewTableMacro
init|=
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|schema
operator|.
name|plus
argument_list|()
argument_list|,
name|sql
argument_list|,
name|schema
operator|.
name|path
argument_list|(
literal|null
argument_list|)
argument_list|,
name|context
operator|.
name|getObjectPath
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|TranslatableTable
name|x
init|=
name|viewTableMacro
operator|.
name|apply
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|queryRowType
operator|=
name|x
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
if|if
condition|(
name|create
operator|.
name|columnList
operator|!=
literal|null
operator|&&
name|queryRowType
operator|.
name|getFieldCount
argument_list|()
operator|!=
name|create
operator|.
name|columnList
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|create
operator|.
name|columnList
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|columnCountMismatch
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|queryRowType
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|create
operator|.
name|columnList
operator|!=
literal|null
condition|)
block|{
specifier|final
name|SqlValidator
name|validator
init|=
operator|new
name|ContextSqlValidator
argument_list|(
name|context
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|create
operator|.
name|forEachNameType
argument_list|(
parameter_list|(
name|name
parameter_list|,
name|typeSpec
parameter_list|)
lambda|->
name|builder
operator|.
name|add
argument_list|(
name|name
operator|.
name|getSimple
argument_list|()
argument_list|,
name|typeSpec
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|queryRowType
operator|==
literal|null
condition|)
block|{
comment|// "CREATE TABLE t" is invalid; because there is no "AS query" we need
comment|// a list of column names and types, "CREATE TABLE t (INT c)".
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|create
operator|.
name|name
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|createTableRequiresColumnList
argument_list|()
argument_list|)
throw|;
block|}
name|builder
operator|.
name|addAll
argument_list|(
name|queryRowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelDataType
name|rowType
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
name|create
operator|.
name|name
operator|.
name|getSimple
argument_list|()
argument_list|,
operator|new
name|MutableArrayTable
argument_list|(
name|create
operator|.
name|name
operator|.
name|getSimple
argument_list|()
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|rowType
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|create
operator|.
name|query
operator|!=
literal|null
condition|)
block|{
name|populate
argument_list|(
name|create
operator|.
name|name
argument_list|,
name|create
operator|.
name|query
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Populates the table called {@code name} by executing {@code query}. */
specifier|protected
specifier|static
name|void
name|populate
parameter_list|(
name|SqlIdentifier
name|name
parameter_list|,
name|SqlNode
name|query
parameter_list|,
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|)
block|{
comment|// Generate, prepare and execute an "INSERT INTO table query" statement.
comment|// (It's a bit inefficient that we convert from SqlNode to SQL and back
comment|// again.)
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|defaultSchema
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|Schemas
operator|.
name|subSchema
argument_list|(
name|context
operator|.
name|getRootSchema
argument_list|()
argument_list|,
name|context
operator|.
name|getDefaultSchemaPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|plus
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Planner
name|planner
init|=
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|config
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|SqlPrettyWriter
name|w
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlPrettyWriter
operator|.
name|config
argument_list|()
operator|.
name|withDialect
argument_list|(
name|CalciteSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|withAlwaysUseParentheses
argument_list|(
literal|false
argument_list|)
argument_list|,
name|buf
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"INSERT INTO "
argument_list|)
expr_stmt|;
name|name
operator|.
name|unparse
argument_list|(
name|w
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|query
operator|.
name|unparse
argument_list|(
name|w
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|query1
init|=
name|planner
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|query2
init|=
name|planner
operator|.
name|validate
argument_list|(
name|query1
argument_list|)
decl_stmt|;
specifier|final
name|RelRoot
name|r
init|=
name|planner
operator|.
name|rel
argument_list|(
name|query2
argument_list|)
decl_stmt|;
specifier|final
name|PreparedStatement
name|prepare
init|=
name|context
operator|.
name|getRelRunner
argument_list|()
operator|.
name|prepareStatement
argument_list|(
name|r
operator|.
name|rel
argument_list|)
decl_stmt|;
name|int
name|rowCount
init|=
name|prepare
operator|.
name|executeUpdate
argument_list|()
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|rowCount
argument_list|)
expr_stmt|;
name|prepare
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
decl||
name|ValidationException
decl||
name|RelConversionException
decl||
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|throwAsRuntime
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Table backed by a Java list. */
specifier|private
specifier|static
class|class
name|MutableArrayTable
extends|extends
name|AbstractModifiableTable
block|{
specifier|final
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RelProtoDataType
name|protoRowType
decl_stmt|;
name|MutableArrayTable
parameter_list|(
name|String
name|name
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|protoRowType
operator|=
name|protoRowType
expr_stmt|;
block|}
specifier|public
name|Collection
name|getModifiableCollection
parameter_list|()
block|{
return|return
name|list
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
name|list
argument_list|)
return|;
block|}
block|}
return|;
block|}
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
block|}
block|}
end_class

end_unit

