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
name|tools
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
name|config
operator|.
name|CalciteConnectionProperty
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
name|jdbc
operator|.
name|CalciteSchema
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
name|Context
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
name|RelOptCostFactory
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
name|RelTraitDef
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
name|prepare
operator|.
name|PlannerImpl
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
name|RelDataTypeSystem
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
name|RexExecutor
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
name|SchemaPlus
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
name|server
operator|.
name|CalciteServerStatement
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
name|SqlOperatorTable
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
name|sql
operator|.
name|parser
operator|.
name|SqlParser
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
name|sql2rel
operator|.
name|SqlRexConvertletTable
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
name|sql2rel
operator|.
name|SqlToRelConverter
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
name|sql2rel
operator|.
name|StandardConvertletTable
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
name|statistic
operator|.
name|QuerySqlStatisticProvider
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
name|collect
operator|.
name|ImmutableList
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
name|DriverManager
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Tools for invoking Calcite functionality without initializing a container /  * server first.  */
end_comment

begin_class
specifier|public
class|class
name|Frameworks
block|{
specifier|private
name|Frameworks
parameter_list|()
block|{
block|}
comment|/**    * Creates a planner.    *    * @param config Planner configuration    * @return Planner    */
specifier|public
specifier|static
name|Planner
name|getPlanner
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
block|{
return|return
operator|new
name|PlannerImpl
argument_list|(
name|config
argument_list|)
return|;
block|}
comment|/** Piece of code to be run in a context where a planner is available. The    * planner is accessible from the {@code cluster} parameter, as are several    * other useful objects.    *    * @param<R> result type */
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|PlannerAction
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
name|SchemaPlus
name|rootSchema
parameter_list|)
function_decl|;
block|}
comment|/** Piece of code to be run in a context where a planner and statement are    * available. The planner is accessible from the {@code cluster} parameter, as    * are several other useful objects. The connection and    * {@link org.apache.calcite.DataContext} are accessible from the    * statement.    *    * @param<R> result type */
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|BasePrepareAction
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
name|SchemaPlus
name|rootSchema
parameter_list|,
name|CalciteServerStatement
name|statement
parameter_list|)
function_decl|;
block|}
comment|/** As {@link BasePrepareAction} but with a {@link FrameworkConfig} included.    * Deprecated because a functional interface is more convenient.    *    * @param<R> result type */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|abstract
specifier|static
class|class
name|PrepareAction
parameter_list|<
name|R
parameter_list|>
implements|implements
name|BasePrepareAction
argument_list|<
name|R
argument_list|>
block|{
specifier|private
specifier|final
name|FrameworkConfig
name|config
decl_stmt|;
specifier|public
name|PrepareAction
parameter_list|()
block|{
name|this
operator|.
name|config
operator|=
name|newConfigBuilder
argument_list|()
operator|.
name|defaultSchema
argument_list|(
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
name|PrepareAction
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|FrameworkConfig
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
block|}
comment|/**    * Initializes a container then calls user-specified code with a planner.    *    * @param action Callback containing user-specified code    * @param config FrameworkConfig to use for planner action.    * @return Return value from action    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withPlanner
parameter_list|(
specifier|final
name|PlannerAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|,
specifier|final
name|FrameworkConfig
name|config
parameter_list|)
block|{
return|return
name|withPrepare
argument_list|(
name|config
argument_list|,
parameter_list|(
name|cluster
parameter_list|,
name|relOptSchema
parameter_list|,
name|rootSchema
parameter_list|,
name|statement
parameter_list|)
lambda|->
block|{
specifier|final
name|CalciteSchema
name|schema
init|=
name|CalciteSchema
operator|.
name|from
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|config
operator|.
name|getDefaultSchema
argument_list|()
argument_list|,
name|rootSchema
argument_list|)
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
name|schema
operator|.
name|root
argument_list|()
operator|.
name|plus
argument_list|()
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
comment|/**    * Initializes a container then calls user-specified code with a planner.    *    * @param action Callback containing user-specified code    * @return Return value from action    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withPlanner
parameter_list|(
specifier|final
name|PlannerAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|)
block|{
name|FrameworkConfig
name|config
init|=
name|newConfigBuilder
argument_list|()
comment|//
operator|.
name|defaultSchema
argument_list|(
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|withPlanner
argument_list|(
name|action
argument_list|,
name|config
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withPrepare
parameter_list|(
name|PrepareAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|)
block|{
return|return
name|withPrepare
argument_list|(
name|action
operator|.
name|getConfig
argument_list|()
argument_list|,
name|action
argument_list|)
return|;
block|}
comment|/** As {@link #withPrepare(FrameworkConfig, BasePrepareAction)} but using a    * default configuration. */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withPrepare
parameter_list|(
name|BasePrepareAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|)
block|{
specifier|final
name|FrameworkConfig
name|config
init|=
name|newConfigBuilder
argument_list|()
operator|.
name|defaultSchema
argument_list|(
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|withPrepare
argument_list|(
name|config
argument_list|,
name|action
argument_list|)
return|;
block|}
comment|/**    * Initializes a container then calls user-specified code with a planner    * and statement.    *    * @param action Callback containing user-specified code    * @return Return value from action    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withPrepare
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|,
name|BasePrepareAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getTypeSystem
argument_list|()
operator|!=
name|RelDataTypeSystem
operator|.
name|DEFAULT
condition|)
block|{
name|info
operator|.
name|setProperty
argument_list|(
name|CalciteConnectionProperty
operator|.
name|TYPE_SYSTEM
operator|.
name|camelName
argument_list|()
argument_list|,
name|config
operator|.
name|getTypeSystem
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
specifier|final
name|CalciteServerStatement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|unwrap
argument_list|(
name|CalciteServerStatement
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|CalcitePrepareImpl
argument_list|()
operator|.
name|perform
argument_list|(
name|statement
argument_list|,
name|config
argument_list|,
name|action
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
comment|/**    * Creates a root schema.    *    * @param addMetadataSchema Whether to add "metadata" schema containing    *    definitions of tables, columns etc.    */
specifier|public
specifier|static
name|SchemaPlus
name|createRootSchema
parameter_list|(
name|boolean
name|addMetadataSchema
parameter_list|)
block|{
return|return
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
name|addMetadataSchema
argument_list|)
operator|.
name|plus
argument_list|()
return|;
block|}
comment|/** Creates a config builder with each setting initialized to its default    * value. */
specifier|public
specifier|static
name|ConfigBuilder
name|newConfigBuilder
parameter_list|()
block|{
return|return
operator|new
name|ConfigBuilder
argument_list|()
return|;
block|}
comment|/** Creates a config builder initializing each setting from an existing    * config.    *    *<p>So, {@code newConfigBuilder(config).build()} will return a    * value equal to {@code config}. */
specifier|public
specifier|static
name|ConfigBuilder
name|newConfigBuilder
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
block|{
return|return
operator|new
name|ConfigBuilder
argument_list|(
name|config
argument_list|)
return|;
block|}
comment|/**    * A builder to help you build a {@link FrameworkConfig} using defaults    * where values aren't required.    */
specifier|public
specifier|static
class|class
name|ConfigBuilder
block|{
specifier|private
name|SqlRexConvertletTable
name|convertletTable
decl_stmt|;
specifier|private
name|SqlOperatorTable
name|operatorTable
decl_stmt|;
specifier|private
name|ImmutableList
argument_list|<
name|Program
argument_list|>
name|programs
decl_stmt|;
specifier|private
name|Context
name|context
decl_stmt|;
specifier|private
name|ImmutableList
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
decl_stmt|;
specifier|private
name|SqlParser
operator|.
name|Config
name|parserConfig
decl_stmt|;
specifier|private
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConverterConfig
decl_stmt|;
specifier|private
name|SchemaPlus
name|defaultSchema
decl_stmt|;
specifier|private
name|RexExecutor
name|executor
decl_stmt|;
specifier|private
name|RelOptCostFactory
name|costFactory
decl_stmt|;
specifier|private
name|RelDataTypeSystem
name|typeSystem
decl_stmt|;
specifier|private
name|boolean
name|evolveLattice
decl_stmt|;
specifier|private
name|SqlStatisticProvider
name|statisticProvider
decl_stmt|;
specifier|private
name|RelOptTable
operator|.
name|ViewExpander
name|viewExpander
decl_stmt|;
comment|/** Creates a ConfigBuilder, initializing to defaults. */
specifier|private
name|ConfigBuilder
parameter_list|()
block|{
name|convertletTable
operator|=
name|StandardConvertletTable
operator|.
name|INSTANCE
expr_stmt|;
name|operatorTable
operator|=
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
expr_stmt|;
name|programs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
name|context
operator|=
name|Contexts
operator|.
name|empty
argument_list|()
expr_stmt|;
name|parserConfig
operator|=
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
expr_stmt|;
name|sqlToRelConverterConfig
operator|=
name|SqlToRelConverter
operator|.
name|Config
operator|.
name|DEFAULT
expr_stmt|;
name|typeSystem
operator|=
name|RelDataTypeSystem
operator|.
name|DEFAULT
expr_stmt|;
name|evolveLattice
operator|=
literal|false
expr_stmt|;
name|statisticProvider
operator|=
name|QuerySqlStatisticProvider
operator|.
name|SILENT_CACHING_INSTANCE
expr_stmt|;
block|}
comment|/** Creates a ConfigBuilder, initializing from an existing config. */
specifier|private
name|ConfigBuilder
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
block|{
name|convertletTable
operator|=
name|config
operator|.
name|getConvertletTable
argument_list|()
expr_stmt|;
name|operatorTable
operator|=
name|config
operator|.
name|getOperatorTable
argument_list|()
expr_stmt|;
name|programs
operator|=
name|config
operator|.
name|getPrograms
argument_list|()
expr_stmt|;
name|context
operator|=
name|config
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|traitDefs
operator|=
name|config
operator|.
name|getTraitDefs
argument_list|()
expr_stmt|;
name|parserConfig
operator|=
name|config
operator|.
name|getParserConfig
argument_list|()
expr_stmt|;
name|sqlToRelConverterConfig
operator|=
name|config
operator|.
name|getSqlToRelConverterConfig
argument_list|()
expr_stmt|;
name|defaultSchema
operator|=
name|config
operator|.
name|getDefaultSchema
argument_list|()
expr_stmt|;
name|executor
operator|=
name|config
operator|.
name|getExecutor
argument_list|()
expr_stmt|;
name|costFactory
operator|=
name|config
operator|.
name|getCostFactory
argument_list|()
expr_stmt|;
name|typeSystem
operator|=
name|config
operator|.
name|getTypeSystem
argument_list|()
expr_stmt|;
name|evolveLattice
operator|=
name|config
operator|.
name|isEvolveLattice
argument_list|()
expr_stmt|;
name|statisticProvider
operator|=
name|config
operator|.
name|getStatisticProvider
argument_list|()
expr_stmt|;
block|}
specifier|public
name|FrameworkConfig
name|build
parameter_list|()
block|{
return|return
operator|new
name|StdFrameworkConfig
argument_list|(
name|context
argument_list|,
name|convertletTable
argument_list|,
name|operatorTable
argument_list|,
name|programs
argument_list|,
name|traitDefs
argument_list|,
name|parserConfig
argument_list|,
name|sqlToRelConverterConfig
argument_list|,
name|defaultSchema
argument_list|,
name|costFactory
argument_list|,
name|typeSystem
argument_list|,
name|executor
argument_list|,
name|evolveLattice
argument_list|,
name|statisticProvider
argument_list|,
name|viewExpander
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|context
parameter_list|(
name|Context
name|c
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|executor
parameter_list|(
name|RexExecutor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|executor
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|convertletTable
parameter_list|(
name|SqlRexConvertletTable
name|convertletTable
parameter_list|)
block|{
name|this
operator|.
name|convertletTable
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|convertletTable
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|operatorTable
parameter_list|(
name|SqlOperatorTable
name|operatorTable
parameter_list|)
block|{
name|this
operator|.
name|operatorTable
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|operatorTable
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|traitDefs
parameter_list|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
parameter_list|)
block|{
if|if
condition|(
name|traitDefs
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|traitDefs
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|traitDefs
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|traitDefs
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|traitDefs
parameter_list|(
name|RelTraitDef
modifier|...
name|traitDefs
parameter_list|)
block|{
name|this
operator|.
name|traitDefs
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|traitDefs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|parserConfig
parameter_list|(
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|)
block|{
name|this
operator|.
name|parserConfig
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parserConfig
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|sqlToRelConverterConfig
parameter_list|(
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConverterConfig
parameter_list|)
block|{
name|this
operator|.
name|sqlToRelConverterConfig
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|sqlToRelConverterConfig
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|defaultSchema
parameter_list|(
name|SchemaPlus
name|defaultSchema
parameter_list|)
block|{
name|this
operator|.
name|defaultSchema
operator|=
name|defaultSchema
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|costFactory
parameter_list|(
name|RelOptCostFactory
name|costFactory
parameter_list|)
block|{
name|this
operator|.
name|costFactory
operator|=
name|costFactory
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|ruleSets
parameter_list|(
name|RuleSet
modifier|...
name|ruleSets
parameter_list|)
block|{
return|return
name|programs
argument_list|(
name|Programs
operator|.
name|listOf
argument_list|(
name|ruleSets
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|ruleSets
parameter_list|(
name|List
argument_list|<
name|RuleSet
argument_list|>
name|ruleSets
parameter_list|)
block|{
return|return
name|programs
argument_list|(
name|Programs
operator|.
name|listOf
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|ruleSets
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|programs
parameter_list|(
name|List
argument_list|<
name|Program
argument_list|>
name|programs
parameter_list|)
block|{
name|this
operator|.
name|programs
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|programs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|programs
parameter_list|(
name|Program
modifier|...
name|programs
parameter_list|)
block|{
name|this
operator|.
name|programs
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|programs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|typeSystem
parameter_list|(
name|RelDataTypeSystem
name|typeSystem
parameter_list|)
block|{
name|this
operator|.
name|typeSystem
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|typeSystem
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|evolveLattice
parameter_list|(
name|boolean
name|evolveLattice
parameter_list|)
block|{
name|this
operator|.
name|evolveLattice
operator|=
name|evolveLattice
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|statisticProvider
parameter_list|(
name|SqlStatisticProvider
name|statisticProvider
parameter_list|)
block|{
name|this
operator|.
name|statisticProvider
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|statisticProvider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|viewExpander
parameter_list|(
name|RelOptTable
operator|.
name|ViewExpander
name|viewExpander
parameter_list|)
block|{
name|this
operator|.
name|viewExpander
operator|=
name|viewExpander
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|/**    * An implementation of {@link FrameworkConfig} that uses standard Calcite    * classes to provide basic planner functionality.    */
specifier|static
class|class
name|StdFrameworkConfig
implements|implements
name|FrameworkConfig
block|{
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
specifier|private
specifier|final
name|SqlRexConvertletTable
name|convertletTable
decl_stmt|;
specifier|private
specifier|final
name|SqlOperatorTable
name|operatorTable
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Program
argument_list|>
name|programs
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
decl_stmt|;
specifier|private
specifier|final
name|SqlParser
operator|.
name|Config
name|parserConfig
decl_stmt|;
specifier|private
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConverterConfig
decl_stmt|;
specifier|private
specifier|final
name|SchemaPlus
name|defaultSchema
decl_stmt|;
specifier|private
specifier|final
name|RelOptCostFactory
name|costFactory
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeSystem
name|typeSystem
decl_stmt|;
specifier|private
specifier|final
name|RexExecutor
name|executor
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|evolveLattice
decl_stmt|;
specifier|private
specifier|final
name|SqlStatisticProvider
name|statisticProvider
decl_stmt|;
specifier|private
specifier|final
name|RelOptTable
operator|.
name|ViewExpander
name|viewExpander
decl_stmt|;
name|StdFrameworkConfig
parameter_list|(
name|Context
name|context
parameter_list|,
name|SqlRexConvertletTable
name|convertletTable
parameter_list|,
name|SqlOperatorTable
name|operatorTable
parameter_list|,
name|ImmutableList
argument_list|<
name|Program
argument_list|>
name|programs
parameter_list|,
name|ImmutableList
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
parameter_list|,
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|,
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConverterConfig
parameter_list|,
name|SchemaPlus
name|defaultSchema
parameter_list|,
name|RelOptCostFactory
name|costFactory
parameter_list|,
name|RelDataTypeSystem
name|typeSystem
parameter_list|,
name|RexExecutor
name|executor
parameter_list|,
name|boolean
name|evolveLattice
parameter_list|,
name|SqlStatisticProvider
name|statisticProvider
parameter_list|,
name|RelOptTable
operator|.
name|ViewExpander
name|viewExpander
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|convertletTable
operator|=
name|convertletTable
expr_stmt|;
name|this
operator|.
name|operatorTable
operator|=
name|operatorTable
expr_stmt|;
name|this
operator|.
name|programs
operator|=
name|programs
expr_stmt|;
name|this
operator|.
name|traitDefs
operator|=
name|traitDefs
expr_stmt|;
name|this
operator|.
name|parserConfig
operator|=
name|parserConfig
expr_stmt|;
name|this
operator|.
name|sqlToRelConverterConfig
operator|=
name|sqlToRelConverterConfig
expr_stmt|;
name|this
operator|.
name|defaultSchema
operator|=
name|defaultSchema
expr_stmt|;
name|this
operator|.
name|costFactory
operator|=
name|costFactory
expr_stmt|;
name|this
operator|.
name|typeSystem
operator|=
name|typeSystem
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|evolveLattice
operator|=
name|evolveLattice
expr_stmt|;
name|this
operator|.
name|statisticProvider
operator|=
name|statisticProvider
expr_stmt|;
name|this
operator|.
name|viewExpander
operator|=
name|viewExpander
expr_stmt|;
block|}
specifier|public
name|SqlParser
operator|.
name|Config
name|getParserConfig
parameter_list|()
block|{
return|return
name|parserConfig
return|;
block|}
specifier|public
name|SqlToRelConverter
operator|.
name|Config
name|getSqlToRelConverterConfig
parameter_list|()
block|{
return|return
name|sqlToRelConverterConfig
return|;
block|}
specifier|public
name|SchemaPlus
name|getDefaultSchema
parameter_list|()
block|{
return|return
name|defaultSchema
return|;
block|}
specifier|public
name|RexExecutor
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|Program
argument_list|>
name|getPrograms
parameter_list|()
block|{
return|return
name|programs
return|;
block|}
specifier|public
name|RelOptCostFactory
name|getCostFactory
parameter_list|()
block|{
return|return
name|costFactory
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelTraitDef
argument_list|>
name|getTraitDefs
parameter_list|()
block|{
return|return
name|traitDefs
return|;
block|}
specifier|public
name|SqlRexConvertletTable
name|getConvertletTable
parameter_list|()
block|{
return|return
name|convertletTable
return|;
block|}
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|SqlOperatorTable
name|getOperatorTable
parameter_list|()
block|{
return|return
name|operatorTable
return|;
block|}
specifier|public
name|RelDataTypeSystem
name|getTypeSystem
parameter_list|()
block|{
return|return
name|typeSystem
return|;
block|}
specifier|public
name|boolean
name|isEvolveLattice
parameter_list|()
block|{
return|return
name|evolveLattice
return|;
block|}
specifier|public
name|SqlStatisticProvider
name|getStatisticProvider
parameter_list|()
block|{
return|return
name|statisticProvider
return|;
block|}
specifier|public
name|RelOptTable
operator|.
name|ViewExpander
name|getViewExpander
parameter_list|()
block|{
return|return
name|viewExpander
return|;
block|}
block|}
block|}
end_class

end_unit

