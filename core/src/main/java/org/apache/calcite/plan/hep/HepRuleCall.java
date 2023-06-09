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
name|plan
operator|.
name|hep
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
name|RelHintsPropagator
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
name|RelOptPlanner
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
name|plan
operator|.
name|RelOptRuleOperand
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
name|RelOptUtil
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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * HepRuleCall implements {@link RelOptRuleCall} for a {@link HepPlanner}. It  * remembers transformation results so that the planner can choose which one (if  * any) should replace the original expression.  */
end_comment

begin_class
specifier|public
class|class
name|HepRuleCall
extends|extends
name|RelOptRuleCall
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|List
argument_list|<
name|RelNode
argument_list|>
name|results
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|HepRuleCall
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelNode
index|[]
name|rels
parameter_list|,
name|Map
argument_list|<
name|RelNode
argument_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
argument_list|>
name|nodeChildren
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|RelNode
argument_list|>
name|parents
parameter_list|)
block|{
name|super
argument_list|(
name|planner
argument_list|,
name|operand
argument_list|,
name|rels
argument_list|,
name|nodeChildren
argument_list|,
name|parents
argument_list|)
expr_stmt|;
name|results
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|transformTo
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Map
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|equiv
parameter_list|,
name|RelHintsPropagator
name|handler
parameter_list|)
block|{
specifier|final
name|RelNode
name|rel0
init|=
name|rels
index|[
literal|0
index|]
decl_stmt|;
name|RelOptUtil
operator|.
name|verifyTypeEquivalence
argument_list|(
name|rel0
argument_list|,
name|rel
argument_list|,
name|rel0
argument_list|)
expr_stmt|;
name|rel
operator|=
name|handler
operator|.
name|propagate
argument_list|(
name|rel0
argument_list|,
name|rel
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|rel0
operator|.
name|getCluster
argument_list|()
operator|.
name|invalidateMetadataQuery
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|RelNode
argument_list|>
name|getResults
parameter_list|()
block|{
return|return
name|results
return|;
block|}
block|}
end_class

end_unit

