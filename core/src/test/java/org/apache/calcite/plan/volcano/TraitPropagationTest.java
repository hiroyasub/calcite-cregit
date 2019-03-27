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
operator|.
name|volcano
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
name|EnumerableTableScan
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
name|config
operator|.
name|CalciteSystemProperty
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
name|jdbc
operator|.
name|CalcitePrepare
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
name|ConventionTraitDef
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
name|RelOptAbstractTable
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
name|RelOptCost
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
name|RelOptRuleOperand
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
name|RelOptSchema
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
name|RelTrait
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
name|plan
operator|.
name|volcano
operator|.
name|AbstractConverter
operator|.
name|ExpandConversionRule
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
name|prepare
operator|.
name|CalciteCatalogReader
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
name|AbstractRelNode
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
name|RelCollationTraitDef
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
name|Aggregate
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
name|AggregateCall
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
name|LogicalAggregate
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
name|metadata
operator|.
name|RelMdCollation
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|rules
operator|.
name|SortRemoveRule
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
name|RelDataTypeFactory
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
name|RexBuilder
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
name|schema
operator|.
name|SchemaPlus
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
name|schema
operator|.
name|Statistic
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
name|schema
operator|.
name|Statistics
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|AbstractTable
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
name|server
operator|.
name|CalciteServerStatement
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
name|SqlExplainFormat
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
name|SqlExplainLevel
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|type
operator|.
name|SqlTypeName
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
name|FrameworkConfig
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
name|Frameworks
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
name|RuleSet
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
name|RuleSets
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
name|ImmutableBitSet
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
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
name|Properties
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests that determine whether trait propagation work in Volcano Planner.  */
end_comment

