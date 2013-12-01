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
name|test
package|;
end_package

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
name|clone
operator|.
name|ArrayTableTest
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
name|util
operator|.
name|PartiallyOrderedSetTest
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
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptUtilTest
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
name|VolcanoPlannerTest
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
name|VolcanoPlannerTraitTest
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
name|parser
operator|.
name|SqlParserTest
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
name|test
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
name|test
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
name|util
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
comment|/**  * Optiq test suite.  *  *<p>Tests are sorted by approximate running time. The suite runs the fastest  * tests first, so that regressions can be discovered as fast as possible.  * Most unit tests run very quickly, and are scheduled before system tests  * (which are slower but more likely to break because they have more  * dependencies). Slow unit tests that don't break often are scheduled last.</p>  */
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
name|ArrayTableTest
operator|.
name|class
block|,
name|ArrayQueueTest
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
name|UtilTest
operator|.
name|class
block|,
name|MappingTest
operator|.
name|class
block|,
name|EigenbaseResourceTest
operator|.
name|class
block|,
name|FilteratorTest
operator|.
name|class
block|,
name|OptionsListTest
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
name|VolcanoPlannerTest
operator|.
name|class
block|,
name|SargTest
operator|.
name|class
block|,
name|SqlPrettyWriterTest
operator|.
name|class
block|,
name|RexProgramTest
operator|.
name|class
block|,
comment|// medium tests (above 0.1s)
name|SqlParserTest
operator|.
name|class
block|,
name|SqlValidatorTest
operator|.
name|class
block|,
name|SqlAdvisorTest
operator|.
name|class
block|,
name|RexTransformerTest
operator|.
name|class
block|,
name|RelMetadataTest
operator|.
name|class
block|,
name|HepPlannerTest
operator|.
name|class
block|,
name|RelOptRulesTest
operator|.
name|class
block|,
name|MaterializationTest
operator|.
name|class
block|,
name|SqlLimitsTest
operator|.
name|class
block|,
name|LinqFrontJdbcBackTest
operator|.
name|class
block|,
name|JdbcFrontLinqBackTest
operator|.
name|class
block|,
name|JdbcFrontJdbcBackTest
operator|.
name|class
block|,
name|SqlToRelConverterTest
operator|.
name|class
block|,
name|SqlOperatorTest
operator|.
name|class
block|,
name|RexTransformerTest
operator|.
name|class
block|,
name|ChunkListTest
operator|.
name|class
block|,
comment|// slow tests (above 1s)
name|JdbcAdapterTest
operator|.
name|class
block|,
name|JdbcFrontJdbcBackLinqMiddleTest
operator|.
name|class
block|,
name|OptiqSqlOperatorTest
operator|.
name|class
block|,
name|ReflectiveSchemaTest
operator|.
name|class
block|,
name|JdbcTest
operator|.
name|class
block|,
comment|// test cases
name|TableInRootSchemaTest
operator|.
name|class
block|,
comment|// slow tests that don't break often
name|PartiallyOrderedSetTest
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|OptiqSuite
block|{ }
end_class

begin_comment
comment|// End OptiqSuite.java
end_comment

end_unit

