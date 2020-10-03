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
name|util
operator|.
name|DateString
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
name|TimeString
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
name|TimestampString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
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
name|comparator
operator|.
name|ComparisonOperator
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
name|KeyMeta
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
name|HashMultimap
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
name|Comparator
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
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Translates {@link RexNode} expressions into {@link IndexCondition}  * which might be pushed down to an InnoDB data source.  */
end_comment

begin_class
class|class
name|InnodbFilterTranslator
block|{
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
comment|/** Field names per row type. */
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
comment|/** Primary key metadata. */
specifier|private
specifier|final
name|KeyMeta
name|pkMeta
decl_stmt|;
comment|/** Secondary key metadata. */
specifier|private
specifier|final
name|List
argument_list|<
name|KeyMeta
argument_list|>
name|skMetaList
decl_stmt|;
comment|/** If not null, force to use one specific index from hint. */
specifier|private
specifier|final
annotation|@
name|Nullable
name|String
name|forceIndexName
decl_stmt|;
name|InnodbFilterTranslator
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|TableDef
name|tableDef
parameter_list|,
annotation|@
name|Nullable
name|String
name|forceIndexName
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
name|this
operator|.
name|fieldNames
operator|=
name|InnodbRules
operator|.
name|innodbFieldNames
argument_list|(
name|rowType
argument_list|)
expr_stmt|;
name|this
operator|.
name|pkMeta
operator|=
name|tableDef
operator|.
name|getPrimaryKeyMeta
argument_list|()
expr_stmt|;
name|this
operator|.
name|skMetaList
operator|=
name|tableDef
operator|.
name|getSecondaryKeyMetaList
argument_list|()
expr_stmt|;
name|this
operator|.
name|forceIndexName
operator|=
name|forceIndexName
expr_stmt|;
block|}
comment|/**    * Produces the push down condition for the given    * relational expression condition.    *    * @param condition condition to translate    * @return push down condition    */
specifier|public
name|IndexCondition
name|translateMatch
parameter_list|(
name|RexNode
name|condition
parameter_list|)
block|{
comment|// does not support disjunctions
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
operator|==
literal|1
condition|)
block|{
return|return
name|translateAnd
argument_list|(
name|disjunctions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"cannot translate "
operator|+
name|condition
argument_list|)
throw|;
block|}
block|}
comment|/**    * Translates a conjunctive predicate to a push down condition.    *    * @param condition a conjunctive predicate    * @return push down condition    */
specifier|private
name|IndexCondition
name|translateAnd
parameter_list|(
name|RexNode
name|condition
parameter_list|)
block|{
comment|// expand calls to SEARCH(..., Sarg()) to>, =, etc.
specifier|final
name|RexNode
name|condition2
init|=
name|RexUtil
operator|.
name|expandSearch
argument_list|(
name|rexBuilder
argument_list|,
literal|null
argument_list|,
name|condition
argument_list|)
decl_stmt|;
comment|// decompose condition by AND, flatten row expression
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexNodeList
init|=
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|condition2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexCondition
argument_list|>
name|indexConditions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// try to push down filter by primary key
if|if
condition|(
name|pkMeta
operator|!=
literal|null
condition|)
block|{
name|IndexCondition
name|pkPushDownCond
init|=
name|findPushDownCondition
argument_list|(
name|rexNodeList
argument_list|,
name|pkMeta
argument_list|)
decl_stmt|;
name|indexConditions
operator|.
name|add
argument_list|(
name|pkPushDownCond
argument_list|)
expr_stmt|;
block|}
comment|// try to push down filter by secondary keys
if|if
condition|(
name|CollectionUtils
operator|.
name|isNotEmpty
argument_list|(
name|skMetaList
argument_list|)
condition|)
block|{
for|for
control|(
name|KeyMeta
name|skMeta
range|:
name|skMetaList
control|)
block|{
name|indexConditions
operator|.
name|add
argument_list|(
name|findPushDownCondition
argument_list|(
name|rexNodeList
argument_list|,
name|skMeta
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// a collection of all possible push down conditions, see if it can
comment|// be pushed down, filter by forcing index name, then sort by comparator
name|Stream
argument_list|<
name|IndexCondition
argument_list|>
name|pushDownConditions
init|=
name|indexConditions
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|IndexCondition
operator|::
name|canPushDown
argument_list|)
operator|.
name|filter
argument_list|(
name|this
operator|::
name|nonForceIndexOrMatchForceIndexName
argument_list|)
operator|.
name|sorted
argument_list|(
operator|new
name|IndexConditionComparator
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|pushDownConditions
operator|.
name|findFirst
argument_list|()
operator|.
name|orElse
argument_list|(
name|IndexCondition
operator|.
name|EMPTY_CONDITION
argument_list|)
return|;
block|}
comment|/**    * Tries to translate a conjunctive predicate to push down condition.    *    * @param rexNodeList original field expressions    * @param keyMeta     index metadata    * @return push down condition    */
specifier|private
name|IndexCondition
name|findPushDownCondition
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexNodeList
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|)
block|{
comment|// find field expressions matching index columns and specific operators
name|List
argument_list|<
name|InternalRexNode
argument_list|>
name|matchedRexNodeList
init|=
name|analyzePrefixMatches
argument_list|(
name|rexNodeList
argument_list|,
name|keyMeta
argument_list|)
decl_stmt|;
comment|// none of the conditions can be pushed down
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|matchedRexNodeList
argument_list|)
condition|)
block|{
return|return
name|IndexCondition
operator|.
name|EMPTY_CONDITION
return|;
block|}
comment|// a collection that maps ordinal in index column list
comment|// to multiple field expressions
name|Multimap
argument_list|<
name|Integer
argument_list|,
name|InternalRexNode
argument_list|>
name|keyOrdToNodesMap
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalRexNode
name|node
range|:
name|matchedRexNodeList
control|)
block|{
name|keyOrdToNodesMap
operator|.
name|put
argument_list|(
name|node
operator|.
name|ordinalInKey
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
comment|// left-prefix index rule not match
name|Collection
argument_list|<
name|InternalRexNode
argument_list|>
name|leftMostKeyNodes
init|=
name|keyOrdToNodesMap
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|leftMostKeyNodes
argument_list|)
condition|)
block|{
return|return
name|IndexCondition
operator|.
name|EMPTY_CONDITION
return|;
block|}
comment|// create result which might have conditions to push down
name|List
argument_list|<
name|String
argument_list|>
name|indexColumnNames
init|=
name|keyMeta
operator|.
name|getKeyColumnNames
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|pushDownRexNodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|remainderRexNodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|rexNodeList
argument_list|)
decl_stmt|;
name|IndexCondition
name|condition
init|=
name|IndexCondition
operator|.
name|create
argument_list|(
name|fieldNames
argument_list|,
name|keyMeta
operator|.
name|getName
argument_list|()
argument_list|,
name|indexColumnNames
argument_list|,
name|pushDownRexNodeList
argument_list|,
name|remainderRexNodeList
argument_list|)
decl_stmt|;
comment|// handle point query if possible
name|condition
operator|=
name|handlePointQuery
argument_list|(
name|condition
argument_list|,
name|keyMeta
argument_list|,
name|leftMostKeyNodes
argument_list|,
name|keyOrdToNodesMap
argument_list|,
name|pushDownRexNodeList
argument_list|,
name|remainderRexNodeList
argument_list|)
expr_stmt|;
if|if
condition|(
name|condition
operator|.
name|canPushDown
argument_list|()
condition|)
block|{
return|return
name|condition
return|;
block|}
comment|// handle range query
name|condition
operator|=
name|handleRangeQuery
argument_list|(
name|condition
argument_list|,
name|keyMeta
argument_list|,
name|leftMostKeyNodes
argument_list|,
name|pushDownRexNodeList
argument_list|,
name|remainderRexNodeList
argument_list|,
literal|">="
argument_list|,
literal|">"
argument_list|)
expr_stmt|;
name|condition
operator|=
name|handleRangeQuery
argument_list|(
name|condition
argument_list|,
name|keyMeta
argument_list|,
name|leftMostKeyNodes
argument_list|,
name|pushDownRexNodeList
argument_list|,
name|remainderRexNodeList
argument_list|,
literal|"<="
argument_list|,
literal|"<"
argument_list|)
expr_stmt|;
return|return
name|condition
return|;
block|}
comment|/**    * Analyzes from the first to the subsequent field expression following the    * left-prefix rule, this will based on a specific index    * (<code>KeyMeta</code>), check the column and its corresponding operation,    * see if it can be translated into a push down condition.    *    *<p>The result is a collection of matched field expressions.    *    * @param rexNodeList Field expressions    * @param keyMeta     Index metadata    * @return a collection of matched field expressions    */
specifier|private
name|List
argument_list|<
name|InternalRexNode
argument_list|>
name|analyzePrefixMatches
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexNodeList
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|)
block|{
return|return
name|rexNodeList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|rexNode
lambda|->
name|translateMatch2
argument_list|(
name|rexNode
argument_list|,
name|keyMeta
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|Optional
operator|::
name|isPresent
argument_list|)
operator|.
name|map
argument_list|(
name|Optional
operator|::
name|get
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Handles point query push down. The operation of the leftmost nodes    * should be "=", then we try to find as many "=" operations as    * possible, if "=" operation found on all index columns, then it is a    * point query on key (both primary key or composite key), else it will    * transform to a range query.    *    *<p>If conditions can be pushed down, for range query, we only remove    * first node from field expression list (<code>rexNodeList</code>),    * because Innodb-java-reader only support range query, not fully    * index condition pushdown; for point query, we can remove them all.    */
specifier|private
name|IndexCondition
name|handlePointQuery
parameter_list|(
name|IndexCondition
name|condition
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|,
name|Collection
argument_list|<
name|InternalRexNode
argument_list|>
name|leftMostKeyNodes
parameter_list|,
name|Multimap
argument_list|<
name|Integer
argument_list|,
name|InternalRexNode
argument_list|>
name|keyOrdToNodesMap
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|pushDownRexNodeList
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|remainderRexNodeList
parameter_list|)
block|{
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|leftMostEqOpNode
init|=
name|findFirstOp
argument_list|(
name|leftMostKeyNodes
argument_list|,
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftMostEqOpNode
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|InternalRexNode
name|node
init|=
name|leftMostEqOpNode
operator|.
name|get
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|InternalRexNode
argument_list|>
name|matchNodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|findSubsequentMatches
argument_list|(
name|matchNodes
argument_list|,
name|keyMeta
operator|.
name|getNumOfColumns
argument_list|()
argument_list|,
name|keyOrdToNodesMap
argument_list|,
literal|"="
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|key
init|=
name|createKey
argument_list|(
name|matchNodes
argument_list|)
decl_stmt|;
name|pushDownRexNodeList
operator|.
name|add
argument_list|(
name|node
operator|.
name|node
argument_list|)
expr_stmt|;
name|remainderRexNodeList
operator|.
name|remove
argument_list|(
name|node
operator|.
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|matchNodes
operator|.
name|size
argument_list|()
operator|!=
name|keyMeta
operator|.
name|getNumOfColumns
argument_list|()
condition|)
block|{
comment|// "=" operation does not apply on all index columns
return|return
name|condition
operator|.
name|withQueryType
argument_list|(
name|QueryType
operator|.
name|getRangeQuery
argument_list|(
name|keyMeta
operator|.
name|isSecondaryKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withRangeQueryLowerOp
argument_list|(
name|ComparisonOperator
operator|.
name|GTE
argument_list|)
operator|.
name|withRangeQueryLowerKey
argument_list|(
name|key
argument_list|)
operator|.
name|withRangeQueryUpperOp
argument_list|(
name|ComparisonOperator
operator|.
name|LTE
argument_list|)
operator|.
name|withRangeQueryUpperKey
argument_list|(
name|key
argument_list|)
operator|.
name|withPushDownConditions
argument_list|(
name|pushDownRexNodeList
argument_list|)
operator|.
name|withRemainderConditions
argument_list|(
name|remainderRexNodeList
argument_list|)
return|;
block|}
else|else
block|{
for|for
control|(
name|InternalRexNode
name|n
range|:
name|matchNodes
control|)
block|{
name|pushDownRexNodeList
operator|.
name|add
argument_list|(
name|n
operator|.
name|node
argument_list|)
expr_stmt|;
name|remainderRexNodeList
operator|.
name|remove
argument_list|(
name|n
operator|.
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|condition
operator|.
name|withQueryType
argument_list|(
name|QueryType
operator|.
name|getPointQuery
argument_list|(
name|keyMeta
operator|.
name|isSecondaryKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withPointQueryKey
argument_list|(
name|key
argument_list|)
operator|.
name|withPushDownConditions
argument_list|(
name|pushDownRexNodeList
argument_list|)
operator|.
name|withRemainderConditions
argument_list|(
name|remainderRexNodeList
argument_list|)
return|;
block|}
block|}
return|return
name|condition
return|;
block|}
comment|/**    * Handles range query push down. We try to find operation of GTE, GT, LT    * or LTE in the left most key.    *    *<p>We only push down partial condition since Innodb-java-reader only    * supports range query with lower and upper bound, not fully index condition    * push down.    *    *<p>For example, given the following 7 rows with (a,b) as secondary key.    *<pre>    *   a=100,b=200    *   a=100,b=300    *   a=100,b=500    *   a=200,b=100    *   a=200,b=400    *   a=300,b=300    *   a=500,b=600    *</pre>    *    *<p>If condition is<code>a&gt;200 AND b&gt;300</code>,    * the lower bound should be    *<code>a=300,b=300</code>, we can only push down one condition    *<code>a&gt;200</code> as lower bound condition, we cannot push    *<code>a&gt;200 AND b&gt;300</code> because it will include    *<code>a=200,b=400</code> as well which is incorrect.    *    *<p>If conditions can be pushed down, we will first node from field    * expression list (<code>rexNodeList</code>).    */
specifier|private
name|IndexCondition
name|handleRangeQuery
parameter_list|(
name|IndexCondition
name|condition
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|,
name|Collection
argument_list|<
name|InternalRexNode
argument_list|>
name|leftMostKeyNodes
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|pushDownRexNodeList
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|remainderRexNodeList
parameter_list|,
name|String
modifier|...
name|opList
parameter_list|)
block|{
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|node
init|=
name|findFirstOp
argument_list|(
name|leftMostKeyNodes
argument_list|,
name|opList
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|pushDownRexNodeList
operator|.
name|add
argument_list|(
name|node
operator|.
name|get
argument_list|()
operator|.
name|node
argument_list|)
expr_stmt|;
name|remainderRexNodeList
operator|.
name|remove
argument_list|(
name|node
operator|.
name|get
argument_list|()
operator|.
name|node
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|key
init|=
name|createKey
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|node
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ComparisonOperator
name|op
init|=
name|ComparisonOperator
operator|.
name|parse
argument_list|(
name|node
operator|.
name|get
argument_list|()
operator|.
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|ComparisonOperator
operator|.
name|isLowerBoundOp
argument_list|(
name|opList
argument_list|)
condition|)
block|{
return|return
name|condition
operator|.
name|withQueryType
argument_list|(
name|QueryType
operator|.
name|getRangeQuery
argument_list|(
name|keyMeta
operator|.
name|isSecondaryKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withRangeQueryLowerOp
argument_list|(
name|op
argument_list|)
operator|.
name|withRangeQueryLowerKey
argument_list|(
name|key
argument_list|)
operator|.
name|withPushDownConditions
argument_list|(
name|pushDownRexNodeList
argument_list|)
operator|.
name|withRemainderConditions
argument_list|(
name|remainderRexNodeList
argument_list|)
return|;
block|}
if|else if
condition|(
name|ComparisonOperator
operator|.
name|isUpperBoundOp
argument_list|(
name|opList
argument_list|)
condition|)
block|{
return|return
name|condition
operator|.
name|withQueryType
argument_list|(
name|QueryType
operator|.
name|getRangeQuery
argument_list|(
name|keyMeta
operator|.
name|isSecondaryKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withRangeQueryUpperOp
argument_list|(
name|op
argument_list|)
operator|.
name|withRangeQueryUpperKey
argument_list|(
name|key
argument_list|)
operator|.
name|withPushDownConditions
argument_list|(
name|pushDownRexNodeList
argument_list|)
operator|.
name|withRemainderConditions
argument_list|(
name|remainderRexNodeList
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"comparison operation is invalid "
operator|+
name|op
argument_list|)
throw|;
block|}
block|}
return|return
name|condition
return|;
block|}
comment|/**    * Translates a binary relation.    */
specifier|private
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|translateMatch2
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|EQUALS
case|:
return|return
name|translateBinary
argument_list|(
literal|"="
argument_list|,
literal|"="
argument_list|,
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|keyMeta
argument_list|)
return|;
case|case
name|LESS_THAN
case|:
return|return
name|translateBinary
argument_list|(
literal|"<"
argument_list|,
literal|">"
argument_list|,
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|keyMeta
argument_list|)
return|;
case|case
name|LESS_THAN_OR_EQUAL
case|:
return|return
name|translateBinary
argument_list|(
literal|"<="
argument_list|,
literal|">="
argument_list|,
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|keyMeta
argument_list|)
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|translateBinary
argument_list|(
literal|">"
argument_list|,
literal|"<"
argument_list|,
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|keyMeta
argument_list|)
return|;
case|case
name|GREATER_THAN_OR_EQUAL
case|:
return|return
name|translateBinary
argument_list|(
literal|">="
argument_list|,
literal|"<="
argument_list|,
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|keyMeta
argument_list|)
return|;
default|default:
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
comment|/**    * Translates a call to a binary operator, reversing arguments if    * necessary.    */
specifier|private
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|translateBinary
parameter_list|(
name|String
name|op
parameter_list|,
name|String
name|rop
parameter_list|,
name|RexCall
name|call
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|)
block|{
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
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|expression
init|=
name|translateBinary2
argument_list|(
name|op
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|call
argument_list|,
name|keyMeta
argument_list|)
decl_stmt|;
if|if
condition|(
name|expression
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|expression
return|;
block|}
name|expression
operator|=
name|translateBinary2
argument_list|(
name|rop
argument_list|,
name|right
argument_list|,
name|left
argument_list|,
name|call
argument_list|,
name|keyMeta
argument_list|)
expr_stmt|;
return|return
name|expression
return|;
block|}
comment|/**    * Translates a call to a binary operator. Returns null on failure.    */
specifier|private
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|translateBinary2
parameter_list|(
name|String
name|op
parameter_list|,
name|RexNode
name|left
parameter_list|,
name|RexNode
name|right
parameter_list|,
name|RexNode
name|originNode
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|)
block|{
name|RexLiteral
name|rightLiteral
decl_stmt|;
if|if
condition|(
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
name|rightLiteral
operator|=
operator|(
name|RexLiteral
operator|)
name|right
expr_stmt|;
block|}
else|else
block|{
comment|// because MySQL's TIMESTAMP is mapped to TIMESTAMP_WITH_TIME_ZONE sql type,
comment|// we should cast the value to literal.
if|if
condition|(
name|right
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|)
operator|&&
name|isSqlTypeMatch
argument_list|(
operator|(
name|RexCall
operator|)
name|right
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
condition|)
block|{
name|rightLiteral
operator|=
operator|(
name|RexLiteral
operator|)
operator|(
operator|(
name|RexCall
operator|)
name|right
operator|)
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
switch|switch
condition|(
name|left
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|INPUT_REF
case|:
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
comment|// filter out field does not show in index column
if|if
condition|(
operator|!
name|keyMeta
operator|.
name|getKeyColumnNames
argument_list|()
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
return|return
name|translateOp2
argument_list|(
name|op
argument_list|,
name|name
argument_list|,
name|rightLiteral
argument_list|,
name|originNode
argument_list|,
name|keyMeta
argument_list|)
return|;
case|case
name|CAST
case|:
return|return
name|translateBinary2
argument_list|(
name|op
argument_list|,
operator|(
operator|(
name|RexCall
operator|)
name|left
operator|)
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|right
argument_list|,
name|originNode
argument_list|,
name|keyMeta
argument_list|)
return|;
default|default:
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
comment|/**    * Combines a field name, operator, and literal to produce a predicate string.    */
specifier|private
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|translateOp2
parameter_list|(
name|String
name|op
parameter_list|,
name|String
name|name
parameter_list|,
name|RexLiteral
name|right
parameter_list|,
name|RexNode
name|originNode
parameter_list|,
name|KeyMeta
name|keyMeta
parameter_list|)
block|{
name|String
name|value
init|=
name|literalValue
argument_list|(
name|right
argument_list|)
decl_stmt|;
name|InternalRexNode
name|node
init|=
operator|new
name|InternalRexNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|node
operator|=
name|originNode
expr_stmt|;
name|node
operator|.
name|ordinalInKey
operator|=
name|keyMeta
operator|.
name|getKeyColumnNames
argument_list|()
operator|.
name|indexOf
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// For variable length column, Innodb-java-reader have a limitation,
comment|// left-prefix index length should be less than search value literal.
comment|// For example, we cannot leverage index of EMAIL(3) upon search value
comment|// `someone@apache.org`, because the value length is longer than 3.
if|if
condition|(
name|keyMeta
operator|.
name|getVarLen
argument_list|(
name|name
argument_list|)
operator|.
name|isPresent
argument_list|()
operator|&&
name|keyMeta
operator|.
name|getVarLen
argument_list|(
name|name
argument_list|)
operator|.
name|get
argument_list|()
operator|<
name|value
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
name|node
operator|.
name|fieldName
operator|=
name|name
expr_stmt|;
name|node
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|node
operator|.
name|right
operator|=
name|value
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|node
argument_list|)
return|;
block|}
comment|/**    * Converts the value of a literal to a string.    *    * @param literal Literal to translate    * @return String representation of the literal    */
specifier|private
specifier|static
name|String
name|literalValue
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
switch|switch
condition|(
name|literal
operator|.
name|getTypeName
argument_list|()
condition|)
block|{
case|case
name|DATE
case|:
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValueAs
argument_list|(
name|DateString
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValueAs
argument_list|(
name|TimestampString
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|TIME
case|:
case|case
name|TIME_WITH_LOCAL_TIME_ZONE
case|:
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValueAs
argument_list|(
name|TimeString
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|DECIMAL
case|:
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
default|default:
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValue2
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|findSubsequentMatches
parameter_list|(
name|List
argument_list|<
name|InternalRexNode
argument_list|>
name|nodes
parameter_list|,
name|int
name|numOfKeyColumns
parameter_list|,
name|Multimap
argument_list|<
name|Integer
argument_list|,
name|InternalRexNode
argument_list|>
name|keyOrdToNodesMap
parameter_list|,
name|String
name|op
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|nodes
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|numOfKeyColumns
condition|;
name|i
operator|++
control|)
block|{
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|eqOpNode
init|=
name|findFirstOp
argument_list|(
name|keyOrdToNodesMap
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|eqOpNode
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|eqOpNode
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|createKey
parameter_list|(
name|List
argument_list|<
name|InternalRexNode
argument_list|>
name|nodes
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|n
lambda|->
name|n
operator|.
name|right
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Finds first node from field expression nodes which match specific    * operations.    *    *<p>If not found, result is {@link Optional#empty()}.    */
specifier|private
name|Optional
argument_list|<
name|InternalRexNode
argument_list|>
name|findFirstOp
parameter_list|(
name|Collection
argument_list|<
name|InternalRexNode
argument_list|>
name|nodes
parameter_list|,
name|String
modifier|...
name|opList
parameter_list|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|nodes
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
for|for
control|(
name|InternalRexNode
name|node
range|:
name|nodes
control|)
block|{
for|for
control|(
name|String
name|op
range|:
name|opList
control|)
block|{
if|if
condition|(
name|op
operator|.
name|equals
argument_list|(
name|node
operator|.
name|op
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|nonForceIndexOrMatchForceIndexName
parameter_list|(
name|IndexCondition
name|indexCondition
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|forceIndexName
argument_list|)
operator|.
name|map
argument_list|(
name|indexCondition
operator|::
name|nameMatch
argument_list|)
operator|.
name|orElse
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/** Internal representation of a row expression. */
specifier|private
specifier|static
class|class
name|InternalRexNode
block|{
comment|/** Relation expression node. */
name|RexNode
name|node
decl_stmt|;
comment|/** Field ordinal in indexes. */
name|int
name|ordinalInKey
decl_stmt|;
comment|/** Field name. */
name|String
name|fieldName
decl_stmt|;
comment|/** Binary operation like =,>=,<=,> or<.*/
name|String
name|op
decl_stmt|;
comment|/** Binary operation right literal value. */
name|Object
name|right
decl_stmt|;
block|}
comment|/** Index condition comparator. */
specifier|static
class|class
name|IndexConditionComparator
implements|implements
name|Comparator
argument_list|<
name|IndexCondition
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|IndexCondition
name|o1
parameter_list|,
name|IndexCondition
name|o2
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getQueryType
argument_list|()
operator|.
name|priority
argument_list|()
argument_list|,
name|o2
operator|.
name|getQueryType
argument_list|()
operator|.
name|priority
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
name|boolean
name|isSqlTypeMatch
parameter_list|(
name|RexCall
name|rexCall
parameter_list|,
name|SqlTypeName
name|sqlTypeName
parameter_list|)
block|{
assert|assert
name|rexCall
operator|!=
literal|null
assert|;
return|return
name|rexCall
operator|.
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|sqlTypeName
return|;
block|}
block|}
end_class

end_unit
