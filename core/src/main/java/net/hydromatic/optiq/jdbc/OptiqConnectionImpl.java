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
name|jdbc
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
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
name|linq4j
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
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
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
name|Expressions
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
name|Function0
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
name|config
operator|.
name|Lex
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
name|config
operator|.
name|OptiqConnectionProperty
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
name|AbstractSchema
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
name|prepare
operator|.
name|OptiqCatalogReader
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
name|runtime
operator|.
name|Hook
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
name|server
operator|.
name|OptiqServer
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
name|server
operator|.
name|OptiqServerStatement
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
name|advise
operator|.
name|SqlAdvisor
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
name|advise
operator|.
name|SqlAdvisorValidator
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|validate
operator|.
name|SqlConformance
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
name|validate
operator|.
name|SqlValidatorWithHints
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
name|Holder
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|*
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
comment|/**  * Implementation of JDBC connection  * in the Optiq engine.  *  *<p>Abstract to allow newer versions of JDBC to add methods.</p>  */
end_comment

begin_class
specifier|abstract
class|class
name|OptiqConnectionImpl
extends|extends
name|AvaticaConnection
implements|implements
name|OptiqConnection
implements|,
name|QueryProvider
block|{
specifier|public
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|final
name|OptiqRootSchema
name|rootSchema
decl_stmt|;
specifier|final
name|Function0
argument_list|<
name|OptiqPrepare
argument_list|>
name|prepareFactory
decl_stmt|;
specifier|final
name|OptiqServer
name|server
init|=
operator|new
name|OptiqServerImpl
argument_list|()
decl_stmt|;
comment|// must be package-protected
specifier|static
specifier|final
name|Trojan
name|TROJAN
init|=
name|createTrojan
argument_list|()
decl_stmt|;
comment|/**    * Creates an OptiqConnectionImpl.    *    *<p>Not public; method is called only from the driver.</p>    *    * @param driver Driver    * @param factory Factory for JDBC objects    * @param url Server URL    * @param info Other connection properties    * @param rootSchema Root schema, or null    * @param typeFactory Type factory, or null    */
specifier|protected
name|OptiqConnectionImpl
parameter_list|(
name|Driver
name|driver
parameter_list|,
name|AvaticaFactory
name|factory
parameter_list|,
name|String
name|url
parameter_list|,
name|Properties
name|info
parameter_list|,
name|OptiqRootSchema
name|rootSchema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|driver
argument_list|,
name|factory
argument_list|,
name|url
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|prepareFactory
operator|=
name|driver
operator|.
name|prepareFactory
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
operator|!=
literal|null
condition|?
name|typeFactory
else|:
operator|new
name|JavaTypeFactoryImpl
argument_list|()
expr_stmt|;
name|this
operator|.
name|rootSchema
operator|=
name|rootSchema
operator|!=
literal|null
condition|?
name|rootSchema
else|:
name|OptiqSchema
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OptiqConnectionConfig
name|cfg
init|=
operator|new
name|OptiqConnectionConfigImpl
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|InternalProperty
operator|.
name|CASE_SENSITIVE
argument_list|,
name|cfg
operator|.
name|caseSensitive
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|InternalProperty
operator|.
name|UNQUOTED_CASING
argument_list|,
name|cfg
operator|.
name|unquotedCasing
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|InternalProperty
operator|.
name|QUOTED_CASING
argument_list|,
name|cfg
operator|.
name|quotedCasing
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|InternalProperty
operator|.
name|QUOTING
argument_list|,
name|cfg
operator|.
name|quoting
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Meta
name|createMeta
parameter_list|()
block|{
return|return
operator|new
name|MetaImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
name|MetaImpl
name|meta
parameter_list|()
block|{
return|return
operator|(
name|MetaImpl
operator|)
name|meta
return|;
block|}
specifier|public
name|OptiqConnectionConfig
name|config
parameter_list|()
block|{
return|return
operator|new
name|OptiqConnectionConfigImpl
argument_list|(
name|info
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AvaticaStatement
name|createStatement
parameter_list|(
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|,
name|int
name|resultSetHoldability
parameter_list|)
throws|throws
name|SQLException
block|{
name|OptiqStatement
name|statement
init|=
operator|(
name|OptiqStatement
operator|)
name|super
operator|.
name|createStatement
argument_list|(
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
decl_stmt|;
name|server
operator|.
name|addStatement
argument_list|(
name|statement
argument_list|)
expr_stmt|;
return|return
name|statement
return|;
block|}
annotation|@
name|Override
specifier|public
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|,
name|int
name|resultSetHoldability
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
block|{
name|AvaticaPrepareResult
name|prepareResult
init|=
name|parseQuery
argument_list|(
name|sql
argument_list|,
operator|new
name|ContextImpl
argument_list|(
name|this
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|OptiqPreparedStatement
name|statement
init|=
operator|(
name|OptiqPreparedStatement
operator|)
name|factory
operator|.
name|newPreparedStatement
argument_list|(
name|this
argument_list|,
name|prepareResult
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
decl_stmt|;
name|server
operator|.
name|addStatement
argument_list|(
name|statement
argument_list|)
expr_stmt|;
return|return
name|statement
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|Helper
operator|.
name|INSTANCE
operator|.
name|createException
argument_list|(
literal|"Error while preparing statement ["
operator|+
name|sql
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|Helper
operator|.
name|INSTANCE
operator|.
name|createException
argument_list|(
literal|"Error while preparing statement ["
operator|+
name|sql
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
parameter_list|<
name|T
parameter_list|>
name|OptiqPrepare
operator|.
name|PrepareResult
argument_list|<
name|T
argument_list|>
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|,
name|OptiqPrepare
operator|.
name|Context
name|prepareContext
parameter_list|,
name|int
name|maxRowCount
parameter_list|)
block|{
name|OptiqPrepare
operator|.
name|Dummy
operator|.
name|push
argument_list|(
name|prepareContext
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|OptiqPrepare
name|prepare
init|=
name|prepareFactory
operator|.
name|apply
argument_list|()
decl_stmt|;
return|return
name|prepare
operator|.
name|prepareSql
argument_list|(
name|prepareContext
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
name|maxRowCount
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
name|prepareContext
argument_list|)
expr_stmt|;
block|}
block|}
comment|// OptiqConnection methods
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
return|return
name|rootSchema
operator|.
name|plus
argument_list|()
return|;
block|}
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
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|info
return|;
block|}
comment|// QueryProvider methods
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|createQuery
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|OptiqQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|rowType
argument_list|,
name|expression
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
name|createQuery
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Type
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|OptiqQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|rowType
argument_list|,
name|expression
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO:
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO:
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|executeQuery
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
try|try
block|{
name|OptiqStatement
name|statement
init|=
operator|(
name|OptiqStatement
operator|)
name|createStatement
argument_list|()
decl_stmt|;
name|OptiqPrepare
operator|.
name|PrepareResult
argument_list|<
name|T
argument_list|>
name|enumerable
init|=
name|statement
operator|.
name|prepare
argument_list|(
name|queryable
argument_list|)
decl_stmt|;
specifier|final
name|DataContext
name|dataContext
init|=
name|createDataContext
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|enumerable
operator|.
name|enumerator
argument_list|(
name|dataContext
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
specifier|public
name|DataContext
name|createDataContext
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|parameterValues
parameter_list|)
block|{
if|if
condition|(
name|config
argument_list|()
operator|.
name|spark
argument_list|()
condition|)
block|{
return|return
operator|new
name|SlimDataContext
argument_list|()
return|;
block|}
return|return
operator|new
name|DataContextImpl
argument_list|(
name|this
argument_list|,
name|parameterValues
argument_list|)
return|;
block|}
comment|// do not make public
name|UnregisteredDriver
name|getDriver
parameter_list|()
block|{
return|return
name|driver
return|;
block|}
comment|// do not make public
name|AvaticaFactory
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
comment|/** Implementation of Queryable. */
specifier|static
class|class
name|OptiqQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|BaseQueryable
argument_list|<
name|T
argument_list|>
block|{
specifier|public
name|OptiqQueryable
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|elementType
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OptiqConnection
name|getConnection
parameter_list|()
block|{
return|return
operator|(
name|OptiqConnection
operator|)
name|provider
return|;
block|}
block|}
comment|/** Implementation of Server. */
specifier|private
specifier|static
class|class
name|OptiqServerImpl
implements|implements
name|OptiqServer
block|{
specifier|final
name|List
argument_list|<
name|OptiqServerStatement
argument_list|>
name|statementList
init|=
operator|new
name|ArrayList
argument_list|<
name|OptiqServerStatement
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|removeStatement
parameter_list|(
name|OptiqServerStatement
name|optiqServerStatement
parameter_list|)
block|{
name|statementList
operator|.
name|add
argument_list|(
name|optiqServerStatement
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addStatement
parameter_list|(
name|OptiqServerStatement
name|statement
parameter_list|)
block|{
name|statementList
operator|.
name|add
argument_list|(
name|statement
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Schema that has no parents. */
specifier|static
class|class
name|RootSchema
extends|extends
name|AbstractSchema
block|{
name|RootSchema
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|DataContext
operator|.
name|ROOT
argument_list|,
name|BuiltinMethod
operator|.
name|DATA_CONTEXT_GET_ROOT_SCHEMA
operator|.
name|method
argument_list|)
return|;
block|}
block|}
comment|/** Implementation of DataContext. */
specifier|static
class|class
name|DataContextImpl
implements|implements
name|DataContext
block|{
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|final
name|OptiqSchema
name|rootSchema
decl_stmt|;
specifier|private
specifier|final
name|QueryProvider
name|queryProvider
decl_stmt|;
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
name|DataContextImpl
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|parameterValues
parameter_list|)
block|{
name|this
operator|.
name|queryProvider
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|connection
operator|.
name|getTypeFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|rootSchema
operator|=
name|connection
operator|.
name|rootSchema
expr_stmt|;
comment|// Store the time at which the query started executing. The SQL
comment|// standard says that functions such as CURRENT_TIMESTAMP return the
comment|// same value throughout the query.
specifier|final
name|Holder
argument_list|<
name|Long
argument_list|>
name|timeHolder
init|=
name|Holder
operator|.
name|of
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
comment|// Give a hook chance to alter the clock.
name|Hook
operator|.
name|CURRENT_TIME
operator|.
name|run
argument_list|(
name|timeHolder
argument_list|)
expr_stmt|;
specifier|final
name|long
name|time
init|=
name|timeHolder
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|TimeZone
name|timeZone
init|=
name|connection
operator|.
name|getTimeZone
argument_list|()
decl_stmt|;
specifier|final
name|long
name|localOffset
init|=
name|timeZone
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
decl_stmt|;
specifier|final
name|long
name|currentOffset
init|=
name|localOffset
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|Variable
operator|.
name|UTC_TIMESTAMP
operator|.
name|camelName
argument_list|,
name|time
argument_list|)
operator|.
name|put
argument_list|(
name|Variable
operator|.
name|CURRENT_TIMESTAMP
operator|.
name|camelName
argument_list|,
name|time
operator|+
name|currentOffset
argument_list|)
operator|.
name|put
argument_list|(
name|Variable
operator|.
name|LOCAL_TIMESTAMP
operator|.
name|camelName
argument_list|,
name|time
operator|+
name|localOffset
argument_list|)
operator|.
name|put
argument_list|(
name|Variable
operator|.
name|TIME_ZONE
operator|.
name|camelName
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|Object
argument_list|>
name|value
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|parameterValues
argument_list|)
control|)
block|{
name|Object
name|e
init|=
name|value
operator|.
name|e
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
name|AvaticaParameter
operator|.
name|DUMMY_VALUE
expr_stmt|;
block|}
name|builder
operator|.
name|put
argument_list|(
literal|"?"
operator|+
name|value
operator|.
name|i
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|map
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Object
name|o
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
name|AvaticaParameter
operator|.
name|DUMMY_VALUE
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|&&
name|Variable
operator|.
name|SQL_ADVISOR
operator|.
name|camelName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|getSqlAdvisor
argument_list|()
return|;
block|}
return|return
name|o
return|;
block|}
specifier|private
name|SqlAdvisor
name|getSqlAdvisor
parameter_list|()
block|{
specifier|final
name|OptiqConnectionImpl
name|con
init|=
operator|(
name|OptiqConnectionImpl
operator|)
name|queryProvider
decl_stmt|;
specifier|final
name|String
name|schemaName
init|=
name|con
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
init|=
name|schemaName
operator|==
literal|null
condition|?
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
else|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|schemaName
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorWithHints
name|validator
init|=
operator|new
name|SqlAdvisorValidator
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
operator|new
name|OptiqCatalogReader
argument_list|(
name|rootSchema
argument_list|,
name|con
operator|.
name|config
argument_list|()
operator|.
name|caseSensitive
argument_list|()
argument_list|,
name|schemaPath
argument_list|,
name|typeFactory
argument_list|)
argument_list|,
name|typeFactory
argument_list|,
name|SqlConformance
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlAdvisor
argument_list|(
name|validator
argument_list|)
return|;
block|}
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
return|return
name|rootSchema
operator|.
name|plus
argument_list|()
return|;
block|}
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
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
return|return
name|queryProvider
return|;
block|}
block|}
comment|/** Implementation of Context. */
specifier|static
class|class
name|ContextImpl
implements|implements
name|OptiqPrepare
operator|.
name|Context
block|{
specifier|private
specifier|final
name|OptiqConnectionImpl
name|connection
decl_stmt|;
specifier|public
name|ContextImpl
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|connection
operator|.
name|typeFactory
return|;
block|}
specifier|public
name|OptiqRootSchema
name|getRootSchema
parameter_list|()
block|{
return|return
name|connection
operator|.
name|rootSchema
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
specifier|final
name|String
name|schemaName
init|=
name|connection
operator|.
name|getSchema
argument_list|()
decl_stmt|;
return|return
name|schemaName
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
else|:
name|Collections
operator|.
name|singletonList
argument_list|(
name|schemaName
argument_list|)
return|;
block|}
specifier|public
name|OptiqConnectionConfig
name|config
parameter_list|()
block|{
return|return
name|connection
operator|.
name|config
argument_list|()
return|;
block|}
specifier|public
name|DataContext
name|getDataContext
parameter_list|()
block|{
return|return
name|connection
operator|.
name|createDataContext
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
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
comment|/** Implementation of {@link DataContext} that has few variables and is    * {@link Serializable}. For Spark. */
specifier|private
specifier|static
class|class
name|SlimDataContext
implements|implements
name|DataContext
implements|,
name|Serializable
block|{
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
return|return
literal|null
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
literal|null
return|;
block|}
block|}
comment|/** Implementation of {@link OptiqConnectionConfig}. */
specifier|private
specifier|static
class|class
name|OptiqConnectionConfigImpl
extends|extends
name|ConnectionConfigImpl
implements|implements
name|OptiqConnectionConfig
block|{
specifier|public
name|OptiqConnectionConfigImpl
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|super
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|autoTemp
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|AUTO_TEMP
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|materializationsEnabled
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|MATERIALIZATIONS_ENABLED
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|String
name|model
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|MODEL
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
specifier|public
name|Lex
name|lex
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|LEX
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Lex
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|Quoting
name|quoting
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|QUOTING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Quoting
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|quoting
argument_list|)
return|;
block|}
specifier|public
name|Casing
name|unquotedCasing
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|UNQUOTED_CASING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Casing
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|unquotedCasing
argument_list|)
return|;
block|}
specifier|public
name|Casing
name|quotedCasing
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|QUOTED_CASING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Casing
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|quotedCasing
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|caseSensitive
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|CASE_SENSITIVE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|(
name|lex
argument_list|()
operator|.
name|caseSensitive
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|spark
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|SPARK
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End OptiqConnectionImpl.java
end_comment

end_unit

