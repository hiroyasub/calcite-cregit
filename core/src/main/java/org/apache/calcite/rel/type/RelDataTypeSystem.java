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
name|rel
operator|.
name|type
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
name|util
operator|.
name|Glossary
import|;
end_import

begin_comment
comment|/**  * Type system.  *  *<p>Provides behaviors concerning type limits and behaviors. For example,  * in the default system, a DECIMAL can have maximum precision 19, but Hive  * overrides to 38.  *  *<p>The default implementation is {@link #DEFAULT}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataTypeSystem
block|{
comment|/** Default type system. */
name|RelDataTypeSystem
name|DEFAULT
init|=
operator|new
name|RelDataTypeSystemImpl
argument_list|()
block|{ }
decl_stmt|;
comment|/** Returns the maximum scale of a given type. */
name|int
name|getMaxScale
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/**    * Returns default precision for this type if supported, otherwise -1 if    * precision is either unsupported or must be specified explicitly.    *    * @return Default precision    */
name|int
name|getDefaultPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/**    * Returns the maximum precision (or length) allowed for this type, or -1 if    * precision/length are not applicable for this type.    *    * @return Maximum allowed precision    */
name|int
name|getMaxPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/** Returns the maximum scale of a NUMERIC or DECIMAL type. */
name|int
name|getMaxNumericScale
parameter_list|()
function_decl|;
comment|/** Returns the maximum precision of a NUMERIC or DECIMAL type. */
name|int
name|getMaxNumericPrecision
parameter_list|()
function_decl|;
comment|/** Returns the LITERAL string for the type, either PREFIX/SUFFIX. */
name|String
name|getLiteral
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|isPrefix
parameter_list|)
function_decl|;
comment|/** Returns whether the type is case sensitive. */
name|boolean
name|isCaseSensitive
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/** Returns whether the type can be auto increment. */
name|boolean
name|isAutoincrement
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/** Returns the numeric type radix, typically 2 or 10.    * 0 means "not applicable". */
name|int
name|getNumTypeRadix
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/** Returns the return type of a call to the {@code SUM} aggregate function,    * inferred from its argument type. */
name|RelDataType
name|deriveSumType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|argumentType
parameter_list|)
function_decl|;
comment|/** Returns the return type of a call to the {@code AVG}, {@code STDDEV} or    * {@code VAR} aggregate functions, inferred from its argument type.    */
name|RelDataType
name|deriveAvgAggType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|argumentType
parameter_list|)
function_decl|;
comment|/** Returns the return type of a call to the {@code COVAR} aggregate function,    * inferred from its argument types. */
name|RelDataType
name|deriveCovarType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|arg0Type
parameter_list|,
name|RelDataType
name|arg1Type
parameter_list|)
function_decl|;
comment|/** Returns the return type of the {@code CUME_DIST} and {@code PERCENT_RANK}    * aggregate functions. */
name|RelDataType
name|deriveFractionalRankType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
comment|/** Returns the return type of the {@code NTILE}, {@code RANK},    * {@code DENSE_RANK}, and {@code ROW_NUMBER} aggregate functions. */
name|RelDataType
name|deriveRankType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
comment|/** Whether two record types are considered distinct if their field names    * are the same but in different cases. */
name|boolean
name|isSchemaCaseSensitive
parameter_list|()
function_decl|;
comment|/** Whether the least restrictive type of a number of CHAR types of different    * lengths should be a VARCHAR type. And similarly BINARY to VARBINARY. */
name|boolean
name|shouldConvertRaggedUnionTypesToVarying
parameter_list|()
function_decl|;
comment|/**    * Returns whether a decimal multiplication should be implemented by casting    * arguments to double values.    *    *<p>Pre-condition:<code>createDecimalProduct(type1, type2) != null</code>    */
specifier|default
name|boolean
name|shouldUseDoubleMultiplication
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
assert|assert
name|deriveDecimalMultiplyType
argument_list|(
name|typeFactory
argument_list|,
name|type1
argument_list|,
name|type2
argument_list|)
operator|!=
literal|null
assert|;
return|return
literal|false
return|;
block|}
comment|/**    * Infers the return type of a decimal addition. Decimal addition involves    * at least one decimal operand and requires both operands to have exact    * numeric types.    *    *<p>Rules:    *    *<ul>    *<li>Let p1, s1 be the precision and scale of the first operand</li>    *<li>Let p2, s2 be the precision and scale of the second operand</li>    *<li>Let p, s be the precision and scale of the result</li>    *<li>Let d be the number of whole digits in the result</li>    *<li>Then the result type is a decimal with:    *<ul>    *<li>s = max(s1, s2)</li>    *<li>p = max(p1 - s1, p2 - s2) + s + 1</li>    *</ul>    *</li>    *<li>p and s are capped at their maximum values</li>    *</ul>    *    * @see Glossary#SQL2003 SQL:2003 Part 2 Section 6.26    *    * @param typeFactory TypeFactory used to create output type    * @param type1       Type of the first operand    * @param type2       Type of the second operand    * @return Result type for a decimal addition    */
specifier|default
name|RelDataType
name|deriveDecimalPlusType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
comment|// Java numeric will always have invalid precision/scale,
comment|// use its default decimal precision/scale instead.
name|type1
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type1
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type1
argument_list|)
else|:
name|type1
expr_stmt|;
name|type2
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type2
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type2
argument_list|)
else|:
name|type2
expr_stmt|;
name|int
name|p1
init|=
name|type1
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|type2
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|type1
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|type2
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|scale
init|=
name|Math
operator|.
name|max
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
decl_stmt|;
assert|assert
name|scale
operator|<=
name|getMaxNumericScale
argument_list|()
assert|;
name|int
name|precision
init|=
name|Math
operator|.
name|max
argument_list|(
name|p1
operator|-
name|s1
argument_list|,
name|p2
operator|-
name|s2
argument_list|)
operator|+
name|scale
operator|+
literal|1
decl_stmt|;
name|precision
operator|=
name|Math
operator|.
name|min
argument_list|(
name|precision
argument_list|,
name|getMaxNumericPrecision
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|precision
operator|>
literal|0
assert|;
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Infers the return type of a decimal multiplication. Decimal    * multiplication involves at least one decimal operand and requires both    * operands to have exact numeric types.    *    *<p>The default implementation is SQL:2003 compliant.    *    *<p>Rules:    *    *<ul>    *<li>Let p1, s1 be the precision and scale of the first operand</li>    *<li>Let p2, s2 be the precision and scale of the second operand</li>    *<li>Let p, s be the precision and scale of the result</li>    *<li>Let d be the number of whole digits in the result</li>    *<li>Then the result type is a decimal with:    *<ul>    *<li>p = p1 + p2)</li>    *<li>s = s1 + s2</li>    *</ul>    *</li>    *<li>p and s are capped at their maximum values</li>    *</ul>    *    *<p>p and s are capped at their maximum values    *    * @see Glossary#SQL2003 SQL:2003 Part 2 Section 6.26    *    * @param typeFactory TypeFactory used to create output type    * @param type1       Type of the first operand    * @param type2       Type of the second operand    * @return Result type for a decimal multiplication, or null if decimal    * multiplication should not be applied to the operands    */
specifier|default
name|RelDataType
name|deriveDecimalMultiplyType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
comment|// Java numeric will always have invalid precision/scale,
comment|// use its default decimal precision/scale instead.
name|type1
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type1
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type1
argument_list|)
else|:
name|type1
expr_stmt|;
name|type2
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type2
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type2
argument_list|)
else|:
name|type2
expr_stmt|;
name|int
name|p1
init|=
name|type1
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|type2
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|type1
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|type2
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|scale
init|=
name|s1
operator|+
name|s2
decl_stmt|;
name|scale
operator|=
name|Math
operator|.
name|min
argument_list|(
name|scale
argument_list|,
name|getMaxNumericScale
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|precision
init|=
name|p1
operator|+
name|p2
decl_stmt|;
name|precision
operator|=
name|Math
operator|.
name|min
argument_list|(
name|precision
argument_list|,
name|getMaxNumericPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|RelDataType
name|ret
decl_stmt|;
name|ret
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Infers the return type of a decimal division. Decimal division involves    * at least one decimal operand and requires both operands to have exact    * numeric types.    *    *<p>The default implementation is SQL:2003 compliant.    *    *<p>Rules:    *    *<ul>    *<li>Let p1, s1 be the precision and scale of the first operand</li>    *<li>Let p2, s2 be the precision and scale of the second operand</li>    *<li>Let p, s be the precision and scale of the result</li>    *<li>Let d be the number of whole digits in the result</li>    *<li>Then the result type is a decimal with:    *<ul>    *<li>d = p1 - s1 + s2</li>    *<li>s&lt; max(6, s1 + p2 + 1)</li>    *<li>p = d + s</li>    *</ul>    *</li>    *<li>p and s are capped at their maximum values</li>    *</ul>    *    * @see Glossary#SQL2003 SQL:2003 Part 2 Section 6.26    *    * @param typeFactory TypeFactory used to create output type    * @param type1       Type of the first operand    * @param type2       Type of the second operand    * @return Result type for a decimal division, or null if decimal    * division should not be applied to the operands    */
specifier|default
name|RelDataType
name|deriveDecimalDivideType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
comment|// Java numeric will always have invalid precision/scale,
comment|// use its default decimal precision/scale instead.
name|type1
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type1
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type1
argument_list|)
else|:
name|type1
expr_stmt|;
name|type2
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type2
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type2
argument_list|)
else|:
name|type2
expr_stmt|;
name|int
name|p1
init|=
name|type1
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|type2
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|type1
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|type2
operator|.
name|getScale
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxNumericPrecision
init|=
name|getMaxNumericPrecision
argument_list|()
decl_stmt|;
name|int
name|dout
init|=
name|Math
operator|.
name|min
argument_list|(
name|p1
operator|-
name|s1
operator|+
name|s2
argument_list|,
name|maxNumericPrecision
argument_list|)
decl_stmt|;
name|int
name|scale
init|=
name|Math
operator|.
name|max
argument_list|(
literal|6
argument_list|,
name|s1
operator|+
name|p2
operator|+
literal|1
argument_list|)
decl_stmt|;
name|scale
operator|=
name|Math
operator|.
name|min
argument_list|(
name|scale
argument_list|,
name|maxNumericPrecision
operator|-
name|dout
argument_list|)
expr_stmt|;
name|scale
operator|=
name|Math
operator|.
name|min
argument_list|(
name|scale
argument_list|,
name|getMaxNumericScale
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|precision
init|=
name|dout
operator|+
name|scale
decl_stmt|;
assert|assert
name|precision
operator|<=
name|maxNumericPrecision
assert|;
assert|assert
name|precision
operator|>
literal|0
assert|;
name|RelDataType
name|ret
decl_stmt|;
name|ret
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Infers the return type of a decimal modulus operation. Decimal modulus    * involves at least one decimal operand.    *    *<p>The default implementation is SQL:2003 compliant: the declared type of    * the result is the declared type of the second operand (expression divisor).    *    * @see Glossary#SQL2003 SQL:2003 Part 2 Section 6.27    *    *<p>Rules:    *    *<ul>    *<li>Let p1, s1 be the precision and scale of the first operand</li>    *<li>Let p2, s2 be the precision and scale of the second operand</li>    *<li>Let p, s be the precision and scale of the result</li>    *<li>Let d be the number of whole digits in the result</li>    *<li>Then the result type is a decimal with:    *<ul>    *<li>s = max(s1, s2)</li>    *<li>p = min(p1 - s1, p2 - s2) + max(s1, s2)</li>    *</ul>    *</li>    *<li>p and s are capped at their maximum values</li>    *</ul>    *    * @param typeFactory TypeFactory used to create output type    * @param type1       Type of the first operand    * @param type2       Type of the second operand    * @return Result type for a decimal modulus, or null if decimal    * modulus should not be applied to the operands    */
specifier|default
name|RelDataType
name|deriveDecimalModType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
comment|// Java numeric will always have invalid precision/scale,
comment|// use its default decimal precision/scale instead.
name|type1
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type1
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type1
argument_list|)
else|:
name|type1
expr_stmt|;
name|type2
operator|=
name|RelDataTypeFactoryImpl
operator|.
name|isJavaType
argument_list|(
name|type2
argument_list|)
condition|?
name|typeFactory
operator|.
name|decimalOf
argument_list|(
name|type2
argument_list|)
else|:
name|type2
expr_stmt|;
name|int
name|p1
init|=
name|type1
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|type2
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|type1
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|type2
operator|.
name|getScale
argument_list|()
decl_stmt|;
comment|// Keep consistency with SQL standard.
if|if
condition|(
name|s1
operator|==
literal|0
operator|&&
name|s2
operator|==
literal|0
condition|)
block|{
return|return
name|type2
return|;
block|}
name|int
name|scale
init|=
name|Math
operator|.
name|max
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
decl_stmt|;
assert|assert
name|scale
operator|<=
name|getMaxNumericScale
argument_list|()
assert|;
name|int
name|precision
init|=
name|Math
operator|.
name|min
argument_list|(
name|p1
operator|-
name|s1
argument_list|,
name|p2
operator|-
name|s2
argument_list|)
operator|+
name|Math
operator|.
name|max
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
decl_stmt|;
name|precision
operator|=
name|Math
operator|.
name|min
argument_list|(
name|precision
argument_list|,
name|getMaxNumericPrecision
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|precision
operator|>
literal|0
assert|;
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_interface

end_unit

