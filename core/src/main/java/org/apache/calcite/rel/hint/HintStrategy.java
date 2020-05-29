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
name|hint
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
name|rel
operator|.
name|convert
operator|.
name|ConverterRule
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
name|ImmutableSet
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Represents a hint strategy entry of {@link HintStrategyTable}.  *  *<p>A {@code HintStrategy} defines:  *  *<ul>  *<li>{@link HintPredicate}: tests whether a hint should apply to  *   a relational expression;</li>  *<li>{@link HintOptionChecker}: validates the hint options;</li>  *<li>{@code excludedRules}: rules to exclude when a relational expression  *   is going to apply a planner rule;</li>  *<li>{@code converterRules}: fallback rules to apply when there are  *   no proper implementations after excluding the {@code excludedRules}.</li>  *</ul>  *  *<p>The {@link HintPredicate} is required, all the other items are optional.  *  *<p>{@link HintStrategy} is immutable.  */
end_comment

begin_class
specifier|public
class|class
name|HintStrategy
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|HintPredicate
name|predicate
decl_stmt|;
specifier|public
specifier|final
name|HintOptionChecker
name|hintOptionChecker
decl_stmt|;
specifier|public
specifier|final
name|ImmutableSet
argument_list|<
name|RelOptRule
argument_list|>
name|excludedRules
decl_stmt|;
specifier|public
specifier|final
name|ImmutableSet
argument_list|<
name|ConverterRule
argument_list|>
name|converterRules
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|HintStrategy
parameter_list|(
name|HintPredicate
name|predicate
parameter_list|,
name|HintOptionChecker
name|hintOptionChecker
parameter_list|,
name|ImmutableSet
argument_list|<
name|RelOptRule
argument_list|>
name|excludedRules
parameter_list|,
name|ImmutableSet
argument_list|<
name|ConverterRule
argument_list|>
name|converterRules
parameter_list|)
block|{
name|this
operator|.
name|predicate
operator|=
name|predicate
expr_stmt|;
name|this
operator|.
name|hintOptionChecker
operator|=
name|hintOptionChecker
expr_stmt|;
name|this
operator|.
name|excludedRules
operator|=
name|excludedRules
expr_stmt|;
name|this
operator|.
name|converterRules
operator|=
name|converterRules
expr_stmt|;
block|}
comment|/**    * Returns a {@link HintStrategy} builder with given hint predicate.    *    * @param hintPredicate hint predicate    * @return {@link Builder} instance    */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|HintPredicate
name|hintPredicate
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|hintPredicate
argument_list|)
return|;
block|}
comment|//~ Inner Class ------------------------------------------------------------
comment|/** Builder for {@link HintStrategy}. */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
specifier|final
name|HintPredicate
name|predicate
decl_stmt|;
annotation|@
name|Nullable
specifier|private
name|HintOptionChecker
name|optionChecker
decl_stmt|;
specifier|private
name|ImmutableSet
argument_list|<
name|RelOptRule
argument_list|>
name|excludedRules
decl_stmt|;
specifier|private
name|ImmutableSet
argument_list|<
name|ConverterRule
argument_list|>
name|converterRules
decl_stmt|;
specifier|private
name|Builder
parameter_list|(
name|HintPredicate
name|predicate
parameter_list|)
block|{
name|this
operator|.
name|predicate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
name|this
operator|.
name|excludedRules
operator|=
name|ImmutableSet
operator|.
name|of
argument_list|()
expr_stmt|;
name|this
operator|.
name|converterRules
operator|=
name|ImmutableSet
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
comment|/** Registers a hint option checker to validate the hint options. */
specifier|public
name|Builder
name|optionChecker
parameter_list|(
name|HintOptionChecker
name|optionChecker
parameter_list|)
block|{
name|this
operator|.
name|optionChecker
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|optionChecker
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Registers an array of rules to exclude during the      * {@link org.apache.calcite.plan.RelOptPlanner} planning.      *      *<p>The desired converter rules work together with the excluded rules.      * We have no validation here but they expect to have the same      * function(semantic equivalent).      *      *<p>A rule fire cancels if:      *      *<ol>      *<li>The registered {@link #excludedRules} contains the rule</li>      *<li>And the desired converter rules conversion is not possible      *   for the rule matched root node</li>      *</ol>      *      * @param rules excluded rules      */
specifier|public
name|Builder
name|excludedRules
parameter_list|(
name|RelOptRule
modifier|...
name|rules
parameter_list|)
block|{
name|this
operator|.
name|excludedRules
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|rules
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Registers an array of desired converter rules during the      * {@link org.apache.calcite.plan.RelOptPlanner} planning.      *      *<p>The desired converter rules work together with the excluded rules.      * We have no validation here but they expect to have the same      * function(semantic equivalent).      *      *<p>A rule fire cancels if:      *      *<ol>      *<li>The registered {@link #excludedRules} contains the rule</li>      *<li>And the desired converter rules conversion is not possible      *   for the rule matched root node</li>      *</ol>      *      *<p>If no converter rules are specified, we assume the conversion is possible.      *      * @param rules desired converter rules      */
specifier|public
name|Builder
name|converterRules
parameter_list|(
name|ConverterRule
modifier|...
name|rules
parameter_list|)
block|{
name|this
operator|.
name|converterRules
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|rules
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|HintStrategy
name|build
parameter_list|()
block|{
return|return
operator|new
name|HintStrategy
argument_list|(
name|predicate
argument_list|,
name|optionChecker
argument_list|,
name|excludedRules
argument_list|,
name|converterRules
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

