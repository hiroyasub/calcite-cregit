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
name|cassandra
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableLimit
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|RexCall
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
name|SqlKind
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Rules and relational operators for  * {@link CassandraRel#CONVENTION}  * calling convention.  */
end_comment

begin_class
specifier|public
class|class
name|CassandraRules
block|{
specifier|private
name|CassandraRules
parameter_list|()
block|{
block|}
specifier|public
specifier|static
specifier|final
name|CassandraFilterRule
name|FILTER
init|=
name|CassandraFilterRule
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
name|CassandraProjectRule
name|PROJECT
init|=
name|CassandraProjectRule
operator|.
name|DEFAULT_CONFIG
operator|.
name|toRule
argument_list|(
name|CassandraProjectRule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|CassandraSortRule
name|SORT
init|=
name|CassandraSortRule
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
name|CassandraLimitRule
name|LIMIT
init|=
name|CassandraLimitRule
operator|.
name|Config
operator|.
name|DEFAULT
operator|.
name|toRule
argument_list|()
decl_stmt|;
comment|/** Rule to convert a relational expression from    * {@link CassandraRel#CONVENTION} to {@link EnumerableConvention}. */
specifier|public
specifier|static
specifier|final
name|CassandraToEnumerableConverterRule
name|TO_ENUMERABLE
init|=
name|CassandraToEnumerableConverterRule
operator|.
name|DEFAULT_CONFIG
operator|.
name|toRule
argument_list|(
name|CassandraToEnumerableConverterRule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptRule
index|[]
name|RULES
init|=
block|{
name|FILTER
block|,
name|PROJECT
block|,
name|SORT
block|,
name|LIMIT
block|}
decl_stmt|;
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|cassandraFieldNames
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
comment|/** Translator from {@link RexNode} to strings in Cassandra's expression    * language. */
specifier|static
class|class
name|RexToCassandraTranslator
extends|extends
name|RexVisitorImpl
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inFields
decl_stmt|;
specifier|protected
name|RexToCassandraTranslator
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
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
name|typeFactory
operator|=
name|typeFactory
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
comment|/** Base class for planner rules that convert a relational expression to    * Cassandra calling convention. */
specifier|abstract
specifier|static
class|class
name|CassandraConverterRule
extends|extends
name|ConverterRule
block|{
name|CassandraConverterRule
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
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalFilter} to a    * {@link CassandraFilter}.    *    * @see #FILTER    */
specifier|public
specifier|static
class|class
name|CassandraFilterRule
extends|extends
name|RelRule
argument_list|<
name|CassandraFilterRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates a CassandraFilterRule. */
specifier|protected
name|CassandraFilterRule
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
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|LogicalFilter
argument_list|>
name|PREDICATE
init|=
comment|// TODO: Check for an equality predicate on the partition key
comment|// Right now this just checks if we have a single top-level AND
name|filter
lambda|->
name|RelOptUtil
operator|.
name|disjunctions
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|1
decl_stmt|;
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
comment|// Get the condition from the filter operation
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
name|RexNode
name|condition
init|=
name|filter
operator|.
name|getCondition
argument_list|()
decl_stmt|;
comment|// Get field names from the scan operation
name|CassandraTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Pair
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|keyFields
init|=
name|scan
operator|.
name|cassandraTable
operator|.
name|getKeyFields
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|partitionKeys
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|keyFields
operator|.
name|left
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|CassandraRules
operator|.
name|cassandraFieldNames
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|disjunctions
init|=
name|RelOptUtil
operator|.
name|disjunctions
argument_list|(
name|condition
argument_list|)
decl_stmt|;
if|if
condition|(
name|disjunctions
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// Check that all conjunctions are primary key equalities
name|condition
operator|=
name|disjunctions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|RexNode
name|predicate
range|:
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|condition
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|isEqualityOnKey
argument_list|(
name|predicate
argument_list|,
name|fieldNames
argument_list|,
name|partitionKeys
argument_list|,
name|keyFields
operator|.
name|right
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|// Either all of the partition keys must be specified or none
return|return
name|partitionKeys
operator|.
name|size
argument_list|()
operator|==
name|keyFields
operator|.
name|left
operator|.
name|size
argument_list|()
operator|||
name|partitionKeys
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
comment|/** Check if the node is a supported predicate (primary key equality).      *      * @param node Condition node to check      * @param fieldNames Names of all columns in the table      * @param partitionKeys Names of primary key columns      * @param clusteringKeys Names of primary key columns      * @return True if the node represents an equality predicate on a primary key      */
specifier|private
name|boolean
name|isEqualityOnKey
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|partitionKeys
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|clusteringKeys
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|EQUALS
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
specifier|final
name|RexNode
name|left
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|right
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|compareFieldWithLiteral
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|compareFieldWithLiteral
argument_list|(
name|right
argument_list|,
name|left
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
return|return
name|partitionKeys
operator|.
name|remove
argument_list|(
name|key
argument_list|)
operator|||
name|clusteringKeys
operator|.
name|contains
argument_list|(
name|key
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Check if an equality operation is comparing a primary key column with a literal.      *      * @param left Left operand of the equality      * @param right Right operand of the equality      * @param fieldNames Names of all columns in the table      * @return The field being compared or null if there is no key equality      */
specifier|private
name|String
name|compareFieldWithLiteral
parameter_list|(
name|RexNode
name|left
parameter_list|,
name|RexNode
name|right
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
comment|// FIXME Ignore casts for new and assume they aren't really necessary
if|if
condition|(
name|left
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|)
condition|)
block|{
name|left
operator|=
operator|(
operator|(
name|RexCall
operator|)
name|left
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|left
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|INPUT_REF
argument_list|)
operator|&&
name|right
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|)
condition|)
block|{
specifier|final
name|RexInputRef
name|left1
init|=
operator|(
name|RexInputRef
operator|)
name|left
decl_stmt|;
name|String
name|name
init|=
name|fieldNames
operator|.
name|get
argument_list|(
name|left1
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|name
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
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
name|CassandraTableScan
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
name|CassandraTableScan
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
name|CassandraRel
operator|.
name|CONVENTION
argument_list|)
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|keyFields
init|=
name|scan
operator|.
name|cassandraTable
operator|.
name|getKeyFields
argument_list|()
decl_stmt|;
return|return
operator|new
name|CassandraFilter
argument_list|(
name|filter
operator|.
name|getCluster
argument_list|()
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
name|CassandraRel
operator|.
name|CONVENTION
argument_list|)
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|,
name|keyFields
operator|.
name|left
argument_list|,
name|keyFields
operator|.
name|right
argument_list|,
name|scan
operator|.
name|cassandraTable
operator|.
name|getClusteringOrder
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
name|CassandraTableScan
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
name|CassandraFilterRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|CassandraFilterRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalProject}    * to a {@link CassandraProject}.    *    * @see #PROJECT    */
specifier|public
specifier|static
class|class
name|CassandraProjectRule
extends|extends
name|CassandraConverterRule
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
name|CassandraRel
operator|.
name|CONVENTION
argument_list|,
literal|"CassandraProjectRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|CassandraProjectRule
operator|::
operator|new
argument_list|)
decl_stmt|;
specifier|protected
name|CassandraProjectRule
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
name|CassandraProject
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
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.core.Sort} to a    * {@link CassandraSort}.    *    * @see #SORT    */
specifier|public
specifier|static
class|class
name|CassandraSortRule
extends|extends
name|RelRule
argument_list|<
name|CassandraSortRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates a CassandraSortRule. */
specifier|protected
name|CassandraSortRule
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
specifier|public
name|RelNode
name|convert
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|CassandraFilter
name|filter
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
name|CassandraRel
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
name|CassandraSort
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
name|CassandraFilter
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
comment|/** Check if it is possible to exploit native CQL sorting for a given collation.      *      * @return True if it is possible to achieve this sort in Cassandra      */
specifier|private
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
comment|// Check if we need to reverse the order of the implicit collation
name|boolean
name|reversed
init|=
name|reverseDirection
argument_list|(
name|sortFieldCollations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDirection
argument_list|()
argument_list|)
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
comment|// Check that the fields being sorted match
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
comment|// Either all fields must be sorted in the same direction
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
name|reverseDirection
argument_list|(
name|sortDirection
argument_list|)
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
comment|/** Find the reverse of a given collation direction.      *      * @return Reverse of the input direction      */
specifier|private
name|RelFieldCollation
operator|.
name|Direction
name|reverseDirection
parameter_list|(
name|RelFieldCollation
operator|.
name|Direction
name|direction
parameter_list|)
block|{
switch|switch
condition|(
name|direction
condition|)
block|{
case|case
name|ASCENDING
case|:
case|case
name|STRICTLY_ASCENDING
case|:
return|return
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
return|;
case|case
name|DESCENDING
case|:
case|case
name|STRICTLY_DESCENDING
case|:
return|return
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
return|;
default|default:
return|return
literal|null
return|;
block|}
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
name|CassandraFilter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|sort
argument_list|,
name|filter
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
comment|// Limits are handled by CassandraLimit
operator|.
name|predicate
argument_list|(
name|sort
lambda|->
name|sort
operator|.
name|offset
operator|==
literal|null
operator|&&
name|sort
operator|.
name|fetch
operator|==
literal|null
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
name|CassandraToEnumerableConverter
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
name|CassandraFilter
operator|.
name|class
argument_list|)
comment|// We can only use implicit sorting within a
comment|// single partition
operator|.
name|predicate
argument_list|(
name|CassandraFilter
operator|::
name|isSinglePartition
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
name|CassandraSortRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|CassandraSortRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Rule to convert a    * {@link org.apache.calcite.adapter.enumerable.EnumerableLimit} to a    * {@link CassandraLimit}.    *    * @see #LIMIT    */
specifier|public
specifier|static
class|class
name|CassandraLimitRule
extends|extends
name|RelRule
argument_list|<
name|CassandraLimitRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates a CassandraLimitRule. */
specifier|protected
name|CassandraLimitRule
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
specifier|public
name|RelNode
name|convert
parameter_list|(
name|EnumerableLimit
name|limit
parameter_list|)
block|{
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|limit
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|CassandraRel
operator|.
name|CONVENTION
argument_list|)
decl_stmt|;
return|return
operator|new
name|CassandraLimit
argument_list|(
name|limit
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|limit
operator|.
name|getInput
argument_list|()
argument_list|,
name|CassandraRel
operator|.
name|CONVENTION
argument_list|)
argument_list|,
name|limit
operator|.
name|offset
argument_list|,
name|limit
operator|.
name|fetch
argument_list|)
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
name|EnumerableLimit
name|limit
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
name|limit
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
name|EnumerableLimit
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
name|CassandraToEnumerableConverter
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
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
name|CassandraLimitRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|CassandraLimitRule
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

