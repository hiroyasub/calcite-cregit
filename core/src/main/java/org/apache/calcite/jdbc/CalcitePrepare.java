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
name|RelCollation
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
name|RelRoot
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
name|rex
operator|.
name|RexNode
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
name|ArrayBindable
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
name|schema
operator|.
name|Table
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
name|validate
operator|.
name|CyclicDefinitionException
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
name|tools
operator|.
name|RelRunner
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
name|ImmutableIntList
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|CalcitePrepareImpl
operator|::
operator|new
decl_stmt|;
name|ThreadLocal
argument_list|<
name|Deque
argument_list|<
name|Context
argument_list|>
argument_list|>
name|THREAD_CONTEXT_STACK
init|=
name|ThreadLocal
operator|.
name|withInitial
argument_list|(
name|ArrayDeque
operator|::
operator|new
argument_list|)
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
comment|/** Executes a DDL statement.    *    *<p>The statement identified itself as DDL in the    * {@link org.apache.calcite.jdbc.CalcitePrepare.ParseResult#kind} field. */
name|void
name|executeDdl
parameter_list|(
name|Context
name|context
parameter_list|,
name|SqlNode
name|node
parameter_list|)
function_decl|;
comment|/** Analyzes a view.    *    * @param context Context    * @param sql View SQL    * @param fail Whether to fail (and throw a descriptive error message) if the    *             view is not modifiable    * @return Result of analyzing the view    */
name|AnalyzeViewResult
name|analyzeView
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|sql
parameter_list|,
name|boolean
name|fail
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
name|Query
argument_list|<
name|T
argument_list|>
name|query
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|long
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
comment|/** Returns the root schema for statements that need a read-consistent      * snapshot. */
name|CalciteSchema
name|getRootSchema
parameter_list|()
function_decl|;
comment|/** Returns the root schema for statements that need to be able to modify      * schemas and have the results available to other statements. Viz, DDL      * statements. */
name|CalciteSchema
name|getMutableRootSchema
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
comment|/** Returns the path of the object being analyzed, or null.      *      *<p>The object is being analyzed is typically a view. If it is already      * being analyzed further up the stack, the view definition can be deduced      * to be cyclic. */
name|List
argument_list|<
name|String
argument_list|>
name|getObjectPath
parameter_list|()
function_decl|;
comment|/** Gets a runner; it can execute a relational expression. */
name|RelRunner
name|getRelRunner
parameter_list|()
function_decl|;
block|}
comment|/** Callback to register Spark as the main engine. */
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
name|ArrayBindable
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
class|class
name|Dummy
block|{
specifier|private
specifier|static
name|SparkHandler
name|sparkHandler
decl_stmt|;
specifier|private
name|Dummy
parameter_list|()
block|{
block|}
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
decl||
name|ClassCastException
decl||
name|InvocationTargetException
decl||
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
specifier|final
name|Deque
argument_list|<
name|Context
argument_list|>
name|stack
init|=
name|THREAD_CONTEXT_STACK
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
init|=
name|context
operator|.
name|getObjectPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Context
name|context1
range|:
name|stack
control|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path1
init|=
name|context1
operator|.
name|getObjectPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|path1
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CyclicDefinitionException
argument_list|(
name|stack
operator|.
name|size
argument_list|()
argument_list|,
name|path
argument_list|)
throw|;
block|}
block|}
block|}
name|stack
operator|.
name|push
argument_list|(
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
name|THREAD_CONTEXT_STACK
operator|.
name|get
argument_list|()
operator|.
name|peek
argument_list|()
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
name|Context
name|x
init|=
name|THREAD_CONTEXT_STACK
operator|.
name|get
argument_list|()
operator|.
name|pop
argument_list|()
decl_stmt|;
assert|assert
name|x
operator|==
name|context
assert|;
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
name|ArrayBindable
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
comment|/** Returns the kind of statement.      *      *<p>Possibilities include:      *      *<ul>      *<li>Queries: usually {@link SqlKind#SELECT}, but      *   other query operators such as {@link SqlKind#UNION} and      *   {@link SqlKind#ORDER_BY} are possible      *<li>DML statements: {@link SqlKind#INSERT}, {@link SqlKind#UPDATE} etc.      *<li>Session control statements: {@link SqlKind#COMMIT}      *<li>DDL statements: {@link SqlKind#CREATE_TABLE},      *   {@link SqlKind#DROP_INDEX}      *</ul>      *      * @return Kind of statement, never null      */
specifier|public
name|SqlKind
name|kind
parameter_list|()
block|{
return|return
name|sqlNode
operator|.
name|getKind
argument_list|()
return|;
block|}
block|}
comment|/** The result of parsing and validating a SQL query and converting it to    * relational algebra. */
class|class
name|ConvertResult
extends|extends
name|ParseResult
block|{
specifier|public
specifier|final
name|RelRoot
name|root
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
name|RelRoot
name|root
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
name|root
operator|=
name|root
expr_stmt|;
block|}
block|}
comment|/** The result of analyzing a view. */
class|class
name|AnalyzeViewResult
extends|extends
name|ConvertResult
block|{
comment|/** Not null if and only if the view is modifiable. */
specifier|public
specifier|final
name|Table
name|table
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|tablePath
decl_stmt|;
specifier|public
specifier|final
name|RexNode
name|constraint
decl_stmt|;
specifier|public
specifier|final
name|ImmutableIntList
name|columnMapping
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|modifiable
decl_stmt|;
specifier|public
name|AnalyzeViewResult
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
name|RelRoot
name|root
parameter_list|,
name|Table
name|table
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|tablePath
parameter_list|,
name|RexNode
name|constraint
parameter_list|,
name|ImmutableIntList
name|columnMapping
parameter_list|,
name|boolean
name|modifiable
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
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|this
operator|.
name|tablePath
operator|=
name|tablePath
expr_stmt|;
name|this
operator|.
name|constraint
operator|=
name|constraint
expr_stmt|;
name|this
operator|.
name|columnMapping
operator|=
name|columnMapping
expr_stmt|;
name|this
operator|.
name|modifiable
operator|=
name|modifiable
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|modifiable
operator|==
operator|(
name|table
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** The result of preparing a query. It gives the Avatica driver framework    * the information it needs to create a prepared statement, or to execute a    * statement directly, without an explicit prepare step.    *    * @param<T> element type */
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
annotation|@
name|JsonIgnore
specifier|public
specifier|final
name|CalciteSchema
name|rootSchema
decl_stmt|;
annotation|@
name|JsonIgnore
specifier|private
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
decl_stmt|;
specifier|private
specifier|final
name|long
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
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|CalciteSchema
name|rootSchema
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|,
name|long
name|maxRowCount
parameter_list|,
name|Bindable
argument_list|<
name|T
argument_list|>
name|bindable
parameter_list|)
block|{
name|this
argument_list|(
name|sql
argument_list|,
name|parameterList
argument_list|,
name|internalParameters
argument_list|,
name|rowType
argument_list|,
name|columns
argument_list|,
name|cursorFactory
argument_list|,
name|rootSchema
argument_list|,
name|collationList
argument_list|,
name|maxRowCount
argument_list|,
name|bindable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|CalciteSchema
name|rootSchema
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|,
name|long
name|maxRowCount
parameter_list|,
name|Bindable
argument_list|<
name|T
argument_list|>
name|bindable
parameter_list|,
name|Meta
operator|.
name|StatementType
name|statementType
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
argument_list|,
name|statementType
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
name|rootSchema
operator|=
name|rootSchema
expr_stmt|;
name|this
operator|.
name|collationList
operator|=
name|collationList
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
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|collationList
return|;
block|}
block|}
comment|/** A union type of the three possible ways of expressing a query: as a SQL    * string, a {@link Queryable} or a {@link RelNode}. Exactly one must be    * provided.    *    * @param<T> element type */
class|class
name|Query
parameter_list|<
name|T
parameter_list|>
block|{
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
specifier|public
specifier|final
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
decl_stmt|;
specifier|public
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|private
name|Query
parameter_list|(
name|String
name|sql
parameter_list|,
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|queryable
operator|=
name|queryable
expr_stmt|;
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
assert|assert
operator|(
name|sql
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
operator|+
operator|(
name|queryable
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
operator|+
operator|(
name|rel
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
operator|==
literal|1
assert|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Query
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Query
argument_list|<>
argument_list|(
name|sql
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Query
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
return|return
operator|new
name|Query
argument_list|<>
argument_list|(
literal|null
argument_list|,
name|queryable
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Query
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|Query
argument_list|<>
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End CalcitePrepare.java
end_comment

end_unit

