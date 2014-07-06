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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqSchema
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
name|SchemaPlus
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
name|SchemaPlus
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
name|mapper
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_COMMENTS
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
comment|/** Creates and validates a ScalarFunctionImpl. */
specifier|public
specifier|static
name|void
name|create
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|functionName
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|,
name|String
name|className
parameter_list|,
name|String
name|methodName
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"UDF class '"
operator|+
name|className
operator|+
literal|"' not found"
argument_list|)
throw|;
block|}
comment|// Must look for TableMacro before ScalarFunction. Both have an "eval"
comment|// method.
specifier|final
name|TableFunction
name|tableFunction
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|tableFunction
operator|!=
literal|null
condition|)
block|{
name|schema
operator|.
name|add
argument_list|(
name|functionName
argument_list|,
name|tableFunction
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|TableMacro
name|macro
init|=
name|TableMacroImpl
operator|.
name|create
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|macro
operator|!=
literal|null
condition|)
block|{
name|schema
operator|.
name|add
argument_list|(
name|functionName
argument_list|,
name|macro
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|methodName
operator|!=
literal|null
operator|&&
name|methodName
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ScalarFunction
argument_list|>
name|entry
range|:
name|ScalarFunctionImpl
operator|.
name|createAll
argument_list|(
name|clazz
argument_list|)
operator|.
name|entries
argument_list|()
control|)
block|{
name|schema
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
else|else
block|{
specifier|final
name|ScalarFunction
name|function
init|=
name|ScalarFunctionImpl
operator|.
name|create
argument_list|(
name|clazz
argument_list|,
name|Util
operator|.
name|first
argument_list|(
name|methodName
argument_list|,
literal|"eval"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|function
operator|!=
literal|null
condition|)
block|{
name|schema
operator|.
name|add
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|functionName
argument_list|,
name|methodName
argument_list|)
argument_list|,
name|function
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|methodName
operator|==
literal|null
condition|)
block|{
specifier|final
name|AggregateFunction
name|aggFunction
init|=
name|AggregateFunctionImpl
operator|.
name|create
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggFunction
operator|!=
literal|null
condition|)
block|{
name|schema
operator|.
name|add
argument_list|(
name|functionName
argument_list|,
name|aggFunction
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not a valid function class: "
operator|+
name|clazz
operator|+
literal|". Scalar functions and table macros have an 'eval' method; "
operator|+
literal|"aggregate functions have 'init' and 'add' methods, and optionally "
operator|+
literal|"'initAdd', 'merge' and 'result' methods."
argument_list|)
throw|;
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
name|SchemaPlus
argument_list|>
name|pair
init|=
name|Pair
operator|.
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
name|SchemaPlus
name|parentSchema
init|=
name|currentMutableSchema
argument_list|(
literal|"schema"
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|schema
init|=
name|parentSchema
operator|.
name|add
argument_list|(
name|jsonSchema
operator|.
name|name
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|jsonSchema
operator|.
name|path
operator|!=
literal|null
condition|)
block|{
name|schema
operator|.
name|setPath
argument_list|(
name|stringListList
argument_list|(
name|jsonSchema
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|populateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|schema
argument_list|)
expr_stmt|;
if|if
condition|(
name|schema
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"mat"
argument_list|)
condition|)
block|{
comment|// Inject by hand a Star Table. Later we'll add a JSON model element.
specifier|final
name|List
argument_list|<
name|Table
argument_list|>
name|tables
init|=
operator|new
name|ArrayList
argument_list|<
name|Table
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|tableNames
init|=
block|{
literal|"sales_fact_1997"
block|,
literal|"time_by_day"
block|,
literal|"product"
block|,
literal|"product_class"
block|}
decl_stmt|;
specifier|final
name|SchemaPlus
name|schema2
init|=
name|parentSchema
operator|.
name|getSubSchema
argument_list|(
literal|"foodmart"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tableName
range|:
name|tableNames
control|)
block|{
name|tables
operator|.
name|add
argument_list|(
name|schema2
operator|.
name|getTable
argument_list|(
name|tableName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|tableName
init|=
literal|"star"
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
name|tableName
argument_list|,
name|StarTable
operator|.
name|of
argument_list|(
name|tables
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|ImmutableList
argument_list|<
name|ImmutableList
argument_list|<
name|String
argument_list|>
argument_list|>
name|stringListList
parameter_list|(
name|List
name|path
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|ImmutableList
argument_list|<
name|String
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|s
range|:
name|path
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|stringList
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|stringList
parameter_list|(
name|Object
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|instanceof
name|String
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|(
name|String
operator|)
name|s
argument_list|)
return|;
block|}
if|else if
condition|(
name|s
operator|instanceof
name|List
condition|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|builder2
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
operator|(
name|List
operator|)
name|s
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|builder2
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid path element "
operator|+
name|o
operator|+
literal|"; was expecting string"
argument_list|)
throw|;
block|}
block|}
return|return
name|builder2
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid path element "
operator|+
name|s
operator|+
literal|"; was expecting string or list of string"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|populateSchema
parameter_list|(
name|JsonSchema
name|jsonSchema
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|)
block|{
name|boolean
name|cache
init|=
name|jsonSchema
operator|.
name|cache
operator|==
literal|null
operator|||
name|jsonSchema
operator|.
name|cache
decl_stmt|;
name|schema
operator|.
name|setCacheEnabled
argument_list|(
name|cache
argument_list|)
expr_stmt|;
specifier|final
name|Pair
argument_list|<
name|String
argument_list|,
name|SchemaPlus
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
name|SchemaPlus
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
specifier|final
name|SchemaPlus
name|optiqSchema
init|=
name|parentSchema
operator|.
name|add
argument_list|(
name|jsonSchema
operator|.
name|name
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|populateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|optiqSchema
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
specifier|final
name|SchemaPlus
name|parentSchema
init|=
name|currentMutableSchema
argument_list|(
literal|"jdbc schema"
argument_list|)
decl_stmt|;
specifier|final
name|DataSource
name|dataSource
init|=
name|JdbcSchema
operator|.
name|dataSource
argument_list|(
name|jsonSchema
operator|.
name|jdbcUrl
argument_list|,
name|jsonSchema
operator|.
name|jdbcDriver
argument_list|,
name|jsonSchema
operator|.
name|jdbcUser
argument_list|,
name|jsonSchema
operator|.
name|jdbcPassword
argument_list|)
decl_stmt|;
name|JdbcSchema
name|schema
init|=
name|JdbcSchema
operator|.
name|create
argument_list|(
name|parentSchema
argument_list|,
name|jsonSchema
operator|.
name|name
argument_list|,
name|dataSource
argument_list|,
name|jsonSchema
operator|.
name|jdbcCatalog
argument_list|,
name|jsonSchema
operator|.
name|jdbcSchema
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|optiqSchema
init|=
name|parentSchema
operator|.
name|add
argument_list|(
name|jsonSchema
operator|.
name|name
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|populateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|optiqSchema
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
name|SchemaPlus
name|schema
init|=
name|currentSchema
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|schema
operator|.
name|isMutable
argument_list|()
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
name|OptiqSchema
name|optiqSchema
init|=
name|OptiqSchema
operator|.
name|from
argument_list|(
name|schema
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
name|jsonMaterialization
operator|.
name|view
argument_list|,
name|MaterializedViewTable
operator|.
name|create
argument_list|(
name|optiqSchema
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
name|SchemaPlus
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
name|add
argument_list|(
name|jsonTable
operator|.
name|name
argument_list|,
name|table
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
name|SchemaPlus
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
name|Util
operator|.
name|first
argument_list|(
name|jsonView
operator|.
name|path
argument_list|,
name|currentSchemaPath
argument_list|()
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
name|jsonView
operator|.
name|name
argument_list|,
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|schema
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
name|SchemaPlus
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
name|SchemaPlus
name|currentMutableSchema
parameter_list|(
name|String
name|elementType
parameter_list|)
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|currentSchema
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|schema
operator|.
name|isMutable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot define "
operator|+
name|elementType
operator|+
literal|"; parent schema '"
operator|+
name|schema
operator|.
name|getName
argument_list|()
operator|+
literal|"' is not mutable"
argument_list|)
throw|;
block|}
return|return
name|schema
return|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|JsonFunction
name|jsonFunction
parameter_list|)
block|{
try|try
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|currentMutableSchema
argument_list|(
literal|"function"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
init|=
name|Util
operator|.
name|first
argument_list|(
name|jsonFunction
operator|.
name|path
argument_list|,
name|currentSchemaPath
argument_list|()
argument_list|)
decl_stmt|;
name|create
argument_list|(
name|schema
argument_list|,
name|jsonFunction
operator|.
name|name
argument_list|,
name|path
argument_list|,
name|jsonFunction
operator|.
name|className
argument_list|,
name|jsonFunction
operator|.
name|methodName
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
name|jsonFunction
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ModelHandler.java
end_comment

end_unit

