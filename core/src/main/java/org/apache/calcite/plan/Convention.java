begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_comment
comment|/**  * Calling convention trait.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Convention
extends|extends
name|RelTrait
block|{
comment|/**    * Convention that for a relational expression that does not support any    * convention. It is not implementable, and has to be transformed to    * something else in order to be implemented.    *    *<p>Relational expressions generally start off in this form.</p>    *    *<p>Such expressions always have infinite cost.</p>    */
name|Convention
name|NONE
init|=
operator|new
name|Impl
argument_list|(
literal|"NONE"
argument_list|,
name|RelNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|Class
name|getInterface
parameter_list|()
function_decl|;
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Returns whether we should convert from this convention to    * {@code toConvention}. Used by {@link ConventionTraitDef}.    *    * @param toConvention Desired convention to convert to    * @return Whether we should convert from this convention to toConvention    */
specifier|default
name|boolean
name|canConvertConvention
parameter_list|(
name|Convention
name|toConvention
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns whether we should convert from this trait set to the other trait    * set.    *    *<p>The convention decides whether it wants to handle other trait    * conversions, e.g. collation, distribution, etc.  For a given convention, we    * will only add abstract converters to handle the trait (convention,    * collation, distribution, etc.) conversions if this function returns true.    *    * @param fromTraits Traits of the RelNode that we are converting from    * @param toTraits Target traits    * @return Whether we should add converters    */
specifier|default
name|boolean
name|useAbstractConvertersForConversion
parameter_list|(
name|RelTraitSet
name|fromTraits
parameter_list|,
name|RelTraitSet
name|toTraits
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Default implementation.    */
class|class
name|Impl
implements|implements
name|Convention
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
decl_stmt|;
specifier|public
name|Impl
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|relClass
operator|=
name|relClass
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
block|}
specifier|public
name|boolean
name|satisfies
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
name|this
operator|==
name|trait
return|;
block|}
specifier|public
name|Class
name|getInterface
parameter_list|()
block|{
return|return
name|relClass
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|ConventionTraitDef
operator|.
name|INSTANCE
return|;
block|}
specifier|public
name|boolean
name|canConvertConvention
parameter_list|(
name|Convention
name|toConvention
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|useAbstractConvertersForConversion
parameter_list|(
name|RelTraitSet
name|fromTraits
parameter_list|,
name|RelTraitSet
name|toTraits
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_interface

end_unit

