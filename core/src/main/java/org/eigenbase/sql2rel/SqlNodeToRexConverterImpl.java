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
name|sql2rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
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
name|parser
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
name|type
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
name|validate
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
name|util
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
name|avatica
operator|.
name|ByteString
import|;
end_import

begin_comment
comment|/**  * Standard implementation of {@link SqlNodeToRexConverter}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlNodeToRexConverterImpl
implements|implements
name|SqlNodeToRexConverter
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlRexConvertletTable
name|convertletTable
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|SqlNodeToRexConverterImpl
parameter_list|(
name|SqlRexConvertletTable
name|convertletTable
parameter_list|)
block|{
name|this
operator|.
name|convertletTable
operator|=
name|convertletTable
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RexNode
name|convertCall
parameter_list|(
name|SqlRexContext
name|cx
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
specifier|final
name|SqlRexConvertlet
name|convertlet
init|=
name|convertletTable
operator|.
name|get
argument_list|(
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
operator|.
name|convertCall
argument_list|(
name|cx
argument_list|,
name|call
argument_list|)
return|;
block|}
comment|// No convertlet was suitable. (Unlikely, because the standard
comment|// convertlet table has a fall-back for all possible calls.)
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|call
argument_list|)
throw|;
block|}
specifier|public
name|RexLiteral
name|convertInterval
parameter_list|(
name|SqlRexContext
name|cx
parameter_list|,
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
block|{
name|RexBuilder
name|rexBuilder
init|=
name|cx
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeIntervalLiteral
argument_list|(
name|intervalQualifier
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|convertLiteral
parameter_list|(
name|SqlRexContext
name|cx
parameter_list|,
name|SqlLiteral
name|literal
parameter_list|)
block|{
name|RexBuilder
name|rexBuilder
init|=
name|cx
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|cx
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|SqlValidator
name|validator
init|=
name|cx
operator|.
name|getValidator
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|value
init|=
name|literal
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// Since there is no eq. RexLiteral of SqlLiteral.Unknown we
comment|// treat it as a cast(null as boolean)
name|RelDataType
name|type
decl_stmt|;
if|if
condition|(
name|literal
operator|.
name|getTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
name|type
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
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
else|else
block|{
name|type
operator|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|literal
argument_list|)
expr_stmt|;
block|}
return|return
name|rexBuilder
operator|.
name|makeCast
argument_list|(
name|type
argument_list|,
name|rexBuilder
operator|.
name|constantNull
argument_list|()
argument_list|)
return|;
block|}
name|BitString
name|bitString
decl_stmt|;
name|SqlIntervalLiteral
operator|.
name|IntervalValue
name|intervalValue
decl_stmt|;
name|long
name|l
decl_stmt|;
switch|switch
condition|(
name|literal
operator|.
name|getTypeName
argument_list|()
condition|)
block|{
case|case
name|DECIMAL
case|:
comment|// exact number
name|BigDecimal
name|bd
init|=
operator|(
name|BigDecimal
operator|)
name|value
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|bd
argument_list|,
name|literal
operator|.
name|createSqlType
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
return|;
case|case
name|DOUBLE
case|:
comment|// approximate type
comment|// TODO:  preserve fixed-point precision and large integers
return|return
name|rexBuilder
operator|.
name|makeApproxLiteral
argument_list|(
operator|(
name|BigDecimal
operator|)
name|value
argument_list|)
return|;
case|case
name|CHAR
case|:
return|return
name|rexBuilder
operator|.
name|makeCharLiteral
argument_list|(
operator|(
name|NlsString
operator|)
name|value
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
return|return
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
return|;
case|case
name|BINARY
case|:
name|bitString
operator|=
operator|(
name|BitString
operator|)
name|value
expr_stmt|;
name|Util
operator|.
name|permAssert
argument_list|(
operator|(
name|bitString
operator|.
name|getBitCount
argument_list|()
operator|%
literal|8
operator|)
operator|==
literal|0
argument_list|,
literal|"incomplete octet"
argument_list|)
expr_stmt|;
comment|// An even number of hexits (e.g. X'ABCD') makes whole number
comment|// of bytes.
name|ByteString
name|byteString
init|=
operator|new
name|ByteString
argument_list|(
name|bitString
operator|.
name|getAsByteArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeBinaryLiteral
argument_list|(
name|byteString
argument_list|)
return|;
case|case
name|SYMBOL
case|:
return|return
name|rexBuilder
operator|.
name|makeFlag
argument_list|(
operator|(
name|Enum
operator|)
name|value
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|rexBuilder
operator|.
name|makeTimestampLiteral
argument_list|(
operator|(
name|Calendar
operator|)
name|value
argument_list|,
operator|(
operator|(
name|SqlTimestampLiteral
operator|)
name|literal
operator|)
operator|.
name|getPrec
argument_list|()
argument_list|)
return|;
case|case
name|TIME
case|:
return|return
name|rexBuilder
operator|.
name|makeTimeLiteral
argument_list|(
operator|(
name|Calendar
operator|)
name|value
argument_list|,
operator|(
operator|(
name|SqlTimeLiteral
operator|)
name|literal
operator|)
operator|.
name|getPrec
argument_list|()
argument_list|)
return|;
case|case
name|DATE
case|:
return|return
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
operator|(
name|Calendar
operator|)
name|value
argument_list|)
return|;
case|case
name|INTERVAL_YEAR_MONTH
case|:
name|intervalValue
operator|=
operator|(
name|SqlIntervalLiteral
operator|.
name|IntervalValue
operator|)
name|value
expr_stmt|;
name|l
operator|=
name|SqlParserUtil
operator|.
name|intervalToMonths
argument_list|(
name|intervalValue
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeIntervalLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|l
argument_list|)
argument_list|,
name|intervalValue
operator|.
name|getIntervalQualifier
argument_list|()
argument_list|)
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
name|intervalValue
operator|=
operator|(
name|SqlIntervalLiteral
operator|.
name|IntervalValue
operator|)
name|value
expr_stmt|;
name|l
operator|=
name|SqlParserUtil
operator|.
name|intervalToMillis
argument_list|(
name|intervalValue
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeIntervalLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|l
argument_list|)
argument_list|,
name|intervalValue
operator|.
name|getIntervalQualifier
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlNodeToRexConverterImpl.java
end_comment

end_unit

