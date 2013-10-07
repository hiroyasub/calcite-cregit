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
name|impl
operator|.
name|jdbc
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
name|expressions
operator|.
name|Primitive
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
name|function
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
name|sql
operator|.
name|SqlDialect
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
name|IntList
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
name|util14
operator|.
name|DateTimeUtil
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
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
name|Date
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

begin_comment
comment|/**  * Utilities for the JDBC provider.  */
end_comment

begin_class
specifier|final
class|class
name|JdbcUtils
block|{
specifier|private
name|JdbcUtils
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
class|class
name|DialectPool
block|{
specifier|final
name|Map
argument_list|<
name|List
argument_list|,
name|SqlDialect
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|List
argument_list|,
name|SqlDialect
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DialectPool
name|INSTANCE
init|=
operator|new
name|DialectPool
argument_list|()
decl_stmt|;
name|SqlDialect
name|get
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|dataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|String
name|productName
init|=
name|metaData
operator|.
name|getDatabaseProductName
argument_list|()
decl_stmt|;
name|String
name|productVersion
init|=
name|metaData
operator|.
name|getDatabaseProductVersion
argument_list|()
decl_stmt|;
name|List
name|key
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|productName
argument_list|,
name|productVersion
argument_list|)
decl_stmt|;
name|SqlDialect
name|dialect
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|dialect
operator|==
literal|null
condition|)
block|{
name|dialect
operator|=
operator|new
name|SqlDialect
argument_list|(
name|SqlDialect
operator|.
name|getProduct
argument_list|(
name|productName
argument_list|,
name|productVersion
argument_list|)
argument_list|,
name|productName
argument_list|,
name|metaData
operator|.
name|getIdentifierQuoteString
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|dialect
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
return|return
name|dialect
return|;
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
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
block|}
comment|/** Builder that calls {@link ResultSet#getObject(int)} for every column,    * or {@code getXxx} if the result type is a primitive {@code xxx},    * and returns an array of objects for each row. */
specifier|public
specifier|static
class|class
name|ObjectArrayRowBuilder
implements|implements
name|Function0
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|ResultSet
name|resultSet
decl_stmt|;
specifier|private
specifier|final
name|int
name|columnCount
decl_stmt|;
specifier|private
specifier|final
name|Primitive
index|[]
name|primitives
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|types
decl_stmt|;
specifier|public
name|ObjectArrayRowBuilder
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|Primitive
index|[]
name|primitives
parameter_list|,
name|int
index|[]
name|types
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|resultSet
operator|=
name|resultSet
expr_stmt|;
name|this
operator|.
name|primitives
operator|=
name|primitives
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
name|this
operator|.
name|columnCount
operator|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|>
name|factory
parameter_list|(
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|Primitive
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Function0
argument_list|<
name|Object
index|[]
argument_list|>
name|apply
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|ObjectArrayRowBuilder
argument_list|(
name|resultSet
argument_list|,
name|Pair
operator|.
name|left
argument_list|(
name|list
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|Primitive
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|IntList
operator|.
name|toArray
argument_list|(
name|Pair
operator|.
name|right
argument_list|(
name|list
argument_list|)
argument_list|)
argument_list|)
return|;
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
return|;
block|}
specifier|public
name|Object
index|[]
name|apply
parameter_list|()
block|{
try|try
block|{
specifier|final
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|columnCount
index|]
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
name|columnCount
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|value
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
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
comment|/**      * Gets a value from a given column in a JDBC result set.      *      * @param i Ordinal of column (1-based, per JDBC)      */
specifier|private
name|Object
name|value
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// MySQL returns timestamps shifted into local time. Using
comment|// getTimestamp(int, Calendar) with a UTC calendar should prevent this,
comment|// but does not. So we shift explicitly.
switch|switch
condition|(
name|types
index|[
name|i
index|]
condition|)
block|{
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
return|return
name|shift
argument_list|(
name|resultSet
operator|.
name|getTimestamp
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
return|;
case|case
name|Types
operator|.
name|TIME
case|:
return|return
name|shift
argument_list|(
name|resultSet
operator|.
name|getTime
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
return|;
case|case
name|Types
operator|.
name|DATE
case|:
return|return
name|shift
argument_list|(
name|resultSet
operator|.
name|getDate
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
return|return
name|primitives
index|[
name|i
index|]
operator|.
name|jdbcGet
argument_list|(
name|resultSet
argument_list|,
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Timestamp
name|shift
parameter_list|(
name|Timestamp
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|time
init|=
name|v
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
decl_stmt|;
return|return
operator|new
name|Timestamp
argument_list|(
name|time
operator|+
name|offset
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Time
name|shift
parameter_list|(
name|Time
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|time
init|=
name|v
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
decl_stmt|;
return|return
operator|new
name|Time
argument_list|(
operator|(
name|time
operator|+
name|offset
operator|)
operator|%
name|DateTimeUtil
operator|.
name|MILLIS_PER_DAY
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Date
name|shift
parameter_list|(
name|Date
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|time
init|=
name|v
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
decl_stmt|;
return|return
operator|new
name|Date
argument_list|(
name|time
operator|+
name|offset
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JdbcUtils.java
end_comment

end_unit

