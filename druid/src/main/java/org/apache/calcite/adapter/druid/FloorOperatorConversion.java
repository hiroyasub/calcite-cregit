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
name|adapter
operator|.
name|druid
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
name|RexNode
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
name|fun
operator|.
name|SqlStdOperatorTable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * DruidSqlOperatorConverter implementation that handles Floor operations conversions  */
end_comment

begin_class
specifier|public
class|class
name|FloorOperatorConversion
implements|implements
name|DruidSqlOperatorConverter
block|{
annotation|@
name|Override
specifier|public
name|SqlOperator
name|calciteOperator
parameter_list|()
block|{
return|return
name|SqlStdOperatorTable
operator|.
name|FLOOR
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|toDruidExpression
parameter_list|(
name|RexNode
name|rexNode
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|DruidQuery
name|druidQuery
parameter_list|)
block|{
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|rexNode
decl_stmt|;
specifier|final
name|RexNode
name|arg
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|druidExpression
init|=
name|DruidExpressions
operator|.
name|toDruidExpression
argument_list|(
name|arg
argument_list|,
name|rowType
argument_list|,
name|druidQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|druidExpression
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// case FLOOR(expr)
return|return
name|DruidQuery
operator|.
name|format
argument_list|(
literal|"floor(%s)"
argument_list|,
name|druidExpression
argument_list|)
return|;
block|}
if|else if
condition|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
comment|// FLOOR(expr TO timeUnit)
specifier|final
name|Granularity
name|granularity
init|=
name|DruidDateTimeUtils
operator|.
name|extractGranularity
argument_list|(
name|call
argument_list|,
name|druidQuery
operator|.
name|getConnectionConfig
argument_list|()
operator|.
name|timeZone
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|granularity
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|isoPeriodFormat
init|=
name|DruidDateTimeUtils
operator|.
name|toISOPeriodFormat
argument_list|(
name|granularity
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isoPeriodFormat
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|DruidExpressions
operator|.
name|applyTimestampFloor
argument_list|(
name|druidExpression
argument_list|,
name|isoPeriodFormat
argument_list|,
literal|""
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|druidQuery
operator|.
name|getConnectionConfig
argument_list|()
operator|.
name|timeZone
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End FloorOperatorConversion.java
end_comment

end_unit
