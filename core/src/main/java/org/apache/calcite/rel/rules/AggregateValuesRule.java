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
name|rules
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
name|Values
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
name|Predicates
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
name|ArrayList
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

begin_comment
comment|/**  * Rule that applies {@link Aggregate} to a {@link Values} (currently just an  * empty {@code Value}s).  *  *<p>This is still useful because {@link PruneEmptyRules#AGGREGATE_INSTANCE}  * doesn't handle {@code Aggregate}, which is in turn because {@code Aggregate}  * of empty relations need some special handling: a single row will be  * generated, where each column's value depends on the specific aggregate calls  * (e.g. COUNT is 0, SUM is NULL).  *  *<p>Sample query where this matters:  *  *<blockquote><code>SELECT COUNT(*) FROM s.foo WHERE 1 = 0</code></blockquote>  *  *<p>This rule only applies to "grand totals", that is, {@code GROUP BY ()}.  * Any non-empty {@code GROUP BY} clause will return one row per group key  * value, and each group will consist of at least one row.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateValuesRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateValuesRule
name|INSTANCE
init|=
operator|new
name|AggregateValuesRule
argument_list|()
decl_stmt|;
specifier|private
name|AggregateValuesRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Predicates
operator|.
name|not
argument_list|(
name|Aggregate
operator|.
name|IS_NOT_GRAND_TOTAL
argument_list|)
argument_list|,
name|operand
argument_list|(
name|Values
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|.
name|IS_EMPTY
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|Aggregate
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Values
name|values
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|values
argument_list|)
expr_stmt|;
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|relBuilder
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|literals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|AggregateCall
name|aggregateCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
switch|switch
condition|(
name|aggregateCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|COUNT
case|:
case|case
name|SUM0
case|:
name|literals
operator|.
name|add
argument_list|(
operator|(
name|RexLiteral
operator|)
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|BigDecimal
operator|.
name|ZERO
argument_list|,
name|aggregateCall
operator|.
name|getType
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|MIN
case|:
case|case
name|MAX
case|:
case|case
name|SUM
case|:
name|literals
operator|.
name|add
argument_list|(
operator|(
name|RexLiteral
operator|)
name|rexBuilder
operator|.
name|makeCast
argument_list|(
name|aggregateCall
operator|.
name|getType
argument_list|()
argument_list|,
name|rexBuilder
operator|.
name|constantNull
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Unknown what this aggregate call should do on empty Values. Bail out to be safe.
return|return;
block|}
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|values
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|literals
argument_list|)
argument_list|,
name|aggregate
operator|.
name|getRowType
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// New plan is absolutely better than old plan.
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|setImportance
argument_list|(
name|aggregate
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateValuesRule.java
end_comment

end_unit

