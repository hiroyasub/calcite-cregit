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
name|adapter
operator|.
name|enumerable
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
name|plan
operator|.
name|Convention
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
name|convert
operator|.
name|ConverterRule
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
name|Project
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
name|LogicalProject
import|;
end_import

begin_comment
comment|/**  * Rule to convert a {@link LogicalProject} to an {@link EnumerableProject}.  * You may provide a custom config to convert other nodes that extend {@link Project}.  *  * @see EnumerableRules#ENUMERABLE_PROJECT_RULE  */
end_comment

begin_class
class|class
name|EnumerableProjectRule
extends|extends
name|ConverterRule
block|{
comment|/** Default configuration. */
specifier|static
specifier|final
name|Config
name|DEFAULT_CONFIG
init|=
name|Config
operator|.
name|EMPTY
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withConversion
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|p
lambda|->
operator|!
name|p
operator|.
name|containsOver
argument_list|()
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"EnumerableProjectRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|EnumerableProjectRule
operator|::
operator|new
argument_list|)
decl_stmt|;
comment|/** Creates an EnumerableProjectRule. */
specifier|protected
name|EnumerableProjectRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|Project
name|project
init|=
operator|(
name|Project
operator|)
name|rel
decl_stmt|;
return|return
name|EnumerableProject
operator|.
name|create
argument_list|(
name|convert
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|project
operator|.
name|getInput
argument_list|()
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|)
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

