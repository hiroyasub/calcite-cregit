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
name|test
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
name|DataContexts
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
name|enumerable
operator|.
name|EnumerableTableScan
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
name|ReflectiveSchema
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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|MaterializationService
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
name|RelOptUtil
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
name|CalciteCatalogReader
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
name|core
operator|.
name|RelFactories
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
name|logical
operator|.
name|LogicalTableScan
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
name|RexExecutorImpl
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
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
name|TestUtil
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
name|function
operator|.
name|Predicate
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
comment|/**  * Abstract base class for testing materialized views.  *  * @see MaterializedViewFixture  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MaterializedViewTester
block|{
comment|/** Customizes materialization matching approach. */
specifier|protected
specifier|abstract
name|List
argument_list|<
name|RelNode
argument_list|>
name|optimize
parameter_list|(
name|RelNode
name|queryRel
parameter_list|,
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializationList
parameter_list|)
function_decl|;
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
name|void
name|checkMaterialize
parameter_list|(
name|MaterializedViewFixture
name|f
parameter_list|)
block|{
specifier|final
name|TestConfig
name|testConfig
init|=
name|build
argument_list|(
name|f
argument_list|)
decl_stmt|;
specifier|final
name|Predicate
argument_list|<
name|String
argument_list|>
name|checker
init|=
name|Util
operator|.
name|first
argument_list|(
name|f
operator|.
name|checker
argument_list|,
name|s
lambda|->
name|MaterializedViewFixture
operator|.
name|resultContains
argument_list|(
name|s
argument_list|,
literal|"EnumerableTableScan(table=[["
operator|+
name|testConfig
operator|.
name|defaultSchema
operator|+
literal|", MV0]]"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|substitutes
init|=
name|optimize
argument_list|(
name|testConfig
operator|.
name|queryRel
argument_list|,
name|testConfig
operator|.
name|materializationList
argument_list|)
decl_stmt|;
if|if
condition|(
name|substitutes
operator|.
name|stream
argument_list|()
operator|.
name|noneMatch
argument_list|(
name|sub
lambda|->
name|checker
operator|.
name|test
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|sub
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
name|StringBuilder
name|substituteMessages
operator|=
operator|new
name|StringBuilder
argument_list|()
block|;
for|for
control|(
name|RelNode
name|sub
range|:
name|substitutes
control|)
block|{
name|substituteMessages
operator|.
name|append
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|sub
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Materialized view failed to be matched by optimized results:\n"
operator|+
name|substituteMessages
argument_list|)
throw|;
block|}
block|}
end_class

begin_comment
comment|/** Checks that a given query cannot use a materialized view with a given    * definition. */
end_comment

begin_function
name|void
name|checkNoMaterialize
parameter_list|(
name|MaterializedViewFixture
name|f
parameter_list|)
block|{
specifier|final
name|TestConfig
name|testConfig
init|=
name|build
argument_list|(
name|f
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|results
init|=
name|optimize
argument_list|(
name|testConfig
operator|.
name|queryRel
argument_list|,
name|testConfig
operator|.
name|materializationList
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|results
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|!
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"MV0"
argument_list|)
operator|)
condition|)
block|{
return|return;
block|}
specifier|final
name|StringBuilder
name|errMsgBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|errMsgBuilder
operator|.
name|append
argument_list|(
literal|"Optimization succeeds out of expectation: "
argument_list|)
expr_stmt|;
for|for
control|(
name|RelNode
name|res
range|:
name|results
control|)
block|{
name|errMsgBuilder
operator|.
name|append
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|res
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
name|errMsgBuilder
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
end_function

begin_function
specifier|private
name|TestConfig
name|build
parameter_list|(
name|MaterializedViewFixture
name|f
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
name|cluster
operator|.
name|getPlanner
argument_list|()
operator|.
name|setExecutor
argument_list|(
operator|new
name|RexExecutorImpl
argument_list|(
name|DataContexts
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|SchemaPlus
name|defaultSchema
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|schemaSpec
operator|==
literal|null
condition|)
block|{
name|defaultSchema
operator|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|MaterializationTest
operator|.
name|HrFKUKSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|defaultSchema
operator|=
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|f
operator|.
name|schemaSpec
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|queryRel
init|=
name|toRel
argument_list|(
name|cluster
argument_list|,
name|rootSchema
argument_list|,
name|defaultSchema
argument_list|,
name|f
operator|.
name|query
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|mvs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|RelFactories
operator|.
name|LOGICAL_BUILDER
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|relOptSchema
argument_list|)
decl_stmt|;
specifier|final
name|MaterializationService
operator|.
name|DefaultTableFactory
name|tableFactory
init|=
operator|new
name|MaterializationService
operator|.
name|DefaultTableFactory
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pair
range|:
name|f
operator|.
name|materializationList
control|)
block|{
name|String
name|sql
init|=
name|requireNonNull
argument_list|(
name|pair
operator|.
name|left
argument_list|,
literal|"sql"
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|mvRel
init|=
name|toRel
argument_list|(
name|cluster
argument_list|,
name|rootSchema
argument_list|,
name|defaultSchema
argument_list|,
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|tableFactory
operator|.
name|createTable
argument_list|(
name|CalciteSchema
operator|.
name|from
argument_list|(
name|rootSchema
argument_list|)
argument_list|,
name|sql
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|defaultSchema
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|requireNonNull
argument_list|(
name|pair
operator|.
name|right
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|defaultSchema
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|scan
argument_list|(
name|defaultSchema
operator|.
name|getName
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
specifier|final
name|LogicalTableScan
name|logicalScan
init|=
operator|(
name|LogicalTableScan
operator|)
name|relBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|EnumerableTableScan
name|replacement
init|=
name|EnumerableTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|logicalScan
operator|.
name|getTable
argument_list|()
argument_list|)
decl_stmt|;
name|mvs
operator|.
name|add
argument_list|(
operator|new
name|RelOptMaterialization
argument_list|(
name|replacement
argument_list|,
name|mvRel
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|defaultSchema
operator|.
name|getName
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TestConfig
argument_list|(
name|defaultSchema
operator|.
name|getName
argument_list|()
argument_list|,
name|queryRel
argument_list|,
name|mvs
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
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
specifier|private
name|RelNode
name|toRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|,
name|SchemaPlus
name|defaultSchema
parameter_list|,
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
block|{
specifier|final
name|SqlParser
name|parser
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|,
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|parsed
init|=
name|parser
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
specifier|final
name|CalciteCatalogReader
name|catalogReader
init|=
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
operator|new
name|JavaTypeFactoryImpl
argument_list|()
argument_list|,
name|CalciteConnectionConfig
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|SqlValidatorUtil
operator|.
name|newValidator
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
name|catalogReader
argument_list|,
operator|new
name|JavaTypeFactoryImpl
argument_list|()
argument_list|,
name|SqlValidator
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|validated
init|=
name|validator
operator|.
name|validate
argument_list|(
name|parsed
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
name|config
argument_list|()
operator|.
name|withTrimUnusedFields
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|withDecorrelationEnabled
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|SqlToRelConverter
name|converter
init|=
operator|new
name|SqlToRelConverter
argument_list|(
parameter_list|(
name|rowType
parameter_list|,
name|queryString
parameter_list|,
name|schemaPath
parameter_list|,
name|viewPath
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot expand view"
argument_list|)
throw|;
block|}
argument_list|,
name|validator
argument_list|,
name|catalogReader
argument_list|,
name|cluster
argument_list|,
name|StandardConvertletTable
operator|.
name|INSTANCE
argument_list|,
name|config
argument_list|)
decl_stmt|;
return|return
name|converter
operator|.
name|convertQuery
argument_list|(
name|validated
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|.
name|rel
return|;
block|}
end_function

begin_comment
comment|/**    * Processed testing definition.    */
end_comment

begin_class
specifier|private
specifier|static
class|class
name|TestConfig
block|{
specifier|final
name|String
name|defaultSchema
decl_stmt|;
specifier|final
name|RelNode
name|queryRel
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializationList
decl_stmt|;
name|TestConfig
parameter_list|(
name|String
name|defaultSchema
parameter_list|,
name|RelNode
name|queryRel
parameter_list|,
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializationList
parameter_list|)
block|{
name|this
operator|.
name|defaultSchema
operator|=
name|defaultSchema
expr_stmt|;
name|this
operator|.
name|queryRel
operator|=
name|queryRel
expr_stmt|;
name|this
operator|.
name|materializationList
operator|=
name|materializationList
expr_stmt|;
block|}
block|}
end_class

unit|}
end_unit
