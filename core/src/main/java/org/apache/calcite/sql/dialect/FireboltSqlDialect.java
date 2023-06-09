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
name|SqlFloorFunction
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
name|util
operator|.
name|RelToSqlConverterUtil
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * A SqlDialect implementation for the Firebolt database.  */
end_comment

begin_class
specifier|public
class|class
name|FireboltSqlDialect
extends|extends
name|SqlDialect
block|{
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
name|DatabaseProduct
operator|.
name|FIREBOLT
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"\""
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
name|FireboltSqlDialect
argument_list|(
name|DEFAULT_CONTEXT
argument_list|)
decl_stmt|;
comment|/** Creates a FireboltSqlDialect. */
specifier|public
name|FireboltSqlDialect
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
block|}
comment|/** Reserved Keywords for Firebolt. */
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|RESERVED_KEYWORDS
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"ALL"
argument_list|,
literal|"ALTER"
argument_list|,
literal|"AND"
argument_list|,
literal|"ARRAY"
argument_list|,
literal|"BETWEEN"
argument_list|,
literal|"BIGINT"
argument_list|,
literal|"BOOL"
argument_list|,
literal|"BOOLEAN"
argument_list|,
literal|"BOTH"
argument_list|,
literal|"CASE"
argument_list|,
literal|"CAST"
argument_list|,
literal|"CHAR"
argument_list|,
literal|"CONCAT"
argument_list|,
literal|"COPY"
argument_list|,
literal|"CREATE"
argument_list|,
literal|"CROSS"
argument_list|,
literal|"CURRENT_DATE"
argument_list|,
literal|"CURRENT_TIMESTAMP"
argument_list|,
literal|"DATABASE"
argument_list|,
literal|"DATE"
argument_list|,
literal|"DATETIME"
argument_list|,
literal|"DECIMAL"
argument_list|,
literal|"DELETE"
argument_list|,
literal|"DESCRIBE"
argument_list|,
literal|"DISTINCT"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|"DOUBLECOLON"
argument_list|,
literal|"DOW"
argument_list|,
literal|"DOY"
argument_list|,
literal|"DROP"
argument_list|,
literal|"EMPTY_IDENTIFIER"
argument_list|,
literal|"EPOCH"
argument_list|,
literal|"EXCEPT"
argument_list|,
literal|"EXECUTE"
argument_list|,
literal|"EXISTS"
argument_list|,
literal|"EXPLAIN"
argument_list|,
literal|"EXTRACT"
argument_list|,
literal|"FALSE"
argument_list|,
literal|"FETCH"
argument_list|,
literal|"FIRST"
argument_list|,
literal|"FLOAT"
argument_list|,
literal|"FROM"
argument_list|,
literal|"FULL"
argument_list|,
literal|"GENERATE"
argument_list|,
literal|"GROUP"
argument_list|,
literal|"HAVING"
argument_list|,
literal|"IF"
argument_list|,
literal|"ILIKE"
argument_list|,
literal|"IN"
argument_list|,
literal|"INNER"
argument_list|,
literal|"INSERT"
argument_list|,
literal|"INT"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"INTERSECT"
argument_list|,
literal|"INTERVAL"
argument_list|,
literal|"IS"
argument_list|,
literal|"ISNULL"
argument_list|,
literal|"JOIN"
argument_list|,
literal|"JOIN_TYPE"
argument_list|,
literal|"LEADING"
argument_list|,
literal|"LEFT"
argument_list|,
literal|"LIKE"
argument_list|,
literal|"LIMIT"
argument_list|,
literal|"LIMIT_DISTINCT"
argument_list|,
literal|"LOCALTIMESTAMP"
argument_list|,
literal|"LONG"
argument_list|,
literal|"NATURAL"
argument_list|,
literal|"NEXT"
argument_list|,
literal|"NOT"
argument_list|,
literal|"NULL"
argument_list|,
literal|"NUMERIC"
argument_list|,
literal|"OFFSET"
argument_list|,
literal|"ON"
argument_list|,
literal|"ONLY"
argument_list|,
literal|"OR"
argument_list|,
literal|"ORDER"
argument_list|,
literal|"OUTER"
argument_list|,
literal|"OVER"
argument_list|,
literal|"PARTITION"
argument_list|,
literal|"PRECISION"
argument_list|,
literal|"PREPARE"
argument_list|,
literal|"PRIMARY"
argument_list|,
literal|"QUARTER"
argument_list|,
literal|"RIGHT"
argument_list|,
literal|"ROW"
argument_list|,
literal|"ROWS"
argument_list|,
literal|"SAMPLE"
argument_list|,
literal|"SELECT"
argument_list|,
literal|"SET"
argument_list|,
literal|"SHOW"
argument_list|,
literal|"TEXT"
argument_list|,
literal|"TIME"
argument_list|,
literal|"TIMESTAMP"
argument_list|,
literal|"TOP"
argument_list|,
literal|"TRAILING"
argument_list|,
literal|"TRIM"
argument_list|,
literal|"TRUE"
argument_list|,
literal|"TRUNCATE"
argument_list|,
literal|"UNION"
argument_list|,
literal|"UNKNOWN_CHAR"
argument_list|,
literal|"UNNEST"
argument_list|,
literal|"UNTERMINATED_STRING"
argument_list|,
literal|"UPDATE"
argument_list|,
literal|"USING"
argument_list|,
literal|"VARCHAR"
argument_list|,
literal|"WEEK"
argument_list|,
literal|"WHEN"
argument_list|,
literal|"WHERE"
argument_list|,
literal|"WITH"
argument_list|)
argument_list|)
decl_stmt|;
comment|/** An unquoted Firebolt identifier must start with a letter and be followed    * by zero or more letters, digits or _. */
specifier|private
specifier|static
specifier|final
name|Pattern
name|IDENTIFIER_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[A-Za-z][A-Za-z0-9_]*"
argument_list|)
decl_stmt|;
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
name|void
name|unparseOffsetFetch
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
annotation|@
name|Nullable
name|SqlNode
name|offset
parameter_list|,
annotation|@
name|Nullable
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
name|ANY_VALUE
case|:
case|case
name|AVG
case|:
case|case
name|COUNT
case|:
case|case
name|MAX
case|:
case|case
name|MIN
case|:
case|case
name|STDDEV_SAMP
case|:
case|case
name|SUM
case|:
return|return
literal|true
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
specifier|protected
name|boolean
name|identifierNeedsQuote
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|IDENTIFIER_REGEX
operator|.
name|matcher
argument_list|(
name|val
argument_list|)
operator|.
name|matches
argument_list|()
operator|||
name|RESERVED_KEYWORDS
operator|.
name|contains
argument_list|(
name|val
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlNode
name|getCastSpec
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|String
name|castSpec
decl_stmt|;
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|TINYINT
case|:
case|case
name|SMALLINT
case|:
comment|// Firebolt has no tinyint or smallint, so instead cast to INT
comment|// fall through
name|castSpec
operator|=
literal|"INT"
expr_stmt|;
break|break;
case|case
name|TIME
case|:
case|case
name|TIME_WITH_LOCAL_TIME_ZONE
case|:
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
comment|// Firebolt has no TIME, TimeWithLocalTimezone and TimestampWithLocalTimezone
comment|// so instead cast all those to TIMESTAMP
comment|// fall through
name|castSpec
operator|=
literal|"TIMESTAMP"
expr_stmt|;
break|break;
case|case
name|CHAR
case|:
comment|// Firebolt has no CHAR, so instead cast to VARCHAR
name|castSpec
operator|=
literal|"VARCHAR"
expr_stmt|;
break|break;
case|case
name|DECIMAL
case|:
comment|// Firebolt has no DECIMAL, so instead cast to FLOAT
name|castSpec
operator|=
literal|"FLOAT"
expr_stmt|;
break|break;
case|case
name|REAL
case|:
comment|// Firebolt has no REAL, so instead cast to DOUBLE
name|castSpec
operator|=
literal|"DOUBLE"
expr_stmt|;
break|break;
default|default:
return|return
name|super
operator|.
name|getCastSpec
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|// returning a type specification representing a type.
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlAlienSystemTypeNameSpec
argument_list|(
name|castSpec
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
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsAggregateFunctionFilter
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
name|supportsFunction
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|RelDataType
name|type
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|)
block|{
switch|switch
condition|(
name|operator
operator|.
name|kind
condition|)
block|{
case|case
name|LIKE
case|:
comment|// introduces support for ILIKE as well
case|case
name|EXISTS
case|:
comment|// introduces support for EXISTS as well
return|return
literal|true
return|;
default|default:
return|return
name|super
operator|.
name|supportsFunction
argument_list|(
name|operator
argument_list|,
name|type
argument_list|,
name|paramTypes
argument_list|)
return|;
block|}
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
name|RelToSqlConverterUtil
operator|.
name|specialOperatorByName
argument_list|(
literal|"SUBSTR"
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|0
argument_list|,
literal|0
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
specifier|final
name|SqlLiteral
name|timeUnitNode
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|TimeUnitRange
name|timeUnit
init|=
name|timeUnitNode
operator|.
name|getValueAs
argument_list|(
name|TimeUnitRange
operator|.
name|class
argument_list|)
decl_stmt|;
name|SqlCall
name|call2
init|=
name|SqlFloorFunction
operator|.
name|replaceTimeUnitOperand
argument_list|(
name|call
argument_list|,
name|timeUnit
operator|.
name|name
argument_list|()
argument_list|,
name|timeUnitNode
operator|.
name|getParserPosition
argument_list|()
argument_list|)
decl_stmt|;
name|SqlFloorFunction
operator|.
name|unparseDatetimeFunction
argument_list|(
name|writer
argument_list|,
name|call2
argument_list|,
literal|"DATE_TRUNC"
argument_list|,
literal|false
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
comment|/** Firebolt interval syntax: sign INTERVAL int64 time_unit. */
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
name|SqlIntervalLiteral
operator|.
name|IntervalValue
name|interval
init|=
name|literal
operator|.
name|getValueAs
argument_list|(
name|SqlIntervalLiteral
operator|.
name|IntervalValue
operator|.
name|class
argument_list|)
decl_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"INTERVAL"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
try|try
block|{
name|Long
operator|.
name|parseLong
argument_list|(
name|interval
operator|.
name|getIntervalLiteral
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Only INT64 is supported as the interval value for Firebolt."
argument_list|)
throw|;
block|}
name|writer
operator|.
name|literal
argument_list|(
name|interval
operator|.
name|getIntervalLiteral
argument_list|()
argument_list|)
expr_stmt|;
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
name|print
argument_list|(
literal|"'"
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Range time unit is not supported for Firebolt."
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
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
name|MILLISECOND
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
name|YEAR
case|:
case|case
name|DECADE
case|:
case|case
name|CENTURY
case|:
case|case
name|MILLENNIUM
case|:
return|return
name|timeUnit
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Time unit "
operator|+
name|timeUnit
operator|+
literal|" is not supported for Firebolt."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

