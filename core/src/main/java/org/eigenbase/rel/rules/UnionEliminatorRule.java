begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
comment|/**  *<code>UnionEliminatorRule</code> checks to see if its possible to optimize a  * Union call by eliminating the Union operator altogether in the case the call  * consists of only one input.  */
end_comment

begin_class
specifier|public
class|class
name|UnionEliminatorRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|UnionEliminatorRule
name|INSTANCE
init|=
operator|new
name|UnionEliminatorRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a UnionEliminatorRule.    */
specifier|private
name|UnionEliminatorRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|UnionRel
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
name|UnionRel
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
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|union
operator|.
name|all
condition|)
block|{
return|return;
block|}
comment|// REVIEW jvs 14-Mar-2006:  why don't we need to register
comment|// the equivalence here like we do in RemoveDistinctRule?
name|call
operator|.
name|transformTo
argument_list|(
name|union
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End UnionEliminatorRule.java
end_comment

end_unit

