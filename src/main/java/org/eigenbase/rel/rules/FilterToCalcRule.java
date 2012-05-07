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
comment|/**  * Planner rule which converts a {@link FilterRel} to a {@link CalcRel}.  *  *<p>The rule does<em>NOT</em> fire if the child is a {@link FilterRel} or a  * {@link ProjectRel} (we assume they they will be converted using {@link  * FilterToCalcRule} or {@link ProjectToCalcRule}) or a {@link CalcRel}. This  * {@link FilterRel} will eventually be converted by {@link  * MergeFilterOntoCalcRule}.  *  * @author jhyde  * @version $Id$  * @since Mar 7, 2004  */
end_comment

begin_class
specifier|public
class|class
name|FilterToCalcRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|FilterToCalcRule
name|instance
init|=
operator|new
name|FilterToCalcRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|FilterToCalcRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|FilterRel
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
name|FilterRel
name|filter
init|=
operator|(
name|FilterRel
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
name|rel
init|=
name|filter
operator|.
name|getChild
argument_list|()
decl_stmt|;
comment|// Create a program containing a filter.
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|filter
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|inputRowType
init|=
name|rel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|RexProgramBuilder
name|programBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|inputRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
name|programBuilder
operator|.
name|addIdentity
argument_list|()
expr_stmt|;
if|if
condition|(
name|filter
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|programBuilder
operator|.
name|addCondition
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RexProgram
name|program
init|=
name|programBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
specifier|final
name|CalcRel
name|calc
init|=
operator|new
name|CalcRel
argument_list|(
name|filter
operator|.
name|getCluster
argument_list|()
argument_list|,
name|filter
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|rel
argument_list|,
name|inputRowType
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
comment|// End FilterToCalcRule.java
end_comment

end_unit

