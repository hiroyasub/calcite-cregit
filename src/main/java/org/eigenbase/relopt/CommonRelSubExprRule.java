begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_comment
comment|/**  * A<code>CommonRelSubExprRule</code> is an abstract base class for rules  * that are fired only on relational expressions that appear more than once  * in a query tree.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CommonRelSubExprRule
extends|extends
name|RelOptRule
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a<code>CommonRelSubExprRule</code>.      *      * @param operand root operand, must not be null      *      * @pre operand != null      */
specifier|public
name|CommonRelSubExprRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
block|}
end_class

begin_comment
comment|// End CommonRelSubExprRule.java
end_comment

end_unit

