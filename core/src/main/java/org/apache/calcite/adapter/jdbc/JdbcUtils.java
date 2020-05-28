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
name|adapter
operator|.
name|jdbc
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
name|avatica
operator|.
name|ColumnMetaData
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
name|avatica
operator|.
name|util
operator|.
name|DateTimeUtils
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
name|Function0
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
name|Function1
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
name|SqlDialectFactory
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
name|ImmutableNullableList
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
name|Pair
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
name|dbcp2
operator|.
name|BasicDataSource
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|primitives
operator|.
name|Ints
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
name|PolyNull
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
name|Date
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
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
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
name|TimeZone
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
comment|/** Pool of dialects. */
specifier|static
class|class
name|DialectPool
block|{
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
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Pair
argument_list|<
name|SqlDialectFactory
argument_list|,
name|DataSource
argument_list|>
argument_list|,
name|SqlDialect
argument_list|>
name|cache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|softValues
argument_list|()
operator|.
name|build
argument_list|(
name|CacheLoader
operator|.
name|from
argument_list|(
name|DialectPool
operator|::
name|dialect
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|SqlDialect
name|dialect
parameter_list|(
name|Pair
argument_list|<
name|SqlDialectFactory
argument_list|,
name|DataSource
argument_list|>
name|key
parameter_list|)
block|{
name|SqlDialectFactory
name|dialectFactory
init|=
name|key
operator|.
name|left
decl_stmt|;
name|DataSource
name|dataSource
init|=
name|key
operator|.
name|right
decl_stmt|;
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
name|SqlDialect
name|dialect
init|=
name|dialectFactory
operator|.
name|create
argument_list|(
name|metaData
argument_list|)
decl_stmt|;
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
specifier|public
name|SqlDialect
name|get
parameter_list|(
name|SqlDialectFactory
name|dialectFactory
parameter_list|,
name|DataSource
name|dataSource
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|SqlDialectFactory
argument_list|,
name|DataSource
argument_list|>
name|key
init|=
name|Pair
operator|.
name|of
argument_list|(
name|dialectFactory
argument_list|,
name|dataSource
argument_list|)
decl_stmt|;
return|return
name|cache
operator|.
name|getUnchecked
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/** Builder that calls {@link ResultSet#getObject(int)} for every column,    * or {@code getXxx} if the result type is a primitive {@code xxx},    * and returns an array of objects for each row. */
specifier|static
class|class
name|ObjectArrayRowBuilder
implements|implements
name|Function0
argument_list|<
annotation|@
name|Nullable
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
name|ColumnMetaData
operator|.
name|Rep
index|[]
name|reps
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|types
decl_stmt|;
name|ObjectArrayRowBuilder
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|ColumnMetaData
operator|.
name|Rep
index|[]
name|reps
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
name|reps
operator|=
name|reps
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
annotation|@
name|Nullable
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
name|ColumnMetaData
operator|.
name|Rep
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|resultSet
lambda|->
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
name|ColumnMetaData
operator|.
name|Rep
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|Ints
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
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|Object
index|[]
name|apply
parameter_list|()
block|{
try|try
block|{
specifier|final
annotation|@
name|Nullable
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
annotation|@
name|Nullable
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
default|default:
break|break;
block|}
return|return
name|reps
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
comment|/** Returns a timestamp shifted by the default time-zone's offset;      * null if and only if {@code v} is null. */
specifier|private
specifier|static
annotation|@
name|PolyNull
name|Timestamp
name|shift
parameter_list|(
annotation|@
name|PolyNull
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
comment|/** Returns a time shifted by the default time-zone's offset;      * null if and only if {@code v} is null. */
specifier|private
specifier|static
annotation|@
name|PolyNull
name|Time
name|shift
parameter_list|(
annotation|@
name|PolyNull
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
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
argument_list|)
return|;
block|}
comment|/** Returns a date shifted by the default time-zone's offset;      * null if and only if {@code v} is null. */
specifier|private
specifier|static
annotation|@
name|PolyNull
name|Date
name|shift
parameter_list|(
annotation|@
name|PolyNull
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
comment|/** Ensures that if two data sources have the same definition, they will use    * the same object.    *    *<p>This in turn makes it easier to cache    * {@link org.apache.calcite.sql.SqlDialect} objects. Otherwise, each time we    * see a new data source, we have to open a connection to find out what    * database product and version it is. */
specifier|static
class|class
name|DataSourcePool
block|{
specifier|public
specifier|static
specifier|final
name|DataSourcePool
name|INSTANCE
init|=
operator|new
name|DataSourcePool
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|List
argument_list|<
annotation|@
name|Nullable
name|String
argument_list|>
argument_list|,
name|BasicDataSource
argument_list|>
name|cache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|softValues
argument_list|()
operator|.
name|build
argument_list|(
name|CacheLoader
operator|.
name|from
argument_list|(
name|DataSourcePool
operator|::
name|dataSource
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|BasicDataSource
name|dataSource
argument_list|(
name|List
operator|<
condition|?
then|extends @
name|Nullable
name|String
operator|>
name|key
argument_list|)
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
name|key
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setUsername
argument_list|(
name|key
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setPassword
argument_list|(
name|key
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setDriverClassName
argument_list|(
name|key
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|dataSource
return|;
block|}
specifier|public
name|DataSource
name|get
parameter_list|(
name|String
name|url
parameter_list|,
annotation|@
name|Nullable
name|String
name|driverClassName
parameter_list|,
annotation|@
name|Nullable
name|String
name|username
parameter_list|,
annotation|@
name|Nullable
name|String
name|password
parameter_list|)
block|{
comment|// Get data source objects from a cache, so that we don't have to sniff
comment|// out what kind of database they are quite as often.
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|String
argument_list|>
name|key
init|=
name|ImmutableNullableList
operator|.
name|of
argument_list|(
name|url
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|driverClassName
argument_list|)
decl_stmt|;
return|return
name|cache
operator|.
name|getUnchecked
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

