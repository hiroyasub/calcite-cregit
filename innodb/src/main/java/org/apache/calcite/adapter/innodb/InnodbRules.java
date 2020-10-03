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
name|innodb
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableConvention
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
name|Convention
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
name|RelOptCluster
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
name|RelTraitSet
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
name|RelCollation
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
name|RelCollations
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
name|RelFieldCollation
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
name|convert
operator|.
name|ConverterRule
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
name|Sort
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
name|LogicalFilter
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
name|RexUtil
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
name|RexVisitorImpl
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|schema
operator|.
name|TableDef
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
comment|/**  * Rules and relational operators for {@link InnodbRel#CONVENTION}  * calling convention.  */
end_comment

begin_class
specifier|public
class|class
name|InnodbRules
block|{
specifier|private
name|InnodbRules
parameter_list|()
block|{
block|}
comment|/** Rule to convert a relational expression from    * {@link InnodbRel#CONVENTION} to {@link EnumerableConvention}. */
specifier|public
specifier|static
specifier|final
name|InnodbToEnumerableConverterRule
name|TO_ENUMERABLE
init|=
name|InnodbToEnumerableConverterRule
operator|.
name|DEFAULT_CONFIG
operator|.
name|toRule
argument_list|(
name|InnodbToEnumerableConverterRule
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Rule to convert a {@link org.apache.calcite.rel.logical.LogicalProject}    * to a {@link InnodbProject}. */
specifier|public
specifier|static
specifier|final
name|InnodbProjectRule
name|PROJECT
init|=
name|InnodbProjectRule
operator|.
name|DEFAULT_CONFIG
operator|.
name|toRule
argument_list|(
name|InnodbProjectRule
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Rule to convert a {@link org.apache.calcite.rel.logical.LogicalFilter} to    * a {@link InnodbFilter}. */
specifier|public
specifier|static
specifier|final
name|InnodbFilterRule
name|FILTER
init|=
name|InnodbFilterRule
operator|.
name|Config
operator|.
name|DEFAULT
operator|.
name|toRule
argument_list|()
decl_stmt|;
comment|/** Rule to convert a {@link org.apache.calcite.rel.core.Sort} with a    * {@link org.apache.calcite.rel.core.Filter} to a    * {@link InnodbSort}. */
specifier|public
specifier|static
specifier|final
name|InnodbSortFilterRule
name|SORT_FILTER
init|=
name|InnodbSortFilterRule
operator|.
name|Config
operator|.
name|DEFAULT
operator|.
name|toRule
argument_list|()
decl_stmt|;
comment|/** Rule to convert a {@link org.apache.calcite.rel.core.Sort} to a    * {@link InnodbSort} based on InnoDB table clustering index. */
specifier|public
specifier|static
specifier|final
name|InnodbSortTableScanRule
name|SORT_SCAN
init|=
name|InnodbSortTableScanRule
operator|.
name|Config
operator|.
name|DEFAULT
operator|.
name|toRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|PROJECT
argument_list|,
name|FILTER
argument_list|,
name|SORT_FILTER
argument_list|,
name|SORT_SCAN
argument_list|)
decl_stmt|;
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|innodbFieldNames
parameter_list|(
specifier|final
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|rowType
operator|.
name|getFieldNames
argument_list|()
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Translator from {@link RexNode} to strings in InnoDB's expression    * language. */
specifier|static
class|class
name|RexToInnodbTranslator
extends|extends
name|RexVisitorImpl
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inFields
decl_stmt|;
specifier|protected
name|RexToInnodbTranslator
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|inFields
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|inFields
operator|=
name|inFields
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
return|return
name|inFields
operator|.
name|get
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Base class for planner rules that convert a relational expression to    * Innodb calling convention.    */
specifier|abstract
specifier|static
class|class
name|InnodbConverterRule
extends|extends
name|ConverterRule
block|{
name|InnodbConverterRule
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
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalProject}    * to a {@link InnodbProject}.    *    * @see #PROJECT    */
specifier|public
specifier|static
class|class
name|InnodbProjectRule
extends|extends
name|InnodbConverterRule
block|{
comment|/** Default configuration. */
specifier|private
specifier|static
specifier|final
name|Config
name|DEFAULT_CONFIG
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|InnodbRel
operator|.
name|CONVENTION
argument_list|,
literal|"InnodbProjectRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|InnodbProjectRule
operator|::
operator|new
argument_list|)
decl_stmt|;
specifier|protected
name|InnodbProjectRule
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
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
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
for|for
control|(
name|RexNode
name|e
range|:
name|project
operator|.
name|getProjects
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|RexInputRef
operator|)
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
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|LogicalProject
name|project
init|=
operator|(
name|LogicalProject
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|project
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|InnodbProject
argument_list|(
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|out
argument_list|)
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalFilter} to a    * {@link InnodbFilter}.    *    * @see #FILTER    */
specifier|public
specifier|static
class|class
name|InnodbFilterRule
extends|extends
name|RelRule
argument_list|<
name|InnodbFilterRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates a InnodbFilterRule. */
specifier|protected
name|InnodbFilterRule
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
name|LogicalFilter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|InnodbTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
condition|)
block|{
specifier|final
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|filter
argument_list|,
name|scan
argument_list|)
decl_stmt|;
if|if
condition|(
name|converted
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|converted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|RelNode
name|convert
parameter_list|(
name|LogicalFilter
name|filter
parameter_list|,
name|InnodbTableScan
name|scan
parameter_list|)
block|{
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|InnodbRel
operator|.
name|CONVENTION
argument_list|)
decl_stmt|;
specifier|final
name|TableDef
name|tableDef
init|=
name|scan
operator|.
name|innodbTable
operator|.
name|getTableDef
argument_list|()
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|filter
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|InnodbFilterTranslator
name|translator
init|=
operator|new
name|InnodbFilterTranslator
argument_list|(
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|filter
operator|.
name|getRowType
argument_list|()
argument_list|,
name|tableDef
argument_list|,
name|scan
operator|.
name|getForceIndexName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|IndexCondition
name|indexCondition
init|=
name|translator
operator|.
name|translateMatch
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
name|InnodbFilter
name|innodbFilter
init|=
name|InnodbFilter
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|,
name|InnodbRel
operator|.
name|CONVENTION
argument_list|)
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|,
name|indexCondition
argument_list|,
name|tableDef
argument_list|,
name|scan
operator|.
name|getForceIndexName
argument_list|()
argument_list|)
decl_stmt|;
comment|// if some conditions can be pushed down, we left the remainder conditions
comment|// in the original filter and create a subsidiary filter
if|if
condition|(
name|innodbFilter
operator|.
name|indexCondition
operator|.
name|canPushDown
argument_list|()
condition|)
block|{
return|return
name|LogicalFilter
operator|.
name|create
argument_list|(
name|innodbFilter
argument_list|,
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|indexCondition
operator|.
name|getRemainderConditions
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|filter
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
name|LogicalFilter
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
name|InnodbTableScan
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
name|InnodbFilterRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|InnodbFilterRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.core.Sort} to a    * {@link InnodbSort}.    *    * @param<C> The rule configuration type.    */
specifier|private
specifier|static
class|class
name|AbstractInnodbSortRule
parameter_list|<
name|C
extends|extends
name|RelRule
operator|.
name|Config
parameter_list|>
extends|extends
name|RelRule
argument_list|<
name|C
argument_list|>
block|{
name|AbstractInnodbSortRule
parameter_list|(
name|C
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
name|RelNode
name|convert
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|sort
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|InnodbRel
operator|.
name|CONVENTION
argument_list|)
operator|.
name|replace
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|InnodbSort
argument_list|(
name|sort
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|,
name|traitSet
operator|.
name|replace
argument_list|(
name|RelCollations
operator|.
name|EMPTY
argument_list|)
argument_list|)
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Check if it is possible to exploit sorting for a given collation.      *      * @return true if it is possible to achieve this sort in Innodb data source      */
specifier|protected
name|boolean
name|collationsCompatible
parameter_list|(
name|RelCollation
name|sortCollation
parameter_list|,
name|RelCollation
name|implicitCollation
parameter_list|)
block|{
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|sortFieldCollations
init|=
name|sortCollation
operator|.
name|getFieldCollations
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|implicitFieldCollations
init|=
name|implicitCollation
operator|.
name|getFieldCollations
argument_list|()
decl_stmt|;
if|if
condition|(
name|sortFieldCollations
operator|.
name|size
argument_list|()
operator|>
name|implicitFieldCollations
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|sortFieldCollations
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// check if we need to reverse the order of the implicit collation
name|boolean
name|reversed
init|=
name|sortFieldCollations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDirection
argument_list|()
operator|.
name|reverse
argument_list|()
operator|.
name|lax
argument_list|()
operator|==
name|implicitFieldCollations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDirection
argument_list|()
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
name|sortFieldCollations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelFieldCollation
name|sorted
init|=
name|sortFieldCollations
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RelFieldCollation
name|implied
init|=
name|implicitFieldCollations
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// check that the fields being sorted match
if|if
condition|(
name|sorted
operator|.
name|getFieldIndex
argument_list|()
operator|!=
name|implied
operator|.
name|getFieldIndex
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// either all fields must be sorted in the same direction
comment|// or the opposite direction based on whether we decided
comment|// if the sort direction should be reversed above
name|RelFieldCollation
operator|.
name|Direction
name|sortDirection
init|=
name|sorted
operator|.
name|getDirection
argument_list|()
decl_stmt|;
name|RelFieldCollation
operator|.
name|Direction
name|implicitDirection
init|=
name|implied
operator|.
name|getDirection
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|reversed
operator|&&
name|sortDirection
operator|!=
name|implicitDirection
operator|)
operator|||
operator|(
name|reversed
operator|&&
name|sortDirection
operator|.
name|reverse
argument_list|()
operator|.
name|lax
argument_list|()
operator|!=
name|implicitDirection
operator|)
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
name|Sort
name|sort
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|sort
argument_list|)
decl_stmt|;
if|if
condition|(
name|converted
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|converted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.core.Sort} to a    * {@link InnodbSort}.    *    * @see #SORT_FILTER    */
specifier|public
specifier|static
class|class
name|InnodbSortFilterRule
extends|extends
name|AbstractInnodbSortRule
argument_list|<
name|InnodbSortFilterRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates a InnodbSortFilterRule. */
specifier|protected
name|InnodbSortFilterRule
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
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|InnodbFilter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
name|collationsCompatible
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|filter
operator|.
name|getImplicitCollation
argument_list|()
argument_list|)
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
name|Sort
operator|.
name|class
argument_list|)
operator|.
name|predicate
argument_list|(
name|sort
lambda|->
literal|true
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
name|InnodbToEnumerableConverter
operator|.
name|class
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|InnodbFilter
operator|.
name|class
argument_list|)
operator|.
name|predicate
argument_list|(
name|innodbFilter
lambda|->
literal|true
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
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
name|InnodbSortFilterRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|InnodbSortFilterRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.core.Sort} to a    * {@link InnodbSort} based on InnoDB table clustering index.    *    * @see #SORT_SCAN    */
specifier|public
specifier|static
class|class
name|InnodbSortTableScanRule
extends|extends
name|AbstractInnodbSortRule
argument_list|<
name|InnodbSortTableScanRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates a InnodbSortTableScanRule. */
specifier|protected
name|InnodbSortTableScanRule
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
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|InnodbTableScan
name|tableScan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
name|collationsCompatible
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|tableScan
operator|.
name|getImplicitCollation
argument_list|()
argument_list|)
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
name|InnodbSortTableScanRule
operator|.
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
name|Sort
operator|.
name|class
argument_list|)
operator|.
name|predicate
argument_list|(
name|sort
lambda|->
literal|true
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
name|InnodbToEnumerableConverter
operator|.
name|class
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|InnodbTableScan
operator|.
name|class
argument_list|)
operator|.
name|predicate
argument_list|(
name|tableScan
lambda|->
literal|true
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|InnodbSortTableScanRule
operator|.
name|Config
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|InnodbSortTableScanRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|InnodbSortTableScanRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit
