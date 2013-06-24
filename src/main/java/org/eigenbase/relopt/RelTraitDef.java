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
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|*
import|;
end_import

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
name|convert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * RelTraitDef represents a class of {@link RelTrait}s. Implementations of  * RelTraitDef may be singletons under the following conditions:  *  *<ol>  *<li>if the set of all possible associated RelTraits is finite and fixed (e.g.  * all RelTraits for this RelTraitDef are known at compile time). For example,  * the CallingConvention trait meets this requirement, because CallingConvention  * is effectively an enumeration.</li>  *<li>Either  *  *<ul>  *<li> {@link #canConvert(RelOptPlanner, RelTrait, RelTrait)} and {@link  * #convert(RelOptPlanner, RelNode, RelTrait, boolean)} do not require  * planner-instance-specific information,<b>or</b></li>  *<li>the RelTraitDef manages separate sets of conversion data internally. See  * {@link ConventionTraitDef} for an example of this.</li>  *</ul>  *</li>  *</ol>  *  *<p>Otherwise, a new instance of RelTraitDef must be constructed and  * registered with each new planner instantiated.</p>  *  * @author Stephan Zuercher  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelTraitDef
parameter_list|<
name|T
extends|extends
name|RelTrait
parameter_list|>
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|WeakHashMap
argument_list|<
name|RelTrait
argument_list|,
name|WeakReference
argument_list|<
name|RelTrait
argument_list|>
argument_list|>
name|canonicalMap
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelTraitDef
parameter_list|()
block|{
name|this
operator|.
name|canonicalMap
operator|=
operator|new
name|WeakHashMap
argument_list|<
name|RelTrait
argument_list|,
name|WeakReference
argument_list|<
name|RelTrait
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Whether a relational expression may possess more than one instance of      * this trait simultaneously.      *      *<p>A subset has only one instance of a trait.</p>      */
specifier|public
name|boolean
name|multiple
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return the specific RelTrait type associated with this RelTraitDef.      */
specifier|public
specifier|abstract
name|Class
argument_list|<
name|T
argument_list|>
name|getTraitClass
parameter_list|()
function_decl|;
comment|/**      * @return a simple name for this RelTraitDef (for use in      * {@link org.eigenbase.rel.RelNode#explain(RelOptPlanWriter)}).      */
specifier|public
specifier|abstract
name|String
name|getSimpleName
parameter_list|()
function_decl|;
comment|/**      * Takes an arbitrary RelTrait and returns the canonical representation of      * that RelTrait. Canonized RelTrait objects may always be compared using      * the equality operator (<code>==</code>).      *      *<p>If an equal RelTrait has already been canonized and is still in use,      * it will be returned. Otherwise, the given RelTrait is made canonical and      * returned.      *      * @param trait a possibly non-canonical RelTrait      *      * @return a canonical RelTrait.      */
specifier|public
specifier|final
name|RelTrait
name|canonize
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
assert|assert
name|getTraitClass
argument_list|()
operator|.
name|isInstance
argument_list|(
name|trait
argument_list|)
operator|:
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" cannot canonize a "
operator|+
name|trait
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
assert|;
if|if
condition|(
name|canonicalMap
operator|.
name|containsKey
argument_list|(
name|trait
argument_list|)
condition|)
block|{
name|WeakReference
argument_list|<
name|RelTrait
argument_list|>
name|canonicalTraitRef
init|=
name|canonicalMap
operator|.
name|get
argument_list|(
name|trait
argument_list|)
decl_stmt|;
if|if
condition|(
name|canonicalTraitRef
operator|!=
literal|null
condition|)
block|{
comment|// Make sure the canonical trait didn't disappear between
comment|// containsKey and get.
name|RelTrait
name|canonicalTrait
init|=
name|canonicalTraitRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|canonicalTrait
operator|!=
literal|null
condition|)
block|{
comment|// Make sure the canonical trait didn't disappear between
comment|// WeakHashMap.get() and WeakReference.get()
return|return
name|canonicalTrait
return|;
block|}
block|}
block|}
comment|// Canonical trait wasn't in map or was *very* recently removed from
comment|// the map. Removal, however, indicates that no other references to
comment|// the canonical trait existed, so the caller's trait becomes
comment|// canonical.
name|canonicalMap
operator|.
name|put
argument_list|(
name|trait
argument_list|,
operator|new
name|WeakReference
argument_list|<
name|RelTrait
argument_list|>
argument_list|(
name|trait
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|trait
return|;
block|}
comment|/**      * Converts the given RelNode to the given RelTrait.      *      * @param planner the planner requesting the conversion      * @param rel RelNode to convert      * @param toTrait RelTrait to convert to      * @param allowInfiniteCostConverters flag indicating whether infinite cost      * converters are allowed      *      * @return a converted RelNode or null if conversion is not possible      */
specifier|public
specifier|abstract
name|RelNode
name|convert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|T
name|toTrait
parameter_list|,
name|boolean
name|allowInfiniteCostConverters
parameter_list|)
function_decl|;
comment|/**      * Tests whether the given RelTrait can be converted to another RelTrait.      *      * @param planner the planner requesting the conversion test      * @param fromTrait the RelTrait to convert from      * @param toTrait the RelTrait to convert to      *      * @return true if fromTrait can be converted to toTrait      */
specifier|public
specifier|abstract
name|boolean
name|canConvert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|T
name|fromTrait
parameter_list|,
name|T
name|toTrait
parameter_list|)
function_decl|;
comment|/**      * Provides notification of the registration of a particular {@link      * ConverterRule} with a {@link RelOptPlanner}. The default implementation      * does nothing.      *      * @param planner the planner registering the rule      * @param converterRule the registered converter rule      */
specifier|public
name|void
name|registerConverterRule
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|ConverterRule
name|converterRule
parameter_list|)
block|{
block|}
comment|/**      * Provides notification that a particular {@link ConverterRule} has been      * de-registered from a {@link RelOptPlanner}. The default implementation      * does nothing.      *      * @param planner the planner registering the rule      * @param converterRule the registered converter rule      */
specifier|public
name|void
name|deregisterConverterRule
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|ConverterRule
name|converterRule
parameter_list|)
block|{
block|}
comment|/**      * Returns the default member of this trait.      */
specifier|public
specifier|abstract
name|T
name|getDefault
parameter_list|()
function_decl|;
block|}
end_class

begin_comment
comment|// End RelTraitDef.java
end_comment

end_unit

