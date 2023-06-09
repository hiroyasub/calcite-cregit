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
name|prepare
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
name|linq4j
operator|.
name|Queryable
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
name|BlockStatement
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
name|Blocks
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
name|ConstantExpression
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
name|FunctionExpression
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
name|MethodCallExpression
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
name|NewExpression
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
name|linq4j
operator|.
name|tree
operator|.
name|Types
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
name|ViewExpanders
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
name|logical
operator|.
name|LogicalFilter
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
name|logical
operator|.
name|LogicalProject
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
name|logical
operator|.
name|LogicalTableScan
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
name|util
operator|.
name|BuiltInMethod
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
name|ImmutableSet
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
name|Type
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
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Translates a tree of linq4j {@link Queryable} nodes to a tree of  * {@link RelNode} planner nodes.  *  * @see QueryableRelBuilder  */
end_comment

begin_class
class|class
name|LixToRelTranslator
block|{
specifier|final
name|RelOptCluster
name|cluster
decl_stmt|;
specifier|private
specifier|final
name|Prepare
name|preparingStmt
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
name|LixToRelTranslator
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|Prepare
name|preparingStmt
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
name|preparingStmt
operator|=
name|preparingStmt
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
operator|(
name|JavaTypeFactory
operator|)
name|cluster
operator|.
name|getTypeFactory
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|BlockStatement
name|getBody
parameter_list|(
name|FunctionExpression
argument_list|<
name|?
argument_list|>
name|expression
parameter_list|)
block|{
return|return
name|requireNonNull
argument_list|(
name|expression
operator|.
name|body
argument_list|,
parameter_list|()
lambda|->
literal|"body in "
operator|+
name|expression
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|getParameterList
parameter_list|(
name|FunctionExpression
argument_list|<
name|?
argument_list|>
name|expression
parameter_list|)
block|{
return|return
name|requireNonNull
argument_list|(
name|expression
operator|.
name|parameterList
argument_list|,
parameter_list|()
lambda|->
literal|"parameterList in "
operator|+
name|expression
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Expression
name|getTargetExpression
parameter_list|(
name|MethodCallExpression
name|call
parameter_list|)
block|{
return|return
name|requireNonNull
argument_list|(
name|call
operator|.
name|targetExpression
argument_list|,
literal|"translation of static calls is not supported yet"
argument_list|)
return|;
block|}
name|RelOptTable
operator|.
name|ToRelContext
name|toRelContext
parameter_list|()
block|{
if|if
condition|(
name|preparingStmt
operator|instanceof
name|RelOptTable
operator|.
name|ViewExpander
condition|)
block|{
specifier|final
name|RelOptTable
operator|.
name|ViewExpander
name|viewExpander
init|=
operator|(
name|RelOptTable
operator|.
name|ViewExpander
operator|)
name|this
operator|.
name|preparingStmt
decl_stmt|;
return|return
name|ViewExpanders
operator|.
name|toRelContext
argument_list|(
name|viewExpander
argument_list|,
name|cluster
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ViewExpanders
operator|.
name|simpleContext
argument_list|(
name|cluster
argument_list|)
return|;
block|}
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|RelNode
name|translate
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
name|QueryableRelBuilder
argument_list|<
name|T
argument_list|>
name|translatorQueryable
init|=
operator|new
name|QueryableRelBuilder
argument_list|<>
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|translatorQueryable
operator|.
name|toRel
argument_list|(
name|queryable
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|translate
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|MethodCallExpression
condition|)
block|{
specifier|final
name|MethodCallExpression
name|call
init|=
operator|(
name|MethodCallExpression
operator|)
name|expression
decl_stmt|;
name|BuiltInMethod
name|method
init|=
name|BuiltInMethod
operator|.
name|MAP
operator|.
name|get
argument_list|(
name|call
operator|.
name|method
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"unknown method "
operator|+
name|call
operator|.
name|method
argument_list|)
throw|;
block|}
name|RelNode
name|input
decl_stmt|;
switch|switch
condition|(
name|method
condition|)
block|{
case|case
name|SELECT
case|:
name|input
operator|=
name|translate
argument_list|(
name|getTargetExpression
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|LogicalProject
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|toRex
argument_list|(
name|input
argument_list|,
operator|(
name|FunctionExpression
operator|)
name|call
operator|.
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
literal|null
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
return|;
case|case
name|WHERE
case|:
name|input
operator|=
name|translate
argument_list|(
name|getTargetExpression
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|LogicalFilter
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|toRex
argument_list|(
operator|(
name|FunctionExpression
operator|)
name|call
operator|.
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|input
argument_list|)
argument_list|)
return|;
case|case
name|AS_QUERYABLE
case|:
return|return
name|LogicalTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|RelOptTableImpl
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Types
operator|.
name|toClass
argument_list|(
name|getElementType
argument_list|(
name|call
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|getTargetExpression
argument_list|(
name|call
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
case|case
name|SCHEMA_GET_TABLE
case|:
return|return
name|LogicalTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|RelOptTableImpl
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|typeFactory
operator|.
name|createJavaType
argument_list|(
operator|(
name|Class
operator|)
name|requireNonNull
argument_list|(
operator|(
operator|(
name|ConstantExpression
operator|)
name|call
operator|.
name|expressions
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|value
argument_list|,
literal|"argument 1 (0-based) is null Class"
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|getTargetExpression
argument_list|(
name|call
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"unknown method "
operator|+
name|call
operator|.
name|method
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"unknown expression type "
operator|+
name|expression
operator|.
name|getNodeType
argument_list|()
argument_list|)
throw|;
block|}
specifier|private
specifier|static
name|Type
name|getElementType
parameter_list|(
name|MethodCallExpression
name|call
parameter_list|)
block|{
name|Type
name|type
init|=
name|getTargetExpression
argument_list|(
name|call
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|requireNonNull
argument_list|(
name|Types
operator|.
name|getElementType
argument_list|(
name|type
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"unable to figure out element type from "
operator|+
name|type
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|RexNode
argument_list|>
name|toRex
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|FunctionExpression
name|expression
parameter_list|)
block|{
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|rexBuilder
operator|.
name|makeRangeReference
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
name|CalcitePrepareImpl
operator|.
name|ScalarTranslator
name|translator
init|=
name|CalcitePrepareImpl
operator|.
name|EmptyScalarTranslator
operator|.
name|empty
argument_list|(
name|rexBuilder
argument_list|)
operator|.
name|bind
argument_list|(
name|getParameterList
argument_list|(
name|expression
argument_list|)
argument_list|,
name|list
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Expression
name|simple
init|=
name|Blocks
operator|.
name|simple
argument_list|(
name|getBody
argument_list|(
name|expression
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Expression
name|expression1
range|:
name|fieldExpressions
argument_list|(
name|simple
argument_list|)
control|)
block|{
name|rexList
operator|.
name|add
argument_list|(
name|translator
operator|.
name|toRex
argument_list|(
name|expression1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rexList
return|;
block|}
name|List
argument_list|<
name|Expression
argument_list|>
name|fieldExpressions
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|NewExpression
condition|)
block|{
comment|// Note: We are assuming that the arguments to the constructor
comment|// are the same order as the fields of the class.
return|return
operator|(
operator|(
name|NewExpression
operator|)
name|expression
operator|)
operator|.
name|arguments
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unsupported expression type "
operator|+
name|expression
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|RexNode
argument_list|>
name|toRexList
parameter_list|(
name|FunctionExpression
name|expression
parameter_list|,
name|RelNode
modifier|...
name|inputs
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeRangeReference
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|CalcitePrepareImpl
operator|.
name|EmptyScalarTranslator
operator|.
name|empty
argument_list|(
name|rexBuilder
argument_list|)
operator|.
name|bind
argument_list|(
name|getParameterList
argument_list|(
name|expression
argument_list|)
argument_list|,
name|list
argument_list|)
operator|.
name|toRexList
argument_list|(
name|getBody
argument_list|(
name|expression
argument_list|)
argument_list|)
return|;
block|}
name|RexNode
name|toRex
parameter_list|(
name|FunctionExpression
name|expression
parameter_list|,
name|RelNode
modifier|...
name|inputs
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeRangeReference
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|CalcitePrepareImpl
operator|.
name|EmptyScalarTranslator
operator|.
name|empty
argument_list|(
name|rexBuilder
argument_list|)
operator|.
name|bind
argument_list|(
name|getParameterList
argument_list|(
name|expression
argument_list|)
argument_list|,
name|list
argument_list|)
operator|.
name|toRex
argument_list|(
name|getBody
argument_list|(
name|expression
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

