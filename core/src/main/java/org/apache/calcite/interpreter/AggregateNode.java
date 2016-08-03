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
name|interpreter
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
name|DataContext
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
name|AggAddContext
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
name|AggImpState
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
name|JavaRowFormat
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
name|PhysType
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
name|PhysTypeImpl
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
name|RexToLixTranslator
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
name|impl
operator|.
name|AggAddContextImpl
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
name|interpreter
operator|.
name|Row
operator|.
name|RowBuilder
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
name|linq4j
operator|.
name|tree
operator|.
name|BlockBuilder
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|linq4j
operator|.
name|tree
operator|.
name|ParameterExpression
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
name|schema
operator|.
name|impl
operator|.
name|AggregateFunctionImpl
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|Throwables
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
name|Maps
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
comment|/**  * Interpreter node that implements an  * {@link org.apache.calcite.rel.core.Aggregate}.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateNode
extends|extends
name|AbstractSingleNode
argument_list|<
name|Aggregate
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Grouping
argument_list|>
name|groups
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ImmutableBitSet
name|unionGroups
decl_stmt|;
specifier|private
specifier|final
name|int
name|outputRowLength
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|AccumulatorFactory
argument_list|>
name|accumulatorFactories
decl_stmt|;
specifier|private
specifier|final
name|DataContext
name|dataContext
decl_stmt|;
specifier|public
name|AggregateNode
parameter_list|(
name|Interpreter
name|interpreter
parameter_list|,
name|Aggregate
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|interpreter
argument_list|,
name|rel
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataContext
operator|=
name|interpreter
operator|.
name|getDataContext
argument_list|()
expr_stmt|;
name|ImmutableBitSet
name|union
init|=
name|ImmutableBitSet
operator|.
name|of
argument_list|()
decl_stmt|;
if|if
condition|(
name|rel
operator|.
name|getGroupSets
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ImmutableBitSet
name|group
range|:
name|rel
operator|.
name|getGroupSets
argument_list|()
control|)
block|{
name|union
operator|=
name|union
operator|.
name|union
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
operator|new
name|Grouping
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|unionGroups
operator|=
name|union
expr_stmt|;
name|this
operator|.
name|outputRowLength
operator|=
name|unionGroups
operator|.
name|cardinality
argument_list|()
operator|+
operator|(
name|rel
operator|.
name|indicator
condition|?
name|unionGroups
operator|.
name|cardinality
argument_list|()
else|:
literal|0
operator|)
operator|+
name|rel
operator|.
name|getAggCallList
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AccumulatorFactory
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
name|AggregateCall
name|aggregateCall
range|:
name|rel
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|getAccumulator
argument_list|(
name|aggregateCall
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|accumulatorFactories
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Row
name|r
decl_stmt|;
while|while
condition|(
operator|(
name|r
operator|=
name|source
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Grouping
name|group
range|:
name|groups
control|)
block|{
name|group
operator|.
name|send
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Grouping
name|group
range|:
name|groups
control|)
block|{
name|group
operator|.
name|end
argument_list|(
name|sink
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|AccumulatorFactory
name|getAccumulator
parameter_list|(
specifier|final
name|AggregateCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|getAggregation
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|COUNT
condition|)
block|{
return|return
operator|new
name|AccumulatorFactory
argument_list|()
block|{
specifier|public
name|Accumulator
name|get
parameter_list|()
block|{
return|return
operator|new
name|CountAccumulator
argument_list|(
name|call
argument_list|)
return|;
block|}
block|}
return|;
block|}
if|else if
condition|(
name|call
operator|.
name|getAggregation
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|SUM
condition|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
decl_stmt|;
switch|switch
condition|(
name|call
operator|.
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|DOUBLE
case|:
case|case
name|REAL
case|:
case|case
name|FLOAT
case|:
name|clazz
operator|=
name|DoubleSum
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
name|clazz
operator|=
name|IntSum
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|BIGINT
case|:
default|default:
name|clazz
operator|=
name|LongSum
operator|.
name|class
expr_stmt|;
break|break;
block|}
return|return
operator|new
name|UdaAccumulatorFactory
argument_list|(
name|AggregateFunctionImpl
operator|.
name|create
argument_list|(
name|clazz
argument_list|)
argument_list|,
name|call
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|int
name|stateOffset
init|=
literal|0
decl_stmt|;
specifier|final
name|AggImpState
name|agg
init|=
operator|new
name|AggImpState
argument_list|(
literal|0
argument_list|,
name|call
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|stateSize
init|=
name|agg
operator|.
name|state
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|BlockBuilder
name|builder2
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|PhysType
name|inputPhysType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|typeFactory
argument_list|,
name|rel
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|JavaRowFormat
operator|.
name|ARRAY
argument_list|)
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|FieldInfoBuilder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Expression
name|expression
range|:
name|agg
operator|.
name|state
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
name|typeFactory
operator|.
name|createJavaType
argument_list|(
operator|(
name|Class
operator|)
name|expression
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PhysType
name|accPhysType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|typeFactory
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|,
name|JavaRowFormat
operator|.
name|ARRAY
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|inParameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|inputPhysType
operator|.
name|getJavaRowType
argument_list|()
argument_list|,
literal|"in"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|acc_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|accPhysType
operator|.
name|getJavaRowType
argument_list|()
argument_list|,
literal|"acc"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|accumulator
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|stateSize
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|stateSize
condition|;
name|j
operator|++
control|)
block|{
name|accumulator
operator|.
name|add
argument_list|(
name|accPhysType
operator|.
name|fieldReference
argument_list|(
name|acc_
argument_list|,
name|j
operator|+
name|stateOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|agg
operator|.
name|state
operator|=
name|accumulator
expr_stmt|;
name|AggAddContext
name|addContext
init|=
operator|new
name|AggAddContextImpl
argument_list|(
name|builder2
argument_list|,
name|accumulator
argument_list|)
block|{
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexArguments
parameter_list|()
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
range|:
name|agg
operator|.
name|call
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|args
operator|.
name|add
argument_list|(
name|RexInputRef
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|inputPhysType
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|args
return|;
block|}
specifier|public
name|RexNode
name|rexFilterArgument
parameter_list|()
block|{
return|return
name|agg
operator|.
name|call
operator|.
name|filterArg
operator|<
literal|0
condition|?
literal|null
else|:
name|RexInputRef
operator|.
name|of
argument_list|(
name|agg
operator|.
name|call
operator|.
name|filterArg
argument_list|,
name|inputPhysType
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RexToLixTranslator
name|rowTranslator
parameter_list|()
block|{
return|return
name|RexToLixTranslator
operator|.
name|forAggregation
argument_list|(
name|typeFactory
argument_list|,
name|currentBlock
argument_list|()
argument_list|,
operator|new
name|RexToLixTranslator
operator|.
name|InputGetterImpl
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|Expression
operator|)
name|inParameter
argument_list|,
name|inputPhysType
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setNullable
argument_list|(
name|currentNullables
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|agg
operator|.
name|implementor
operator|.
name|implementAdd
argument_list|(
name|agg
operator|.
name|context
argument_list|,
name|addContext
argument_list|)
expr_stmt|;
specifier|final
name|ParameterExpression
name|context_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Context
operator|.
name|class
argument_list|,
literal|"context"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|outputValues_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|,
literal|"outputValues"
argument_list|)
decl_stmt|;
name|Scalar
name|addScalar
init|=
name|JaninoRexCompiler
operator|.
name|baz
argument_list|(
name|context_
argument_list|,
name|outputValues_
argument_list|,
name|builder2
operator|.
name|toBlock
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScalarAccumulatorDef
argument_list|(
literal|null
argument_list|,
name|addScalar
argument_list|,
literal|null
argument_list|,
name|rel
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|stateSize
argument_list|,
name|dataContext
argument_list|)
return|;
block|}
block|}
comment|/** Accumulator for calls to the COUNT function. */
specifier|private
specifier|static
class|class
name|CountAccumulator
implements|implements
name|Accumulator
block|{
specifier|private
specifier|final
name|AggregateCall
name|call
decl_stmt|;
name|long
name|cnt
decl_stmt|;
name|CountAccumulator
parameter_list|(
name|AggregateCall
name|call
parameter_list|)
block|{
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
name|cnt
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Row
name|row
parameter_list|)
block|{
name|boolean
name|notNull
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|call
operator|.
name|getArgList
argument_list|()
control|)
block|{
if|if
condition|(
name|row
operator|.
name|getObject
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
name|notNull
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|notNull
condition|)
block|{
name|cnt
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|Object
name|end
parameter_list|()
block|{
return|return
name|cnt
return|;
block|}
block|}
comment|/** Creates an {@link Accumulator}. */
specifier|private
interface|interface
name|AccumulatorFactory
extends|extends
name|Supplier
argument_list|<
name|Accumulator
argument_list|>
block|{   }
comment|/** Accumulator powered by {@link Scalar} code fragments. */
specifier|private
specifier|static
class|class
name|ScalarAccumulatorDef
implements|implements
name|AccumulatorFactory
block|{
specifier|final
name|Scalar
name|initScalar
decl_stmt|;
specifier|final
name|Scalar
name|addScalar
decl_stmt|;
specifier|final
name|Scalar
name|endScalar
decl_stmt|;
specifier|final
name|Context
name|sendContext
decl_stmt|;
specifier|final
name|Context
name|endContext
decl_stmt|;
specifier|final
name|int
name|rowLength
decl_stmt|;
specifier|final
name|int
name|accumulatorLength
decl_stmt|;
specifier|private
name|ScalarAccumulatorDef
parameter_list|(
name|Scalar
name|initScalar
parameter_list|,
name|Scalar
name|addScalar
parameter_list|,
name|Scalar
name|endScalar
parameter_list|,
name|int
name|rowLength
parameter_list|,
name|int
name|accumulatorLength
parameter_list|,
name|DataContext
name|root
parameter_list|)
block|{
name|this
operator|.
name|initScalar
operator|=
name|initScalar
expr_stmt|;
name|this
operator|.
name|addScalar
operator|=
name|addScalar
expr_stmt|;
name|this
operator|.
name|endScalar
operator|=
name|endScalar
expr_stmt|;
name|this
operator|.
name|accumulatorLength
operator|=
name|accumulatorLength
expr_stmt|;
name|this
operator|.
name|rowLength
operator|=
name|rowLength
expr_stmt|;
name|this
operator|.
name|sendContext
operator|=
operator|new
name|Context
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|sendContext
operator|.
name|values
operator|=
operator|new
name|Object
index|[
name|rowLength
operator|+
name|accumulatorLength
index|]
expr_stmt|;
name|this
operator|.
name|endContext
operator|=
operator|new
name|Context
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|endContext
operator|.
name|values
operator|=
operator|new
name|Object
index|[
name|accumulatorLength
index|]
expr_stmt|;
block|}
specifier|public
name|Accumulator
name|get
parameter_list|()
block|{
return|return
operator|new
name|ScalarAccumulator
argument_list|(
name|this
argument_list|,
operator|new
name|Object
index|[
name|accumulatorLength
index|]
argument_list|)
return|;
block|}
block|}
comment|/** Accumulator powered by {@link Scalar} code fragments. */
specifier|private
specifier|static
class|class
name|ScalarAccumulator
implements|implements
name|Accumulator
block|{
specifier|final
name|ScalarAccumulatorDef
name|def
decl_stmt|;
specifier|final
name|Object
index|[]
name|values
decl_stmt|;
specifier|private
name|ScalarAccumulator
parameter_list|(
name|ScalarAccumulatorDef
name|def
parameter_list|,
name|Object
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|def
operator|=
name|def
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Row
name|row
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|row
operator|.
name|getValues
argument_list|()
argument_list|,
literal|0
argument_list|,
name|def
operator|.
name|sendContext
operator|.
name|values
argument_list|,
literal|0
argument_list|,
name|def
operator|.
name|rowLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|def
operator|.
name|sendContext
operator|.
name|values
argument_list|,
name|def
operator|.
name|rowLength
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|def
operator|.
name|addScalar
operator|.
name|execute
argument_list|(
name|def
operator|.
name|sendContext
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|end
parameter_list|()
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|def
operator|.
name|endContext
operator|.
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|def
operator|.
name|endScalar
operator|.
name|execute
argument_list|(
name|def
operator|.
name|endContext
argument_list|)
return|;
block|}
block|}
comment|/**    * Internal class to track groupings.    */
specifier|private
class|class
name|Grouping
block|{
specifier|private
specifier|final
name|ImmutableBitSet
name|grouping
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Row
argument_list|,
name|AccumulatorList
argument_list|>
name|accumulators
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Grouping
parameter_list|(
name|ImmutableBitSet
name|grouping
parameter_list|)
block|{
name|this
operator|.
name|grouping
operator|=
name|grouping
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Row
name|row
parameter_list|)
block|{
comment|// TODO: fix the size of this row.
name|RowBuilder
name|builder
init|=
name|Row
operator|.
name|newBuilder
argument_list|(
name|grouping
operator|.
name|cardinality
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|grouping
control|)
block|{
name|builder
operator|.
name|set
argument_list|(
name|j
operator|++
argument_list|,
name|row
operator|.
name|getObject
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Row
name|key
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|accumulators
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|AccumulatorList
name|list
init|=
operator|new
name|AccumulatorList
argument_list|()
decl_stmt|;
for|for
control|(
name|AccumulatorFactory
name|factory
range|:
name|accumulatorFactories
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|factory
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|accumulators
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|accumulators
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|send
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|end
parameter_list|(
name|Sink
name|sink
parameter_list|)
throws|throws
name|InterruptedException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Row
argument_list|,
name|AccumulatorList
argument_list|>
name|e
range|:
name|accumulators
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Row
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|AccumulatorList
name|list
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|RowBuilder
name|rb
init|=
name|Row
operator|.
name|newBuilder
argument_list|(
name|outputRowLength
argument_list|)
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Integer
name|groupPos
range|:
name|unionGroups
control|)
block|{
if|if
condition|(
name|grouping
operator|.
name|get
argument_list|(
name|groupPos
argument_list|)
condition|)
block|{
name|rb
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|key
operator|.
name|getObject
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rel
operator|.
name|indicator
condition|)
block|{
name|rb
operator|.
name|set
argument_list|(
name|unionGroups
operator|.
name|cardinality
argument_list|()
operator|+
name|index
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// need to set false when not part of grouping set.
name|index
operator|++
expr_stmt|;
block|}
name|list
operator|.
name|end
argument_list|(
name|rb
argument_list|)
expr_stmt|;
name|sink
operator|.
name|send
argument_list|(
name|rb
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * A list of accumulators used during grouping.    */
specifier|private
class|class
name|AccumulatorList
extends|extends
name|ArrayList
argument_list|<
name|Accumulator
argument_list|>
block|{
specifier|public
name|void
name|send
parameter_list|(
name|Row
name|row
parameter_list|)
block|{
for|for
control|(
name|Accumulator
name|a
range|:
name|this
control|)
block|{
name|a
operator|.
name|send
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|end
parameter_list|(
name|RowBuilder
name|r
parameter_list|)
block|{
for|for
control|(
name|int
name|accIndex
init|=
literal|0
init|,
name|rowIndex
init|=
name|r
operator|.
name|size
argument_list|()
operator|-
name|size
argument_list|()
init|;
name|rowIndex
operator|<
name|r
operator|.
name|size
argument_list|()
condition|;
name|rowIndex
operator|++
operator|,
name|accIndex
operator|++
control|)
block|{
name|r
operator|.
name|set
argument_list|(
name|rowIndex
argument_list|,
name|get
argument_list|(
name|accIndex
argument_list|)
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Defines function implementation for    * things like {@code count()} and {@code sum()}.    */
specifier|private
interface|interface
name|Accumulator
block|{
name|void
name|send
parameter_list|(
name|Row
name|row
parameter_list|)
function_decl|;
name|Object
name|end
parameter_list|()
function_decl|;
block|}
comment|/** Implementation of {@code SUM} over INTEGER values as a user-defined    * aggregate. */
specifier|public
specifier|static
class|class
name|IntSum
block|{
specifier|public
name|IntSum
parameter_list|()
block|{
block|}
specifier|public
name|int
name|init
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|add
parameter_list|(
name|int
name|accumulator
parameter_list|,
name|int
name|v
parameter_list|)
block|{
return|return
name|accumulator
operator|+
name|v
return|;
block|}
specifier|public
name|int
name|merge
parameter_list|(
name|int
name|accumulator0
parameter_list|,
name|int
name|accumulator1
parameter_list|)
block|{
return|return
name|accumulator0
operator|+
name|accumulator1
return|;
block|}
specifier|public
name|int
name|result
parameter_list|(
name|int
name|accumulator
parameter_list|)
block|{
return|return
name|accumulator
return|;
block|}
block|}
comment|/** Implementation of {@code SUM} over BIGINT values as a user-defined    * aggregate. */
specifier|public
specifier|static
class|class
name|LongSum
block|{
specifier|public
name|LongSum
parameter_list|()
block|{
block|}
specifier|public
name|long
name|init
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
specifier|public
name|long
name|add
parameter_list|(
name|long
name|accumulator
parameter_list|,
name|long
name|v
parameter_list|)
block|{
return|return
name|accumulator
operator|+
name|v
return|;
block|}
specifier|public
name|long
name|merge
parameter_list|(
name|long
name|accumulator0
parameter_list|,
name|long
name|accumulator1
parameter_list|)
block|{
return|return
name|accumulator0
operator|+
name|accumulator1
return|;
block|}
specifier|public
name|long
name|result
parameter_list|(
name|long
name|accumulator
parameter_list|)
block|{
return|return
name|accumulator
return|;
block|}
block|}
comment|/** Implementation of {@code SUM} over DOUBLE values as a user-defined    * aggregate. */
specifier|public
specifier|static
class|class
name|DoubleSum
block|{
specifier|public
name|DoubleSum
parameter_list|()
block|{
block|}
specifier|public
name|double
name|init
parameter_list|()
block|{
return|return
literal|0D
return|;
block|}
specifier|public
name|double
name|add
parameter_list|(
name|double
name|accumulator
parameter_list|,
name|double
name|v
parameter_list|)
block|{
return|return
name|accumulator
operator|+
name|v
return|;
block|}
specifier|public
name|double
name|merge
parameter_list|(
name|double
name|accumulator0
parameter_list|,
name|double
name|accumulator1
parameter_list|)
block|{
return|return
name|accumulator0
operator|+
name|accumulator1
return|;
block|}
specifier|public
name|double
name|result
parameter_list|(
name|double
name|accumulator
parameter_list|)
block|{
return|return
name|accumulator
return|;
block|}
block|}
comment|/** Accumulator factory based on a user-defined aggregate function. */
specifier|private
specifier|static
class|class
name|UdaAccumulatorFactory
implements|implements
name|AccumulatorFactory
block|{
specifier|final
name|AggregateFunctionImpl
name|aggFunction
decl_stmt|;
specifier|final
name|int
name|argOrdinal
decl_stmt|;
specifier|public
specifier|final
name|Object
name|instance
decl_stmt|;
name|UdaAccumulatorFactory
parameter_list|(
name|AggregateFunctionImpl
name|aggFunction
parameter_list|,
name|AggregateCall
name|call
parameter_list|)
block|{
name|this
operator|.
name|aggFunction
operator|=
name|aggFunction
expr_stmt|;
if|if
condition|(
name|call
operator|.
name|getArgList
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"in current implementation, "
operator|+
literal|"aggregate must have precisely one argument"
argument_list|)
throw|;
block|}
name|argOrdinal
operator|=
name|call
operator|.
name|getArgList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|aggFunction
operator|.
name|isStatic
condition|)
block|{
name|instance
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|instance
operator|=
name|aggFunction
operator|.
name|declaringClass
operator|.
name|newInstance
argument_list|()
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
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|Accumulator
name|get
parameter_list|()
block|{
return|return
operator|new
name|UdaAccumulator
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Accumulator based upon a user-defined aggregate. */
specifier|private
specifier|static
class|class
name|UdaAccumulator
implements|implements
name|Accumulator
block|{
specifier|private
specifier|final
name|UdaAccumulatorFactory
name|factory
decl_stmt|;
specifier|private
name|Object
name|value
decl_stmt|;
name|UdaAccumulator
parameter_list|(
name|UdaAccumulatorFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
try|try
block|{
name|this
operator|.
name|value
operator|=
name|factory
operator|.
name|aggFunction
operator|.
name|initMethod
operator|.
name|invoke
argument_list|(
name|factory
operator|.
name|instance
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Row
name|row
parameter_list|)
block|{
specifier|final
name|Object
index|[]
name|args
init|=
block|{
name|value
block|,
name|row
operator|.
name|getValues
argument_list|()
index|[
name|factory
operator|.
name|argOrdinal
index|]
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
return|return;
comment|// one of the arguments is null; don't add to the total
block|}
block|}
try|try
block|{
name|value
operator|=
name|factory
operator|.
name|aggFunction
operator|.
name|addMethod
operator|.
name|invoke
argument_list|(
name|factory
operator|.
name|instance
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Object
name|end
parameter_list|()
block|{
specifier|final
name|Object
index|[]
name|args
init|=
block|{
name|value
block|}
decl_stmt|;
try|try
block|{
return|return
name|factory
operator|.
name|aggFunction
operator|.
name|resultMethod
operator|.
name|invoke
argument_list|(
name|factory
operator|.
name|instance
argument_list|,
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End AggregateNode.java
end_comment

end_unit

