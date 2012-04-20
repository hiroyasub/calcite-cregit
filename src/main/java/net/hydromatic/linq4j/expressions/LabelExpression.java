begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
package|;
end_package

begin_comment
comment|/**  * Represents a label, which can be put in any {@link Expression} context. If it  * is jumped to, it will get the value provided by the corresponding  * {@link GotoExpression}. Otherwise, it receives the value in  * {@link #defaultValue}. If the Type equals {@link Void}, no value should be  * provided.  */
end_comment

begin_class
specifier|public
class|class
name|LabelExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Expression
name|defaultValue
decl_stmt|;
specifier|public
name|LabelExpression
parameter_list|(
name|Expression
name|defaultValue
parameter_list|,
name|ExpressionType
name|nodeType
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
block|}
block|}
end_class

end_unit

