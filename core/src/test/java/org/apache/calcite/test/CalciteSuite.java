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
name|TestKtTest
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
name|clone
operator|.
name|ArrayTableTest
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
name|CalciteRemoteDriverTest
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
name|LatticeSuggesterTest
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
name|RelOptPlanReaderTest
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
name|RelOptUtilTest
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
name|RelTraitTest
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
name|RelWriterTest
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
name|volcano
operator|.
name|CollationConversionTest
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
name|volcano
operator|.
name|ComboRuleTest
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
name|volcano
operator|.
name|TraitConversionTest
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
name|volcano
operator|.
name|TraitPropagationTest
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
name|volcano
operator|.
name|VolcanoPlannerTest
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
name|volcano
operator|.
name|VolcanoPlannerTraitTest
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
name|LookupOperatorOverloadsTest
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
name|profile
operator|.
name|ProfilerTest
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
name|RelCollationTest
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
name|RelDistributionTest
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
name|RelToSqlConverterStructsTest
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
name|RelToSqlConverterTest
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
name|rules
operator|.
name|DateRangeRulesTest
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
name|rules
operator|.
name|SortRemoveRuleTest
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
name|RexBuilderTest
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
name|RexExecutorTest
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
name|RexSqlStandardConvertletTableTest
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
name|BinarySearchTest
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
name|EnumerablesTest
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
name|SqlSetOptionOperatorTest
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
name|SqlParserTest
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
name|SqlUnParserTest
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
name|parserextensiontesting
operator|.
name|ExtensionSqlParserTest
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
name|test
operator|.
name|SqlAdvisorTest
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
name|test
operator|.
name|SqlOperatorTest
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
name|test
operator|.
name|SqlPrettyWriterTest
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
name|test
operator|.
name|SqlTypeNameTest
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
name|type
operator|.
name|SqlTypeFactoryTest
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
name|type
operator|.
name|SqlTypeUtilTest
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
name|LexCaseSensitiveTest
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
name|LexEscapeTest
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
name|SqlValidatorUtilTest
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
name|enumerable
operator|.
name|EnumerableCorrelateTest
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
name|fuzzer
operator|.
name|RexProgramFuzzyTest
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
name|FrameworksTest
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
name|PlannerTest
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
name|BitSetsTest
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
name|ChunkListTest
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
name|ImmutableBitSetTest
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
name|PartiallyOrderedSetTest
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
name|PermutationTestCase
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
name|PrecedenceClimbingParserTest
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
name|ReflectVisitorTest
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
name|SourceTest
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
name|TestUtilTest
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
name|UtilTest
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
name|graph
operator|.
name|DirectedGraphTest
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
name|mapping
operator|.
name|MappingTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Suite
import|;
end_import

