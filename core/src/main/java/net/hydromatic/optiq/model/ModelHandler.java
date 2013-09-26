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
name|MapSchema
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
name|jdbc
operator|.
name|JdbcSchema
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
name|OptiqConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|dbcp
operator|.
name|BasicDataSource
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
name|Pair
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Stacks
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Reads a model and creates schema objects accordingly.  */
end_comment

begin_class
specifier|public
class|class
name|ModelHandler
block|{
specifier|private
specifier|final
name|OptiqConnection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
argument_list|>
name|schemaStack
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ModelHandler
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
specifier|final
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_UNQUOTED_FIELD_NAMES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_SINGLE_QUOTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|JsonRoot
name|root
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"inline:"
argument_list|)
condition|)
block|{
name|root
operator|=
name|mapper
operator|.
name|readValue
argument_list|(
name|uri
operator|.
name|substring
argument_list|(
literal|"inline:"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|JsonRoot
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|root
operator|=
name|mapper
operator|.
name|readValue
argument_list|(
operator|new
name|File
argument_list|(
name|uri
argument_list|)
argument_list|,
name|JsonRoot
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|visit
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonRoot
name|root
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|pair
init|=
name|Pair
operator|.
expr|<
name|String
decl_stmt|,
name|Schema
decl|>
name|of
argument_list|(
literal|null
argument_list|,
name|connection
operator|.
name|getRootSchema
argument_list|()
argument_list|)
decl_stmt|;
name|push
argument_list|(
name|schemaStack
argument_list|,
name|pair
argument_list|)
expr_stmt|;
for|for
control|(
name|JsonSchema
name|schema
range|:
name|root
operator|.
name|schemas
control|)
block|{
name|schema
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|pop
argument_list|(
name|schemaStack
argument_list|,
name|pair
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|.
name|defaultSchema
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|setSchema
argument_list|(
name|root
operator|.
name|defaultSchema
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonMapSchema
name|jsonSchema
parameter_list|)
block|{
specifier|final
name|MutableSchema
name|parentSchema
init|=
name|currentMutableSchema
argument_list|(
literal|"schema"
argument_list|)
decl_stmt|;
specifier|final
name|MapSchema
name|schema
init|=
name|MapSchema
operator|.
name|create
argument_list|(
name|parentSchema
argument_list|,
name|jsonSchema
operator|.
name|name
argument_list|)
decl_stmt|;
name|schema
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|populateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|populateSchema
parameter_list|(
name|JsonSchema
name|jsonSchema
parameter_list|,
name|Schema
name|schema
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|pair
init|=
name|Pair
operator|.
name|of
argument_list|(
name|jsonSchema
operator|.
name|name
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|push
argument_list|(
name|schemaStack
argument_list|,
name|pair
argument_list|)
expr_stmt|;
name|jsonSchema
operator|.
name|visitChildren
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|pop
argument_list|(
name|schemaStack
argument_list|,
name|pair
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonCustomSchema
name|jsonSchema
parameter_list|)
block|{
try|try
block|{
specifier|final
name|MutableSchema
name|parentSchema
init|=
name|currentMutableSchema
argument_list|(
literal|"sub-schema"
argument_list|)
decl_stmt|;
specifier|final
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|jsonSchema
operator|.
name|factory
argument_list|)
decl_stmt|;
specifier|final
name|SchemaFactory
name|schemaFactory
init|=
operator|(
name|SchemaFactory
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
name|Schema
name|schema
init|=
name|schemaFactory
operator|.
name|create
argument_list|(
name|parentSchema
argument_list|,
name|jsonSchema
operator|.
name|name
argument_list|,
name|jsonSchema
operator|.
name|operand
argument_list|)
decl_stmt|;
name|parentSchema
operator|.
name|addSchema
argument_list|(
name|jsonSchema
operator|.
name|name
argument_list|,
name|schema
argument_list|)
expr_stmt|;
if|if
condition|(
name|schema
operator|instanceof
name|MapSchema
condition|)
block|{
operator|(
operator|(
name|MapSchema
operator|)
name|schema
operator|)
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
name|populateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|schema
argument_list|)
expr_stmt|;
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
literal|"Error instantiating "
operator|+
name|jsonSchema
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonJdbcSchema
name|jsonSchema
parameter_list|)
block|{
name|JdbcSchema
name|schema
init|=
name|JdbcSchema
operator|.
name|create
argument_list|(
name|currentMutableSchema
argument_list|(
literal|"jdbc schema"
argument_list|)
argument_list|,
name|dataSource
argument_list|(
name|jsonSchema
argument_list|)
argument_list|,
name|jsonSchema
operator|.
name|jdbcCatalog
argument_list|,
name|jsonSchema
operator|.
name|jdbcSchema
argument_list|,
name|jsonSchema
operator|.
name|name
argument_list|)
decl_stmt|;
name|populateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonMaterialization
name|jsonMaterialization
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Schema
name|schema
init|=
name|currentSchema
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|schema
operator|instanceof
name|MutableSchema
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot define materialization; parent schema '"
operator|+
name|currentSchemaName
argument_list|()
operator|+
literal|"' is not a SemiMutableSchema"
argument_list|)
throw|;
block|}
specifier|final
name|MutableSchema
name|mutableSchema
init|=
operator|(
name|MutableSchema
operator|)
name|schema
decl_stmt|;
name|Schema
operator|.
name|TableFunctionInSchema
name|tableFunctionInSchema
init|=
name|MaterializedViewTable
operator|.
name|create
argument_list|(
name|schema
argument_list|,
name|jsonMaterialization
operator|.
name|view
argument_list|,
name|jsonMaterialization
operator|.
name|sql
argument_list|,
literal|null
argument_list|,
name|jsonMaterialization
operator|.
name|table
argument_list|)
decl_stmt|;
name|mutableSchema
operator|.
name|addTableFunction
argument_list|(
name|tableFunctionInSchema
argument_list|)
expr_stmt|;
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
literal|"Error instantiating "
operator|+
name|jsonMaterialization
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|DataSource
name|dataSource
parameter_list|(
name|JsonJdbcSchema
name|jsonJdbcSchema
parameter_list|)
block|{
name|BasicDataSource
name|dataSource
init|=
operator|new
name|BasicDataSource
argument_list|()
decl_stmt|;
name|dataSource
operator|.
name|setUrl
argument_list|(
name|jsonJdbcSchema
operator|.
name|jdbcUrl
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setUsername
argument_list|(
name|jsonJdbcSchema
operator|.
name|jdbcUser
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setPassword
argument_list|(
name|jsonJdbcSchema
operator|.
name|jdbcPassword
argument_list|)
expr_stmt|;
return|return
name|dataSource
return|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonCustomTable
name|jsonTable
parameter_list|)
block|{
try|try
block|{
specifier|final
name|MutableSchema
name|schema
init|=
name|currentMutableSchema
argument_list|(
literal|"table"
argument_list|)
decl_stmt|;
specifier|final
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|jsonTable
operator|.
name|factory
argument_list|)
decl_stmt|;
specifier|final
name|TableFactory
name|tableFactory
init|=
operator|(
name|TableFactory
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|tableFactory
operator|.
name|create
argument_list|(
name|schema
argument_list|,
name|jsonTable
operator|.
name|name
argument_list|,
name|jsonTable
operator|.
name|operand
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|addTable
argument_list|(
operator|new
name|TableInSchemaImpl
argument_list|(
name|schema
argument_list|,
name|jsonTable
operator|.
name|name
argument_list|,
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
argument_list|,
name|table
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Error instantiating "
operator|+
name|jsonTable
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonView
name|jsonView
parameter_list|)
block|{
try|try
block|{
specifier|final
name|MutableSchema
name|schema
init|=
name|currentMutableSchema
argument_list|(
literal|"view"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
init|=
name|jsonView
operator|.
name|path
operator|==
literal|null
condition|?
name|currentSchemaPath
argument_list|()
else|:
name|jsonView
operator|.
name|path
decl_stmt|;
name|schema
operator|.
name|addTableFunction
argument_list|(
name|ViewTable
operator|.
name|viewFunction
argument_list|(
name|schema
argument_list|,
name|jsonView
operator|.
name|name
argument_list|,
name|jsonView
operator|.
name|sql
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Error instantiating "
operator|+
name|jsonView
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|currentSchemaPath
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|peek
argument_list|(
name|schemaStack
argument_list|)
operator|.
name|left
argument_list|)
return|;
block|}
specifier|private
name|Schema
name|currentSchema
parameter_list|()
block|{
return|return
name|peek
argument_list|(
name|schemaStack
argument_list|)
operator|.
name|right
return|;
block|}
specifier|private
name|String
name|currentSchemaName
parameter_list|()
block|{
return|return
name|peek
argument_list|(
name|schemaStack
argument_list|)
operator|.
name|left
return|;
block|}
specifier|private
name|MutableSchema
name|currentMutableSchema
parameter_list|(
name|String
name|elementType
parameter_list|)
block|{
specifier|final
name|Schema
name|schema
init|=
name|currentSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|instanceof
name|MutableSchema
condition|)
block|{
return|return
operator|(
name|MutableSchema
operator|)
name|schema
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot define "
operator|+
name|elementType
operator|+
literal|"; parent schema "
operator|+
name|schema
operator|+
literal|" is not mutable"
argument_list|)
throw|;
block|}
block|}
end_class

begin_comment
comment|// End ModelHandler.java
end_comment

end_unit

