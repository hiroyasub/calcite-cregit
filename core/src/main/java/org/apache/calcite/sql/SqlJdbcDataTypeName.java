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
name|SqlTypeName
import|;
end_import

begin_comment
comment|/**  * Defines the name of the types which can occur as a type argument  * in a JDBC<code>{fn CONVERT(value, type)}</code> function.  * (This function has similar functionality to {@code CAST}, and is not to be  * confused with the SQL standard  * {@link org.apache.calcite.sql.fun.SqlConvertFunction CONVERT} function.)  *  * @see SqlJdbcFunctionCall  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlJdbcDataTypeName
block|{
name|SQL_CHAR
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
block|,
name|SQL_VARCHAR
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
block|,
name|SQL_DATE
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
block|,
name|SQL_TIME
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
block|,
name|SQL_TIMESTAMP
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
block|,
name|SQL_DECIMAL
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
block|,
name|SQL_NUMERIC
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
block|,
name|SQL_BOOLEAN
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
block|,
name|SQL_INTEGER
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
block|,
name|SQL_BINARY
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|)
block|,
name|SQL_VARBINARY
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
block|,
name|SQL_TINYINT
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|)
block|,
name|SQL_SMALLINT
argument_list|(
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|)
block|,
name|SQL_BIGINT
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
block|,
name|SQL_REAL
argument_list|(
name|SqlTypeName
operator|.
name|REAL
argument_list|)
block|,
name|SQL_DOUBLE
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
block|,
name|SQL_FLOAT
argument_list|(
name|SqlTypeName
operator|.
name|FLOAT
argument_list|)
block|,
name|SQL_INTERVAL_YEAR
argument_list|(
name|TimeUnitRange
operator|.
name|YEAR
argument_list|)
block|,
name|SQL_INTERVAL_YEAR_TO_MONTH
argument_list|(
name|TimeUnitRange
operator|.
name|YEAR_TO_MONTH
argument_list|)
block|,
name|SQL_INTERVAL_MONTH
argument_list|(
name|TimeUnitRange
operator|.
name|MONTH
argument_list|)
block|,
name|SQL_INTERVAL_DAY
argument_list|(
name|TimeUnitRange
operator|.
name|DAY
argument_list|)
block|,
name|SQL_INTERVAL_DAY_TO_HOUR
argument_list|(
name|TimeUnitRange
operator|.
name|DAY_TO_HOUR
argument_list|)
block|,
name|SQL_INTERVAL_DAY_TO_MINUTE
argument_list|(
name|TimeUnitRange
operator|.
name|DAY_TO_MINUTE
argument_list|)
block|,
name|SQL_INTERVAL_DAY_TO_SECOND
argument_list|(
name|TimeUnitRange
operator|.
name|DAY_TO_SECOND
argument_list|)
block|,
name|SQL_INTERVAL_HOUR
argument_list|(
name|TimeUnitRange
operator|.
name|HOUR
argument_list|)
block|,
name|SQL_INTERVAL_HOUR_TO_MINUTE
argument_list|(
name|TimeUnitRange
operator|.
name|HOUR_TO_MINUTE
argument_list|)
block|,
name|SQL_INTERVAL_HOUR_TO_SECOND
argument_list|(
name|TimeUnitRange
operator|.
name|HOUR_TO_SECOND
argument_list|)
block|,
name|SQL_INTERVAL_MINUTE
argument_list|(
name|TimeUnitRange
operator|.
name|MINUTE
argument_list|)
block|,
name|SQL_INTERVAL_MINUTE_TO_SECOND
argument_list|(
name|TimeUnitRange
operator|.
name|MINUTE_TO_SECOND
argument_list|)
block|,
name|SQL_INTERVAL_SECOND
argument_list|(
name|TimeUnitRange
operator|.
name|SECOND
argument_list|)
block|;
specifier|private
specifier|final
name|TimeUnitRange
name|range
decl_stmt|;
specifier|private
specifier|final
name|SqlTypeName
name|typeName
decl_stmt|;
name|SqlJdbcDataTypeName
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
name|this
argument_list|(
name|typeName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|SqlJdbcDataTypeName
parameter_list|(
name|TimeUnitRange
name|range
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|range
argument_list|)
expr_stmt|;
block|}
name|SqlJdbcDataTypeName
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|TimeUnitRange
name|range
parameter_list|)
block|{
assert|assert
operator|(
name|typeName
operator|==
literal|null
operator|)
operator|!=
operator|(
name|range
operator|==
literal|null
operator|)
assert|;
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|range
operator|=
name|range
expr_stmt|;
block|}
comment|/**    * Creates a parse-tree node representing an occurrence of this keyword    * at a particular position in the parsed text.    */
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
comment|/** Creates a parse tree node for a type identifier of this name. */
specifier|public
name|SqlNode
name|createDataType
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
if|if
condition|(
name|typeName
operator|!=
literal|null
condition|)
block|{
assert|assert
name|range
operator|==
literal|null
assert|;
specifier|final
name|SqlIdentifier
name|id
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|typeName
operator|.
name|name
argument_list|()
argument_list|,
name|pos
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
name|id
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|range
operator|!=
literal|null
assert|;
return|return
operator|new
name|SqlIntervalQualifier
argument_list|(
name|range
operator|.
name|startUnit
argument_list|,
name|range
operator|.
name|endUnit
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
block|}
end_enum

begin_comment
comment|// End SqlJdbcDataTypeName.java
end_comment

end_unit

