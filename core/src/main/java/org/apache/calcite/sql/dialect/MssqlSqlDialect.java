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
name|dialect
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
name|TimeUnitRange
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
name|RelDataTypeSystem
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
name|SqlAbstractDateTimeLiteral
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
name|SqlDialect
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
name|SqlIntervalLiteral
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
name|SqlUtil
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
name|type
operator|.
name|ReturnTypes
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDialect</code> implementation for the Microsoft SQL Server  * database.  */
end_comment

begin_class
specifier|public
class|class
name|MssqlSqlDialect
extends|extends
name|SqlDialect
block|{
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|DEFAULT
init|=
operator|new
name|MssqlSqlDialect
argument_list|(
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|MSSQL
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"["
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlFunction
name|MSSQL_SUBSTRING
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"SUBSTRING"
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
comment|/** Whether to generate "SELECT TOP(fetch)" rather than    * "SELECT ... FETCH NEXT fetch ROWS ONLY". */
specifier|private
specifier|final
name|boolean
name|top
decl_stmt|;
comment|/** Creates a MssqlSqlDialect. */
specifier|public
name|MssqlSqlDialect
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// MSSQL 2008 (version 10) and earlier only supports TOP
comment|// MSSQL 2012 (version 11) and higher supports OFFSET and FETCH
name|top
operator|=
name|context
operator|.
name|databaseMajorVersion
argument_list|()
operator|<
literal|11
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseOffsetFetch
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
name|offset
parameter_list|,
name|SqlNode
name|fetch
parameter_list|)
block|{
if|if
condition|(
operator|!
name|top
condition|)
block|{
name|super
operator|.
name|unparseOffsetFetch
argument_list|(
name|writer
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseTopN
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
name|offset
parameter_list|,
name|SqlNode
name|fetch
parameter_list|)
block|{
if|if
condition|(
name|top
condition|)
block|{
comment|// Per Microsoft:
comment|//   "For backward compatibility, the parentheses are optional in SELECT
comment|//   statements. We recommend that you always use parentheses for TOP in
comment|//   SELECT statements. Doing so provides consistency with its required
comment|//   use in INSERT, UPDATE, MERGE, and DELETE statements."
comment|//
comment|// Note that "fetch" is ignored.
name|writer
operator|.
name|keyword
argument_list|(
literal|"TOP"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseDateTimeLiteral
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlAbstractDateTimeLiteral
name|literal
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|literal
argument_list|(
literal|"'"
operator|+
name|literal
operator|.
name|toFormattedString
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseCall
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
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
condition|)
block|{
if|if
condition|(
name|call
operator|.
name|operandCount
argument_list|()
operator|!=
literal|3
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"MSSQL SUBSTRING requires FROM and FOR arguments"
argument_list|)
throw|;
block|}
name|SqlUtil
operator|.
name|unparseFunctionSyntax
argument_list|(
name|MSSQL_SUBSTRING
argument_list|,
name|writer
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|FLOOR
case|:
if|if
condition|(
name|call
operator|.
name|operandCount
argument_list|()
operator|!=
literal|2
condition|)
block|{
name|super
operator|.
name|unparseCall
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
return|return;
block|}
name|unparseFloor
argument_list|(
name|writer
argument_list|,
name|call
argument_list|)
expr_stmt|;
break|break;
default|default:
name|super
operator|.
name|unparseCall
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsCharSet
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Unparses datetime floor for Microsoft SQL Server.    * There is no TRUNC function, so simulate this using calls to CONVERT.    *    * @param writer Writer    * @param call Call    */
specifier|private
name|void
name|unparseFloor
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|SqlLiteral
name|node
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TimeUnitRange
name|unit
init|=
operator|(
name|TimeUnitRange
operator|)
name|node
operator|.
name|getValue
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|unit
condition|)
block|{
case|case
name|YEAR
case|:
name|unparseFloorWithUnit
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|4
argument_list|,
literal|"-01-01"
argument_list|)
expr_stmt|;
break|break;
case|case
name|MONTH
case|:
name|unparseFloorWithUnit
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|7
argument_list|,
literal|"-01"
argument_list|)
expr_stmt|;
break|break;
case|case
name|WEEK
case|:
name|writer
operator|.
name|print
argument_list|(
literal|"CONVERT(DATETIME, CONVERT(VARCHAR(10), "
operator|+
literal|"DATEADD(day, - (6 + DATEPART(weekday, "
argument_list|)
expr_stmt|;
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
name|print
argument_list|(
literal|")) % 7, "
argument_list|)
expr_stmt|;
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
name|print
argument_list|(
literal|"), 126))"
argument_list|)
expr_stmt|;
break|break;
case|case
name|DAY
case|:
name|unparseFloorWithUnit
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|10
argument_list|,
literal|""
argument_list|)
expr_stmt|;
break|break;
case|case
name|HOUR
case|:
name|unparseFloorWithUnit
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|13
argument_list|,
literal|":00:00"
argument_list|)
expr_stmt|;
break|break;
case|case
name|MINUTE
case|:
name|unparseFloorWithUnit
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|16
argument_list|,
literal|":00"
argument_list|)
expr_stmt|;
break|break;
case|case
name|SECOND
case|:
name|unparseFloorWithUnit
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|19
argument_list|,
literal|":00"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"MSSQL does not support FLOOR for time unit: "
operator|+
name|unit
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseSqlDatetimeArithmetic
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlKind
name|sqlKind
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
literal|"DATEADD"
argument_list|)
decl_stmt|;
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|instanceof
name|SqlIntervalLiteral
condition|)
block|{
comment|//There is no DATESUB method available, so change the sign.
name|unparseSqlIntervalLiteralMssql
argument_list|(
name|writer
argument_list|,
operator|(
name|SqlIntervalLiteral
operator|)
name|operand
argument_list|,
name|sqlKind
operator|==
name|SqlKind
operator|.
name|MINUS
condition|?
operator|-
literal|1
else|:
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|operand
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
block|}
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
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseSqlIntervalQualifier
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlIntervalQualifier
name|qualifier
parameter_list|,
name|RelDataTypeSystem
name|typeSystem
parameter_list|)
block|{
switch|switch
condition|(
name|qualifier
operator|.
name|timeUnitRange
condition|)
block|{
case|case
name|YEAR
case|:
case|case
name|QUARTER
case|:
case|case
name|MONTH
case|:
case|case
name|WEEK
case|:
case|case
name|DAY
case|:
case|case
name|HOUR
case|:
case|case
name|MINUTE
case|:
case|case
name|SECOND
case|:
case|case
name|MILLISECOND
case|:
case|case
name|MICROSECOND
case|:
specifier|final
name|String
name|timeUnit
init|=
name|qualifier
operator|.
name|timeUnitRange
operator|.
name|startUnit
operator|.
name|name
argument_list|()
decl_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|timeUnit
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported type: "
operator|+
name|qualifier
operator|.
name|timeUnitRange
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|!=
name|qualifier
operator|.
name|timeUnitRange
operator|.
name|endUnit
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"End unit is not supported now: "
operator|+
name|qualifier
operator|.
name|timeUnitRange
operator|.
name|endUnit
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseSqlIntervalLiteral
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlIntervalLiteral
name|literal
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|unparseSqlIntervalLiteralMssql
argument_list|(
name|writer
argument_list|,
name|literal
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|unparseSqlIntervalLiteralMssql
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlIntervalLiteral
name|literal
parameter_list|,
name|int
name|sign
parameter_list|)
block|{
specifier|final
name|SqlIntervalLiteral
operator|.
name|IntervalValue
name|interval
init|=
operator|(
name|SqlIntervalLiteral
operator|.
name|IntervalValue
operator|)
name|literal
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|unparseSqlIntervalQualifier
argument_list|(
name|writer
argument_list|,
name|interval
operator|.
name|getIntervalQualifier
argument_list|()
argument_list|,
name|RelDataTypeSystem
operator|.
name|DEFAULT
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
if|if
condition|(
name|interval
operator|.
name|getSign
argument_list|()
operator|*
name|sign
operator|==
operator|-
literal|1
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|literal
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|unparseFloorWithUnit
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|charLen
parameter_list|,
name|String
name|offset
parameter_list|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"CONVERT"
argument_list|)
expr_stmt|;
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"DATETIME, CONVERT(VARCHAR("
operator|+
name|charLen
operator|+
literal|"), "
argument_list|)
expr_stmt|;
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
name|print
argument_list|(
literal|", 126)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"+'"
operator|+
name|offset
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MssqlSqlDialect.java
end_comment

end_unit

