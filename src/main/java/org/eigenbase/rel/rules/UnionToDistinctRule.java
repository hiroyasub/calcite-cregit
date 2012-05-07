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
name|rel
operator|.
name|*
import|;
end_import

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
comment|/**  *<code>UnionToDistinctRule</code> translates a distinct {@link UnionRel}  * (<code>all</code> =<code>false</code>) into an {@link AggregateRel} on top  * of a non-distinct {@link UnionRel} (<code>all</code> =<code>true</code>).  */
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
name|instance
init|=
operator|new
name|UnionToDistinctRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a UnionToDistinctRule.      */
specifier|private
name|UnionToDistinctRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|UnionRel
operator|.
name|class
argument_list|,
name|ANY
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
name|UnionRel
name|union
init|=
operator|(
name|UnionRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|union
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
return|return;
comment|// nothing to do
block|}
name|UnionRel
name|unionAll
init|=
operator|new
name|UnionRel
argument_list|(
name|union
operator|.
name|getCluster
argument_list|()
argument_list|,
name|union
operator|.
name|getInputs
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|RelOptUtil
operator|.
name|createDistinctRel
argument_list|(
name|unionAll
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End UnionToDistinctRule.java
end_comment

end_unit

