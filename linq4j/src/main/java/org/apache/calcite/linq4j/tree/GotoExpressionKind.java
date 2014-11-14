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
name|linq4j
operator|.
name|tree
package|;
end_package

begin_comment
comment|/**  * Specifies what kind of jump a {@link GotoStatement} represents.  */
end_comment

begin_enum
specifier|public
enum|enum
name|GotoExpressionKind
block|{
comment|/**    * A GotoExpression that represents a jump to some location.    */
name|Goto
argument_list|(
literal|"goto "
argument_list|)
block|,
comment|/**    * A GotoExpression that represents a return statement.    */
name|Return
argument_list|(
literal|"return"
argument_list|)
block|,
comment|/**    * A GotoExpression that represents a break statement.    */
name|Break
argument_list|(
literal|"break"
argument_list|)
block|,
comment|/**    * A GotoExpression that represents a continue statement.    */
name|Continue
argument_list|(
literal|"continue"
argument_list|)
block|,
comment|/**    * A GotoExpression that evaluates an expression and carries on.    */
name|Sequence
argument_list|(
literal|""
argument_list|)
block|;
specifier|final
name|String
name|prefix
decl_stmt|;
name|GotoExpressionKind
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
block|}
end_enum

begin_comment
comment|// End GotoExpressionKind.java
end_comment

end_unit