begin_comment
comment|/**  * Calcite test suite.  *  *<p>Tests are sorted by approximate running time. The suite runs the fastest  * tests first, so that regressions can be discovered as fast as possible.  * Most unit tests run very quickly, and are scheduled before system tests  * (which are slower but more likely to break because they have more  * dependencies). Slow unit tests that don't break often are scheduled last.</p>  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Suite
operator|.
name|class
argument_list|)
annotation|@
name|Suite
operator|.
name|SuiteClasses
argument_list|(
block|{
comment|// very fast tests (under 0.1s)
name|TestKtTest
operator|.
name|class
block|,
name|ArrayTableTest
operator|.
name|class
block|,
name|BitSetsTest
operator|.
name|class
block|,
name|ImmutableBitSetTest
operator|.
name|class
block|,
name|DirectedGraphTest
operator|.
name|class
block|,
name|ReflectVisitorTest
operator|.
name|class
block|,
name|RelOptUtilTest
operator|.
name|class
block|,
name|RelCollationTest
operator|.
name|class
block|,
name|UtilTest
operator|.
name|class
block|,
name|PrecedenceClimbingParserTest
operator|.
name|class
block|,
name|SourceTest
operator|.
name|class
block|,
name|MappingTest
operator|.
name|class
block|,
name|CalciteResourceTest
operator|.
name|class
block|,
name|FilteratorTest
operator|.
name|class
block|,
name|PermutationTestCase
operator|.
name|class
block|,
name|SqlFunctionsTest
operator|.
name|class
block|,
name|SqlJsonFunctionsTest
operator|.
name|class
block|,
name|SqlTypeNameTest
operator|.
name|class
block|,
name|ModelTest
operator|.
name|class
block|,
name|SqlValidatorFeatureTest
operator|.
name|class
block|,
name|VolcanoPlannerTraitTest
operator|.
name|class
block|,
name|InterpreterTest
operator|.
name|class
block|,
name|TestUtilTest
operator|.
name|class
block|,
name|VolcanoPlannerTest
operator|.
name|class
block|,
name|RelTraitTest
operator|.
name|class
block|,
name|HepPlannerTest
operator|.
name|class
block|,
name|TraitPropagationTest
operator|.
name|class
block|,
name|RelDistributionTest
operator|.
name|class
block|,
name|RelWriterTest
operator|.
name|class
block|,
name|RexProgramTest
operator|.
name|class
block|,
name|SqlOperatorBindingTest
operator|.
name|class
block|,
name|RexTransformerTest
operator|.
name|class
block|,
name|BinarySearchTest
operator|.
name|class
block|,
name|EnumerablesTest
operator|.
name|class
block|,
name|ExceptionMessageTest
operator|.
name|class
block|,
name|InduceGroupingTypeTest
operator|.
name|class
block|,
name|RelOptPlanReaderTest
operator|.
name|class
block|,
name|RexBuilderTest
operator|.
name|class
block|,
name|RexSqlStandardConvertletTableTest
operator|.
name|class
block|,
name|SqlTypeFactoryTest
operator|.
name|class
block|,
name|SqlTypeUtilTest
operator|.
name|class
block|,
name|SqlValidatorUtilTest
operator|.
name|class
block|,
comment|// medium tests (above 0.1s)
name|SqlParserTest
operator|.
name|class
block|,
name|SqlUnParserTest
operator|.
name|class
block|,
name|ExtensionSqlParserTest
operator|.
name|class
block|,
name|SqlSetOptionOperatorTest
operator|.
name|class
block|,
name|SqlPrettyWriterTest
operator|.
name|class
block|,
name|SqlValidatorTest
operator|.
name|class
block|,
name|SqlValidatorDynamicTest
operator|.
name|class
block|,
name|SqlValidatorMatchTest
operator|.
name|class
block|,
name|SqlAdvisorTest
operator|.
name|class
block|,
name|RelMetadataTest
operator|.
name|class
block|,
name|DateRangeRulesTest
operator|.
name|class
block|,
name|ScannableTableTest
operator|.
name|class
block|,
name|RexExecutorTest
operator|.
name|class
block|,
name|SqlLimitsTest
operator|.
name|class
block|,
name|JdbcFrontLinqBackTest
operator|.
name|class
block|,
name|RelToSqlConverterTest
operator|.
name|class
block|,
name|RelToSqlConverterStructsTest
operator|.
name|class
block|,
name|SqlOperatorTest
operator|.
name|class
block|,
name|ChunkListTest
operator|.
name|class
block|,
name|FrameworksTest
operator|.
name|class
block|,
name|EnumerableCorrelateTest
operator|.
name|class
block|,
name|LookupOperatorOverloadsTest
operator|.
name|class
block|,
name|LexCaseSensitiveTest
operator|.
name|class
block|,
name|LexEscapeTest
operator|.
name|class
block|,
name|CollationConversionTest
operator|.
name|class
block|,
name|TraitConversionTest
operator|.
name|class
block|,
name|ComboRuleTest
operator|.
name|class
block|,
name|MutableRelTest
operator|.
name|class
block|,
comment|// slow tests (above 1s)
name|UdfTest
operator|.
name|class
block|,
name|UdtTest
operator|.
name|class
block|,
name|TableFunctionTest
operator|.
name|class
block|,
name|PlannerTest
operator|.
name|class
block|,
name|RelBuilderTest
operator|.
name|class
block|,
name|PigRelBuilderTest
operator|.
name|class
block|,
name|RexImplicationCheckerTest
operator|.
name|class
block|,
name|JdbcAdapterTest
operator|.
name|class
block|,
name|LinqFrontJdbcBackTest
operator|.
name|class
block|,
name|JdbcFrontJdbcBackLinqMiddleTest
operator|.
name|class
block|,
name|RexProgramFuzzyTest
operator|.
name|class
block|,
name|SqlToRelConverterTest
operator|.
name|class
block|,
name|ProfilerTest
operator|.
name|class
block|,
name|SqlStatisticProviderTest
operator|.
name|class
block|,
name|SqlAdvisorJdbcTest
operator|.
name|class
block|,
name|CoreQuidemTest
operator|.
name|class
block|,
name|CalciteRemoteDriverTest
operator|.
name|class
block|,
name|StreamTest
operator|.
name|class
block|,
name|SortRemoveRuleTest
operator|.
name|class
block|,
comment|// above 10sec
name|JdbcFrontJdbcBackTest
operator|.
name|class
block|,
comment|// above 20sec
name|JdbcTest
operator|.
name|class
block|,
name|CalciteSqlOperatorTest
operator|.
name|class
block|,
name|ReflectiveSchemaTest
operator|.
name|class
block|,
name|RelOptRulesTest
operator|.
name|class
block|,
comment|// test cases
name|TableInRootSchemaTest
operator|.
name|class
block|,
name|RelMdColumnOriginsTest
operator|.
name|class
block|,
name|MultiJdbcSchemaJoinTest
operator|.
name|class
block|,
name|SqlLineTest
operator|.
name|class
block|,
name|CollectionTypeTest
operator|.
name|class
block|,
comment|// slow tests that don't break often
name|SqlToRelConverterExtendedTest
operator|.
name|class
block|,
name|PartiallyOrderedSetTest
operator|.
name|class
block|,
comment|// above 30sec
name|LatticeSuggesterTest
operator|.
name|class
block|,
name|MaterializationTest
operator|.
name|class
block|,
comment|// above 120sec
name|LatticeTest
operator|.
name|class
block|,
comment|// system tests and benchmarks (very slow, but usually only run if
comment|// '-Dcalcite.test.slow' is specified)
name|FoodmartTest
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|CalciteSuite
block|{ }
end_class

begin_comment
comment|// End CalciteSuite.java
end_comment

end_unit

