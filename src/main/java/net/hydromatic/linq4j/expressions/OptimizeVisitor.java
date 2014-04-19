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
name|linq4j
operator|.
name|expressions
package|;
end_package

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
name|Equal
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
name|NotEqual
import|;
end_import

begin_comment
comment|/**  * Visitor that optimizes expressions.  *<p/>  *<p>The optimizations are essential, not mere tweaks. Without  * optimization, expressions such as {@code false == null} will be left in,  * which are invalid to Janino (because it does not automatically box  * primitives).</p>  */
end_comment

begin_class
specifier|public
class|class
name|OptimizeVisitor
extends|extends
name|Visitor
block|{
specifier|public
specifier|static
specifier|final
name|ConstantExpression
name|FALSE_EXPR
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ConstantExpression
name|TRUE_EXPR
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MemberExpression
name|BOXED_FALSE_EXPR
init|=
name|Expressions
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|Boolean
operator|.
name|class
argument_list|,
literal|"FALSE"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MemberExpression
name|BOXED_TRUE_EXPR
init|=
name|Expressions
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|Boolean
operator|.
name|class
argument_list|,
literal|"TRUE"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Statement
name|EMPTY_STATEMENT
init|=
name|Expressions
operator|.
name|statement
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Method
argument_list|>
name|KNOWN_NON_NULL_METHODS
init|=
operator|new
name|HashSet
argument_list|<
name|Method
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|Class
name|aClass
range|:
operator|new
name|Class
index|[]
block|{
name|Boolean
operator|.
name|class
block|,
name|Byte
operator|.
name|class
block|,
name|Short
operator|.
name|class
block|,
name|Integer
operator|.
name|class
block|,
name|Long
operator|.
name|class
block|,
name|String
operator|.
name|class
block|}
control|)
block|{
for|for
control|(
name|Method
name|method
range|:
name|aClass
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
literal|"valueOf"
operator|.
name|equals
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|KNOWN_NON_NULL_METHODS
operator|.
name|add
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|ExpressionType
argument_list|,
name|ExpressionType
argument_list|>
name|NOT_BINARY_COMPLEMENT
init|=
operator|new
name|EnumMap
argument_list|<
name|ExpressionType
argument_list|,
name|ExpressionType
argument_list|>
argument_list|(
name|ExpressionType
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|addComplement
argument_list|(
name|ExpressionType
operator|.
name|Equal
argument_list|,
name|ExpressionType
operator|.
name|NotEqual
argument_list|)
expr_stmt|;
name|addComplement
argument_list|(
name|ExpressionType
operator|.
name|GreaterThanOrEqual
argument_list|,
name|ExpressionType
operator|.
name|LessThan
argument_list|)
expr_stmt|;
name|addComplement
argument_list|(
name|ExpressionType
operator|.
name|GreaterThan
argument_list|,
name|ExpressionType
operator|.
name|LessThanOrEqual
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addComplement
parameter_list|(
name|ExpressionType
name|eq
parameter_list|,
name|ExpressionType
name|ne
parameter_list|)
block|{
name|NOT_BINARY_COMPLEMENT
operator|.
name|put
argument_list|(
name|eq
argument_list|,
name|ne
argument_list|)
expr_stmt|;
name|NOT_BINARY_COMPLEMENT
operator|.
name|put
argument_list|(
name|ne
argument_list|,
name|eq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|TernaryExpression
name|ternary
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|,
name|Expression
name|expression2
parameter_list|)
block|{
switch|switch
condition|(
name|ternary
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Conditional
case|:
name|Boolean
name|always
init|=
name|always
argument_list|(
name|expression0
argument_list|)
decl_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
condition|)
block|{
comment|// true ? y : z  ===  y
comment|// false ? y : z  === z
return|return
name|always
condition|?
name|expression1
else|:
name|expression2
return|;
block|}
if|if
condition|(
name|expression1
operator|.
name|equals
argument_list|(
name|expression2
argument_list|)
condition|)
block|{
comment|// a ? b : b   ===   b
return|return
name|expression1
return|;
block|}
comment|// !a ? b : c == a ? c : b
if|if
condition|(
name|expression0
operator|instanceof
name|UnaryExpression
condition|)
block|{
name|UnaryExpression
name|una
init|=
operator|(
name|UnaryExpression
operator|)
name|expression0
decl_stmt|;
if|if
condition|(
name|una
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Not
condition|)
block|{
return|return
name|Expressions
operator|.
name|makeTernary
argument_list|(
name|ternary
operator|.
name|getNodeType
argument_list|()
argument_list|,
name|una
operator|.
name|expression
argument_list|,
name|expression2
argument_list|,
name|expression1
argument_list|)
return|;
block|}
block|}
comment|// a ? true : b  === a || b
comment|// a ? false : b === !a&& b
name|always
operator|=
name|always
argument_list|(
name|expression1
argument_list|)
expr_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
operator|&&
name|isKnownNotNull
argument_list|(
name|expression2
argument_list|)
condition|)
block|{
return|return
operator|(
name|always
condition|?
name|Expressions
operator|.
name|orElse
argument_list|(
name|expression0
argument_list|,
name|expression2
argument_list|)
else|:
name|Expressions
operator|.
name|andAlso
argument_list|(
name|Expressions
operator|.
name|not
argument_list|(
name|expression0
argument_list|)
argument_list|,
name|expression2
argument_list|)
operator|)
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// a ? b : true  === !a || b
comment|// a ? b : false === a&& b
name|always
operator|=
name|always
argument_list|(
name|expression2
argument_list|)
expr_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
operator|&&
name|isKnownNotNull
argument_list|(
name|expression1
argument_list|)
condition|)
block|{
return|return
operator|(
name|always
condition|?
name|Expressions
operator|.
name|orElse
argument_list|(
name|Expressions
operator|.
name|not
argument_list|(
name|expression0
argument_list|)
argument_list|,
name|expression1
argument_list|)
else|:
name|Expressions
operator|.
name|andAlso
argument_list|(
name|expression0
argument_list|,
name|expression1
argument_list|)
operator|)
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
if|if
condition|(
name|expression0
operator|instanceof
name|BinaryExpression
operator|&&
operator|(
name|expression0
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Equal
operator|||
name|expression0
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|NotEqual
operator|)
condition|)
block|{
name|BinaryExpression
name|cmp
init|=
operator|(
name|BinaryExpression
operator|)
name|expression0
decl_stmt|;
name|Expression
name|expr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|eq
argument_list|(
name|cmp
operator|.
name|expression0
argument_list|,
name|expression2
argument_list|)
operator|&&
name|eq
argument_list|(
name|cmp
operator|.
name|expression1
argument_list|,
name|expression1
argument_list|)
condition|)
block|{
comment|// a == b ? b : a === a (hint: if a==b, then a == b ? a : a)
comment|// a != b ? b : a === b (hint: if a==b, then a != b ? b : b)
name|expr
operator|=
name|expression0
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Equal
condition|?
name|expression2
else|:
name|expression1
expr_stmt|;
block|}
if|if
condition|(
name|eq
argument_list|(
name|cmp
operator|.
name|expression0
argument_list|,
name|expression1
argument_list|)
operator|&&
name|eq
argument_list|(
name|cmp
operator|.
name|expression1
argument_list|,
name|expression2
argument_list|)
condition|)
block|{
comment|// a == b ? a : b === b (hint: if a==b, then a == b ? b : b)
comment|// a != b ? a : b === a (hint: if a==b, then a == b ? a : a)
name|expr
operator|=
name|expression0
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Equal
condition|?
name|expression2
else|:
name|expression1
expr_stmt|;
block|}
if|if
condition|(
name|expr
operator|!=
literal|null
condition|)
block|{
return|return
name|expr
return|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|ternary
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|,
name|expression2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|BinaryExpression
name|binary
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|)
block|{
comment|//
name|Expression
name|result
decl_stmt|;
switch|switch
condition|(
name|binary
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|AndAlso
case|:
case|case
name|OrElse
case|:
if|if
condition|(
name|eq
argument_list|(
name|expression0
argument_list|,
name|expression1
argument_list|)
condition|)
block|{
return|return
name|expression0
return|;
block|}
block|}
switch|switch
condition|(
name|binary
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Equal
case|:
case|case
name|NotEqual
case|:
if|if
condition|(
name|eq
argument_list|(
name|expression0
argument_list|,
name|expression1
argument_list|)
condition|)
block|{
return|return
name|binary
operator|.
name|getNodeType
argument_list|()
operator|==
name|Equal
condition|?
name|TRUE_EXPR
else|:
name|FALSE_EXPR
return|;
block|}
if|else if
condition|(
name|expression0
operator|instanceof
name|ConstantExpression
operator|&&
name|expression1
operator|instanceof
name|ConstantExpression
condition|)
block|{
name|ConstantExpression
name|c0
init|=
operator|(
name|ConstantExpression
operator|)
name|expression0
decl_stmt|;
name|ConstantExpression
name|c1
init|=
operator|(
name|ConstantExpression
operator|)
name|expression1
decl_stmt|;
if|if
condition|(
name|c0
operator|.
name|getType
argument_list|()
operator|==
name|c1
operator|.
name|getType
argument_list|()
operator|||
operator|!
operator|(
name|Primitive
operator|.
name|is
argument_list|(
name|c0
operator|.
name|getType
argument_list|()
argument_list|)
operator|||
name|Primitive
operator|.
name|is
argument_list|(
name|c1
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
condition|)
block|{
return|return
name|binary
operator|.
name|getNodeType
argument_list|()
operator|==
name|NotEqual
condition|?
name|TRUE_EXPR
else|:
name|FALSE_EXPR
return|;
block|}
block|}
if|if
condition|(
name|expression0
operator|instanceof
name|TernaryExpression
operator|&&
name|expression0
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Conditional
condition|)
block|{
name|TernaryExpression
name|ternary
init|=
operator|(
name|TernaryExpression
operator|)
name|expression0
decl_stmt|;
name|Expression
name|expr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|eq
argument_list|(
name|ternary
operator|.
name|expression1
argument_list|,
name|expression1
argument_list|)
condition|)
block|{
comment|// (a ? b : c) == b === a || c == b
name|expr
operator|=
name|Expressions
operator|.
name|orElse
argument_list|(
name|ternary
operator|.
name|expression0
argument_list|,
name|Expressions
operator|.
name|equal
argument_list|(
name|ternary
operator|.
name|expression2
argument_list|,
name|expression1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|eq
argument_list|(
name|ternary
operator|.
name|expression2
argument_list|,
name|expression1
argument_list|)
condition|)
block|{
comment|// (a ? b : c) == c === !a || b == c
name|expr
operator|=
name|Expressions
operator|.
name|orElse
argument_list|(
name|Expressions
operator|.
name|not
argument_list|(
name|ternary
operator|.
name|expression0
argument_list|)
argument_list|,
name|Expressions
operator|.
name|equal
argument_list|(
name|ternary
operator|.
name|expression1
argument_list|,
name|expression1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|binary
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|NotEqual
condition|)
block|{
name|expr
operator|=
name|Expressions
operator|.
name|not
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
return|return
name|expr
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|// drop down
case|case
name|AndAlso
case|:
case|case
name|OrElse
case|:
name|result
operator|=
name|visit0
argument_list|(
name|binary
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|result
operator|=
name|visit0
argument_list|(
name|binary
argument_list|,
name|expression1
argument_list|,
name|expression0
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|binary
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|)
return|;
block|}
specifier|private
name|Expression
name|visit0
parameter_list|(
name|BinaryExpression
name|binary
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|)
block|{
name|Boolean
name|always
decl_stmt|;
switch|switch
condition|(
name|binary
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|AndAlso
case|:
name|always
operator|=
name|always
argument_list|(
name|expression0
argument_list|)
expr_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
condition|)
block|{
return|return
name|always
condition|?
name|expression1
else|:
name|FALSE_EXPR
return|;
block|}
break|break;
case|case
name|OrElse
case|:
name|always
operator|=
name|always
argument_list|(
name|expression0
argument_list|)
expr_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
condition|)
block|{
comment|// true or x  --> true
comment|// false or x --> x
return|return
name|always
condition|?
name|TRUE_EXPR
else|:
name|expression1
return|;
block|}
break|break;
case|case
name|Equal
case|:
if|if
condition|(
name|isConstantNull
argument_list|(
name|expression1
argument_list|)
operator|&&
name|Primitive
operator|.
name|is
argument_list|(
name|expression0
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|FALSE_EXPR
return|;
block|}
comment|// a == true  -> a
comment|// a == false -> !a
name|always
operator|=
name|always
argument_list|(
name|expression0
argument_list|)
expr_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
condition|)
block|{
return|return
name|always
condition|?
name|expression1
else|:
name|Expressions
operator|.
name|not
argument_list|(
name|expression1
argument_list|)
return|;
block|}
break|break;
case|case
name|NotEqual
case|:
if|if
condition|(
name|isConstantNull
argument_list|(
name|expression1
argument_list|)
operator|&&
name|Primitive
operator|.
name|is
argument_list|(
name|expression0
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|TRUE_EXPR
return|;
block|}
comment|// a != true  -> !a
comment|// a != false -> a
name|always
operator|=
name|always
argument_list|(
name|expression0
argument_list|)
expr_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
condition|)
block|{
return|return
name|always
condition|?
name|Expressions
operator|.
name|not
argument_list|(
name|expression1
argument_list|)
else|:
name|expression1
return|;
block|}
break|break;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|UnaryExpression
name|unaryExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
switch|switch
condition|(
name|unaryExpression
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Convert
case|:
if|if
condition|(
name|expression
operator|.
name|getType
argument_list|()
operator|==
name|unaryExpression
operator|.
name|getType
argument_list|()
condition|)
block|{
return|return
name|expression
return|;
block|}
if|if
condition|(
name|expression
operator|instanceof
name|ConstantExpression
condition|)
block|{
return|return
name|Expressions
operator|.
name|constant
argument_list|(
operator|(
operator|(
name|ConstantExpression
operator|)
name|expression
operator|)
operator|.
name|value
argument_list|,
name|unaryExpression
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
break|break;
case|case
name|Not
case|:
name|Boolean
name|always
init|=
name|always
argument_list|(
name|expression
argument_list|)
decl_stmt|;
if|if
condition|(
name|always
operator|!=
literal|null
condition|)
block|{
return|return
name|always
condition|?
name|FALSE_EXPR
else|:
name|TRUE_EXPR
return|;
block|}
if|if
condition|(
name|expression
operator|instanceof
name|UnaryExpression
condition|)
block|{
name|UnaryExpression
name|arg
init|=
operator|(
name|UnaryExpression
operator|)
name|expression
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Not
condition|)
block|{
return|return
name|arg
operator|.
name|expression
return|;
block|}
block|}
if|if
condition|(
name|expression
operator|instanceof
name|BinaryExpression
condition|)
block|{
name|BinaryExpression
name|bin
init|=
operator|(
name|BinaryExpression
operator|)
name|expression
decl_stmt|;
name|ExpressionType
name|comp
init|=
name|NOT_BINARY_COMPLEMENT
operator|.
name|get
argument_list|(
name|bin
operator|.
name|getNodeType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|comp
operator|!=
literal|null
condition|)
block|{
return|return
name|Expressions
operator|.
name|makeBinary
argument_list|(
name|comp
argument_list|,
name|bin
operator|.
name|expression0
argument_list|,
name|bin
operator|.
name|expression1
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|unaryExpression
argument_list|,
name|expression
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Statement
name|visit
parameter_list|(
name|ConditionalStatement
name|conditionalStatement
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|list
parameter_list|)
block|{
comment|// if (false) {<-- remove branch
comment|// } if (true) {<-- stop here
comment|// } else if (...)
comment|// } else {...}
name|boolean
name|optimal
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|&&
name|optimal
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|Boolean
name|always
init|=
name|always
argument_list|(
operator|(
name|Expression
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|always
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|i
operator|==
literal|0
operator|&&
name|always
condition|)
block|{
comment|// when the very first test is always true, just return its statement
return|return
operator|(
name|Statement
operator|)
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
return|;
block|}
name|optimal
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|optimal
condition|)
block|{
comment|// Nothing to optimize
return|return
name|super
operator|.
name|visit
argument_list|(
name|conditionalStatement
argument_list|,
name|list
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Node
argument_list|>
name|newList
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// Iterate over all the tests, except the latest "else"
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
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
name|Expression
name|test
init|=
operator|(
name|Expression
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Node
name|stmt
init|=
name|list
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Boolean
name|always
init|=
name|always
argument_list|(
name|test
argument_list|)
decl_stmt|;
if|if
condition|(
name|always
operator|==
literal|null
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|newList
operator|.
name|add
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|always
condition|)
block|{
comment|// No need to verify other tests
name|newList
operator|.
name|add
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// We might have dangling "else", however if we have just single item
comment|// it means we have if (false) else if(false) else if (true) {...} code.
comment|// Then we just return statement from true branch
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|(
name|Statement
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|// Add "else" from original list
if|if
condition|(
name|newList
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|0
operator|&&
name|list
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|Node
name|elseBlock
init|=
name|list
operator|.
name|get
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|newList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|(
name|Statement
operator|)
name|elseBlock
return|;
block|}
name|newList
operator|.
name|add
argument_list|(
name|elseBlock
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY_STATEMENT
return|;
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|conditionalStatement
argument_list|,
name|newList
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isConstantNull
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|expression
operator|instanceof
name|ConstantExpression
operator|&&
operator|(
operator|(
name|ConstantExpression
operator|)
name|expression
operator|)
operator|.
name|value
operator|==
literal|null
return|;
block|}
comment|/**    * Returns whether an expression always evaluates to true or false.    * Assumes that expression has already been optimized.    */
specifier|private
specifier|static
name|Boolean
name|always
parameter_list|(
name|Expression
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|.
name|equals
argument_list|(
name|FALSE_EXPR
argument_list|)
operator|||
name|x
operator|.
name|equals
argument_list|(
name|BOXED_FALSE_EXPR
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
if|if
condition|(
name|x
operator|.
name|equals
argument_list|(
name|TRUE_EXPR
argument_list|)
operator|||
name|x
operator|.
name|equals
argument_list|(
name|BOXED_TRUE_EXPR
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Verifies if the expression always returns non-null result.    * For instance, primitive types cannot contain null values.    *    * @param expression expression to test    * @return true when the expression is known to be not-null    */
specifier|protected
name|boolean
name|isKnownNotNull
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|Primitive
operator|.
name|is
argument_list|(
name|expression
operator|.
name|getType
argument_list|()
argument_list|)
operator|||
name|always
argument_list|(
name|expression
argument_list|)
operator|!=
literal|null
operator|||
operator|(
name|expression
operator|instanceof
name|MethodCallExpression
operator|&&
name|KNOWN_NON_NULL_METHODS
operator|.
name|contains
argument_list|(
operator|(
operator|(
name|MethodCallExpression
operator|)
name|expression
operator|)
operator|.
name|method
argument_list|)
operator|)
return|;
block|}
comment|/**    * Treats two expressions equal even if they represent different null types    */
specifier|private
specifier|static
name|boolean
name|eq
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
operator|||
operator|(
name|a
operator|instanceof
name|ConstantExpression
operator|&&
name|b
operator|instanceof
name|ConstantExpression
operator|&&
operator|(
operator|(
name|ConstantExpression
operator|)
name|a
operator|)
operator|.
name|value
operator|==
operator|(
operator|(
name|ConstantExpression
operator|)
name|b
operator|)
operator|.
name|value
operator|)
return|;
block|}
block|}
end_class

end_unit

