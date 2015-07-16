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
name|type
operator|.
name|RelDataTypeField
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
name|RexInputRef
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
name|RexShuttle
import|;
end_import

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
comment|/**  * Extension to {@link SubstitutionVisitor}.  */
end_comment

begin_class
specifier|public
class|class
name|MaterializedViewSubstitutionVisitor
extends|extends
name|SubstitutionVisitor
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|UnifyRule
argument_list|>
name|EXTENDED_RULES
init|=
name|ImmutableList
operator|.
expr|<
name|UnifyRule
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|DEFAULT_RULES
argument_list|)
operator|.
name|add
argument_list|(
name|ProjectToProjectUnifyRule1
operator|.
name|INSTANCE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|public
name|MaterializedViewSubstitutionVisitor
parameter_list|(
name|RelNode
name|target_
parameter_list|,
name|RelNode
name|query_
parameter_list|)
block|{
name|super
argument_list|(
name|target_
argument_list|,
name|query_
argument_list|,
name|EXTENDED_RULES
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|go
parameter_list|(
name|RelNode
name|replacement_
parameter_list|)
block|{
return|return
name|super
operator|.
name|go
argument_list|(
name|replacement_
argument_list|)
return|;
block|}
comment|/**    * Project to Project Unify rule.    */
specifier|private
specifier|static
class|class
name|ProjectToProjectUnifyRule1
extends|extends
name|AbstractUnifyRule
block|{
specifier|public
specifier|static
specifier|final
name|ProjectToProjectUnifyRule1
name|INSTANCE
init|=
operator|new
name|ProjectToProjectUnifyRule1
argument_list|()
decl_stmt|;
specifier|private
name|ProjectToProjectUnifyRule1
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|MutableProject
operator|.
name|class
argument_list|,
name|query
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|operand
argument_list|(
name|MutableProject
operator|.
name|class
argument_list|,
name|target
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|UnifyResult
name|apply
parameter_list|(
name|UnifyRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|MutableProject
name|query
init|=
operator|(
name|MutableProject
operator|)
name|call
operator|.
name|query
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|oldFieldList
init|=
name|query
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|newFieldList
init|=
name|call
operator|.
name|target
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|newProjects
decl_stmt|;
try|try
block|{
name|newProjects
operator|=
name|transformRex
argument_list|(
name|query
operator|.
name|getProjects
argument_list|()
argument_list|,
name|oldFieldList
argument_list|,
name|newFieldList
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MatchFailed
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|MutableProject
name|newProject
init|=
name|MutableProject
operator|.
name|of
argument_list|(
name|query
operator|.
name|getRowType
argument_list|()
argument_list|,
name|call
operator|.
name|target
argument_list|,
name|newProjects
argument_list|)
decl_stmt|;
specifier|final
name|MutableRel
name|newProject2
init|=
name|MutableRels
operator|.
name|strip
argument_list|(
name|newProject
argument_list|)
decl_stmt|;
return|return
name|call
operator|.
name|result
argument_list|(
name|newProject2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|UnifyRuleCall
name|match
parameter_list|(
name|SubstitutionVisitor
name|visitor
parameter_list|,
name|MutableRel
name|query
parameter_list|,
name|MutableRel
name|target
parameter_list|)
block|{
assert|assert
name|query
operator|instanceof
name|MutableProject
operator|&&
name|target
operator|instanceof
name|MutableProject
assert|;
if|if
condition|(
name|queryOperand
operator|.
name|matches
argument_list|(
name|visitor
argument_list|,
name|query
argument_list|)
condition|)
block|{
if|if
condition|(
name|targetOperand
operator|.
name|matches
argument_list|(
name|visitor
argument_list|,
name|target
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|targetOperand
operator|.
name|isWeaker
argument_list|(
name|visitor
argument_list|,
name|target
argument_list|)
condition|)
block|{
specifier|final
name|MutableProject
name|queryProject
init|=
operator|(
name|MutableProject
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|queryProject
operator|.
name|getInput
argument_list|()
operator|instanceof
name|MutableFilter
condition|)
block|{
specifier|final
name|MutableFilter
name|innerFilter
init|=
operator|(
name|MutableFilter
operator|)
name|queryProject
operator|.
name|getInput
argument_list|()
decl_stmt|;
name|RexNode
name|newCondition
decl_stmt|;
try|try
block|{
name|newCondition
operator|=
name|transformRex
argument_list|(
name|innerFilter
operator|.
name|getCondition
argument_list|()
argument_list|,
name|innerFilter
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|target
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MatchFailed
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|MutableFilter
name|newFilter
init|=
name|MutableFilter
operator|.
name|of
argument_list|(
name|target
argument_list|,
name|newCondition
argument_list|)
decl_stmt|;
return|return
name|visitor
operator|.
expr|new
name|UnifyRuleCall
argument_list|(
name|this
argument_list|,
name|query
argument_list|,
name|newFilter
argument_list|,
name|copy
argument_list|(
name|visitor
operator|.
name|slots
argument_list|,
name|slotCount
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|RexNode
name|transformRex
parameter_list|(
name|RexNode
name|node
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|oldFields
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|newFields
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
init|=
name|transformRex
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|node
argument_list|)
argument_list|,
name|oldFields
argument_list|,
name|newFields
argument_list|)
decl_stmt|;
return|return
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|RexNode
argument_list|>
name|transformRex
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|oldFields
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|newFields
parameter_list|)
block|{
name|RexShuttle
name|shuttle
init|=
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitInputRef
parameter_list|(
name|RexInputRef
name|ref
parameter_list|)
block|{
name|RelDataTypeField
name|f
init|=
name|oldFields
operator|.
name|get
argument_list|(
name|ref
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|newFields
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|RelDataTypeField
name|newf
init|=
name|newFields
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|newf
operator|.
name|getKey
argument_list|()
argument_list|)
operator|&&
name|f
operator|.
name|getValue
argument_list|()
operator|==
name|newf
operator|.
name|getValue
argument_list|()
condition|)
block|{
return|return
operator|new
name|RexInputRef
argument_list|(
name|index
argument_list|,
name|f
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
throw|throw
name|MatchFailed
operator|.
name|INSTANCE
throw|;
block|}
block|}
decl_stmt|;
return|return
name|shuttle
operator|.
name|apply
argument_list|(
name|nodes
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MaterializedViewSubstitutionVisitor.java
end_comment

end_unit

