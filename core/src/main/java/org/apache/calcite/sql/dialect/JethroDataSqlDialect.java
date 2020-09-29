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
name|sql
operator|.
name|dialect
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
name|sql
operator|.
name|SqlDialect
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
name|SqlKind
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
name|SqlOperator
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
name|type
operator|.
name|SqlTypeName
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSetMultimap
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
name|LinkedHashMultimap
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
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Locale
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDialect</code> implementation for the JethroData database.  */
end_comment

begin_class
specifier|public
class|class
name|JethroDataSqlDialect
extends|extends
name|SqlDialect
block|{
specifier|private
specifier|final
name|JethroInfo
name|info
decl_stmt|;
comment|/** Creates a JethroDataSqlDialect. */
specifier|public
name|JethroDataSqlDialect
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|context
operator|.
name|jethroInfo
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsCharSet
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlNode
name|emulateNullDirection
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|boolean
name|nullsFirst
parameter_list|,
name|boolean
name|desc
parameter_list|)
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsAggregateFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|COUNT
case|:
case|case
name|SUM
case|:
case|case
name|AVG
case|:
case|case
name|MIN
case|:
case|case
name|MAX
case|:
case|case
name|STDDEV_POP
case|:
case|case
name|STDDEV_SAMP
case|:
case|case
name|VAR_POP
case|:
case|case
name|VAR_SAMP
case|:
return|return
literal|true
return|;
default|default:
break|break;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsFunction
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|)
block|{
switch|switch
condition|(
name|operator
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|IS_NOT_NULL
case|:
case|case
name|IS_NULL
case|:
case|case
name|AND
case|:
case|case
name|OR
case|:
case|case
name|NOT
case|:
case|case
name|BETWEEN
case|:
case|case
name|CASE
case|:
case|case
name|CAST
case|:
return|return
literal|true
return|;
block|}
specifier|final
name|Set
argument_list|<
name|JethroSupportedFunction
argument_list|>
name|functions
init|=
name|info
operator|.
name|supportedFunctions
operator|.
name|get
argument_list|(
name|operator
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|functions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|JethroSupportedFunction
name|f
range|:
name|functions
control|)
block|{
if|if
condition|(
name|f
operator|.
name|argumentsMatch
argument_list|(
name|paramTypes
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Unsupported function in jethro: "
operator|+
name|operator
operator|+
literal|" with params "
operator|+
name|paramTypes
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|boolean
name|supportsOffsetFetch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsNestedAggregations
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
specifier|static
name|JethroInfoCache
name|createCache
parameter_list|()
block|{
return|return
operator|new
name|JethroInfoCacheImpl
argument_list|()
return|;
block|}
comment|/** Information about a function supported by Jethro. */
specifier|static
class|class
name|JethroSupportedFunction
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|SqlTypeName
argument_list|>
name|operandTypes
decl_stmt|;
name|JethroSupportedFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|operands
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// not currently used
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|SqlTypeName
argument_list|>
name|b
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|strType
range|:
name|operands
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
control|)
block|{
name|b
operator|.
name|add
argument_list|(
name|parse
argument_list|(
name|strType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|operandTypes
operator|=
name|b
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|private
name|SqlTypeName
name|parse
parameter_list|(
name|String
name|strType
parameter_list|)
block|{
switch|switch
condition|(
name|strType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
condition|)
block|{
case|case
literal|"bigint"
case|:
case|case
literal|"long"
case|:
return|return
name|SqlTypeName
operator|.
name|BIGINT
return|;
case|case
literal|"integer"
case|:
case|case
literal|"int"
case|:
return|return
name|SqlTypeName
operator|.
name|INTEGER
return|;
case|case
literal|"double"
case|:
return|return
name|SqlTypeName
operator|.
name|DOUBLE
return|;
case|case
literal|"float"
case|:
return|return
name|SqlTypeName
operator|.
name|FLOAT
return|;
case|case
literal|"string"
case|:
return|return
name|SqlTypeName
operator|.
name|VARCHAR
return|;
case|case
literal|"timestamp"
case|:
return|return
name|SqlTypeName
operator|.
name|TIMESTAMP
return|;
default|default:
return|return
name|SqlTypeName
operator|.
name|ANY
return|;
block|}
block|}
name|boolean
name|argumentsMatch
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|)
block|{
if|if
condition|(
name|paramTypes
operator|.
name|size
argument_list|()
operator|!=
name|operandTypes
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
name|paramTypes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|paramTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getSqlTypeName
argument_list|()
operator|!=
name|operandTypes
operator|.
name|get
argument_list|(
name|i
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
block|}
comment|/** Stores information about capabilities of Jethro databases. */
specifier|public
interface|interface
name|JethroInfoCache
block|{
name|JethroInfo
name|get
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
function_decl|;
block|}
comment|/** Implementation of {@code JethroInfoCache}. */
specifier|private
specifier|static
class|class
name|JethroInfoCacheImpl
implements|implements
name|JethroInfoCache
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|JethroInfo
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|JethroInfo
name|get
parameter_list|(
specifier|final
name|DatabaseMetaData
name|metaData
parameter_list|)
block|{
try|try
block|{
assert|assert
literal|"JethroData"
operator|.
name|equals
argument_list|(
name|metaData
operator|.
name|getDatabaseProductName
argument_list|()
argument_list|)
assert|;
name|String
name|productVersion
init|=
name|metaData
operator|.
name|getDatabaseProductVersion
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|JethroInfoCacheImpl
operator|.
name|this
init|)
block|{
name|JethroInfo
name|info
init|=
name|map
operator|.
name|get
argument_list|(
name|productVersion
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
specifier|final
name|Connection
name|c
init|=
name|metaData
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|info
operator|=
name|makeInfo
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|productVersion
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|error
argument_list|(
literal|"Failed to create JethroDataDialect"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create JethroDataDialect"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|JethroInfo
name|makeInfo
parameter_list|(
name|Connection
name|jethroConnection
parameter_list|)
block|{
try|try
init|(
name|Statement
name|jethroStatement
init|=
name|jethroConnection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|functionsTupleSet
init|=
name|jethroStatement
operator|.
name|executeQuery
argument_list|(
literal|"show functions extended"
argument_list|)
init|)
block|{
specifier|final
name|Multimap
argument_list|<
name|String
argument_list|,
name|JethroSupportedFunction
argument_list|>
name|supportedFunctions
init|=
name|LinkedHashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
while|while
condition|(
name|functionsTupleSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|String
name|functionName
init|=
name|functionsTupleSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|operandsType
init|=
name|functionsTupleSet
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|supportedFunctions
operator|.
name|put
argument_list|(
name|functionName
argument_list|,
operator|new
name|JethroSupportedFunction
argument_list|(
name|functionName
argument_list|,
name|operandsType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|JethroInfo
argument_list|(
name|supportedFunctions
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Jethro server failed to execute 'show functions extended'"
decl_stmt|;
name|LOGGER
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|msg
operator|+
literal|"; make sure your Jethro server is up to date"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Information about the capabilities of a Jethro database. */
specifier|public
specifier|static
class|class
name|JethroInfo
block|{
specifier|public
specifier|static
specifier|final
name|JethroInfo
name|EMPTY
init|=
operator|new
name|JethroInfo
argument_list|(
name|ImmutableSetMultimap
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ImmutableSetMultimap
argument_list|<
name|String
argument_list|,
name|JethroSupportedFunction
argument_list|>
name|supportedFunctions
decl_stmt|;
specifier|public
name|JethroInfo
parameter_list|(
name|Multimap
argument_list|<
name|String
argument_list|,
name|JethroSupportedFunction
argument_list|>
name|supportedFunctions
parameter_list|)
block|{
name|this
operator|.
name|supportedFunctions
operator|=
name|ImmutableSetMultimap
operator|.
name|copyOf
argument_list|(
name|supportedFunctions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

