begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
comment|/**  * VolcanoPlannerPhaseRuleMappingInitializer describes an inteface for  * initializing the mapping of {@link VolcanoPlannerPhase}s to sets of rule  * descriptions.  *  *<p><b>Note:</b> Rule descriptions are obtained via {@link  * org.eigenbase.relopt.RelOptRule#toString()}. By default they are the class's  * simple name (e.g. class name sans package), unless the class is an inner  * class, in which case the default is the inner class's simple name. Some rules  * explicitly provide alternate descriptions by calling the  * {@link org.eigenbase.relopt.RelOptRule#RelOptRule(org.eigenbase.relopt.RelOptRuleOperand, String)}  * constructor.  */
end_comment

begin_interface
specifier|public
interface|interface
name|VolcanoPlannerPhaseRuleMappingInitializer
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Initializes a {@link VolcanoPlannerPhase}-to-rule map. Rules are    * specified by description (see above). When this method is called, the map    * will already be pre-initialized with empty sets for each    * VolcanoPlannerPhase. Implementations must not return having added or    * removed keys from the map, although it is safe to temporarily add or    * remove keys.    *    * @param phaseRuleMap a {@link VolcanoPlannerPhase}-to-rule map    */
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
function_decl|;
block|}
end_interface

begin_comment
comment|// End VolcanoPlannerPhaseRuleMappingInitializer.java
end_comment

end_unit

