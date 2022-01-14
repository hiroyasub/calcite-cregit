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
operator|.
name|hep
package|;
end_package

begin_comment
comment|/** Able to execute an instruction or program, and contains all mutable state  * for that instruction.  *  *<p>The goal is that programs are re-entrant - they can be used by more than  * one thread at a time. We achieve this by making instructions and programs  * immutable. All mutable state is held in the state objects.  *  *<p>State objects are allocated, just before the program is executed, by  * calling {@link HepInstruction#prepare(HepInstruction.PrepareContext)} on the  * program and recursively on all of its instructions. */
end_comment

begin_class
specifier|abstract
class|class
name|HepState
block|{
specifier|final
name|HepPlanner
name|planner
decl_stmt|;
specifier|final
name|HepProgram
operator|.
name|State
name|programState
decl_stmt|;
name|HepState
parameter_list|(
name|HepInstruction
operator|.
name|PrepareContext
name|px
parameter_list|)
block|{
name|this
operator|.
name|planner
operator|=
name|px
operator|.
name|planner
expr_stmt|;
name|this
operator|.
name|programState
operator|=
name|px
operator|.
name|programState
expr_stmt|;
block|}
comment|/** Executes the instruction. */
specifier|abstract
name|void
name|execute
parameter_list|()
function_decl|;
comment|/** Re-initializes the state. (The state was initialized when it was created    * via {@link HepInstruction#prepare}.) */
name|void
name|init
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

