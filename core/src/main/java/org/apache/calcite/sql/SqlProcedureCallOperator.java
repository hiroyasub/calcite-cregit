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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|validate
operator|.
name|SqlValidator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/**  * SqlProcedureCallOperator represents the CALL statement. It takes a single  * operand which is the real SqlCall.  */
end_comment

begin_class
specifier|public
class|class
name|SqlProcedureCallOperator
extends|extends
name|SqlPrefixOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlProcedureCallOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"CALL"
argument_list|,
name|SqlKind
operator|.
name|PROCEDURE_CALL
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
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
comment|// for now, rewrite "CALL f(x)" to "SELECT f(x) FROM VALUES(0)"
comment|// TODO jvs 18-Jan-2005:  rewrite to SELECT * FROM TABLE f(x)
comment|// once we support function calls as tables
return|return
operator|new
name|SqlSelect
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
literal|null
argument_list|,
operator|new
name|SqlNodeList
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlStdOperatorTable
operator|.
name|VALUES
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ROW
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|SqlLiteral
operator|.
name|createExactNumeric
argument_list|(
literal|"0"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

