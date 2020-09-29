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
name|RelDistributions
import|;
end_import

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
name|core
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|mapping
operator|.
name|Mappings
import|;
end_import

begin_comment
comment|/**  * RelTrait represents the manifestation of a relational expression trait within  * a trait definition. For example, a {@code CallingConvention.JAVA} is a trait  * of the {@link ConventionTraitDef} trait definition.  *  *<h2><a id="EqualsHashCodeNote">Note about equals() and hashCode()</a></h2>  *  *<p>If all instances of RelTrait for a particular RelTraitDef are defined in  * an {@code enum} and no new RelTraits can be introduced at runtime, you need  * not override {@link #hashCode()} and {@link #equals(Object)}. If, however,  * new RelTrait instances are generated at runtime (e.g. based on state external  * to the planner), you must implement {@link #hashCode()} and  * {@link #equals(Object)} for proper {@link RelTraitDef#canonize canonization}  * of your RelTrait objects.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelTrait
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the RelTraitDef that defines this RelTrait.    *    * @return the RelTraitDef that defines this RelTrait    */
name|RelTraitDef
name|getTraitDef
parameter_list|()
function_decl|;
comment|/**    * See<a href="#EqualsHashCodeNote">note about equals() and hashCode()</a>.    */
annotation|@
name|Override
name|int
name|hashCode
parameter_list|()
function_decl|;
comment|/**    * See<a href="#EqualsHashCodeNote">note about equals() and hashCode()</a>.    */
annotation|@
name|Override
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
comment|/**    * Returns whether this trait satisfies a given trait.    *    *<p>A trait satisfies another if it is the same or stricter. For example,    * {@code ORDER BY x, y} satisfies {@code ORDER BY x}.    *    *<p>A trait's {@code satisfies} relation must be a partial order (reflexive,    * anti-symmetric, transitive). Many traits cannot be "loosened"; their    * {@code satisfies} is an equivalence relation, where only X satisfies X.    *    *<p>If a trait has multiple values    * (see {@link org.apache.calcite.plan.RelCompositeTrait})    * a collection (T0, T1, ...) satisfies T if any Ti satisfies T.    *    * @param trait Given trait    * @return Whether this trait subsumes a given trait    */
name|boolean
name|satisfies
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
function_decl|;
comment|/**    * Returns a succinct name for this trait. The planner may use this String    * to describe the trait.    */
annotation|@
name|Override
name|String
name|toString
parameter_list|()
function_decl|;
comment|/**    * Registers a trait instance with the planner.    *    *<p>This is an opportunity to add rules that relate to that trait. However,    * typical implementations will do nothing.</p>    *    * @param planner Planner    */
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
function_decl|;
comment|/**    * Applies a mapping to this trait.    *    *<p>Some traits may be changed if the columns order is changed by a mapping    * of the {@link Project} operator.</p>    *    *<p>For example, if relation {@code SELECT a, b ORDER BY a, b} is sorted by    * columns [0, 1], then the project {@code SELECT b, a} over this relation    * will be sorted by columns [1, 0]. In the same time project {@code SELECT b}    * will not be sorted at all because it doesn't contain the collation    * prefix and this method will return an empty collation.</p>    *    *<p>Other traits are independent from the columns remapping. For example    * {@link Convention} or {@link RelDistributions#SINGLETON}.</p>    *    * @param mapping   Mapping    * @return trait with mapping applied    */
specifier|default
parameter_list|<
name|T
extends|extends
name|RelTrait
parameter_list|>
name|T
name|apply
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**    * Returns whether this trait is the default trait value.    */
specifier|default
name|boolean
name|isDefault
parameter_list|()
block|{
return|return
name|this
operator|==
name|getTraitDef
argument_list|()
operator|.
name|getDefault
argument_list|()
return|;
block|}
block|}
end_interface

end_unit

