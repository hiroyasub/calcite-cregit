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
name|test
operator|.
name|SqlValidatorTestCase
import|;
end_import

begin_comment
comment|/**  * Concrete subclass of {@link SqlOperatorBaseTest} which checks against  * a {@link SqlValidator}. Tests that involve execution trivially succeed.  */
end_comment

begin_class
class|class
name|SqlOperatorTest
extends|extends
name|SqlOperatorBaseTest
block|{
specifier|private
specifier|static
specifier|final
name|SqlTester
name|DEFAULT_TESTER
init|=
operator|(
name|SqlTester
operator|)
operator|new
name|SqlValidatorTestCase
argument_list|()
operator|.
name|getTester
argument_list|()
decl_stmt|;
comment|/**    * Creates a SqlOperatorTest.    */
name|SqlOperatorTest
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|,
name|DEFAULT_TESTER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

