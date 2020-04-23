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

begin_comment
comment|/**  * The {@code GROUPING} function. It accepts 1 or more arguments and they must be  * from the GROUP BY list. The result is calculated from a bitmap (the right most bit  * is the lowest), which indicates whether an argument is aggregated or grouped  * -- The N-th bit is lit if the N-th argument is aggregated.  *  *<p>Example: {@code GROUPING(deptno, gender)} returns  * 0 if both deptno and gender are being grouped,  * 1 if only deptno is being grouped,  * 2 if only gender is being grouped,  * 3 if neither deptno nor gender are being grouped.  *  *<p>This function is defined in the SQL standard.  * {@code GROUPING_ID} is a non-standard synonym.  *  *<p>Some examples are in {@code agg.iq}.  */
end_comment

begin_class
class|class
name|SqlGroupingFunction
extends|extends
name|SqlAbstractGroupFunction
block|{
name|SqlGroupingFunction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|GROUPING
argument_list|,
name|ReturnTypes
operator|.
name|BIGINT
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|ONE_OR_MORE
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

