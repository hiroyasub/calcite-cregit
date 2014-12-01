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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|AvaticaParameter
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
name|Meta
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|EnumerableDefaults
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
name|Queryable
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
name|tree
operator|.
name|ClassDeclaration
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
name|RelOptPlanner
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
name|RelOptRule
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
name|prepare
operator|.
name|CalcitePrepareImpl
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|runtime
operator|.
name|Bindable
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
name|validate
operator|.
name|SqlValidator
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
name|Stacks
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
name|annotation
operator|.
name|JsonIgnore
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * API for a service that prepares statements for execution.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CalcitePrepare
block|{
name|Function0
argument_list|<
name|CalcitePrepare
argument_list|>
name|DEFAULT_FACTORY
init|=
operator|new
name|Function0
argument_list|<
name|CalcitePrepare
argument_list|>
argument_list|()
block|{
specifier|public
name|CalcitePrepare
name|apply
parameter_list|()
block|{
return|return
operator|new
name|CalcitePrepareImpl
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
name|ConvertResult
name|convert
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
name|CalciteSignature
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
name|CalciteSignature
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
name|CalciteRootSchema
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
name|CalciteConnectionConfig
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
name|RuleSetBuilder
name|builder
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
comment|/** Allows Spark to declare the rules it needs. */
interface|interface
name|RuleSetBuilder
block|{
name|void
name|addRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
function_decl|;
name|void
name|removeRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
function_decl|;
block|}
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
comment|/** Returns a spark handler. Returns a trivial handler, for which      * {@link SparkHandler#enabled()} returns {@code false}, if {@code enable}      * is {@code false} or if Spark is not on the class path. Never returns      * null. */
specifier|public
specifier|static
specifier|synchronized
name|SparkHandler
name|getSparkHandler
parameter_list|(
name|boolean
name|enable
parameter_list|)
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
name|enable
condition|?
name|createHandler
argument_list|()
else|:
operator|new
name|TrivialSparkHandler
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
literal|"org.apache.calcite.adapter.spark.SparkHandlerImpl"
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
name|CalcitePrepare
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
name|RuleSetBuilder
name|builder
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
name|CalcitePrepareImpl
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
name|CalcitePrepareImpl
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
comment|/** The result of parsing and validating a SQL query and converting it to    * relational algebra. */
specifier|public
specifier|static
class|class
name|ConvertResult
extends|extends
name|ParseResult
block|{
specifier|public
specifier|final
name|RelNode
name|relNode
decl_stmt|;
specifier|public
name|ConvertResult
parameter_list|(
name|CalcitePrepareImpl
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
parameter_list|,
name|RelNode
name|relNode
parameter_list|)
block|{
name|super
argument_list|(
name|prepare
argument_list|,
name|validator
argument_list|,
name|sql
argument_list|,
name|sqlNode
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
name|this
operator|.
name|relNode
operator|=
name|relNode
expr_stmt|;
block|}
block|}
comment|/** The result of preparing a query. It gives the Avatica driver framework    * the information it needs to create a prepared statement, or to execute a    * statement directly, without an explicit prepare step. */
specifier|public
specifier|static
class|class
name|CalciteSignature
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Meta
operator|.
name|Signature
block|{
annotation|@
name|JsonIgnore
specifier|public
specifier|final
name|RelDataType
name|rowType
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
name|CalciteSignature
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|internalParameters
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columns
parameter_list|,
name|Meta
operator|.
name|CursorFactory
name|cursorFactory
parameter_list|,
name|int
name|maxRowCount
parameter_list|,
name|Bindable
argument_list|<
name|T
argument_list|>
name|bindable
parameter_list|)
block|{
name|super
argument_list|(
name|columns
argument_list|,
name|sql
argument_list|,
name|parameterList
argument_list|,
name|internalParameters
argument_list|,
name|cursorFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
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
block|}
specifier|public
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable
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
block|}
block|}
end_interface

begin_comment
comment|// End CalcitePrepare.java
end_comment

end_unit

