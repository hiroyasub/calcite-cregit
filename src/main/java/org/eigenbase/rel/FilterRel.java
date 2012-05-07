begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

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
comment|/**  * A<code>FilterRel</code> is a relational expression which iterates over its  * input, and returns elements for which<code>condition</code> evaluates to  *<code>true</code>.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FilterRel
extends|extends
name|FilterRelBase
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a filter.      *      * @param cluster {@link RelOptCluster}  this relational expression belongs      * to      * @param child input relational expression      * @param condition boolean expression which determines whether a row is      * allowed to pass      */
specifier|public
name|FilterRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CallingConvention
operator|.
name|NONE
argument_list|)
argument_list|,
name|child
argument_list|,
name|condition
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|comprises
argument_list|(
name|CallingConvention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|FilterRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End FilterRel.java
end_comment

end_unit

