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
name|sql
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
name|prepare
operator|.
name|Prepare
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
name|DelegatingTypeSystem
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
name|advise
operator|.
name|SqlAdvisor
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
name|SqlValidatorCatalogReader
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
name|test
operator|.
name|CalciteAssert
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
name|test
operator|.
name|ConnectionFactories
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
name|test
operator|.
name|ConnectionFactory
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
name|test
operator|.
name|MockRelOptPlanner
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
name|test
operator|.
name|MockSqlOperatorTable
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
name|test
operator|.
name|catalog
operator|.
name|MockCatalogReaderSimple
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
name|SourceStringReader
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
name|Suppliers
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
name|Supplier
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
name|UnaryOperator
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
comment|/**  * As {@link SqlTestFactory} but has no state, and therefore  * configuration is passed to each method. */
end_comment

begin_class
specifier|public
class|class
name|SqlTestFactory
block|{
specifier|public
specifier|static
specifier|final
name|SqlTestFactory
name|INSTANCE
init|=
operator|new
name|SqlTestFactory
argument_list|(
name|MockCatalogReaderSimple
operator|::
name|create
argument_list|,
name|SqlTestFactory
operator|::
name|createTypeFactory
argument_list|,
name|MockRelOptPlanner
operator|::
operator|new
argument_list|,
name|Contexts
operator|.
name|of
argument_list|()
argument_list|,
name|UnaryOperator
operator|.
name|identity
argument_list|()
argument_list|,
name|SqlValidatorUtil
operator|::
name|newValidator
argument_list|,
name|ConnectionFactories
operator|.
name|empty
argument_list|()
operator|.
name|with
argument_list|(
name|ConnectionFactories
operator|.
name|add
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|HR
argument_list|)
argument_list|)
argument_list|,
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|,
name|SqlValidator
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|,
name|SqlToRelConverter
operator|.
name|CONFIG
argument_list|,
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
name|UnaryOperator
operator|.
name|identity
argument_list|()
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|o
lambda|->
name|MockSqlOperatorTable
operator|.
name|of
argument_list|(
name|o
argument_list|)
operator|.
name|extend
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|public
specifier|final
name|TypeFactoryFactory
name|typeFactoryFactory
decl_stmt|;
specifier|private
specifier|final
name|CatalogReaderFactory
name|catalogReaderFactory
decl_stmt|;
specifier|private
specifier|final
name|PlannerFactory
name|plannerFactory
decl_stmt|;
specifier|private
specifier|final
name|Context
name|plannerContext
decl_stmt|;
specifier|private
specifier|final
name|UnaryOperator
argument_list|<
name|RelOptCluster
argument_list|>
name|clusterTransform
decl_stmt|;
specifier|private
specifier|final
name|ValidatorFactory
name|validatorFactory
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|RelDataTypeFactory
argument_list|>
name|typeFactorySupplier
decl_stmt|;
specifier|private
specifier|final
name|SqlOperatorTable
name|operatorTable
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|SqlValidatorCatalogReader
argument_list|>
name|catalogReaderSupplier
decl_stmt|;
specifier|private
specifier|final
name|SqlParser
operator|.
name|Config
name|parserConfig
decl_stmt|;
specifier|public
specifier|final
name|SqlValidator
operator|.
name|Config
name|validatorConfig
decl_stmt|;
specifier|public
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConfig
decl_stmt|;
specifier|public
specifier|final
name|UnaryOperator
argument_list|<
name|RelDataTypeSystem
argument_list|>
name|typeSystemTransform
decl_stmt|;
specifier|protected
name|SqlTestFactory
parameter_list|(
name|CatalogReaderFactory
name|catalogReaderFactory
parameter_list|,
name|TypeFactoryFactory
name|typeFactoryFactory
parameter_list|,
name|PlannerFactory
name|plannerFactory
parameter_list|,
name|Context
name|plannerContext
parameter_list|,
name|UnaryOperator
argument_list|<
name|RelOptCluster
argument_list|>
name|clusterTransform
parameter_list|,
name|ValidatorFactory
name|validatorFactory
parameter_list|,
name|ConnectionFactory
name|connectionFactory
parameter_list|,
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|,
name|SqlValidator
operator|.
name|Config
name|validatorConfig
parameter_list|,
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConfig
parameter_list|,
name|SqlOperatorTable
name|operatorTable
parameter_list|,
name|UnaryOperator
argument_list|<
name|RelDataTypeSystem
argument_list|>
name|typeSystemTransform
parameter_list|)
block|{
name|this
operator|.
name|catalogReaderFactory
operator|=
name|requireNonNull
argument_list|(
name|catalogReaderFactory
argument_list|,
literal|"catalogReaderFactory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeFactoryFactory
operator|=
name|requireNonNull
argument_list|(
name|typeFactoryFactory
argument_list|,
literal|"typeFactoryFactory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|plannerFactory
operator|=
name|requireNonNull
argument_list|(
name|plannerFactory
argument_list|,
literal|"plannerFactory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|plannerContext
operator|=
name|requireNonNull
argument_list|(
name|plannerContext
argument_list|,
literal|"plannerContext"
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterTransform
operator|=
name|requireNonNull
argument_list|(
name|clusterTransform
argument_list|,
literal|"clusterTransform"
argument_list|)
expr_stmt|;
name|this
operator|.
name|validatorFactory
operator|=
name|requireNonNull
argument_list|(
name|validatorFactory
argument_list|,
literal|"validatorFactory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|connectionFactory
operator|=
name|requireNonNull
argument_list|(
name|connectionFactory
argument_list|,
literal|"connectionFactory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|sqlToRelConfig
operator|=
name|requireNonNull
argument_list|(
name|sqlToRelConfig
argument_list|,
literal|"sqlToRelConfig"
argument_list|)
expr_stmt|;
name|this
operator|.
name|operatorTable
operator|=
name|operatorTable
expr_stmt|;
name|this
operator|.
name|typeSystemTransform
operator|=
name|typeSystemTransform
expr_stmt|;
name|this
operator|.
name|typeFactorySupplier
operator|=
name|Suppliers
operator|.
name|memoize
argument_list|(
parameter_list|()
lambda|->
name|typeFactoryFactory
operator|.
name|create
argument_list|(
name|validatorConfig
operator|.
name|conformance
argument_list|()
argument_list|,
name|typeSystemTransform
operator|.
name|apply
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
argument_list|)
argument_list|)
operator|::
name|get
expr_stmt|;
name|this
operator|.
name|catalogReaderSupplier
operator|=
name|Suppliers
operator|.
name|memoize
argument_list|(
parameter_list|()
lambda|->
name|catalogReaderFactory
operator|.
name|create
argument_list|(
name|this
operator|.
name|typeFactorySupplier
operator|.
name|get
argument_list|()
argument_list|,
name|parserConfig
operator|.
name|caseSensitive
argument_list|()
argument_list|)
argument_list|)
operator|::
name|get
expr_stmt|;
name|this
operator|.
name|parserConfig
operator|=
name|parserConfig
expr_stmt|;
name|this
operator|.
name|validatorConfig
operator|=
name|validatorConfig
expr_stmt|;
block|}
comment|/** Creates a parser. */
specifier|public
name|SqlParser
name|createParser
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|SqlParser
operator|.
name|Config
name|parserConfig
init|=
name|parserConfig
argument_list|()
decl_stmt|;
return|return
name|SqlParser
operator|.
name|create
argument_list|(
operator|new
name|SourceStringReader
argument_list|(
name|sql
argument_list|)
argument_list|,
name|parserConfig
argument_list|)
return|;
block|}
comment|/** Creates a validator. */
specifier|public
name|SqlValidator
name|createValidator
parameter_list|()
block|{
return|return
name|validatorFactory
operator|.
name|create
argument_list|(
name|operatorTable
argument_list|,
name|catalogReaderSupplier
operator|.
name|get
argument_list|()
argument_list|,
name|typeFactorySupplier
operator|.
name|get
argument_list|()
argument_list|,
name|validatorConfig
argument_list|)
return|;
block|}
specifier|public
name|SqlAdvisor
name|createAdvisor
parameter_list|()
block|{
name|SqlValidator
name|validator
init|=
name|createValidator
argument_list|()
decl_stmt|;
if|if
condition|(
name|validator
operator|instanceof
name|SqlValidatorWithHints
condition|)
block|{
return|return
operator|new
name|SqlAdvisor
argument_list|(
operator|(
name|SqlValidatorWithHints
operator|)
name|validator
argument_list|,
name|parserConfig
argument_list|)
return|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Validator should implement SqlValidatorWithHints, actual validator is "
operator|+
name|validator
argument_list|)
throw|;
block|}
specifier|public
name|SqlTestFactory
name|withTypeFactoryFactory
parameter_list|(
name|TypeFactoryFactory
name|typeFactoryFactory
parameter_list|)
block|{
if|if
condition|(
name|typeFactoryFactory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|typeFactoryFactory
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withTypeSystem
parameter_list|(
name|UnaryOperator
argument_list|<
name|RelDataTypeSystem
argument_list|>
name|typeSystemTransform
parameter_list|)
block|{
if|if
condition|(
name|typeSystemTransform
operator|.
name|equals
argument_list|(
name|this
operator|.
name|typeSystemTransform
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withPlannerFactory
parameter_list|(
name|PlannerFactory
name|plannerFactory
parameter_list|)
block|{
if|if
condition|(
name|plannerFactory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|plannerFactory
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withPlannerContext
parameter_list|(
name|UnaryOperator
argument_list|<
name|Context
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|Context
name|plannerContext
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|plannerContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|plannerContext
operator|.
name|equals
argument_list|(
name|this
operator|.
name|plannerContext
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withCluster
parameter_list|(
name|UnaryOperator
argument_list|<
name|RelOptCluster
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|RelOptCluster
argument_list|>
name|clusterTransform
init|=
name|this
operator|.
name|clusterTransform
operator|.
name|andThen
argument_list|(
name|transform
argument_list|)
operator|::
name|apply
decl_stmt|;
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withCatalogReader
parameter_list|(
name|CatalogReaderFactory
name|catalogReaderFactory
parameter_list|)
block|{
if|if
condition|(
name|catalogReaderFactory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|catalogReaderFactory
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withValidator
parameter_list|(
name|ValidatorFactory
name|validatorFactory
parameter_list|)
block|{
if|if
condition|(
name|validatorFactory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|validatorFactory
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withValidatorConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlValidator
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlValidator
operator|.
name|Config
name|validatorConfig
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|validatorConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|validatorConfig
operator|.
name|equals
argument_list|(
name|this
operator|.
name|validatorConfig
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withSqlToRelConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConfig
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|sqlToRelConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|sqlToRelConfig
operator|.
name|equals
argument_list|(
name|this
operator|.
name|sqlToRelConfig
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RelDataTypeFactory
name|createTypeFactory
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|,
name|RelDataTypeSystem
name|typeSystem
parameter_list|)
block|{
if|if
condition|(
name|conformance
operator|.
name|shouldConvertRaggedUnionTypesToVarying
argument_list|()
condition|)
block|{
name|typeSystem
operator|=
operator|new
name|DelegatingTypeSystem
argument_list|(
name|typeSystem
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldConvertRaggedUnionTypesToVarying
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
expr_stmt|;
block|}
return|return
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|typeSystem
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withParserConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlParser
operator|.
name|Config
name|parserConfig
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|parserConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|parserConfig
operator|.
name|equals
argument_list|(
name|this
operator|.
name|parserConfig
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withConnectionFactory
parameter_list|(
name|UnaryOperator
argument_list|<
name|ConnectionFactory
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|ConnectionFactory
name|connectionFactory
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|connectionFactory
argument_list|)
decl_stmt|;
if|if
condition|(
name|connectionFactory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|connectionFactory
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlTestFactory
name|withOperatorTable
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlOperatorTable
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlOperatorTable
name|operatorTable
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|operatorTable
argument_list|)
decl_stmt|;
if|if
condition|(
name|operatorTable
operator|.
name|equals
argument_list|(
name|this
operator|.
name|operatorTable
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlTestFactory
argument_list|(
name|catalogReaderFactory
argument_list|,
name|typeFactoryFactory
argument_list|,
name|plannerFactory
argument_list|,
name|plannerContext
argument_list|,
name|clusterTransform
argument_list|,
name|validatorFactory
argument_list|,
name|connectionFactory
argument_list|,
name|parserConfig
argument_list|,
name|validatorConfig
argument_list|,
name|sqlToRelConfig
argument_list|,
name|operatorTable
argument_list|,
name|typeSystemTransform
argument_list|)
return|;
block|}
specifier|public
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|()
block|{
return|return
name|parserConfig
return|;
block|}
specifier|public
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactorySupplier
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|SqlToRelConverter
name|createSqlToRelConverter
parameter_list|()
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
init|=
operator|(
name|Prepare
operator|.
name|CatalogReader
operator|)
name|catalogReaderSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|createValidator
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|RelOptPlanner
name|planner
init|=
name|plannerFactory
operator|.
name|create
argument_list|(
name|plannerContext
argument_list|)
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|clusterTransform
operator|.
name|apply
argument_list|(
name|RelOptCluster
operator|.
name|create
argument_list|(
name|planner
argument_list|,
name|rexBuilder
argument_list|)
argument_list|)
decl_stmt|;
name|RelOptTable
operator|.
name|ViewExpander
name|viewExpander
init|=
operator|new
name|MockViewExpander
argument_list|(
name|validator
argument_list|,
name|catalogReader
argument_list|,
name|cluster
argument_list|,
name|sqlToRelConfig
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlToRelConverter
argument_list|(
name|viewExpander
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
name|sqlToRelConfig
argument_list|)
return|;
block|}
comment|/** Creates a {@link RelDataTypeFactory} for tests. */
specifier|public
interface|interface
name|TypeFactoryFactory
block|{
name|RelDataTypeFactory
name|create
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|,
name|RelDataTypeSystem
name|typeSystem
parameter_list|)
function_decl|;
block|}
comment|/** Creates a {@link RelOptPlanner} for tests. */
specifier|public
interface|interface
name|PlannerFactory
block|{
name|RelOptPlanner
name|create
parameter_list|(
name|Context
name|context
parameter_list|)
function_decl|;
block|}
comment|/** Creates {@link SqlValidator} for tests. */
specifier|public
interface|interface
name|ValidatorFactory
block|{
name|SqlValidator
name|create
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlValidator
operator|.
name|Config
name|config
parameter_list|)
function_decl|;
block|}
comment|/** Creates a {@link SqlValidatorCatalogReader} for tests. */
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|CatalogReaderFactory
block|{
name|SqlValidatorCatalogReader
name|create
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
function_decl|;
block|}
comment|/** Implementation for {@link RelOptTable.ViewExpander} for testing. */
specifier|private
specifier|static
class|class
name|MockViewExpander
implements|implements
name|RelOptTable
operator|.
name|ViewExpander
block|{
specifier|private
specifier|final
name|SqlValidator
name|validator
decl_stmt|;
specifier|private
specifier|final
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
decl_stmt|;
specifier|private
specifier|final
name|RelOptCluster
name|cluster
decl_stmt|;
specifier|private
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|config
decl_stmt|;
name|MockViewExpander
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
parameter_list|,
name|RelOptCluster
name|cluster
parameter_list|,
name|SqlToRelConverter
operator|.
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
name|this
operator|.
name|catalogReader
operator|=
name|catalogReader
expr_stmt|;
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
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
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|)
block|{
try|try
block|{
name|SqlNode
name|parsedNode
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|queryString
argument_list|)
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
name|SqlNode
name|validatedNode
init|=
name|validator
operator|.
name|validate
argument_list|(
name|parsedNode
argument_list|)
decl_stmt|;
name|SqlToRelConverter
name|converter
init|=
operator|new
name|SqlToRelConverter
argument_list|(
name|this
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
name|validatedNode
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
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
literal|"Error happened while expanding view."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

