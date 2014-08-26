begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Rule which removes a trivial {@link CalcRel}.  *  *<p>A {@link CalcRel} is trivial if it projects its input fields in their  * original order, and it does not filter.  *  * @see org.eigenbase.rel.rules.RemoveTrivialProjectRule  */
end_comment

begin_class
specifier|public
class|class
name|RemoveTrivialCalcRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|RemoveTrivialCalcRule
name|INSTANCE
init|=
operator|new
name|RemoveTrivialCalcRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RemoveTrivialCalcRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|CalcRel
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
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|CalcRel
name|calc
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RexProgram
name|program
init|=
name|calc
operator|.
name|getProgram
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|program
operator|.
name|isTrivial
argument_list|()
condition|)
block|{
return|return;
block|}
name|RelNode
name|child
init|=
name|calc
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|child
operator|=
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|register
argument_list|(
name|child
argument_list|,
name|calc
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|convert
argument_list|(
name|child
argument_list|,
name|calc
operator|.
name|getTraitSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RemoveTrivialCalcRule.java
end_comment

end_unit