begin_class
specifier|public
class|class
name|TraitPropagationTest
block|{
specifier|static
specifier|final
name|Convention
name|PHYSICAL
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"PHYSICAL"
argument_list|,
name|Phys
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|RelCollation
name|COLLATION
init|=
name|RelCollations
operator|.
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
literal|0
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|,
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|FIRST
argument_list|)
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|RuleSet
name|RULES
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|PhysAggRule
operator|.
name|INSTANCE
argument_list|,
name|PhysProjRule
operator|.
name|INSTANCE
argument_list|,
name|PhysTableRule
operator|.
name|INSTANCE
argument_list|,
name|PhysSortRule
operator|.
name|INSTANCE
argument_list|,
name|SortRemoveRule
operator|.
name|INSTANCE
argument_list|,
name|ExpandConversionRule
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testOne
parameter_list|()
throws|throws
name|Exception
block|{
name|RelNode
name|planned
init|=
name|run
argument_list|(
operator|new
name|PropAction
argument_list|()
argument_list|,
name|RULES
argument_list|)
decl_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|"LOGICAL PLAN"
argument_list|,
name|planned
argument_list|,
name|SqlExplainFormat
operator|.
name|TEXT
argument_list|,
name|SqlExplainLevel
operator|.
name|ALL_ATTRIBUTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Sortedness was not propagated"
argument_list|,
literal|3
argument_list|,
name|mq
operator|.
name|getCumulativeCost
argument_list|(
name|planned
argument_list|)
operator|.
name|getRows
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Materialized anonymous class for simplicity    */
specifier|private
specifier|static
class|class
name|PropAction
block|{
specifier|public
name|RelNode
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelOptPlanner
name|planner
init|=
name|cluster
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|stringType
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|integerType
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlBigInt
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
decl_stmt|;
comment|// SELECT * from T;
specifier|final
name|Table
name|table
init|=
operator|new
name|AbstractTable
argument_list|()
block|{
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"s"
argument_list|,
name|stringType
argument_list|)
operator|.
name|add
argument_list|(
literal|"i"
argument_list|,
name|integerType
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|of
argument_list|(
literal|100d
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|COLLATION
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|RelOptAbstractTable
name|t1
init|=
operator|new
name|RelOptAbstractTable
argument_list|(
name|relOptSchema
argument_list|,
literal|"t1"
argument_list|,
name|table
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|isInstance
argument_list|(
name|table
argument_list|)
condition|?
name|clazz
operator|.
name|cast
argument_list|(
name|table
argument_list|)
else|:
name|super
operator|.
name|unwrap
argument_list|(
name|clazz
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|RelNode
name|rt1
init|=
name|EnumerableTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|t1
argument_list|)
decl_stmt|;
comment|// project s column
name|RelNode
name|project
init|=
name|LogicalProject
operator|.
name|create
argument_list|(
name|rt1
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|(
name|RexNode
operator|)
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|stringType
argument_list|,
literal|0
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|integerType
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"s"
argument_list|,
name|stringType
argument_list|)
operator|.
name|add
argument_list|(
literal|"i"
argument_list|,
name|integerType
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|// aggregate on s, count
name|AggregateCall
name|aggCall
init|=
name|AggregateCall
operator|.
name|create
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|RelCollations
operator|.
name|EMPTY
argument_list|,
name|sqlBigInt
argument_list|,
literal|"cnt"
argument_list|)
decl_stmt|;
name|RelNode
name|agg
init|=
operator|new
name|LogicalAggregate
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|project
argument_list|,
literal|false
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|aggCall
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rootRel
init|=
name|agg
decl_stmt|;
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|"LOGICAL PLAN"
argument_list|,
name|rootRel
argument_list|,
name|SqlExplainFormat
operator|.
name|TEXT
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
expr_stmt|;
name|RelTraitSet
name|desiredTraits
init|=
name|rootRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|PHYSICAL
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rootRel2
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|rootRel
argument_list|,
name|desiredTraits
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rootRel2
argument_list|)
expr_stmt|;
return|return
name|planner
operator|.
name|findBestExp
argument_list|()
return|;
block|}
block|}
comment|// RULES
comment|/** Rule for PhysAgg */
specifier|private
specifier|static
class|class
name|PhysAggRule
extends|extends
name|RelOptRule
block|{
specifier|static
specifier|final
name|PhysAggRule
name|INSTANCE
init|=
operator|new
name|PhysAggRule
argument_list|()
decl_stmt|;
specifier|private
name|PhysAggRule
parameter_list|()
block|{
name|super
argument_list|(
name|anyChild
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|)
argument_list|,
literal|"PhysAgg"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|RelTraitSet
name|empty
init|=
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|emptyTraitSet
argument_list|()
decl_stmt|;
name|LogicalAggregate
name|rel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
assert|assert
name|rel
operator|.
name|getGroupSet
argument_list|()
operator|.
name|cardinality
argument_list|()
operator|==
literal|1
assert|;
name|int
name|aggIndex
init|=
name|rel
operator|.
name|getGroupSet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|RelTrait
name|collation
init|=
name|RelCollations
operator|.
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|aggIndex
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|,
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|FIRST
argument_list|)
argument_list|)
decl_stmt|;
name|RelTraitSet
name|desiredTraits
init|=
name|empty
operator|.
name|replace
argument_list|(
name|PHYSICAL
argument_list|)
operator|.
name|replace
argument_list|(
name|collation
argument_list|)
decl_stmt|;
name|RelNode
name|convertedInput
init|=
name|convert
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|desiredTraits
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|PhysAgg
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|empty
operator|.
name|replace
argument_list|(
name|PHYSICAL
argument_list|)
argument_list|,
name|convertedInput
argument_list|,
name|rel
operator|.
name|indicator
argument_list|,
name|rel
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|rel
operator|.
name|getGroupSets
argument_list|()
argument_list|,
name|rel
operator|.
name|getAggCallList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Rule for PhysProj */
specifier|private
specifier|static
class|class
name|PhysProjRule
extends|extends
name|RelOptRule
block|{
specifier|static
specifier|final
name|PhysProjRule
name|INSTANCE
init|=
operator|new
name|PhysProjRule
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|subsetHack
decl_stmt|;
specifier|private
name|PhysProjRule
parameter_list|(
name|boolean
name|subsetHack
parameter_list|)
block|{
name|super
argument_list|(
name|RelOptRule
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|anyChild
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
literal|"PhysProj"
argument_list|)
expr_stmt|;
name|this
operator|.
name|subsetHack
operator|=
name|subsetHack
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|LogicalProject
name|rel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelNode
name|rawInput
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelNode
name|input
init|=
name|convert
argument_list|(
name|rawInput
argument_list|,
name|PHYSICAL
argument_list|)
decl_stmt|;
if|if
condition|(
name|subsetHack
operator|&&
name|input
operator|instanceof
name|RelSubset
condition|)
block|{
name|RelSubset
name|subset
init|=
operator|(
name|RelSubset
operator|)
name|input
decl_stmt|;
for|for
control|(
name|RelNode
name|child
range|:
name|subset
operator|.
name|getRels
argument_list|()
control|)
block|{
comment|// skip logical nodes
if|if
condition|(
name|child
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
operator|==
name|Convention
operator|.
name|NONE
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|RelTraitSet
name|outcome
init|=
name|child
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|PHYSICAL
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|PhysProj
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|outcome
argument_list|,
name|convert
argument_list|(
name|child
argument_list|,
name|outcome
argument_list|)
argument_list|,
name|rel
operator|.
name|getChildExps
argument_list|()
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|PhysProj
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|rel
operator|.
name|getChildExps
argument_list|()
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Rule for PhysSort */
specifier|private
specifier|static
class|class
name|PhysSortRule
extends|extends
name|ConverterRule
block|{
specifier|static
specifier|final
name|PhysSortRule
name|INSTANCE
init|=
operator|new
name|PhysSortRule
argument_list|()
decl_stmt|;
name|PhysSortRule
parameter_list|()
block|{
name|super
argument_list|(
name|Sort
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|PHYSICAL
argument_list|,
literal|"PhysSortRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
operator|(
name|Sort
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|convert
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|,
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|traitSetOf
argument_list|(
name|PHYSICAL
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|PhysSort
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|plus
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|,
name|convert
argument_list|(
name|input
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|PHYSICAL
argument_list|)
argument_list|)
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
comment|/** Rule for PhysTable */
specifier|private
specifier|static
class|class
name|PhysTableRule
extends|extends
name|RelOptRule
block|{
specifier|static
specifier|final
name|PhysTableRule
name|INSTANCE
init|=
operator|new
name|PhysTableRule
argument_list|()
decl_stmt|;
specifier|private
name|PhysTableRule
parameter_list|()
block|{
name|super
argument_list|(
name|anyChild
argument_list|(
name|EnumerableTableScan
operator|.
name|class
argument_list|)
argument_list|,
literal|"PhysScan"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|EnumerableTableScan
name|rel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|PhysTable
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* RELS */
comment|/** Market interface for Phys nodes */
specifier|private
interface|interface
name|Phys
extends|extends
name|RelNode
block|{ }
comment|/** Physical Aggregate RelNode */
specifier|private
specifier|static
class|class
name|PhysAgg
extends|extends
name|Aggregate
implements|implements
name|Phys
block|{
name|PhysAgg
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|,
name|indicator
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Aggregate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
return|return
operator|new
name|PhysAgg
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|indicator
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
comment|/** Physical Project RelNode */
specifier|private
specifier|static
class|class
name|PhysProj
extends|extends
name|Project
implements|implements
name|Phys
block|{
name|PhysProj
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|,
name|exps
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|PhysProj
name|create
parameter_list|(
specifier|final
name|RelNode
name|input
parameter_list|,
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|PHYSICAL
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|RelMdCollation
operator|.
name|project
argument_list|(
name|mq
argument_list|,
name|input
argument_list|,
name|projects
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|PhysProj
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|rowType
argument_list|)
return|;
block|}
specifier|public
name|PhysProj
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|PhysProj
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|exps
argument_list|,
name|rowType
argument_list|)
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
comment|/** Physical Sort RelNode */
specifier|private
specifier|static
class|class
name|PhysSort
extends|extends
name|Sort
implements|implements
name|Phys
block|{
name|PhysSort
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelCollation
name|collation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PhysSort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
return|return
operator|new
name|PhysSort
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|newCollation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
comment|/** Physical Table RelNode */
specifier|private
specifier|static
class|class
name|PhysTable
extends|extends
name|AbstractRelNode
implements|implements
name|Phys
block|{
name|PhysTable
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|PHYSICAL
argument_list|)
operator|.
name|replace
argument_list|(
name|COLLATION
argument_list|)
argument_list|)
expr_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|stringType
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|integerType
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|rowType
operator|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"s"
argument_list|,
name|stringType
argument_list|)
operator|.
name|add
argument_list|(
literal|"i"
argument_list|,
name|integerType
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
comment|/* UTILS */
specifier|public
specifier|static
name|RelOptRuleOperand
name|anyChild
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|first
parameter_list|)
block|{
return|return
name|RelOptRule
operator|.
name|operand
argument_list|(
name|first
argument_list|,
name|RelOptRule
operator|.
name|any
argument_list|()
argument_list|)
return|;
block|}
comment|// Created so that we can control when the TraitDefs are defined (e.g.
comment|// before the cluster is created).
specifier|private
specifier|static
name|RelNode
name|run
parameter_list|(
name|PropAction
name|action
parameter_list|,
name|RuleSet
name|rules
parameter_list|)
throws|throws
name|Exception
block|{
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|ruleSets
argument_list|(
name|rules
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
specifier|final
name|CalciteServerStatement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|unwrap
argument_list|(
name|CalciteServerStatement
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|CalcitePrepare
operator|.
name|Context
name|prepareContext
init|=
name|statement
operator|.
name|createPrepareContext
argument_list|()
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
name|prepareContext
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|CalciteCatalogReader
name|catalogReader
init|=
operator|new
name|CalciteCatalogReader
argument_list|(
name|prepareContext
operator|.
name|getRootSchema
argument_list|()
argument_list|,
name|prepareContext
operator|.
name|getDefaultSchemaPath
argument_list|()
argument_list|,
name|typeFactory
argument_list|,
name|prepareContext
operator|.
name|config
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|RelOptPlanner
name|planner
init|=
operator|new
name|VolcanoPlanner
argument_list|(
name|config
operator|.
name|getCostFactory
argument_list|()
argument_list|,
name|config
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
comment|// set up rules before we generate cluster
name|planner
operator|.
name|clearRelTraitDefs
argument_list|()
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|RelOptRule
name|r
range|:
name|rules
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelOptCluster
name|cluster
init|=
name|RelOptCluster
operator|.
name|create
argument_list|(
name|planner
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
return|return
name|action
operator|.
name|apply
argument_list|(
name|cluster
argument_list|,
name|catalogReader
argument_list|,
name|prepareContext
operator|.
name|getRootSchema
argument_list|()
operator|.
name|plus
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End TraitPropagationTest.java
end_comment

end_unit

