begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptRule
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Utilities for creating and composing rule sets.  *  * @see net.hydromatic.optiq.tools.RuleSet  */
end_comment

begin_class
specifier|public
class|class
name|RuleSets
block|{
specifier|private
name|RuleSets
parameter_list|()
block|{
block|}
comment|/** Creates a rule set with a given array of rules. */
specifier|public
specifier|static
name|RuleSet
name|ofList
parameter_list|(
name|RelOptRule
modifier|...
name|rules
parameter_list|)
block|{
return|return
operator|new
name|ListRuleSet
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rules
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a rule set with a given collection of rules. */
specifier|public
specifier|static
name|RuleSet
name|ofList
parameter_list|(
name|Collection
argument_list|<
name|RelOptRule
argument_list|>
name|rules
parameter_list|)
block|{
return|return
operator|new
name|ListRuleSet
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rules
argument_list|)
argument_list|)
return|;
block|}
comment|/** Rule set that consists of a list of rules. */
specifier|private
specifier|static
class|class
name|ListRuleSet
implements|implements
name|RuleSet
block|{
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|RelOptRule
argument_list|>
name|rules
decl_stmt|;
specifier|public
name|ListRuleSet
parameter_list|(
name|ImmutableList
argument_list|<
name|RelOptRule
argument_list|>
name|rules
parameter_list|)
block|{
name|this
operator|.
name|rules
operator|=
name|rules
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|rules
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|ListRuleSet
operator|&&
name|rules
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ListRuleSet
operator|)
name|obj
operator|)
operator|.
name|rules
argument_list|)
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|RelOptRule
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|rules
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RuleSets.java
end_comment

end_unit

