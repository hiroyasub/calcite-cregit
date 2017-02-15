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
name|rel2sql
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
name|Calc
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
name|core
operator|.
name|Intersect
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
name|Join
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
name|JoinRelType
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
name|Minus
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
name|Sort
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
name|TableModify
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
name|TableScan
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
name|Union
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
name|sql
operator|.
name|JoinConditionType
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
name|JoinType
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
name|SqlDelete
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
name|SqlIdentifier
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
name|SqlInsert
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
name|SqlJoin
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
name|SqlSelect
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
name|SqlUpdate
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
name|SqlRowOperator
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
name|SqlSingleValueAggFunction
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
name|sql
operator|.
name|parser
operator|.
name|SqlParserPos
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
name|validate
operator|.
name|SqlValidatorUtil
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
name|ReflectUtil
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
name|ReflectiveVisitor
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
name|Function
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
name|Lists
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
comment|/**  * Utility to convert relational expressions to SQL abstract syntax tree.  */
end_comment

begin_class
specifier|public
class|class
name|RelToSqlConverter
extends|extends
name|SqlImplementor
implements|implements
name|ReflectiveVisitor
block|{
comment|/** Similar to {@link SqlStdOperatorTable#ROW}, but does not print "ROW". */
specifier|private
specifier|static
specifier|final
name|SqlRowOperator
name|ANONYMOUS_ROW
init|=
operator|new
name|SqlRowOperator
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReflectUtil
operator|.
name|MethodDispatcher
argument_list|<
name|Result
argument_list|>
name|dispatcher
decl_stmt|;
comment|/** Creates a RelToSqlConverter. */
specifier|public
name|RelToSqlConverter
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
name|dispatcher
operator|=
name|ReflectUtil
operator|.
name|createMethodDispatcher
argument_list|(
name|Result
operator|.
name|class
argument_list|,
name|this
argument_list|,
literal|"visit"
argument_list|,
name|RelNode
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** Dispatches a call to the {@code visit(Xxx e)} method where {@code Xxx}    * most closely matches the runtime type of the argument. */
specifier|protected
name|Result
name|dispatch
parameter_list|(
name|RelNode
name|e
parameter_list|)
block|{
return|return
name|dispatcher
operator|.
name|invoke
argument_list|(
name|e
argument_list|)
return|;
block|}
specifier|public
name|Result
name|visitChild
parameter_list|(
name|int
name|i
parameter_list|,
name|RelNode
name|e
parameter_list|)
block|{
return|return
name|dispatch
argument_list|(
name|e
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|RelNode
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Need to implement "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Join
name|e
parameter_list|)
block|{
specifier|final
name|Result
name|leftResult
init|=
name|visitChild
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getLeft
argument_list|()
argument_list|)
operator|.
name|resetAlias
argument_list|()
decl_stmt|;
specifier|final
name|Result
name|rightResult
init|=
name|visitChild
argument_list|(
literal|1
argument_list|,
name|e
operator|.
name|getRight
argument_list|()
argument_list|)
operator|.
name|resetAlias
argument_list|()
decl_stmt|;
specifier|final
name|Context
name|leftContext
init|=
name|leftResult
operator|.
name|qualifiedContext
argument_list|()
decl_stmt|;
specifier|final
name|Context
name|rightContext
init|=
name|rightResult
operator|.
name|qualifiedContext
argument_list|()
decl_stmt|;
name|SqlNode
name|sqlCondition
init|=
literal|null
decl_stmt|;
name|SqlLiteral
name|condType
init|=
name|JoinConditionType
operator|.
name|ON
operator|.
name|symbol
argument_list|(
name|POS
argument_list|)
decl_stmt|;
name|JoinType
name|joinType
init|=
name|joinType
argument_list|(
name|e
operator|.
name|getJoinType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|INNER
operator|&&
name|e
operator|.
name|getCondition
argument_list|()
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
name|joinType
operator|=
name|JoinType
operator|.
name|COMMA
expr_stmt|;
name|condType
operator|=
name|JoinConditionType
operator|.
name|NONE
operator|.
name|symbol
argument_list|(
name|POS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sqlCondition
operator|=
name|convertConditionToSqlNode
argument_list|(
name|e
operator|.
name|getCondition
argument_list|()
argument_list|,
name|leftContext
argument_list|,
name|rightContext
argument_list|,
name|e
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SqlNode
name|join
init|=
operator|new
name|SqlJoin
argument_list|(
name|POS
argument_list|,
name|leftResult
operator|.
name|asFrom
argument_list|()
argument_list|,
name|SqlLiteral
operator|.
name|createBoolean
argument_list|(
literal|false
argument_list|,
name|POS
argument_list|)
argument_list|,
name|joinType
operator|.
name|symbol
argument_list|(
name|POS
argument_list|)
argument_list|,
name|rightResult
operator|.
name|asFrom
argument_list|()
argument_list|,
name|condType
argument_list|,
name|sqlCondition
argument_list|)
decl_stmt|;
return|return
name|result
argument_list|(
name|join
argument_list|,
name|leftResult
argument_list|,
name|rightResult
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Filter
name|e
parameter_list|)
block|{
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
name|WHERE
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setWhere
argument_list|(
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
literal|null
argument_list|,
name|e
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|result
argument_list|()
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Project
name|e
parameter_list|)
block|{
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
if|if
condition|(
name|isStar
argument_list|(
name|e
operator|.
name|getChildExps
argument_list|()
argument_list|,
name|e
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|x
return|;
block|}
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
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|ref
range|:
name|e
operator|.
name|getChildExps
argument_list|()
control|)
block|{
name|SqlNode
name|sqlExpr
init|=
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
literal|null
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|addSelect
argument_list|(
name|selectList
argument_list|,
name|sqlExpr
argument_list|,
name|e
operator|.
name|getRowType
argument_list|()
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
return|return
name|builder
operator|.
name|result
argument_list|()
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Aggregate
name|e
parameter_list|)
block|{
comment|// "select a, b, sum(x) from ( ... ) group by a, b"
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
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getInput
argument_list|()
operator|instanceof
name|Project
condition|)
block|{
name|builder
operator|=
name|x
operator|.
name|builder
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|builder
operator|.
name|clauses
operator|.
name|add
argument_list|(
name|Clause
operator|.
name|GROUP_BY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|=
name|x
operator|.
name|builder
argument_list|(
name|e
argument_list|,
name|Clause
operator|.
name|GROUP_BY
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|int
name|group
range|:
name|e
operator|.
name|getGroupSet
argument_list|()
control|)
block|{
specifier|final
name|SqlNode
name|field
init|=
name|builder
operator|.
name|context
operator|.
name|field
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|addSelect
argument_list|(
name|selectList
argument_list|,
name|field
argument_list|,
name|e
operator|.
name|getRowType
argument_list|()
argument_list|)
expr_stmt|;
name|groupByList
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|e
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
name|SqlNode
name|aggCallSqlNode
init|=
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
name|aggCall
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
operator|instanceof
name|SqlSingleValueAggFunction
condition|)
block|{
name|aggCallSqlNode
operator|=
name|rewriteSingleValueExpr
argument_list|(
name|aggCallSqlNode
argument_list|,
name|dialect
argument_list|)
expr_stmt|;
block|}
name|addSelect
argument_list|(
name|selectList
argument_list|,
name|aggCallSqlNode
argument_list|,
name|e
operator|.
name|getRowType
argument_list|()
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
if|if
condition|(
operator|!
name|groupByList
operator|.
name|isEmpty
argument_list|()
operator|||
name|e
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Some databases don't support "GROUP BY ()". We can omit it as long
comment|// as there is at least one aggregate function.
name|builder
operator|.
name|setGroupBy
argument_list|(
operator|new
name|SqlNodeList
argument_list|(
name|groupByList
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
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|TableScan
name|e
parameter_list|)
block|{
specifier|final
name|SqlIdentifier
name|identifier
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|e
operator|.
name|getTable
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
return|return
name|result
argument_list|(
name|identifier
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|Clause
operator|.
name|FROM
argument_list|)
argument_list|,
name|e
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Union
name|e
parameter_list|)
block|{
return|return
name|setOpToSql
argument_list|(
name|e
operator|.
name|all
condition|?
name|SqlStdOperatorTable
operator|.
name|UNION_ALL
else|:
name|SqlStdOperatorTable
operator|.
name|UNION
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Intersect
name|e
parameter_list|)
block|{
return|return
name|setOpToSql
argument_list|(
name|e
operator|.
name|all
condition|?
name|SqlStdOperatorTable
operator|.
name|INTERSECT_ALL
else|:
name|SqlStdOperatorTable
operator|.
name|INTERSECT
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Minus
name|e
parameter_list|)
block|{
return|return
name|setOpToSql
argument_list|(
name|e
operator|.
name|all
condition|?
name|SqlStdOperatorTable
operator|.
name|EXCEPT_ALL
else|:
name|SqlStdOperatorTable
operator|.
name|EXCEPT
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Calc
name|e
parameter_list|)
block|{
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
name|RexProgram
name|program
init|=
name|e
operator|.
name|getProgram
argument_list|()
decl_stmt|;
name|Builder
name|builder
init|=
name|program
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
condition|?
name|x
operator|.
name|builder
argument_list|(
name|e
argument_list|,
name|Clause
operator|.
name|WHERE
argument_list|)
else|:
name|x
operator|.
name|builder
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isStar
argument_list|(
name|program
argument_list|)
condition|)
block|{
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
for|for
control|(
name|RexLocalRef
name|ref
range|:
name|program
operator|.
name|getProjectList
argument_list|()
control|)
block|{
name|SqlNode
name|sqlExpr
init|=
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
name|program
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|addSelect
argument_list|(
name|selectList
argument_list|,
name|sqlExpr
argument_list|,
name|e
operator|.
name|getRowType
argument_list|()
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
if|if
condition|(
name|program
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setWhere
argument_list|(
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
name|program
argument_list|,
name|program
operator|.
name|getCondition
argument_list|()
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
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Values
name|e
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Clause
argument_list|>
name|clauses
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Clause
operator|.
name|SELECT
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|pairs
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|final
name|Context
name|context
init|=
name|aliasContext
argument_list|(
name|pairs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|SqlNodeList
name|selects
init|=
operator|new
name|SqlNodeList
argument_list|(
name|POS
argument_list|)
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|tuple
range|:
name|e
operator|.
name|getTuples
argument_list|()
control|)
block|{
name|selects
operator|.
name|add
argument_list|(
name|ANONYMOUS_ROW
operator|.
name|createCall
argument_list|(
name|exprList
argument_list|(
name|context
argument_list|,
name|tuple
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SqlNode
name|query
init|=
name|SqlStdOperatorTable
operator|.
name|VALUES
operator|.
name|createCall
argument_list|(
name|selects
argument_list|)
decl_stmt|;
return|return
name|result
argument_list|(
name|query
argument_list|,
name|clauses
argument_list|,
name|e
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|Sort
name|e
parameter_list|)
block|{
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
name|ORDER_BY
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SqlNode
argument_list|>
name|orderByList
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|field
range|:
name|e
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
name|builder
operator|.
name|addOrderItem
argument_list|(
name|orderByList
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|orderByList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setOrderBy
argument_list|(
operator|new
name|SqlNodeList
argument_list|(
name|orderByList
argument_list|,
name|POS
argument_list|)
argument_list|)
expr_stmt|;
name|x
operator|=
name|builder
operator|.
name|result
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|.
name|fetch
operator|!=
literal|null
condition|)
block|{
name|builder
operator|=
name|x
operator|.
name|builder
argument_list|(
name|e
argument_list|,
name|Clause
operator|.
name|FETCH
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setFetch
argument_list|(
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
literal|null
argument_list|,
name|e
operator|.
name|fetch
argument_list|)
argument_list|)
expr_stmt|;
name|x
operator|=
name|builder
operator|.
name|result
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|.
name|offset
operator|!=
literal|null
condition|)
block|{
name|builder
operator|=
name|x
operator|.
name|builder
argument_list|(
name|e
argument_list|,
name|Clause
operator|.
name|OFFSET
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setOffset
argument_list|(
name|builder
operator|.
name|context
operator|.
name|toSql
argument_list|(
literal|null
argument_list|,
name|e
operator|.
name|offset
argument_list|)
argument_list|)
expr_stmt|;
name|x
operator|=
name|builder
operator|.
name|result
argument_list|()
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
comment|/** @see #dispatch */
specifier|public
name|Result
name|visit
parameter_list|(
name|TableModify
name|modify
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|pairs
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|final
name|Context
name|context
init|=
name|aliasContext
argument_list|(
name|pairs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Target Table Name
specifier|final
name|SqlIdentifier
name|sqlTargetTable
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|modify
operator|.
name|getTable
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|POS
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|modify
operator|.
name|getOperation
argument_list|()
condition|)
block|{
case|case
name|INSERT
case|:
block|{
comment|// Convert the input to a SELECT query or keep as VALUES. Not all
comment|// dialects support naked VALUES, but all support VALUES inside INSERT.
specifier|final
name|SqlNode
name|sqlSource
init|=
name|visitChild
argument_list|(
literal|0
argument_list|,
name|modify
operator|.
name|getInput
argument_list|()
argument_list|)
operator|.
name|asQueryOrValues
argument_list|()
decl_stmt|;
specifier|final
name|SqlInsert
name|sqlInsert
init|=
operator|new
name|SqlInsert
argument_list|(
name|POS
argument_list|,
name|SqlNodeList
operator|.
name|EMPTY
argument_list|,
name|sqlTargetTable
argument_list|,
name|sqlSource
argument_list|,
name|identifierList
argument_list|(
name|modify
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|result
argument_list|(
name|sqlInsert
argument_list|,
name|ImmutableList
operator|.
expr|<
name|Clause
operator|>
name|of
argument_list|()
argument_list|,
name|modify
argument_list|,
literal|null
argument_list|)
return|;
block|}
case|case
name|UPDATE
case|:
block|{
specifier|final
name|Result
name|input
init|=
name|visitChild
argument_list|(
literal|0
argument_list|,
name|modify
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SqlUpdate
name|sqlUpdate
init|=
operator|new
name|SqlUpdate
argument_list|(
name|POS
argument_list|,
name|sqlTargetTable
argument_list|,
name|identifierList
argument_list|(
name|modify
operator|.
name|getUpdateColumnList
argument_list|()
argument_list|)
argument_list|,
name|exprList
argument_list|(
name|context
argument_list|,
name|modify
operator|.
name|getSourceExpressionList
argument_list|()
argument_list|)
argument_list|,
operator|(
operator|(
name|SqlSelect
operator|)
name|input
operator|.
name|node
operator|)
operator|.
name|getWhere
argument_list|()
argument_list|,
name|input
operator|.
name|asSelect
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|result
argument_list|(
name|sqlUpdate
argument_list|,
name|input
operator|.
name|clauses
argument_list|,
name|modify
argument_list|,
literal|null
argument_list|)
return|;
block|}
case|case
name|DELETE
case|:
block|{
specifier|final
name|Result
name|input
init|=
name|visitChild
argument_list|(
literal|0
argument_list|,
name|modify
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SqlDelete
name|sqlDelete
init|=
operator|new
name|SqlDelete
argument_list|(
name|POS
argument_list|,
name|sqlTargetTable
argument_list|,
name|input
operator|.
name|asSelect
argument_list|()
operator|.
name|getWhere
argument_list|()
argument_list|,
name|input
operator|.
name|asSelect
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|result
argument_list|(
name|sqlDelete
argument_list|,
name|input
operator|.
name|clauses
argument_list|,
name|modify
argument_list|,
literal|null
argument_list|)
return|;
block|}
case|case
name|MERGE
case|:
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"not implemented: "
operator|+
name|modify
argument_list|)
throw|;
block|}
block|}
comment|/** Converts a list of {@link RexNode} expressions to {@link SqlNode}    * expressions. */
specifier|private
name|SqlNodeList
name|exprList
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|exprs
parameter_list|)
block|{
return|return
operator|new
name|SqlNodeList
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|exprs
argument_list|,
operator|new
name|Function
argument_list|<
name|RexNode
argument_list|,
name|SqlNode
argument_list|>
argument_list|()
block|{
specifier|public
name|SqlNode
name|apply
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
return|return
name|context
operator|.
name|toSql
argument_list|(
literal|null
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|POS
argument_list|)
return|;
block|}
comment|/** Converts a list of names expressions to a list of single-part    * {@link SqlIdentifier}s. */
specifier|private
name|SqlNodeList
name|identifierList
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
operator|new
name|SqlNodeList
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|names
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|SqlNode
argument_list|>
argument_list|()
block|{
specifier|public
name|SqlNode
name|apply
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|name
argument_list|,
name|POS
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|POS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addSelect
parameter_list|(
name|List
argument_list|<
name|SqlNode
argument_list|>
name|selectList
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|String
name|name
init|=
name|rowType
operator|.
name|getFieldNames
argument_list|()
operator|.
name|get
argument_list|(
name|selectList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|alias
init|=
name|SqlValidatorUtil
operator|.
name|getAlias
argument_list|(
name|node
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"expr$"
argument_list|)
condition|)
block|{
comment|//Put it in ordinalMap
name|ordinalMap
operator|.
name|put
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|alias
operator|==
literal|null
operator|||
operator|!
name|alias
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|node
operator|=
name|SqlStdOperatorTable
operator|.
name|AS
operator|.
name|createCall
argument_list|(
name|POS
argument_list|,
name|node
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
name|name
argument_list|,
name|POS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|selectList
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelToSqlConverter.java
end_comment

end_unit

