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
name|sql
operator|.
name|validate
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
comment|/** Thrown when an object, such as a view, is found to have a cylic  * definition. */
end_comment

begin_class
specifier|public
class|class
name|CyclicDefinitionException
extends|extends
name|RuntimeException
block|{
specifier|public
specifier|final
name|int
name|depth
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
decl_stmt|;
comment|/** Creates CyclicDefinitionException. */
specifier|public
name|CyclicDefinitionException
parameter_list|(
name|int
name|depth
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
name|super
argument_list|(
literal|"Cyclic object definition: "
operator|+
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

