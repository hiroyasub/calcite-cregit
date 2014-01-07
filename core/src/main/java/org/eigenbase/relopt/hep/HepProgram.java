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
name|hep
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

begin_comment
comment|/**  * HepProgram specifies the order in which rules should be attempted by {@link  * HepPlanner}. Use {@link HepProgramBuilder} to create a new instance of  * HepProgram.  *  *<p>Note that the structure of a program is immutable, but the planner uses it  * as read/write during planning, so a program can only be in use by a single  * planner at a time.  */
end_comment

begin_class
specifier|public
class|class
name|HepProgram
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * Symbolic constant for matching until no more matches occur.      */
specifier|public
specifier|static
specifier|final
name|int
name|MATCH_UNTIL_FIXPOINT
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|final
name|ImmutableList
argument_list|<
name|HepInstruction
argument_list|>
name|instructions
decl_stmt|;
name|int
name|matchLimit
decl_stmt|;
name|HepMatchOrder
name|matchOrder
decl_stmt|;
name|HepInstruction
operator|.
name|EndGroup
name|group
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a new empty HepProgram. The program has an initial match order of      * {@link org.eigenbase.relopt.hep.HepMatchOrder#ARBITRARY}, and an initial      * match limit of {@link #MATCH_UNTIL_FIXPOINT}.      */
name|HepProgram
parameter_list|(
name|List
argument_list|<
name|HepInstruction
argument_list|>
name|instructions
parameter_list|)
block|{
name|this
operator|.
name|instructions
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|instructions
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|HepProgramBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|HepProgramBuilder
argument_list|()
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
name|void
name|initialize
parameter_list|(
name|boolean
name|clearCache
parameter_list|)
block|{
name|matchLimit
operator|=
name|MATCH_UNTIL_FIXPOINT
expr_stmt|;
name|matchOrder
operator|=
name|HepMatchOrder
operator|.
name|ARBITRARY
expr_stmt|;
name|group
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|HepInstruction
name|instruction
range|:
name|instructions
control|)
block|{
name|instruction
operator|.
name|initialize
argument_list|(
name|clearCache
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End HepProgram.java
end_comment

end_unit

