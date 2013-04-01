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
name|oj
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
name|rel
operator|.
name|convert
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
comment|/**  * A collection of optimizer rules related to the {@link  * CallingConvention#ITERATOR iterator calling convention}.  *  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|IterRules
block|{
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Rule to convert a {@link UnionRel} to {@link CallingConvention#ITERATOR      * iterator calling convention}.      */
specifier|public
specifier|static
class|class
name|UnionToIteratorRule
extends|extends
name|ConverterRule
block|{
specifier|public
name|UnionToIteratorRule
parameter_list|()
block|{
name|this
argument_list|(
literal|"UnionToIteratorRule"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|UnionToIteratorRule
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|UnionRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|// factory method
specifier|protected
name|RelNode
name|newIterConcatenateRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
operator|new
name|IterConcatenateRel
argument_list|(
name|cluster
argument_list|,
name|inputs
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|UnionRel
name|union
init|=
operator|(
name|UnionRel
operator|)
name|rel
decl_stmt|;
if|if
condition|(
operator|!
name|union
operator|.
name|all
condition|)
block|{
return|return
literal|null
return|;
comment|// can only convert non-distinct Union
block|}
specifier|final
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
name|CallingConvention
operator|.
name|ITERATOR
argument_list|)
decl_stmt|;
return|return
name|newIterConcatenateRel
argument_list|(
name|union
operator|.
name|getCluster
argument_list|()
argument_list|,
name|convertList
argument_list|(
name|union
operator|.
name|getInputs
argument_list|()
argument_list|,
name|traitSet
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Refinement of {@link UnionToIteratorRule} which only applies to a {@link      * UnionRel} all of whose input rows are the same type as its output row.      * Luckily, a {@link org.eigenbase.rel.rules.CoerceInputsRule} will have      * made that happen.      */
specifier|public
specifier|static
class|class
name|HomogeneousUnionToIteratorRule
extends|extends
name|UnionToIteratorRule
block|{
specifier|public
specifier|static
specifier|final
name|HomogeneousUnionToIteratorRule
name|instance
init|=
operator|new
name|HomogeneousUnionToIteratorRule
argument_list|()
decl_stmt|;
comment|/**          * Creates a HomogeneousUnionToIteratorRule.          */
specifier|private
name|HomogeneousUnionToIteratorRule
parameter_list|()
block|{
name|this
argument_list|(
literal|"HomogeneousUnionToIteratorRule"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|HomogeneousUnionToIteratorRule
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|UnionRel
name|unionRel
init|=
operator|(
name|UnionRel
operator|)
name|rel
decl_stmt|;
if|if
condition|(
operator|!
name|unionRel
operator|.
name|isHomogeneous
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|super
operator|.
name|convert
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|OneRowToIteratorRule
extends|extends
name|ConverterRule
block|{
specifier|public
specifier|static
specifier|final
name|OneRowToIteratorRule
name|instance
init|=
operator|new
name|OneRowToIteratorRule
argument_list|()
decl_stmt|;
comment|/**          * Creates a OneRowToIteratorRule.          */
specifier|private
name|OneRowToIteratorRule
parameter_list|()
block|{
name|super
argument_list|(
name|OneRowRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
literal|"OneRowToIteratorRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|OneRowRel
name|oneRow
init|=
operator|(
name|OneRowRel
operator|)
name|rel
decl_stmt|;
return|return
operator|new
name|IterOneRowRel
argument_list|(
name|oneRow
operator|.
name|getCluster
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Rule to convert a {@link CalcRel} to an {@link IterCalcRel}.      */
specifier|public
specifier|static
class|class
name|IterCalcRule
extends|extends
name|ConverterRule
block|{
specifier|public
specifier|static
specifier|final
name|IterCalcRule
name|instance
init|=
operator|new
name|IterCalcRule
argument_list|()
decl_stmt|;
specifier|private
name|IterCalcRule
parameter_list|()
block|{
name|super
argument_list|(
name|CalcRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
literal|"IterCalcRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|CalcRel
name|calc
init|=
operator|(
name|CalcRel
operator|)
name|rel
decl_stmt|;
comment|// If there's a multiset, let FarragoMultisetSplitter work on it
comment|// first.
if|if
condition|(
name|RexMultisetUtil
operator|.
name|containsMultiset
argument_list|(
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// REVIEW: want to move canTranslate into RelImplementor
comment|// and implement it for Java& C++ calcs.
specifier|final
name|JavaRelImplementor
name|relImplementor
init|=
literal|null
decl_stmt|;
comment|/*                 rel.getCluster().getPlanner().getJavaRelImplementor(rel);                 */
specifier|final
name|RelNode
name|convertedChild
init|=
name|convert
argument_list|(
name|calc
operator|.
name|getChild
argument_list|()
argument_list|,
name|calc
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|CallingConvention
operator|.
name|ITERATOR
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|relImplementor
operator|.
name|canTranslate
argument_list|(
name|convertedChild
argument_list|,
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|)
condition|)
block|{
comment|// Some of the expressions cannot be translated into Java
return|return
literal|null
return|;
block|}
return|return
operator|new
name|IterCalcRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|convertedChild
argument_list|,
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|,
name|ProjectRelBase
operator|.
name|Flags
operator|.
name|Boxed
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End IterRules.java
end_comment

end_unit

