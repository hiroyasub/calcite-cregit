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
name|sql
operator|.
name|SqlCall
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
name|SqlCallBinding
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
name|SqlLiteral
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
name|SqlNode
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
name|SqlWriter
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
name|SameOperandTypeChecker
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
name|SqlTypeFamily
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
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * Definition of the "TRIM" builtin SQL function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTrimFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Defines the enumerated values "LEADING", "TRAILING", "BOTH".    */
specifier|public
enum|enum
name|Flag
implements|implements
name|SqlLiteral
operator|.
name|SqlSymbol
block|{
name|BOTH
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
block|,
name|LEADING
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
block|,
name|TRAILING
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
block|;
specifier|private
specifier|final
name|int
name|left
decl_stmt|;
specifier|private
specifier|final
name|int
name|right
decl_stmt|;
name|Flag
parameter_list|(
name|int
name|left
parameter_list|,
name|int
name|right
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
specifier|public
name|int
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|int
name|getRight
parameter_list|()
block|{
return|return
name|right
return|;
block|}
comment|/**      * Creates a parse-tree node representing an occurrence of this flag      * at a particular position in the parsed text.      */
specifier|public
name|SqlLiteral
name|symbol
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlTrimFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"TRIM"
argument_list|,
name|SqlKind
operator|.
name|TRIM
argument_list|,
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|ReturnTypes
operator|.
name|ARG2
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
name|and
argument_list|(
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ANY
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|)
argument_list|,
comment|// Arguments 1 and 2 must have same type
operator|new
name|SameOperandTypeChecker
argument_list|(
literal|3
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|Integer
argument_list|>
name|getOperandList
parameter_list|(
name|int
name|operandCount
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startFunCall
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|SqlLiteral
operator|:
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
assert|;
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"FROM"
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
specifier|final
name|int
name|operandsCount
parameter_list|)
block|{
switch|switch
condition|(
name|operandsCount
condition|)
block|{
case|case
literal|3
case|:
return|return
literal|"{0}([BOTH|LEADING|TRAILING} {1} FROM {2})"
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
specifier|public
name|SqlCall
name|createCall
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
assert|assert
name|functionQualifier
operator|==
literal|null
assert|;
switch|switch
condition|(
name|operands
operator|.
name|length
condition|)
block|{
case|case
literal|1
case|:
comment|// This variant occurs when someone writes TRIM(string)
comment|// as opposed to the sugared syntax TRIM(string FROM string).
name|operands
operator|=
operator|new
name|SqlNode
index|[]
block|{
name|Flag
operator|.
name|BOTH
operator|.
name|symbol
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
block|,
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
literal|" "
argument_list|,
name|pos
argument_list|)
block|,
name|operands
index|[
literal|0
index|]
block|}
expr_stmt|;
break|break;
case|case
literal|3
case|:
assert|assert
name|operands
index|[
literal|0
index|]
operator|instanceof
name|SqlLiteral
operator|&&
operator|(
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|0
index|]
operator|)
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Flag
assert|;
if|if
condition|(
name|operands
index|[
literal|1
index|]
operator|==
literal|null
condition|)
block|{
name|operands
index|[
literal|1
index|]
operator|=
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
literal|" "
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid operand count "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|operands
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|createCall
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|operands
argument_list|)
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
name|SqlTypeUtil
operator|.
name|isCharTypeComparable
argument_list|(
name|callBinding
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|callBinding
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|,
name|callBinding
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlTrimFunction.java
end_comment

end_unit

