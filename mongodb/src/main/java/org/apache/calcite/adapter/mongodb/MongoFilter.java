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
name|mongodb
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
name|util
operator|.
name|JsonBuilder
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
name|LinkedHashMap
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

begin_comment
comment|/**  * Implementation of a {@link org.apache.calcite.rel.core.Filter}  * relational expression in MongoDB.  */
end_comment

begin_class
specifier|public
class|class
name|MongoFilter
extends|extends
name|Filter
implements|implements
name|MongoRel
block|{
specifier|public
name|MongoFilter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
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
name|child
argument_list|,
name|condition
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|MongoRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|child
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
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
specifier|public
name|MongoFilter
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
name|MongoFilter
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
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|Translator
name|translator
init|=
operator|new
name|Translator
argument_list|(
name|implementor
operator|.
name|rexBuilder
argument_list|,
name|MongoRules
operator|.
name|mongoFieldNames
argument_list|(
name|getRowType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|match
init|=
name|translator
operator|.
name|translateMatch
argument_list|(
name|condition
argument_list|)
decl_stmt|;
name|implementor
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
comment|/** Translates {@link RexNode} expressions into MongoDB expression strings. */
specifier|static
class|class
name|Translator
block|{
specifier|final
name|JsonBuilder
name|builder
init|=
operator|new
name|JsonBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Multimap
argument_list|<
name|String
argument_list|,
name|Pair
argument_list|<
name|String
argument_list|,
name|RexLiteral
argument_list|>
argument_list|>
name|multimap
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RexLiteral
argument_list|>
name|eqMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
name|Translator
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
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
name|fieldNames
expr_stmt|;
block|}
specifier|private
name|String
name|translateMatch
parameter_list|(
name|RexNode
name|condition
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|builder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"$match"
argument_list|,
name|translateOr
argument_list|(
name|condition
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toJsonString
argument_list|(
name|map
argument_list|)
return|;
block|}
specifier|private
name|Object
name|translateOr
parameter_list|(
name|RexNode
name|condition
parameter_list|)
block|{
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
name|List
argument_list|<
name|Object
argument_list|>
name|list
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
name|disjunctions
argument_list|(
name|condition2
argument_list|)
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|translateAnd
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|builder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"$or"
argument_list|,
name|list
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
block|}
comment|/** Translates a condition that may be an AND of other conditions. Gathers      * together conditions that apply to the same field. */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|translateAnd
parameter_list|(
name|RexNode
name|node0
parameter_list|)
block|{
name|eqMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|multimap
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|node0
argument_list|)
control|)
block|{
name|translateMatch2
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|builder
operator|.
name|map
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RexLiteral
argument_list|>
name|entry
range|:
name|eqMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|multimap
operator|.
name|removeAll
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|literalValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RexLiteral
argument_list|>
argument_list|>
argument_list|>
name|entry
range|:
name|multimap
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map2
init|=
name|builder
operator|.
name|map
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|RexLiteral
argument_list|>
name|s
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|addPredicate
argument_list|(
name|map2
argument_list|,
name|s
operator|.
name|left
argument_list|,
name|literalValue
argument_list|(
name|s
operator|.
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|map2
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|private
name|void
name|addPredicate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|,
name|String
name|op
parameter_list|,
name|Object
name|v
parameter_list|)
block|{
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|op
argument_list|)
operator|&&
name|stronger
argument_list|(
name|op
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|op
argument_list|)
argument_list|,
name|v
argument_list|)
condition|)
block|{
return|return;
block|}
name|map
operator|.
name|put
argument_list|(
name|op
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
comment|/** Returns whether {@code v0} is a stronger value for operator {@code key}      * than {@code v1}.      *      *<p>For example, {@code stronger("$lt", 100, 200)} returns true, because      * "&lt; 100" is a more powerful condition than "&lt; 200".      */
specifier|private
name|boolean
name|stronger
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|v0
parameter_list|,
name|Object
name|v1
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"$lt"
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
literal|"$lte"
argument_list|)
condition|)
block|{
if|if
condition|(
name|v0
operator|instanceof
name|Number
operator|&&
name|v1
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|v0
operator|)
operator|.
name|doubleValue
argument_list|()
operator|<
operator|(
operator|(
name|Number
operator|)
name|v1
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
if|if
condition|(
name|v0
operator|instanceof
name|String
operator|&&
name|v1
operator|instanceof
name|String
condition|)
block|{
return|return
name|v0
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|v1
operator|.
name|toString
argument_list|()
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"$gt"
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
literal|"$gte"
argument_list|)
condition|)
block|{
return|return
name|stronger
argument_list|(
literal|"$lt"
argument_list|,
name|v1
argument_list|,
name|v0
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|Object
name|literalValue
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
return|return
name|literal
operator|.
name|getValue2
argument_list|()
return|;
block|}
specifier|private
name|Void
name|translateMatch2
parameter_list|(
name|RexNode
name|node
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
literal|null
argument_list|,
literal|null
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
literal|"$lt"
argument_list|,
literal|"$gt"
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
literal|"$lte"
argument_list|,
literal|"$gte"
argument_list|,
operator|(
name|RexCall
operator|)
name|node
argument_list|)
return|;
case|case
name|NOT_EQUALS
case|:
return|return
name|translateBinary
argument_list|(
literal|"$ne"
argument_list|,
literal|"$ne"
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
literal|"$gt"
argument_list|,
literal|"$lt"
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
literal|"$gte"
argument_list|,
literal|"$lte"
argument_list|,
operator|(
name|RexCall
operator|)
name|node
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"cannot translate "
operator|+
name|node
argument_list|)
throw|;
block|}
block|}
comment|/** Translates a call to a binary operator, reversing arguments if      * necessary. */
specifier|private
name|Void
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
name|boolean
name|b
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
name|b
condition|)
block|{
return|return
literal|null
return|;
block|}
name|b
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
name|b
condition|)
block|{
return|return
literal|null
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
comment|/** Translates a call to a binary operator. Returns whether successful. */
specifier|private
name|boolean
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
literal|false
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
name|translateOp2
argument_list|(
name|op
argument_list|,
name|name
argument_list|,
name|rightLiteral
argument_list|)
expr_stmt|;
return|return
literal|true
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
argument_list|)
return|;
case|case
name|ITEM
case|:
name|String
name|itemName
init|=
name|MongoRules
operator|.
name|isItem
argument_list|(
operator|(
name|RexCall
operator|)
name|left
argument_list|)
decl_stmt|;
if|if
condition|(
name|itemName
operator|!=
literal|null
condition|)
block|{
name|translateOp2
argument_list|(
name|op
argument_list|,
name|itemName
argument_list|,
name|rightLiteral
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// fall through
default|default:
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|void
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
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
comment|// E.g.: {deptno: 100}
name|eqMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// E.g. {deptno: {$lt: 100}}
comment|// which may later be combined with other conditions:
comment|// E.g. {deptno: [$lt: 100, $gt: 50]}
name|multimap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
name|op
argument_list|,
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

