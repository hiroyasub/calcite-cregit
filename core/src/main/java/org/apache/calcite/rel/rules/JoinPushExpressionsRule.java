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
name|rules
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
name|plan
operator|.
name|RelOptRule
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
name|plan
operator|.
name|RelOptRuleCall
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
name|plan
operator|.
name|RelOptUtil
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
name|rel
operator|.
name|RelNode
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
name|rel
operator|.
name|core
operator|.
name|Join
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
name|rel
operator|.
name|core
operator|.
name|RelFactories
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
name|rex
operator|.
name|RexNode
import|;
end_import

begin_comment
comment|/**  * Planner rule that pushes down expressions in "equal" join condition.  *  *<p>For example, given  * "emp JOIN dept ON emp.deptno + 1 = dept.deptno", adds a project above  * "emp" that computes the expression  * "emp.deptno + 1". The resulting join condition is a simple combination  * of AND, equals, and input fields, plus the remaining non-equal conditions.  */
end_comment

begin_class
specifier|public
class|class
name|JoinPushExpressionsRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|JoinPushExpressionsRule
name|INSTANCE
init|=
operator|new
name|JoinPushExpressionsRule
argument_list|(
name|Join
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|DEFAULT_PROJECT_FACTORY
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
decl_stmt|;
comment|/** Creates a JoinPushExpressionsRule. */
specifier|public
name|JoinPushExpressionsRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|clazz
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|projectFactory
operator|=
name|projectFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Push expression in join condition into Project below Join.
name|RelNode
name|newJoin
init|=
name|RelOptUtil
operator|.
name|pushDownJoinConditions
argument_list|(
name|join
argument_list|,
name|projectFactory
argument_list|)
decl_stmt|;
comment|// If the join is the same, we bail out
if|if
condition|(
name|newJoin
operator|instanceof
name|Join
condition|)
block|{
specifier|final
name|RexNode
name|newCondition
init|=
operator|(
operator|(
name|Join
operator|)
name|newJoin
operator|)
operator|.
name|getCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|join
operator|.
name|getCondition
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|newCondition
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|newJoin
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JoinPushExpressionsRule.java
end_comment

end_unit

