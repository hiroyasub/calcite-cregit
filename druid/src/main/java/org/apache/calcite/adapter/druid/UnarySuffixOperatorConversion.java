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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
comment|/**  * Unary suffix operator conversion, used to convert function like: expression Unary_Operator  */
end_comment

begin_class
specifier|public
class|class
name|UnarySuffixOperatorConversion
implements|implements
name|DruidSqlOperatorConverter
block|{
specifier|private
specifier|final
name|SqlOperator
name|operator
decl_stmt|;
specifier|private
specifier|final
name|String
name|druidOperator
decl_stmt|;
specifier|public
name|UnarySuffixOperatorConversion
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|String
name|druidOperator
parameter_list|)
block|{
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|this
operator|.
name|druidOperator
operator|=
name|druidOperator
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperator
name|calciteOperator
parameter_list|()
block|{
return|return
name|operator
return|;
block|}
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
name|List
argument_list|<
name|String
argument_list|>
name|druidExpressions
init|=
name|DruidExpressions
operator|.
name|toDruidExpressions
argument_list|(
name|druidQuery
argument_list|,
name|rowType
argument_list|,
name|call
operator|.
name|getOperands
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|druidExpressions
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|DruidQuery
operator|.
name|format
argument_list|(
literal|"(%s %s)"
argument_list|,
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|druidExpressions
argument_list|)
argument_list|,
name|druidOperator
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End UnarySuffixOperatorConversion.java
end_comment

end_unit

