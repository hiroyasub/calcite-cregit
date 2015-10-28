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
name|SqlAsOperator
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
name|type
operator|.
name|InferTypes
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
comment|/**  * Operator that assigns an argument to a function call to a particular named  * parameter.  *  *<p>Not an expression; just a holder to represent syntax until the validator  * has chance to resolve arguments.  *  *<p>Sub-class of {@link SqlAsOperator} ("AS") out of convenience; to be  * consistent with AS, we reverse the arguments.  */
end_comment

begin_class
class|class
name|SqlArgumentAssignmentOperator
extends|extends
name|SqlAsOperator
block|{
specifier|public
name|SqlArgumentAssignmentOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"=>"
argument_list|,
name|SqlKind
operator|.
name|ARGUMENT_ASSIGNMENT
argument_list|,
literal|20
argument_list|,
literal|true
argument_list|,
name|ReturnTypes
operator|.
name|ARG0
argument_list|,
name|InferTypes
operator|.
name|RETURN_TYPE
argument_list|,
name|OperandTypes
operator|.
name|ANY_ANY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparse
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
comment|// Arguments are held in reverse order to be consistent with base class (AS).
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getRightPrec
argument_list|()
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlArgumentAssignmentOperator.java
end_comment

end_unit

