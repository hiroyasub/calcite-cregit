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
name|util
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
name|runtime
operator|.
name|CalciteResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|ConsList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|Resources
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
comment|/**  * Definitions of objects to be statically imported.  *  *<h2>Note to developers</h2>  *  *<p>Please give careful consideration before including an object in this  * class. Pros:  *<ul>  *<li>Code that uses these objects will be terser.  *</ul>  *  *<p>Cons:</p>  *<ul>  *<li>Namespace pollution,  *<li>code that is difficult to understand (a general problem with static  * imports),  *<li>potential cyclic initialization.  *</ul>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Static
block|{
specifier|private
name|Static
parameter_list|()
block|{
block|}
comment|/** Resources. */
specifier|public
specifier|static
specifier|final
name|CalciteResource
name|RESOURCE
init|=
name|Resources
operator|.
name|create
argument_list|(
name|CalciteResource
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Builds a list. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|cons
parameter_list|(
name|E
name|first
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|rest
parameter_list|)
block|{
return|return
name|ConsList
operator|.
name|of
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
return|;
block|}
block|}
end_class

end_unit

