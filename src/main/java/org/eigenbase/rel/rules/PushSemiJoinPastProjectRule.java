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
comment|/**  * PushSemiJoinPastProjectRule implements the rule for pushing semijoins down in  * a tree past a project in order to trigger other rules that will convert  * semijoins.  *  *<p>SemiJoinRel(ProjectRel(X), Y) --> ProjectRel(SemiJoinRel(X, Y))  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|PushSemiJoinPastProjectRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushSemiJoinPastProjectRule
name|instance
init|=
operator|new
name|PushSemiJoinPastProjectRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a PushSemiJoinPastProjectRule.      */
specifier|private
name|PushSemiJoinPastProjectRule
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
name|SemiJoinRel
name|semiJoin
init|=
operator|(
name|SemiJoinRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
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
literal|1
index|]
decl_stmt|;
comment|// convert the LHS semijoin keys to reference the child projection
comment|// expression; all projection expressions must be RexInputRefs,
comment|// otherwise, we wouldn't have created this semijoin
name|List
argument_list|<
name|Integer
argument_list|>
name|newLeftKeys
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
name|semiJoin
operator|.
name|getLeftKeys
argument_list|()
decl_stmt|;
name|RexNode
index|[]
name|projExprs
init|=
name|project
operator|.
name|getProjectExps
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|leftKeys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexInputRef
name|inputRef
init|=
operator|(
name|RexInputRef
operator|)
name|projExprs
index|[
name|leftKeys
operator|.
name|get
argument_list|(
name|i
argument_list|)
index|]
decl_stmt|;
name|newLeftKeys
operator|.
name|add
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// convert the semijoin condition to reflect the LHS with the project
comment|// pulled up
name|RexNode
name|newCondition
init|=
name|adjustCondition
argument_list|(
name|project
argument_list|,
name|semiJoin
argument_list|)
decl_stmt|;
name|SemiJoinRel
name|newSemiJoin
init|=
operator|new
name|SemiJoinRel
argument_list|(
name|semiJoin
operator|.
name|getCluster
argument_list|()
argument_list|,
name|project
operator|.
name|getChild
argument_list|()
argument_list|,
name|semiJoin
operator|.
name|getRight
argument_list|()
argument_list|,
name|newCondition
argument_list|,
name|newLeftKeys
argument_list|,
name|semiJoin
operator|.
name|getRightKeys
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create the new projection.  Note that the projection expressions
comment|// are the same as the original because they only reference the LHS
comment|// of the semijoin and the semijoin only projects out the LHS
name|RelNode
name|newProject
init|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|newSemiJoin
argument_list|,
name|projExprs
argument_list|,
name|RelOptUtil
operator|.
name|getFieldNames
argument_list|(
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newProject
argument_list|)
expr_stmt|;
block|}
comment|/**      * Pulls the project above the semijoin and returns the resulting semijoin      * condition. As a result, the semijoin condition should be modified such      * that references to the LHS of a semijoin should now reference the      * children of the project that's on the LHS.      *      * @param project ProjectRel on the LHS of the semijoin      * @param semiJoin the semijoin      *      * @return the modified semijoin condition      */
specifier|private
name|RexNode
name|adjustCondition
parameter_list|(
name|ProjectRel
name|project
parameter_list|,
name|SemiJoinRel
name|semiJoin
parameter_list|)
block|{
comment|// create two RexPrograms -- the bottom one representing a
comment|// concatenation of the project and the RHS of the semijoin and the
comment|// top one representing the semijoin condition
name|RexBuilder
name|rexBuilder
init|=
name|project
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|RelNode
name|rightChild
init|=
name|semiJoin
operator|.
name|getRight
argument_list|()
decl_stmt|;
comment|// for the bottom RexProgram, the input is a concatenation of the
comment|// child of the project and the RHS of the semijoin
name|RelDataType
name|bottomInputRowType
init|=
name|typeFactory
operator|.
name|createJoinType
argument_list|(
operator|new
name|RelDataType
index|[]
block|{
name|project
operator|.
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
block|,
name|rightChild
operator|.
name|getRowType
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|RexProgramBuilder
name|bottomProgramBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|bottomInputRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
comment|// add the project expressions, then add input references for the RHS
comment|// of the semijoin
name|RexNode
index|[]
name|projExprs
init|=
name|project
operator|.
name|getProjectExps
argument_list|()
decl_stmt|;
name|RelDataTypeField
index|[]
name|projFields
init|=
name|project
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|projExprs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bottomProgramBuilder
operator|.
name|addProject
argument_list|(
name|projExprs
index|[
name|i
index|]
argument_list|,
name|projFields
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|nLeftFields
init|=
name|project
operator|.
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|RelDataTypeField
index|[]
name|rightFields
init|=
name|rightChild
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|int
name|nRightFields
init|=
name|rightFields
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nRightFields
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|inputRef
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|rightFields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|i
operator|+
name|nLeftFields
argument_list|)
decl_stmt|;
name|bottomProgramBuilder
operator|.
name|addProject
argument_list|(
name|inputRef
argument_list|,
name|rightFields
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RexProgram
name|bottomProgram
init|=
name|bottomProgramBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
comment|// input rowtype into the top program is the concatenation of the
comment|// project and the RHS of the semijoin
name|RelDataType
name|topInputRowType
init|=
name|typeFactory
operator|.
name|createJoinType
argument_list|(
operator|new
name|RelDataType
index|[]
block|{
name|project
operator|.
name|getRowType
argument_list|()
block|,
name|rightChild
operator|.
name|getRowType
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|RexProgramBuilder
name|topProgramBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|topInputRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
name|topProgramBuilder
operator|.
name|addIdentity
argument_list|()
expr_stmt|;
name|topProgramBuilder
operator|.
name|addCondition
argument_list|(
name|semiJoin
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
name|RexProgram
name|topProgram
init|=
name|topProgramBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
comment|// merge the programs and expand out the local references to form
comment|// the new semijoin condition; it now references a concatenation of
comment|// the project's child and the RHS of the semijoin
name|RexProgram
name|mergedProgram
init|=
name|RexProgramBuilder
operator|.
name|mergePrograms
argument_list|(
name|topProgram
argument_list|,
name|bottomProgram
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
return|return
name|mergedProgram
operator|.
name|expandLocalRef
argument_list|(
name|mergedProgram
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End PushSemiJoinPastProjectRule.java
end_comment

end_unit

