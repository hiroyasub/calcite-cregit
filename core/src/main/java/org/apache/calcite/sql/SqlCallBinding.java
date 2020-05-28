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
name|sql
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
name|EnumUtils
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
name|RelDataTypeFactoryImpl
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
name|runtime
operator|.
name|CalciteException
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
name|runtime
operator|.
name|Resources
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
name|SqlLiteralChainOperator
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
name|type
operator|.
name|SqlOperandMetadata
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
name|SqlOperandTypeChecker
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeUtil
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
name|SelectScope
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
name|SqlMonotonicity
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
name|SqlNameMatcher
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
name|SqlValidator
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
name|SqlValidatorException
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
name|SqlValidatorNamespace
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
name|SqlValidatorScope
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
name|ImmutableNullableList
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
name|NlsString
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
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
comment|/**  *<code>SqlCallBinding</code> implements {@link SqlOperatorBinding} by  * analyzing to the operands of a {@link SqlCall} with a {@link SqlValidator}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCallBinding
extends|extends
name|SqlOperatorBinding
block|{
comment|/** Static nested class required due to    *<a href="https://issues.apache.org/jira/browse/CALCITE-4393">[CALCITE-4393]    * ExceptionInInitializerError due to NPE in SqlCallBinding caused by circular dependency</a>.    * The static field inside it cannot be part of the outer class: it must be defined    * within a nested class in order to break the cycle during class loading. */
specifier|private
specifier|static
class|class
name|DefaultCallHolder
block|{
specifier|private
specifier|static
specifier|final
name|SqlCall
name|DEFAULT_CALL
init|=
name|SqlStdOperatorTable
operator|.
name|DEFAULT
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
block|}
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlValidator
name|validator
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|SqlValidatorScope
name|scope
decl_stmt|;
specifier|private
specifier|final
name|SqlCall
name|call
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a call binding.    *    * @param validator Validator    * @param scope     Scope of call    * @param call      Call node    */
specifier|public
name|SqlCallBinding
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
annotation|@
name|Nullable
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|super
argument_list|(
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|int
name|getGroupCount
parameter_list|()
block|{
specifier|final
name|SelectScope
name|selectScope
init|=
name|SqlValidatorUtil
operator|.
name|getEnclosingSelectScope
argument_list|(
name|scope
argument_list|)
decl_stmt|;
if|if
condition|(
name|selectScope
operator|==
literal|null
condition|)
block|{
comment|// Probably "VALUES expr". Treat same as "SELECT expr GROUP BY ()"
return|return
literal|0
return|;
block|}
specifier|final
name|SqlSelect
name|select
init|=
name|selectScope
operator|.
name|getNode
argument_list|()
decl_stmt|;
specifier|final
name|SqlNodeList
name|group
init|=
name|select
operator|.
name|getGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SqlNode
name|groupItem
range|:
name|group
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|groupItem
operator|instanceof
name|SqlNodeList
operator|)
operator|||
operator|(
operator|(
name|SqlNodeList
operator|)
name|groupItem
operator|)
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
operator|++
name|n
expr_stmt|;
block|}
block|}
return|return
name|n
return|;
block|}
return|return
name|validator
operator|.
name|isAggregate
argument_list|(
name|select
argument_list|)
condition|?
literal|0
else|:
operator|-
literal|1
return|;
block|}
comment|/**    * Returns the validator.    */
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
return|return
name|validator
return|;
block|}
comment|/**    * Returns the scope of the call.    */
specifier|public
annotation|@
name|Nullable
name|SqlValidatorScope
name|getScope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
comment|/**    * Returns the call node.    */
specifier|public
name|SqlCall
name|getCall
parameter_list|()
block|{
return|return
name|call
return|;
block|}
comment|/** Returns the operands to a call permuted into the same order as the    * formal parameters of the function. */
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
parameter_list|()
block|{
if|if
condition|(
name|hasAssignment
argument_list|()
operator|&&
operator|!
operator|(
name|call
operator|.
name|getOperator
argument_list|()
operator|instanceof
name|SqlUnresolvedFunction
operator|)
condition|)
block|{
return|return
name|permutedOperands
argument_list|(
name|call
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
specifier|final
name|SqlOperandTypeChecker
name|checker
init|=
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getOperandTypeChecker
argument_list|()
decl_stmt|;
if|if
condition|(
name|checker
operator|==
literal|null
condition|)
block|{
return|return
name|operandList
return|;
block|}
specifier|final
name|SqlOperandCountRange
name|range
init|=
name|checker
operator|.
name|getOperandCountRange
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operandList
argument_list|)
decl_stmt|;
while|while
condition|(
name|list
operator|.
name|size
argument_list|()
operator|<
name|range
operator|.
name|getMax
argument_list|()
operator|&&
name|checker
operator|.
name|isOptional
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
operator|&&
name|checker
operator|.
name|isFixedParameters
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|DefaultCallHolder
operator|.
name|DEFAULT_CALL
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
comment|/** Returns whether arguments have name assignment. */
specifier|private
name|boolean
name|hasAssignment
parameter_list|()
block|{
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|getOperandList
argument_list|()
control|)
block|{
if|if
condition|(
name|operand
operator|!=
literal|null
operator|&&
name|operand
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|ARGUMENT_ASSIGNMENT
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Returns the operands to a call permuted into the same order as the    * formal parameters of the function. */
specifier|private
name|List
argument_list|<
name|SqlNode
argument_list|>
name|permutedOperands
parameter_list|(
specifier|final
name|SqlCall
name|call
parameter_list|)
block|{
specifier|final
name|SqlOperandMetadata
name|operandMetadata
init|=
name|requireNonNull
argument_list|(
operator|(
name|SqlOperandMetadata
operator|)
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getOperandTypeChecker
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"operandTypeChecker is null for "
operator|+
name|call
operator|+
literal|", operator "
operator|+
name|call
operator|.
name|getOperator
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
init|=
name|operandMetadata
operator|.
name|paramNames
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|permuted
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SqlNameMatcher
name|nameMatcher
init|=
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|nameMatcher
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|paramName
range|:
name|paramNames
control|)
block|{
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlIdentifier
argument_list|>
name|args
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|SqlCall
name|call2
init|=
name|call
operator|.
name|operand
argument_list|(
name|j
argument_list|)
decl_stmt|;
assert|assert
name|call2
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|ARGUMENT_ASSIGNMENT
assert|;
specifier|final
name|SqlIdentifier
name|operandID
init|=
name|call2
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|operandName
init|=
name|operandID
operator|.
name|getSimple
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameMatcher
operator|.
name|matches
argument_list|(
name|operandName
argument_list|,
name|paramName
argument_list|)
condition|)
block|{
name|permuted
operator|.
name|add
argument_list|(
name|call2
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
if|else if
condition|(
name|args
operator|==
literal|null
operator|&&
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
operator|&&
name|operandName
operator|.
name|equalsIgnoreCase
argument_list|(
name|paramName
argument_list|)
condition|)
block|{
name|args
operator|=
name|Pair
operator|.
name|of
argument_list|(
name|paramName
argument_list|,
name|operandID
argument_list|)
expr_stmt|;
block|}
comment|// the last operand, there is still no match.
if|if
condition|(
name|j
operator|==
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|args
operator|.
name|right
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|paramNotFoundInFunctionDidYouMean
argument_list|(
name|args
operator|.
name|right
operator|.
name|getSimple
argument_list|()
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|args
operator|.
name|left
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|operandMetadata
operator|.
name|isFixedParameters
argument_list|()
condition|)
block|{
comment|// Not like user defined functions, we do not patch up the operands
comment|// with DEFAULT and then convert to nulls during sql-to-rel conversion.
comment|// Thus, there is no need to show the optional operands in the plan and
comment|// decide if the optional operand is null when code generation.
name|permuted
operator|.
name|add
argument_list|(
name|DefaultCallHolder
operator|.
name|DEFAULT_CALL
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|permuted
return|;
block|}
comment|/**    * Returns a particular operand.    */
specifier|public
name|SqlNode
name|operand
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|operands
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/** Returns a call that is equivalent except that arguments have been    * permuted into the logical order. Any arguments whose default value is being    * used are null. */
specifier|public
name|SqlCall
name|permutedCall
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
init|=
name|operands
argument_list|()
decl_stmt|;
if|if
condition|(
name|operandList
operator|.
name|equals
argument_list|(
name|call
operator|.
name|getOperandList
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|call
return|;
block|}
return|return
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|createCall
argument_list|(
name|call
operator|.
name|pos
argument_list|,
name|operandList
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getOperandMonotonicity
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
operator|.
name|getMonotonicity
argument_list|(
name|scope
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|String
name|getStringLiteralOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|SqlNode
name|node
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|o
init|=
name|SqlLiteral
operator|.
name|value
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
name|o
operator|instanceof
name|NlsString
condition|?
operator|(
operator|(
name|NlsString
operator|)
name|o
operator|)
operator|.
name|getValue
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|int
name|getIntLiteralOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|SqlNode
name|node
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|o
init|=
name|SqlLiteral
operator|.
name|value
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|BigDecimal
condition|)
block|{
name|BigDecimal
name|bd
init|=
operator|(
name|BigDecimal
operator|)
name|o
decl_stmt|;
try|try
block|{
return|return
name|bd
operator|.
name|intValueExact
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|e
parameter_list|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|node
operator|.
name|pos
argument_list|,
name|RESOURCE
operator|.
name|numberLiteralOutOfRange
argument_list|(
name|bd
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Object
parameter_list|>
annotation|@
name|Nullable
name|T
name|getOperandLiteralValue
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
specifier|final
name|SqlNode
name|node
init|=
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
return|return
name|valueAs
argument_list|(
name|node
argument_list|,
name|clazz
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|Object
name|getOperandLiteralValue
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|type
operator|instanceof
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
operator|(
operator|(
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
operator|)
name|type
operator|)
operator|.
name|getJavaClass
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|o
init|=
name|getOperandLiteralValue
argument_list|(
name|ordinal
argument_list|,
name|Object
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|final
name|Object
name|o2
init|=
name|o
operator|instanceof
name|NlsString
condition|?
operator|(
operator|(
name|NlsString
operator|)
name|o
operator|)
operator|.
name|getValue
argument_list|()
else|:
name|o
decl_stmt|;
return|return
name|EnumUtils
operator|.
name|evaluate
argument_list|(
name|o2
argument_list|,
name|clazz
argument_list|)
return|;
block|}
specifier|private
parameter_list|<
name|T
extends|extends
name|Object
parameter_list|>
annotation|@
name|Nullable
name|T
name|valueAs
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
specifier|final
name|SqlLiteral
name|literal
decl_stmt|;
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|ARRAY_VALUE_CONSTRUCTOR
case|:
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlNode
name|o
range|:
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|valueAs
argument_list|(
name|o
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|ImmutableNullableList
operator|.
name|copyOf
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
case|case
name|MAP_VALUE_CONSTRUCTOR
case|:
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|builder2
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperandList
argument_list|()
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
name|operands
operator|.
name|size
argument_list|()
condition|;
name|i
operator|+=
literal|2
control|)
block|{
specifier|final
name|SqlNode
name|key
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|value
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|builder2
operator|.
name|put
argument_list|(
name|requireNonNull
argument_list|(
name|valueAs
argument_list|(
name|key
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
literal|"key"
argument_list|)
argument_list|,
name|requireNonNull
argument_list|(
name|valueAs
argument_list|(
name|value
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|builder2
operator|.
name|build
argument_list|()
argument_list|)
return|;
case|case
name|CAST
case|:
return|return
name|valueAs
argument_list|(
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|,
name|clazz
argument_list|)
return|;
case|case
name|LITERAL
case|:
name|literal
operator|=
operator|(
name|SqlLiteral
operator|)
name|node
expr_stmt|;
if|if
condition|(
name|literal
operator|.
name|getTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|literal
operator|.
name|getValueAs
argument_list|(
name|clazz
argument_list|)
return|;
case|case
name|LITERAL_CHAIN
case|:
name|literal
operator|=
name|SqlLiteralChainOperator
operator|.
name|concatenateOperands
argument_list|(
operator|(
name|SqlCall
operator|)
name|node
argument_list|)
expr_stmt|;
return|return
name|literal
operator|.
name|getValueAs
argument_list|(
name|clazz
argument_list|)
return|;
case|case
name|INTERVAL_QUALIFIER
case|:
specifier|final
name|SqlIntervalQualifier
name|q
init|=
operator|(
name|SqlIntervalQualifier
operator|)
name|node
decl_stmt|;
specifier|final
name|SqlIntervalLiteral
operator|.
name|IntervalValue
name|intervalValue
init|=
operator|new
name|SqlIntervalLiteral
operator|.
name|IntervalValue
argument_list|(
name|q
argument_list|,
literal|1
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|literal
operator|=
operator|new
name|SqlLiteral
argument_list|(
name|intervalValue
argument_list|,
name|q
operator|.
name|typeName
argument_list|()
argument_list|,
name|q
operator|.
name|pos
argument_list|)
expr_stmt|;
return|return
name|literal
operator|.
name|getValueAs
argument_list|(
name|clazz
argument_list|)
return|;
case|case
name|DEFAULT
case|:
return|return
literal|null
return|;
comment|// currently NULL is the only default value
default|default:
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|node
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
comment|// NULL literal
block|}
return|return
literal|null
return|;
comment|// not a literal
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOperandNull
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|boolean
name|allowCast
parameter_list|)
block|{
return|return
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|operand
argument_list|(
name|ordinal
argument_list|)
argument_list|,
name|allowCast
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOperandLiteral
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|boolean
name|allowCast
parameter_list|)
block|{
return|return
name|SqlUtil
operator|.
name|isLiteral
argument_list|(
name|operand
argument_list|(
name|ordinal
argument_list|)
argument_list|,
name|allowCast
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOperandCount
parameter_list|()
block|{
return|return
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getOperandType
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|SqlTypeUtil
operator|.
name|deriveType
argument_list|(
name|this
argument_list|,
name|operand
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorNamespace
name|namespace
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|operand
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespace
operator|!=
literal|null
condition|)
block|{
return|return
name|namespace
operator|.
name|getType
argument_list|()
return|;
block|}
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataType
name|getCursorOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlUtil
operator|.
name|isCallTo
argument_list|(
name|operand
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CURSOR
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|SqlCall
name|cursorCall
init|=
operator|(
name|SqlCall
operator|)
name|operand
decl_stmt|;
specifier|final
name|SqlNode
name|query
init|=
name|cursorCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|SqlTypeUtil
operator|.
name|deriveType
argument_list|(
name|this
argument_list|,
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|String
name|getColumnListParamInfo
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|String
name|paramName
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|columnList
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlUtil
operator|.
name|isCallTo
argument_list|(
name|operand
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|columnList
operator|.
name|addAll
argument_list|(
name|SqlIdentifier
operator|.
name|simpleNames
argument_list|(
operator|(
operator|(
name|SqlCall
operator|)
name|operand
operator|)
operator|.
name|getOperandList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|validator
operator|.
name|getParentCursor
argument_list|(
name|paramName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CalciteException
name|newError
parameter_list|(
name|Resources
operator|.
name|ExInst
argument_list|<
name|SqlValidatorException
argument_list|>
name|e
parameter_list|)
block|{
return|return
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/**    * Constructs a new validation signature error for the call.    *    * @return signature exception    */
specifier|public
name|CalciteException
name|newValidationSignatureError
parameter_list|()
block|{
return|return
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|RESOURCE
operator|.
name|canNotApplyOp2Type
argument_list|(
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|call
operator|.
name|getCallSignature
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getAllowedSignatures
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Constructs a new validation error for the call. (Do not use this to    * construct a validation error for other nodes such as an operands.)    *    * @param ex underlying exception    * @return wrapped exception    */
specifier|public
name|CalciteException
name|newValidationError
parameter_list|(
name|Resources
operator|.
name|ExInst
argument_list|<
name|SqlValidatorException
argument_list|>
name|ex
parameter_list|)
block|{
return|return
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|ex
argument_list|)
return|;
block|}
comment|/**    * Returns whether to allow implicit type coercion when validation.    * This is a short-cut method.    */
specifier|public
name|boolean
name|isTypeCoercionEnabled
parameter_list|()
block|{
return|return
name|validator
operator|.
name|config
argument_list|()
operator|.
name|typeCoercionEnabled
argument_list|()
return|;
block|}
block|}
end_class

end_unit

