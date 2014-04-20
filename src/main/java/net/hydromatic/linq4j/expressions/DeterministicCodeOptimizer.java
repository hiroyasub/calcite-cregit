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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
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
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Factors out deterministic expressions to final static fields.  * Instances of this class should not be reused, so new visitor should be  * created for optimizing a new expression tree.  */
end_comment

begin_class
specifier|public
class|class
name|DeterministicCodeOptimizer
extends|extends
name|Visitor
block|{
specifier|protected
specifier|final
name|DeterministicCodeOptimizer
name|parent
decl_stmt|;
comment|/**    * The map contains known to be effectively-final expression.    * The map uses identity equality.    * Typically the key is ParameterExpression, however there might be    * non-factored to final field expression that is known to be constant.    * For instance, cast expression will not be factored to a field,    * but we still need to track its constant status.    */
specifier|protected
specifier|final
name|Map
argument_list|<
name|Expression
argument_list|,
name|Boolean
argument_list|>
name|constants
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Expression
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * The map that de-duplicates expressions, so the same expressions may reuse    * the same final static fields.    */
specifier|protected
specifier|final
name|Map
argument_list|<
name|Expression
argument_list|,
name|ParameterExpression
argument_list|>
name|dedup
init|=
operator|new
name|HashMap
argument_list|<
name|Expression
argument_list|,
name|ParameterExpression
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * The map of all the added final static fields. Allows to identify if the    * name is occupied or not.    */
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ParameterExpression
argument_list|>
name|fieldsByName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ParameterExpression
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * The list of new final static fields to be added to the current class.    */
specifier|protected
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|addedDeclarations
init|=
operator|new
name|ArrayList
argument_list|<
name|MemberDeclaration
argument_list|>
argument_list|()
decl_stmt|;
comment|// Pre-compiled patterns for generation names for the final static fields
specifier|private
specifier|static
specifier|final
name|Pattern
name|NON_ASCII
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^0-9a-zA-Z$]+"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|PATTERN_L4JC
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"_$L4J$C$"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|>
name|DETERMINISTIC_CLASSES
init|=
operator|new
name|HashSet
argument_list|<
name|Class
argument_list|>
argument_list|(
name|Arrays
operator|.
expr|<
name|Class
operator|>
name|asList
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|class
argument_list|,
name|Short
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|,
name|BigInteger
operator|.
name|class
argument_list|,
name|BigDecimal
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Math
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Creates optimizer with no parent.    */
specifier|public
name|DeterministicCodeOptimizer
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a child optimizer.    * Typically a child is created for each class declaration,    * so each optimizer collects fields for exactly one class.    *    * @param parent parent optimizer    */
specifier|public
name|DeterministicCodeOptimizer
parameter_list|(
name|DeterministicCodeOptimizer
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/**    * Creates optimizer local to the newly generated anonymous class.    *    * @param newExpression expression to optimize    * @return nested visitor if anonymous class is given    */
annotation|@
name|Override
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|)
block|{
if|if
condition|(
name|newExpression
operator|.
name|memberDeclarations
operator|==
literal|null
condition|)
block|{
return|return
name|this
return|;
block|}
name|DeterministicCodeOptimizer
name|visitor
init|=
name|goDeeper
argument_list|()
decl_stmt|;
name|visitor
operator|.
name|learnFinalStaticDeclarations
argument_list|(
name|newExpression
operator|.
name|memberDeclarations
argument_list|)
expr_stmt|;
return|return
name|visitor
return|;
block|}
comment|/**    * Creates optimizer local to the newly generated class.    *    * @param classDeclaration expression to optimize    * @return nested visitor    */
annotation|@
name|Override
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|ClassDeclaration
name|classDeclaration
parameter_list|)
block|{
name|DeterministicCodeOptimizer
name|visitor
init|=
name|goDeeper
argument_list|()
decl_stmt|;
name|visitor
operator|.
name|learnFinalStaticDeclarations
argument_list|(
name|classDeclaration
operator|.
name|memberDeclarations
argument_list|)
expr_stmt|;
return|return
name|visitor
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
name|Expression
name|result
init|=
name|super
operator|.
name|visit
argument_list|(
name|newExpression
argument_list|,
name|arguments
argument_list|,
name|memberDeclarations
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|memberDeclarations
operator|==
literal|null
condition|)
block|{
return|return
name|tryOptimizeNewInstance
argument_list|(
operator|(
name|NewExpression
operator|)
name|result
argument_list|)
return|;
block|}
name|memberDeclarations
operator|=
name|optimizeDeclarations
argument_list|(
name|memberDeclarations
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
operator|(
name|NewExpression
operator|)
name|result
argument_list|,
name|arguments
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
comment|/**    * Optimizes new Type(); constructs,    *    * @param newExpression expression to optimize    * @return optimized expression    */
specifier|protected
name|Expression
name|tryOptimizeNewInstance
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|)
block|{
if|if
condition|(
name|newExpression
operator|.
name|type
operator|instanceof
name|Class
operator|&&
name|isConstant
argument_list|(
name|newExpression
operator|.
name|arguments
argument_list|)
operator|&&
name|isConstructorDeterministic
argument_list|(
operator|(
name|Class
operator|)
name|newExpression
operator|.
name|type
argument_list|)
condition|)
block|{
comment|// Reuse instance creation when class is immutable: new BigInteger(3)
return|return
name|createField
argument_list|(
name|newExpression
argument_list|)
return|;
block|}
return|return
name|newExpression
return|;
block|}
annotation|@
name|Override
specifier|public
name|ClassDeclaration
name|visit
parameter_list|(
name|ClassDeclaration
name|classDeclaration
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|visit
argument_list|(
name|classDeclaration
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
name|memberDeclarations
operator|=
name|optimizeDeclarations
argument_list|(
name|memberDeclarations
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|classDeclaration
argument_list|,
name|memberDeclarations
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
name|binaryExpression
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|)
block|{
name|Expression
name|result
init|=
name|super
operator|.
name|visit
argument_list|(
name|binaryExpression
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|binaryExpression
operator|.
name|getNodeType
argument_list|()
operator|.
name|modifiesLvalue
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|isConstant
argument_list|(
name|expression0
argument_list|)
operator|&&
name|isConstant
argument_list|(
name|expression1
argument_list|)
condition|)
block|{
return|return
name|createField
argument_list|(
name|result
argument_list|)
return|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|TernaryExpression
name|ternaryExpression
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
name|Expression
name|result
init|=
name|super
operator|.
name|visit
argument_list|(
name|ternaryExpression
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|,
name|expression2
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|isConstant
argument_list|(
name|expression0
argument_list|)
operator|&&
name|isConstant
argument_list|(
name|expression1
argument_list|)
operator|&&
name|isConstant
argument_list|(
name|expression2
argument_list|)
condition|)
block|{
return|return
name|createField
argument_list|(
name|result
argument_list|)
return|;
block|}
return|return
name|result
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
name|Expression
name|result
init|=
name|super
operator|.
name|visit
argument_list|(
name|unaryExpression
argument_list|,
name|expression
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|isConstant
argument_list|(
name|expression
argument_list|)
condition|)
block|{
name|constants
operator|.
name|put
argument_list|(
name|result
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|getNodeType
argument_list|()
operator|!=
name|ExpressionType
operator|.
name|Convert
condition|)
block|{
return|return
name|createField
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|TypeBinaryExpression
name|typeBinaryExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|Expression
name|result
init|=
name|super
operator|.
name|visit
argument_list|(
name|typeBinaryExpression
argument_list|,
name|expression
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|isConstant
argument_list|(
name|expression
argument_list|)
condition|)
block|{
name|constants
operator|.
name|put
argument_list|(
name|result
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Optimized method call, possibly converting it to final static field.    *    * @param methodCallExpression method call to optimize    * @return optimized expression    */
specifier|protected
name|Expression
name|tryOptimizeMethodCall
parameter_list|(
name|MethodCallExpression
name|methodCallExpression
parameter_list|)
block|{
if|if
condition|(
name|isConstant
argument_list|(
name|methodCallExpression
operator|.
name|targetExpression
argument_list|)
operator|&&
name|isConstant
argument_list|(
name|methodCallExpression
operator|.
name|expressions
argument_list|)
operator|&&
name|isMethodDeterministic
argument_list|(
name|methodCallExpression
operator|.
name|method
argument_list|)
condition|)
block|{
return|return
name|createField
argument_list|(
name|methodCallExpression
argument_list|)
return|;
block|}
return|return
name|methodCallExpression
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|MethodCallExpression
name|methodCallExpression
parameter_list|,
name|Expression
name|targetExpression
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
name|Expression
name|result
init|=
name|super
operator|.
name|visit
argument_list|(
name|methodCallExpression
argument_list|,
name|targetExpression
argument_list|,
name|expressions
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|result
operator|=
name|tryOptimizeMethodCall
argument_list|(
operator|(
name|MethodCallExpression
operator|)
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|MemberExpression
name|memberExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|Expression
name|result
init|=
name|super
operator|.
name|visit
argument_list|(
name|memberExpression
argument_list|,
name|expression
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|isConstant
argument_list|(
name|expression
argument_list|)
operator|&&
name|Modifier
operator|.
name|isFinal
argument_list|(
name|memberExpression
operator|.
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|constants
operator|.
name|put
argument_list|(
name|result
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Processes the list of declarations and learns final static ones as    * effectively constant.    *    * @param memberDeclarations list of declarations to search finals from    */
specifier|protected
name|void
name|learnFinalStaticDeclarations
parameter_list|(
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
for|for
control|(
name|MemberDeclaration
name|decl
range|:
name|memberDeclarations
control|)
block|{
if|if
condition|(
name|decl
operator|instanceof
name|FieldDeclaration
condition|)
block|{
name|FieldDeclaration
name|field
init|=
operator|(
name|FieldDeclaration
operator|)
name|decl
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|modifier
argument_list|)
operator|&&
name|Modifier
operator|.
name|isFinal
argument_list|(
name|field
operator|.
name|modifier
argument_list|)
operator|&&
name|field
operator|.
name|initializer
operator|!=
literal|null
condition|)
block|{
name|constants
operator|.
name|put
argument_list|(
name|field
operator|.
name|parameter
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fieldsByName
operator|.
name|put
argument_list|(
name|field
operator|.
name|parameter
operator|.
name|name
argument_list|,
name|field
operator|.
name|parameter
argument_list|)
expr_stmt|;
name|dedup
operator|.
name|put
argument_list|(
name|field
operator|.
name|initializer
argument_list|,
name|field
operator|.
name|parameter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Adds new declarations (e.g. final static fields) to the list of existing    * ones.    *    * @param memberDeclarations existing list of declarations    * @return new list of declarations or the same if no modifications required    */
specifier|protected
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|optimizeDeclarations
parameter_list|(
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
if|if
condition|(
name|addedDeclarations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|memberDeclarations
return|;
block|}
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|newDecls
init|=
operator|new
name|ArrayList
argument_list|<
name|MemberDeclaration
argument_list|>
argument_list|(
name|memberDeclarations
operator|.
name|size
argument_list|()
operator|+
name|addedDeclarations
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|newDecls
operator|.
name|addAll
argument_list|(
name|memberDeclarations
argument_list|)
expr_stmt|;
name|newDecls
operator|.
name|addAll
argument_list|(
name|addedDeclarations
argument_list|)
expr_stmt|;
return|return
name|newDecls
return|;
block|}
comment|/**    * Finds if there exists ready for reuse declaration for given expression.    *    * @param expression input expression    * @return parameter of the already existing declaration, or null    */
specifier|protected
name|ParameterExpression
name|findDeclaredExpression
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
operator|!
name|dedup
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ParameterExpression
name|pe
init|=
name|dedup
operator|.
name|get
argument_list|(
name|expression
argument_list|)
decl_stmt|;
if|if
condition|(
name|pe
operator|!=
literal|null
condition|)
block|{
return|return
name|pe
return|;
block|}
block|}
return|return
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
name|parent
operator|.
name|findDeclaredExpression
argument_list|(
name|expression
argument_list|)
return|;
block|}
comment|/**    * Creates final static field to hold the given expression.    * The method might reuse existing declarations if appropriate.    *    * @param expression expression to store in final field    * @return expression for the given input expression    */
specifier|protected
name|Expression
name|createField
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|ParameterExpression
name|pe
init|=
name|findDeclaredExpression
argument_list|(
name|expression
argument_list|)
decl_stmt|;
if|if
condition|(
name|pe
operator|!=
literal|null
condition|)
block|{
return|return
name|pe
return|;
block|}
name|String
name|name
init|=
name|inventFieldName
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|pe
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
name|name
argument_list|)
expr_stmt|;
name|FieldDeclaration
name|decl
init|=
name|Expressions
operator|.
name|fieldDecl
argument_list|(
name|Modifier
operator|.
name|FINAL
operator||
name|Modifier
operator|.
name|STATIC
argument_list|,
name|pe
argument_list|,
name|expression
argument_list|)
decl_stmt|;
name|dedup
operator|.
name|put
argument_list|(
name|expression
argument_list|,
name|pe
argument_list|)
expr_stmt|;
name|addedDeclarations
operator|.
name|add
argument_list|(
name|decl
argument_list|)
expr_stmt|;
name|constants
operator|.
name|put
argument_list|(
name|pe
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|pe
return|;
block|}
comment|/**    * Generates field name to store given expression.    * The expression is converted to string and all the non-ascii/numeric    * characters are replaced with underscores and "_$L4J$C$" suffix is added    * to avoid conflicts with other variables.    * When multiple variables are mangled to the same name,    * counter is used to avoid conflicts.    *    * @param expression input expression    * @return unique name to store given expression    */
specifier|protected
name|String
name|inventFieldName
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|String
name|exprText
init|=
name|expression
operator|.
name|toString
argument_list|()
decl_stmt|;
name|exprText
operator|=
name|PATTERN_L4JC
operator|.
name|matcher
argument_list|(
name|exprText
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|exprText
operator|=
name|NON_ASCII
operator|.
name|matcher
argument_list|(
name|exprText
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"_"
argument_list|)
operator|+
literal|"_$L4J$C$"
expr_stmt|;
name|String
name|fieldName
init|=
name|exprText
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|hasField
argument_list|(
name|fieldName
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|fieldName
operator|=
name|exprText
operator|+
name|i
expr_stmt|;
block|}
return|return
name|fieldName
return|;
block|}
comment|/**    * Verifies if the expression is effectively constant.    * It is assumed the expression is simple (e.g. ConstantExpression or    * ParameterExpression).    * The method verifies parent chain since the expression might be defined    * in enclosing class.    *    * @param expression expression to test    * @return true when the expression is known to be constant    */
specifier|protected
name|boolean
name|isConstant
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|expression
operator|==
literal|null
operator|||
name|expression
operator|instanceof
name|ConstantExpression
operator|||
operator|!
name|constants
operator|.
name|isEmpty
argument_list|()
operator|&&
name|constants
operator|.
name|containsKey
argument_list|(
name|expression
argument_list|)
operator|||
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|isConstant
argument_list|(
name|expression
argument_list|)
return|;
block|}
comment|/**    * Verifies if all the expressions in given list are  effectively constant.    *    * @param list list of expressions to test    * @return true when all the expressions are known to be constant    */
specifier|protected
name|boolean
name|isConstant
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|list
parameter_list|)
block|{
for|for
control|(
name|Expression
name|expression
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|isConstant
argument_list|(
name|expression
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Checks if given method is deterministic (i.e. returns the same output    * given the same inputs).    *    * @param method method to test    * @return true when the method is deterministic    */
specifier|protected
name|boolean
name|isMethodDeterministic
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
return|return
name|allMethodsDeterministic
argument_list|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Checks if new instance creation can be reused. For instance new    * BigInteger("42") is effectively final and can be reused.    *    * @param klass method to test    * @return true when the method is deterministic    */
specifier|protected
name|boolean
name|isConstructorDeterministic
parameter_list|(
name|Class
name|klass
parameter_list|)
block|{
return|return
name|allMethodsDeterministic
argument_list|(
name|klass
argument_list|)
return|;
block|}
comment|/**    * Checks if all the methods in given class are deterministic (i.e. return    * the same value given the same inputs)    *    * @param klass class to test    * @return true when all the methods including constructors are deterministic    */
specifier|protected
name|boolean
name|allMethodsDeterministic
parameter_list|(
name|Class
name|klass
parameter_list|)
block|{
return|return
name|DETERMINISTIC_CLASSES
operator|.
name|contains
argument_list|(
name|klass
argument_list|)
return|;
block|}
comment|/**    * Verifies if the variable name is already in use.    * Only the variables that are explicitly added to fieldsByName are verified.    * The method verifies parent chain.    *    * @param name name of the variable to test    * @return true if the name is used by one of static final fields    */
specifier|protected
name|boolean
name|hasField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|!
name|fieldsByName
operator|.
name|isEmpty
argument_list|()
operator|&&
name|fieldsByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|||
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|hasField
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Creates child visitor. It is used to traverse nested class declarations.    *    * @return new Visitor that is used to optimize class declarations    */
specifier|protected
name|DeterministicCodeOptimizer
name|goDeeper
parameter_list|()
block|{
return|return
operator|new
name|DeterministicCodeOptimizer
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End DeterministicCodeOptimizer.java
end_comment

end_unit

