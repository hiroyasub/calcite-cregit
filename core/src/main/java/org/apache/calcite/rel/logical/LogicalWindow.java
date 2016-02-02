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
name|logical
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
name|linq4j
operator|.
name|Ord
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
name|core
operator|.
name|Window
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
name|RexFieldCollation
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
name|RexLocalRef
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
name|RexOver
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
name|RexProgram
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rex
operator|.
name|RexWindow
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
name|RexWindowBound
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
name|util
operator|.
name|ImmutableBitSet
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
name|Litmus
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|LinkedListMultimap
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
name|Lists
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
name|Multimap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Sub-class of {@link org.apache.calcite.rel.core.Window}  * not targeted at any particular engine or calling convention.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LogicalWindow
extends|extends
name|Window
block|{
comment|/**    * Creates a LogicalWindow.    *    *<p>Use {@link #create} unless you know what you're doing.    *    * @param cluster Cluster    * @param traitSet Trait set    * @param input   Input relational expression    * @param constants List of constants that are additional inputs    * @param rowType Output row type    * @param groups Window groups    */
specifier|public
name|LogicalWindow
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|constants
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|Group
argument_list|>
name|groups
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|constants
argument_list|,
name|rowType
argument_list|,
name|groups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|LogicalWindow
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
operator|new
name|LogicalWindow
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|constants
argument_list|,
name|rowType
argument_list|,
name|groups
argument_list|)
return|;
block|}
comment|/**    * Creates a LogicalWindow.    *    * @param input   Input relational expression    * @param traitSet Trait set    * @param constants List of constants that are additional inputs    * @param rowType Output row type    * @param groups Window groups    */
specifier|public
specifier|static
name|LogicalWindow
name|create
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|constants
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|Group
argument_list|>
name|groups
parameter_list|)
block|{
return|return
operator|new
name|LogicalWindow
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|constants
argument_list|,
name|rowType
argument_list|,
name|groups
argument_list|)
return|;
block|}
comment|/**    * Creates a LogicalWindow by parsing a {@link RexProgram}.    */
specifier|public
specifier|static
name|RelNode
name|create
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelBuilder
name|relBuilder
parameter_list|,
name|RelNode
name|child
parameter_list|,
specifier|final
name|RexProgram
name|program
parameter_list|)
block|{
specifier|final
name|RelDataType
name|outRowType
init|=
name|program
operator|.
name|getOutputRowType
argument_list|()
decl_stmt|;
comment|// Build a list of distinct groups, partitions and aggregate
comment|// functions.
specifier|final
name|Multimap
argument_list|<
name|WindowKey
argument_list|,
name|RexOver
argument_list|>
name|windowMap
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|final
name|int
name|inputFieldCount
init|=
name|child
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|RexLiteral
argument_list|,
name|RexInputRef
argument_list|>
name|constantPool
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|constants
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Identify constants in the expression tree and replace them with
comment|// references to newly generated constant pool.
name|RexShuttle
name|replaceConstants
init|=
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
name|RexInputRef
name|ref
init|=
name|constantPool
operator|.
name|get
argument_list|(
name|literal
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
return|return
name|ref
return|;
block|}
name|constants
operator|.
name|add
argument_list|(
name|literal
argument_list|)
expr_stmt|;
name|ref
operator|=
operator|new
name|RexInputRef
argument_list|(
name|constantPool
operator|.
name|size
argument_list|()
operator|+
name|inputFieldCount
argument_list|,
name|literal
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|constantPool
operator|.
name|put
argument_list|(
name|literal
argument_list|,
name|ref
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
block|}
decl_stmt|;
comment|// Build a list of groups, partitions, and aggregate functions. Each
comment|// aggregate function will add its arguments as outputs of the input
comment|// program.
specifier|final
name|Map
argument_list|<
name|RexOver
argument_list|,
name|RexOver
argument_list|>
name|origToNewOver
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|agg
range|:
name|program
operator|.
name|getExprList
argument_list|()
control|)
block|{
if|if
condition|(
name|agg
operator|instanceof
name|RexOver
condition|)
block|{
specifier|final
name|RexOver
name|origOver
init|=
operator|(
name|RexOver
operator|)
name|agg
decl_stmt|;
specifier|final
name|RexOver
name|newOver
init|=
operator|(
name|RexOver
operator|)
name|origOver
operator|.
name|accept
argument_list|(
name|replaceConstants
argument_list|)
decl_stmt|;
name|origToNewOver
operator|.
name|put
argument_list|(
name|origOver
argument_list|,
name|newOver
argument_list|)
expr_stmt|;
name|addWindows
argument_list|(
name|windowMap
argument_list|,
name|newOver
argument_list|,
name|inputFieldCount
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Map
argument_list|<
name|RexOver
argument_list|,
name|Window
operator|.
name|RexWinAggCall
argument_list|>
name|aggMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|WindowKey
argument_list|,
name|Collection
argument_list|<
name|RexOver
argument_list|>
argument_list|>
name|entry
range|:
name|windowMap
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|WindowKey
name|windowKey
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexWinAggCall
argument_list|>
name|aggCalls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexOver
name|over
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
specifier|final
name|RexWinAggCall
name|aggCall
init|=
operator|new
name|RexWinAggCall
argument_list|(
name|over
operator|.
name|getAggOperator
argument_list|()
argument_list|,
name|over
operator|.
name|getType
argument_list|()
argument_list|,
name|toInputRefs
argument_list|(
name|over
operator|.
name|operands
argument_list|)
argument_list|,
name|aggMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|aggCalls
operator|.
name|add
argument_list|(
name|aggCall
argument_list|)
expr_stmt|;
name|aggMap
operator|.
name|put
argument_list|(
name|over
argument_list|,
name|aggCall
argument_list|)
expr_stmt|;
block|}
name|RexShuttle
name|toInputRefs
init|=
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|)
block|{
return|return
operator|new
name|RexInputRef
argument_list|(
name|localRef
operator|.
name|getIndex
argument_list|()
argument_list|,
name|localRef
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|groups
operator|.
name|add
argument_list|(
operator|new
name|Group
argument_list|(
name|windowKey
operator|.
name|groupSet
argument_list|,
name|windowKey
operator|.
name|isRows
argument_list|,
name|windowKey
operator|.
name|lowerBound
operator|.
name|accept
argument_list|(
name|toInputRefs
argument_list|)
argument_list|,
name|windowKey
operator|.
name|upperBound
operator|.
name|accept
argument_list|(
name|toInputRefs
argument_list|)
argument_list|,
name|windowKey
operator|.
name|orderKeys
argument_list|,
name|aggCalls
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Figure out the type of the inputs to the output program.
comment|// They are: the inputs to this rel, followed by the outputs of
comment|// each window.
specifier|final
name|List
argument_list|<
name|Window
operator|.
name|RexWinAggCall
argument_list|>
name|flattenedAggCallList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|fieldList
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
argument_list|(
name|child
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|fieldList
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Use better field names for agg calls that are projected.
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RexLocalRef
argument_list|>
name|ref
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|int
name|index
init|=
name|ref
operator|.
name|e
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|>=
name|offset
condition|)
block|{
name|fieldNames
operator|.
name|put
argument_list|(
name|index
operator|-
name|offset
argument_list|,
name|outRowType
operator|.
name|getFieldNames
argument_list|()
operator|.
name|get
argument_list|(
name|ref
operator|.
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Ord
argument_list|<
name|Group
argument_list|>
name|window
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|groups
argument_list|)
control|)
block|{
for|for
control|(
name|Ord
argument_list|<
name|RexWinAggCall
argument_list|>
name|over
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|window
operator|.
name|e
operator|.
name|aggCalls
argument_list|)
control|)
block|{
comment|// Add the k-th over expression of
comment|// the i-th window to the output of the program.
name|String
name|name
init|=
name|fieldNames
operator|.
name|get
argument_list|(
name|over
operator|.
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|startsWith
argument_list|(
literal|"$"
argument_list|)
condition|)
block|{
name|name
operator|=
literal|"w"
operator|+
name|window
operator|.
name|i
operator|+
literal|"$o"
operator|+
name|over
operator|.
name|i
expr_stmt|;
block|}
name|fieldList
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|name
argument_list|,
name|over
operator|.
name|e
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|flattenedAggCallList
operator|.
name|add
argument_list|(
name|over
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RelDataType
name|intermediateRowType
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
name|fieldList
argument_list|)
decl_stmt|;
comment|// The output program is the windowed agg's program, combined with
comment|// the output calc (if it exists).
name|RexShuttle
name|shuttle
init|=
operator|new
name|RexShuttle
argument_list|()
block|{
specifier|public
name|RexNode
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|)
block|{
comment|// Look up the aggCall which this expr was translated to.
specifier|final
name|Window
operator|.
name|RexWinAggCall
name|aggCall
init|=
name|aggMap
operator|.
name|get
argument_list|(
name|origToNewOver
operator|.
name|get
argument_list|(
name|over
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|aggCall
operator|!=
literal|null
assert|;
assert|assert
name|RelOptUtil
operator|.
name|eq
argument_list|(
literal|"over"
argument_list|,
name|over
operator|.
name|getType
argument_list|()
argument_list|,
literal|"aggCall"
argument_list|,
name|aggCall
operator|.
name|getType
argument_list|()
argument_list|,
name|Litmus
operator|.
name|THROW
argument_list|)
assert|;
comment|// Find the index of the aggCall among all partitions of all
comment|// groups.
specifier|final
name|int
name|aggCallIndex
init|=
name|flattenedAggCallList
operator|.
name|indexOf
argument_list|(
name|aggCall
argument_list|)
decl_stmt|;
assert|assert
name|aggCallIndex
operator|>=
literal|0
assert|;
comment|// Replace expression with a reference to the window slot.
specifier|final
name|int
name|index
init|=
name|inputFieldCount
operator|+
name|aggCallIndex
decl_stmt|;
assert|assert
name|RelOptUtil
operator|.
name|eq
argument_list|(
literal|"over"
argument_list|,
name|over
operator|.
name|getType
argument_list|()
argument_list|,
literal|"intermed"
argument_list|,
name|intermediateRowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|Litmus
operator|.
name|THROW
argument_list|)
assert|;
return|return
operator|new
name|RexInputRef
argument_list|(
name|index
argument_list|,
name|over
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|localRef
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|<
name|inputFieldCount
condition|)
block|{
comment|// Reference to input field.
return|return
name|localRef
return|;
block|}
return|return
operator|new
name|RexLocalRef
argument_list|(
name|flattenedAggCallList
operator|.
name|size
argument_list|()
operator|+
name|index
argument_list|,
name|localRef
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|LogicalWindow
name|window
init|=
name|LogicalWindow
operator|.
name|create
argument_list|(
name|traitSet
argument_list|,
name|child
argument_list|,
name|constants
argument_list|,
name|intermediateRowType
argument_list|,
name|groups
argument_list|)
decl_stmt|;
comment|// The order that the "over" calls occur in the groups and
comment|// partitions may not match the order in which they occurred in the
comment|// original expression.
comment|// Add a project to permute them.
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexNodesWindow
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|rexNode
range|:
name|program
operator|.
name|getExprList
argument_list|()
control|)
block|{
name|rexNodesWindow
operator|.
name|add
argument_list|(
name|rexNode
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|refToWindow
init|=
name|toInputRefs
argument_list|(
name|rexNodesWindow
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projectList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexLocalRef
name|inputRef
range|:
name|program
operator|.
name|getProjectList
argument_list|()
control|)
block|{
specifier|final
name|int
name|index
init|=
name|inputRef
operator|.
name|getIndex
argument_list|()
decl_stmt|;
specifier|final
name|RexInputRef
name|ref
init|=
operator|(
name|RexInputRef
operator|)
name|refToWindow
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|projectList
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|window
argument_list|)
operator|.
name|project
argument_list|(
name|projectList
argument_list|,
name|outRowType
operator|.
name|getFieldNames
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|RexNode
argument_list|>
name|toInputRefs
parameter_list|(
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|operands
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|operands
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|RexNode
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|RexNode
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|instanceof
name|RexInputRef
condition|)
block|{
return|return
name|operand
return|;
block|}
assert|assert
name|operand
operator|instanceof
name|RexLocalRef
assert|;
specifier|final
name|RexLocalRef
name|ref
init|=
operator|(
name|RexLocalRef
operator|)
name|operand
decl_stmt|;
return|return
operator|new
name|RexInputRef
argument_list|(
name|ref
operator|.
name|getIndex
argument_list|()
argument_list|,
name|ref
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Group specification. All windowed aggregates over the same window    * (regardless of how it is specified, in terms of a named window or specified    * attribute by attribute) will end up with the same window key. */
specifier|private
specifier|static
class|class
name|WindowKey
block|{
specifier|private
specifier|final
name|ImmutableBitSet
name|groupSet
decl_stmt|;
specifier|private
specifier|final
name|RelCollation
name|orderKeys
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isRows
decl_stmt|;
specifier|private
specifier|final
name|RexWindowBound
name|lowerBound
decl_stmt|;
specifier|private
specifier|final
name|RexWindowBound
name|upperBound
decl_stmt|;
specifier|public
name|WindowKey
parameter_list|(
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|boolean
name|isRows
parameter_list|,
name|RexWindowBound
name|lowerBound
parameter_list|,
name|RexWindowBound
name|upperBound
parameter_list|)
block|{
name|this
operator|.
name|groupSet
operator|=
name|groupSet
expr_stmt|;
name|this
operator|.
name|orderKeys
operator|=
name|orderKeys
expr_stmt|;
name|this
operator|.
name|isRows
operator|=
name|isRows
expr_stmt|;
name|this
operator|.
name|lowerBound
operator|=
name|lowerBound
expr_stmt|;
name|this
operator|.
name|upperBound
operator|=
name|upperBound
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|groupSet
argument_list|,
name|orderKeys
argument_list|,
name|isRows
argument_list|,
name|lowerBound
argument_list|,
name|upperBound
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|WindowKey
operator|&&
name|groupSet
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|WindowKey
operator|)
name|obj
operator|)
operator|.
name|groupSet
argument_list|)
operator|&&
name|orderKeys
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|WindowKey
operator|)
name|obj
operator|)
operator|.
name|orderKeys
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|lowerBound
argument_list|,
operator|(
operator|(
name|WindowKey
operator|)
name|obj
operator|)
operator|.
name|lowerBound
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|upperBound
argument_list|,
operator|(
operator|(
name|WindowKey
operator|)
name|obj
operator|)
operator|.
name|upperBound
argument_list|)
operator|&&
name|isRows
operator|==
operator|(
operator|(
name|WindowKey
operator|)
name|obj
operator|)
operator|.
name|isRows
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|addWindows
parameter_list|(
name|Multimap
argument_list|<
name|WindowKey
argument_list|,
name|RexOver
argument_list|>
name|windowMap
parameter_list|,
name|RexOver
name|over
parameter_list|,
specifier|final
name|int
name|inputFieldCount
parameter_list|)
block|{
specifier|final
name|RexWindow
name|aggWindow
init|=
name|over
operator|.
name|getWindow
argument_list|()
decl_stmt|;
comment|// Look up or create a window.
name|RelCollation
name|orderKeys
init|=
name|getCollation
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|aggWindow
operator|.
name|orderKeys
argument_list|,
operator|new
name|Predicate
argument_list|<
name|RexFieldCollation
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|RexFieldCollation
name|rexFieldCollation
parameter_list|)
block|{
comment|// If ORDER BY references constant (i.e. RexInputRef),
comment|// then we can ignore such ORDER BY key.
return|return
name|rexFieldCollation
operator|.
name|left
operator|instanceof
name|RexLocalRef
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ImmutableBitSet
name|groupSet
init|=
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|getProjectOrdinals
argument_list|(
name|aggWindow
operator|.
name|partitionKeys
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|groupLength
init|=
name|groupSet
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|inputFieldCount
operator|<
name|groupLength
condition|)
block|{
comment|// If PARTITION BY references constant, we can ignore such partition key.
comment|// All the inputs after inputFieldCount are literals, thus we can clear.
name|groupSet
operator|=
name|groupSet
operator|.
name|except
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|inputFieldCount
argument_list|,
name|groupLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|WindowKey
name|windowKey
init|=
operator|new
name|WindowKey
argument_list|(
name|groupSet
argument_list|,
name|orderKeys
argument_list|,
name|aggWindow
operator|.
name|isRows
argument_list|()
argument_list|,
name|aggWindow
operator|.
name|getLowerBound
argument_list|()
argument_list|,
name|aggWindow
operator|.
name|getUpperBound
argument_list|()
argument_list|)
decl_stmt|;
name|windowMap
operator|.
name|put
argument_list|(
name|windowKey
argument_list|,
name|over
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalWindow.java
end_comment

end_unit

