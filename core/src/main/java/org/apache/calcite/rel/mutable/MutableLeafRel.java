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
name|mutable
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
name|List
import|;
end_import

begin_comment
comment|/** Abstract base class for implementations of {@link MutableRel} that have  * no inputs. */
end_comment

begin_class
specifier|abstract
class|class
name|MutableLeafRel
extends|extends
name|MutableRel
block|{
specifier|protected
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|protected
name|MutableLeafRel
parameter_list|(
name|MutableRelType
name|type
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
block|}
specifier|public
name|void
name|setInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|MutableRel
name|input
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
specifier|public
name|List
argument_list|<
name|MutableRel
argument_list|>
name|getInputs
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
name|void
name|childrenAccept
parameter_list|(
name|MutableRelVisitor
name|visitor
parameter_list|)
block|{
comment|// no children - nothing to do
block|}
block|}
end_class

begin_comment
comment|// End MutableLeafRel.java
end_comment

end_unit

