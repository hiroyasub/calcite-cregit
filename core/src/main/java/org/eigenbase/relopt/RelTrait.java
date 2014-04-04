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

begin_comment
comment|/**  * RelTrait represents the manifestation of a relational expression trait within  * a trait definition. For example, a {@code CallingConvention.JAVA} is a trait  * of the {@link ConventionTraitDef} trait definition.  *  *<h3><a name="EqualsHashCodeNote">Note about equals() and hashCode()</a></h3>  *  *<p>If all instances of RelTrait for a particular RelTraitDef are defined in  * an {@code enum} and no new RelTraits can be introduced at runtime, you need  * not override {@link #hashCode()} and {@link #equals(Object)}. If, however,  * new RelTrait instances are generated at runtime (e.g. based on state external  * to the planner), you must implement {@link #hashCode()} and {@link  * #equals(Object)} for proper {@link RelTraitDef#canonize canonization} of your  * RelTrait objects.</p>  */
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
name|int
name|hashCode
parameter_list|()
function_decl|;
comment|/**    * See<a href="#EqualsHashCodeNote">note about equals() and hashCode()</a>.    */
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
comment|/**    * Returns whether this trait subsumes a given trait.    *    *<p>Must form a partial order: must be reflective (t subsumes t),    * anti-symmetric (if t1 subsumes t2 and t1 != t2 then t2 does not subsume    * t1),    * and transitive (if t1 subsumes t2 and t2 subsumes t3, then t1 subsumes    * t3)</p>    *    *<p>Many traits cannot be substituted, in which case, this method should    * return {@code equals(trait)}.</p>    */
name|boolean
name|subsumes
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
function_decl|;
comment|/**    * Returns a succinct name for this trait. The planner may use this String    * to describe the trait.    */
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
block|}
end_interface

begin_comment
comment|// End RelTrait.java
end_comment

end_unit

