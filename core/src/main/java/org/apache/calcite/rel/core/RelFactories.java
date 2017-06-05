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
name|core
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
name|Contexts
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
name|RelOptTable
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
name|LogicalCorrelate
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
name|LogicalIntersect
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
name|LogicalJoin
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
name|LogicalMatch
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
name|LogicalMinus
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
name|logical
operator|.
name|LogicalSort
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
name|LogicalTableScan
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
name|LogicalUnion
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
name|LogicalValues
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
name|RexLiteral
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
name|sql
operator|.
name|SemiJoinType
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
name|tools
operator|.
name|RelBuilder
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
name|Map
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
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * Contains factory interface and default implementation for creating various  * rel nodes.  */
end_comment

begin_class
specifier|public
class|class
name|RelFactories
block|{
specifier|public
specifier|static
specifier|final
name|ProjectFactory
name|DEFAULT_PROJECT_FACTORY
init|=
operator|new
name|ProjectFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FilterFactory
name|DEFAULT_FILTER_FACTORY
init|=
operator|new
name|FilterFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|JoinFactory
name|DEFAULT_JOIN_FACTORY
init|=
operator|new
name|JoinFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|CorrelateFactory
name|DEFAULT_CORRELATE_FACTORY
init|=
operator|new
name|CorrelateFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SemiJoinFactory
name|DEFAULT_SEMI_JOIN_FACTORY
init|=
operator|new
name|SemiJoinFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SortFactory
name|DEFAULT_SORT_FACTORY
init|=
operator|new
name|SortFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|AggregateFactory
name|DEFAULT_AGGREGATE_FACTORY
init|=
operator|new
name|AggregateFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MatchFactory
name|DEFAULT_MATCH_FACTORY
init|=
operator|new
name|MatchFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SetOpFactory
name|DEFAULT_SET_OP_FACTORY
init|=
operator|new
name|SetOpFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ValuesFactory
name|DEFAULT_VALUES_FACTORY
init|=
operator|new
name|ValuesFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TableScanFactory
name|DEFAULT_TABLE_SCAN_FACTORY
init|=
operator|new
name|TableScanFactoryImpl
argument_list|()
decl_stmt|;
comment|/** A {@link RelBuilderFactory} that creates a {@link RelBuilder} that will    * create logical relational expressions for everything. */
specifier|public
specifier|static
specifier|final
name|RelBuilderFactory
name|LOGICAL_BUILDER
init|=
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|DEFAULT_PROJECT_FACTORY
argument_list|,
name|DEFAULT_FILTER_FACTORY
argument_list|,
name|DEFAULT_JOIN_FACTORY
argument_list|,
name|DEFAULT_SEMI_JOIN_FACTORY
argument_list|,
name|DEFAULT_SORT_FACTORY
argument_list|,
name|DEFAULT_AGGREGATE_FACTORY
argument_list|,
name|DEFAULT_MATCH_FACTORY
argument_list|,
name|DEFAULT_SET_OP_FACTORY
argument_list|,
name|DEFAULT_VALUES_FACTORY
argument_list|,
name|DEFAULT_TABLE_SCAN_FACTORY
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|RelFactories
parameter_list|()
block|{
block|}
comment|/**    * Can create a    * {@link org.apache.calcite.rel.logical.LogicalProject} of the    * appropriate type for this rule's calling convention.    */
specifier|public
interface|interface
name|ProjectFactory
block|{
comment|/** Creates a project. */
name|RelNode
name|createProject
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|childExprs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link ProjectFactory} that returns a vanilla    * {@link org.apache.calcite.rel.logical.LogicalProject}.    */
specifier|private
specifier|static
class|class
name|ProjectFactoryImpl
implements|implements
name|ProjectFactory
block|{
specifier|public
name|RelNode
name|createProject
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|childExprs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
return|return
name|LogicalProject
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|childExprs
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a {@link Sort} of the appropriate type    * for this rule's calling convention.    */
specifier|public
interface|interface
name|SortFactory
block|{
comment|/** Creates a sort. */
name|RelNode
name|createSort
parameter_list|(
name|RelNode
name|input
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
function_decl|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|RelNode
name|createSort
parameter_list|(
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|input
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
function_decl|;
block|}
comment|/**    * Implementation of {@link RelFactories.SortFactory} that    * returns a vanilla {@link Sort}.    */
specifier|private
specifier|static
class|class
name|SortFactoryImpl
implements|implements
name|SortFactory
block|{
specifier|public
name|RelNode
name|createSort
parameter_list|(
name|RelNode
name|input
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
return|return
name|LogicalSort
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|RelNode
name|createSort
parameter_list|(
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|input
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
return|return
name|createSort
argument_list|(
name|input
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a {@link SetOp} for a particular kind of    * set operation (UNION, EXCEPT, INTERSECT) and of the appropriate type    * for this rule's calling convention.    */
specifier|public
interface|interface
name|SetOpFactory
block|{
comment|/** Creates a set operation. */
name|RelNode
name|createSetOp
parameter_list|(
name|SqlKind
name|kind
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link RelFactories.SetOpFactory} that    * returns a vanilla {@link SetOp} for the particular kind of set    * operation (UNION, EXCEPT, INTERSECT).    */
specifier|private
specifier|static
class|class
name|SetOpFactoryImpl
implements|implements
name|SetOpFactory
block|{
specifier|public
name|RelNode
name|createSetOp
parameter_list|(
name|SqlKind
name|kind
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|UNION
case|:
return|return
name|LogicalUnion
operator|.
name|create
argument_list|(
name|inputs
argument_list|,
name|all
argument_list|)
return|;
case|case
name|EXCEPT
case|:
return|return
name|LogicalMinus
operator|.
name|create
argument_list|(
name|inputs
argument_list|,
name|all
argument_list|)
return|;
case|case
name|INTERSECT
case|:
return|return
name|LogicalIntersect
operator|.
name|create
argument_list|(
name|inputs
argument_list|,
name|all
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"not a set op: "
operator|+
name|kind
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Can create a {@link LogicalAggregate} of the appropriate type    * for this rule's calling convention.    */
specifier|public
interface|interface
name|AggregateFactory
block|{
comment|/** Creates an aggregate. */
name|RelNode
name|createAggregate
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|ImmutableList
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
function_decl|;
block|}
comment|/**    * Implementation of {@link RelFactories.AggregateFactory}    * that returns a vanilla {@link LogicalAggregate}.    */
specifier|private
specifier|static
class|class
name|AggregateFactoryImpl
implements|implements
name|AggregateFactory
block|{
specifier|public
name|RelNode
name|createAggregate
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|ImmutableList
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
name|LogicalAggregate
operator|.
name|create
argument_list|(
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
block|}
comment|/**    * Can create a {@link LogicalFilter} of the appropriate type    * for this rule's calling convention.    */
specifier|public
interface|interface
name|FilterFactory
block|{
comment|/** Creates a filter. */
name|RelNode
name|createFilter
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RexNode
name|condition
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link RelFactories.FilterFactory} that    * returns a vanilla {@link LogicalFilter}.    */
specifier|private
specifier|static
class|class
name|FilterFactoryImpl
implements|implements
name|FilterFactory
block|{
specifier|public
name|RelNode
name|createFilter
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
return|return
name|LogicalFilter
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|condition
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a join of the appropriate type for a rule's calling convention.    *    *<p>The result is typically a {@link Join}.    */
specifier|public
interface|interface
name|JoinFactory
block|{
comment|/**      * Creates a join.      *      * @param left             Left input      * @param right            Right input      * @param condition        Join condition      * @param variablesSet     Set of variables that are set by the      *                         LHS and used by the RHS and are not available to      *                         nodes above this LogicalJoin in the tree      * @param joinType         Join type      * @param semiJoinDone     Whether this join has been translated to a      *                         semi-join      */
name|RelNode
name|createJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesSet
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
function_decl|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|RelNode
name|createJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link JoinFactory} that returns a vanilla    * {@link org.apache.calcite.rel.logical.LogicalJoin}.    */
specifier|private
specifier|static
class|class
name|JoinFactoryImpl
implements|implements
name|JoinFactory
block|{
specifier|public
name|RelNode
name|createJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesSet
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
block|{
return|return
name|LogicalJoin
operator|.
name|create
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|variablesSet
argument_list|,
name|joinType
argument_list|,
name|semiJoinDone
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelDataTypeField
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|createJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
block|{
return|return
name|createJoin
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|CorrelationId
operator|.
name|setOf
argument_list|(
name|variablesStopped
argument_list|)
argument_list|,
name|joinType
argument_list|,
name|semiJoinDone
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a correlate of the appropriate type for a rule's calling    * convention.    *    *<p>The result is typically a {@link Correlate}.    */
specifier|public
interface|interface
name|CorrelateFactory
block|{
comment|/**      * Creates a correlate.      *      * @param left             Left input      * @param right            Right input      * @param correlationId    Variable name for the row of left input      * @param requiredColumns  Required columns      * @param joinType         Join type      */
name|RelNode
name|createCorrelate
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|SemiJoinType
name|joinType
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link CorrelateFactory} that returns a vanilla    * {@link org.apache.calcite.rel.logical.LogicalCorrelate}.    */
specifier|private
specifier|static
class|class
name|CorrelateFactoryImpl
implements|implements
name|CorrelateFactory
block|{
specifier|public
name|RelNode
name|createCorrelate
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|SemiJoinType
name|joinType
parameter_list|)
block|{
return|return
name|LogicalCorrelate
operator|.
name|create
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a semi-join of the appropriate type for a rule's calling    * convention.    */
specifier|public
interface|interface
name|SemiJoinFactory
block|{
comment|/**      * Creates a semi-join.      *      * @param left             Left input      * @param right            Right input      * @param condition        Join condition      */
name|RelNode
name|createSemiJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link SemiJoinFactory} that returns a vanilla    * {@link SemiJoin}.    */
specifier|private
specifier|static
class|class
name|SemiJoinFactoryImpl
implements|implements
name|SemiJoinFactory
block|{
specifier|public
name|RelNode
name|createSemiJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
specifier|final
name|JoinInfo
name|joinInfo
init|=
name|JoinInfo
operator|.
name|of
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|)
decl_stmt|;
return|return
name|SemiJoin
operator|.
name|create
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|joinInfo
operator|.
name|leftKeys
argument_list|,
name|joinInfo
operator|.
name|rightKeys
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a {@link Values} of the appropriate type for a rule's calling    * convention.    */
specifier|public
interface|interface
name|ValuesFactory
block|{
comment|/**      * Creates a Values.      */
name|RelNode
name|createValues
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link ValuesFactory} that returns a    * {@link LogicalValues}.    */
specifier|private
specifier|static
class|class
name|ValuesFactoryImpl
implements|implements
name|ValuesFactory
block|{
specifier|public
name|RelNode
name|createValues
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|)
block|{
return|return
name|LogicalValues
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|rowType
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|tuples
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a {@link TableScan} of the appropriate type for a rule's calling    * convention.    */
specifier|public
interface|interface
name|TableScanFactory
block|{
comment|/**      * Creates a {@link TableScan}.      */
name|RelNode
name|createScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link TableScanFactory} that returns a    * {@link LogicalTableScan}.    */
specifier|private
specifier|static
class|class
name|TableScanFactoryImpl
implements|implements
name|TableScanFactory
block|{
specifier|public
name|RelNode
name|createScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|)
block|{
return|return
name|LogicalTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|table
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a {@link Match} of    * the appropriate type for a rule's calling convention.    */
specifier|public
interface|interface
name|MatchFactory
block|{
comment|/** Creates a {@link Match}. */
name|RelNode
name|createMatchRecognize
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RexNode
name|pattern
parameter_list|,
name|boolean
name|strictStart
parameter_list|,
name|boolean
name|strictEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
parameter_list|,
name|RexNode
name|after
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|subsets
parameter_list|,
name|boolean
name|allRows
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link MatchFactory}    * that returns a {@link LogicalMatch}.    */
specifier|private
specifier|static
class|class
name|MatchFactoryImpl
implements|implements
name|MatchFactory
block|{
specifier|public
name|RelNode
name|createMatchRecognize
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RexNode
name|pattern
parameter_list|,
name|boolean
name|strictStart
parameter_list|,
name|boolean
name|strictEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
parameter_list|,
name|RexNode
name|after
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|subsets
parameter_list|,
name|boolean
name|allRows
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
name|LogicalMatch
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefinitions
argument_list|,
name|measures
argument_list|,
name|after
argument_list|,
name|subsets
argument_list|,
name|allRows
argument_list|,
name|partitionKeys
argument_list|,
name|orderKeys
argument_list|,
name|rowType
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelFactories.java
end_comment

end_unit

