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
name|SqlNodeList
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
name|SqlTypeTransforms
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
name|Util
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
comment|/**  * The<code>COALESCE</code> function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCoalesceFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCoalesceFunction
parameter_list|()
block|{
comment|// NOTE jvs 26-July-2006:  We fill in the type strategies here,
comment|// but normally they are not used because the validator invokes
comment|// rewriteCall to convert COALESCE into CASE early.  However,
comment|// validator rewrite can optionally be disabled, in which case these
comment|// strategies are used.
name|super
argument_list|(
literal|"COALESCE"
argument_list|,
name|SqlKind
operator|.
name|COALESCE
argument_list|,
name|ReturnTypes
operator|.
name|LEAST_RESTRICTIVE
operator|.
name|andThen
argument_list|(
name|SqlTypeTransforms
operator|.
name|LEAST_NULLABLE
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|SAME_VARIADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// override SqlOperator
annotation|@
name|Override
specifier|public
name|SqlNode
name|rewriteCall
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|validateQuantifier
argument_list|(
name|validator
argument_list|,
name|call
argument_list|)
expr_stmt|;
comment|// check DISTINCT/ALL
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
if|if
condition|(
name|operands
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// No CASE needed
return|return
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|SqlParserPos
name|pos
init|=
name|call
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
name|SqlNodeList
name|whenList
init|=
operator|new
name|SqlNodeList
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|SqlNodeList
name|thenList
init|=
operator|new
name|SqlNodeList
argument_list|(
name|pos
argument_list|)
decl_stmt|;
comment|// todo: optimize when know operand is not null.
for|for
control|(
name|SqlNode
name|operand
range|:
name|Util
operator|.
name|skipLast
argument_list|(
name|operands
argument_list|)
control|)
block|{
name|whenList
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
operator|.
name|createCall
argument_list|(
name|pos
argument_list|,
name|operand
argument_list|)
argument_list|)
expr_stmt|;
name|thenList
operator|.
name|add
argument_list|(
name|SqlNode
operator|.
name|clone
argument_list|(
name|operand
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SqlNode
name|elseExpr
init|=
name|Util
operator|.
name|last
argument_list|(
name|operands
argument_list|)
decl_stmt|;
assert|assert
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
operator|==
literal|null
assert|;
return|return
name|SqlCase
operator|.
name|createSwitched
argument_list|(
name|pos
argument_list|,
literal|null
argument_list|,
name|whenList
argument_list|,
name|thenList
argument_list|,
name|elseExpr
argument_list|)
return|;
block|}
block|}
end_class

end_unit

