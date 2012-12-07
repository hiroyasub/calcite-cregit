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
comment|/**  * A SemiJoinRel represents two relational expressions joined according to some  * condition, where the output only contains the columns from the left join  * input.  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SemiJoinRel
extends|extends
name|JoinRelBase
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|rightKeys
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * @param cluster cluster that join belongs to      * @param left left join input      * @param right right join input      * @param condition join condition      * @param leftKeys left keys of the semijoin      * @param rightKeys right keys of the semijoin      */
specifier|public
name|SemiJoinRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|rightKeys
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
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|leftKeys
operator|=
name|leftKeys
expr_stmt|;
name|this
operator|.
name|rightKeys
operator|=
name|rightKeys
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SemiJoinRel
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RexNode
name|conditionExpr
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|)
block|{
return|return
operator|new
name|SemiJoinRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|conditionExpr
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|getLeftKeys
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|getRightKeys
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|// implement RelNode
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// REVIEW jvs 9-Apr-2006:  Just for now...
return|return
name|planner
operator|.
name|makeTinyCost
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|public
name|double
name|getRows
parameter_list|()
block|{
comment|// TODO:  correlation factor
return|return
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|left
argument_list|)
operator|*
name|RexUtil
operator|.
name|getSelectivity
argument_list|(
name|condition
argument_list|)
return|;
block|}
comment|/**      * @return returns rowtype representing only the left join input      */
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|deriveJoinRowType
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|null
argument_list|,
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
expr|<
name|RelDataTypeField
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getLeftKeys
parameter_list|()
block|{
return|return
name|leftKeys
return|;
block|}
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getRightKeys
parameter_list|()
block|{
return|return
name|rightKeys
return|;
block|}
block|}
end_class

begin_comment
comment|// End SemiJoinRel.java
end_comment

end_unit

