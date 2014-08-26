begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
package|;
end_package

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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Strategies for checking operand types.  *  *<p>This class defines singleton instances of strategy objects for operand  * type checking. {@link org.eigenbase.sql.type.ReturnTypes}  * and {@link org.eigenbase.sql.type.InferTypes} provide similar strategies  * for operand type inference and operator return type inference.  *  *<p>Note to developers: avoid anonymous inner classes here except for unique,  * non-generalizable strategies; anything else belongs in a reusable top-level  * class. If you find yourself copying and pasting an existing strategy's  * anonymous inner class, you're making a mistake.  *  * @see org.eigenbase.sql.type.SqlOperandTypeChecker  * @see org.eigenbase.sql.type.ReturnTypes  * @see org.eigenbase.sql.type.InferTypes  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OperandTypes
block|{
specifier|private
name|OperandTypes
parameter_list|()
block|{
block|}
comment|/**    * Creates a checker that passes if each operand is a member of a    * corresponding family.    */
specifier|public
specifier|static
name|FamilyOperandTypeChecker
name|family
parameter_list|(
name|SqlTypeFamily
modifier|...
name|families
parameter_list|)
block|{
return|return
operator|new
name|FamilyOperandTypeChecker
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|families
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a checker that passes if each operand is a member of a    * corresponding family.    */
specifier|public
specifier|static
name|FamilyOperandTypeChecker
name|family
parameter_list|(
name|List
argument_list|<
name|SqlTypeFamily
argument_list|>
name|families
parameter_list|)
block|{
return|return
operator|new
name|FamilyOperandTypeChecker
argument_list|(
name|families
argument_list|)
return|;
block|}
comment|/**    * Creates a checker that passes if any one of the rules passes.    */
specifier|public
specifier|static
name|SqlSingleOperandTypeChecker
name|or
parameter_list|(
name|SqlSingleOperandTypeChecker
modifier|...
name|rules
parameter_list|)
block|{
return|return
operator|new
name|CompositeOperandTypeChecker
argument_list|(
name|CompositeOperandTypeChecker
operator|.
name|Composition
operator|.
name|OR
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rules
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a checker that passes if any one of the rules passes.    */
specifier|public
specifier|static
name|SqlSingleOperandTypeChecker
name|and
parameter_list|(
name|SqlSingleOperandTypeChecker
modifier|...
name|rules
parameter_list|)
block|{
return|return
operator|new
name|CompositeOperandTypeChecker
argument_list|(
name|CompositeOperandTypeChecker
operator|.
name|Composition
operator|.
name|AND
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rules
argument_list|)
argument_list|)
return|;
block|}
comment|// ----------------------------------------------------------------------
comment|// SqlOperandTypeChecker definitions
comment|// ----------------------------------------------------------------------
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * Operand type-checking strategy for an operator which takes no operands.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|NILADIC
init|=
name|family
argument_list|()
decl_stmt|;
comment|/**    * Operand type-checking strategy for an operator with no restrictions on    * number or type of operands.    */
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|VARIADIC
init|=
name|variadic
argument_list|(
name|SqlOperandCountRanges
operator|.
name|any
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Operand type-checking strategy that allows one or more operands. */
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|ONE_OR_MORE
init|=
name|variadic
argument_list|(
name|SqlOperandCountRanges
operator|.
name|from
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|SqlOperandTypeChecker
name|variadic
parameter_list|(
specifier|final
name|SqlOperandCountRange
name|range
parameter_list|)
block|{
return|return
operator|new
name|SqlOperandTypeChecker
argument_list|()
block|{
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
return|return
name|range
operator|.
name|isValidCount
argument_list|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|range
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
return|return
name|opName
operator|+
literal|"(...)"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|BOOLEAN
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|BOOLEAN_BOOLEAN
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|BOOLEAN
argument_list|,
name|SqlTypeFamily
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|NUMERIC
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|NUMERIC_NUMERIC
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|,
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|EXACT_NUMERIC
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|EXACT_NUMERIC
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|EXACT_NUMERIC_EXACT_NUMERIC
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|EXACT_NUMERIC
argument_list|,
name|SqlTypeFamily
operator|.
name|EXACT_NUMERIC
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|BINARY
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|BINARY
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|STRING
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|STRING
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FamilyOperandTypeChecker
name|STRING_STRING
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FamilyOperandTypeChecker
name|STRING_STRING_STRING
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|CHARACTER
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|DATETIME
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|INTERVAL
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FamilyOperandTypeChecker
name|INTERVAL_INTERVAL
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|MULTISET
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|MULTISET
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|ARRAY
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ARRAY
argument_list|)
decl_stmt|;
comment|/** Checks that returns whether a value is a multiset or an array.    * Cf Java, where list and set are collections but a map is not. */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|COLLECTION
init|=
name|or
argument_list|(
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|MULTISET
argument_list|)
argument_list|,
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ARRAY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|COLLECTION_OR_MAP
init|=
name|or
argument_list|(
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|MULTISET
argument_list|)
argument_list|,
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ARRAY
argument_list|)
argument_list|,
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|MAP
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where type must be a literal or NULL.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|NULLABLE_LITERAL
init|=
operator|new
name|LiteralOperandTypeChecker
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy type must be a non-NULL literal.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|LITERAL
init|=
operator|new
name|LiteralOperandTypeChecker
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy type must be a positive integer non-NULL    * literal.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|POSITIVE_INTEGER_LITERAL
init|=
operator|new
name|FamilyOperandTypeChecker
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeFamily
operator|.
name|INTEGER
argument_list|)
argument_list|)
block|{
specifier|public
name|boolean
name|checkSingleOperandType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|int
name|iFormalOperand
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
if|if
condition|(
operator|!
name|LITERAL
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
name|iFormalOperand
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
name|iFormalOperand
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|SqlLiteral
name|arg
init|=
operator|(
name|SqlLiteral
operator|)
name|node
decl_stmt|;
specifier|final
name|int
name|value
init|=
name|arg
operator|.
name|intValue
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newError
argument_list|(
name|RESOURCE
operator|.
name|argumentMustBePositiveInteger
argument_list|(
name|callBinding
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Operand type-checking strategy where two operands must both be in the    * same type family.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|SAME_SAME
init|=
operator|new
name|SameOperandTypeChecker
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where three operands must all be in the    * same type family.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|SAME_SAME_SAME
init|=
operator|new
name|SameOperandTypeChecker
argument_list|(
literal|3
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where any number of operands must all be    * in the same type family.    */
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|SAME_VARIADIC
init|=
operator|new
name|SameOperandTypeChecker
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where operand types must allow ordered    * comparisons.    */
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|COMPARABLE_ORDERED_COMPARABLE_ORDERED
init|=
operator|new
name|ComparableOperandTypeChecker
argument_list|(
literal|2
argument_list|,
name|RelDataTypeComparability
operator|.
name|ALL
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where operand type must allow ordered    * comparisons. Used when instance comparisons are made on single operand    * functions    */
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|COMPARABLE_ORDERED
init|=
operator|new
name|ComparableOperandTypeChecker
argument_list|(
literal|1
argument_list|,
name|RelDataTypeComparability
operator|.
name|ALL
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where operand types must allow unordered    * comparisons.    */
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|COMPARABLE_UNORDERED_COMPARABLE_UNORDERED
init|=
operator|new
name|ComparableOperandTypeChecker
argument_list|(
literal|2
argument_list|,
name|RelDataTypeComparability
operator|.
name|UNORDERED
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where two operands must both be in the    * same string type family.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|STRING_SAME_SAME
init|=
name|OperandTypes
operator|.
name|and
argument_list|(
name|STRING_STRING
argument_list|,
name|SAME_SAME
argument_list|)
decl_stmt|;
comment|/**    * Operand type-checking strategy where three operands must all be in the    * same string type family.    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|STRING_SAME_SAME_SAME
init|=
name|OperandTypes
operator|.
name|and
argument_list|(
name|STRING_STRING_STRING
argument_list|,
name|SAME_SAME_SAME
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|STRING_STRING_INTEGER
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|STRING_STRING_INTEGER_INTEGER
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|INTEGER
argument_list|,
name|SqlTypeFamily
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|ANY
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ANY
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|ANY_ANY
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ANY
argument_list|,
name|SqlTypeFamily
operator|.
name|ANY
argument_list|)
decl_stmt|;
comment|/**    * Parameter type-checking strategy type must a nullable time interval,    * nullable time interval    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|INTERVAL_SAME_SAME
init|=
name|OperandTypes
operator|.
name|and
argument_list|(
name|INTERVAL_INTERVAL
argument_list|,
name|SAME_SAME
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|NUMERIC_INTERVAL
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|INTERVAL_NUMERIC
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|,
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|DATETIME_INTERVAL
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|INTERVAL_DATETIME
init|=
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|INTERVALINTERVAL_INTERVALDATETIME
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|INTERVAL_SAME_SAME
argument_list|,
name|INTERVAL_DATETIME
argument_list|)
decl_stmt|;
comment|// TODO: datetime+interval checking missing
comment|// TODO: interval+datetime checking missing
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|PLUS_OPERATOR
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|NUMERIC_NUMERIC
argument_list|,
name|INTERVAL_SAME_SAME
argument_list|,
name|DATETIME_INTERVAL
argument_list|,
name|INTERVAL_DATETIME
argument_list|)
decl_stmt|;
comment|/**    * Type checking strategy for the "*" operator    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|MULTIPLY_OPERATOR
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|NUMERIC_NUMERIC
argument_list|,
name|INTERVAL_NUMERIC
argument_list|,
name|NUMERIC_INTERVAL
argument_list|)
decl_stmt|;
comment|/**    * Type checking strategy for the "/" operator    */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|DIVISION_OPERATOR
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|NUMERIC_NUMERIC
argument_list|,
name|INTERVAL_NUMERIC
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|MINUS_OPERATOR
init|=
comment|// TODO:  compatibility check
name|OperandTypes
operator|.
name|or
argument_list|(
name|NUMERIC_NUMERIC
argument_list|,
name|INTERVAL_SAME_SAME
argument_list|,
name|DATETIME_INTERVAL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|MINUS_DATE_OPERATOR
init|=
operator|new
name|FamilyOperandTypeChecker
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|)
argument_list|)
block|{
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|SAME_SAME
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|NUMERIC_OR_INTERVAL
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|NUMERIC
argument_list|,
name|INTERVAL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|NUMERIC_OR_STRING
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|NUMERIC
argument_list|,
name|STRING
argument_list|)
decl_stmt|;
comment|/** Checker that returns whether a value is a multiset of records or an    * array of records.    *    * @see #COLLECTION */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|RECORD_COLLECTION
init|=
operator|new
name|SqlSingleOperandTypeChecker
argument_list|()
block|{
specifier|public
name|boolean
name|checkSingleOperandType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|int
name|iFormalOperand
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
assert|assert
literal|0
operator|==
name|iFormalOperand
assert|;
name|RelDataType
name|type
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|boolean
name|validationError
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|validationError
operator|=
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|validationError
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|SqlTypeName
name|typeName
init|=
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|MULTISET
operator|&&
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|ARRAY
condition|)
block|{
name|validationError
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|validationError
operator|&&
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
return|return
operator|!
name|validationError
return|;
block|}
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
return|return
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
literal|1
argument_list|)
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
return|return
literal|"UNNEST(<MULTISET>)"
return|;
block|}
block|}
decl_stmt|;
comment|/** Checker that returns whether a value is a collection (multiset or array)    * of scalar or record values. */
specifier|public
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|SCALAR_OR_RECORD_COLLECTION
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|COLLECTION
argument_list|,
name|RECORD_COLLECTION
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|MULTISET_MULTISET
init|=
operator|new
name|MultisetOperandTypeChecker
argument_list|()
decl_stmt|;
comment|/**    * Operand type-checking strategy for a set operator (UNION, INTERSECT,    * EXCEPT).    */
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|SET_OP
init|=
operator|new
name|SetopOperandTypeChecker
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|RECORD_TO_SCALAR
init|=
operator|new
name|SqlSingleOperandTypeChecker
argument_list|()
block|{
specifier|public
name|boolean
name|checkSingleOperandType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|int
name|iFormalOperand
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
assert|assert
literal|0
operator|==
name|iFormalOperand
assert|;
name|RelDataType
name|type
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|boolean
name|validationError
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|validationError
operator|=
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|validationError
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|validationError
operator|&&
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
return|return
operator|!
name|validationError
return|;
block|}
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
return|return
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
literal|1
argument_list|)
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
return|return
name|SqlUtil
operator|.
name|getAliasedSignature
argument_list|(
name|op
argument_list|,
name|opName
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"RECORDTYPE(SINGLE FIELD)"
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

begin_comment
comment|// End OperandTypes.java
end_comment

end_unit

