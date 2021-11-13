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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
import|;
end_import

begin_comment
comment|/**  * Embodiment of {@link SqlOperatorTest}  * that generates SQL statements and executes them using Calcite.  */
end_comment

begin_class
class|class
name|CalciteSqlOperatorTest
extends|extends
name|SqlOperatorTest
block|{
annotation|@
name|Override
specifier|protected
name|SqlOperatorFixture
name|fixture
parameter_list|()
block|{
return|return
name|super
operator|.
name|fixture
argument_list|()
operator|.
name|withTester
argument_list|(
name|t
lambda|->
name|TESTER
argument_list|)
return|;
block|}
block|}
end_class

end_unit

