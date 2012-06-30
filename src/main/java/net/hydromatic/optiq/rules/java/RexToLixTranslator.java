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
name|rules
operator|.
name|java
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
name|*
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
name|SqlOperator
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Modifier
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

begin_import
import|import static
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|ExpressionType
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Translates {@link org.eigenbase.rex.RexNode REX expressions} to  * {@link net.hydromatic.linq4j.expressions.Expression linq4j expressions}.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|RexToLixTranslator
block|{
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|Method
argument_list|,
name|SqlOperator
argument_list|>
name|JAVA_TO_SQL_METHOD_MAP
init|=
name|Util
operator|.
expr|<
name|Method
decl_stmt|,
name|SqlOperator
decl|>
name|mapOf
argument_list|(
name|findMethod
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"toUpperCase"
argument_list|)
argument_list|,
name|upperFunc
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|SqlOperator
argument_list|,
name|ExpressionType
argument_list|>
name|SQL_TO_LINQ_OPERATOR_MAP
init|=
name|Util
operator|.
expr|<
name|SqlOperator
decl_stmt|,
name|ExpressionType
decl|>
name|mapOf
argument_list|(
name|andOperator
argument_list|,
name|AndAlso
argument_list|,
name|orOperator
argument_list|,
name|OrElse
argument_list|,
name|lessThanOperator
argument_list|,
name|LessThan
argument_list|,
name|lessThanOrEqualOperator
argument_list|,
name|LessThanOrEqual
argument_list|,
name|greaterThanOperator
argument_list|,
name|GreaterThan
argument_list|,
name|greaterThanOrEqualOperator
argument_list|,
name|GreaterThanOrEqual
argument_list|,
name|equalsOperator
argument_list|,
name|Equal
argument_list|,
name|notEqualsOperator
argument_list|,
name|NotEqual
argument_list|,
name|notOperator
argument_list|,
name|Not
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|SqlOperator
argument_list|,
name|Method
argument_list|>
name|SQL_OP_TO_JAVA_METHOD_MAP
init|=
operator|new
name|HashMap
argument_list|<
name|SqlOperator
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Method
argument_list|,
name|SqlOperator
argument_list|>
name|entry
range|:
name|JAVA_TO_SQL_METHOD_MAP
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SQL_OP_TO_JAVA_METHOD_MAP
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Slot
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|RexNode
argument_list|,
name|Slot
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|RexProgram
name|program
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Slot
argument_list|>
name|inputSlots
init|=
operator|new
name|ArrayList
argument_list|<
name|Slot
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Set of expressions which are to be translated inline. That is, they      * should not be assigned to variables on first use. At present, the      * algorithm is to use a first pass to determine how many times each      * expression is used, and expressions are marked for inlining if they are      * used at most once. */
specifier|private
specifier|final
name|Set
argument_list|<
name|RexNode
argument_list|>
name|inlineRexSet
init|=
operator|new
name|HashSet
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Statement
argument_list|>
name|list
decl_stmt|;
specifier|private
specifier|static
name|Method
name|findMethod
parameter_list|(
name|Class
argument_list|<
name|String
argument_list|>
name|clazz
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|clazz
operator|.
name|getMethod
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
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
block|}
specifier|private
name|RexToLixTranslator
parameter_list|(
name|RexProgram
name|program
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|inputs
parameter_list|)
block|{
name|this
operator|.
name|program
operator|=
name|program
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
for|for
control|(
name|Expression
name|input
range|:
name|inputs
control|)
block|{
name|inputSlots
operator|.
name|add
argument_list|(
operator|new
name|Slot
argument_list|(
literal|null
argument_list|,
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Translates a {@link RexProgram} to a sequence of expressions and      * declarations.      *      * @param inputs Variables holding the current record of each input      * relational expression      * @param program Program to be translated      * @return Sequence of expressions, optional condition      */
specifier|public
specifier|static
name|List
argument_list|<
name|Expression
argument_list|>
name|translateProjects
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|inputs
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|Statement
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|new
name|RexToLixTranslator
argument_list|(
name|program
argument_list|,
name|typeFactory
argument_list|,
name|inputs
argument_list|)
operator|.
name|translate
argument_list|(
name|list
argument_list|,
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Expression
name|translate
parameter_list|(
name|RexNode
name|expr
parameter_list|)
block|{
name|Slot
name|slot
init|=
name|map
operator|.
name|get
argument_list|(
name|expr
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|==
literal|null
condition|)
block|{
name|Expression
name|expression
init|=
name|translate0
argument_list|(
name|expr
argument_list|)
decl_stmt|;
assert|assert
name|expression
operator|!=
literal|null
assert|;
specifier|final
name|ParameterExpression
name|parameter
decl_stmt|;
if|if
condition|(
operator|!
name|inlineRexSet
operator|.
name|contains
argument_list|(
name|expr
argument_list|)
condition|)
block|{
name|parameter
operator|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|expression
operator|.
name|getType
argument_list|()
argument_list|,
literal|"v"
operator|+
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parameter
operator|=
literal|null
expr_stmt|;
block|}
name|slot
operator|=
operator|new
name|Slot
argument_list|(
name|parameter
argument_list|,
name|expression
argument_list|)
expr_stmt|;
if|if
condition|(
name|parameter
operator|!=
literal|null
operator|&&
name|list
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|slot
operator|.
name|parameterExpression
argument_list|,
name|slot
operator|.
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|expr
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
name|slot
operator|.
name|count
operator|++
expr_stmt|;
return|return
name|slot
operator|.
name|parameterExpression
operator|!=
literal|null
condition|?
name|slot
operator|.
name|parameterExpression
else|:
name|slot
operator|.
name|expression
return|;
block|}
specifier|private
name|Expression
name|translate0
parameter_list|(
name|RexNode
name|expr
parameter_list|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|RexInputRef
condition|)
block|{
comment|// TODO: multiple inputs, e.g. joins
specifier|final
name|Expression
name|input
init|=
name|getInput
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|index
init|=
operator|(
operator|(
name|RexInputRef
operator|)
name|expr
operator|)
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|RelDataTypeField
name|field
init|=
name|program
operator|.
name|getInputRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|getType
argument_list|()
operator|==
name|Object
index|[]
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|arrayIndex
argument_list|(
name|input
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|field
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Types
operator|.
name|box
argument_list|(
name|JavaRules
operator|.
name|EnumUtil
operator|.
name|javaClass
argument_list|(
name|typeFactory
argument_list|,
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Expressions
operator|.
name|field
argument_list|(
name|input
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|expr
operator|instanceof
name|RexLocalRef
condition|)
block|{
return|return
name|translate
argument_list|(
name|program
operator|.
name|getExprList
argument_list|()
operator|.
name|get
argument_list|(
operator|(
operator|(
name|RexLocalRef
operator|)
name|expr
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|expr
operator|instanceof
name|RexLiteral
condition|)
block|{
return|return
name|Expressions
operator|.
name|constant
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|expr
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|expr
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
name|expr
decl_stmt|;
specifier|final
name|SqlOperator
name|operator
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
specifier|final
name|ExpressionType
name|expressionType
init|=
name|SQL_TO_LINQ_OPERATOR_MAP
operator|.
name|get
argument_list|(
name|operator
argument_list|)
decl_stmt|;
if|if
condition|(
name|expressionType
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|operator
operator|.
name|getSyntax
argument_list|()
condition|)
block|{
case|case
name|Binary
case|:
return|return
name|Expressions
operator|.
name|makeBinary
argument_list|(
name|expressionType
argument_list|,
name|translate
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|translate
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
index|[
literal|1
index|]
argument_list|)
argument_list|)
return|;
case|case
name|Postfix
case|:
case|case
name|Prefix
case|:
return|return
name|Expressions
operator|.
name|makeUnary
argument_list|(
name|expressionType
argument_list|,
name|translate
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown syntax "
operator|+
name|operator
operator|.
name|getSyntax
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|Method
name|method
init|=
name|SQL_OP_TO_JAVA_METHOD_MAP
operator|.
name|get
argument_list|(
name|operator
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Expression
argument_list|>
name|exprs
init|=
name|translateList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|call
operator|.
name|operands
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|?
name|Expressions
operator|.
name|call
argument_list|(
name|exprs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|method
argument_list|,
name|exprs
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|exprs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
else|:
name|Expressions
operator|.
name|call
argument_list|(
name|method
argument_list|,
name|exprs
argument_list|)
return|;
block|}
switch|switch
condition|(
name|expr
operator|.
name|getKind
argument_list|()
condition|)
block|{
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot translate expression "
operator|+
name|expr
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot translate expression "
operator|+
name|expr
argument_list|)
throw|;
block|}
comment|/**      * Gets the expression for an input and counts it.      *      * @param index Input ordinal      * @return Expression to which an input should be translated      */
specifier|private
name|Expression
name|getInput
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|Slot
name|slot
init|=
name|inputSlots
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|slot
operator|.
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|slot
operator|.
name|count
operator|>
literal|1
operator|&&
name|slot
operator|.
name|parameterExpression
operator|==
literal|null
condition|)
block|{
name|slot
operator|.
name|parameterExpression
operator|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|slot
operator|.
name|expression
operator|.
name|type
argument_list|,
literal|"current"
operator|+
name|index
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|slot
operator|.
name|parameterExpression
argument_list|,
name|slot
operator|.
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|slot
operator|.
name|parameterExpression
operator|!=
literal|null
condition|?
name|slot
operator|.
name|parameterExpression
else|:
name|slot
operator|.
name|expression
return|;
block|}
specifier|private
name|List
argument_list|<
name|Expression
argument_list|>
name|translateList
parameter_list|(
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
name|Expression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
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
name|translate
argument_list|(
name|rex
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|private
name|List
argument_list|<
name|Expression
argument_list|>
name|translate
parameter_list|(
name|List
argument_list|<
name|Statement
argument_list|>
name|list
parameter_list|,
name|List
argument_list|<
name|RexLocalRef
argument_list|>
name|rexList
parameter_list|)
block|{
comment|// First pass. Count how many times each sub-expression is used.
name|this
operator|.
name|list
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|RexNode
name|rexExpr
range|:
name|rexList
control|)
block|{
name|translate
argument_list|(
name|rexExpr
argument_list|)
expr_stmt|;
block|}
comment|// Mark expressions as inline if they are not used more than once.
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RexNode
argument_list|,
name|Slot
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|count
operator|<
literal|2
condition|)
block|{
name|inlineRexSet
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Second pass. When translating each expression, if it is used more
comment|// than once, the first time it is encountered, add a declaration to the
comment|// list and set its usage count to 0.
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|translateds
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|rexExpr
range|:
name|rexList
control|)
block|{
name|translateds
operator|.
name|add
argument_list|(
name|translate
argument_list|(
name|rexExpr
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|translateds
return|;
block|}
specifier|public
specifier|static
name|Expression
name|translateCondition
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|inputs
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|Statement
argument_list|>
name|list
parameter_list|)
block|{
name|List
argument_list|<
name|Expression
argument_list|>
name|x
init|=
operator|new
name|RexToLixTranslator
argument_list|(
name|program
argument_list|,
name|typeFactory
argument_list|,
name|inputs
argument_list|)
operator|.
name|translate
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|program
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|x
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|x
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|Slot
block|{
name|ParameterExpression
name|parameterExpression
decl_stmt|;
name|Expression
name|expression
decl_stmt|;
name|int
name|count
decl_stmt|;
specifier|public
name|Slot
parameter_list|(
name|ParameterExpression
name|parameterExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|this
operator|.
name|parameterExpression
operator|=
name|parameterExpression
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexToLixTranslator.java
end_comment

end_unit

