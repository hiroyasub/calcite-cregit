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
comment|/**  * Definition of the<code>ANY_VALUE</code> aggregate functions,  * returning any one of the values which go into it.  */
end_comment

begin_class
specifier|public
class|class
name|SqlAnyValueAggFunction
extends|extends
name|SqlAggFunction
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates a SqlAnyValueAggFunction. */
specifier|public
name|SqlAnyValueAggFunction
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
name|ANY
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|kind
operator|==
name|SqlKind
operator|.
name|ANY_VALUE
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
block|}
end_class

begin_comment
comment|// End SqlAnyValueAggFunction.java
end_comment

end_unit

