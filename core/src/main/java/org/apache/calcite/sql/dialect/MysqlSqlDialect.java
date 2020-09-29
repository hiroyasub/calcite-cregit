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
name|Casing
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
name|config
operator|.
name|NullCollation
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeSystemImpl
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
name|SqlAlienSystemTypeNameSpec
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
name|SqlBasicCall
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
name|SqlBasicTypeNameSpec
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
name|SqlDataTypeSpec
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
name|SqlNodeList
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
name|SqlSelect
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
name|SqlCase
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
name|InferTypes
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
name|SqlTypeName
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
name|type
operator|.
name|SqlTypeName
operator|.
name|TIMESTAMP
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDialect</code> implementation for the MySQL database.  */
end_comment

begin_class
specifier|public
class|class
name|MysqlSqlDialect
extends|extends
name|SqlDialect
block|{
comment|/** MySQL type system. */
specifier|public
specifier|static
specifier|final
name|RelDataTypeSystem
name|MYSQL_TYPE_SYSTEM
init|=
operator|new
name|RelDataTypeSystemImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getMaxPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|CHAR
case|:
return|return
literal|255
return|;
case|case
name|VARCHAR
case|:
return|return
literal|65535
return|;
case|case
name|TIMESTAMP
case|:
return|return
literal|6
return|;
default|default:
return|return
name|super
operator|.
name|getMaxPrecision
argument_list|(
name|typeName
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlDialect
operator|.
name|Context
name|DEFAULT_CONTEXT
init|=
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"`"
argument_list|)
operator|.
name|withDataTypeSystem
argument_list|(
name|MYSQL_TYPE_SYSTEM
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|NullCollation
operator|.
name|LOW
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|DEFAULT
init|=
operator|new
name|MysqlSqlDialect
argument_list|(
name|DEFAULT_CONTEXT
argument_list|)
decl_stmt|;
comment|/** MySQL specific function. */
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|ISNULL_FUNCTION
init|=
operator|new
name|SqlFunction
argument_list|(
literal|"ISNULL"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|BOOLEAN
argument_list|,
name|InferTypes
operator|.
name|FIRST_KNOWN
argument_list|,
name|OperandTypes
operator|.
name|ANY
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|majorVersion
decl_stmt|;
comment|/** Creates a MysqlSqlDialect. */
specifier|public
name|MysqlSqlDialect
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
name|majorVersion
operator|=
name|context
operator|.
name|databaseMajorVersion
argument_list|()
expr_stmt|;
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
annotation|@
name|Override
specifier|public
name|boolean
name|requiresAliasForFromItems
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsAliasedValues
parameter_list|()
block|{
comment|// MySQL supports VALUES only in INSERT; not in a FROM clause
return|return
literal|false
return|;
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
name|unparseFetchUsingLimit
argument_list|(
name|writer
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlNode
name|emulateNullDirection
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|boolean
name|nullsFirst
parameter_list|,
name|boolean
name|desc
parameter_list|)
block|{
return|return
name|emulateNullDirectionWithIsNull
argument_list|(
name|node
argument_list|,
name|nullsFirst
argument_list|,
name|desc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsAggregateFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|COUNT
case|:
case|case
name|SUM
case|:
case|case
name|SUM0
case|:
case|case
name|MIN
case|:
case|case
name|MAX
case|:
case|case
name|SINGLE_VALUE
case|:
return|return
literal|true
return|;
case|case
name|ROLLUP
case|:
comment|// MySQL 5 does not support standard "GROUP BY ROLLUP(x, y)",
comment|// only the non-standard "GROUP BY x, y WITH ROLLUP".
return|return
name|majorVersion
operator|>=
literal|8
return|;
default|default:
break|break;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsNestedAggregations
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsGroupByWithRollup
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|CalendarPolicy
name|getCalendarPolicy
parameter_list|()
block|{
return|return
name|CalendarPolicy
operator|.
name|SHIFT
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlNode
name|getCastSpec
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|VARCHAR
case|:
comment|// MySQL doesn't have a VARCHAR type, only CHAR.
name|int
name|vcMaxPrecision
init|=
name|this
operator|.
name|getTypeSystem
argument_list|()
operator|.
name|getMaxPrecision
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
decl_stmt|;
name|int
name|precision
init|=
name|type
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
if|if
condition|(
name|vcMaxPrecision
operator|>
literal|0
operator|&&
name|precision
operator|>
name|vcMaxPrecision
condition|)
block|{
name|precision
operator|=
name|vcMaxPrecision
expr_stmt|;
block|}
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlBasicTypeNameSpec
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|precision
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
case|case
name|INTEGER
case|:
case|case
name|BIGINT
case|:
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlAlienSystemTypeNameSpec
argument_list|(
literal|"SIGNED"
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlAlienSystemTypeNameSpec
argument_list|(
literal|"DATETIME"
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
default|default:
break|break;
block|}
return|return
name|super
operator|.
name|getCastSpec
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlNode
name|rewriteSingleValueExpr
parameter_list|(
name|SqlNode
name|aggCall
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
operator|(
operator|(
name|SqlBasicCall
operator|)
name|aggCall
operator|)
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlLiteral
name|nullLiteral
init|=
name|SqlLiteral
operator|.
name|createNull
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|unionOperand
init|=
operator|new
name|SqlSelect
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|SqlNodeList
operator|.
name|EMPTY
argument_list|,
name|SqlNodeList
operator|.
name|of
argument_list|(
name|nullLiteral
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlNodeList
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlNodeList
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
comment|// For MySQL, generate
comment|//   CASE COUNT(*)
comment|//   WHEN 0 THEN NULL
comment|//   WHEN 1 THEN<result>
comment|//   ELSE (SELECT NULL UNION ALL SELECT NULL)
comment|//   END
specifier|final
name|SqlNode
name|caseExpr
init|=
operator|new
name|SqlCase
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|SqlStdOperatorTable
operator|.
name|COUNT
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|operand
argument_list|)
argument_list|,
name|SqlNodeList
operator|.
name|of
argument_list|(
name|SqlLiteral
operator|.
name|createExactNumeric
argument_list|(
literal|"0"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlLiteral
operator|.
name|createExactNumeric
argument_list|(
literal|"1"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
argument_list|,
name|SqlNodeList
operator|.
name|of
argument_list|(
name|nullLiteral
argument_list|,
name|operand
argument_list|)
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SCALAR_QUERY
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|SqlStdOperatorTable
operator|.
name|UNION_ALL
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|unionOperand
argument_list|,
name|unionOperand
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"SINGLE_VALUE rewritten into [{}]"
argument_list|,
name|caseExpr
argument_list|)
expr_stmt|;
return|return
name|caseExpr
return|;
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
comment|/**    * Unparses datetime floor for MySQL. There is no TRUNC function, so simulate    * this using calls to DATE_FORMAT.    *    * @param writer Writer    * @param call Call    */
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
if|if
condition|(
name|unit
operator|==
name|TimeUnitRange
operator|.
name|WEEK
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"STR_TO_DATE"
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
literal|"DATE_FORMAT("
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
literal|", '%x%v-1'), '%x%v-%w'"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|format
decl_stmt|;
switch|switch
condition|(
name|unit
condition|)
block|{
case|case
name|YEAR
case|:
name|format
operator|=
literal|"%Y-01-01"
expr_stmt|;
break|break;
case|case
name|MONTH
case|:
name|format
operator|=
literal|"%Y-%m-01"
expr_stmt|;
break|break;
case|case
name|DAY
case|:
name|format
operator|=
literal|"%Y-%m-%d"
expr_stmt|;
break|break;
case|case
name|HOUR
case|:
name|format
operator|=
literal|"%Y-%m-%d %H:00:00"
expr_stmt|;
break|break;
case|case
name|MINUTE
case|:
name|format
operator|=
literal|"%Y-%m-%d %H:%i:00"
expr_stmt|;
break|break;
case|case
name|SECOND
case|:
name|format
operator|=
literal|"%Y-%m-%d %H:%i:%s"
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"MYSQL does not support FLOOR for time unit: "
operator|+
name|unit
argument_list|)
throw|;
block|}
name|writer
operator|.
name|print
argument_list|(
literal|"DATE_FORMAT"
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
name|writer
operator|.
name|print
argument_list|(
literal|"'"
operator|+
name|format
operator|+
literal|"'"
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
comment|//  Unit Value         | Expected Format
comment|// --------------------+-------------------------------------------
comment|//  MICROSECOND        | MICROSECONDS
comment|//  SECOND             | SECONDS
comment|//  MINUTE             | MINUTES
comment|//  HOUR               | HOURS
comment|//  DAY                | DAYS
comment|//  WEEK               | WEEKS
comment|//  MONTH              | MONTHS
comment|//  QUARTER            | QUARTERS
comment|//  YEAR               | YEARS
comment|//  MINUTE_SECOND      | 'MINUTES:SECONDS'
comment|//  HOUR_MINUTE        | 'HOURS:MINUTES'
comment|//  DAY_HOUR           | 'DAYS HOURS'
comment|//  YEAR_MONTH         | 'YEARS-MONTHS'
comment|//  MINUTE_MICROSECOND | 'MINUTES:SECONDS.MICROSECONDS'
comment|//  HOUR_MICROSECOND   | 'HOURS:MINUTES:SECONDS.MICROSECONDS'
comment|//  SECOND_MICROSECOND | 'SECONDS.MICROSECONDS'
comment|//  DAY_MINUTE         | 'DAYS HOURS:MINUTES'
comment|//  DAY_MICROSECOND    | 'DAYS HOURS:MINUTES:SECONDS.MICROSECONDS'
comment|//  DAY_SECOND         | 'DAYS HOURS:MINUTES:SECONDS'
comment|//  HOUR_SECOND        | 'HOURS:MINUTES:SECONDS'
if|if
condition|(
operator|!
name|qualifier
operator|.
name|useDefaultFractionalSecondPrecision
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Fractional second precision is not supported now "
argument_list|)
throw|;
block|}
specifier|final
name|String
name|start
init|=
name|validate
argument_list|(
name|qualifier
operator|.
name|timeUnitRange
operator|.
name|startUnit
argument_list|)
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|qualifier
operator|.
name|timeUnitRange
operator|.
name|startUnit
operator|==
name|TimeUnit
operator|.
name|SECOND
operator|||
name|qualifier
operator|.
name|timeUnitRange
operator|.
name|endUnit
operator|==
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|keyword
argument_list|(
name|start
operator|+
literal|"_"
operator|+
name|qualifier
operator|.
name|timeUnitRange
operator|.
name|endUnit
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|TimeUnit
name|validate
parameter_list|(
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
switch|switch
condition|(
name|timeUnit
condition|)
block|{
case|case
name|MICROSECOND
case|:
case|case
name|SECOND
case|:
case|case
name|MINUTE
case|:
case|case
name|HOUR
case|:
case|case
name|DAY
case|:
case|case
name|WEEK
case|:
case|case
name|MONTH
case|:
case|case
name|QUARTER
case|:
case|case
name|YEAR
case|:
return|return
name|timeUnit
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|" Time unit "
operator|+
name|timeUnit
operator|+
literal|"is not supported now."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

