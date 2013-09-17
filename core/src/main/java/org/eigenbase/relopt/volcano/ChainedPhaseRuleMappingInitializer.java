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
operator|.
name|volcano
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * ChainedPhaseRuleMappingInitializer is an abstract implementation of {@link  * VolcanoPlannerPhaseRuleMappingInitializer} that allows additional rules to be  * layered ontop of those configured by a subordinate  * VolcanoPlannerPhaseRuleMappingInitializer.  *  * @author Stephan Zuercher  * @see VolcanoPlannerPhaseRuleMappingInitializer  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ChainedPhaseRuleMappingInitializer
implements|implements
name|VolcanoPlannerPhaseRuleMappingInitializer
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|VolcanoPlannerPhaseRuleMappingInitializer
name|subordinate
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ChainedPhaseRuleMappingInitializer
parameter_list|(
name|VolcanoPlannerPhaseRuleMappingInitializer
name|subordinate
parameter_list|)
block|{
name|this
operator|.
name|subordinate
operator|=
name|subordinate
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|final
name|void
name|initialize
parameter_list|(
name|Map
argument_list|<
name|VolcanoPlannerPhase
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|phaseRuleMap
parameter_list|)
block|{
comment|// Initialize subordinate's mappings.
name|subordinate
operator|.
name|initialize
argument_list|(
name|phaseRuleMap
argument_list|)
expr_stmt|;
comment|// Initialize our mappings.
name|chainedInitialize
argument_list|(
name|phaseRuleMap
argument_list|)
expr_stmt|;
block|}
comment|/**      * Extend this method to provide phase-to-rule mappings beyond what is      * provided by this initializer's subordinate.      *      *<p>When this method is called, the map will already be pre-initialized      * with empty sets for each VolcanoPlannerPhase. Implementations must not      * return having added or removed keys from the map, although it is safe to      * temporarily add or remove keys.      *      * @param phaseRuleMap the {@link VolcanoPlannerPhase}-rule description map      *      * @see VolcanoPlannerPhaseRuleMappingInitializer      */
specifier|public
specifier|abstract
name|void
name|chainedInitialize
parameter_list|(
name|Map
argument_list|<
name|VolcanoPlannerPhase
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|phaseRuleMap
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End ChainedPhaseRuleMappingInitializer.java
end_comment

end_unit

