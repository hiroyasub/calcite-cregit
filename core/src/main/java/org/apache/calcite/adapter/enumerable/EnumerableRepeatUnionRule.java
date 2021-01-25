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
name|plan
operator|.
name|RelTraitSet
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
name|RepeatUnion
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
name|LogicalRepeatUnion
import|;
end_import

begin_comment
comment|/**  * Rule to convert a {@link LogicalRepeatUnion} into an {@link EnumerableRepeatUnion}.  * You may provide a custom config to convert other nodes that extend {@link RepeatUnion}.  *  * @see EnumerableRules#ENUMERABLE_REPEAT_UNION_RULE  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableRepeatUnionRule
extends|extends
name|ConverterRule
block|{
comment|/** Default configuration. */
specifier|public
specifier|static
specifier|final
name|Config
name|DEFAULT_CONFIG
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|LogicalRepeatUnion
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"EnumerableRepeatUnionRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|EnumerableRepeatUnionRule
operator|::
operator|new
argument_list|)
decl_stmt|;
comment|/** Called from the Config. */
specifier|protected
name|EnumerableRepeatUnionRule
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
name|RepeatUnion
name|union
init|=
operator|(
name|RepeatUnion
operator|)
name|rel
decl_stmt|;
name|EnumerableConvention
name|out
init|=
name|EnumerableConvention
operator|.
name|INSTANCE
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|union
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|RelNode
name|seedRel
init|=
name|union
operator|.
name|getSeedRel
argument_list|()
decl_stmt|;
name|RelNode
name|iterativeRel
init|=
name|union
operator|.
name|getIterativeRel
argument_list|()
decl_stmt|;
return|return
operator|new
name|EnumerableRepeatUnion
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|seedRel
argument_list|,
name|seedRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
argument_list|)
argument_list|,
name|convert
argument_list|(
name|iterativeRel
argument_list|,
name|iterativeRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
argument_list|)
argument_list|,
name|union
operator|.
name|all
argument_list|,
name|union
operator|.
name|iterationLimit
argument_list|)
return|;
block|}
block|}
end_class

end_unit

