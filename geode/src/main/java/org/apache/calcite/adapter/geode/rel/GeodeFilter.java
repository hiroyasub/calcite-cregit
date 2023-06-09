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
name|geode
operator|.
name|rel
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
name|calcite
operator|.
name|util
operator|.
name|Util
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
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Locale
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
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
operator|.
name|CHAR
import|;
end_import

begin_comment
comment|/**  * Implementation of  * {@link Filter} relational expression in Geode.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeFilter
extends|extends
name|Filter
implements|implements
name|GeodeRel
block|{
specifier|private
specifier|final
name|String
name|match
decl_stmt|;
name|GeodeFilter
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
name|RexNode
name|condition
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
name|condition
argument_list|)
expr_stmt|;
name|Translator
name|translator
init|=
operator|new
name|Translator
argument_list|(
name|getRowType
argument_list|()
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|match
operator|=
name|translator
operator|.
name|translateMatch
argument_list|(
name|condition
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|GeodeRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|input
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|0.1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|GeodeFilter
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
return|return
operator|new
name|GeodeFilter
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|condition
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|GeodeImplementContext
name|geodeImplementContext
parameter_list|)
block|{
comment|// first call the input down the tree.
name|geodeImplementContext
operator|.
name|visitChild
argument_list|(
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|geodeImplementContext
operator|.
name|addPredicates
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|match
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Translates {@link RexNode} expressions into Geode expression strings.    */
specifier|static
class|class
name|Translator
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|RexBuilder
name|rexBuilder
decl_stmt|;
name|Translator
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
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
name|GeodeRules
operator|.
name|geodeFieldNames
argument_list|(
name|rowType
argument_list|)
expr_stmt|;
block|}
comment|/**      * Converts the value of a literal to a string.      *      * @param literal Literal to translate      * @return String representation of the literal      */
specifier|private
specifier|static
name|String
name|literalValue
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
specifier|final
name|Comparable
name|valueComparable
init|=
name|literal
operator|.
name|getValueAs
argument_list|(
name|Comparable
operator|.
name|class
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|literal
operator|.
name|getTypeName
argument_list|()
condition|)
block|{
case|case
name|TIMESTAMP
case|:
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
assert|assert
name|valueComparable
operator|instanceof
name|TimestampString
assert|;
return|return
literal|"TIMESTAMP '"
operator|+
name|valueComparable
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
return|;
case|case
name|DATE
case|:
assert|assert
name|valueComparable
operator|instanceof
name|DateString
assert|;
return|return
literal|"DATE '"
operator|+
name|valueComparable
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
return|;
case|case
name|TIME
case|:
case|case
name|TIME_WITH_LOCAL_TIME_ZONE
case|:
assert|assert
name|valueComparable
operator|instanceof
name|TimeString
assert|;
return|return
literal|"TIME '"
operator|+
name|valueComparable
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
return|;
default|default:
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValue3
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Produce the OQL predicate string for the given condition.      *      * @param condition Condition to translate      * @return OQL predicate string      */
specifier|private
name|String
name|translateMatch
parameter_list|(
name|RexNode
name|condition
parameter_list|)
block|{
comment|// Remove SEARCH calls because current translation logic cannot handle it.
comment|// However, it would efficient to handle SEARCH explicitly; a Geode
comment|// 'IN SET' would always manifest as a SEARCH.
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
comment|// Returns condition decomposed by OR
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
name|condition2
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
return|return
name|translateOr
argument_list|(
name|disjunctions
argument_list|)
return|;
block|}
block|}
comment|/**      * Translate a conjunctive predicate to a OQL string.      *      * @param condition A conjunctive predicate      * @return OQL string for the predicate      */
specifier|private
name|String
name|translateAnd
parameter_list|(
name|RexNode
name|condition
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|predicates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|condition
argument_list|)
control|)
block|{
name|predicates
operator|.
name|add
argument_list|(
name|translateMatch2
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Util
operator|.
name|toString
argument_list|(
name|predicates
argument_list|,
literal|""
argument_list|,
literal|" AND "
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/** Returns the field name for the left node to use for {@code IN SET}      * query. */
specifier|private
name|String
name|getLeftNodeFieldName
parameter_list|(
name|RexNode
name|left
parameter_list|)
block|{
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
return|return
name|fieldNames
operator|.
name|get
argument_list|(
name|left1
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
case|case
name|CAST
case|:
comment|// FIXME This will not work in all cases (for example, we ignore string encoding)
return|return
name|getLeftNodeFieldName
argument_list|(
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
argument_list|)
return|;
case|case
name|ITEM
case|:
case|case
name|OTHER_FUNCTION
case|:
return|return
name|left
operator|.
name|accept
argument_list|(
operator|new
name|GeodeRules
operator|.
name|RexToGeodeTranslator
argument_list|(
name|this
operator|.
name|fieldNames
argument_list|)
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
comment|/** Returns whether we can use the {@code IN SET} query clause to      * improve query performance. */
specifier|private
name|boolean
name|useInSetQueryClause
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|disjunctions
parameter_list|)
block|{
comment|// Only use the in set for more than one disjunctions
if|if
condition|(
name|disjunctions
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|disjunctions
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|node
lambda|->
block|{
comment|// IN SET query can only be used for EQUALS
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
comment|// The right node should always be literal
if|if
condition|(
name|right
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|LITERAL
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|name
init|=
name|getLeftNodeFieldName
argument_list|(
name|left
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
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
argument_list|)
return|;
block|}
comment|/** Creates OQL {@code IN SET} predicate string. */
specifier|private
name|String
name|translateInSet
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|disjunctions
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|disjunctions
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"empty disjunctions"
argument_list|)
expr_stmt|;
name|RexNode
name|firstNode
init|=
name|disjunctions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RexCall
name|firstCall
init|=
operator|(
name|RexCall
operator|)
name|firstNode
decl_stmt|;
specifier|final
name|RexNode
name|left
init|=
name|firstCall
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|getLeftNodeFieldName
argument_list|(
name|left
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rightLiteralValueList
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|disjunctions
operator|.
name|forEach
argument_list|(
name|node
lambda|->
block|{
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
name|RexLiteral
name|rightLiteral
init|=
operator|(
name|RexLiteral
operator|)
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|rightLiteralValueList
operator|.
name|add
argument_list|(
name|quoteCharLiteral
argument_list|(
name|rightLiteral
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s IN SET(%s)"
argument_list|,
name|name
argument_list|,
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|rightLiteralValueList
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|getLeftNodeFieldNameForNode
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
specifier|final
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
return|return
name|getLeftNodeFieldName
argument_list|(
name|left
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|RexNode
argument_list|>
name|getLeftNodeDisjunctions
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|disjunctions
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|leftNodeDisjunctions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|leftNodeFieldName
init|=
name|getLeftNodeFieldNameForNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftNodeFieldName
operator|!=
literal|null
condition|)
block|{
name|leftNodeDisjunctions
operator|=
name|disjunctions
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|rexNode
lambda|->
block|{
name|RexCall
name|rexCall
init|=
operator|(
name|RexCall
operator|)
name|rexNode
decl_stmt|;
name|RexNode
name|rexCallLeft
init|=
name|rexCall
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|leftNodeFieldName
operator|.
name|equals
argument_list|(
name|getLeftNodeFieldName
argument_list|(
name|rexCallLeft
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|leftNodeDisjunctions
return|;
block|}
specifier|private
name|String
name|translateOr
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|disjunctions
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|predicates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|leftFieldNameList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|inSetLeftFieldNameList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|disjunctions
control|)
block|{
specifier|final
name|String
name|leftNodeFieldName
init|=
name|getLeftNodeFieldNameForNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|// If any one left node is processed with IN SET predicate
comment|// all the nodes are already handled
if|if
condition|(
name|inSetLeftFieldNameList
operator|.
name|contains
argument_list|(
name|leftNodeFieldName
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|List
argument_list|<
name|RexNode
argument_list|>
name|leftNodeDisjunctions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|useInSetQueryClause
init|=
literal|false
decl_stmt|;
comment|// In case the left field node name is already processed and not applicable
comment|// for IN SET query clause, we can skip the checking
if|if
condition|(
operator|!
name|leftFieldNameList
operator|.
name|contains
argument_list|(
name|leftNodeFieldName
argument_list|)
condition|)
block|{
name|leftNodeDisjunctions
operator|=
name|getLeftNodeDisjunctions
argument_list|(
name|node
argument_list|,
name|disjunctions
argument_list|)
expr_stmt|;
name|useInSetQueryClause
operator|=
name|useInSetQueryClause
argument_list|(
name|leftNodeDisjunctions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useInSetQueryClause
condition|)
block|{
name|predicates
operator|.
name|add
argument_list|(
name|translateInSet
argument_list|(
name|leftNodeDisjunctions
argument_list|)
argument_list|)
expr_stmt|;
name|inSetLeftFieldNameList
operator|.
name|add
argument_list|(
name|leftNodeFieldName
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|node
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|predicates
operator|.
name|add
argument_list|(
literal|"("
operator|+
name|translateMatch
argument_list|(
name|node
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|predicates
operator|.
name|add
argument_list|(
name|translateMatch2
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|leftFieldNameList
operator|.
name|add
argument_list|(
name|leftNodeFieldName
argument_list|)
expr_stmt|;
block|}
return|return
name|Util
operator|.
name|toString
argument_list|(
name|predicates
argument_list|,
literal|""
argument_list|,
literal|" OR "
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**      * Translate a binary relation.      */
specifier|private
name|String
name|translateMatch2
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
comment|// We currently only use equality, but inequalities on clustering keys
comment|// should be possible in the future
name|RexNode
name|child
decl_stmt|;
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
argument_list|)
return|;
case|case
name|INPUT_REF
case|:
return|return
name|translateBinary2
argument_list|(
literal|"="
argument_list|,
name|node
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|)
return|;
case|case
name|NOT
case|:
name|child
operator|=
operator|(
operator|(
name|RexCall
operator|)
name|node
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
if|if
condition|(
name|child
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|CAST
condition|)
block|{
name|child
operator|=
operator|(
operator|(
name|RexCall
operator|)
name|child
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
name|child
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|INPUT_REF
condition|)
block|{
return|return
name|translateBinary2
argument_list|(
literal|"="
argument_list|,
name|child
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
argument_list|)
return|;
block|}
break|break;
case|case
name|CAST
case|:
return|return
name|translateMatch2
argument_list|(
operator|(
operator|(
name|RexCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
default|default:
break|break;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Cannot translate "
operator|+
name|node
operator|+
literal|", kind="
operator|+
name|node
operator|.
name|getKind
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Translates a call to a binary operator, reversing arguments if      * necessary.      */
specifier|private
name|String
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
name|String
name|expression
init|=
name|translateBinary2
argument_list|(
name|op
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
name|expression
operator|!=
literal|null
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
argument_list|)
expr_stmt|;
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
return|return
name|expression
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"cannot translate op "
operator|+
name|op
operator|+
literal|" call "
operator|+
name|call
argument_list|)
throw|;
block|}
comment|/**      * Translates a call to a binary operator. Returns null on failure.      */
specifier|private
name|String
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
parameter_list|)
block|{
switch|switch
condition|(
name|right
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|LITERAL
case|:
break|break;
default|default:
return|return
literal|null
return|;
block|}
specifier|final
name|RexLiteral
name|rightLiteral
init|=
operator|(
name|RexLiteral
operator|)
name|right
decl_stmt|;
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
return|return
name|translateOp2
argument_list|(
name|op
argument_list|,
name|name
argument_list|,
name|rightLiteral
argument_list|)
return|;
case|case
name|CAST
case|:
comment|// FIXME This will not work in all cases (for example, we ignore string encoding)
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
argument_list|)
return|;
case|case
name|ITEM
case|:
name|String
name|item
init|=
name|left
operator|.
name|accept
argument_list|(
operator|new
name|GeodeRules
operator|.
name|RexToGeodeTranslator
argument_list|(
name|this
operator|.
name|fieldNames
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|item
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|item
operator|+
literal|" "
operator|+
name|op
operator|+
literal|" "
operator|+
name|quoteCharLiteral
argument_list|(
name|rightLiteral
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|quoteCharLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
name|String
name|value
init|=
name|literalValue
argument_list|(
name|literal
argument_list|)
decl_stmt|;
if|if
condition|(
name|literal
operator|.
name|getTypeName
argument_list|()
operator|==
name|CHAR
condition|)
block|{
name|value
operator|=
literal|"'"
operator|+
name|value
operator|+
literal|"'"
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * Combines a field name, operator, and literal to produce a predicate string.      */
specifier|private
specifier|static
name|String
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
parameter_list|)
block|{
name|String
name|valueString
init|=
name|quoteCharLiteral
argument_list|(
name|right
argument_list|)
decl_stmt|;
return|return
name|name
operator|+
literal|" "
operator|+
name|op
operator|+
literal|" "
operator|+
name|valueString
return|;
block|}
block|}
block|}
end_class

end_unit

