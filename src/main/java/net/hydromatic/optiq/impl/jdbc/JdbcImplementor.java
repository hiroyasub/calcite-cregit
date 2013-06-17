begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expressions
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
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
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
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
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|BasicSqlType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
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
name|eigenbase
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
name|eigenbase
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * State for generating a SQL statement.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcImplementor
block|{
specifier|public
specifier|static
specifier|final
name|SqlParserPos
name|POS
init|=
name|SqlParserPos
operator|.
name|ZERO
decl_stmt|;
specifier|final
name|SqlDialect
name|dialect
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|aliasSet
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|JdbcImplementor
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a result based on a single relational expression. */
specifier|public
name|Result
name|result
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|Collection
argument_list|<
name|Clause
argument_list|>
name|clauses
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|String
name|alias2
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
specifier|final
name|String
name|alias3
init|=
name|alias2
operator|!=
literal|null
condition|?
name|alias2
else|:
literal|"t"
decl_stmt|;
specifier|final
name|String
name|alias4
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|alias3
argument_list|,
name|aliasSet
argument_list|)
decl_stmt|;
specifier|final
name|String
name|alias5
init|=
name|alias2
operator|==
literal|null
operator|||
operator|!
name|alias2
operator|.
name|equals
argument_list|(
name|alias4
argument_list|)
condition|?
name|alias4
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|node
argument_list|,
name|clauses
argument_list|,
name|alias5
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|alias4
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a result based on a join. (Each join could contain one or more    * relational expressions.) */
specifier|public
name|Result
name|result
parameter_list|(
name|SqlNode
name|join
parameter_list|,
name|Result
name|leftResult
parameter_list|,
name|Result
name|rightResult
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|leftResult
operator|.
name|aliases
argument_list|)
expr_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|rightResult
operator|.
name|aliases
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|join
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|Clause
operator|.
name|FROM
argument_list|)
argument_list|,
literal|null
argument_list|,
name|list
argument_list|)
return|;
block|}
comment|/** Wraps a node in a SELECT statement that has no clauses:    *  "SELECT ... FROM (node)". */
name|SqlSelect
name|wrapSelect
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
assert|assert
name|node
operator|instanceof
name|SqlJoin
operator|||
name|node
operator|instanceof
name|SqlIdentifier
operator|||
name|node
operator|instanceof
name|SqlCall
operator|&&
operator|(
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperator
argument_list|()
operator|instanceof
name|SqlSetOperator
operator|||
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|asOperator
operator|)
operator|:
name|node
assert|;
return|return
name|SqlStdOperatorTable
operator|.
name|selectOperator
operator|.
name|createCall
argument_list|(
name|SqlNodeList
operator|.
name|Empty
argument_list|,
literal|null
argument_list|,
name|node
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlNodeList
operator|.
name|Empty
argument_list|,
literal|null
argument_list|,
name|POS
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
operator|(
operator|(
name|JdbcRel
operator|)
name|e
operator|)
operator|.
name|implement
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Context for translating a {@link RexNode} expression (within a    * {@link RelNode}) into a {@link SqlNode} expression (within a SQL parse    * tree). */
specifier|public
specifier|abstract
class|class
name|Context
block|{
specifier|private
specifier|final
name|int
name|fieldCount
decl_stmt|;
specifier|protected
name|Context
parameter_list|(
name|int
name|fieldCount
parameter_list|)
block|{
name|this
operator|.
name|fieldCount
operator|=
name|fieldCount
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|SqlNode
name|field
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/** Converts an expression from {@link RexNode} to {@link SqlNode}      * format. */
name|SqlNode
name|toSql
parameter_list|(
name|RexProgram
name|program
parameter_list|,
name|RexNode
name|rex
parameter_list|)
block|{
if|if
condition|(
name|rex
operator|instanceof
name|RexLocalRef
condition|)
block|{
specifier|final
name|int
name|index
init|=
operator|(
operator|(
name|RexLocalRef
operator|)
name|rex
operator|)
operator|.
name|getIndex
argument_list|()
decl_stmt|;
return|return
name|toSql
argument_list|(
name|program
argument_list|,
name|program
operator|.
name|getExprList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|rex
operator|instanceof
name|RexInputRef
condition|)
block|{
return|return
name|field
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|rex
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|rex
operator|instanceof
name|RexLiteral
condition|)
block|{
specifier|final
name|RexLiteral
name|literal
init|=
operator|(
name|RexLiteral
operator|)
name|rex
decl_stmt|;
switch|switch
condition|(
name|literal
operator|.
name|getTypeName
argument_list|()
operator|.
name|getFamily
argument_list|()
condition|)
block|{
case|case
name|CHARACTER
case|:
return|return
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
operator|(
name|String
operator|)
name|literal
operator|.
name|getValue2
argument_list|()
argument_list|,
name|POS
argument_list|)
return|;
case|case
name|NUMERIC
case|:
case|case
name|EXACT_NUMERIC
case|:
return|return
name|SqlLiteral
operator|.
name|createExactNumeric
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|POS
argument_list|)
return|;
case|case
name|APPROXIMATE_NUMERIC
case|:
return|return
name|SqlLiteral
operator|.
name|createApproxNumeric
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|POS
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
return|return
name|SqlLiteral
operator|.
name|createBoolean
argument_list|(
operator|(
name|Boolean
operator|)
name|literal
operator|.
name|getValue
argument_list|()
argument_list|,
name|POS
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|literal
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|rex
operator|instanceof
name|RexCall
condition|)
block|{
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|rex
decl_stmt|;
specifier|final
name|SqlOperator
name|op
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|nodeList
init|=
name|toSql
argument_list|(
name|program
argument_list|,
name|call
operator|.
name|getOperandList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
name|SqlStdOperatorTable
operator|.
name|castFunc
condition|)
block|{
name|RelDataType
name|type
init|=
name|call
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|VARCHAR
operator|&&
name|dialect
operator|.
name|getDatabaseProduct
argument_list|()
operator|==
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
condition|)
block|{
comment|// MySQL doesn't have a VARCHAR type, only CHAR.
name|nodeList
operator|.
name|add
argument_list|(
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
literal|"CHAR"
argument_list|,
name|POS
argument_list|)
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|POS
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodeList
operator|.
name|add
argument_list|(
name|toSql
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|op
operator|==
name|SqlStdOperatorTable
operator|.
name|caseOperator
condition|)
block|{
specifier|final
name|SqlNode
name|valueNode
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|whenList
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
name|thenList
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|elseNode
decl_stmt|;
if|if
condition|(
name|nodeList
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
comment|// switched:
comment|//   "case x when v1 then t1 when v2 then t2 ... else e end"
name|valueNode
operator|=
name|nodeList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|nodeList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|whenList
operator|.
name|add
argument_list|(
name|nodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|thenList
operator|.
name|add
argument_list|(
name|nodeList
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// other: "case when w1 then t1 when w2 then t2 ... else e end"
name|valueNode
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|whenList
operator|.
name|add
argument_list|(
name|nodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|thenList
operator|.
name|add
argument_list|(
name|nodeList
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|elseNode
operator|=
name|nodeList
operator|.
name|get
argument_list|(
name|nodeList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|op
operator|.
name|createCall
argument_list|(
name|POS
argument_list|,
name|valueNode
argument_list|,
operator|new
name|SqlNodeList
argument_list|(
name|whenList
argument_list|,
name|POS
argument_list|)
argument_list|,
operator|new
name|SqlNodeList
argument_list|(
name|thenList
argument_list|,
name|POS
argument_list|)
argument_list|,
name|elseNode
argument_list|)
return|;
block|}
return|return
name|op
operator|.
name|createCall
argument_list|(
operator|new
name|SqlNodeList
argument_list|(
name|nodeList
argument_list|,
name|POS
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
name|rex
argument_list|)
throw|;
block|}
block|}
specifier|private
name|SqlNode
name|toSql
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|dialect
operator|.
name|getDatabaseProduct
argument_list|()
operator|==
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
condition|)
block|{
specifier|final
name|SqlTypeName
name|sqlTypeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|sqlTypeName
condition|)
block|{
case|case
name|VARCHAR
case|:
comment|// MySQL doesn't have a VARCHAR type, only CHAR.
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
literal|"CHAR"
argument_list|,
name|POS
argument_list|)
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|POS
argument_list|)
return|;
case|case
name|INTEGER
case|:
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
literal|"_UNSIGNED"
argument_list|,
name|POS
argument_list|)
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|POS
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|type
operator|instanceof
name|BasicSqlType
condition|)
block|{
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|POS
argument_list|)
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|type
operator|.
name|getScale
argument_list|()
argument_list|,
name|type
operator|.
name|getCharset
argument_list|()
operator|!=
literal|null
operator|&&
name|dialect
operator|.
name|supportsCharSet
argument_list|()
condition|?
name|type
operator|.
name|getCharset
argument_list|()
operator|.
name|name
argument_list|()
else|:
literal|null
argument_list|,
literal|null
argument_list|,
name|POS
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
name|type
argument_list|)
throw|;
comment|// TODO: implement
block|}
specifier|private
name|List
argument_list|<
name|SqlNode
argument_list|>
name|toSql
parameter_list|(
name|RexProgram
name|program
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operandList
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SqlNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|rex
range|:
name|operandList
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|toSql
argument_list|(
name|program
argument_list|,
name|rex
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|fieldList
parameter_list|()
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|SqlNode
argument_list|>
argument_list|()
block|{
specifier|public
name|SqlNode
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|field
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fieldCount
return|;
block|}
block|}
return|;
block|}
comment|/** Converts a call to an aggregate function to an expression. */
specifier|public
name|SqlNode
name|toSql
parameter_list|(
name|AggregateCall
name|aggCall
parameter_list|)
block|{
name|SqlOperator
name|op
init|=
operator|(
name|SqlAggFunction
operator|)
name|aggCall
operator|.
name|getAggregation
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|arg
range|:
name|aggCall
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|operands
operator|.
name|add
argument_list|(
name|field
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|op
operator|.
name|createCall
argument_list|(
name|aggCall
operator|.
name|isDistinct
argument_list|()
condition|?
name|SqlSelectKeyword
operator|.
name|Distinct
operator|.
name|symbol
argument_list|(
name|POS
argument_list|)
else|:
literal|null
argument_list|,
name|POS
argument_list|,
name|operands
operator|.
name|toArray
argument_list|(
operator|new
name|SqlNode
index|[
name|operands
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts a collation to an ORDER BY item. */
specifier|public
name|SqlNode
name|toSql
parameter_list|(
name|RelFieldCollation
name|collation
parameter_list|)
block|{
name|SqlNode
name|node
init|=
name|field
argument_list|(
name|collation
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|collation
operator|.
name|getDirection
argument_list|()
condition|)
block|{
case|case
name|Descending
case|:
case|case
name|StrictlyDescending
case|:
name|node
operator|=
name|SqlStdOperatorTable
operator|.
name|descendingOperator
operator|.
name|createCall
argument_list|(
name|POS
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|collation
operator|.
name|nullDirection
condition|)
block|{
case|case
name|FIRST
case|:
name|node
operator|=
name|SqlStdOperatorTable
operator|.
name|nullsFirstOperator
operator|.
name|createCall
argument_list|(
name|POS
argument_list|,
name|node
argument_list|)
expr_stmt|;
break|break;
case|case
name|LAST
case|:
name|node
operator|=
name|SqlStdOperatorTable
operator|.
name|nullsLastOperator
operator|.
name|createCall
argument_list|(
name|POS
argument_list|,
name|node
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|node
return|;
block|}
block|}
specifier|private
specifier|static
name|int
name|computeFieldCount
parameter_list|(
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|aliases
parameter_list|)
block|{
name|int
name|x
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|alias
range|:
name|aliases
control|)
block|{
name|x
operator|+=
name|alias
operator|.
name|right
operator|.
name|getFieldCount
argument_list|()
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
comment|/** Implementation of Context that precedes field references with their    * "table alias" based on the current sub-query's FROM clause. */
specifier|public
class|class
name|AliasContext
extends|extends
name|Context
block|{
specifier|private
specifier|final
name|boolean
name|qualified
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|aliases
decl_stmt|;
specifier|public
name|AliasContext
parameter_list|(
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|aliases
parameter_list|,
name|boolean
name|qualified
parameter_list|)
block|{
name|super
argument_list|(
name|computeFieldCount
argument_list|(
name|aliases
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
name|this
operator|.
name|qualified
operator|=
name|qualified
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|field
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|alias
range|:
name|aliases
control|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|alias
operator|.
name|right
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlIdentifier
argument_list|(
operator|!
name|qualified
condition|?
operator|new
name|String
index|[]
block|{
name|field
operator|.
name|getName
argument_list|()
block|}
else|:
operator|new
name|String
index|[]
block|{
name|alias
operator|.
name|left
block|,
name|field
operator|.
name|getName
argument_list|()
block|}
argument_list|,
name|POS
argument_list|)
return|;
block|}
name|ordinal
operator|-=
name|fields
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"field ordinal "
operator|+
name|ordinal
operator|+
literal|" out of range "
operator|+
name|aliases
argument_list|)
throw|;
block|}
block|}
specifier|public
class|class
name|Result
block|{
specifier|final
name|SqlNode
name|node
decl_stmt|;
specifier|private
specifier|final
name|String
name|neededAlias
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|aliases
decl_stmt|;
specifier|final
name|Expressions
operator|.
name|FluentList
argument_list|<
name|Clause
argument_list|>
name|clauses
decl_stmt|;
specifier|private
name|Result
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|Collection
argument_list|<
name|Clause
argument_list|>
name|clauses
parameter_list|,
name|String
name|neededAlias
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|aliases
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|neededAlias
operator|=
name|neededAlias
expr_stmt|;
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
name|this
operator|.
name|clauses
operator|=
name|Expressions
operator|.
name|list
argument_list|(
name|clauses
argument_list|)
expr_stmt|;
block|}
comment|/** Once you have a Result of implementing a child relational expression,      * call this method to create a Builder to implement the current relational      * expression by adding additional clauses to the SQL query.      *      *<p>You need to declare which clauses you intend to add. If the clauses      * are "later", you can add to the same query. For example, "GROUP BY" comes      * after "WHERE". But if they are the same or earlier, this method will      * start a new SELECT that wraps the previous result.</p>      *      *<p>When you have called      * {@link Builder#setSelect(org.eigenbase.sql.SqlNodeList)},      * {@link Builder#setWhere(org.eigenbase.sql.SqlNode)} etc. call      * {@link Builder#result(org.eigenbase.sql.SqlNode, java.util.Collection, org.eigenbase.rel.RelNode)}      * to fix the new query.</p>      *      * @param rel Relational expression being implemented      * @param clauses Clauses that will be generated to implement current      *                relational expression      * @return A builder      */
specifier|public
name|Builder
name|builder
parameter_list|(
name|JdbcRel
name|rel
parameter_list|,
name|Clause
modifier|...
name|clauses
parameter_list|)
block|{
specifier|final
name|Clause
name|maxClause
init|=
name|maxClause
argument_list|()
decl_stmt|;
name|boolean
name|needNew
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Clause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|maxClause
operator|.
name|ordinal
argument_list|()
operator|>=
name|clause
operator|.
name|ordinal
argument_list|()
condition|)
block|{
name|needNew
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|SqlSelect
name|select
decl_stmt|;
name|Expressions
operator|.
name|FluentList
argument_list|<
name|Clause
argument_list|>
name|clauseList
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|needNew
condition|)
block|{
name|select
operator|=
name|subSelect
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|select
operator|=
name|asSelect
argument_list|()
expr_stmt|;
name|clauseList
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|clauses
argument_list|)
expr_stmt|;
block|}
name|clauseList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|clauses
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|newContext
decl_stmt|;
specifier|final
name|SqlNodeList
name|selectList
init|=
name|select
operator|.
name|getSelectList
argument_list|()
decl_stmt|;
if|if
condition|(
name|selectList
operator|!=
literal|null
condition|)
block|{
name|newContext
operator|=
operator|new
name|Context
argument_list|(
name|selectList
operator|.
name|size
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SqlNode
name|field
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
specifier|final
name|SqlNode
name|selectItem
init|=
name|selectList
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|selectItem
operator|instanceof
name|SqlCall
operator|&&
operator|(
operator|(
name|SqlCall
operator|)
name|selectItem
operator|)
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|asOperator
condition|)
block|{
return|return
operator|(
operator|(
name|SqlCall
operator|)
name|selectItem
operator|)
operator|.
name|operands
index|[
literal|0
index|]
return|;
block|}
return|return
name|selectItem
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|newContext
operator|=
operator|new
name|AliasContext
argument_list|(
name|aliases
argument_list|,
name|aliases
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Builder
argument_list|(
name|rel
argument_list|,
name|clauseList
argument_list|,
name|select
argument_list|,
name|newContext
argument_list|)
return|;
block|}
comment|// make private?
specifier|public
name|Clause
name|maxClause
parameter_list|()
block|{
name|Clause
name|maxClause
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Clause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|maxClause
operator|==
literal|null
operator|||
name|clause
operator|.
name|ordinal
argument_list|()
operator|>
name|maxClause
operator|.
name|ordinal
argument_list|()
condition|)
block|{
name|maxClause
operator|=
name|clause
expr_stmt|;
block|}
block|}
assert|assert
name|maxClause
operator|!=
literal|null
assert|;
return|return
name|maxClause
return|;
block|}
comment|/** Returns a node that can be included in the FROM clause or a JOIN. It has      * an alias that is unique within the query. The alias is implicit if it      * can be derived using the usual rules (For example, "SELECT * FROM emp" is      * equivalent to "SELECT * FROM emp AS emp".) */
specifier|public
name|SqlNode
name|asFrom
parameter_list|()
block|{
if|if
condition|(
name|neededAlias
operator|!=
literal|null
condition|)
block|{
return|return
name|SqlStdOperatorTable
operator|.
name|asOperator
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
name|neededAlias
argument_list|,
name|POS
argument_list|)
argument_list|)
return|;
block|}
return|return
name|node
return|;
block|}
specifier|public
name|SqlSelect
name|subSelect
parameter_list|()
block|{
return|return
name|wrapSelect
argument_list|(
name|asFrom
argument_list|()
argument_list|)
return|;
block|}
comment|/** Converts a non-query node into a SELECT node. Set operators (UNION,      * INTERSECT, EXCEPT) remain as is. */
name|SqlSelect
name|asSelect
parameter_list|()
block|{
if|if
condition|(
name|node
operator|instanceof
name|SqlSelect
condition|)
block|{
return|return
operator|(
name|SqlSelect
operator|)
name|node
return|;
block|}
return|return
name|wrapSelect
argument_list|(
name|node
argument_list|)
return|;
block|}
comment|/** Converts a non-query node into a SELECT node. Set operators (UNION,      * INTERSECT, EXCEPT) remain as is. */
name|SqlNode
name|asQuery
parameter_list|()
block|{
if|if
condition|(
name|node
operator|instanceof
name|SqlCall
operator|&&
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperator
argument_list|()
operator|instanceof
name|SqlSetOperator
condition|)
block|{
return|return
name|node
return|;
block|}
return|return
name|asSelect
argument_list|()
return|;
block|}
comment|/** Returns a context that always qualifies identifiers. Useful if the      * Context deals with just one arm of a join, yet we wish to generate      * a join condition that qualifies column names to disambiguate them. */
specifier|public
name|Context
name|qualifiedContext
parameter_list|()
block|{
return|return
operator|new
name|AliasContext
argument_list|(
name|aliases
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
specifier|public
class|class
name|Builder
block|{
specifier|private
specifier|final
name|JdbcRel
name|rel
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Clause
argument_list|>
name|clauses
decl_stmt|;
specifier|private
specifier|final
name|SqlSelect
name|select
decl_stmt|;
specifier|public
specifier|final
name|Context
name|context
decl_stmt|;
specifier|public
name|Builder
parameter_list|(
name|JdbcRel
name|rel
parameter_list|,
name|List
argument_list|<
name|Clause
argument_list|>
name|clauses
parameter_list|,
name|SqlSelect
name|select
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|clauses
operator|=
name|clauses
expr_stmt|;
name|this
operator|.
name|select
operator|=
name|select
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|void
name|setSelect
parameter_list|(
name|SqlNodeList
name|nodeList
parameter_list|)
block|{
name|select
operator|.
name|operands
index|[
name|SqlSelect
operator|.
name|SELECT_OPERAND
index|]
operator|=
name|nodeList
expr_stmt|;
block|}
specifier|public
name|void
name|setWhere
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
assert|assert
name|clauses
operator|.
name|contains
argument_list|(
name|Clause
operator|.
name|WHERE
argument_list|)
assert|;
name|select
operator|.
name|operands
index|[
name|SqlSelect
operator|.
name|WHERE_OPERAND
index|]
operator|=
name|node
expr_stmt|;
block|}
specifier|public
name|void
name|setGroupBy
parameter_list|(
name|SqlNodeList
name|nodeList
parameter_list|)
block|{
assert|assert
name|clauses
operator|.
name|contains
argument_list|(
name|Clause
operator|.
name|GROUP_BY
argument_list|)
assert|;
name|select
operator|.
name|operands
index|[
name|SqlSelect
operator|.
name|GROUP_OPERAND
index|]
operator|=
name|nodeList
expr_stmt|;
block|}
specifier|public
name|void
name|setOrderBy
parameter_list|(
name|SqlNodeList
name|nodeList
parameter_list|)
block|{
assert|assert
name|clauses
operator|.
name|contains
argument_list|(
name|Clause
operator|.
name|ORDER_BY
argument_list|)
assert|;
name|select
operator|.
name|operands
index|[
name|SqlSelect
operator|.
name|ORDER_OPERAND
index|]
operator|=
name|nodeList
expr_stmt|;
block|}
specifier|public
name|Result
name|result
parameter_list|()
block|{
return|return
name|JdbcImplementor
operator|.
name|this
operator|.
name|result
argument_list|(
name|select
argument_list|,
name|clauses
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
comment|/** Clauses in a SQL query. Ordered by evaluation order.    * SELECT is set only when there is a NON-TRIVIAL SELECT clause. */
enum|enum
name|Clause
block|{
name|FROM
block|,
name|WHERE
block|,
name|GROUP_BY
block|,
name|HAVING
block|,
name|SELECT
block|,
name|SET_OP
block|,
name|ORDER_BY
block|}
block|}
end_class

begin_comment
comment|// End JdbcImplementor.java
end_comment

end_unit

