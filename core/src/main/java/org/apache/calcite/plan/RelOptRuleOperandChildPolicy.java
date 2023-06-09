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
name|plan
package|;
end_package

begin_comment
comment|/**  * Policy by which operands will be matched by relational expressions with  * any number of children.  */
end_comment

begin_enum
specifier|public
enum|enum
name|RelOptRuleOperandChildPolicy
block|{
comment|/**    * Signifies that operand can have any number of children.    */
name|ANY
block|,
comment|/**    * Signifies that operand has no children. Therefore it matches a    * leaf node, such as a table scan or VALUES operator.    *    *<p>{@code RelOptRuleOperand(Foo.class, NONE)} is equivalent to    * {@code RelOptRuleOperand(Foo.class)} but we prefer the former because    * it is more explicit.</p>    */
name|LEAF
block|,
comment|/**    * Signifies that the operand's children must precisely match its    * child operands, in order.    */
name|SOME
block|,
comment|/**    * Signifies that the rule matches any one of its parents' children.    * The parent may have one or more children.    */
name|UNORDERED
block|, }
end_enum

end_unit

