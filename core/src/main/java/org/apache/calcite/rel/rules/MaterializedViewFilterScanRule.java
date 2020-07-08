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
name|RelOptMaterialization
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
name|RelOptMaterializations
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
name|RelOptPlanner
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
name|plan
operator|.
name|SubstitutionVisitor
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
name|hep
operator|.
name|HepPlanner
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
name|hep
operator|.
name|HepProgram
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
name|hep
operator|.
name|HepProgramBuilder
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
name|Filter
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
name|TableScan
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
name|tools
operator|.
name|RelBuilderFactory
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
name|Suppliers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Planner rule that converts  * a {@link org.apache.calcite.rel.core.Filter}  * on a {@link org.apache.calcite.rel.core.TableScan}  * to a {@link org.apache.calcite.rel.core.Filter} on a Materialized View.  *  * @see org.apache.calcite.rel.rules.materialize.MaterializedViewRules#FILTER_SCAN  */
end_comment

begin_class
specifier|public
class|class
name|MaterializedViewFilterScanRule
extends|extends
name|RelRule
argument_list|<
name|MaterializedViewFilterScanRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
specifier|private
specifier|static
specifier|final
name|Supplier
argument_list|<
name|HepProgram
argument_list|>
name|PROGRAM
init|=
name|Suppliers
operator|.
name|memoize
argument_list|(
parameter_list|()
lambda|->
operator|new
name|HepProgramBuilder
argument_list|()
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_PROJECT_TRANSPOSE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_MERGE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|::
name|get
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates a MaterializedViewFilterScanRule. */
specifier|protected
name|MaterializedViewFilterScanRule
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
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|MaterializedViewFilterScanRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|Filter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|apply
argument_list|(
name|call
argument_list|,
name|filter
argument_list|,
name|scan
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|apply
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|TableScan
name|scan
parameter_list|)
block|{
specifier|final
name|RelOptPlanner
name|planner
init|=
name|call
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializations
init|=
name|planner
operator|.
name|getMaterializations
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|materializations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|RelNode
name|root
init|=
name|filter
operator|.
name|copy
argument_list|(
name|filter
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|scan
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|applicableMaterializations
init|=
name|RelOptMaterializations
operator|.
name|getApplicableMaterializations
argument_list|(
name|root
argument_list|,
name|materializations
argument_list|)
decl_stmt|;
for|for
control|(
name|RelOptMaterialization
name|materialization
range|:
name|applicableMaterializations
control|)
block|{
if|if
condition|(
name|RelOptUtil
operator|.
name|areRowTypesEqual
argument_list|(
name|scan
operator|.
name|getRowType
argument_list|()
argument_list|,
name|materialization
operator|.
name|queryRel
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|RelNode
name|target
init|=
name|materialization
operator|.
name|queryRel
decl_stmt|;
specifier|final
name|HepPlanner
name|hepPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|PROGRAM
operator|.
name|get
argument_list|()
argument_list|,
name|planner
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
name|hepPlanner
operator|.
name|setRoot
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|target
operator|=
name|hepPlanner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|subs
init|=
operator|new
name|SubstitutionVisitor
argument_list|(
name|target
argument_list|,
name|root
argument_list|)
operator|.
name|go
argument_list|(
name|materialization
operator|.
name|tableRel
argument_list|)
decl_stmt|;
for|for
control|(
name|RelNode
name|s
range|:
name|subs
control|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
name|TableScan
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|MaterializedViewFilterScanRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|MaterializedViewFilterScanRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filterClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|TableScan
argument_list|>
name|scanClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|filterClass
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
name|scanClass
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
return|;
block|}
block|}
block|}
end_class

end_unit

