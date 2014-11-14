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
name|SemiJoin
import|;
end_import

begin_comment
comment|/**  * Planner rule that removes a {@link org.apache.calcite.rel.core.SemiJoin}s  * from a join tree.  *  *<p>It is invoked after attempts have been made to convert a SemiJoin to an  * indexed scan on a join factor have failed. Namely, if the join factor does  * not reduce to a single table that can be scanned using an index.  *  *<p>It should only be enabled if all SemiJoins in the plan are advisory; that  * is, they can be safely dropped without affecting the semantics of the query.  */
end_comment

begin_class
specifier|public
class|class
name|SemiJoinRemoveRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|SemiJoinRemoveRule
name|INSTANCE
init|=
operator|new
name|SemiJoinRemoveRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates a SemiJoinRemoveRule. */
specifier|private
name|SemiJoinRemoveRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|SemiJoin
operator|.
name|class
argument_list|,
name|any
argument_list|()
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
name|call
operator|.
name|transformTo
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SemiJoinRemoveRule.java
end_comment

end_unit

