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
name|runtime
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
name|avatica
operator|.
name|SqlType
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
name|linq4j
operator|.
name|tree
operator|.
name|Primitive
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
name|Static
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|NClob
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
name|Ref
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
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|RowId
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
name|SQLFeatureNotSupportedException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLXML
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
name|time
operator|.
name|Instant
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
name|List
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
comment|/**  * Executes a SQL statement and returns the result as an {@link Enumerable}.  *  * @param<T> Element type  */
end_comment

begin_class
specifier|public
class|class
name|ResultSetEnumerable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractEnumerable
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|DataSource
name|dataSource
decl_stmt|;
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
specifier|private
specifier|final
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|T
argument_list|>
argument_list|>
name|rowBuilderFactory
decl_stmt|;
specifier|private
specifier|final
name|PreparedStatementEnricher
name|preparedStatementEnricher
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ResultSetEnumerable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Long
name|queryStart
decl_stmt|;
specifier|private
name|long
name|timeout
decl_stmt|;
specifier|private
name|boolean
name|timeoutSetFailed
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|Object
argument_list|>
argument_list|>
name|AUTO_ROW_BUILDER_FACTORY
init|=
name|resultSet
lambda|->
block|{
specifier|final
name|ResultSetMetaData
name|metaData
decl_stmt|;
specifier|final
name|int
name|columnCount
decl_stmt|;
try|try
block|{
name|metaData
operator|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
expr_stmt|;
name|columnCount
operator|=
name|metaData
operator|.
name|getColumnCount
argument_list|()
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
if|if
condition|(
name|columnCount
operator|==
literal|1
condition|)
block|{
return|return
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
name|resultSet
operator|.
name|getObject
argument_list|(
literal|1
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
else|else
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Function0
operator|)
parameter_list|()
lambda|->
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|columnCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|metaData
operator|.
name|getColumnType
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
name|Types
operator|.
name|TIMESTAMP
condition|)
block|{
name|long
name|v
init|=
name|resultSet
operator|.
name|getLong
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
operator|&&
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|resultSet
operator|.
name|getObject
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|()
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
block|}
decl_stmt|;
specifier|private
name|ResultSetEnumerable
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|String
name|sql
parameter_list|,
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|T
argument_list|>
argument_list|>
name|rowBuilderFactory
parameter_list|,
name|PreparedStatementEnricher
name|preparedStatementEnricher
parameter_list|)
block|{
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|rowBuilderFactory
operator|=
name|rowBuilderFactory
expr_stmt|;
name|this
operator|.
name|preparedStatementEnricher
operator|=
name|preparedStatementEnricher
expr_stmt|;
block|}
specifier|private
name|ResultSetEnumerable
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|String
name|sql
parameter_list|,
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|T
argument_list|>
argument_list|>
name|rowBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|dataSource
argument_list|,
name|sql
argument_list|,
name|rowBuilderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an ResultSetEnumerable. */
specifier|public
specifier|static
name|ResultSetEnumerable
argument_list|<
name|Object
argument_list|>
name|of
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|dataSource
argument_list|,
name|sql
argument_list|,
name|AUTO_ROW_BUILDER_FACTORY
argument_list|)
return|;
block|}
comment|/** Creates an ResultSetEnumerable that retrieves columns as specific    * Java types. */
specifier|public
specifier|static
name|ResultSetEnumerable
argument_list|<
name|Object
argument_list|>
name|of
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|String
name|sql
parameter_list|,
name|Primitive
index|[]
name|primitives
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|dataSource
argument_list|,
name|sql
argument_list|,
name|primitiveRowBuilderFactory
argument_list|(
name|primitives
argument_list|)
argument_list|)
return|;
block|}
comment|/** Executes a SQL query and returns the results as an enumerator, using a    * row builder to convert JDBC column values into rows. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ResultSetEnumerable
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|String
name|sql
parameter_list|,
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|T
argument_list|>
argument_list|>
name|rowBuilderFactory
parameter_list|)
block|{
return|return
operator|new
name|ResultSetEnumerable
argument_list|<>
argument_list|(
name|dataSource
argument_list|,
name|sql
argument_list|,
name|rowBuilderFactory
argument_list|)
return|;
block|}
comment|/** Executes a SQL query and returns the results as an enumerator, using a    * row builder to convert JDBC column values into rows.    *    *<p>It uses a {@link PreparedStatement} for computing the query result,    * and that means that it can bind parameters. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ResultSetEnumerable
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|String
name|sql
parameter_list|,
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|T
argument_list|>
argument_list|>
name|rowBuilderFactory
parameter_list|,
name|PreparedStatementEnricher
name|consumer
parameter_list|)
block|{
return|return
operator|new
name|ResultSetEnumerable
argument_list|<>
argument_list|(
name|dataSource
argument_list|,
name|sql
argument_list|,
name|rowBuilderFactory
argument_list|,
name|consumer
argument_list|)
return|;
block|}
specifier|public
name|void
name|setTimeout
parameter_list|(
name|DataContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|queryStart
operator|=
operator|(
name|Long
operator|)
name|context
operator|.
name|get
argument_list|(
name|DataContext
operator|.
name|Variable
operator|.
name|UTC_TIMESTAMP
operator|.
name|camelName
argument_list|)
expr_stmt|;
name|Object
name|timeout
init|=
name|context
operator|.
name|get
argument_list|(
name|DataContext
operator|.
name|Variable
operator|.
name|TIMEOUT
operator|.
name|camelName
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeout
operator|instanceof
name|Long
condition|)
block|{
name|this
operator|.
name|timeout
operator|=
operator|(
name|Long
operator|)
name|timeout
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|timeout
operator|!=
literal|null
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Variable.TIMEOUT should be `long`. Given value was {}"
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|timeout
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** Called from generated code that proposes to create a    * {@code ResultSetEnumerable} over a prepared statement. */
specifier|public
specifier|static
name|PreparedStatementEnricher
name|createEnricher
parameter_list|(
name|Integer
index|[]
name|indexes
parameter_list|,
name|DataContext
name|context
parameter_list|)
block|{
return|return
name|preparedStatement
lambda|->
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|index
init|=
name|indexes
index|[
name|i
index|]
decl_stmt|;
name|setDynamicParam
argument_list|(
name|preparedStatement
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|context
operator|.
name|get
argument_list|(
literal|"?"
operator|+
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** Assigns a value to a dynamic parameter in a prepared statement, calling    * the appropriate {@code setXxx} method based on the type of the value. */
specifier|private
specifier|static
name|void
name|setDynamicParam
parameter_list|(
name|PreparedStatement
name|preparedStatement
parameter_list|,
name|int
name|i
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|preparedStatement
operator|.
name|setObject
argument_list|(
name|i
argument_list|,
literal|null
argument_list|,
name|SqlType
operator|.
name|ANY
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Timestamp
condition|)
block|{
name|preparedStatement
operator|.
name|setTimestamp
argument_list|(
name|i
argument_list|,
operator|(
name|Timestamp
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Time
condition|)
block|{
name|preparedStatement
operator|.
name|setTime
argument_list|(
name|i
argument_list|,
operator|(
name|Time
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|preparedStatement
operator|.
name|setString
argument_list|(
name|i
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
name|preparedStatement
operator|.
name|setInt
argument_list|(
name|i
argument_list|,
operator|(
name|Integer
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Double
condition|)
block|{
name|preparedStatement
operator|.
name|setDouble
argument_list|(
name|i
argument_list|,
operator|(
name|Double
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|java
operator|.
name|sql
operator|.
name|Array
condition|)
block|{
name|preparedStatement
operator|.
name|setArray
argument_list|(
name|i
argument_list|,
operator|(
name|java
operator|.
name|sql
operator|.
name|Array
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|BigDecimal
condition|)
block|{
name|preparedStatement
operator|.
name|setBigDecimal
argument_list|(
name|i
argument_list|,
operator|(
name|BigDecimal
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
name|preparedStatement
operator|.
name|setBoolean
argument_list|(
name|i
argument_list|,
operator|(
name|Boolean
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Blob
condition|)
block|{
name|preparedStatement
operator|.
name|setBlob
argument_list|(
name|i
argument_list|,
operator|(
name|Blob
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Byte
condition|)
block|{
name|preparedStatement
operator|.
name|setByte
argument_list|(
name|i
argument_list|,
operator|(
name|Byte
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|NClob
condition|)
block|{
name|preparedStatement
operator|.
name|setNClob
argument_list|(
name|i
argument_list|,
operator|(
name|NClob
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Clob
condition|)
block|{
name|preparedStatement
operator|.
name|setClob
argument_list|(
name|i
argument_list|,
operator|(
name|Clob
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|preparedStatement
operator|.
name|setBytes
argument_list|(
name|i
argument_list|,
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Date
condition|)
block|{
name|preparedStatement
operator|.
name|setDate
argument_list|(
name|i
argument_list|,
operator|(
name|Date
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Float
condition|)
block|{
name|preparedStatement
operator|.
name|setFloat
argument_list|(
name|i
argument_list|,
operator|(
name|Float
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Long
condition|)
block|{
name|preparedStatement
operator|.
name|setLong
argument_list|(
name|i
argument_list|,
operator|(
name|Long
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Ref
condition|)
block|{
name|preparedStatement
operator|.
name|setRef
argument_list|(
name|i
argument_list|,
operator|(
name|Ref
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|RowId
condition|)
block|{
name|preparedStatement
operator|.
name|setRowId
argument_list|(
name|i
argument_list|,
operator|(
name|RowId
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Short
condition|)
block|{
name|preparedStatement
operator|.
name|setShort
argument_list|(
name|i
argument_list|,
operator|(
name|Short
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|URL
condition|)
block|{
name|preparedStatement
operator|.
name|setURL
argument_list|(
name|i
argument_list|,
operator|(
name|URL
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|SQLXML
condition|)
block|{
name|preparedStatement
operator|.
name|setSQLXML
argument_list|(
name|i
argument_list|,
operator|(
name|SQLXML
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|preparedStatement
operator|.
name|setObject
argument_list|(
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
if|if
condition|(
name|preparedStatementEnricher
operator|==
literal|null
condition|)
block|{
return|return
name|enumeratorBasedOnStatement
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|enumeratorBasedOnPreparedStatement
argument_list|()
return|;
block|}
block|}
specifier|private
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumeratorBasedOnStatement
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|Statement
name|statement
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
name|statement
operator|=
name|connection
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|setTimeoutIfPossible
argument_list|(
name|statement
argument_list|)
expr_stmt|;
if|if
condition|(
name|statement
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
condition|)
block|{
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
name|statement
operator|=
literal|null
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
return|return
operator|new
name|ResultSetEnumerator
argument_list|<>
argument_list|(
name|resultSet
argument_list|,
name|rowBuilderFactory
argument_list|)
return|;
block|}
else|else
block|{
name|Integer
name|updateCount
init|=
name|statement
operator|.
name|getUpdateCount
argument_list|()
decl_stmt|;
return|return
name|Linq4j
operator|.
name|singletonEnumerator
argument_list|(
operator|(
name|T
operator|)
name|updateCount
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|Static
operator|.
name|RESOURCE
operator|.
name|exceptionWhilePerformingQueryOnJdbcSubSchema
argument_list|(
name|sql
argument_list|)
operator|.
name|ex
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|closeIfPossible
argument_list|(
name|connection
argument_list|,
name|statement
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumeratorBasedOnPreparedStatement
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|preparedStatement
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
name|preparedStatement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|setTimeoutIfPossible
argument_list|(
name|preparedStatement
argument_list|)
expr_stmt|;
name|preparedStatementEnricher
operator|.
name|enrich
argument_list|(
name|preparedStatement
argument_list|)
expr_stmt|;
if|if
condition|(
name|preparedStatement
operator|.
name|execute
argument_list|()
condition|)
block|{
specifier|final
name|ResultSet
name|resultSet
init|=
name|preparedStatement
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
name|preparedStatement
operator|=
literal|null
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
return|return
operator|new
name|ResultSetEnumerator
argument_list|<>
argument_list|(
name|resultSet
argument_list|,
name|rowBuilderFactory
argument_list|)
return|;
block|}
else|else
block|{
name|Integer
name|updateCount
init|=
name|preparedStatement
operator|.
name|getUpdateCount
argument_list|()
decl_stmt|;
return|return
name|Linq4j
operator|.
name|singletonEnumerator
argument_list|(
operator|(
name|T
operator|)
name|updateCount
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|Static
operator|.
name|RESOURCE
operator|.
name|exceptionWhilePerformingQueryOnJdbcSubSchema
argument_list|(
name|sql
argument_list|)
operator|.
name|ex
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|closeIfPossible
argument_list|(
name|connection
argument_list|,
name|preparedStatement
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setTimeoutIfPossible
parameter_list|(
name|Statement
name|statement
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|timeout
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|secondsLeft
init|=
operator|(
name|queryStart
operator|+
name|timeout
operator|-
name|now
operator|)
operator|/
literal|1000
decl_stmt|;
if|if
condition|(
name|secondsLeft
operator|<=
literal|0
condition|)
block|{
throw|throw
name|Static
operator|.
name|RESOURCE
operator|.
name|queryExecutionTimeoutReached
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|timeout
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|queryStart
argument_list|)
argument_list|)
argument_list|)
operator|.
name|ex
argument_list|()
throw|;
block|}
if|if
condition|(
name|secondsLeft
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
comment|// Just ignore the timeout if it happens to be too big, we can't squeeze it into int
return|return;
block|}
try|try
block|{
name|statement
operator|.
name|setQueryTimeout
argument_list|(
operator|(
name|int
operator|)
name|secondsLeft
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLFeatureNotSupportedException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|timeoutSetFailed
operator|&&
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|// We don't really want to print this again and again if enumerable is used multiple times
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Failed to set query timeout "
operator|+
name|secondsLeft
operator|+
literal|" seconds"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|timeoutSetFailed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|closeIfPossible
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Statement
name|statement
parameter_list|)
block|{
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|statement
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
comment|/** Implementation of {@link Enumerator} that reads from a    * {@link ResultSet}.    *    * @param<T> element type */
specifier|private
specifier|static
class|class
name|ResultSetEnumerator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Function0
argument_list|<
name|T
argument_list|>
name|rowBuilder
decl_stmt|;
specifier|private
name|ResultSet
name|resultSet
decl_stmt|;
name|ResultSetEnumerator
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|T
argument_list|>
argument_list|>
name|rowBuilderFactory
parameter_list|)
block|{
name|this
operator|.
name|resultSet
operator|=
name|resultSet
expr_stmt|;
name|this
operator|.
name|rowBuilder
operator|=
name|rowBuilderFactory
operator|.
name|apply
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|T
name|current
parameter_list|()
block|{
return|return
name|rowBuilder
operator|.
name|apply
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
try|try
block|{
return|return
name|resultSet
operator|.
name|next
argument_list|()
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
specifier|public
name|void
name|reset
parameter_list|()
block|{
try|try
block|{
name|resultSet
operator|.
name|beforeFirst
argument_list|()
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
specifier|public
name|void
name|close
parameter_list|()
block|{
name|ResultSet
name|savedResultSet
init|=
name|resultSet
decl_stmt|;
if|if
condition|(
name|savedResultSet
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|resultSet
operator|=
literal|null
expr_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|savedResultSet
operator|.
name|getStatement
argument_list|()
decl_stmt|;
name|savedResultSet
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Connection
name|connection
init|=
name|statement
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
specifier|private
specifier|static
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primitiveRowBuilderFactory
parameter_list|(
specifier|final
name|Primitive
index|[]
name|primitives
parameter_list|)
block|{
return|return
name|resultSet
lambda|->
block|{
specifier|final
name|ResultSetMetaData
name|metaData
decl_stmt|;
specifier|final
name|int
name|columnCount
decl_stmt|;
try|try
block|{
name|metaData
operator|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
expr_stmt|;
name|columnCount
operator|=
name|metaData
operator|.
name|getColumnCount
argument_list|()
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
assert|assert
name|columnCount
operator|==
name|primitives
operator|.
name|length
assert|;
if|if
condition|(
name|columnCount
operator|==
literal|1
condition|)
block|{
return|return
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
name|resultSet
operator|.
name|getObject
argument_list|(
literal|1
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
comment|//noinspection unchecked
return|return
operator|(
name|Function0
operator|)
parameter_list|()
lambda|->
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|columnCount
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
return|return
name|list
operator|.
name|toArray
argument_list|()
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
return|;
block|}
comment|/**    * Consumer for decorating a {@link PreparedStatement}, that is, setting    * its parameters.    */
specifier|public
interface|interface
name|PreparedStatementEnricher
block|{
name|void
name|enrich
parameter_list|(
name|PreparedStatement
name|statement
parameter_list|)
throws|throws
name|SQLException
function_decl|;
block|}
block|}
end_class

end_unit

