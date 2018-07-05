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
name|util
operator|.
name|Casing
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
name|util
operator|.
name|Quoting
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
name|MockCatalogReader
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheLoader
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
name|LoadingCache
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link SqlTestFactory}.  *  *<p>Suitable for most tests. If you want different behavior, you can extend;  * if you want a factory with different properties (e.g. SQL conformance level  * or identifier quoting), wrap in a  * {@link DelegatingSqlTestFactory} and  * override {@link #get}.</p> */
end_comment

begin_class
specifier|public
class|class
name|DefaultSqlTestFactory
implements|implements
name|SqlTestFactory
block|{
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|DEFAULT_OPTIONS
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"quoting"
argument_list|,
name|Quoting
operator|.
name|DOUBLE_QUOTE
argument_list|)
decl|.
name|put
argument_list|(
literal|"quotedCasing"
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|)
decl|.
name|put
argument_list|(
literal|"unquotedCasing"
argument_list|,
name|Casing
operator|.
name|TO_UPPER
argument_list|)
decl|.
name|put
argument_list|(
literal|"caseSensitive"
argument_list|,
literal|true
argument_list|)
decl|.
name|put
argument_list|(
literal|"conformance"
argument_list|,
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
decl|.
name|put
argument_list|(
literal|"operatorTable"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"connectionFactory"
argument_list|,
name|CalciteAssert
operator|.
name|EMPTY_CONNECTION_FACTORY
operator|.
name|with
argument_list|(
operator|new
name|CalciteAssert
operator|.
name|AddSchemaSpecPostProcessor
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|HR
argument_list|)
argument_list|)
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|/** Caches the mock catalog.    * Due to view parsing, initializing a mock catalog is quite expensive.    * Validator is not re-entrant, so we create a new one for each test.    * Caching improves SqlValidatorTest from 23s to 8s,    * and CalciteSqlOperatorTest from 65s to 43s. */
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|SqlTestFactory
argument_list|,
name|Xyz
argument_list|>
name|cache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|CacheLoader
operator|.
name|from
argument_list|(
name|Xyz
operator|::
name|from
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DefaultSqlTestFactory
name|INSTANCE
init|=
operator|new
name|DefaultSqlTestFactory
argument_list|()
decl_stmt|;
specifier|private
name|DefaultSqlTestFactory
parameter_list|()
block|{
block|}
specifier|public
name|MockCatalogReader
name|createCatalogReader
parameter_list|(
name|SqlTestFactory
name|testFactory
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|boolean
name|caseSensitive
init|=
operator|(
name|Boolean
operator|)
name|testFactory
operator|.
name|get
argument_list|(
literal|"caseSensitive"
argument_list|)
decl_stmt|;
return|return
operator|new
name|MockCatalogReader
argument_list|(
name|typeFactory
argument_list|,
name|caseSensitive
argument_list|)
operator|.
name|init
argument_list|()
return|;
block|}
specifier|public
name|SqlOperatorTable
name|createOperatorTable
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
specifier|final
name|SqlOperatorTable
name|opTab0
init|=
operator|(
name|SqlOperatorTable
operator|)
name|factory
operator|.
name|get
argument_list|(
literal|"operatorTable"
argument_list|)
decl_stmt|;
name|MockSqlOperatorTable
name|opTab
init|=
operator|new
name|MockSqlOperatorTable
argument_list|(
name|opTab0
argument_list|)
decl_stmt|;
name|MockSqlOperatorTable
operator|.
name|addRamp
argument_list|(
name|opTab
argument_list|)
expr_stmt|;
return|return
name|opTab
return|;
block|}
specifier|public
name|SqlParser
name|createParser
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
return|return
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|,
name|SqlParser
operator|.
name|configBuilder
argument_list|()
operator|.
name|setQuoting
argument_list|(
operator|(
name|Quoting
operator|)
name|factory
operator|.
name|get
argument_list|(
literal|"quoting"
argument_list|)
argument_list|)
operator|.
name|setUnquotedCasing
argument_list|(
operator|(
name|Casing
operator|)
name|factory
operator|.
name|get
argument_list|(
literal|"unquotedCasing"
argument_list|)
argument_list|)
operator|.
name|setQuotedCasing
argument_list|(
operator|(
name|Casing
operator|)
name|factory
operator|.
name|get
argument_list|(
literal|"quotedCasing"
argument_list|)
argument_list|)
operator|.
name|setConformance
argument_list|(
operator|(
name|SqlConformance
operator|)
name|factory
operator|.
name|get
argument_list|(
literal|"conformance"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|SqlValidator
name|getValidator
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
specifier|final
name|Xyz
name|xyz
init|=
name|cache
operator|.
name|getUnchecked
argument_list|(
name|factory
argument_list|)
decl_stmt|;
specifier|final
name|SqlConformance
name|conformance
init|=
operator|(
name|SqlConformance
operator|)
name|factory
operator|.
name|get
argument_list|(
literal|"conformance"
argument_list|)
decl_stmt|;
return|return
name|SqlValidatorUtil
operator|.
name|newValidator
argument_list|(
name|xyz
operator|.
name|operatorTable
argument_list|,
name|xyz
operator|.
name|catalogReader
argument_list|,
name|xyz
operator|.
name|typeFactory
argument_list|,
name|conformance
argument_list|)
return|;
block|}
specifier|public
name|SqlAdvisor
name|createAdvisor
parameter_list|(
name|SqlValidatorWithHints
name|validator
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
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|DEFAULT_OPTIONS
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** State that can be cached and shared among tests. */
specifier|private
specifier|static
class|class
name|Xyz
block|{
specifier|private
specifier|final
name|SqlOperatorTable
name|operatorTable
decl_stmt|;
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|MockCatalogReader
name|catalogReader
decl_stmt|;
name|Xyz
parameter_list|(
name|SqlOperatorTable
name|operatorTable
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|MockCatalogReader
name|catalogReader
parameter_list|)
block|{
name|this
operator|.
name|operatorTable
operator|=
name|operatorTable
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|catalogReader
operator|=
name|catalogReader
expr_stmt|;
block|}
specifier|static
name|Xyz
name|from
parameter_list|(
annotation|@
name|Nonnull
name|SqlTestFactory
name|factory
parameter_list|)
block|{
specifier|final
name|SqlOperatorTable
name|operatorTable
init|=
name|factory
operator|.
name|createOperatorTable
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|RelDataTypeSystem
name|typeSystem
init|=
name|RelDataTypeSystem
operator|.
name|DEFAULT
decl_stmt|;
specifier|final
name|SqlConformance
name|conformance
init|=
operator|(
name|SqlConformance
operator|)
name|factory
operator|.
name|get
argument_list|(
literal|"conformance"
argument_list|)
decl_stmt|;
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
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|typeSystem
argument_list|)
decl_stmt|;
specifier|final
name|MockCatalogReader
name|catalogReader
init|=
name|factory
operator|.
name|createCatalogReader
argument_list|(
name|factory
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
return|return
operator|new
name|Xyz
argument_list|(
name|operatorTable
argument_list|,
name|typeFactory
argument_list|,
name|catalogReader
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End DefaultSqlTestFactory.java
end_comment

end_unit

