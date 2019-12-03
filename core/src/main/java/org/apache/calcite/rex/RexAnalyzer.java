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
name|rex
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
name|Linq4j
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
name|RelOptPredicateList
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
name|NullSentinel
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
name|NlsString
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
name|ImmutableMap
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
name|java
operator|.
name|math
operator|.
name|BigDecimal
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

begin_comment
comment|/** Analyzes an expression, figures out what are the unbound variables,  * assigns a variety of values to each unbound variable, and evaluates  * the expression. */
end_comment

begin_class
specifier|public
class|class
name|RexAnalyzer
block|{
specifier|public
specifier|final
name|RexNode
name|e
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|variables
decl_stmt|;
specifier|public
specifier|final
name|int
name|unsupportedCount
decl_stmt|;
comment|/** Creates a RexAnalyzer. */
specifier|public
name|RexAnalyzer
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|RelOptPredicateList
name|predicates
parameter_list|)
block|{
name|this
operator|.
name|e
operator|=
name|e
expr_stmt|;
specifier|final
name|VariableCollector
name|variableCollector
init|=
operator|new
name|VariableCollector
argument_list|()
decl_stmt|;
name|e
operator|.
name|accept
argument_list|(
name|variableCollector
argument_list|)
expr_stmt|;
name|predicates
operator|.
name|pulledUpPredicates
operator|.
name|forEach
argument_list|(
name|p
lambda|->
name|p
operator|.
name|accept
argument_list|(
name|variableCollector
argument_list|)
argument_list|)
expr_stmt|;
name|variables
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|variableCollector
operator|.
name|builder
argument_list|)
expr_stmt|;
name|unsupportedCount
operator|=
name|variableCollector
operator|.
name|unsupportedCount
expr_stmt|;
block|}
comment|/** Generates a map of variables and lists of values that could be assigned    * to them. */
specifier|public
name|Iterable
argument_list|<
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Comparable
argument_list|>
argument_list|>
name|assignments
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|generators
init|=
name|variables
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|RexAnalyzer
operator|::
name|getComparables
argument_list|)
operator|.
name|collect
argument_list|(
name|Util
operator|.
name|toImmutableList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Iterable
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|product
init|=
name|Linq4j
operator|.
name|product
argument_list|(
name|generators
argument_list|)
decl_stmt|;
comment|//noinspection StaticPseudoFunctionalStyleMethod
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|product
argument_list|,
name|values
lambda|->
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|Pair
operator|.
name|zip
argument_list|(
name|variables
argument_list|,
name|values
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Comparable
argument_list|>
name|getComparables
parameter_list|(
name|RexNode
name|variable
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Comparable
argument_list|>
name|values
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|variable
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
name|values
operator|.
name|add
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
name|values
operator|.
name|add
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|1_000_000L
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|VARCHAR
case|:
name|values
operator|.
name|add
argument_list|(
operator|new
name|NlsString
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|NlsString
argument_list|(
literal|"hello"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|TIMESTAMP
case|:
name|values
operator|.
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
comment|// 1970-01-01 00:00:00
break|break;
case|case
name|DATE
case|:
name|values
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// 1970-01-01
name|values
operator|.
name|add
argument_list|(
literal|365
argument_list|)
expr_stmt|;
comment|// 1971-01-01
name|values
operator|.
name|add
argument_list|(
operator|-
literal|365
argument_list|)
expr_stmt|;
comment|// 1969-01-01
break|break;
case|case
name|TIME
case|:
name|values
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// 00:00:00.000
name|values
operator|.
name|add
argument_list|(
literal|86_399_000
argument_list|)
expr_stmt|;
comment|// 23:59:59.000
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"don't know values for "
operator|+
name|variable
operator|+
literal|" of type "
operator|+
name|variable
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|variable
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|NullSentinel
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
return|return
name|values
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Collects the variables (or other bindable sites) in an expression, and    * counts features (such as CAST) that {@link RexInterpreter} cannot    * handle. */
specifier|private
specifier|static
class|class
name|VariableCollector
extends|extends
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|RexNode
argument_list|>
name|builder
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|unsupportedCount
init|=
literal|0
decl_stmt|;
name|VariableCollector
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|inputRef
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visitInputRef
argument_list|(
name|inputRef
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|)
block|{
if|if
condition|(
name|fieldAccess
operator|.
name|getReferenceExpr
argument_list|()
operator|instanceof
name|RexDynamicParam
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|fieldAccess
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|visitFieldAccess
argument_list|(
name|fieldAccess
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Void
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|CAST
case|:
operator|++
name|unsupportedCount
expr_stmt|;
return|return
literal|null
return|;
default|default:
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

