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
name|relopt
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
comment|/**  * A<code>RelOptCluster</code> is a collection of {@link RelNode relational  * expressions} which have the same environment.  *  *<p>See the comment against<code>net.sf.saffron.oj.xlat.QueryInfo</code> on  * why you should put fields in that class, not this one.</p>  *  * @author jhyde  * @version $Id$  * @since 27 September, 2001  */
end_comment

begin_class
specifier|public
class|class
name|RelOptCluster
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|RelOptQuery
name|query
decl_stmt|;
specifier|private
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
name|RexNode
name|originalExpression
decl_stmt|;
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|private
name|RelMetadataProvider
name|metadataProvider
decl_stmt|;
specifier|private
specifier|final
name|RelTraitSet
name|emptyTraitSet
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a cluster.      */
name|RelOptCluster
parameter_list|(
name|RelOptQuery
name|query
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelTraitSet
name|emptyTraitSet
parameter_list|)
block|{
assert|assert
name|planner
operator|!=
literal|null
assert|;
assert|assert
name|typeFactory
operator|!=
literal|null
assert|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
name|this
operator|.
name|originalExpression
operator|=
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
comment|// set up a default rel metadata provider,
comment|// giving the planner first crack at everything
name|metadataProvider
operator|=
operator|new
name|DefaultRelMetadataProvider
argument_list|()
expr_stmt|;
name|this
operator|.
name|emptyTraitSet
operator|=
name|emptyTraitSet
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelOptQuery
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
specifier|public
name|RexNode
name|getOriginalExpression
parameter_list|()
block|{
return|return
name|originalExpression
return|;
block|}
specifier|public
name|void
name|setOriginalExpression
parameter_list|(
name|RexNode
name|originalExpression
parameter_list|)
block|{
name|this
operator|.
name|originalExpression
operator|=
name|originalExpression
expr_stmt|;
block|}
specifier|public
name|RelOptPlanner
name|getPlanner
parameter_list|()
block|{
return|return
name|planner
return|;
block|}
specifier|public
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactory
return|;
block|}
specifier|public
name|RexBuilder
name|getRexBuilder
parameter_list|()
block|{
return|return
name|rexBuilder
return|;
block|}
specifier|public
name|RelMetadataProvider
name|getMetadataProvider
parameter_list|()
block|{
return|return
name|metadataProvider
return|;
block|}
comment|/**      * Overrides the default metadata provider for this cluster.      *      * @param metadataProvider custom provider      */
specifier|public
name|void
name|setMetadataProvider
parameter_list|(
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
name|this
operator|.
name|metadataProvider
operator|=
name|metadataProvider
expr_stmt|;
block|}
specifier|public
name|RelTraitSet
name|getEmptyTraitSet
parameter_list|()
block|{
return|return
name|emptyTraitSet
return|;
block|}
specifier|public
name|RelTraitSet
name|traitSetOf
parameter_list|(
name|RelTrait
modifier|...
name|traits
parameter_list|)
block|{
return|return
name|planner
operator|.
name|emptyTraitSet
argument_list|()
operator|.
name|plusAll
argument_list|(
name|traits
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptCluster.java
end_comment

end_unit

