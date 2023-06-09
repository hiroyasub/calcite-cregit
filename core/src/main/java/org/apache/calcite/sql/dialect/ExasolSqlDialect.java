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
comment|/**  * A<code>SqlDialect</code> implementation for the Exasol database.  */
end_comment

begin_class
specifier|public
class|class
name|ExasolSqlDialect
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
name|EXASOL
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"\""
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|DEFAULT
init|=
operator|new
name|ExasolSqlDialect
argument_list|(
name|DEFAULT_CONTEXT
argument_list|)
decl_stmt|;
comment|/** An unquoted Exasol identifier must start with a letter and be followed    * by zero or more letters, digits or _. */
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
name|of
argument_list|(
literal|"ABS"
argument_list|,
literal|"ACCESS"
argument_list|,
literal|"ACOS"
argument_list|,
literal|"ADAPTER"
argument_list|,
literal|"ADD_DAYS"
argument_list|,
literal|"ADD_HOURS"
argument_list|,
literal|"ADD_MINUTES"
argument_list|,
literal|"ADD_MONTHS"
argument_list|,
literal|"ADD_SECONDS"
argument_list|,
literal|"ADD_WEEKS"
argument_list|,
literal|"ADD_YEARS"
argument_list|,
literal|"ADMIN"
argument_list|,
literal|"ALIGN"
argument_list|,
literal|"ALWAYS"
argument_list|,
literal|"ANALYZE"
argument_list|,
literal|"ANSI"
argument_list|,
literal|"APPROXIMATE_COUNT_DISTINCT"
argument_list|,
literal|"ASCII"
argument_list|,
literal|"ASIN"
argument_list|,
literal|"ASSIGNMENT"
argument_list|,
literal|"ASYMMETRIC"
argument_list|,
literal|"ATAN"
argument_list|,
literal|"ATAN2"
argument_list|,
literal|"ATOMIC"
argument_list|,
literal|"ATTEMPTS"
argument_list|,
literal|"AUDIT"
argument_list|,
literal|"AUTHENTICATED"
argument_list|,
literal|"AUTO"
argument_list|,
literal|"AVG"
argument_list|,
literal|"BACKUP"
argument_list|,
literal|"BERNOULLI"
argument_list|,
literal|"BIT_AND"
argument_list|,
literal|"BIT_CHECK"
argument_list|,
literal|"BIT_LENGTH"
argument_list|,
literal|"BIT_LROTATE"
argument_list|,
literal|"BIT_LSHIFT"
argument_list|,
literal|"BIT_NOT"
argument_list|,
literal|"BIT_OR"
argument_list|,
literal|"BIT_RROTATE"
argument_list|,
literal|"BIT_RSHIFT"
argument_list|,
literal|"BIT_SET"
argument_list|,
literal|"BIT_TO_NUM"
argument_list|,
literal|"BIT_XOR"
argument_list|,
literal|"BREADTH"
argument_list|,
literal|"CEIL"
argument_list|,
literal|"CEILING"
argument_list|,
literal|"CHANGE"
argument_list|,
literal|"CHARACTERS"
argument_list|,
literal|"CHARACTER_LENGTH"
argument_list|,
literal|"CHR"
argument_list|,
literal|"CLEAR"
argument_list|,
literal|"COBOL"
argument_list|,
literal|"COLOGNE_PHONETIC"
argument_list|,
literal|"COMMENT"
argument_list|,
literal|"COMMENTS"
argument_list|,
literal|"COMMITTED"
argument_list|,
literal|"CONCAT"
argument_list|,
literal|"CONNECT"
argument_list|,
literal|"CONVERT_TZ"
argument_list|,
literal|"CORR"
argument_list|,
literal|"COS"
argument_list|,
literal|"COSH"
argument_list|,
literal|"COT"
argument_list|,
literal|"COUNT"
argument_list|,
literal|"COVAR_POP"
argument_list|,
literal|"COVAR_SAMP"
argument_list|,
literal|"CREATED"
argument_list|,
literal|"CROSS"
argument_list|,
literal|"CURDATE"
argument_list|,
literal|"DATABASE"
argument_list|,
literal|"DATE_TRUNC"
argument_list|,
literal|"DAYS_BETWEEN"
argument_list|,
literal|"DECODE"
argument_list|,
literal|"DEFAULTS"
argument_list|,
literal|"DEFAULT_PRIORITY_GROUP"
argument_list|,
literal|"DEGREES"
argument_list|,
literal|"DELIMIT"
argument_list|,
literal|"DELIMITER"
argument_list|,
literal|"DENSE_RANK"
argument_list|,
literal|"DEPTH"
argument_list|,
literal|"DIAGNOSTICS"
argument_list|,
literal|"DICTIONARY"
argument_list|,
literal|"DISTRIBUTE"
argument_list|,
literal|"DISTRIBUTION"
argument_list|,
literal|"DIV"
argument_list|,
literal|"DOWN"
argument_list|,
literal|"DUMP"
argument_list|,
literal|"EDIT_DISTANCE"
argument_list|,
literal|"ENCODING"
argument_list|,
literal|"ESTIMATE"
argument_list|,
literal|"EVALUATE"
argument_list|,
literal|"EVERY"
argument_list|,
literal|"EXA"
argument_list|,
literal|"EXCLUDE"
argument_list|,
literal|"EXCLUDING"
argument_list|,
literal|"EXP"
argument_list|,
literal|"EXPIRE"
argument_list|,
literal|"EXPLAIN"
argument_list|,
literal|"EXPRESSION"
argument_list|,
literal|"FAILED"
argument_list|,
literal|"FIRST_VALUE"
argument_list|,
literal|"FLOOR"
argument_list|,
literal|"FLUSH"
argument_list|,
literal|"FOREIGN"
argument_list|,
literal|"FORTRAN"
argument_list|,
literal|"FROM_POSIX_TIME"
argument_list|,
literal|"GREATEST"
argument_list|,
literal|"GROUPING_ID"
argument_list|,
literal|"HANDLER"
argument_list|,
literal|"HAS"
argument_list|,
literal|"HASH"
argument_list|,
literal|"HASH_MD5"
argument_list|,
literal|"HASH_SHA"
argument_list|,
literal|"HASH_SHA1"
argument_list|,
literal|"HASH_SHA256"
argument_list|,
literal|"HASH_SHA512"
argument_list|,
literal|"HASH_TIGER"
argument_list|,
literal|"HIERARCHY"
argument_list|,
literal|"HOURS_BETWEEN"
argument_list|,
literal|"IDENTIFIED"
argument_list|,
literal|"IGNORE"
argument_list|,
literal|"IMPERSONATION"
argument_list|,
literal|"INCLUDING"
argument_list|,
literal|"INITCAP"
argument_list|,
literal|"INITIALLY"
argument_list|,
literal|"INSTR"
argument_list|,
literal|"INVALID"
argument_list|,
literal|"IPROC"
argument_list|,
literal|"ISOLATION"
argument_list|,
literal|"IS_BOOLEAN"
argument_list|,
literal|"IS_DATE"
argument_list|,
literal|"IS_DSINTERVAL"
argument_list|,
literal|"IS_NUMBER"
argument_list|,
literal|"IS_TIMESTAMP"
argument_list|,
literal|"IS_YMINTERVAL"
argument_list|,
literal|"JAVA"
argument_list|,
literal|"JAVASCRIPT"
argument_list|,
literal|"KEEP"
argument_list|,
literal|"KEY"
argument_list|,
literal|"KEYS"
argument_list|,
literal|"KILL"
argument_list|,
literal|"LAG"
argument_list|,
literal|"LANGUAGE"
argument_list|,
literal|"LAST_VALUE"
argument_list|,
literal|"LCASE"
argument_list|,
literal|"LEAD"
argument_list|,
literal|"LEAST"
argument_list|,
literal|"LENGTH"
argument_list|,
literal|"LINK"
argument_list|,
literal|"LN"
argument_list|,
literal|"LOCATE"
argument_list|,
literal|"LOCK"
argument_list|,
literal|"LOG10"
argument_list|,
literal|"LOG2"
argument_list|,
literal|"LOGIN"
argument_list|,
literal|"LOGS"
argument_list|,
literal|"LONG"
argument_list|,
literal|"LOWER"
argument_list|,
literal|"LPAD"
argument_list|,
literal|"LTRIM"
argument_list|,
literal|"LUA"
argument_list|,
literal|"MANAGE"
argument_list|,
literal|"MAX"
argument_list|,
literal|"MAXIMAL"
argument_list|,
literal|"MEDIAN"
argument_list|,
literal|"MID"
argument_list|,
literal|"MIN"
argument_list|,
literal|"MINUTES_BETWEEN"
argument_list|,
literal|"MONTHS_BETWEEN"
argument_list|,
literal|"MUMPS"
argument_list|,
literal|"NEVER"
argument_list|,
literal|"NICE"
argument_list|,
literal|"NORMALIZED"
argument_list|,
literal|"NOW"
argument_list|,
literal|"NPROC"
argument_list|,
literal|"NULLIFZERO"
argument_list|,
literal|"NULLS"
argument_list|,
literal|"NUMTODSINTERVAL"
argument_list|,
literal|"NUMTOYMINTERVAL"
argument_list|,
literal|"NVL"
argument_list|,
literal|"NVL2"
argument_list|,
literal|"OCTETS"
argument_list|,
literal|"OCTET_LENGTH"
argument_list|,
literal|"OFFSET"
argument_list|,
literal|"OPTIMIZE"
argument_list|,
literal|"ORA"
argument_list|,
literal|"OWNER"
argument_list|,
literal|"PADDING"
argument_list|,
literal|"PARTITION"
argument_list|,
literal|"PASCAL"
argument_list|,
literal|"PASSWORD"
argument_list|,
literal|"PASSWORD_EXPIRY_POLICY"
argument_list|,
literal|"PASSWORD_SECURITY_POLICY"
argument_list|,
literal|"PERCENTILE_CONT"
argument_list|,
literal|"PERCENTILE_DISC"
argument_list|,
literal|"PI"
argument_list|,
literal|"PLI"
argument_list|,
literal|"POSIX_TIME"
argument_list|,
literal|"POWER"
argument_list|,
literal|"PRECISION"
argument_list|,
literal|"PRELOAD"
argument_list|,
literal|"PRIMARY"
argument_list|,
literal|"PRIORITY"
argument_list|,
literal|"PRIVILEGE"
argument_list|,
literal|"PYTHON"
argument_list|,
literal|"QUERY_CACHE"
argument_list|,
literal|"QUERY_TIMEOUT"
argument_list|,
literal|"R"
argument_list|,
literal|"RADIANS"
argument_list|,
literal|"RAND"
argument_list|,
literal|"RANK"
argument_list|,
literal|"RATIO_TO_REPORT"
argument_list|,
literal|"RAW_SIZE_LIMIT"
argument_list|,
literal|"RECOMPRESS"
argument_list|,
literal|"RECORD"
argument_list|,
literal|"REGEXP_INSTR"
argument_list|,
literal|"REGEXP_REPLACE"
argument_list|,
literal|"REGEXP_SUBSTR"
argument_list|,
literal|"REGR_AVGX"
argument_list|,
literal|"REGR_AVGY"
argument_list|,
literal|"REGR_COUNT"
argument_list|,
literal|"REGR_INTERCEPT"
argument_list|,
literal|"REGR_R2"
argument_list|,
literal|"REGR_SLOPE"
argument_list|,
literal|"REGR_SXX"
argument_list|,
literal|"REGR_SXY"
argument_list|,
literal|"REGR_SYY"
argument_list|,
literal|"REJECT"
argument_list|,
literal|"REORGANIZE"
argument_list|,
literal|"REPEATABLE"
argument_list|,
literal|"RESET"
argument_list|,
literal|"REVERSE"
argument_list|,
literal|"ROLE"
argument_list|,
literal|"ROUND"
argument_list|,
literal|"ROWID"
argument_list|,
literal|"ROW_NUMBER"
argument_list|,
literal|"RPAD"
argument_list|,
literal|"RTRIM"
argument_list|,
literal|"SCALAR"
argument_list|,
literal|"SCHEMAS"
argument_list|,
literal|"SCHEME"
argument_list|,
literal|"SCRIPT_LANGUAGES"
argument_list|,
literal|"SCRIPT_OUTPUT_ADDRESS"
argument_list|,
literal|"SECONDS_BETWEEN"
argument_list|,
literal|"SECURE"
argument_list|,
literal|"SERIALIZABLE"
argument_list|,
literal|"SHUT"
argument_list|,
literal|"SIGN"
argument_list|,
literal|"SIMPLE"
argument_list|,
literal|"SIN"
argument_list|,
literal|"SINH"
argument_list|,
literal|"SIZE"
argument_list|,
literal|"SKIP"
argument_list|,
literal|"SOUNDEX"
argument_list|,
literal|"SQRT"
argument_list|,
literal|"STATISTICS"
argument_list|,
literal|"STDDEV"
argument_list|,
literal|"STDDEV_POP"
argument_list|,
literal|"STDDEV_SAMP"
argument_list|,
literal|"ST_AREA"
argument_list|,
literal|"ST_BOUNDARY"
argument_list|,
literal|"ST_BUFFER"
argument_list|,
literal|"ST_CENTROID"
argument_list|,
literal|"ST_CONTAINS"
argument_list|,
literal|"ST_CONVEXHULL"
argument_list|,
literal|"ST_CROSSES"
argument_list|,
literal|"ST_DIFFERENCE"
argument_list|,
literal|"ST_DIMENSION"
argument_list|,
literal|"ST_DISJOINT"
argument_list|,
literal|"ST_DISTANCE"
argument_list|,
literal|"ST_ENDPOINT"
argument_list|,
literal|"ST_ENVELOPE"
argument_list|,
literal|"ST_EQUALS"
argument_list|,
literal|"ST_EXTERIORRING"
argument_list|,
literal|"ST_FORCE2D"
argument_list|,
literal|"ST_GEOMETRYN"
argument_list|,
literal|"ST_GEOMETRYTYPE"
argument_list|,
literal|"ST_INTERIORRINGN"
argument_list|,
literal|"ST_INTERSECTION"
argument_list|,
literal|"ST_INTERSECTS"
argument_list|,
literal|"ST_ISCLOSED"
argument_list|,
literal|"ST_ISEMPTY"
argument_list|,
literal|"ST_ISRING"
argument_list|,
literal|"ST_ISSIMPLE"
argument_list|,
literal|"ST_LENGTH"
argument_list|,
literal|"ST_NUMGEOMETRIES"
argument_list|,
literal|"ST_NUMINTERIORRINGS"
argument_list|,
literal|"ST_NUMPOINTS"
argument_list|,
literal|"ST_OVERLAPS"
argument_list|,
literal|"ST_POINTN"
argument_list|,
literal|"ST_SETSRID"
argument_list|,
literal|"ST_STARTPOINT"
argument_list|,
literal|"ST_SYMDIFFERENCE"
argument_list|,
literal|"ST_TOUCHES"
argument_list|,
literal|"ST_TRANSFORM"
argument_list|,
literal|"ST_UNION"
argument_list|,
literal|"ST_WITHIN"
argument_list|,
literal|"ST_X"
argument_list|,
literal|"ST_Y"
argument_list|,
literal|"SUBSTR"
argument_list|,
literal|"SUM"
argument_list|,
literal|"SYMMETRIC"
argument_list|,
literal|"SYS_CONNECT_BY_PATH"
argument_list|,
literal|"SYS_GUID"
argument_list|,
literal|"TABLES"
argument_list|,
literal|"TABLESAMPLE"
argument_list|,
literal|"TAN"
argument_list|,
literal|"TANH"
argument_list|,
literal|"TASKS"
argument_list|,
literal|"TIES"
argument_list|,
literal|"TIMESTAMP_ARITHMETIC_BEHAVIOR"
argument_list|,
literal|"TIME_ZONE"
argument_list|,
literal|"TIME_ZONE_BEHAVIOR"
argument_list|,
literal|"TO_CHAR"
argument_list|,
literal|"TO_DATE"
argument_list|,
literal|"TO_DSINTERVAL"
argument_list|,
literal|"TO_NUMBER"
argument_list|,
literal|"TO_TIMESTAMP"
argument_list|,
literal|"TO_YMINTERVAL"
argument_list|,
literal|"TRANSLATE"
argument_list|,
literal|"TRUNC"
argument_list|,
literal|"TYPE"
argument_list|,
literal|"UCASE"
argument_list|,
literal|"UNBOUNDED"
argument_list|,
literal|"UNCOMMITTED"
argument_list|,
literal|"UNDO"
argument_list|,
literal|"UNICODE"
argument_list|,
literal|"UNICODECHR"
argument_list|,
literal|"UNLIMITED"
argument_list|,
literal|"UPPER"
argument_list|,
literal|"UTF8"
argument_list|,
literal|"VALUE2PROC"
argument_list|,
literal|"VARIANCE"
argument_list|,
literal|"VARYING"
argument_list|,
literal|"VAR_POP"
argument_list|,
literal|"VAR_SAMP"
argument_list|,
literal|"VIRTUAL"
argument_list|,
literal|"WEEK"
argument_list|,
literal|"WEIGHT"
argument_list|,
literal|"WRITE"
argument_list|,
literal|"YEARS_BETWEEN"
argument_list|,
literal|"ZEROIFNULL"
argument_list|)
decl_stmt|;
comment|/** Creates a ExasolSqlDialect. */
specifier|public
name|ExasolSqlDialect
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
name|AVG
case|:
case|case
name|COUNT
case|:
case|case
name|COVAR_POP
case|:
case|case
name|COVAR_SAMP
case|:
case|case
name|MAX
case|:
case|case
name|MIN
case|:
case|case
name|STDDEV_POP
case|:
case|case
name|STDDEV_SAMP
case|:
case|case
name|SUM
case|:
case|case
name|VAR_POP
case|:
case|case
name|VAR_SAMP
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
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
operator|!
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
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|TIMESTAMP
case|:
comment|// Exasol does not support TIMESTAMP with precision.
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlBasicTypeNameSpec
argument_list|(
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
return|return
name|super
operator|.
name|getCastSpec
argument_list|(
name|type
argument_list|)
return|;
block|}
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
comment|// Same as PostgreSQL implementation
name|PostgresqlSqlDialect
operator|.
name|DEFAULT
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
end_class

end_unit

