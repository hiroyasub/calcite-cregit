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
name|ClassDeclaration
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
name|OptiqPrepareImpl
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptPlanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|volcano
operator|.
name|VolcanoPlanner
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
name|sql
operator|.
name|SqlNode
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
name|SqlValidator
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
name|Stacks
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
name|InvocationTargetException
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
name|Method
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * API for a service that prepares statements for execution.  */
end_comment

begin_interface
specifier|public
interface|interface
name|OptiqPrepare
block|{
name|Function0
argument_list|<
name|OptiqPrepare
argument_list|>
name|DEFAULT_FACTORY
init|=
operator|new
name|Function0
argument_list|<
name|OptiqPrepare
argument_list|>
argument_list|()
block|{
specifier|public
name|OptiqPrepare
name|apply
parameter_list|()
block|{
return|return
operator|new
name|OptiqPrepareImpl
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|ThreadLocal
argument_list|<
name|ArrayList
argument_list|<
name|Context
argument_list|>
argument_list|>
name|THREAD_CONTEXT_STACK
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ArrayList
argument_list|<
name|Context
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ArrayList
argument_list|<
name|Context
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|Context
argument_list|>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|ParseResult
name|parse
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|sql
parameter_list|)
function_decl|;
parameter_list|<
name|T
parameter_list|>
name|PrepareResult
argument_list|<
name|T
argument_list|>
name|prepareSql
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|sql
parameter_list|,
name|Queryable
argument_list|<
name|T
argument_list|>
name|expression
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|int
name|maxRowCount
parameter_list|)
function_decl|;
parameter_list|<
name|T
parameter_list|>
name|PrepareResult
argument_list|<
name|T
argument_list|>
name|prepareQueryable
parameter_list|(
name|Context
name|context
parameter_list|,
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
function_decl|;
comment|/** Context for preparing a statement. */
interface|interface
name|Context
block|{
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
name|OptiqRootSchema
name|getRootSchema
parameter_list|()
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultSchemaPath
parameter_list|()
function_decl|;
name|ConnectionConfig
name|config
parameter_list|()
function_decl|;
comment|/** Returns the spark handler. Never null. */
name|SparkHandler
name|spark
parameter_list|()
function_decl|;
name|DataContext
name|getDataContext
parameter_list|()
function_decl|;
block|}
comment|/** Callback to register Spark as the main engine. */
specifier|public
interface|interface
name|SparkHandler
block|{
name|RelNode
name|flattenTypes
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|restructure
parameter_list|)
function_decl|;
name|void
name|registerRules
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
function_decl|;
name|boolean
name|enabled
parameter_list|()
function_decl|;
name|Bindable
name|compile
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|)
function_decl|;
name|Object
name|sparkContext
parameter_list|()
function_decl|;
block|}
comment|/** Namespace that allows us to define non-abstract methods inside an    * interface. */
specifier|public
specifier|static
class|class
name|Dummy
block|{
specifier|private
specifier|static
name|SparkHandler
name|sparkHandler
decl_stmt|;
specifier|public
specifier|static
specifier|synchronized
name|SparkHandler
name|getSparkHandler
parameter_list|()
block|{
if|if
condition|(
name|sparkHandler
operator|==
literal|null
condition|)
block|{
name|sparkHandler
operator|=
name|createHandler
argument_list|()
expr_stmt|;
block|}
return|return
name|sparkHandler
return|;
block|}
specifier|private
specifier|static
name|SparkHandler
name|createHandler
parameter_list|()
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.impl.spark.SparkHandlerImpl"
argument_list|)
decl_stmt|;
name|Method
name|method
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"instance"
argument_list|)
decl_stmt|;
return|return
operator|(
name|OptiqPrepare
operator|.
name|SparkHandler
operator|)
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
return|return
operator|new
name|TrivialSparkHandler
argument_list|()
return|;
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
name|ClassCastException
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
name|NoSuchMethodException
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
block|}
specifier|public
specifier|static
name|void
name|push
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|Stacks
operator|.
name|push
argument_list|(
name|THREAD_CONTEXT_STACK
operator|.
name|get
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Context
name|peek
parameter_list|()
block|{
return|return
name|Stacks
operator|.
name|peek
argument_list|(
name|THREAD_CONTEXT_STACK
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|pop
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|Stacks
operator|.
name|pop
argument_list|(
name|THREAD_CONTEXT_STACK
operator|.
name|get
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/** Implementation of {@link SparkHandler} that either does nothing or      * throws for each method. Use this if Spark is not installed. */
specifier|private
specifier|static
class|class
name|TrivialSparkHandler
implements|implements
name|SparkHandler
block|{
specifier|public
name|RelNode
name|flattenTypes
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|restructure
parameter_list|)
block|{
return|return
name|rootRel
return|;
block|}
specifier|public
name|void
name|registerRules
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
block|}
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Bindable
name|compile
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Object
name|sparkContext
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
comment|/** The result of parsing and validating a SQL query. */
specifier|public
specifier|static
class|class
name|ParseResult
block|{
specifier|public
specifier|final
name|OptiqPrepareImpl
name|prepare
decl_stmt|;
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
comment|// for debug
specifier|public
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|public
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|public
name|ParseResult
parameter_list|(
name|OptiqPrepareImpl
name|prepare
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|String
name|sql
parameter_list|,
name|SqlNode
name|sqlNode
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|prepare
operator|=
name|prepare
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|sqlNode
operator|=
name|sqlNode
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** The result of preparing a query. It gives the Avatica driver framework    * the information it needs to create a prepared statement, or to execute a    * statement directly, without an explicit prepare step. */
specifier|public
specifier|static
class|class
name|PrepareResult
parameter_list|<
name|T
parameter_list|>
implements|implements
name|AvaticaPrepareResult
block|{
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
comment|// for debug
specifier|public
specifier|final
name|List
argument_list|<
name|AvaticaParameter
argument_list|>
name|parameterList
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columnList
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxRowCount
decl_stmt|;
specifier|private
specifier|final
name|Bindable
argument_list|<
name|T
argument_list|>
name|bindable
decl_stmt|;
specifier|public
specifier|final
name|Class
name|resultClazz
decl_stmt|;
specifier|public
name|PrepareResult
parameter_list|(
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|AvaticaParameter
argument_list|>
name|parameterList
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columnList
parameter_list|,
name|int
name|maxRowCount
parameter_list|,
name|Bindable
argument_list|<
name|T
argument_list|>
name|bindable
parameter_list|,
name|Class
name|resultClazz
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|parameterList
operator|=
name|parameterList
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
name|this
operator|.
name|maxRowCount
operator|=
name|maxRowCount
expr_stmt|;
name|this
operator|.
name|bindable
operator|=
name|bindable
expr_stmt|;
name|this
operator|.
name|resultClazz
operator|=
name|resultClazz
expr_stmt|;
block|}
specifier|public
name|Cursor
name|createCursor
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
name|Enumerator
argument_list|<
name|?
argument_list|>
name|enumerator
init|=
name|enumerator
argument_list|(
name|dataContext
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
return|return
name|columnList
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
operator|new
name|ObjectEnumeratorCursor
argument_list|(
operator|(
name|Enumerator
operator|)
name|enumerator
argument_list|)
else|:
name|resultClazz
operator|!=
literal|null
operator|&&
operator|!
name|resultClazz
operator|.
name|isArray
argument_list|()
condition|?
operator|new
name|RecordEnumeratorCursor
argument_list|(
operator|(
name|Enumerator
operator|)
name|enumerator
argument_list|,
name|resultClazz
argument_list|)
else|:
operator|new
name|ArrayEnumeratorCursor
argument_list|(
operator|(
name|Enumerator
operator|)
name|enumerator
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|getColumnList
parameter_list|()
block|{
return|return
name|columnList
return|;
block|}
specifier|public
name|List
argument_list|<
name|AvaticaParameter
argument_list|>
name|getParameterList
parameter_list|()
block|{
return|return
name|parameterList
return|;
block|}
specifier|public
name|String
name|getSql
parameter_list|()
block|{
return|return
name|sql
return|;
block|}
specifier|private
name|Enumerable
argument_list|<
name|T
argument_list|>
name|getEnumerable
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable
init|=
name|bindable
operator|.
name|bind
argument_list|(
name|dataContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxRowCount
operator|>=
literal|0
condition|)
block|{
comment|// Apply limit. In JDBC 0 means "no limit". But for us, -1 means
comment|// "no limit", and 0 is a valid limit.
name|enumerable
operator|=
name|EnumerableDefaults
operator|.
name|take
argument_list|(
name|enumerable
argument_list|,
name|maxRowCount
argument_list|)
expr_stmt|;
block|}
return|return
name|enumerable
return|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
return|return
name|getEnumerable
argument_list|(
name|dataContext
argument_list|)
operator|.
name|enumerator
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
return|return
name|getEnumerable
argument_list|(
name|dataContext
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End OptiqPrepare.java
end_comment

end_unit

