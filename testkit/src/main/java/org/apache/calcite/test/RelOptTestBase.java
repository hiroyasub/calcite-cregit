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
name|tools
operator|.
name|RelBuilder
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
name|Function
import|;
end_import

begin_comment
comment|/**  * RelOptTestBase is an abstract base for tests which exercise a planner and/or  * rules via {@link DiffRepository}.  */
end_comment

begin_class
specifier|abstract
class|class
name|RelOptTestBase
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/** Creates a fixture for a test. Derived class must override and set    * {@link RelOptFixture#diffRepos}. */
name|RelOptFixture
name|fixture
parameter_list|()
block|{
return|return
name|RelOptFixture
operator|.
name|DEFAULT
return|;
block|}
comment|/** Creates a fixture and sets its SQL statement. */
specifier|protected
specifier|final
name|RelOptFixture
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|fixture
argument_list|()
operator|.
name|sql
argument_list|(
name|sql
argument_list|)
return|;
block|}
comment|/** Initiates a test case with a given {@link RelNode} supplier. */
specifier|protected
specifier|final
name|RelOptFixture
name|relFn
parameter_list|(
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
parameter_list|)
block|{
return|return
name|fixture
argument_list|()
operator|.
name|relFn
argument_list|(
name|relFn
argument_list|)
return|;
block|}
block|}
end_class

end_unit

