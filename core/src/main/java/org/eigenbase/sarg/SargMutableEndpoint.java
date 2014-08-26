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
name|sarg
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SargMutableEndpoint exposes methods for modifying a {@link SargEndpoint}.  */
end_comment

begin_class
specifier|public
class|class
name|SargMutableEndpoint
extends|extends
name|SargEndpoint
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * @see SargFactory#newEndpoint    */
name|SargMutableEndpoint
parameter_list|(
name|SargFactory
name|factory
parameter_list|,
name|RelDataType
name|dataType
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|dataType
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// publicize SargEndpoint
specifier|public
name|void
name|setInfinity
parameter_list|(
name|int
name|infinitude
parameter_list|)
block|{
name|super
operator|.
name|setInfinity
argument_list|(
name|infinitude
argument_list|)
expr_stmt|;
block|}
comment|// publicize SargEndpoint
specifier|public
name|void
name|setFinite
parameter_list|(
name|SargBoundType
name|boundType
parameter_list|,
name|SargStrictness
name|strictness
parameter_list|,
name|RexNode
name|coordinate
parameter_list|)
block|{
name|super
operator|.
name|setFinite
argument_list|(
name|boundType
argument_list|,
name|strictness
argument_list|,
name|coordinate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SargMutableEndpoint.java
end_comment

end_unit

