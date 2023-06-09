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
name|RelFactories
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|immutables
operator|.
name|value
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|BiConsumer
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
comment|/**  * Rule that is parameterized via a configuration.  *  *<p>Eventually (before Calcite version 2.0), this class will replace  * {@link RelOptRule}. Constructors of {@code RelOptRule} are deprecated, so new  * rule classes should extend {@code RelRule}, not {@code RelOptRule}.  * Next, we will deprecate {@code RelOptRule}, so that variables that reference  * rules will be of type {@code RelRule}.  *  *<p><b>Guidelines for writing rules</b>  *  *<p>1. If your rule is a sub-class of  * {@link org.apache.calcite.rel.convert.ConverterRule}  * and does not need any extra properties,  * there's no need to create an {@code interface Config} inside your class.  * In your class, create a constant  * {@code public static final Config DEFAULT_CONFIG}. Goto step 5.  *  *<p>2. If your rule is not a sub-class of  * {@link org.apache.calcite.rel.convert.ConverterRule},  * create an inner {@code interface Config extends RelRule.Config} and  * annotate it with {@code @Value.Immutable}. Note, if your inner class  * is two levels deep (e.g. top-level Rule with Config inside), we recommend  * you annotate the outer class with {@code @Value.Enclosing} which will  * instruct the annotation processor to put your generated value class  * inside a new Immutable outer class. If your rule is three levels deep,  * the best thing to do is give your class a unique name to avoid any  * generated code class name overlaps.  * Implement {@link Config#toRule() toRule} using a {@code default} method:  *  *<blockquote>  *<code>  *&#x40;Override default CsvProjectTableScanRule toRule() {<br>  *&nbsp;&nbsp;return new CsvProjectTableScanRule(this);<br>  * }  *</code>  *</blockquote>  *  *<p>3. For each configuration property, create a pair of methods in your  * {@code Config} interface. For example, for a property {@code foo} of type  * {@code int}, create methods {@code foo} and {@code withFoo}:  *  *<blockquote><pre><code>  *&#x2f;** Returns foo. *&#x2f;  * int foo();  *  *&#x2f;** Sets {&#x40;link #foo}. *&#x2f;  * Config withFoo(int x);  *</code></pre></blockquote>  *  *<p>4. In your {@code Config} interface, create a {@code DEFAULT} constant  * that represents the most typical configuration of your rule. This default  * will leverage the Immutables class generated by the Annotation Processor  * based on the annotation you provided above. For example,  * {@code CsvProjectTableScanRule.Config} has the following:  *  *<blockquote><pre><code>  * Config DEFAULT = ImmutableCsvProjectTableScanRule.Config.builder()  *     .withOperandSupplier(b0 -&gt;  *         b0.operand(LogicalProject.class).oneInput(b1 -&gt;  *             b1.operand(CsvTableScan.class).noInputs()))  *      .build();  *</code></pre></blockquote>  *  *<p>5. Do not create an {@code INSTANCE} constant inside your rule.  * Instead, create a named instance of your rule, with default configuration,  * in a holder class. The holder class must not be a sub-class of  * {@code RelOptRule} (otherwise cyclic class-loading issues may arise).  * Generally it will be called<code><i>Xxx</i>Rules</code>, for example  * {@code CsvRules}. The rule instance is named after your rule, and is based  * on the default config ({@code Config.DEFAULT}, or {@code DEFAULT_CONFIG} for  * converter rules):  *  *<blockquote><pre><code>  *&#x2f;** Rule that matches a {&#x40;code Project} on a  *  * {&#x40;code CsvTableScan} and pushes down projects if possible. *&#x2f;  * public static final CsvProjectTableScanRule PROJECT_SCAN =  *     CsvProjectTableScanRule.Config.DEFAULT.toRule();  *</code></pre></blockquote>  *  * @param<C> Configuration type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelRule
parameter_list|<
name|C
extends|extends
name|RelRule
operator|.
name|Config
parameter_list|>
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|final
name|C
name|config
decl_stmt|;
comment|/** Creates a RelRule. */
specifier|protected
name|RelRule
parameter_list|(
name|C
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|OperandBuilderImpl
operator|.
name|operand
argument_list|(
name|config
operator|.
name|operandSupplier
argument_list|()
argument_list|)
argument_list|,
name|config
operator|.
name|relBuilderFactory
argument_list|()
argument_list|,
name|config
operator|.
name|description
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/** Rule configuration. */
specifier|public
interface|interface
name|Config
block|{
comment|/** Creates a rule that uses this configuration. Sub-class must override. */
name|RelOptRule
name|toRule
parameter_list|()
function_decl|;
comment|/** Casts this configuration to another type, usually a sub-class. */
specifier|default
parameter_list|<
name|T
extends|extends
name|Object
parameter_list|>
name|T
name|as
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|class_
parameter_list|)
block|{
if|if
condition|(
name|class_
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|class_
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"The current config of type %s is not an instance of %s."
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
argument_list|,
name|class_
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/** The factory that is used to create a      * {@link org.apache.calcite.tools.RelBuilder} during rule invocations. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|()
block|{
return|return
name|RelFactories
operator|.
name|LOGICAL_BUILDER
return|;
block|}
comment|/** Sets {@link #relBuilderFactory()}. */
name|Config
name|withRelBuilderFactory
parameter_list|(
name|RelBuilderFactory
name|factory
parameter_list|)
function_decl|;
comment|/** Description of the rule instance. */
comment|// CALCITE-4831: remove the second nullable annotation once immutables/#1261 is fixed
annotation|@
name|javax
operator|.
name|annotation
operator|.
name|Nullable
annotation|@
name|Nullable
name|String
name|description
parameter_list|()
function_decl|;
comment|/** Sets {@link #description()}. */
name|Config
name|withDescription
parameter_list|(
annotation|@
name|Nullable
name|String
name|description
parameter_list|)
function_decl|;
comment|/** Creates the operands for the rule instance. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|OperandTransform
name|operandSupplier
parameter_list|()
block|{
return|return
name|s
lambda|->
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Rules must have at least one "
operator|+
literal|"operand. Call Config.withOperandSupplier to specify them."
argument_list|)
throw|;
block|}
return|;
block|}
comment|/** Sets {@link #operandSupplier()}. */
name|Config
name|withOperandSupplier
parameter_list|(
name|OperandTransform
name|transform
parameter_list|)
function_decl|;
block|}
comment|/** Function that creates an operand.    *    * @see Config#withOperandSupplier(OperandTransform) */
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|OperandTransform
extends|extends
name|Function
argument_list|<
name|OperandBuilder
argument_list|,
name|Done
argument_list|>
block|{   }
comment|/** Callback to create an operand.    *    * @see OperandTransform */
specifier|public
interface|interface
name|OperandBuilder
block|{
comment|/** Starts building an operand by specifying its class.      * Call further methods on the returned {@link OperandDetailBuilder} to      * complete the operand. */
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|OperandDetailBuilder
argument_list|<
name|R
argument_list|>
name|operand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|relClass
parameter_list|)
function_decl|;
comment|/** Supplies an operand that has been built manually. */
name|Done
name|exactly
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|)
function_decl|;
block|}
comment|/** Indicates that an operand is complete.    *    * @see OperandTransform */
specifier|public
interface|interface
name|Done
block|{   }
comment|/** Add details about an operand, such as its inputs.    *    * @param<R> Type of relational expression */
specifier|public
interface|interface
name|OperandDetailBuilder
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
block|{
comment|/** Sets a trait of this operand. */
name|OperandDetailBuilder
argument_list|<
name|R
argument_list|>
name|trait
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
function_decl|;
comment|/** Sets the predicate of this operand. */
name|OperandDetailBuilder
argument_list|<
name|R
argument_list|>
name|predicate
parameter_list|(
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|)
function_decl|;
comment|/** Indicates that this operand has a single input. */
name|Done
name|oneInput
parameter_list|(
name|OperandTransform
name|transform
parameter_list|)
function_decl|;
comment|/** Indicates that this operand has several inputs. */
name|Done
name|inputs
parameter_list|(
name|OperandTransform
modifier|...
name|transforms
parameter_list|)
function_decl|;
comment|/** Indicates that this operand has several inputs, unordered. */
name|Done
name|unorderedInputs
parameter_list|(
name|OperandTransform
modifier|...
name|transforms
parameter_list|)
function_decl|;
comment|/** Indicates that this operand takes any number or type of inputs. */
name|Done
name|anyInputs
parameter_list|()
function_decl|;
comment|/** Indicates that this operand takes no inputs. */
name|Done
name|noInputs
parameter_list|()
function_decl|;
comment|/** Indicates that this operand converts a relational expression to      * another trait. */
name|Done
name|convert
parameter_list|(
name|RelTrait
name|in
parameter_list|)
function_decl|;
block|}
comment|/** Implementation of {@link OperandBuilder}. */
specifier|private
specifier|static
class|class
name|OperandBuilderImpl
implements|implements
name|OperandBuilder
block|{
specifier|final
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|operands
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|static
name|RelOptRuleOperand
name|operand
parameter_list|(
name|OperandTransform
name|transform
parameter_list|)
block|{
specifier|final
name|OperandBuilderImpl
name|b
init|=
operator|new
name|OperandBuilderImpl
argument_list|()
decl_stmt|;
specifier|final
name|Done
name|done
init|=
name|transform
operator|.
name|apply
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|done
argument_list|,
literal|"done"
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|operands
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"operand supplier must call one of "
operator|+
literal|"the following methods: operand or exactly"
argument_list|)
throw|;
block|}
return|return
name|b
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|OperandDetailBuilder
argument_list|<
name|R
argument_list|>
name|operand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|relClass
parameter_list|)
block|{
return|return
operator|new
name|OperandDetailBuilderImpl
argument_list|<>
argument_list|(
name|this
argument_list|,
name|relClass
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Done
name|exactly
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|)
block|{
name|operands
operator|.
name|add
argument_list|(
name|operand
argument_list|)
expr_stmt|;
return|return
name|DoneImpl
operator|.
name|INSTANCE
return|;
block|}
block|}
comment|/** Implementation of {@link OperandDetailBuilder}.    *    * @param<R> Type of relational expression */
specifier|private
specifier|static
class|class
name|OperandDetailBuilderImpl
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
implements|implements
name|OperandDetailBuilder
argument_list|<
name|R
argument_list|>
block|{
specifier|private
specifier|final
name|OperandBuilderImpl
name|parent
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|R
argument_list|>
name|relClass
decl_stmt|;
specifier|final
name|OperandBuilderImpl
name|inputBuilder
init|=
operator|new
name|OperandBuilderImpl
argument_list|()
decl_stmt|;
specifier|private
annotation|@
name|Nullable
name|RelTrait
name|trait
decl_stmt|;
specifier|private
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
init|=
name|r
lambda|->
literal|true
decl_stmt|;
name|OperandDetailBuilderImpl
parameter_list|(
name|OperandBuilderImpl
name|parent
parameter_list|,
name|Class
argument_list|<
name|R
argument_list|>
name|relClass
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parent
argument_list|,
literal|"parent"
argument_list|)
expr_stmt|;
name|this
operator|.
name|relClass
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|relClass
argument_list|,
literal|"relClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|OperandDetailBuilderImpl
argument_list|<
name|R
argument_list|>
name|trait
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
name|this
operator|.
name|trait
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|trait
argument_list|,
literal|"trait"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|OperandDetailBuilderImpl
argument_list|<
name|R
argument_list|>
name|predicate
parameter_list|(
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|)
block|{
name|this
operator|.
name|predicate
operator|=
name|predicate
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Indicates that there are no more inputs. */
name|Done
name|done
parameter_list|(
name|RelOptRuleOperandChildPolicy
name|childPolicy
parameter_list|)
block|{
name|parent
operator|.
name|operands
operator|.
name|add
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|relClass
argument_list|,
name|trait
argument_list|,
name|predicate
argument_list|,
name|childPolicy
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|inputBuilder
operator|.
name|operands
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|DoneImpl
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Done
name|convert
parameter_list|(
name|RelTrait
name|in
parameter_list|)
block|{
name|parent
operator|.
name|operands
operator|.
name|add
argument_list|(
operator|new
name|ConverterRelOptRuleOperand
argument_list|(
name|relClass
argument_list|,
name|in
argument_list|,
name|predicate
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|DoneImpl
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Done
name|noInputs
parameter_list|()
block|{
return|return
name|done
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|LEAF
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Done
name|anyInputs
parameter_list|()
block|{
return|return
name|done
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|ANY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Done
name|oneInput
parameter_list|(
name|OperandTransform
name|transform
parameter_list|)
block|{
specifier|final
name|Done
name|done
init|=
name|transform
operator|.
name|apply
argument_list|(
name|inputBuilder
argument_list|)
decl_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|done
argument_list|,
literal|"done"
argument_list|)
expr_stmt|;
return|return
name|done
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|SOME
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Done
name|inputs
parameter_list|(
name|OperandTransform
modifier|...
name|transforms
parameter_list|)
block|{
for|for
control|(
name|OperandTransform
name|transform
range|:
name|transforms
control|)
block|{
specifier|final
name|Done
name|done
init|=
name|transform
operator|.
name|apply
argument_list|(
name|inputBuilder
argument_list|)
decl_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|done
argument_list|,
literal|"done"
argument_list|)
expr_stmt|;
block|}
return|return
name|done
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|SOME
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Done
name|unorderedInputs
parameter_list|(
name|OperandTransform
modifier|...
name|transforms
parameter_list|)
block|{
for|for
control|(
name|OperandTransform
name|transform
range|:
name|transforms
control|)
block|{
specifier|final
name|Done
name|done
init|=
name|transform
operator|.
name|apply
argument_list|(
name|inputBuilder
argument_list|)
decl_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|done
argument_list|,
literal|"done"
argument_list|)
expr_stmt|;
block|}
return|return
name|done
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|UNORDERED
argument_list|)
return|;
block|}
block|}
comment|/** Singleton instance of {@link Done}. */
specifier|private
enum|enum
name|DoneImpl
implements|implements
name|Done
block|{
name|INSTANCE
block|}
comment|/** Callback interface that helps you avoid creating sub-classes of    * {@link RelRule} that differ only in implementations of    * {@link #onMatch(RelOptRuleCall)} method.    *    * @param<R> Rule type */
specifier|public
interface|interface
name|MatchHandler
parameter_list|<
name|R
extends|extends
name|RelOptRule
parameter_list|>
extends|extends
name|BiConsumer
argument_list|<
name|R
argument_list|,
name|RelOptRuleCall
argument_list|>
block|{   }
block|}
end_class

end_unit

