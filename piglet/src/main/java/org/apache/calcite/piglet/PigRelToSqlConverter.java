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
name|piglet
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
name|EnumerableInterpreter
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
name|rel2sql
operator|.
name|RelToSqlConverter
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
name|SqlDialect
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
name|SqlLiteral
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
name|SqlNode
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
name|SqlNodeList
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
name|SqlWindow
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
comment|/**  * An extension of {@link RelToSqlConverter} to convert a relation algebra tree,  * translated from a Pig script, into a SQL statement.  *  *<p>The input relational algebra tree can be optimized by the planner for Pig  * to {@link RelNode}.  */
end_comment

begin_class
specifier|public
class|class
name|PigRelToSqlConverter
extends|extends
name|RelToSqlConverter
block|{
comment|/** Creates a RelToSqlConverter.    *    * @param dialect SQL dialect    */
name|PigRelToSqlConverter
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
name|super
argument_list|(
name|dialect
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Result
name|visit
parameter_list|(
name|Aggregate
name|e
parameter_list|)
block|{
specifier|final
name|Result
name|x
init|=
name|visitChild
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isProjectOutput
init|=
name|e
operator|.
name|getInput
argument_list|()
operator|instanceof
name|Project
operator|||
operator|(
name|e
operator|.
name|getInput
argument_list|()
operator|instanceof
name|EnumerableInterpreter
operator|&&
operator|(
operator|(
name|EnumerableInterpreter
operator|)
name|e
operator|.
name|getInput
argument_list|()
operator|)
operator|.
name|getInput
argument_list|()
operator|instanceof
name|Project
operator|)
decl_stmt|;
specifier|final
name|Builder
name|builder
init|=
name|getAggregateBuilder
argument_list|(
name|e
argument_list|,
name|x
argument_list|,
name|isProjectOutput
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|groupByList
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|selectList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|buildAggGroupList
argument_list|(
name|e
argument_list|,
name|builder
argument_list|,
name|groupByList
argument_list|,
name|selectList
argument_list|)
expr_stmt|;
specifier|final
name|int
name|groupSetSize
init|=
name|e
operator|.
name|getGroupSets
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|SqlNodeList
name|groupBy
init|=
operator|new
name|SqlNodeList
argument_list|(
name|groupByList
argument_list|,
name|POS
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupSetSize
operator|>
literal|1
condition|)
block|{
comment|// If there are multiple group sets, this should be a result of converting a
comment|// Pig CUBE/cube or Pig CUBE/rollup
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|cubeRollupList
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupSetSize
operator|==
name|groupByList
operator|.
name|size
argument_list|()
operator|+
literal|1
condition|)
block|{
name|cubeRollupList
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROLLUP
operator|.
name|createCall
argument_list|(
name|groupBy
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|groupSetSize
operator|==
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|groupByList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
assert|;
name|cubeRollupList
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CUBE
operator|.
name|createCall
argument_list|(
name|groupBy
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|groupBy
operator|=
operator|new
name|SqlNodeList
argument_list|(
name|cubeRollupList
argument_list|,
name|POS
argument_list|)
expr_stmt|;
block|}
return|return
name|buildAggregate
argument_list|(
name|e
argument_list|,
name|builder
argument_list|,
name|selectList
argument_list|,
name|groupBy
operator|.
name|getList
argument_list|()
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Window
name|e
parameter_list|)
block|{
specifier|final
name|Result
name|x
init|=
name|visitChild
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Builder
name|builder
init|=
name|x
operator|.
name|builder
argument_list|(
name|e
argument_list|,
name|Clause
operator|.
name|SELECT
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|selectList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|builder
operator|.
name|context
operator|.
name|fieldList
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Window
operator|.
name|Group
name|winGroup
range|:
name|e
operator|.
name|groups
control|)
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|partitionList
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|winGroup
operator|.
name|keys
control|)
block|{
name|partitionList
operator|.
name|add
argument_list|(
name|builder
operator|.
name|context
operator|.
name|field
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|orderList
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|orderKey
range|:
name|winGroup
operator|.
name|collation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
name|orderList
operator|.
name|add
argument_list|(
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
name|orderKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SqlNode
name|sqlWindow
init|=
name|SqlWindow
operator|.
name|create
argument_list|(
literal|null
argument_list|,
comment|// Window declaration name
literal|null
argument_list|,
comment|// Window reference name
operator|new
name|SqlNodeList
argument_list|(
name|partitionList
argument_list|,
name|POS
argument_list|)
argument_list|,
operator|new
name|SqlNodeList
argument_list|(
name|orderList
argument_list|,
name|POS
argument_list|)
argument_list|,
name|SqlLiteral
operator|.
name|createBoolean
argument_list|(
name|winGroup
operator|.
name|isRows
argument_list|,
name|POS
argument_list|)
argument_list|,
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
name|winGroup
operator|.
name|lowerBound
argument_list|)
argument_list|,
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
name|winGroup
operator|.
name|upperBound
argument_list|)
argument_list|,
literal|null
argument_list|,
comment|// allowPartial
name|POS
argument_list|)
decl_stmt|;
for|for
control|(
name|Window
operator|.
name|RexWinAggCall
name|winFunc
range|:
name|winGroup
operator|.
name|aggCalls
control|)
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|winFuncOperands
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|winFunc
operator|.
name|getOperands
argument_list|()
control|)
block|{
name|winFuncOperands
operator|.
name|add
argument_list|(
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
literal|null
argument_list|,
name|operand
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SqlNode
name|aggFunc
init|=
name|winFunc
operator|.
name|getOperator
argument_list|()
operator|.
name|createCall
argument_list|(
operator|new
name|SqlNodeList
argument_list|(
name|winFuncOperands
argument_list|,
name|POS
argument_list|)
argument_list|)
decl_stmt|;
name|selectList
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OVER
operator|.
name|createCall
argument_list|(
name|POS
argument_list|,
name|aggFunc
argument_list|,
name|sqlWindow
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setSelect
argument_list|(
operator|new
name|SqlNodeList
argument_list|(
name|selectList
argument_list|,
name|POS
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|result
argument_list|()
return|;
block|}
block|}
end_class

end_unit

