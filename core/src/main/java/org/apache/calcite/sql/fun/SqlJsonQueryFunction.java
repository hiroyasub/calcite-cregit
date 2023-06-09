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
name|SqlJsonQueryEmptyOrErrorBehavior
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
name|SqlJsonQueryWrapperBehavior
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
comment|/**  * The<code>JSON_QUERY</code> function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlJsonQueryFunction
extends|extends
name|SqlFunction
block|{
specifier|public
name|SqlJsonQueryFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"JSON_QUERY"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|VARCHAR_2000
operator|.
name|andThen
argument_list|(
name|SqlTypeTransforms
operator|.
name|FORCE_NULLABLE
argument_list|)
argument_list|,
literal|null
argument_list|,
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
name|CHARACTER
argument_list|,
name|SqlTypeFamily
operator|.
name|ANY
argument_list|,
name|SqlTypeFamily
operator|.
name|ANY
argument_list|,
name|SqlTypeFamily
operator|.
name|ANY
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|String
name|getSignatureTemplate
parameter_list|(
name|int
name|operandsCount
parameter_list|)
block|{
return|return
literal|"{0}({1} {2} {3} WRAPPER {4} ON EMPTY {5} ON ERROR)"
return|;
block|}
annotation|@
name|Override
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
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|true
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
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|SqlJsonQueryWrapperBehavior
name|wrapperBehavior
init|=
name|getEnumValue
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|wrapperBehavior
condition|)
block|{
case|case
name|WITHOUT_ARRAY
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"WITHOUT ARRAY"
argument_list|)
expr_stmt|;
break|break;
case|case
name|WITH_CONDITIONAL_ARRAY
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"WITH CONDITIONAL ARRAY"
argument_list|)
expr_stmt|;
break|break;
case|case
name|WITH_UNCONDITIONAL_ARRAY
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"WITH UNCONDITIONAL ARRAY"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unreachable code"
argument_list|)
throw|;
block|}
name|writer
operator|.
name|keyword
argument_list|(
literal|"WRAPPER"
argument_list|)
expr_stmt|;
name|unparseEmptyOrErrorBehavior
argument_list|(
name|writer
argument_list|,
name|getEnumValue
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"ON EMPTY"
argument_list|)
expr_stmt|;
name|unparseEmptyOrErrorBehavior
argument_list|(
name|writer
argument_list|,
name|getEnumValue
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"ON ERROR"
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
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
annotation|@
name|Nullable
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
annotation|@
name|Nullable
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
if|if
condition|(
name|operands
index|[
literal|2
index|]
operator|==
literal|null
condition|)
block|{
name|operands
index|[
literal|2
index|]
operator|=
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|SqlJsonQueryWrapperBehavior
operator|.
name|WITHOUT_ARRAY
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|operands
index|[
literal|3
index|]
operator|==
literal|null
condition|)
block|{
name|operands
index|[
literal|3
index|]
operator|=
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|SqlJsonQueryEmptyOrErrorBehavior
operator|.
name|NULL
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|operands
index|[
literal|4
index|]
operator|==
literal|null
condition|)
block|{
name|operands
index|[
literal|4
index|]
operator|=
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|SqlJsonQueryEmptyOrErrorBehavior
operator|.
name|NULL
argument_list|,
name|pos
argument_list|)
expr_stmt|;
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
specifier|private
specifier|static
name|void
name|unparseEmptyOrErrorBehavior
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlJsonQueryEmptyOrErrorBehavior
name|emptyBehavior
parameter_list|)
block|{
switch|switch
condition|(
name|emptyBehavior
condition|)
block|{
case|case
name|NULL
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"NULL"
argument_list|)
expr_stmt|;
break|break;
case|case
name|ERROR
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"ERROR"
argument_list|)
expr_stmt|;
break|break;
case|case
name|EMPTY_ARRAY
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"EMPTY ARRAY"
argument_list|)
expr_stmt|;
break|break;
case|case
name|EMPTY_OBJECT
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"EMPTY OBJECT"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unreachable code"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|E
name|getEnumValue
parameter_list|(
name|SqlNode
name|operand
parameter_list|)
block|{
return|return
operator|(
name|E
operator|)
name|requireNonNull
argument_list|(
operator|(
operator|(
name|SqlLiteral
operator|)
name|operand
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"operand.value"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

