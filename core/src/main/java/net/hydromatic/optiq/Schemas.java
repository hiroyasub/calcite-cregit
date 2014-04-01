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
name|linq4j
operator|.
name|expressions
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
name|config
operator|.
name|OptiqConnectionConfig
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
name|reltype
operator|.
name|RelDataType
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
name|RelDataTypeFactory
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
name|RelProtoDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeUtil
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
name|ImmutableMap
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
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

begin_comment
comment|/**  * Utility functions for schemas.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Schemas
block|{
specifier|private
name|Schemas
parameter_list|()
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"no instances!"
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|OptiqSchema
operator|.
name|FunctionEntry
name|resolve
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|OptiqSchema
operator|.
name|FunctionEntry
argument_list|>
name|functionEntries
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argumentTypes
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|OptiqSchema
operator|.
name|FunctionEntry
argument_list|>
name|matches
init|=
operator|new
name|ArrayList
argument_list|<
name|OptiqSchema
operator|.
name|FunctionEntry
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|OptiqSchema
operator|.
name|FunctionEntry
name|entry
range|:
name|functionEntries
control|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|typeFactory
argument_list|,
name|entry
operator|.
name|getFunction
argument_list|()
argument_list|,
name|argumentTypes
argument_list|)
condition|)
block|{
name|matches
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|matches
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|null
return|;
case|case
literal|1
case|:
return|return
name|matches
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"More than one match for "
operator|+
name|name
operator|+
literal|" with arguments "
operator|+
name|argumentTypes
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|matches
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|Function
name|member
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argumentTypes
parameter_list|)
block|{
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|parameters
init|=
name|member
operator|.
name|getParameters
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameters
operator|.
name|size
argument_list|()
operator|!=
name|argumentTypes
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|argumentTypes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataType
name|argumentType
init|=
name|argumentTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|FunctionParameter
name|parameter
init|=
name|parameters
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|canConvert
argument_list|(
name|argumentType
argument_list|,
name|parameter
operator|.
name|getType
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|boolean
name|canConvert
parameter_list|(
name|RelDataType
name|fromType
parameter_list|,
name|RelDataType
name|toType
parameter_list|)
block|{
return|return
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|toType
argument_list|,
name|fromType
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|TableMacro
name|methodMember
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<
name|FunctionParameter
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|parameterType
range|:
name|method
operator|.
name|getParameterTypes
argument_list|()
control|)
block|{
name|parameters
operator|.
name|add
argument_list|(
operator|new
name|FunctionParameter
argument_list|()
block|{
specifier|final
name|int
name|ordinal
init|=
name|parameters
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|public
name|int
name|getOrdinal
parameter_list|()
block|{
return|return
name|ordinal
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"a"
operator|+
name|ordinal
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|createType
argument_list|(
name|parameterType
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TableMacro
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
specifier|public
name|Table
name|apply
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
try|try
block|{
name|Object
name|o
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|o
operator|=
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
comment|//noinspection unchecked
return|return
operator|(
name|Table
operator|)
name|method
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
name|arguments
operator|.
name|toArray
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Expected "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
operator|+
literal|" actual "
operator|+
name|arguments
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
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
catch|catch
parameter_list|(
name|InvocationTargetException
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
catch|catch
parameter_list|(
name|InstantiationException
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
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|returnType
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|createType
argument_list|(
name|returnType
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Returns the expression for a schema. */
specifier|public
specifier|static
name|Expression
name|expression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getExpression
argument_list|(
name|schema
operator|.
name|getParentSchema
argument_list|()
argument_list|,
name|schema
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the expression for a sub-schema. */
specifier|public
specifier|static
name|Expression
name|subSchemaExpression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Class
name|type
parameter_list|)
block|{
comment|// (Type) schemaExpression.getSubSchema("name")
specifier|final
name|Expression
name|schemaExpression
init|=
name|expression
argument_list|(
name|schema
argument_list|)
decl_stmt|;
name|Expression
name|call
init|=
name|Expressions
operator|.
name|call
argument_list|(
name|schemaExpression
argument_list|,
name|BuiltinMethod
operator|.
name|SCHEMA_GET_SUB_SCHEMA
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
comment|//CHECKSTYLE: IGNORE 2
comment|//noinspection unchecked
if|if
condition|(
literal|false
operator|&&
name|type
operator|!=
literal|null
operator|&&
operator|!
name|type
operator|.
name|isAssignableFrom
argument_list|(
name|Schema
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|unwrap
argument_list|(
name|call
argument_list|,
name|type
argument_list|)
return|;
block|}
return|return
name|call
return|;
block|}
comment|/** Converts a schema expression to a given type by calling the    * {@link net.hydromatic.optiq.SchemaPlus#unwrap(Class)} method. */
specifier|public
specifier|static
name|Expression
name|unwrap
parameter_list|(
name|Expression
name|call
parameter_list|,
name|Class
name|type
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|call
argument_list|,
name|BuiltinMethod
operator|.
name|SCHEMA_PLUS_UNWRAP
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|type
argument_list|)
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|/** Returns the expression to access a table within a schema. */
specifier|public
specifier|static
name|Expression
name|tableExpression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|String
name|tableName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
specifier|final
name|MethodCallExpression
name|expression
decl_stmt|;
if|if
condition|(
name|Table
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|expression
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|expression
argument_list|(
name|schema
argument_list|)
argument_list|,
name|BuiltinMethod
operator|.
name|SCHEMA_GET_TABLE
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|tableName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltinMethod
operator|.
name|SCHEMAS_QUERYABLE
operator|.
name|method
argument_list|,
name|DataContext
operator|.
name|ROOT
argument_list|,
name|expression
argument_list|(
name|schema
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|elementType
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|tableName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|clazz
argument_list|,
name|expression
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DataContext
name|createDataContext
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|DummyDataContext
argument_list|(
operator|(
name|OptiqConnection
operator|)
name|connection
argument_list|)
return|;
block|}
comment|/** Returns a {@link Queryable}, given a fully-qualified table name. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Queryable
argument_list|<
name|E
argument_list|>
name|queryable
parameter_list|(
name|DataContext
name|root
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
parameter_list|,
name|String
modifier|...
name|names
parameter_list|)
block|{
name|SchemaPlus
name|schema
init|=
name|root
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|names
index|[
name|i
index|]
decl_stmt|;
name|schema
operator|=
name|schema
operator|.
name|getSubSchema
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|tableName
init|=
name|names
index|[
name|names
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
return|return
name|queryable
argument_list|(
name|root
argument_list|,
name|schema
argument_list|,
name|clazz
argument_list|,
name|tableName
argument_list|)
return|;
block|}
comment|/** Returns a {@link Queryable}, given a schema and table name. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Queryable
argument_list|<
name|E
argument_list|>
name|queryable
parameter_list|(
name|DataContext
name|root
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|QueryableTable
name|table
init|=
operator|(
name|QueryableTable
operator|)
name|schema
operator|.
name|getTable
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
return|return
name|table
operator|.
name|asQueryable
argument_list|(
name|root
operator|.
name|getQueryProvider
argument_list|()
argument_list|,
name|schema
argument_list|,
name|tableName
argument_list|)
return|;
block|}
comment|/** Parses and validates a SQL query. For use within Optiq only. */
specifier|public
specifier|static
name|OptiqPrepare
operator|.
name|ParseResult
name|parse
parameter_list|(
specifier|final
name|OptiqConnection
name|connection
parameter_list|,
specifier|final
name|OptiqSchema
name|schema
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
specifier|final
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|OptiqPrepare
name|prepare
init|=
name|OptiqPrepare
operator|.
name|DEFAULT_FACTORY
operator|.
name|apply
argument_list|()
decl_stmt|;
specifier|final
name|OptiqPrepare
operator|.
name|Context
name|context
init|=
name|makeContext
argument_list|(
name|connection
argument_list|,
name|schema
argument_list|,
name|schemaPath
argument_list|)
decl_stmt|;
name|OptiqPrepare
operator|.
name|Dummy
operator|.
name|push
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|prepare
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|sql
argument_list|)
return|;
block|}
finally|finally
block|{
name|OptiqPrepare
operator|.
name|Dummy
operator|.
name|pop
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Prepares a SQL query for execution. For use within Optiq only. */
specifier|public
specifier|static
name|OptiqPrepare
operator|.
name|PrepareResult
argument_list|<
name|Object
argument_list|>
name|prepare
parameter_list|(
specifier|final
name|OptiqConnection
name|connection
parameter_list|,
specifier|final
name|OptiqSchema
name|schema
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
specifier|final
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|OptiqPrepare
name|prepare
init|=
name|OptiqPrepare
operator|.
name|DEFAULT_FACTORY
operator|.
name|apply
argument_list|()
decl_stmt|;
return|return
name|prepare
operator|.
name|prepareSql
argument_list|(
name|makeContext
argument_list|(
name|connection
argument_list|,
name|schema
argument_list|,
name|schemaPath
argument_list|)
argument_list|,
name|sql
argument_list|,
literal|null
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|OptiqPrepare
operator|.
name|Context
name|makeContext
parameter_list|(
specifier|final
name|OptiqConnection
name|connection
parameter_list|,
specifier|final
name|OptiqSchema
name|schema
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
specifier|final
name|OptiqPrepare
operator|.
name|Context
name|context0
init|=
name|OptiqPrepare
operator|.
name|Dummy
operator|.
name|peek
argument_list|()
decl_stmt|;
return|return
name|makeContext
argument_list|(
name|context0
operator|.
name|config
argument_list|()
argument_list|,
name|context0
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|context0
operator|.
name|getDataContext
argument_list|()
argument_list|,
name|schema
argument_list|,
name|schemaPath
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|makeContext
argument_list|(
name|connection
operator|.
name|config
argument_list|()
argument_list|,
name|connection
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|createDataContext
argument_list|(
name|connection
argument_list|)
argument_list|,
name|schema
argument_list|,
name|schemaPath
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|OptiqPrepare
operator|.
name|Context
name|makeContext
parameter_list|(
specifier|final
name|OptiqConnectionConfig
name|connectionConfig
parameter_list|,
specifier|final
name|JavaTypeFactory
name|typeFactory
parameter_list|,
specifier|final
name|DataContext
name|dataContext
parameter_list|,
specifier|final
name|OptiqSchema
name|schema
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
name|OptiqPrepare
operator|.
name|Context
argument_list|()
block|{
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactory
return|;
block|}
specifier|public
name|OptiqRootSchema
name|getRootSchema
parameter_list|()
block|{
return|return
name|schema
operator|.
name|root
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultSchemaPath
parameter_list|()
block|{
comment|// schemaPath is usually null. If specified, it overrides schema
comment|// as the context within which the SQL is validated.
if|if
condition|(
name|schemaPath
operator|==
literal|null
condition|)
block|{
return|return
name|schema
operator|.
name|path
argument_list|(
literal|null
argument_list|)
return|;
block|}
return|return
name|schemaPath
return|;
block|}
specifier|public
name|OptiqConnectionConfig
name|config
parameter_list|()
block|{
return|return
name|connectionConfig
return|;
block|}
specifier|public
name|DataContext
name|getDataContext
parameter_list|()
block|{
return|return
name|dataContext
return|;
block|}
specifier|public
name|OptiqPrepare
operator|.
name|SparkHandler
name|spark
parameter_list|()
block|{
specifier|final
name|boolean
name|enable
init|=
name|config
argument_list|()
operator|.
name|spark
argument_list|()
decl_stmt|;
return|return
name|OptiqPrepare
operator|.
name|Dummy
operator|.
name|getSparkHandler
argument_list|(
name|enable
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Returns an implementation of    * {@link RelProtoDataType}    * that asks a given table for its row type with a given type factory. */
specifier|public
specifier|static
name|RelProtoDataType
name|proto
parameter_list|(
specifier|final
name|Table
name|table
parameter_list|)
block|{
return|return
operator|new
name|RelProtoDataType
argument_list|()
block|{
specifier|public
name|RelDataType
name|apply
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|table
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Dummy data context that has no variables. */
specifier|private
specifier|static
class|class
name|DummyDataContext
implements|implements
name|DataContext
block|{
specifier|private
specifier|final
name|OptiqConnection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
specifier|public
name|DummyDataContext
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|Object
operator|>
name|of
argument_list|(
literal|"timeZone"
argument_list|,
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getRootSchema
argument_list|()
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getTypeFactory
argument_list|()
return|;
block|}
specifier|public
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
return|return
name|connection
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
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Schemas.java
end_comment

end_unit

