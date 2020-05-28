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
name|sql
operator|.
name|JoinType
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
name|type
operator|.
name|ReturnTypes
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
name|RelToSqlConverterUtil
operator|.
name|unparseHiveTrim
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDialect</code> implementation for the APACHE SPARK database.  */
end_comment

begin_class
specifier|public
class|class
name|SparkSqlDialect
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
name|SPARK
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
name|SparkSqlDialect
argument_list|(
name|DEFAULT_CONTEXT
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlFunction
name|SPARKSQL_SUBSTRING
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
comment|/**    * Creates a SparkSqlDialect.    */
specifier|public
name|SparkSqlDialect
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
name|JoinType
name|emulateJoinTypeForCrossJoin
parameter_list|()
block|{
return|return
name|JoinType
operator|.
name|CROSS
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
name|supportsGroupByWithCube
parameter_list|()
block|{
return|return
literal|true
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
name|SPARKSQL_SUBSTRING
argument_list|,
name|writer
argument_list|,
name|call
argument_list|,
literal|false
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
case|case
name|TRIM
case|:
name|unparseHiveTrim
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

end_unit

