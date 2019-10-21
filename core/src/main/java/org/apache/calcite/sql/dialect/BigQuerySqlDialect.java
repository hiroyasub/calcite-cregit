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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexUtil
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
name|SqlIdentifier
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
name|SqlSetOperator
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
name|SqlSyntax
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
name|SqlUserDefinedTypeNameSpec
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
name|BasicSqlType
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
comment|/**  * A<code>SqlDialect</code> implementation for Google BigQuery's "Standard SQL"  * dialect.  */
end_comment

begin_class
specifier|public
class|class
name|BigQuerySqlDialect
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
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|withLiteralQuoteString
argument_list|(
literal|"'"
argument_list|)
operator|.
name|withLiteralEscapedQuoteString
argument_list|(
literal|"\\'"
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"`"
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|NullCollation
operator|.
name|LOW
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|withQuotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|DEFAULT
init|=
operator|new
name|BigQuerySqlDialect
argument_list|(
name|DEFAULT_CONTEXT
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
name|copyOf
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"ALL"
argument_list|,
literal|"AND"
argument_list|,
literal|"ANY"
argument_list|,
literal|"ARRAY"
argument_list|,
literal|"AS"
argument_list|,
literal|"ASC"
argument_list|,
literal|"ASSERT_ROWS_MODIFIED"
argument_list|,
literal|"AT"
argument_list|,
literal|"BETWEEN"
argument_list|,
literal|"BY"
argument_list|,
literal|"CASE"
argument_list|,
literal|"CAST"
argument_list|,
literal|"COLLATE"
argument_list|,
literal|"CONTAINS"
argument_list|,
literal|"CREATE"
argument_list|,
literal|"CROSS"
argument_list|,
literal|"CUBE"
argument_list|,
literal|"CURRENT"
argument_list|,
literal|"DEFAULT"
argument_list|,
literal|"DEFINE"
argument_list|,
literal|"DESC"
argument_list|,
literal|"DISTINCT"
argument_list|,
literal|"ELSE"
argument_list|,
literal|"END"
argument_list|,
literal|"ENUM"
argument_list|,
literal|"ESCAPE"
argument_list|,
literal|"EXCEPT"
argument_list|,
literal|"EXCLUDE"
argument_list|,
literal|"EXISTS"
argument_list|,
literal|"EXTRACT"
argument_list|,
literal|"FALSE"
argument_list|,
literal|"FETCH"
argument_list|,
literal|"FOLLOWING"
argument_list|,
literal|"FOR"
argument_list|,
literal|"FROM"
argument_list|,
literal|"FULL"
argument_list|,
literal|"GROUP"
argument_list|,
literal|"GROUPING"
argument_list|,
literal|"GROUPS"
argument_list|,
literal|"HASH"
argument_list|,
literal|"HAVING"
argument_list|,
literal|"IF"
argument_list|,
literal|"IGNORE"
argument_list|,
literal|"IN"
argument_list|,
literal|"INNER"
argument_list|,
literal|"INTERSECT"
argument_list|,
literal|"INTERVAL"
argument_list|,
literal|"INTO"
argument_list|,
literal|"IS"
argument_list|,
literal|"JOIN"
argument_list|,
literal|"LATERAL"
argument_list|,
literal|"LEFT"
argument_list|,
literal|"LIKE"
argument_list|,
literal|"LIMIT"
argument_list|,
literal|"LOOKUP"
argument_list|,
literal|"MERGE"
argument_list|,
literal|"NATURAL"
argument_list|,
literal|"NEW"
argument_list|,
literal|"NO"
argument_list|,
literal|"NOT"
argument_list|,
literal|"NULL"
argument_list|,
literal|"NULLS"
argument_list|,
literal|"OF"
argument_list|,
literal|"ON"
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
literal|"PRECEDING"
argument_list|,
literal|"PROTO"
argument_list|,
literal|"RANGE"
argument_list|,
literal|"RECURSIVE"
argument_list|,
literal|"RESPECT"
argument_list|,
literal|"RIGHT"
argument_list|,
literal|"ROLLUP"
argument_list|,
literal|"ROWS"
argument_list|,
literal|"SELECT"
argument_list|,
literal|"SET"
argument_list|,
literal|"SOME"
argument_list|,
literal|"STRUCT"
argument_list|,
literal|"TABLESAMPLE"
argument_list|,
literal|"THEN"
argument_list|,
literal|"TO"
argument_list|,
literal|"TREAT"
argument_list|,
literal|"TRUE"
argument_list|,
literal|"UNBOUNDED"
argument_list|,
literal|"UNION"
argument_list|,
literal|"UNNEST"
argument_list|,
literal|"USING"
argument_list|,
literal|"WHEN"
argument_list|,
literal|"WHERE"
argument_list|,
literal|"WINDOW"
argument_list|,
literal|"WITH"
argument_list|,
literal|"WITHIN"
argument_list|)
argument_list|)
decl_stmt|;
comment|/** An unquoted BigQuery identifier must start with a letter and be followed    * by zero or more letters, digits or _. */
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
comment|/** Creates a BigQuerySqlDialect. */
specifier|public
name|BigQuerySqlDialect
parameter_list|(
name|SqlDialect
operator|.
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
name|String
name|quoteIdentifier
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|quoteIdentifier
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|,
name|val
argument_list|)
operator|.
name|toString
argument_list|()
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
name|supportsImplicitTypeCoercion
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
return|return
name|super
operator|.
name|supportsImplicitTypeCoercion
argument_list|(
name|call
argument_list|)
operator|&&
name|RexUtil
operator|.
name|isLiteral
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|false
argument_list|)
operator|&&
operator|!
name|SqlTypeUtil
operator|.
name|isNumeric
argument_list|(
name|call
operator|.
name|type
argument_list|)
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
name|void
name|unparseCall
parameter_list|(
specifier|final
name|SqlWriter
name|writer
parameter_list|,
specifier|final
name|SqlCall
name|call
parameter_list|,
specifier|final
name|int
name|leftPrec
parameter_list|,
specifier|final
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
name|POSITION
case|:
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
literal|"STRPOS"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|","
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
literal|","
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
if|if
condition|(
literal|3
operator|==
name|call
operator|.
name|operandCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"3rd operand Not Supported for Function STRPOS in Big Query"
argument_list|)
throw|;
block|}
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|UNION
case|:
if|if
condition|(
operator|!
operator|(
operator|(
name|SqlSetOperator
operator|)
name|call
operator|.
name|getOperator
argument_list|()
operator|)
operator|.
name|isAll
argument_list|()
condition|)
block|{
name|SqlSyntax
operator|.
name|BINARY
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|UNION_DISTINCT
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|EXCEPT
case|:
if|if
condition|(
operator|!
operator|(
operator|(
name|SqlSetOperator
operator|)
name|call
operator|.
name|getOperator
argument_list|()
operator|)
operator|.
name|isAll
argument_list|()
condition|)
block|{
name|SqlSyntax
operator|.
name|BINARY
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|EXCEPT_DISTINCT
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|INTERSECT
case|:
if|if
condition|(
operator|!
operator|(
operator|(
name|SqlSetOperator
operator|)
name|call
operator|.
name|getOperator
argument_list|()
operator|)
operator|.
name|isAll
argument_list|()
condition|)
block|{
name|SqlSyntax
operator|.
name|BINARY
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|INTERSECT_DISTINCT
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
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
comment|/** BigQuery data type reference:    *<a href="https://cloud.google.com/bigquery/docs/reference/standard-sql/data-types">    * Bigquery Standard SQL Data Types</a>    */
annotation|@
name|Override
specifier|public
name|SqlNode
name|getCastSpec
parameter_list|(
specifier|final
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|BasicSqlType
condition|)
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
name|BIGINT
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"INT64"
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"FLOAT64"
argument_list|)
return|;
case|case
name|DECIMAL
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"NUMERIC"
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"BOOL"
argument_list|)
return|;
case|case
name|VARCHAR
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"STRING"
argument_list|)
return|;
case|case
name|VARBINARY
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"BYTES"
argument_list|)
return|;
case|case
name|DATE
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"DATE"
argument_list|)
return|;
case|case
name|TIME
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"TIME"
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|createSqlDataTypeSpecByName
argument_list|(
literal|"TIMESTAMP"
argument_list|)
return|;
block|}
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
specifier|private
name|SqlDataTypeSpec
name|createSqlDataTypeSpecByName
parameter_list|(
name|String
name|identifierName
parameter_list|)
block|{
name|SqlUserDefinedTypeNameSpec
name|typeNameSpec
init|=
operator|new
name|SqlUserDefinedTypeNameSpec
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|identifierName
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
decl_stmt|;
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
name|typeNameSpec
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|/**    * List of BigQuery Specific Operators needed to form Syntactically Correct SQL.    */
specifier|private
specifier|static
specifier|final
name|SqlOperator
name|UNION_DISTINCT
init|=
operator|new
name|SqlSetOperator
argument_list|(
literal|"UNION DISTINCT"
argument_list|,
name|SqlKind
operator|.
name|UNION
argument_list|,
literal|14
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlSetOperator
name|EXCEPT_DISTINCT
init|=
operator|new
name|SqlSetOperator
argument_list|(
literal|"EXCEPT DISTINCT"
argument_list|,
name|SqlKind
operator|.
name|EXCEPT
argument_list|,
literal|14
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlSetOperator
name|INTERSECT_DISTINCT
init|=
operator|new
name|SqlSetOperator
argument_list|(
literal|"INTERSECT DISTINCT"
argument_list|,
name|SqlKind
operator|.
name|INTERSECT
argument_list|,
literal|18
argument_list|,
literal|false
argument_list|)
decl_stmt|;
block|}
end_class

begin_comment
comment|// End BigQuerySqlDialect.java
end_comment

end_unit

