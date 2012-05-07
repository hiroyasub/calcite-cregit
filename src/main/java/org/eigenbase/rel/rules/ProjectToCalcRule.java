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
operator|.
name|rules
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|reltype
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
comment|/**  * Rule to convert a {@link ProjectRel} to a {@link CalcRel}  *  *<p>The rule does not fire if the child is a {@link ProjectRel}, {@link  * FilterRel} or {@link CalcRel}. If it did, then the same {@link CalcRel} would  * be formed via several transformation paths, which is a waste of effort.</p>  *  * @author jhyde  * @version $Id$  * @see FilterToCalcRule  * @since Mar 7, 2004  */
end_comment

begin_class
specifier|public
class|class
name|ProjectToCalcRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|ProjectToCalcRule
name|instance
init|=
operator|new
name|ProjectToCalcRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|ProjectToCalcRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
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
specifier|final
name|ProjectRel
name|project
init|=
operator|(
name|ProjectRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|RelNode
name|child
init|=
name|project
operator|.
name|getChild
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|project
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
index|[]
name|projectExprs
init|=
name|RexUtil
operator|.
name|clone
argument_list|(
name|project
operator|.
name|getProjectExps
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|RexProgram
operator|.
name|create
argument_list|(
name|child
operator|.
name|getRowType
argument_list|()
argument_list|,
name|projectExprs
argument_list|,
literal|null
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|,
name|project
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|CalcRel
name|calc
init|=
operator|new
name|CalcRel
argument_list|(
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|project
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|child
argument_list|,
name|rowType
argument_list|,
name|program
argument_list|,
name|Collections
operator|.
expr|<
name|RelCollation
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|calc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ProjectToCalcRule.java
end_comment

end_unit

