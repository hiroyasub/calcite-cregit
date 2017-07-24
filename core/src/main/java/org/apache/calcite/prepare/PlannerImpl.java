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
name|prepare
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
name|config
operator|.
name|CalciteConnectionConfigImpl
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
name|RelOptLattice
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
name|RelOptMaterialization
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
operator|.
name|ViewExpander
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
name|plan
operator|.
name|RelTraitSet
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
name|metadata
operator|.
name|CachingRelMetadataProvider
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
name|rex
operator|.
name|RexBuilder
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
name|parser
operator|.
name|SqlParseException
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlConformanceEnum
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
name|sql2rel
operator|.
name|RelDecorrelator
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
name|tools
operator|.
name|FrameworkConfig
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
name|Planner
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
name|Program
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
name|RelConversionException
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
name|ValidationException
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
name|Properties
import|;
end_import

begin_comment
comment|/** Implementation of {@link org.apache.calcite.tools.Planner}. */
end_comment

begin_class
specifier|public
class|class
name|PlannerImpl
implements|implements
name|Planner
block|{
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
name|FrameworkConfig
name|config
decl_stmt|;
comment|/** Holds the trait definitions to be registered with planner. May be null. */
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
name|SqlRexConvertletTable
name|convertletTable
decl_stmt|;
specifier|private
name|State
name|state
decl_stmt|;
comment|// set in STATE_1_RESET
specifier|private
name|boolean
name|open
decl_stmt|;
comment|// set in STATE_2_READY
specifier|private
name|SchemaPlus
name|defaultSchema
decl_stmt|;
specifier|private
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
name|RexExecutor
name|executor
decl_stmt|;
comment|// set in STATE_4_VALIDATE
specifier|private
name|CalciteSqlValidator
name|validator
decl_stmt|;
specifier|private
name|SqlNode
name|validatedSqlNode
decl_stmt|;
comment|// set in STATE_5_CONVERT
specifier|private
name|RelRoot
name|root
decl_stmt|;
comment|/** Creates a planner. Not a public API; call    * {@link org.apache.calcite.tools.Frameworks#getPlanner} instead. */
specifier|public
name|PlannerImpl
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
name|this
operator|.
name|defaultSchema
operator|=
name|config
operator|.
name|getDefaultSchema
argument_list|()
expr_stmt|;
name|this
operator|.
name|operatorTable
operator|=
name|config
operator|.
name|getOperatorTable
argument_list|()
expr_stmt|;
name|this
operator|.
name|programs
operator|=
name|config
operator|.
name|getPrograms
argument_list|()
expr_stmt|;
name|this
operator|.
name|parserConfig
operator|=
name|config
operator|.
name|getParserConfig
argument_list|()
expr_stmt|;
name|this
operator|.
name|sqlToRelConverterConfig
operator|=
name|config
operator|.
name|getSqlToRelConverterConfig
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|State
operator|.
name|STATE_0_CLOSED
expr_stmt|;
name|this
operator|.
name|traitDefs
operator|=
name|config
operator|.
name|getTraitDefs
argument_list|()
expr_stmt|;
name|this
operator|.
name|convertletTable
operator|=
name|config
operator|.
name|getConvertletTable
argument_list|()
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|config
operator|.
name|getExecutor
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/** Makes sure that the state is at least the given state. */
specifier|private
name|void
name|ensure
parameter_list|(
name|State
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
name|this
operator|.
name|state
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|state
operator|.
name|ordinal
argument_list|()
operator|<
name|this
operator|.
name|state
operator|.
name|ordinal
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot move to "
operator|+
name|state
operator|+
literal|" from "
operator|+
name|this
operator|.
name|state
argument_list|)
throw|;
block|}
name|state
operator|.
name|from
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelTraitSet
name|getEmptyTraitSet
parameter_list|()
block|{
return|return
name|planner
operator|.
name|emptyTraitSet
argument_list|()
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|open
operator|=
literal|false
expr_stmt|;
name|typeFactory
operator|=
literal|null
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|STATE_0_CLOSED
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|ensure
argument_list|(
name|State
operator|.
name|STATE_0_CLOSED
argument_list|)
expr_stmt|;
name|open
operator|=
literal|true
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|STATE_1_RESET
expr_stmt|;
block|}
specifier|private
name|void
name|ready
parameter_list|()
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|STATE_0_CLOSED
case|:
name|reset
argument_list|()
expr_stmt|;
block|}
name|ensure
argument_list|(
name|State
operator|.
name|STATE_1_RESET
argument_list|)
expr_stmt|;
name|Frameworks
operator|.
name|withPlanner
argument_list|(
operator|new
name|Frameworks
operator|.
name|PlannerAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
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
block|{
name|Util
operator|.
name|discard
argument_list|(
name|rootSchema
argument_list|)
expr_stmt|;
comment|// use our own defaultSchema
name|typeFactory
operator|=
operator|(
name|JavaTypeFactory
operator|)
name|cluster
operator|.
name|getTypeFactory
argument_list|()
expr_stmt|;
name|planner
operator|=
name|cluster
operator|.
name|getPlanner
argument_list|()
expr_stmt|;
name|planner
operator|.
name|setExecutor
argument_list|(
name|executor
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|STATE_2_READY
expr_stmt|;
comment|// If user specify own traitDef, instead of default default trait,
comment|// first, clear the default trait def registered with planner
comment|// then, register the trait def specified in traitDefs.
if|if
condition|(
name|this
operator|.
name|traitDefs
operator|!=
literal|null
condition|)
block|{
name|planner
operator|.
name|clearRelTraitDefs
argument_list|()
expr_stmt|;
for|for
control|(
name|RelTraitDef
name|def
range|:
name|this
operator|.
name|traitDefs
control|)
block|{
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|def
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|SqlNode
name|parse
parameter_list|(
specifier|final
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|STATE_0_CLOSED
case|:
case|case
name|STATE_1_RESET
case|:
name|ready
argument_list|()
expr_stmt|;
block|}
name|ensure
argument_list|(
name|State
operator|.
name|STATE_2_READY
argument_list|)
expr_stmt|;
name|SqlParser
name|parser
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|,
name|parserConfig
argument_list|)
decl_stmt|;
name|SqlNode
name|sqlNode
init|=
name|parser
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
name|state
operator|=
name|State
operator|.
name|STATE_3_PARSED
expr_stmt|;
return|return
name|sqlNode
return|;
block|}
specifier|public
name|SqlNode
name|validate
parameter_list|(
name|SqlNode
name|sqlNode
parameter_list|)
throws|throws
name|ValidationException
block|{
name|ensure
argument_list|(
name|State
operator|.
name|STATE_3_PARSED
argument_list|)
expr_stmt|;
specifier|final
name|SqlConformance
name|conformance
init|=
name|conformance
argument_list|()
decl_stmt|;
specifier|final
name|CalciteCatalogReader
name|catalogReader
init|=
name|createCatalogReader
argument_list|()
decl_stmt|;
name|this
operator|.
name|validator
operator|=
operator|new
name|CalciteSqlValidator
argument_list|(
name|operatorTable
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|conformance
argument_list|)
expr_stmt|;
name|this
operator|.
name|validator
operator|.
name|setIdentifierExpansion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|validatedSqlNode
operator|=
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ValidationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|state
operator|=
name|State
operator|.
name|STATE_4_VALIDATED
expr_stmt|;
return|return
name|validatedSqlNode
return|;
block|}
specifier|private
name|SqlConformance
name|conformance
parameter_list|()
block|{
specifier|final
name|Context
name|context
init|=
name|config
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
specifier|final
name|CalciteConnectionConfig
name|connectionConfig
init|=
name|context
operator|.
name|unwrap
argument_list|(
name|CalciteConnectionConfig
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|connectionConfig
operator|!=
literal|null
condition|)
block|{
return|return
name|connectionConfig
operator|.
name|conformance
argument_list|()
return|;
block|}
block|}
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|RelDataType
argument_list|>
name|validateAndGetType
parameter_list|(
name|SqlNode
name|sqlNode
parameter_list|)
throws|throws
name|ValidationException
block|{
specifier|final
name|SqlNode
name|validatedNode
init|=
name|this
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|this
operator|.
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|validatedNode
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|validatedNode
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|final
name|RelNode
name|convert
parameter_list|(
name|SqlNode
name|sql
parameter_list|)
throws|throws
name|RelConversionException
block|{
return|return
name|rel
argument_list|(
name|sql
argument_list|)
operator|.
name|rel
return|;
block|}
specifier|public
name|RelRoot
name|rel
parameter_list|(
name|SqlNode
name|sql
parameter_list|)
throws|throws
name|RelConversionException
block|{
name|ensure
argument_list|(
name|State
operator|.
name|STATE_4_VALIDATED
argument_list|)
expr_stmt|;
assert|assert
name|validatedSqlNode
operator|!=
literal|null
assert|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|createRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|RelOptCluster
operator|.
name|create
argument_list|(
name|planner
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|config
init|=
name|SqlToRelConverter
operator|.
name|configBuilder
argument_list|()
operator|.
name|withConfig
argument_list|(
name|sqlToRelConverterConfig
argument_list|)
operator|.
name|withTrimUnusedFields
argument_list|(
literal|false
argument_list|)
operator|.
name|withConvertTableAccess
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SqlToRelConverter
name|sqlToRelConverter
init|=
operator|new
name|SqlToRelConverter
argument_list|(
operator|new
name|ViewExpanderImpl
argument_list|()
argument_list|,
name|validator
argument_list|,
name|createCatalogReader
argument_list|()
argument_list|,
name|cluster
argument_list|,
name|convertletTable
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|root
operator|=
name|sqlToRelConverter
operator|.
name|convertQuery
argument_list|(
name|validatedSqlNode
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|=
name|root
operator|.
name|withRel
argument_list|(
name|sqlToRelConverter
operator|.
name|flattenTypes
argument_list|(
name|root
operator|.
name|rel
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
name|root
operator|.
name|withRel
argument_list|(
name|RelDecorrelator
operator|.
name|decorrelateQuery
argument_list|(
name|root
operator|.
name|rel
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|STATE_5_CONVERTED
expr_stmt|;
return|return
name|root
return|;
block|}
comment|/** Implements {@link org.apache.calcite.plan.RelOptTable.ViewExpander}    * interface for {@link org.apache.calcite.tools.Planner}. */
specifier|public
class|class
name|ViewExpanderImpl
implements|implements
name|ViewExpander
block|{
annotation|@
name|Override
specifier|public
name|RelRoot
name|expandView
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|queryString
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|)
block|{
name|SqlParser
name|parser
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|queryString
argument_list|,
name|parserConfig
argument_list|)
decl_stmt|;
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parser
operator|.
name|parseQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"parse failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|SqlConformance
name|conformance
init|=
name|conformance
argument_list|()
decl_stmt|;
specifier|final
name|CalciteCatalogReader
name|catalogReader
init|=
name|createCatalogReader
argument_list|()
operator|.
name|withSchemaPath
argument_list|(
name|schemaPath
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
operator|new
name|CalciteSqlValidator
argument_list|(
name|operatorTable
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|conformance
argument_list|)
decl_stmt|;
name|validator
operator|.
name|setIdentifierExpansion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|SqlNode
name|validatedSqlNode
init|=
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|createRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|RelOptCluster
operator|.
name|create
argument_list|(
name|planner
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|config
init|=
name|SqlToRelConverter
operator|.
name|configBuilder
argument_list|()
operator|.
name|withConfig
argument_list|(
name|sqlToRelConverterConfig
argument_list|)
operator|.
name|withTrimUnusedFields
argument_list|(
literal|false
argument_list|)
operator|.
name|withConvertTableAccess
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SqlToRelConverter
name|sqlToRelConverter
init|=
operator|new
name|SqlToRelConverter
argument_list|(
operator|new
name|ViewExpanderImpl
argument_list|()
argument_list|,
name|validator
argument_list|,
name|catalogReader
argument_list|,
name|cluster
argument_list|,
name|convertletTable
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|root
operator|=
name|sqlToRelConverter
operator|.
name|convertQuery
argument_list|(
name|validatedSqlNode
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|root
operator|=
name|root
operator|.
name|withRel
argument_list|(
name|sqlToRelConverter
operator|.
name|flattenTypes
argument_list|(
name|root
operator|.
name|rel
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
name|root
operator|.
name|withRel
argument_list|(
name|RelDecorrelator
operator|.
name|decorrelateQuery
argument_list|(
name|root
operator|.
name|rel
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|PlannerImpl
operator|.
name|this
operator|.
name|root
return|;
block|}
block|}
comment|// CalciteCatalogReader is stateless; no need to store one
specifier|private
name|CalciteCatalogReader
name|createCatalogReader
parameter_list|()
block|{
name|SchemaPlus
name|rootSchema
init|=
name|rootSchema
argument_list|(
name|defaultSchema
argument_list|)
decl_stmt|;
name|Context
name|context
init|=
name|config
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|CalciteConnectionConfig
name|connectionConfig
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|connectionConfig
operator|=
name|context
operator|.
name|unwrap
argument_list|(
name|CalciteConnectionConfig
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CASE_SENSITIVE
operator|.
name|camelName
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|parserConfig
operator|.
name|caseSensitive
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connectionConfig
operator|=
operator|new
name|CalciteConnectionConfigImpl
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CalciteCatalogReader
argument_list|(
name|CalciteSchema
operator|.
name|from
argument_list|(
name|rootSchema
argument_list|)
argument_list|,
name|CalciteSchema
operator|.
name|from
argument_list|(
name|defaultSchema
argument_list|)
operator|.
name|path
argument_list|(
literal|null
argument_list|)
argument_list|,
name|typeFactory
argument_list|,
name|connectionConfig
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|SchemaPlus
name|rootSchema
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|schema
operator|.
name|getParentSchema
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|schema
return|;
block|}
name|schema
operator|=
name|schema
operator|.
name|getParentSchema
argument_list|()
expr_stmt|;
block|}
block|}
comment|// RexBuilder is stateless; no need to store one
specifier|private
name|RexBuilder
name|createRexBuilder
parameter_list|()
block|{
return|return
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
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
name|RelNode
name|transform
parameter_list|(
name|int
name|ruleSetIndex
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|,
name|RelNode
name|rel
parameter_list|)
throws|throws
name|RelConversionException
block|{
name|ensure
argument_list|(
name|State
operator|.
name|STATE_5_CONVERTED
argument_list|)
expr_stmt|;
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|setMetadataProvider
argument_list|(
operator|new
name|CachingRelMetadataProvider
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getMetadataProvider
argument_list|()
argument_list|,
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Program
name|program
init|=
name|programs
operator|.
name|get
argument_list|(
name|ruleSetIndex
argument_list|)
decl_stmt|;
return|return
name|program
operator|.
name|run
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
name|requiredOutputTraits
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelOptMaterialization
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelOptLattice
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/** Stage of a statement in the query-preparation lifecycle. */
specifier|private
enum|enum
name|State
block|{
name|STATE_0_CLOSED
block|{
annotation|@
name|Override
name|void
name|from
parameter_list|(
name|PlannerImpl
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|,
name|STATE_1_RESET
block|{
annotation|@
name|Override
name|void
name|from
parameter_list|(
name|PlannerImpl
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|ensure
argument_list|(
name|STATE_0_CLOSED
argument_list|)
expr_stmt|;
name|planner
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|,
name|STATE_2_READY
block|{
annotation|@
name|Override
name|void
name|from
parameter_list|(
name|PlannerImpl
name|planner
parameter_list|)
block|{
name|STATE_1_RESET
operator|.
name|from
argument_list|(
name|planner
argument_list|)
expr_stmt|;
name|planner
operator|.
name|ready
argument_list|()
expr_stmt|;
block|}
block|}
block|,
name|STATE_3_PARSED
block|,
name|STATE_4_VALIDATED
block|,
name|STATE_5_CONVERTED
block|;
comment|/** Moves planner's state to this state. This must be a higher state. */
name|void
name|from
parameter_list|(
name|PlannerImpl
name|planner
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot move from "
operator|+
name|planner
operator|.
name|state
operator|+
literal|" to "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End PlannerImpl.java
end_comment

end_unit

