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
name|rel
operator|.
name|convert
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
name|plan
operator|.
name|Convention
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
name|plan
operator|.
name|RelOptRule
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
name|plan
operator|.
name|RelOptRuleCall
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
name|plan
operator|.
name|RelRule
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
name|plan
operator|.
name|RelTrait
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
name|plan
operator|.
name|RelTraitDef
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
name|RelNode
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
name|tools
operator|.
name|RelBuilderFactory
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
name|ImmutableBeans
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|RelRule
argument_list|<
name|ConverterRule
operator|.
name|Config
argument_list|>
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
specifier|protected
specifier|final
name|Convention
name|out
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates a<code>ConverterRule</code>. */
specifier|protected
name|ConverterRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|inTrait
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|config
operator|.
name|inTrait
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|outTrait
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|config
operator|.
name|outTrait
argument_list|()
argument_list|)
expr_stmt|;
comment|// Source and target traits must have same type
assert|assert
name|inTrait
operator|.
name|getTraitDef
argument_list|()
operator|==
name|outTrait
operator|.
name|getTraitDef
argument_list|()
assert|;
comment|// Most sub-classes are concerned with converting one convention to
comment|// another, and for them, the "out" field is a convenient short-cut.
name|this
operator|.
name|out
operator|=
name|outTrait
operator|instanceof
name|Convention
condition|?
operator|(
name|Convention
operator|)
name|outTrait
else|:
literal|null
expr_stmt|;
block|}
comment|/**    * Creates a<code>ConverterRule</code>.    *    * @param clazz       Type of relational expression to consider converting    * @param in          Trait of relational expression to consider converting    * @param out         Trait which is converted to    * @param descriptionPrefix Description prefix of rule    *    * @deprecated Use {@link #ConverterRule(Config)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|descriptionPrefix
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|clazz
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|descriptionPrefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|ConverterRule
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|RelTrait
name|out
parameter_list|,
name|String
name|descriptionPrefix
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|clazz
argument_list|,
operator|(
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
operator|)
name|predicate
operator|::
name|apply
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|descriptionPrefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a<code>ConverterRule</code> with a predicate.    *    * @param clazz       Type of relational expression to consider converting    * @param predicate   Predicate on the relational expression    * @param in          Trait of relational expression to consider converting    * @param out         Trait which is converted to    * @param relBuilderFactory Builder for relational expressions    * @param descriptionPrefix Description prefix of rule    *    * @deprecated Use {@link #ConverterRule(Config)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|ConverterRule
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|RelTrait
name|out
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|descriptionPrefix
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|EMPTY
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withConversion
argument_list|(
name|clazz
argument_list|,
name|predicate
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|descriptionPrefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|ConverterRule
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|RelTrait
name|out
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
operator|(
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
operator|)
name|predicate
operator|::
name|apply
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
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
specifier|private
specifier|static
name|String
name|createDescription
parameter_list|(
name|String
name|descriptionPrefix
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|RelTrait
name|out
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s(in:%s,out:%s)"
argument_list|,
name|Objects
operator|.
name|toString
argument_list|(
name|descriptionPrefix
argument_list|,
literal|"ConverterRule"
argument_list|)
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
return|;
block|}
comment|/** Converts a relational expression to the target trait(s) of this rule.    *    *<p>Returns null if conversion is not possible. */
specifier|public
specifier|abstract
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**    * Returns true if this rule can convert<em>any</em> relational expression    * of the input convention.    *    *<p>The union-to-java converter, for example, is not guaranteed, because    * it only works on unions.</p>    *    * @return {@code true} if this rule can convert<em>any</em> relational    *   expression    */
specifier|public
name|boolean
name|isGuaranteed
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
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
comment|/** Rule configuration. */
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|INSTANCE
init|=
name|EMPTY
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|RelTrait
name|inTrait
parameter_list|()
function_decl|;
comment|/** Sets {@link #inTrait}. */
name|Config
name|withInTrait
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|RelTrait
name|outTrait
parameter_list|()
function_decl|;
comment|/** Sets {@link #outTrait}. */
name|Config
name|withOutTrait
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|Function
argument_list|<
name|Config
argument_list|,
name|ConverterRule
argument_list|>
name|ruleFactory
parameter_list|()
function_decl|;
comment|/** Sets {@link #outTrait}. */
name|Config
name|withRuleFactory
parameter_list|(
name|Function
argument_list|<
name|Config
argument_list|,
name|ConverterRule
argument_list|>
name|factory
parameter_list|)
function_decl|;
specifier|default
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|Config
name|withConversion
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|RelTrait
name|out
parameter_list|,
name|String
name|descriptionPrefix
parameter_list|)
block|{
return|return
name|withInTrait
argument_list|(
name|in
argument_list|)
operator|.
name|withOutTrait
argument_list|(
name|out
argument_list|)
operator|.
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|clazz
argument_list|)
operator|.
name|predicate
argument_list|(
name|predicate
argument_list|)
operator|.
name|convert
argument_list|(
name|in
argument_list|)
argument_list|)
operator|.
name|withDescription
argument_list|(
name|createDescription
argument_list|(
name|descriptionPrefix
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
return|;
block|}
specifier|default
name|Config
name|withConversion
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
name|descriptionPrefix
parameter_list|)
block|{
return|return
name|withConversion
argument_list|(
name|clazz
argument_list|,
name|r
lambda|->
literal|true
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|descriptionPrefix
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|default
name|RelOptRule
name|toRule
parameter_list|()
block|{
return|return
name|toRule
argument_list|(
name|ConverterRule
operator|.
name|class
argument_list|)
return|;
block|}
specifier|default
parameter_list|<
name|R
extends|extends
name|ConverterRule
parameter_list|>
name|R
name|toRule
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|ruleClass
parameter_list|)
block|{
return|return
name|ruleClass
operator|.
name|cast
argument_list|(
name|ruleFactory
argument_list|()
operator|.
name|apply
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

