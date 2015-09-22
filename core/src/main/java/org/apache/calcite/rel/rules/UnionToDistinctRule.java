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
name|rel
operator|.
name|rules
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
name|RelOptRule
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
name|RelOptRuleCall
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
name|RelFactories
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
name|Union
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
name|LogicalUnion
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/**  * Planner rule that translates a distinct  * {@link org.apache.calcite.rel.core.Union}  * (<code>all</code> =<code>false</code>)  * into an {@link org.apache.calcite.rel.core.Aggregate}  * on top of a non-distinct {@link org.apache.calcite.rel.core.Union}  * (<code>all</code> =<code>true</code>).  */
end_comment

begin_class
specifier|public
class|class
name|UnionToDistinctRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|UnionToDistinctRule
name|INSTANCE
init|=
operator|new
name|UnionToDistinctRule
argument_list|(
name|LogicalUnion
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a UnionToDistinctRule.    */
specifier|public
name|UnionToDistinctRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Union
argument_list|>
name|unionClazz
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|unionClazz
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|UnionToDistinctRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Union
argument_list|>
name|unionClazz
parameter_list|,
name|RelFactories
operator|.
name|SetOpFactory
name|setOpFactory
parameter_list|)
block|{
name|this
argument_list|(
name|unionClazz
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|setOpFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Union
name|union
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|union
operator|.
name|all
condition|)
block|{
return|return;
comment|// nothing to do
block|}
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|relBuilder
operator|.
name|pushAll
argument_list|(
name|union
operator|.
name|getInputs
argument_list|()
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|union
argument_list|(
literal|true
argument_list|,
name|union
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|distinct
argument_list|()
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End UnionToDistinctRule.java
end_comment

end_unit

