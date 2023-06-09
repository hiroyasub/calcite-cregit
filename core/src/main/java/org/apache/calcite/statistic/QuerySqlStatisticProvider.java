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
name|statistic
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
name|materialize
operator|.
name|SqlStatisticProvider
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
name|Contexts
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
name|RelOptCluster
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
name|RelOptSchema
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
name|ViewExpanders
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
name|rel2sql
operator|.
name|RelToSqlConverter
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
name|rel2sql
operator|.
name|SqlImplementor
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|RelBuilder
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
name|cache
operator|.
name|CacheBuilder
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
name|Statement
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link SqlStatisticProvider} that generates and executes  * SQL queries.  */
end_comment

begin_class
specifier|public
class|class
name|QuerySqlStatisticProvider
implements|implements
name|SqlStatisticProvider
block|{
comment|/** Instance that uses SQL to compute statistics,    * does not log SQL statements,    * and caches up to 1,024 results for up to 30 minutes.    * (That period should be sufficient for the    * duration of Calcite's tests, and many other purposes.) */
specifier|public
specifier|static
specifier|final
name|SqlStatisticProvider
name|SILENT_CACHING_INSTANCE
init|=
operator|new
name|CachingSqlStatisticProvider
argument_list|(
operator|new
name|QuerySqlStatisticProvider
argument_list|(
name|sql
lambda|->
block|{
block|}
argument_list|)
argument_list|,
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterAccess
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|maximumSize
argument_list|(
literal|1_024
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|/** As {@link #SILENT_CACHING_INSTANCE} but prints SQL statements to    * {@link System#out}. */
specifier|public
specifier|static
specifier|final
name|SqlStatisticProvider
name|VERBOSE_CACHING_INSTANCE
init|=
operator|new
name|CachingSqlStatisticProvider
argument_list|(
operator|new
name|QuerySqlStatisticProvider
argument_list|(
name|sql
lambda|->
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sql
operator|+
literal|":"
argument_list|)
argument_list|)
argument_list|,
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterAccess
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|maximumSize
argument_list|(
literal|1_024
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Consumer
argument_list|<
name|String
argument_list|>
name|sqlConsumer
decl_stmt|;
comment|/** Creates a QuerySqlStatisticProvider.    *    * @param sqlConsumer Called when each SQL statement is generated    */
specifier|public
name|QuerySqlStatisticProvider
parameter_list|(
name|Consumer
argument_list|<
name|String
argument_list|>
name|sqlConsumer
parameter_list|)
block|{
name|this
operator|.
name|sqlConsumer
operator|=
name|requireNonNull
argument_list|(
name|sqlConsumer
argument_list|,
literal|"sqlConsumer"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|tableCardinality
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
specifier|final
name|SqlDialect
name|dialect
init|=
name|table
operator|.
name|unwrapOrThrow
argument_list|(
name|SqlDialect
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|DataSource
name|dataSource
init|=
name|table
operator|.
name|unwrapOrThrow
argument_list|(
name|DataSource
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|withBuilder
argument_list|(
parameter_list|(
name|cluster
parameter_list|,
name|relOptSchema
parameter_list|,
name|relBuilder
parameter_list|)
lambda|->
block|{
comment|// Generate:
comment|//   SELECT COUNT(*) FROM `EMP`
name|relBuilder
operator|.
name|push
argument_list|(
name|table
operator|.
name|toRel
argument_list|(
name|ViewExpanders
operator|.
name|simpleContext
argument_list|(
name|cluster
argument_list|)
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|relBuilder
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|dialect
argument_list|)
decl_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|dataSource
operator|.
name|getConnection
argument_list|()
init|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
if|if
condition|(
operator|!
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected exactly 1 row: "
operator|+
name|sql
argument_list|)
throw|;
block|}
specifier|final
name|double
name|cardinality
init|=
name|resultSet
operator|.
name|getDouble
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected exactly 1 row: "
operator|+
name|sql
argument_list|)
throw|;
block|}
return|return
name|cardinality
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|,
name|sql
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isForeignKey
parameter_list|(
name|RelOptTable
name|fromTable
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fromColumns
parameter_list|,
name|RelOptTable
name|toTable
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|toColumns
parameter_list|)
block|{
specifier|final
name|SqlDialect
name|dialect
init|=
name|fromTable
operator|.
name|unwrapOrThrow
argument_list|(
name|SqlDialect
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|DataSource
name|dataSource
init|=
name|fromTable
operator|.
name|unwrapOrThrow
argument_list|(
name|DataSource
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|withBuilder
argument_list|(
parameter_list|(
name|cluster
parameter_list|,
name|relOptSchema
parameter_list|,
name|relBuilder
parameter_list|)
lambda|->
block|{
comment|// EMP(DEPTNO) is a foreign key to DEPT(DEPTNO) if the following
comment|// query returns 0:
comment|//
comment|//   SELECT COUNT(*) FROM (
comment|//     SELECT deptno FROM `EMP` WHERE deptno IS NOT NULL
comment|//     MINUS
comment|//     SELECT deptno FROM `DEPT`)
specifier|final
name|RelOptTable
operator|.
name|ToRelContext
name|toRelContext
init|=
name|ViewExpanders
operator|.
name|simpleContext
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|fromTable
operator|.
name|toRel
argument_list|(
name|toRelContext
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|fromColumns
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|column
lambda|->
name|relBuilder
operator|.
name|isNotNull
argument_list|(
name|relBuilder
operator|.
name|field
argument_list|(
name|column
argument_list|)
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|fromColumns
argument_list|)
argument_list|)
operator|.
name|push
argument_list|(
name|toTable
operator|.
name|toRel
argument_list|(
name|toRelContext
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|toColumns
argument_list|)
argument_list|)
operator|.
name|minus
argument_list|(
literal|false
argument_list|,
literal|2
argument_list|)
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|relBuilder
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|dialect
argument_list|)
decl_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|dataSource
operator|.
name|getConnection
argument_list|()
init|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
if|if
condition|(
operator|!
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected exactly 1 row: "
operator|+
name|sql
argument_list|)
throw|;
block|}
specifier|final
name|int
name|count
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected exactly 1 row: "
operator|+
name|sql
argument_list|)
throw|;
block|}
return|return
name|count
operator|==
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|,
name|sql
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isKey
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|columns
parameter_list|)
block|{
specifier|final
name|SqlDialect
name|dialect
init|=
name|table
operator|.
name|unwrapOrThrow
argument_list|(
name|SqlDialect
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|DataSource
name|dataSource
init|=
name|table
operator|.
name|unwrapOrThrow
argument_list|(
name|DataSource
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|withBuilder
argument_list|(
parameter_list|(
name|cluster
parameter_list|,
name|relOptSchema
parameter_list|,
name|relBuilder
parameter_list|)
lambda|->
block|{
comment|// The collection of columns ['DEPTNO'] is a key for 'EMP' if the
comment|// following query returns no rows:
comment|//
comment|//   SELECT 1
comment|//   FROM `EMP`
comment|//   GROUP BY `DEPTNO`
comment|//   HAVING COUNT(*)> 1
comment|//
specifier|final
name|RelOptTable
operator|.
name|ToRelContext
name|toRelContext
init|=
name|ViewExpanders
operator|.
name|simpleContext
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|table
operator|.
name|toRel
argument_list|(
name|toRelContext
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|columns
argument_list|)
argument_list|)
argument_list|,
name|relBuilder
operator|.
name|count
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|relBuilder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|Util
operator|.
name|last
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|,
name|relBuilder
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|dialect
argument_list|)
decl_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|dataSource
operator|.
name|getConnection
argument_list|()
init|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
return|return
operator|!
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
name|handle
argument_list|(
name|e
argument_list|,
name|sql
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RuntimeException
name|handle
parameter_list|(
name|SQLException
name|e
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
literal|"Error while executing SQL for statistics: "
operator|+
name|sql
argument_list|,
name|e
argument_list|)
return|;
block|}
specifier|protected
name|String
name|toSql
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|)
block|{
specifier|final
name|RelToSqlConverter
name|converter
init|=
operator|new
name|RelToSqlConverter
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
name|SqlImplementor
operator|.
name|Result
name|result
init|=
name|converter
operator|.
name|visitRoot
argument_list|(
name|rel
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
init|=
name|result
operator|.
name|asStatement
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|dialect
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|sqlConsumer
operator|.
name|accept
argument_list|(
name|sql
argument_list|)
expr_stmt|;
return|return
name|sql
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withBuilder
parameter_list|(
name|BuilderAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|)
block|{
return|return
name|Frameworks
operator|.
name|withPlanner
argument_list|(
parameter_list|(
name|cluster
parameter_list|,
name|relOptSchema
parameter_list|,
name|rootSchema
parameter_list|)
lambda|->
block|{
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|relOptSchema
argument_list|)
decl_stmt|;
return|return
name|action
operator|.
name|apply
argument_list|(
name|cluster
argument_list|,
name|relOptSchema
argument_list|,
name|relBuilder
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
comment|/** Performs an action with a {@link RelBuilder}.    *    * @param<R> Result type */
annotation|@
name|FunctionalInterface
specifier|private
interface|interface
name|BuilderAction
parameter_list|<
name|R
parameter_list|>
block|{
name|R
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|RelBuilder
name|relBuilder
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

