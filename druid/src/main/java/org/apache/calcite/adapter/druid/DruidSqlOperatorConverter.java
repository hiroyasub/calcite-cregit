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

begin_comment
comment|/**  * Defines how to convert a {@link RexNode} with a given Calcite SQL operator to  * a Druid expression.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DruidSqlOperatorConverter
block|{
comment|/**    * Returns the calcite SQL operator corresponding to Druid operator.    *    * @return operator    */
name|SqlOperator
name|calciteOperator
parameter_list|()
function_decl|;
comment|/**    * Translate rexNode to valid Druid expression.    * @param rexNode rexNode to translate to Druid expression    * @param rowType row type associated with rexNode    * @param druidQuery druid query used to figure out configs/fields related like timeZone    *    * @return valid Druid expression or null if it can not convert the rexNode    */
annotation|@
name|Nullable
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
function_decl|;
block|}
end_interface

end_unit

