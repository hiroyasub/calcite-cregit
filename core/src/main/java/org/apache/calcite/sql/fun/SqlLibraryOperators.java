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
operator|.
name|fun
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
name|sql
operator|.
name|SqlFunction
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
name|SqlFunctionCategory
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
name|SqlKind
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
name|SqlOperatorTable
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
name|OperandTypes
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
name|ReturnTypes
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
name|SqlReturnTypeInference
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
name|SqlTypeTransforms
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
name|sql
operator|.
name|fun
operator|.
name|SqlLibrary
operator|.
name|MYSQL
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
name|sql
operator|.
name|fun
operator|.
name|SqlLibrary
operator|.
name|ORACLE
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
name|sql
operator|.
name|fun
operator|.
name|SqlLibrary
operator|.
name|POSTGRESQL
import|;
end_import

begin_comment
comment|/**  * Defines functions and operators that are not part of standard SQL but  * belong to one or more other dialects of SQL.  *  *<p>They are read by {@link SqlLibraryOperatorTableFactory} into instances  * of {@link SqlOperatorTable} that contain functions and operators for  * particular libraries.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlLibraryOperators
block|{
specifier|private
name|SqlLibraryOperators
parameter_list|()
block|{
block|}
comment|/** Return type inference for {@code DECODE}. */
specifier|private
specifier|static
specifier|final
name|SqlReturnTypeInference
name|DECODE_RETURN_TYPE
init|=
name|opBinding
lambda|->
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
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
name|int
name|i
init|=
literal|1
init|,
name|n
init|=
name|opBinding
operator|.
name|getOperandCount
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
name|n
operator|-
literal|1
condition|)
block|{
operator|++
name|i
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|opBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|list
argument_list|)
decl_stmt|;
if|if
condition|(
name|opBinding
operator|.
name|getOperandCount
argument_list|()
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|type
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
decl_stmt|;
comment|/** The "DECODE(v, v1, result1, [v2, result2, ...], resultN)" function. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|DECODE
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"DECODE"
argument_list|,
name|SqlKind
operator|.
name|DECODE
argument_list|,
name|DECODE_RETURN_TYPE
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|VARIADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
decl_stmt|;
comment|/** The "NVL(value, value)" function. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|NVL
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"NVL"
argument_list|,
name|SqlKind
operator|.
name|NVL
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|LEAST_RESTRICTIVE
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE_ALL
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|SAME_SAME
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
decl_stmt|;
comment|/** The "LTRIM(string)" function. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|LTRIM
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"LTRIM"
argument_list|,
name|SqlKind
operator|.
name|LTRIM
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|ARG0
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_VARYING
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|STRING
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
comment|/** The "RTRIM(string)" function. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|RTRIM
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"RTRIM"
argument_list|,
name|SqlKind
operator|.
name|RTRIM
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|ARG0
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_VARYING
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|STRING
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
comment|/** Oracle's "SUBSTR(string, position [, substringLength ])" function.    *    *<p>It has similar semantics to standard SQL's    * {@link SqlStdOperatorTable#SUBSTRING} function but different syntax. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|SUBSTR
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"SUBSTR"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_VARYING
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
comment|/** The "GREATEST(value, value)" function. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|GREATEST
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"GREATEST"
argument_list|,
name|SqlKind
operator|.
name|GREATEST
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|LEAST_RESTRICTIVE
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|SAME_VARIADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
decl_stmt|;
comment|/** The "LEAST(value, value)" function. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|LEAST
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"LEAST"
argument_list|,
name|SqlKind
operator|.
name|LEAST
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|LEAST_RESTRICTIVE
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|SAME_VARIADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
decl_stmt|;
comment|/**    * The<code>TRANSLATE(<i>string_expr</i>,<i>search_chars</i>,    *<i>replacement_chars</i>)</code> function returns<i>string_expr</i> with    * all occurrences of each character in<i>search_chars</i> replaced by its    * corresponding character in<i>replacement_chars</i>.    *    *<p>It is not defined in the SQL standard, but occurs in Oracle and    * PostgreSQL.    */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|,
name|POSTGRESQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|TRANSLATE3
init|=
operator|new
name|SqlTranslate3Function
argument_list|()
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|JSON_TYPE
init|=
operator|new
name|SqlJsonTypeFunction
argument_list|()
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|JSON_DEPTH
init|=
operator|new
name|SqlJsonDepthFunction
argument_list|()
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|JSON_LENGTH
init|=
operator|new
name|SqlJsonLengthFunction
argument_list|()
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|JSON_KEYS
init|=
operator|new
name|SqlJsonKeysFunction
argument_list|()
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|JSON_PRETTY
init|=
operator|new
name|SqlJsonPrettyFunction
argument_list|()
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|JSON_REMOVE
init|=
operator|new
name|SqlJsonRemoveFunction
argument_list|()
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|JSON_STORAGE_SIZE
init|=
operator|new
name|SqlJsonStorageSizeFunction
argument_list|()
decl_stmt|;
comment|/** The "MONTHNAME(datetime)" function; returns the name of the month,    * in the current locale, of a TIMESTAMP or DATE argument. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|MONTHNAME
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"MONTHNAME"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|VARCHAR_2000
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|DATETIME
argument_list|,
name|SqlFunctionCategory
operator|.
name|TIMEDATE
argument_list|)
decl_stmt|;
comment|/** The "DAYNAME(datetime)" function; returns the name of the day of the week,    * in the current locale, of a TIMESTAMP or DATE argument. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|DAYNAME
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"DAYNAME"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|VARCHAR_2000
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|DATETIME
argument_list|,
name|SqlFunctionCategory
operator|.
name|TIMEDATE
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|,
name|POSTGRESQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|LEFT
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"LEFT"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_VARYING
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|CBSTRING_INTEGER
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|,
name|POSTGRESQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|REPEAT
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"REPEAT"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_VARYING
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|STRING_INTEGER
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|,
name|POSTGRESQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|RIGHT
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"RIGHT"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_VARYING
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|CBSTRING_INTEGER
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|SPACE
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"SPACE"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|VARCHAR_2000_NULLABLE
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|INTEGER
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|,
name|POSTGRESQL
block|,
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|SOUNDEX
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"SOUNDEX"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|VARCHAR_4_NULLABLE
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|CHARACTER
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|POSTGRESQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|DIFFERENCE
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"DIFFERENCE"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|INTEGER_NULLABLE
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|STRING_STRING
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|REVERSE
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"REVERSE"
argument_list|,
name|SqlKind
operator|.
name|REVERSE
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_VARYING
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|CHARACTER
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|FROM_BASE64
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"FROM_BASE64"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|explicit
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|STRING
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|MYSQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|TO_BASE64
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"TO_BASE64"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|explicit
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|or
argument_list|(
name|OperandTypes
operator|.
name|STRING
argument_list|,
name|OperandTypes
operator|.
name|BINARY
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|ORACLE
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|CHR
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"CHR"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|CHAR
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|INTEGER
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
comment|/** Infix "::" cast operator used by PostgreSQL, for example    * {@code '100'::INTEGER}. */
annotation|@
name|LibraryOperator
argument_list|(
name|libraries
operator|=
block|{
name|POSTGRESQL
block|}
argument_list|)
specifier|public
specifier|static
specifier|final
name|SqlOperator
name|INFIX_CAST
init|=
operator|new
name|SqlCastOperator
argument_list|()
decl_stmt|;
block|}
end_class

begin_comment
comment|// End SqlLibraryOperators.java
end_comment

end_unit

