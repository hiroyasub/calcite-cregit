begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * RemoveSemiJoinRule implements the rule that removes semijoins from a join  * tree if it turns out it's not possible to convert a SemiJoinRel to an indexed  * scan on a join factor. Namely, if the join factor does not reduce to a single  * table that can be scanned using an index. This rule should only be applied  * after attempts have been made to convert SemiJoinRels.  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RemoveSemiJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|RemoveSemiJoinRule
name|instance
init|=
operator|new
name|RemoveSemiJoinRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a RemoveSemiJoinRule.      */
specifier|private
name|RemoveSemiJoinRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|SemiJoinRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRule
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
name|rels
index|[
literal|0
index|]
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
comment|// End RemoveSemiJoinRule.java
end_comment

end_unit

