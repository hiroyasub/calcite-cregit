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
name|fun
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
name|sql
operator|.
name|SqlAggFunction
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
name|util
operator|.
name|Optionality
import|;
end_import

begin_comment
comment|/**  *<code>NTH_VALUE</code> windowed aggregate function  * returns the value of an expression evaluated at the {@code n}th row of the  * window frame.  */
end_comment

begin_class
specifier|public
class|class
name|SqlNthValueAggFunction
extends|extends
name|SqlAggFunction
block|{
specifier|public
name|SqlNthValueAggFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
literal|null
argument_list|,
name|kind
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_IF_EMPTY
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|ANY_NUMERIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Optionality
operator|.
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowsNullTreatment
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

