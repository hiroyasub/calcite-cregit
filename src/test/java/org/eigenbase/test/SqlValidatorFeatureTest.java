begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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
name|resgen
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
name|resource
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
name|sql
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
name|sql
operator|.
name|fun
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
name|sql
operator|.
name|parser
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
name|sql
operator|.
name|type
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
name|sql
operator|.
name|validate
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * SqlValidatorFeatureTest verifies that features can be independently enabled  * or disabled.  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorFeatureTest
extends|extends
name|SqlValidatorTestCase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|String
name|FEATURE_DISABLED
init|=
literal|"feature_disabled"
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|ResourceDefinition
name|disabledFeature
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlValidatorFeatureTest
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Tester
name|getTester
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
operator|new
name|FeatureTesterImpl
argument_list|(
name|conformance
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinct
parameter_list|()
block|{
name|checkFeature
argument_list|(
literal|"select ^distinct^ name from dept"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLFeature_E051_01
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByDesc
parameter_list|()
block|{
name|checkFeature
argument_list|(
literal|"select name from dept order by ^name desc^"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLConformance_OrderByDesc
argument_list|)
expr_stmt|;
block|}
comment|// NOTE jvs 6-Mar-2006:  carets don't come out properly placed
comment|// for INTERSECT/EXCEPT, so don't bother
annotation|@
name|Test
specifier|public
name|void
name|testIntersect
parameter_list|()
block|{
name|checkFeature
argument_list|(
literal|"^select name from dept intersect select name from dept^"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLFeature_F302
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExcept
parameter_list|()
block|{
name|checkFeature
argument_list|(
literal|"^select name from dept except select name from dept^"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLFeature_E071_03
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiset
parameter_list|()
block|{
name|checkFeature
argument_list|(
literal|"values ^multiset[1]^"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLFeature_S271
argument_list|)
expr_stmt|;
name|checkFeature
argument_list|(
literal|"values ^multiset(select * from dept)^"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLFeature_S271
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTablesample
parameter_list|()
block|{
name|checkFeature
argument_list|(
literal|"select name from ^dept tablesample bernoulli(50)^"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLFeature_T613
argument_list|)
expr_stmt|;
name|checkFeature
argument_list|(
literal|"select name from ^dept tablesample substitute('sample_dept')^"
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|SQLFeatureExt_T613_Substitution
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkFeature
parameter_list|(
name|String
name|sql
parameter_list|,
name|ResourceDefinition
name|feature
parameter_list|)
block|{
comment|// Test once with feature enabled:  should pass
name|check
argument_list|(
name|sql
argument_list|)
expr_stmt|;
comment|// Test once with feature disabled:  should fail
try|try
block|{
name|disabledFeature
operator|=
name|feature
expr_stmt|;
name|checkFails
argument_list|(
name|sql
argument_list|,
name|FEATURE_DISABLED
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|disabledFeature
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
class|class
name|FeatureTesterImpl
extends|extends
name|TesterImpl
block|{
specifier|private
name|FeatureTesterImpl
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
name|super
argument_list|(
name|conformance
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|()
decl_stmt|;
return|return
operator|new
name|FeatureValidator
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
operator|new
name|MockCatalogReader
argument_list|(
name|typeFactory
argument_list|)
argument_list|,
name|typeFactory
argument_list|,
name|getConformance
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
class|class
name|FeatureValidator
extends|extends
name|SqlValidatorImpl
block|{
specifier|protected
name|FeatureValidator
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
name|SqlConformance
name|conformance
parameter_list|)
block|{
name|super
argument_list|(
name|opTab
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|conformance
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|validateFeature
parameter_list|(
name|ResourceDefinition
name|feature
parameter_list|,
name|SqlParserPos
name|context
parameter_list|)
block|{
if|if
condition|(
name|feature
operator|==
name|disabledFeature
condition|)
block|{
name|EigenbaseException
name|ex
init|=
operator|new
name|EigenbaseException
argument_list|(
name|FEATURE_DISABLED
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
throw|throw
operator|new
name|EigenbaseContextException
argument_list|(
literal|"location"
argument_list|,
name|ex
argument_list|,
name|context
operator|.
name|getLineNum
argument_list|()
argument_list|,
name|context
operator|.
name|getColumnNum
argument_list|()
argument_list|,
name|context
operator|.
name|getEndLineNum
argument_list|()
argument_list|,
name|context
operator|.
name|getEndColumnNum
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlValidatorFeatureTest.java
end_comment

end_unit

