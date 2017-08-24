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
comment|/**  * The {@code GROUPING_ID} function.  *  *<p>This function is not defined in the SQL standard; our implementation is  * consistent with Oracle.  *  *<p>Some examples are in {@code agg.iq}.  *  * @deprecated Now that {@code GROUPING} has the same functionality,  * this function is deprecated.  */
end_comment

begin_class
annotation|@
name|Deprecated
comment|// to be removed before 2.0
class|class
name|SqlGroupingIdFunction
extends|extends
name|SqlAbstractGroupFunction
block|{
name|SqlGroupingIdFunction
parameter_list|()
block|{
comment|//noinspection deprecation
name|super
argument_list|(
literal|"GROUPING_ID"
argument_list|,
name|SqlKind
operator|.
name|GROUPING_ID
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

begin_comment
comment|// End SqlGroupingIdFunction.java
end_comment

end_unit

