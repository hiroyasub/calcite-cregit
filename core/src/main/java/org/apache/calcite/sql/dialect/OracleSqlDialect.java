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
name|SqlDateLiteral
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
name|SqlTimeLiteral
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
name|SqlTimestampLiteral
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
name|SqlLibraryOperators
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
name|SqlTypeName
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
name|List
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDialect</code> implementation for the Oracle database.  */
end_comment

begin_class
specifier|public
class|class
name|OracleSqlDialect
extends|extends
name|SqlDialect
block|{
comment|/** OracleDB type system. */
specifier|private
specifier|static
specifier|final
name|RelDataTypeSystem
name|ORACLE_TYPE_SYSTEM
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
name|VARCHAR
case|:
comment|// Maximum size of 4000 bytes for varchar2.
return|return
literal|4000
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
name|DEFAULT
init|=
operator|new
name|OracleSqlDialect
argument_list|(
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|ORACLE
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"\""
argument_list|)
operator|.
name|withDataTypeSystem
argument_list|(
name|ORACLE_TYPE_SYSTEM
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Creates an OracleSqlDialect. */
specifier|public
name|OracleSqlDialect
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
name|supportsDataType
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
name|BOOLEAN
case|:
return|return
literal|false
return|;
default|default:
return|return
name|super
operator|.
name|supportsDataType
argument_list|(
name|type
argument_list|)
return|;
block|}
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
name|SMALLINT
case|:
name|castSpec
operator|=
literal|"_NUMBER(5)"
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
name|castSpec
operator|=
literal|"_NUMBER(10)"
expr_stmt|;
break|break;
case|case
name|BIGINT
case|:
name|castSpec
operator|=
literal|"_NUMBER(19)"
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|castSpec
operator|=
literal|"_DOUBLE PRECISION"
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
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|castSpec
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
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
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|allowsAs
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
name|supportsAliasedValues
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
if|if
condition|(
name|literal
operator|instanceof
name|SqlTimestampLiteral
condition|)
block|{
name|writer
operator|.
name|literal
argument_list|(
literal|"TO_TIMESTAMP('"
operator|+
name|literal
operator|.
name|toFormattedString
argument_list|()
operator|+
literal|"', 'YYYY-MM-DD HH24:MI:SS.FF')"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|literal
operator|instanceof
name|SqlDateLiteral
condition|)
block|{
name|writer
operator|.
name|literal
argument_list|(
literal|"TO_DATE('"
operator|+
name|literal
operator|.
name|toFormattedString
argument_list|()
operator|+
literal|"', 'YYYY-MM-DD')"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|literal
operator|instanceof
name|SqlTimeLiteral
condition|)
block|{
name|writer
operator|.
name|literal
argument_list|(
literal|"TO_TIME('"
operator|+
name|literal
operator|.
name|toFormattedString
argument_list|()
operator|+
literal|"', 'HH24:MI:SS.FF')"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|unparseDateTimeLiteral
argument_list|(
name|writer
argument_list|,
name|literal
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSingleRowTableName
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"DUAL"
argument_list|)
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
name|SqlUtil
operator|.
name|unparseFunctionSyntax
argument_list|(
name|SqlLibraryOperators
operator|.
name|SUBSTR
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
literal|"TRUNC"
argument_list|,
literal|true
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
block|}
end_class

begin_comment
comment|// End OracleSqlDialect.java
end_comment

end_unit

