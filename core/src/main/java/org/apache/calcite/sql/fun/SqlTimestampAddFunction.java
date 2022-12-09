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
name|avatica
operator|.
name|util
operator|.
name|TimeUnit
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
name|SqlIntervalQualifier
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
name|SqlValidatorScope
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Util
operator|.
name|first
import|;
end_import

begin_comment
comment|/**  * The<code>TIMESTAMPADD</code> function, which adds an interval to a  * datetime (TIMESTAMP, TIME or DATE).  *  *<p>The SQL syntax is  *  *<blockquote>  *<code>TIMESTAMPADD(<i>timestamp interval</i>,<i>quantity</i>,  *<i>datetime</i>)</code>  *</blockquote>  *  *<p>The interval time unit can one of the following literals:<ul>  *<li>NANOSECOND (and synonym SQL_TSI_FRAC_SECOND)  *<li>MICROSECOND (and synonyms SQL_TSI_MICROSECOND, FRAC_SECOND)  *<li>SECOND (and synonym SQL_TSI_SECOND)  *<li>MINUTE (and synonym  SQL_TSI_MINUTE)  *<li>HOUR (and synonym  SQL_TSI_HOUR)  *<li>DAY (and synonym SQL_TSI_DAY)  *<li>WEEK (and synonym  SQL_TSI_WEEK)  *<li>MONTH (and synonym SQL_TSI_MONTH)  *<li>QUARTER (and synonym SQL_TSI_QUARTER)  *<li>YEAR (and synonym  SQL_TSI_YEAR)  *</ul>  *  *<p>Returns modified datetime.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTimestampAddFunction
extends|extends
name|SqlFunction
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MILLISECOND_PRECISION
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MICROSECOND_PRECISION
init|=
literal|6
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlReturnTypeInference
name|RETURN_TYPE_INFERENCE
init|=
name|opBinding
lambda|->
name|deduceType
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|opBinding
operator|.
name|getOperandLiteralValue
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|class
argument_list|)
argument_list|,
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|RelDataType
name|deduceType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
annotation|@
name|Nullable
name|TimeUnit
name|timeUnit
parameter_list|,
name|RelDataType
name|operandType1
parameter_list|,
name|RelDataType
name|operandType2
parameter_list|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|deduceType
argument_list|(
name|typeFactory
argument_list|,
name|timeUnit
argument_list|,
name|operandType2
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|operandType1
operator|.
name|isNullable
argument_list|()
operator|||
name|operandType2
operator|.
name|isNullable
argument_list|()
argument_list|)
return|;
block|}
specifier|static
name|RelDataType
name|deduceType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
annotation|@
name|Nullable
name|TimeUnit
name|timeUnit
parameter_list|,
name|RelDataType
name|datetimeType
parameter_list|)
block|{
name|TimeUnit
name|timeUnit2
init|=
name|first
argument_list|(
name|timeUnit
argument_list|,
name|TimeUnit
operator|.
name|EPOCH
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|timeUnit2
condition|)
block|{
case|case
name|MILLISECOND
case|:
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|MILLISECOND_PRECISION
argument_list|)
return|;
case|case
name|MICROSECOND
case|:
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|MICROSECOND_PRECISION
argument_list|)
return|;
case|case
name|HOUR
case|:
case|case
name|MINUTE
case|:
case|case
name|SECOND
case|:
name|SqlTypeName
name|typeName
init|=
name|datetimeType
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|TIME
case|:
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
break|break;
default|default:
comment|// If it is not a TIMESTAMP_WITH_LOCAL_TIME_ZONE, operations involving
comment|// HOUR, MINUTE, SECOND with DATE or TIMESTAMP types will result in
comment|// TIMESTAMP type.
name|typeName
operator|=
name|SqlTypeName
operator|.
name|TIMESTAMP
expr_stmt|;
break|break;
block|}
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
return|;
default|default:
case|case
name|EPOCH
case|:
return|return
name|datetimeType
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|validateCall
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlValidatorScope
name|operandScope
parameter_list|)
block|{
name|super
operator|.
name|validateCall
argument_list|(
name|call
argument_list|,
name|validator
argument_list|,
name|scope
argument_list|,
name|operandScope
argument_list|)
expr_stmt|;
comment|// This is either a time unit or a time frame:
comment|//
comment|//  * In "TIMESTAMPADD(YEAR, 2, x)" operand 0 is a SqlIntervalQualifier
comment|//    with startUnit = YEAR and timeFrameName = null.
comment|//
comment|//  * In "TIMESTAMPADD(MINUTE15, 2, x) operand 0 is a SqlIntervalQualifier
comment|//    with startUnit = EPOCH and timeFrameName = 'MINUTE15'.
comment|//
comment|// If the latter, check that timeFrameName is valid.
name|validator
operator|.
name|validateTimeFrame
argument_list|(
operator|(
name|SqlIntervalQualifier
operator|)
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a SqlTimestampAddFunction. */
name|SqlTimestampAddFunction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|TIMESTAMP_ADD
argument_list|,
name|RETURN_TYPE_INFERENCE
operator|.
name|andThen
argument_list|(
name|SqlTypeTransforms
operator|.
name|TO_NULLABLE
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
name|INTEGER
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|TIMEDATE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

