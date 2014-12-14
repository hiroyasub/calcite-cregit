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
name|RexUtil
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
specifier|final
name|boolean
name|useNamesInIdentityProjCalc
decl_stmt|;
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
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|Project
argument_list|>
name|NAME_CALC_PREDICATE
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
argument_list|,
literal|true
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
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 1.1
specifier|public
specifier|static
specifier|final
name|ProjectRemoveRule
name|NAME_CALC_INSTANCE
init|=
operator|new
name|ProjectRemoveRule
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a ProjectRemoveRule.    *    * @param useNamesInIdentityProjCalc If true consider names while determining    *                                   if two projects are same    */
specifier|private
name|ProjectRemoveRule
parameter_list|(
name|boolean
name|useNamesInIdentityProjCalc
parameter_list|)
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
name|useNamesInIdentityProjCalc
condition|?
name|NAME_CALC_PREDICATE
else|:
name|PREDICATE
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|useNamesInIdentityProjCalc
operator|=
name|useNamesInIdentityProjCalc
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
argument_list|,
name|useNamesInIdentityProjCalc
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
if|if
condition|(
name|stripped
operator|instanceof
name|Project
condition|)
block|{
comment|// Rename columns of child projection if desired field names are given.
name|Project
name|childProject
init|=
operator|(
name|Project
operator|)
name|stripped
decl_stmt|;
name|stripped
operator|=
name|childProject
operator|.
name|copy
argument_list|(
name|childProject
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|childProject
operator|.
name|getInput
argument_list|()
argument_list|,
name|childProject
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Returns the child of a project if the project is trivial    * otherwise the project itself. If useNamesInIdentityProjCalc is true    * then trivial comparison uses both names and types. */
annotation|@
name|Deprecated
comment|// to be removed before 1.1
specifier|public
specifier|static
name|RelNode
name|strip
parameter_list|(
name|Project
name|project
parameter_list|,
name|boolean
name|useNamesInIdentityProjCalc
parameter_list|)
block|{
return|return
name|isTrivial
argument_list|(
name|project
argument_list|,
name|useNamesInIdentityProjCalc
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
return|return
name|isTrivial
argument_list|(
name|project
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 1.1
specifier|public
specifier|static
name|boolean
name|isTrivial
parameter_list|(
name|Project
name|project
parameter_list|,
name|boolean
name|useNamesInIdentityProjCalc
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
name|useNamesInIdentityProjCalc
condition|)
block|{
return|return
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
return|;
block|}
else|else
block|{
return|return
name|isIdentity
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|childRowType
argument_list|)
return|;
block|}
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
name|childRowType
parameter_list|)
block|{
return|return
name|childRowType
operator|.
name|getFieldCount
argument_list|()
operator|==
name|exps
operator|.
name|size
argument_list|()
operator|&&
name|RexUtil
operator|.
name|containIdentity
argument_list|(
name|exps
argument_list|,
name|childRowType
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 1.1
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
return|return
name|childRowType
operator|.
name|getFieldCount
argument_list|()
operator|==
name|exps
operator|.
name|size
argument_list|()
operator|&&
name|RexUtil
operator|.
name|containIdentity
argument_list|(
name|exps
argument_list|,
name|rowType
argument_list|,
name|childRowType
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ProjectRemoveRule.java
end_comment

end_unit

