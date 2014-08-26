begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * MulticastRelOptListener implements the {@link RelOptListener} interface by  * forwarding events on to a collection of other listeners.  */
end_comment

begin_class
specifier|public
class|class
name|MulticastRelOptListener
implements|implements
name|RelOptListener
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|RelOptListener
argument_list|>
name|listeners
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new empty multicast listener.    */
specifier|public
name|MulticastRelOptListener
parameter_list|()
block|{
name|listeners
operator|=
operator|new
name|ArrayList
argument_list|<
name|RelOptListener
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Adds a listener which will receive multicast events.    *    * @param listener listener to add    */
specifier|public
name|void
name|addListener
parameter_list|(
name|RelOptListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|// implement RelOptListener
specifier|public
name|void
name|relEquivalenceFound
parameter_list|(
name|RelEquivalenceEvent
name|event
parameter_list|)
block|{
for|for
control|(
name|RelOptListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|relEquivalenceFound
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement RelOptListener
specifier|public
name|void
name|ruleAttempted
parameter_list|(
name|RuleAttemptedEvent
name|event
parameter_list|)
block|{
for|for
control|(
name|RelOptListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|ruleAttempted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement RelOptListener
specifier|public
name|void
name|ruleProductionSucceeded
parameter_list|(
name|RuleProductionEvent
name|event
parameter_list|)
block|{
for|for
control|(
name|RelOptListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|ruleProductionSucceeded
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement RelOptListener
specifier|public
name|void
name|relChosen
parameter_list|(
name|RelChosenEvent
name|event
parameter_list|)
block|{
for|for
control|(
name|RelOptListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|relChosen
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement RelOptListener
specifier|public
name|void
name|relDiscarded
parameter_list|(
name|RelDiscardedEvent
name|event
parameter_list|)
block|{
for|for
control|(
name|RelOptListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|relDiscarded
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MulticastRelOptListener.java
end_comment

end_unit

