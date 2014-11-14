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
name|rex
package|;
end_package

begin_comment
comment|/**  * A<code>RexPattern</code> represents an expression with holes in it. The  * {@link #match} method tests whether a given expression matches the pattern.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RexPattern
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Calls<code>action</code> for every combination of tokens for which this    * pattern matches.    */
name|void
name|match
parameter_list|(
name|RexNode
name|ptree
parameter_list|,
name|RexAction
name|action
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RexPattern.java
end_comment

end_unit

