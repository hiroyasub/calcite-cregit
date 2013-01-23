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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|SqlFunctions
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
name|Aggregation
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
name|Type
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
comment|/**  * Translates {@link org.eigenbase.rex.RexNode REX expressions} to  * {@link Expression linq4j expressions}.  *  * @author jhyde  */
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
argument_list|,
name|findMethod
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"substring"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|TYPE
argument_list|,
name|Integer
operator|.
name|TYPE
argument_list|)
argument_list|,
name|substringFunc
argument_list|,
name|findMethod
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"charLength"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|characterLengthFunc
argument_list|,
name|findMethod
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"charLength"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|charLengthFunc
argument_list|)
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|final
name|RexBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|RexProgram
name|program
decl_stmt|;
specifier|private
specifier|final
name|RexToLixTranslator
operator|.
name|InputGetter
name|inputGetter
decl_stmt|;
specifier|private
name|BlockBuilder
name|list
decl_stmt|;
specifier|private
specifier|static
name|Method
name|findMethod
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|name
parameter_list|,
name|Class
modifier|...
name|parameterTypes
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
argument_list|,
name|parameterTypes
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
name|InputGetter
name|inputGetter
parameter_list|,
name|BlockBuilder
name|list
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
name|this
operator|.
name|inputGetter
operator|=
name|inputGetter
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|builder
operator|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
comment|/**      * Translates a {@link RexProgram} to a sequence of expressions and      * declarations.      *      * @param program Program to be translated      * @param typeFactory Type factory      * @param list List of statements, populated with declarations      * @param inputGetter Generates expressions for inputs      * @return Sequence of expressions, optional condition      */
specifier|public
specifier|static
name|List
argument_list|<
name|Expression
argument_list|>
name|translateProjects
parameter_list|(
name|RexProgram
name|program
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|BlockBuilder
name|list
parameter_list|,
name|InputGetter
name|inputGetter
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
name|inputGetter
argument_list|,
name|list
argument_list|)
operator|.
name|translate
argument_list|(
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|)
return|;
block|}
name|Expression
name|translate
parameter_list|(
name|RexNode
name|expr
parameter_list|)
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
return|return
name|list
operator|.
name|append
argument_list|(
literal|"v"
argument_list|,
name|expression
argument_list|)
return|;
block|}
name|Expression
name|translateCast
parameter_list|(
name|RexNode
name|expr
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|Expression
name|operand
init|=
name|translate
argument_list|(
name|expr
argument_list|)
decl_stmt|;
return|return
name|convert
argument_list|(
name|operand
argument_list|,
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
argument_list|)
argument_list|)
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
return|return
name|inputGetter
operator|.
name|field
argument_list|(
name|list
argument_list|,
name|index
argument_list|)
return|;
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
name|translateLiteral
argument_list|(
name|expr
argument_list|,
name|typeFactory
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
name|RexImpTable
operator|.
name|CallImplementor
name|implementor
init|=
name|RexImpTable
operator|.
name|INSTANCE
operator|.
name|get
argument_list|(
name|operator
argument_list|)
decl_stmt|;
if|if
condition|(
name|implementor
operator|!=
literal|null
condition|)
block|{
return|return
name|implementor
operator|.
name|implement
argument_list|(
name|this
argument_list|,
name|call
argument_list|)
return|;
block|}
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
comment|/** Translates a literal. */
specifier|public
specifier|static
name|Expression
name|translateLiteral
parameter_list|(
name|RexNode
name|expr
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
name|Type
name|javaClass
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|expr
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|javaClass
operator|==
name|BigDecimal
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|new_
argument_list|(
name|BigDecimal
operator|.
name|class
argument_list|,
name|Arrays
operator|.
expr|<
name|Expression
operator|>
name|asList
argument_list|(
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
name|getValue3
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
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
name|getValue3
argument_list|()
argument_list|,
name|javaClass
argument_list|)
return|;
block|}
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
name|RexLocalRef
argument_list|>
name|rexList
parameter_list|)
block|{
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
name|RexProgram
name|program
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|BlockBuilder
name|list
parameter_list|,
name|InputGetter
name|inputGetter
parameter_list|)
block|{
if|if
condition|(
name|program
operator|.
name|getCondition
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|Expressions
operator|.
name|constant
argument_list|(
literal|true
argument_list|)
return|;
block|}
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
name|inputGetter
argument_list|,
name|list
argument_list|)
operator|.
name|translate
argument_list|(
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
specifier|public
specifier|static
name|Expression
name|translateAggregate
parameter_list|(
name|Expression
name|grouping
parameter_list|,
name|Aggregation
name|aggregation
parameter_list|,
name|Expression
name|accessor
parameter_list|)
block|{
specifier|final
name|RexImpTable
operator|.
name|AggregateImplementor
name|implementor
init|=
name|RexImpTable
operator|.
name|INSTANCE
operator|.
name|aggMap
operator|.
name|get
argument_list|(
name|aggregation
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregation
operator|==
name|countOperator
condition|)
block|{
comment|// FIXME: count(x) and count(distinct x) don't work currently
name|accessor
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|implementor
operator|!=
literal|null
condition|)
block|{
return|return
name|implementor
operator|.
name|implementAggregate
argument_list|(
name|grouping
argument_list|,
name|accessor
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown agg "
operator|+
name|aggregation
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|Expression
name|convert
parameter_list|(
name|Expression
name|operand
parameter_list|,
name|Type
name|toType
parameter_list|)
block|{
specifier|final
name|Type
name|fromType
init|=
name|operand
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|fromType
operator|.
name|equals
argument_list|(
name|toType
argument_list|)
condition|)
block|{
return|return
name|operand
return|;
block|}
comment|// E.g. from "Short" to "int".
comment|// Generate "x.intValue()".
specifier|final
name|Primitive
name|toPrimitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|toType
argument_list|)
decl_stmt|;
specifier|final
name|Primitive
name|fromBox
init|=
name|Primitive
operator|.
name|ofBox
argument_list|(
name|fromType
argument_list|)
decl_stmt|;
specifier|final
name|Primitive
name|fromPrimitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|fromType
argument_list|)
decl_stmt|;
if|if
condition|(
name|toPrimitive
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fromPrimitive
operator|!=
literal|null
condition|)
block|{
comment|// E.g. from "float" to "double"
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|operand
argument_list|,
name|toPrimitive
operator|.
name|primitiveClass
argument_list|)
return|;
block|}
if|if
condition|(
name|fromBox
operator|==
literal|null
operator|&&
operator|!
operator|(
name|fromType
operator|instanceof
name|Class
operator|&&
name|Number
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
operator|(
name|Class
operator|)
name|fromType
argument_list|)
operator|)
condition|)
block|{
comment|// E.g. from "Object" to "short".
comment|// Generate "(Short) x".
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|operand
argument_list|,
name|toPrimitive
operator|.
name|boxClass
argument_list|)
return|;
block|}
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|operand
argument_list|,
name|toPrimitive
operator|.
name|primitiveName
operator|+
literal|"Value"
argument_list|)
return|;
block|}
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|operand
argument_list|,
name|toType
argument_list|)
return|;
block|}
comment|/** Translates a field of an input to an expression. */
specifier|public
interface|interface
name|InputGetter
block|{
name|Expression
name|field
parameter_list|(
name|BlockBuilder
name|list
parameter_list|,
name|int
name|index
parameter_list|)
function_decl|;
block|}
comment|/** Implementation of {@link InputGetter} that calls      * {@link PhysType#fieldReference}. */
specifier|public
specifier|static
class|class
name|InputGetterImpl
implements|implements
name|InputGetter
block|{
specifier|private
name|List
argument_list|<
name|Pair
argument_list|<
name|Expression
argument_list|,
name|PhysType
argument_list|>
argument_list|>
name|inputs
decl_stmt|;
specifier|public
name|InputGetterImpl
parameter_list|(
name|List
argument_list|<
name|Pair
argument_list|<
name|Expression
argument_list|,
name|PhysType
argument_list|>
argument_list|>
name|inputs
parameter_list|)
block|{
name|this
operator|.
name|inputs
operator|=
name|inputs
expr_stmt|;
block|}
specifier|public
name|Expression
name|field
parameter_list|(
name|BlockBuilder
name|list
parameter_list|,
name|int
name|index
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|Expression
argument_list|,
name|PhysType
argument_list|>
name|input
init|=
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|input
operator|.
name|right
decl_stmt|;
specifier|final
name|Expression
name|left
init|=
name|list
operator|.
name|append
argument_list|(
literal|"current"
operator|+
name|index
argument_list|,
name|input
operator|.
name|left
argument_list|)
decl_stmt|;
return|return
name|physType
operator|.
name|fieldReference
argument_list|(
name|left
argument_list|,
name|index
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexToLixTranslator.java
end_comment

end_unit

