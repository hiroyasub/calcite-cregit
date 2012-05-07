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
name|convert
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Abstract implementation of {@link org.eigenbase.rel.convert.ConverterRel}.  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConverterRelImpl
extends|extends
name|SingleRel
implements|implements
name|ConverterRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
name|RelTraitSet
name|inTraits
decl_stmt|;
specifier|protected
specifier|final
name|RelTraitDef
name|traitDef
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a ConverterRelImpl.      *      * @param cluster planner's cluster      * @param traitDef the RelTraitDef this converter converts      * @param traits the output traits of this converter      * @param child child rel (provides input traits)      */
specifier|protected
name|ConverterRelImpl
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitDef
name|traitDef
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
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
name|inTraits
operator|=
name|child
operator|.
name|getTraitSet
argument_list|()
expr_stmt|;
name|this
operator|.
name|traitDef
operator|=
name|traitDef
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelNode
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
name|getChild
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|dCpu
init|=
name|dRows
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
specifier|protected
name|Error
name|cannotImplement
parameter_list|()
block|{
return|return
name|Util
operator|.
name|newInternal
argument_list|(
name|getClass
argument_list|()
operator|+
literal|" cannot convert from "
operator|+
name|inTraits
operator|+
literal|" traits"
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|getChild
argument_list|()
operator|.
name|isDistinct
argument_list|()
return|;
block|}
specifier|protected
name|CallingConvention
name|getInputConvention
parameter_list|()
block|{
return|return
operator|(
name|CallingConvention
operator|)
name|inTraits
operator|.
name|getTrait
argument_list|(
name|CallingConventionTraitDef
operator|.
name|instance
argument_list|)
return|;
block|}
specifier|public
name|RelTraitSet
name|getInputTraits
parameter_list|()
block|{
return|return
name|inTraits
return|;
block|}
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|traitDef
return|;
block|}
block|}
end_class

begin_comment
comment|// End ConverterRelImpl.java
end_comment

end_unit

