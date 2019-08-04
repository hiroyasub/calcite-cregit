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
name|hint
package|;
end_package

begin_comment
comment|/**  * A collection of hint strategies.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|HintStrategies
block|{
comment|/** A hint strategy that indicates a hint can only be used to    * the whole query(no specific nodes). */
specifier|public
specifier|static
specifier|final
name|HintStrategy
name|SET_VAR
init|=
operator|new
name|NodeTypeHintStrategy
argument_list|(
name|NodeTypeHintStrategy
operator|.
name|NodeType
operator|.
name|SET_VAR
argument_list|)
decl_stmt|;
comment|/** A hint strategy that indicates a hint can only be used to    * {@link org.apache.calcite.rel.core.Join} nodes. */
specifier|public
specifier|static
specifier|final
name|HintStrategy
name|JOIN
init|=
operator|new
name|NodeTypeHintStrategy
argument_list|(
name|NodeTypeHintStrategy
operator|.
name|NodeType
operator|.
name|JOIN
argument_list|)
decl_stmt|;
comment|/** A hint strategy that indicates a hint can only be used to    * {@link org.apache.calcite.rel.core.TableScan} nodes. */
specifier|public
specifier|static
specifier|final
name|HintStrategy
name|TABLE_SCAN
init|=
operator|new
name|NodeTypeHintStrategy
argument_list|(
name|NodeTypeHintStrategy
operator|.
name|NodeType
operator|.
name|TABLE_SCAN
argument_list|)
decl_stmt|;
comment|/** A hint strategy that indicates a hint can only be used to    * {@link org.apache.calcite.rel.core.Project} nodes. */
specifier|public
specifier|static
specifier|final
name|HintStrategy
name|PROJECT
init|=
operator|new
name|NodeTypeHintStrategy
argument_list|(
name|NodeTypeHintStrategy
operator|.
name|NodeType
operator|.
name|PROJECT
argument_list|)
decl_stmt|;
comment|/**    * Create a hint strategy from a specific matcher whose rules are totally customized.    *    * @param matcher The strategy matcher    * @return A ExplicitHintStrategy instance.    */
specifier|public
specifier|static
name|HintStrategy
name|explicit
parameter_list|(
name|ExplicitHintMatcher
name|matcher
parameter_list|)
block|{
return|return
operator|new
name|ExplicitHintStrategy
argument_list|(
name|matcher
argument_list|)
return|;
block|}
comment|/**    * Creates a HintStrategyCascade instance whose strategy rules are satisfied only if    * all the {@code hintStrategies} are satisfied.    */
specifier|public
specifier|static
name|HintStrategy
name|cascade
parameter_list|(
name|HintStrategy
modifier|...
name|hintStrategies
parameter_list|)
block|{
return|return
operator|new
name|HintStrategyCascade
argument_list|(
name|hintStrategies
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End HintStrategies.java
end_comment

end_unit

