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
name|Project
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
name|RelDataType
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
comment|/**  * Planner rule that,  * given a {@link org.apache.calcite.rel.core.Project} node that  * merely returns its input, converts the node into its child.  *  *<p>For example,<code>Project(ArrayReader(a), {$input0})</code> becomes  *<code>ArrayReader(a)</code>.</p>  *  * @see CalcRemoveRule  * @see ProjectMergeRule  */
end_comment

begin_class
specifier|public
class|class
name|ProjectRemoveRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|Project
argument_list|>
name|PREDICATE
init|=
operator|new
name|Predicate
argument_list|<
name|Project
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Project
name|input
parameter_list|)
block|{
return|return
name|isTrivial
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ProjectRemoveRule
name|INSTANCE
init|=
operator|new
name|ProjectRemoveRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|ProjectRemoveRule
parameter_list|()
block|{
comment|// Create a specialized operand to detect non-matches early. This keeps
comment|// the rule queue short.
name|super
argument_list|(
name|operand
argument_list|(
name|Project
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|PREDICATE
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
assert|assert
name|isTrivial
argument_list|(
name|project
argument_list|)
assert|;
name|RelNode
name|stripped
init|=
name|project
operator|.
name|getInput
argument_list|()
decl_stmt|;
name|RelNode
name|child
init|=
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|register
argument_list|(
name|stripped
argument_list|,
name|project
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the child of a project if the project is trivial, otherwise    * the project itself.    */
specifier|public
specifier|static
name|RelNode
name|strip
parameter_list|(
name|Project
name|project
parameter_list|)
block|{
return|return
name|isTrivial
argument_list|(
name|project
argument_list|)
condition|?
name|project
operator|.
name|getInput
argument_list|()
else|:
name|project
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isTrivial
parameter_list|(
name|Project
name|project
parameter_list|)
block|{
name|RelNode
name|child
init|=
name|project
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|childRowType
init|=
name|child
operator|.
name|getRowType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|childRowType
operator|.
name|isStruct
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|project
operator|.
name|isBoxed
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|isIdentity
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|,
name|childRowType
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isIdentity
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|RelDataType
name|childRowType
parameter_list|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|childFields
init|=
name|childRowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|int
name|fieldCount
init|=
name|childFields
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|exps
operator|.
name|size
argument_list|()
operator|!=
name|fieldCount
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exps
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|exp
init|=
name|exps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|exp
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RexInputRef
name|var
init|=
operator|(
name|RexInputRef
operator|)
name|exp
decl_stmt|;
if|if
condition|(
name|var
operator|.
name|getIndex
argument_list|()
operator|!=
name|i
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|childFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End ProjectRemoveRule.java
end_comment

end_unit

