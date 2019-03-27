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
name|externalize
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
name|RelDistribution
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
name|RelInput
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
name|schema
operator|.
name|Schema
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
name|SqlAggFunction
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
name|Pair
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
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|type
operator|.
name|TypeReference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|DeserializationFeature
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|Locale
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
comment|/**  * Reads a JSON plan and converts it back to a tree of relational expressions.  *  * @see org.apache.calcite.rel.RelInput  */
end_comment

begin_class
specifier|public
class|class
name|RelJsonReader
block|{
specifier|private
specifier|static
specifier|final
name|TypeReference
argument_list|<
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|TYPE_REF
init|=
operator|new
name|TypeReference
argument_list|<
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{       }
decl_stmt|;
specifier|private
specifier|final
name|RelOptCluster
name|cluster
decl_stmt|;
specifier|private
specifier|final
name|RelOptSchema
name|relOptSchema
decl_stmt|;
specifier|private
specifier|final
name|RelJson
name|relJson
init|=
operator|new
name|RelJson
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelNode
argument_list|>
name|relMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|RelNode
name|lastRel
decl_stmt|;
specifier|public
name|RelJsonReader
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|Schema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|this
operator|.
name|relOptSchema
operator|=
name|relOptSchema
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|schema
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|read
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|lastRel
operator|=
literal|null
expr_stmt|;
specifier|final
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|o
init|=
name|mapper
operator|.
name|configure
argument_list|(
name|DeserializationFeature
operator|.
name|USE_BIG_DECIMAL_FOR_FLOATS
argument_list|,
literal|true
argument_list|)
operator|.
name|readValue
argument_list|(
name|s
argument_list|,
name|TYPE_REF
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rels
init|=
operator|(
name|List
operator|)
name|o
operator|.
name|get
argument_list|(
literal|"rels"
argument_list|)
decl_stmt|;
name|readRels
argument_list|(
name|rels
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|lastRel
argument_list|)
expr_stmt|;
return|return
name|lastRel
return|;
block|}
specifier|private
name|void
name|readRels
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|jsonRels
parameter_list|)
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonRel
range|:
name|jsonRels
control|)
block|{
name|readRel
argument_list|(
name|jsonRel
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|readRel
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonRel
parameter_list|)
block|{
name|String
name|id
init|=
operator|(
name|String
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|type
init|=
operator|(
name|String
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
literal|"relOp"
argument_list|)
decl_stmt|;
name|Constructor
name|constructor
init|=
name|relJson
operator|.
name|getConstructor
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|RelInput
name|input
init|=
operator|new
name|RelInput
argument_list|()
block|{
specifier|public
name|RelOptCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
specifier|public
name|RelTraitSet
name|getTraitSet
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
return|;
block|}
specifier|public
name|RelOptTable
name|getTable
parameter_list|(
name|String
name|table
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|getStringList
argument_list|(
name|table
argument_list|)
decl_stmt|;
return|return
name|relOptSchema
operator|.
name|getTableForMember
argument_list|(
name|list
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|getInput
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|getInputs
argument_list|()
decl_stmt|;
assert|assert
name|inputs
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|jsonInputs
init|=
name|getStringList
argument_list|(
literal|"inputs"
argument_list|)
decl_stmt|;
if|if
condition|(
name|jsonInputs
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|lastRel
argument_list|)
return|;
block|}
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|jsonInput
range|:
name|jsonInputs
control|)
block|{
name|inputs
operator|.
name|add
argument_list|(
name|lookupInput
argument_list|(
name|jsonInput
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|inputs
return|;
block|}
specifier|public
name|RexNode
name|getExpression
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
return|return
name|relJson
operator|.
name|toRex
argument_list|(
name|this
argument_list|,
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableBitSet
name|getBitSet
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
return|return
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|getIntegerList
argument_list|(
name|tag
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getBitSetList
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|list
init|=
name|getIntegerListList
argument_list|(
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|ImmutableBitSet
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|Integer
argument_list|>
name|integers
range|:
name|list
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|integers
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStringList
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getIntegerList
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|Integer
argument_list|>
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|getIntegerListList
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|getAggregateCalls
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|jsonAggs
init|=
operator|(
name|List
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|inputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonAggCall
range|:
name|jsonAggs
control|)
block|{
name|inputs
operator|.
name|add
argument_list|(
name|toAggCall
argument_list|(
name|this
argument_list|,
name|jsonAggCall
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|inputs
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
return|return
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
return|;
block|}
specifier|public
name|String
name|getString
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
return|;
block|}
specifier|public
name|float
name|getFloat
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
operator|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|(
name|String
name|tag
parameter_list|,
name|boolean
name|default_
parameter_list|)
block|{
specifier|final
name|Boolean
name|b
init|=
operator|(
name|Boolean
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
return|return
name|b
operator|!=
literal|null
condition|?
name|b
else|:
name|default_
return|;
block|}
specifier|public
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|E
name|getEnum
parameter_list|(
name|String
name|tag
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|)
block|{
return|return
name|Util
operator|.
name|enumVal
argument_list|(
name|enumClass
argument_list|,
name|getString
argument_list|(
name|tag
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getExpressionList
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|jsonNodes
init|=
operator|(
name|List
operator|)
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|jsonNode
range|:
name|jsonNodes
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|relJson
operator|.
name|toRex
argument_list|(
name|this
argument_list|,
name|jsonNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
specifier|final
name|Object
name|o
init|=
name|jsonRel
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
return|return
name|relJson
operator|.
name|toType
argument_list|(
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|o
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|String
name|expressionsTag
parameter_list|,
name|String
name|fieldsTag
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|expressionList
init|=
name|getExpressionList
argument_list|(
name|expressionsTag
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|get
argument_list|(
name|fieldsTag
argument_list|)
decl_stmt|;
return|return
name|cluster
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
operator|new
name|AbstractList
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
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|,
name|expressionList
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|names
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|RelCollation
name|getCollation
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
name|relJson
operator|.
name|toCollation
argument_list|(
operator|(
name|List
operator|)
name|get
argument_list|(
literal|"collation"
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|getDistribution
parameter_list|()
block|{
return|return
name|relJson
operator|.
name|toDistribution
argument_list|(
name|get
argument_list|(
literal|"distribution"
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|getTuples
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
comment|//noinspection unchecked
specifier|final
name|List
argument_list|<
name|List
argument_list|>
name|jsonTuples
init|=
operator|(
name|List
operator|)
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|List
name|jsonTuple
range|:
name|jsonTuples
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|getTuple
argument_list|(
name|jsonTuple
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|getTuple
parameter_list|(
name|List
name|jsonTuple
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexLiteral
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|jsonValue
range|:
name|jsonTuple
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|(
name|RexLiteral
operator|)
name|relJson
operator|.
name|toRex
argument_list|(
name|this
argument_list|,
name|jsonValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
specifier|final
name|RelNode
name|rel
init|=
operator|(
name|RelNode
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|relMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|rel
argument_list|)
expr_stmt|;
name|lastRel
operator|=
name|rel
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
specifier|final
name|Throwable
name|e2
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|e2
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e2
throw|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e2
argument_list|)
throw|;
block|}
block|}
specifier|private
name|AggregateCall
name|toAggCall
parameter_list|(
name|RelInput
name|relInput
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonAggCall
parameter_list|)
block|{
specifier|final
name|String
name|aggName
init|=
operator|(
name|String
operator|)
name|jsonAggCall
operator|.
name|get
argument_list|(
literal|"agg"
argument_list|)
decl_stmt|;
specifier|final
name|SqlAggFunction
name|aggregation
init|=
name|relJson
operator|.
name|toAggregation
argument_list|(
name|relInput
argument_list|,
name|aggName
argument_list|,
name|jsonAggCall
argument_list|)
decl_stmt|;
specifier|final
name|Boolean
name|distinct
init|=
operator|(
name|Boolean
operator|)
name|jsonAggCall
operator|.
name|get
argument_list|(
literal|"distinct"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|operands
init|=
operator|(
name|List
argument_list|<
name|Integer
argument_list|>
operator|)
name|jsonAggCall
operator|.
name|get
argument_list|(
literal|"operands"
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|filterOperand
init|=
operator|(
name|Integer
operator|)
name|jsonAggCall
operator|.
name|get
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|relJson
operator|.
name|toType
argument_list|(
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|jsonAggCall
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|aggregation
argument_list|,
name|distinct
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|operands
argument_list|,
name|filterOperand
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|filterOperand
argument_list|,
name|RelCollations
operator|.
name|EMPTY
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|RelNode
name|lookupInput
parameter_list|(
name|String
name|jsonInput
parameter_list|)
block|{
name|RelNode
name|node
init|=
name|relMap
operator|.
name|get
argument_list|(
name|jsonInput
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown id "
operator|+
name|jsonInput
operator|+
literal|" for relational expression"
argument_list|)
throw|;
block|}
return|return
name|node
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelJsonReader.java
end_comment

end_unit

