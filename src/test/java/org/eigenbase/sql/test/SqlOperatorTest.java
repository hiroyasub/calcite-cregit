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
name|sql
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
name|test
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Concrete subclass of {@link SqlOperatorBaseTest} which checks against  * a {@link SqlValidator}. Tests that involve execution trivially succeed.  */
end_comment

begin_class
specifier|public
class|class
name|SqlOperatorTest
extends|extends
name|SqlOperatorBaseTest
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|SqlTester
name|tester
init|=
operator|(
name|SqlTester
operator|)
operator|new
name|SqlValidatorTestCase
argument_list|(
literal|null
argument_list|)
operator|.
name|getTester
argument_list|(
name|SqlConformance
operator|.
name|Default
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlOperatorTest
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|SqlTester
name|getTester
parameter_list|()
block|{
return|return
name|tester
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlOperatorTest.java
end_comment

end_unit

