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
name|materialize
package|;
end_package

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
comment|/** A sequence of {@link Step}s from a root node (fact table) to another node  * (dimension table), possibly via intermediate dimension tables. */
end_comment

begin_class
class|class
name|Path
block|{
specifier|final
name|List
argument_list|<
name|Step
argument_list|>
name|steps
decl_stmt|;
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
name|Path
parameter_list|(
name|List
argument_list|<
name|Step
argument_list|>
name|steps
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|steps
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|steps
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
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
name|id
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
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|Path
operator|&&
name|id
operator|==
operator|(
operator|(
name|Path
operator|)
name|obj
operator|)
operator|.
name|id
return|;
block|}
block|}
end_class

end_unit

