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
name|sql
operator|.
name|type
operator|.
name|SqlOperandTypeChecker
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
name|validate
operator|.
name|SqlValidator
import|;
end_import

begin_comment
comment|/**  * The<code>JSON_KEYS</code> function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlJsonKeysFunction
extends|extends
name|SqlFunction
block|{
specifier|public
name|SqlJsonKeysFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"JSON_KEYS"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|VARCHAR_2000
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|checkOperandCount
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlOperandTypeChecker
name|argType
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
assert|assert
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|1
assert|;
block|}
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
return|return
name|super
operator|.
name|createCall
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|operands
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlJsonKeysFunction.java
end_comment

end_unit

