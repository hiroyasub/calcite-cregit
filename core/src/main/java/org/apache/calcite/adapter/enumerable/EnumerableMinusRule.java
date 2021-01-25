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
name|Minus
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
name|LogicalMinus
import|;
end_import

begin_comment
comment|/**  * Rule to convert an {@link LogicalMinus} to an {@link EnumerableMinus}.  * You may provide a custom config to convert other nodes that extend {@link Minus}.  *  * @see EnumerableRules#ENUMERABLE_MINUS_RULE  */
end_comment

begin_class
class|class
name|EnumerableMinusRule
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
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|LogicalMinus
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
literal|"EnumerableMinusRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|EnumerableMinusRule
operator|::
operator|new
argument_list|)
decl_stmt|;
comment|/** Called from the Config. */
specifier|protected
name|EnumerableMinusRule
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
name|Minus
name|minus
init|=
operator|(
name|Minus
operator|)
name|rel
decl_stmt|;
specifier|final
name|EnumerableConvention
name|out
init|=
name|EnumerableConvention
operator|.
name|INSTANCE
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|rel
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
decl_stmt|;
return|return
operator|new
name|EnumerableMinus
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convertList
argument_list|(
name|minus
operator|.
name|getInputs
argument_list|()
argument_list|,
name|out
argument_list|)
argument_list|,
name|minus
operator|.
name|all
argument_list|)
return|;
block|}
block|}
end_class

end_unit

