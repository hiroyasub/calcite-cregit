begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Visitor which replaces {@link RexLocalRef} objects after the expressions in a  * {@link RexProgram} have been reordered.  *  * @author jhyde  * @version $Id$  * @see RexPermuteInputsShuttle  */
end_comment

begin_class
specifier|public
class|class
name|RexPermutationShuttle
extends|extends
name|RexShuttle
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Permutation
name|permutation
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RexPermutationShuttle
parameter_list|(
name|Permutation
name|permutation
parameter_list|)
block|{
name|this
operator|.
name|permutation
operator|=
name|permutation
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RexNode
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|local
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|local
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|int
name|target
init|=
name|permutation
operator|.
name|getTarget
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexLocalRef
argument_list|(
name|target
argument_list|,
name|local
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexPermutationShuttle.java
end_comment

end_unit

