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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|SqlOperator
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
name|tools
operator|.
name|PigRelBuilder
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
name|ImmutableList
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
name|HashMap
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
comment|/**  * Walks over a Piglet AST and calls the corresponding methods in a  * {@link PigRelBuilder}.  */
end_comment

begin_class
specifier|public
class|class
name|Handler
block|{
specifier|private
specifier|final
name|PigRelBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelNode
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|Handler
parameter_list|(
name|PigRelBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
comment|/** Creates relational expressions for a given AST node. */
specifier|public
name|Handler
name|handle
parameter_list|(
name|Ast
operator|.
name|Node
name|node
parameter_list|)
block|{
specifier|final
name|RelNode
name|input
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexNodes
decl_stmt|;
switch|switch
condition|(
name|node
operator|.
name|op
condition|)
block|{
case|case
name|LOAD
case|:
specifier|final
name|Ast
operator|.
name|LoadStmt
name|load
init|=
operator|(
name|Ast
operator|.
name|LoadStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|scan
argument_list|(
operator|(
name|String
operator|)
name|load
operator|.
name|name
operator|.
name|value
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|load
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|VALUES
case|:
specifier|final
name|Ast
operator|.
name|ValuesStmt
name|values
init|=
operator|(
name|Ast
operator|.
name|ValuesStmt
operator|)
name|node
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|toType
argument_list|(
name|values
operator|.
name|schema
argument_list|)
decl_stmt|;
name|builder
operator|.
name|values
argument_list|(
name|tuples
argument_list|(
name|values
argument_list|,
name|rowType
argument_list|)
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|values
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|FOREACH
case|:
specifier|final
name|Ast
operator|.
name|ForeachStmt
name|foreach
init|=
operator|(
name|Ast
operator|.
name|ForeachStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|input
operator|=
name|map
operator|.
name|get
argument_list|(
name|foreach
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|rexNodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Ast
operator|.
name|Node
name|exp
range|:
name|foreach
operator|.
name|expList
control|)
block|{
name|rexNodes
operator|.
name|add
argument_list|(
name|toRex
argument_list|(
name|exp
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|project
argument_list|(
name|rexNodes
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|foreach
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|FOREACH_NESTED
case|:
specifier|final
name|Ast
operator|.
name|ForeachNestedStmt
name|foreachNested
init|=
operator|(
name|Ast
operator|.
name|ForeachNestedStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|input
operator|=
name|map
operator|.
name|get
argument_list|(
name|foreachNested
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
control|)
block|{
switch|switch
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|ARRAY
case|:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Ast
operator|.
name|Stmt
name|stmt
range|:
name|foreachNested
operator|.
name|nestedStmtList
control|)
block|{
name|handle
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
block|}
name|rexNodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Ast
operator|.
name|Node
name|exp
range|:
name|foreachNested
operator|.
name|expList
control|)
block|{
name|rexNodes
operator|.
name|add
argument_list|(
name|toRex
argument_list|(
name|exp
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|project
argument_list|(
name|rexNodes
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|foreachNested
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|FILTER
case|:
specifier|final
name|Ast
operator|.
name|FilterStmt
name|filter
init|=
operator|(
name|Ast
operator|.
name|FilterStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|input
operator|=
name|map
operator|.
name|get
argument_list|(
name|filter
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|rexNode
init|=
name|toRex
argument_list|(
name|filter
operator|.
name|condition
argument_list|)
decl_stmt|;
name|builder
operator|.
name|filter
argument_list|(
name|rexNode
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|filter
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|DISTINCT
case|:
specifier|final
name|Ast
operator|.
name|DistinctStmt
name|distinct
init|=
operator|(
name|Ast
operator|.
name|DistinctStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|input
operator|=
name|map
operator|.
name|get
argument_list|(
name|distinct
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|builder
operator|.
name|distinct
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|distinct
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|ORDER
case|:
specifier|final
name|Ast
operator|.
name|OrderStmt
name|order
init|=
operator|(
name|Ast
operator|.
name|OrderStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|input
operator|=
name|map
operator|.
name|get
argument_list|(
name|order
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
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
name|Pair
argument_list|<
name|Ast
operator|.
name|Identifier
argument_list|,
name|Ast
operator|.
name|Direction
argument_list|>
name|field
range|:
name|order
operator|.
name|fields
control|)
block|{
name|toSortRex
argument_list|(
name|nodes
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|sort
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|order
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|LIMIT
case|:
specifier|final
name|Ast
operator|.
name|LimitStmt
name|limit
init|=
operator|(
name|Ast
operator|.
name|LimitStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|input
operator|=
name|map
operator|.
name|get
argument_list|(
name|limit
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
operator|(
operator|(
name|Number
operator|)
name|limit
operator|.
name|count
operator|.
name|value
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|builder
operator|.
name|limit
argument_list|(
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|limit
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|GROUP
case|:
specifier|final
name|Ast
operator|.
name|GroupStmt
name|group
init|=
operator|(
name|Ast
operator|.
name|GroupStmt
operator|)
name|node
decl_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|input
operator|=
name|map
operator|.
name|get
argument_list|(
name|group
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
operator|.
name|as
argument_list|(
name|group
operator|.
name|source
operator|.
name|value
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelBuilder
operator|.
name|GroupKey
argument_list|>
name|groupKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|keys
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Ast
operator|.
name|Node
name|key
range|:
name|group
operator|.
name|keys
control|)
block|{
name|keys
operator|.
name|add
argument_list|(
name|toRex
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|groupKeys
operator|.
name|add
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|group
argument_list|(
name|PigRelBuilder
operator|.
name|GroupOption
operator|.
name|COLLECTED
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|groupKeys
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|group
operator|.
name|target
operator|.
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
case|case
name|PROGRAM
case|:
specifier|final
name|Ast
operator|.
name|Program
name|program
init|=
operator|(
name|Ast
operator|.
name|Program
operator|)
name|node
decl_stmt|;
for|for
control|(
name|Ast
operator|.
name|Stmt
name|stmt
range|:
name|program
operator|.
name|stmtList
control|)
block|{
name|handle
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
case|case
name|DUMP
case|:
specifier|final
name|Ast
operator|.
name|DumpStmt
name|dump
init|=
operator|(
name|Ast
operator|.
name|DumpStmt
operator|)
name|node
decl_stmt|;
specifier|final
name|RelNode
name|relNode
init|=
name|map
operator|.
name|get
argument_list|(
name|dump
operator|.
name|relation
operator|.
name|value
argument_list|)
decl_stmt|;
name|dump
argument_list|(
name|relNode
argument_list|)
expr_stmt|;
return|return
name|this
return|;
comment|// nothing to do; contains no algebra
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown operation "
operator|+
name|node
operator|.
name|op
argument_list|)
throw|;
block|}
block|}
comment|/** Executes a relational expression and prints the output.    *    *<p>The default implementation does nothing.    *    * @param rel Relational expression    */
specifier|protected
name|void
name|dump
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
block|}
specifier|private
name|ImmutableList
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|(
name|Ast
operator|.
name|ValuesStmt
name|valuesStmt
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
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
name|listBuilder
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
name|Ast
operator|.
name|Node
argument_list|>
name|nodeList
range|:
name|valuesStmt
operator|.
name|tupleList
control|)
block|{
name|listBuilder
operator|.
name|add
argument_list|(
name|tuple
argument_list|(
name|nodeList
argument_list|,
name|rowType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|listBuilder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|tuple
parameter_list|(
name|List
argument_list|<
name|Ast
operator|.
name|Node
argument_list|>
name|nodeList
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexLiteral
argument_list|>
name|listBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|Ast
operator|.
name|Node
argument_list|,
name|RelDataTypeField
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|nodeList
argument_list|,
name|rowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|Ast
operator|.
name|Node
name|node
init|=
name|pair
operator|.
name|left
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|pair
operator|.
name|right
operator|.
name|getType
argument_list|()
decl_stmt|;
name|listBuilder
operator|.
name|add
argument_list|(
name|item
argument_list|(
name|node
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|listBuilder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|bag
parameter_list|(
name|List
argument_list|<
name|Ast
operator|.
name|Node
argument_list|>
name|nodeList
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexLiteral
argument_list|>
name|listBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Ast
operator|.
name|Node
name|node
range|:
name|nodeList
control|)
block|{
name|listBuilder
operator|.
name|add
argument_list|(
name|item
argument_list|(
name|node
argument_list|,
name|type
operator|.
name|getComponentType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|listBuilder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|RexLiteral
name|item
parameter_list|(
name|Ast
operator|.
name|Node
name|node
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|builder
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|node
operator|.
name|op
condition|)
block|{
case|case
name|LITERAL
case|:
specifier|final
name|Ast
operator|.
name|Literal
name|literal
init|=
operator|(
name|Ast
operator|.
name|Literal
operator|)
name|node
decl_stmt|;
return|return
operator|(
name|RexLiteral
operator|)
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|literal
operator|.
name|value
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|TUPLE
case|:
specifier|final
name|Ast
operator|.
name|Call
name|tuple
init|=
operator|(
name|Ast
operator|.
name|Call
operator|)
name|node
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|list
init|=
name|tuple
argument_list|(
name|tuple
operator|.
name|operands
argument_list|,
name|type
argument_list|)
decl_stmt|;
return|return
operator|(
name|RexLiteral
operator|)
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|list
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|BAG
case|:
specifier|final
name|Ast
operator|.
name|Call
name|bag
init|=
operator|(
name|Ast
operator|.
name|Call
operator|)
name|node
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|list2
init|=
name|bag
argument_list|(
name|bag
operator|.
name|operands
argument_list|,
name|type
argument_list|)
decl_stmt|;
return|return
operator|(
name|RexLiteral
operator|)
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|list2
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"not a literal: "
operator|+
name|node
argument_list|)
throw|;
block|}
block|}
specifier|private
name|RelDataType
name|toType
parameter_list|(
name|Ast
operator|.
name|Schema
name|schema
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|typeBuilder
init|=
name|builder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Ast
operator|.
name|FieldSchema
name|fieldSchema
range|:
name|schema
operator|.
name|fieldSchemaList
control|)
block|{
name|typeBuilder
operator|.
name|add
argument_list|(
name|fieldSchema
operator|.
name|id
operator|.
name|value
argument_list|,
name|toType
argument_list|(
name|fieldSchema
operator|.
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|typeBuilder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|RelDataType
name|toType
parameter_list|(
name|Ast
operator|.
name|Type
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
operator|.
name|op
condition|)
block|{
case|case
name|SCALAR_TYPE
case|:
return|return
name|toType
argument_list|(
operator|(
name|Ast
operator|.
name|ScalarType
operator|)
name|type
argument_list|)
return|;
case|case
name|BAG_TYPE
case|:
return|return
name|toType
argument_list|(
operator|(
name|Ast
operator|.
name|BagType
operator|)
name|type
argument_list|)
return|;
case|case
name|MAP_TYPE
case|:
return|return
name|toType
argument_list|(
operator|(
name|Ast
operator|.
name|MapType
operator|)
name|type
argument_list|)
return|;
case|case
name|TUPLE_TYPE
case|:
return|return
name|toType
argument_list|(
operator|(
name|Ast
operator|.
name|TupleType
operator|)
name|type
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown type "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
specifier|private
name|RelDataType
name|toType
parameter_list|(
name|Ast
operator|.
name|ScalarType
name|type
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|builder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
operator|.
name|name
condition|)
block|{
case|case
literal|"boolean"
case|:
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
return|;
case|case
literal|"int"
case|:
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
return|;
case|case
literal|"float"
case|:
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|REAL
argument_list|)
return|;
default|default:
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
return|;
block|}
block|}
specifier|private
name|RelDataType
name|toType
parameter_list|(
name|Ast
operator|.
name|BagType
name|type
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|builder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|t
init|=
name|toType
argument_list|(
name|type
operator|.
name|componentType
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|t
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|toType
parameter_list|(
name|Ast
operator|.
name|MapType
name|type
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|builder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|k
init|=
name|toType
argument_list|(
name|type
operator|.
name|keyType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|v
init|=
name|toType
argument_list|(
name|type
operator|.
name|valueType
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createMapType
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|toType
parameter_list|(
name|Ast
operator|.
name|TupleType
name|type
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|builder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Ast
operator|.
name|FieldSchema
name|fieldSchema
range|:
name|type
operator|.
name|fieldSchemaList
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|fieldSchema
operator|.
name|id
operator|.
name|value
argument_list|,
name|toType
argument_list|(
name|fieldSchema
operator|.
name|type
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
specifier|private
name|void
name|toSortRex
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|,
name|Pair
argument_list|<
name|Ast
operator|.
name|Identifier
argument_list|,
name|Ast
operator|.
name|Direction
argument_list|>
name|pair
parameter_list|)
block|{
if|if
condition|(
name|pair
operator|.
name|left
operator|.
name|isStar
argument_list|()
condition|)
block|{
for|for
control|(
name|RexNode
name|node
range|:
name|builder
operator|.
name|fields
argument_list|()
control|)
block|{
switch|switch
condition|(
name|pair
operator|.
name|right
condition|)
block|{
case|case
name|DESC
case|:
name|node
operator|=
name|builder
operator|.
name|desc
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|RexNode
name|node
init|=
name|toRex
argument_list|(
name|pair
operator|.
name|left
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|pair
operator|.
name|right
condition|)
block|{
case|case
name|DESC
case|:
name|node
operator|=
name|builder
operator|.
name|desc
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|RexNode
name|toRex
parameter_list|(
name|Ast
operator|.
name|Node
name|exp
parameter_list|)
block|{
specifier|final
name|Ast
operator|.
name|Call
name|call
decl_stmt|;
switch|switch
condition|(
name|exp
operator|.
name|op
condition|)
block|{
case|case
name|LITERAL
case|:
return|return
name|builder
operator|.
name|literal
argument_list|(
operator|(
operator|(
name|Ast
operator|.
name|Literal
operator|)
name|exp
operator|)
operator|.
name|value
argument_list|)
return|;
case|case
name|IDENTIFIER
case|:
specifier|final
name|String
name|value
init|=
operator|(
operator|(
name|Ast
operator|.
name|Identifier
operator|)
name|exp
operator|)
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|matches
argument_list|(
literal|"^\\$[0-9]+"
argument_list|)
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|field
argument_list|(
name|i
argument_list|)
return|;
block|}
return|return
name|builder
operator|.
name|field
argument_list|(
name|value
argument_list|)
return|;
case|case
name|DOT
case|:
name|call
operator|=
operator|(
name|Ast
operator|.
name|Call
operator|)
name|exp
expr_stmt|;
specifier|final
name|RexNode
name|left
init|=
name|toRex
argument_list|(
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Ast
operator|.
name|Identifier
name|right
init|=
operator|(
name|Ast
operator|.
name|Identifier
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
return|return
name|builder
operator|.
name|dot
argument_list|(
name|left
argument_list|,
name|right
operator|.
name|value
argument_list|)
return|;
case|case
name|EQ
case|:
case|case
name|NE
case|:
case|case
name|GT
case|:
case|case
name|GTE
case|:
case|case
name|LT
case|:
case|case
name|LTE
case|:
case|case
name|AND
case|:
case|case
name|OR
case|:
case|case
name|NOT
case|:
case|case
name|PLUS
case|:
case|case
name|MINUS
case|:
name|call
operator|=
operator|(
name|Ast
operator|.
name|Call
operator|)
name|exp
expr_stmt|;
return|return
name|builder
operator|.
name|call
argument_list|(
name|op
argument_list|(
name|exp
operator|.
name|op
argument_list|)
argument_list|,
name|toRex
argument_list|(
name|call
operator|.
name|operands
argument_list|)
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown op "
operator|+
name|exp
operator|.
name|op
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|SqlOperator
name|op
parameter_list|(
name|Ast
operator|.
name|Op
name|op
parameter_list|)
block|{
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|EQ
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|EQUALS
return|;
case|case
name|NE
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
return|;
case|case
name|GT
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
return|;
case|case
name|GTE
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
return|;
case|case
name|LT
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
return|;
case|case
name|LTE
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
return|;
case|case
name|AND
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|AND
return|;
case|case
name|OR
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|OR
return|;
case|case
name|NOT
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|NOT
return|;
case|case
name|PLUS
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|PLUS
return|;
case|case
name|MINUS
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|MINUS
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown: "
operator|+
name|op
argument_list|)
throw|;
block|}
block|}
specifier|private
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|toRex
parameter_list|(
name|Iterable
argument_list|<
name|Ast
operator|.
name|Node
argument_list|>
name|operands
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexNode
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
name|Ast
operator|.
name|Node
name|operand
range|:
name|operands
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|toRex
argument_list|(
name|operand
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
comment|/** Assigns the current relational expression to a given name. */
specifier|private
name|void
name|register
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|builder
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End Handler.java
end_comment

end_unit

