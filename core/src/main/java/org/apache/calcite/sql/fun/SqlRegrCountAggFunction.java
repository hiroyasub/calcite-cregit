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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Definition of the SQL<code>REGR_COUNT</code> aggregation function.  *  *<p><code>REGR_COUNT</code> is an aggregator which returns the number of rows which  * have gone into it and both arguments are not<code>null</code>.  */
end_comment

begin_class
specifier|public
class|class
name|SqlRegrCountAggFunction
extends|extends
name|SqlCountAggFunction
block|{
specifier|public
name|SqlRegrCountAggFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
literal|"REGR_COUNT"
argument_list|,
name|OperandTypes
operator|.
name|NUMERIC_NUMERIC
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|SqlKind
operator|.
name|REGR_COUNT
operator|==
name|kind
argument_list|,
literal|"unsupported sql kind: "
operator|+
name|kind
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlRegrCountAggFunction.java
end_comment

end_unit

