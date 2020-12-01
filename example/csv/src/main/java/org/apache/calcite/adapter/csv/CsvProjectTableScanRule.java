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
name|adapter
operator|.
name|csv
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
name|RelRule
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
name|logical
operator|.
name|LogicalProject
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Planner rule that projects from a {@link CsvTableScan} scan just the columns  * needed to satisfy a projection. If the projection's expressions are trivial,  * the projection is removed.  *  * @see CsvRules#PROJECT_SCAN  */
end_comment

begin_class
specifier|public
class|class
name|CsvProjectTableScanRule
extends|extends
name|RelRule
argument_list|<
name|CsvProjectTableScanRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates a CsvProjectTableScanRule. */
specifier|protected
name|CsvProjectTableScanRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
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
specifier|final
name|LogicalProject
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|CsvTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
index|[]
name|fields
init|=
name|getProjectFields
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|// Project contains expressions more complex than just field references.
return|return;
block|}
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|CsvTableScan
argument_list|(
name|scan
operator|.
name|getCluster
argument_list|()
argument_list|,
name|scan
operator|.
name|getTable
argument_list|()
argument_list|,
name|scan
operator|.
name|csvTable
argument_list|,
name|fields
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|int
index|[]
name|getProjectFields
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|fields
init|=
operator|new
name|int
index|[
name|exps
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
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
specifier|final
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
name|exp
operator|instanceof
name|RexInputRef
condition|)
block|{
name|fields
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|RexInputRef
operator|)
name|exp
operator|)
operator|.
name|getIndex
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
comment|// not a simple projection
block|}
block|}
return|return
name|fields
return|;
block|}
comment|/** Rule configuration. */
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|EMPTY
operator|.
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|CsvTableScan
operator|.
name|class
argument_list|)
operator|.
name|noInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|CsvProjectTableScanRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|CsvProjectTableScanRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

