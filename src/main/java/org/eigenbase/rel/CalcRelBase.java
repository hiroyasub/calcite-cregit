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
name|metadata
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
comment|/**  *<code>CalcRelBase</code> is an abstract base class for implementations of  * {@link CalcRel}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CalcRelBase
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RexProgram
name|program
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|CalcRelBase
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|program
operator|=
name|program
expr_stmt|;
name|this
operator|.
name|collationList
operator|=
name|collationList
operator|.
name|isEmpty
argument_list|()
condition|?
name|Collections
operator|.
expr|<
name|RelCollation
operator|>
name|emptyList
argument_list|()
else|:
name|collationList
expr_stmt|;
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
operator|!
name|RelOptUtil
operator|.
name|equal
argument_list|(
literal|"program's input type"
argument_list|,
name|program
operator|.
name|getInputRowType
argument_list|()
argument_list|,
literal|"child's output type"
argument_list|,
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|fail
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|RelOptUtil
operator|.
name|equal
argument_list|(
literal|"rowtype of program"
argument_list|,
name|program
operator|.
name|getOutputRowType
argument_list|()
argument_list|,
literal|"declared rowtype of rel"
argument_list|,
name|rowType
argument_list|,
name|fail
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|program
operator|.
name|isValid
argument_list|(
name|fail
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|program
operator|.
name|isNormalized
argument_list|(
name|fail
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|RelCollationImpl
operator|.
name|isValid
argument_list|(
name|getRowType
argument_list|()
argument_list|,
name|collationList
argument_list|,
name|fail
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|RexProgram
name|getProgram
parameter_list|()
block|{
return|return
name|program
return|;
block|}
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|FilterRel
operator|.
name|estimateFilteredRows
argument_list|(
name|getChild
argument_list|()
argument_list|,
name|program
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|collationList
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|double
name|dRows
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|double
name|dCpu
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|getChild
argument_list|()
argument_list|)
operator|*
name|program
operator|.
name|getExprCount
argument_list|()
decl_stmt|;
name|double
name|dIo
init|=
literal|0
decl_stmt|;
return|return
name|planner
operator|.
name|makeCost
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
argument_list|)
return|;
block|}
specifier|public
name|RexNode
index|[]
name|getChildExps
parameter_list|()
block|{
return|return
name|RexNode
operator|.
name|EMPTY_ARRAY
return|;
block|}
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
return|return
name|program
operator|.
name|explainCalc
argument_list|(
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End CalcRelBase.java
end_comment

end_unit

