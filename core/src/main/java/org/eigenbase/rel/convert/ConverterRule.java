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
name|relopt
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Abstract base class for a rule which converts from one calling convention to  * another without changing semantics.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConverterRule
extends|extends
name|RelOptRule
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelTrait
name|inTrait
decl_stmt|;
specifier|private
specifier|final
name|RelTrait
name|outTrait
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a<code>ConverterRule</code>.    *    * @param clazz       Type of relational expression to consider converting    * @param in          Trait of relational expression to consider converting    * @param out         Trait which is converted to    * @param description Description of rule    * @pre in != null    * @pre out != null    */
specifier|public
name|ConverterRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|RelTrait
name|out
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|ConverterRelOptRuleOperand
argument_list|(
name|clazz
argument_list|,
name|in
argument_list|)
argument_list|,
name|description
operator|==
literal|null
condition|?
literal|"ConverterRule<in="
operator|+
name|in
operator|+
literal|",out="
operator|+
name|out
operator|+
literal|">"
else|:
name|description
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|in
operator|!=
literal|null
operator|)
assert|;
assert|assert
operator|(
name|out
operator|!=
literal|null
operator|)
assert|;
comment|// Source and target traits must have same type
assert|assert
name|in
operator|.
name|getTraitDef
argument_list|()
operator|==
name|out
operator|.
name|getTraitDef
argument_list|()
assert|;
name|this
operator|.
name|inTrait
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|outTrait
operator|=
name|out
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
operator|(
name|Convention
operator|)
name|outTrait
return|;
block|}
specifier|public
name|RelTrait
name|getOutTrait
parameter_list|()
block|{
return|return
name|outTrait
return|;
block|}
specifier|public
name|RelTrait
name|getInTrait
parameter_list|()
block|{
return|return
name|inTrait
return|;
block|}
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|inTrait
operator|.
name|getTraitDef
argument_list|()
return|;
block|}
specifier|public
specifier|abstract
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**    * Returns true if this rule can convert<em>any</em> relational expression    * of the input convention.    *    *<p>The union-to-java converter, for example, is not guaranteed, because    * it only works on unions.</p>    */
specifier|public
name|boolean
name|isGuaranteed
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|RelNode
name|rel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|inTrait
argument_list|)
condition|)
block|{
specifier|final
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|converted
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|converted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
specifier|static
class|class
name|ConverterRelOptRuleOperand
extends|extends
name|RelOptRuleOperand
block|{
specifier|public
name|ConverterRelOptRuleOperand
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|RelTrait
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
argument_list|,
name|in
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
comment|// Don't apply converters to converters that operate
comment|// on the same RelTraitDef -- otherwise we get
comment|// an n^2 effect.
if|if
condition|(
name|rel
operator|instanceof
name|ConverterRel
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|ConverterRule
operator|)
name|getRule
argument_list|()
operator|)
operator|.
name|getTraitDef
argument_list|()
operator|==
operator|(
operator|(
name|ConverterRel
operator|)
name|rel
operator|)
operator|.
name|getTraitDef
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|super
operator|.
name|matches
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ConverterRule.java
end_comment

end_unit

