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

begin_comment
comment|/**  * Builder for {@link BlockExpression}.  *  *<p>Has methods that help ensure that variable names are unique.</p>  */
end_comment

begin_class
specifier|public
class|class
name|BlockBuilder
block|{
specifier|final
name|List
argument_list|<
name|Statement
argument_list|>
name|statements
init|=
operator|new
name|ArrayList
argument_list|<
name|Statement
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|variables
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|optimizing
decl_stmt|;
comment|/**    * Creates a non-optimizing BlockBuilder.    */
specifier|public
name|BlockBuilder
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a BlockBuilder.    *    * @param optimizing Whether to eliminate common sub-expressions    */
specifier|public
name|BlockBuilder
parameter_list|(
name|boolean
name|optimizing
parameter_list|)
block|{
name|this
operator|.
name|optimizing
operator|=
name|optimizing
expr_stmt|;
block|}
comment|/**    * Clears this BlockBuilder.    */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|statements
operator|.
name|clear
argument_list|()
expr_stmt|;
name|variables
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Appends a block to a list of statements and returns an expression    * (possibly a variable) that represents the result of the newly added    * block.    */
specifier|public
name|Expression
name|append
parameter_list|(
name|String
name|name
parameter_list|,
name|BlockExpression
name|block
parameter_list|)
block|{
return|return
name|append
argument_list|(
name|name
argument_list|,
name|block
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Appends an expression to a list of statements, optionally optimizing it    * to a variable if it is used more than once.    *    * @param name Suggested variable name    * @param block Expression    * @param optimize Whether to try to optimize by assigning the expression to    * a variable. Do not do this if the expression has    * side-effects or a time-dependent value.    */
specifier|public
name|Expression
name|append
parameter_list|(
name|String
name|name
parameter_list|,
name|BlockExpression
name|block
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
if|if
condition|(
name|statements
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Statement
name|lastStatement
init|=
name|statements
operator|.
name|get
argument_list|(
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastStatement
operator|instanceof
name|GotoExpression
condition|)
block|{
comment|// convert "return expr;" into "expr;"
name|statements
operator|.
name|set
argument_list|(
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|Expressions
operator|.
name|statement
argument_list|(
operator|(
operator|(
name|GotoExpression
operator|)
name|lastStatement
operator|)
operator|.
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Expression
name|result
init|=
literal|null
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|ParameterExpression
argument_list|,
name|Expression
argument_list|>
name|replacements
init|=
operator|new
name|HashMap
argument_list|<
name|ParameterExpression
argument_list|,
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Visitor
name|visitor
init|=
operator|new
name|SubstituteVariableVisitor
argument_list|(
name|replacements
argument_list|)
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
name|block
operator|.
name|statements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Statement
name|statement
init|=
name|block
operator|.
name|statements
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|replacements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Save effort, and only substitute variables if there are some.
name|statement
operator|=
name|statement
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|statement
operator|instanceof
name|DeclarationExpression
condition|)
block|{
name|DeclarationExpression
name|declaration
init|=
operator|(
name|DeclarationExpression
operator|)
name|statement
decl_stmt|;
if|if
condition|(
name|variables
operator|.
name|contains
argument_list|(
name|declaration
operator|.
name|parameter
operator|.
name|name
argument_list|)
condition|)
block|{
name|Expression
name|x
init|=
name|append
argument_list|(
name|newName
argument_list|(
name|declaration
operator|.
name|parameter
operator|.
name|name
argument_list|,
name|optimize
argument_list|)
argument_list|,
name|declaration
operator|.
name|initializer
argument_list|)
decl_stmt|;
name|statement
operator|=
literal|null
expr_stmt|;
name|result
operator|=
name|x
expr_stmt|;
name|replacements
operator|.
name|put
argument_list|(
name|declaration
operator|.
name|parameter
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|add
argument_list|(
name|statement
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|add
argument_list|(
name|statement
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
name|block
operator|.
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|statement
operator|instanceof
name|DeclarationExpression
condition|)
block|{
name|result
operator|=
operator|(
operator|(
name|DeclarationExpression
operator|)
name|statement
operator|)
operator|.
name|parameter
expr_stmt|;
block|}
if|else if
condition|(
name|statement
operator|instanceof
name|GotoExpression
condition|)
block|{
name|statements
operator|.
name|remove
argument_list|(
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|append_
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|GotoExpression
operator|)
name|statement
operator|)
operator|.
name|expression
argument_list|,
name|optimize
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|instanceof
name|ParameterExpression
operator|||
name|result
operator|instanceof
name|ConstantExpression
condition|)
block|{
comment|// already simple; no need to declare a variable or
comment|// even to evaluate the expression
block|}
else|else
block|{
name|DeclarationExpression
name|declare
init|=
name|Expressions
operator|.
name|declare
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|newName
argument_list|(
name|name
argument_list|,
name|optimize
argument_list|)
argument_list|,
name|result
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|declare
argument_list|)
expr_stmt|;
name|result
operator|=
name|declare
operator|.
name|parameter
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// not an expression -- result remains null
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Appends an expression to a list of statements, and returns an expression    * (possibly a variable) that represents the result of the newly added    * block.    */
specifier|public
name|Expression
name|append
parameter_list|(
name|String
name|name
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|append
argument_list|(
name|name
argument_list|,
name|expression
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Appends an expression to a list of statements, optionally optimizing if    * the expression is used more than once.    */
specifier|public
name|Expression
name|append
parameter_list|(
name|String
name|name
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
if|if
condition|(
name|statements
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Statement
name|lastStatement
init|=
name|statements
operator|.
name|get
argument_list|(
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastStatement
operator|instanceof
name|GotoExpression
condition|)
block|{
comment|// convert "return expr;" into "expr;"
name|statements
operator|.
name|set
argument_list|(
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|Expressions
operator|.
name|statement
argument_list|(
operator|(
operator|(
name|GotoExpression
operator|)
name|lastStatement
operator|)
operator|.
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|append_
argument_list|(
name|name
argument_list|,
name|expression
argument_list|,
name|optimize
argument_list|)
return|;
block|}
specifier|private
name|Expression
name|append_
parameter_list|(
name|String
name|name
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
comment|// We treat "1" and "null" as atoms, but not "(Comparator) null".
if|if
condition|(
name|expression
operator|instanceof
name|ParameterExpression
operator|||
operator|(
name|expression
operator|instanceof
name|ConstantExpression
operator|&&
operator|(
operator|(
operator|(
name|ConstantExpression
operator|)
name|expression
operator|)
operator|.
name|value
operator|!=
literal|null
operator|||
name|expression
operator|.
name|type
operator|==
name|Object
operator|.
name|class
operator|)
operator|)
condition|)
block|{
comment|// already simple; no need to declare a variable or
comment|// even to evaluate the expression
return|return
name|expression
return|;
block|}
if|if
condition|(
name|optimizing
condition|)
block|{
for|for
control|(
name|Statement
name|statement
range|:
name|statements
control|)
block|{
if|if
condition|(
name|statement
operator|instanceof
name|DeclarationExpression
condition|)
block|{
name|DeclarationExpression
name|decl
init|=
operator|(
name|DeclarationExpression
operator|)
name|statement
decl_stmt|;
if|if
condition|(
operator|(
name|decl
operator|.
name|modifiers
operator|&
name|Modifier
operator|.
name|FINAL
operator|)
operator|!=
literal|0
operator|&&
name|decl
operator|.
name|initializer
operator|!=
literal|null
operator|&&
name|decl
operator|.
name|initializer
operator|.
name|equals
argument_list|(
name|expression
argument_list|)
condition|)
block|{
return|return
name|decl
operator|.
name|parameter
return|;
block|}
block|}
block|}
block|}
name|DeclarationExpression
name|declare
init|=
name|Expressions
operator|.
name|declare
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|newName
argument_list|(
name|name
argument_list|,
name|optimize
argument_list|)
argument_list|,
name|expression
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|declare
argument_list|)
expr_stmt|;
return|return
name|declare
operator|.
name|parameter
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Statement
name|statement
parameter_list|)
block|{
name|statements
operator|.
name|add
argument_list|(
name|statement
argument_list|)
expr_stmt|;
if|if
condition|(
name|statement
operator|instanceof
name|DeclarationExpression
condition|)
block|{
name|String
name|name
init|=
operator|(
operator|(
name|DeclarationExpression
operator|)
name|statement
operator|)
operator|.
name|parameter
operator|.
name|name
decl_stmt|;
if|if
condition|(
operator|!
name|variables
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"duplicate variable "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a block consisting of the current list of statements.    */
specifier|public
name|BlockExpression
name|toBlock
parameter_list|()
block|{
if|if
condition|(
name|optimizing
condition|)
block|{
name|optimize
argument_list|()
expr_stmt|;
block|}
return|return
name|Expressions
operator|.
name|block
argument_list|(
name|statements
argument_list|)
return|;
block|}
comment|/**    * Optimizes the list of statements. If an expression is used only once,    * it is inlined.    */
specifier|private
name|void
name|optimize
parameter_list|()
block|{
name|List
argument_list|<
name|Slot
argument_list|>
name|slots
init|=
operator|new
name|ArrayList
argument_list|<
name|Slot
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|UseCounter
name|useCounter
init|=
operator|new
name|UseCounter
argument_list|()
decl_stmt|;
for|for
control|(
name|Statement
name|statement
range|:
name|statements
control|)
block|{
if|if
condition|(
name|statement
operator|instanceof
name|DeclarationExpression
condition|)
block|{
specifier|final
name|Slot
name|slot
init|=
operator|new
name|Slot
argument_list|(
operator|(
name|DeclarationExpression
operator|)
name|statement
argument_list|)
decl_stmt|;
name|useCounter
operator|.
name|map
operator|.
name|put
argument_list|(
name|slot
operator|.
name|parameter
argument_list|,
name|slot
argument_list|)
expr_stmt|;
name|slots
operator|.
name|add
argument_list|(
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Statement
name|statement
range|:
name|statements
control|)
block|{
name|statement
operator|.
name|accept
argument_list|(
name|useCounter
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|ParameterExpression
argument_list|,
name|Expression
argument_list|>
name|subMap
init|=
operator|new
name|HashMap
argument_list|<
name|ParameterExpression
argument_list|,
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|SubstituteVariableVisitor
name|visitor
init|=
operator|new
name|SubstituteVariableVisitor
argument_list|(
name|subMap
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|Statement
argument_list|>
name|oldStatements
init|=
operator|new
name|ArrayList
argument_list|<
name|Statement
argument_list|>
argument_list|(
name|statements
argument_list|)
decl_stmt|;
name|statements
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Statement
name|oldStatement
range|:
name|oldStatements
control|)
block|{
if|if
condition|(
name|oldStatement
operator|instanceof
name|DeclarationExpression
condition|)
block|{
name|DeclarationExpression
name|statement
init|=
operator|(
name|DeclarationExpression
operator|)
name|oldStatement
decl_stmt|;
specifier|final
name|Slot
name|slot
init|=
name|useCounter
operator|.
name|map
operator|.
name|get
argument_list|(
name|statement
operator|.
name|parameter
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|slot
operator|.
name|count
decl_stmt|;
if|if
condition|(
name|Expressions
operator|.
name|isConstantNull
argument_list|(
name|slot
operator|.
name|expression
argument_list|)
condition|)
block|{
comment|// Don't allow 'final Type t = null' to be inlined. There
comment|// is an implicit cast.
name|count
operator|=
literal|100
expr_stmt|;
block|}
if|if
condition|(
name|statement
operator|.
name|parameter
operator|.
name|name
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
comment|// Don't inline variables whose name begins with "_". This
comment|// is a hacky way to prevent inlining. E.g.
comment|//   final int _count = collection.size();
comment|//   foo(collection);
comment|//   return collection.size() - _count;
name|count
operator|=
literal|100
expr_stmt|;
block|}
if|if
condition|(
name|slot
operator|.
name|expression
operator|instanceof
name|NewExpression
operator|&&
operator|(
operator|(
name|NewExpression
operator|)
name|slot
operator|.
name|expression
operator|)
operator|.
name|memberDeclarations
operator|!=
literal|null
condition|)
block|{
comment|// Don't inline anonymous inner classes. Janino gets
comment|// confused referencing variables from deeply nested
comment|// anonymous classes.
name|count
operator|=
literal|100
expr_stmt|;
block|}
switch|switch
condition|(
name|count
condition|)
block|{
case|case
literal|0
case|:
comment|// Only declared, never used. Throw away declaration.
break|break;
case|case
literal|1
case|:
comment|// declared, used once. inline it.
name|subMap
operator|.
name|put
argument_list|(
name|slot
operator|.
name|parameter
argument_list|,
name|slot
operator|.
name|expression
argument_list|)
expr_stmt|;
break|break;
default|default:
name|statements
operator|.
name|add
argument_list|(
name|statement
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|statements
operator|.
name|add
argument_list|(
name|oldStatement
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|subMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|oldStatements
operator|.
name|clear
argument_list|()
expr_stmt|;
name|oldStatements
operator|.
name|addAll
argument_list|(
name|statements
argument_list|)
expr_stmt|;
name|statements
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Statement
name|oldStatement
range|:
name|oldStatements
control|)
block|{
name|statements
operator|.
name|add
argument_list|(
name|oldStatement
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Creates a name for a new variable, unique within this block.    */
specifier|private
name|String
name|newName
parameter_list|(
name|String
name|suggestion
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
if|if
condition|(
operator|!
name|optimize
operator|&&
operator|!
name|suggestion
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
comment|// "_" prefix reminds us not to consider the variable for inlining
name|suggestion
operator|=
literal|'_'
operator|+
name|suggestion
expr_stmt|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|candidate
init|=
name|suggestion
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|variables
operator|.
name|contains
argument_list|(
name|candidate
argument_list|)
condition|)
block|{
return|return
name|candidate
return|;
block|}
name|candidate
operator|=
name|suggestion
operator|+
operator|(
name|i
operator|++
operator|)
expr_stmt|;
block|}
block|}
specifier|public
name|BlockBuilder
name|append
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|add
argument_list|(
name|expression
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
specifier|static
class|class
name|SubstituteVariableVisitor
extends|extends
name|Visitor
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|ParameterExpression
argument_list|,
name|Expression
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ParameterExpression
argument_list|,
name|Boolean
argument_list|>
name|actives
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|ParameterExpression
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SubstituteVariableVisitor
parameter_list|(
name|Map
argument_list|<
name|ParameterExpression
argument_list|,
name|Expression
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|ParameterExpression
name|parameterExpression
parameter_list|)
block|{
name|Expression
name|e
init|=
name|map
operator|.
name|get
argument_list|(
name|parameterExpression
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|Boolean
name|put
init|=
name|actives
operator|.
name|put
argument_list|(
name|parameterExpression
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|put
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"recursive expansion of "
operator|+
name|parameterExpression
operator|+
literal|" in "
operator|+
name|actives
operator|.
name|keySet
argument_list|()
argument_list|)
throw|;
block|}
comment|// recursively substitute
return|return
name|e
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
finally|finally
block|{
name|actives
operator|.
name|remove
argument_list|(
name|parameterExpression
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|parameterExpression
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|UseCounter
extends|extends
name|Visitor
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|ParameterExpression
argument_list|,
name|Slot
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|ParameterExpression
argument_list|,
name|Slot
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Expression
name|visit
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|)
block|{
specifier|final
name|Slot
name|slot
init|=
name|map
operator|.
name|get
argument_list|(
name|parameter
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|!=
literal|null
condition|)
block|{
comment|// Count use of parameter, if it's registered. It's OK if
comment|// parameter is not registered. It might be beyond the control
comment|// of this block.
name|slot
operator|.
name|count
operator|++
expr_stmt|;
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|parameter
argument_list|)
return|;
block|}
block|}
comment|/**    * Workspace for optimization.    */
specifier|private
specifier|static
class|class
name|Slot
block|{
specifier|private
specifier|final
name|ParameterExpression
name|parameter
decl_stmt|;
specifier|private
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|public
name|Slot
parameter_list|(
name|DeclarationExpression
name|declarationExpression
parameter_list|)
block|{
name|this
operator|.
name|parameter
operator|=
name|declarationExpression
operator|.
name|parameter
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|declarationExpression
operator|.
name|initializer
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End BlockBuilder.java
end_comment

end_unit

