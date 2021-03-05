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
name|rex
operator|.
name|RexCall
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
name|SqlInternalOperator
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
name|SqlOperator
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
name|SqlOperatorTable
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
name|Litmus
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
comment|/**  * Contains internal operators.  *  *<p>These operators are always created directly, not by looking up a function  * or operator by name or syntax, and therefore this class does not implement  * interface {@link SqlOperatorTable}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlInternalOperators
block|{
specifier|private
name|SqlInternalOperators
parameter_list|()
block|{
block|}
comment|/** Similar to {@link SqlStdOperatorTable#ROW}, but does not print "ROW".    *    *<p>For arguments [1, TRUE], ROW would print "{@code ROW (1, TRUE)}",    * but this operator prints "{@code (1, TRUE)}". */
specifier|public
specifier|static
specifier|final
name|SqlRowOperator
name|ANONYMOUS_ROW
init|=
operator|new
name|SqlRowOperator
argument_list|(
literal|"$ANONYMOUS_ROW"
argument_list|)
block|{
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"assignment.type.incompatible"
argument_list|)
name|List
argument_list|<
annotation|@
name|Nullable
name|SqlNode
argument_list|>
name|operandList
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
name|writer
operator|.
name|list
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|PARENTHESES
argument_list|,
name|SqlWriter
operator|.
name|COMMA
argument_list|,
name|SqlNodeList
operator|.
name|of
argument_list|(
name|call
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|operandList
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/** Similar to {@link #ANONYMOUS_ROW}, but does not print "ROW" or    * parentheses.    *    *<p>For arguments [1, TRUE], prints "{@code 1, TRUE}".  It is used in    * contexts where parentheses have been printed (because we thought we were    * about to print "{@code (ROW (1, TRUE))}") and we wish we had not. */
specifier|public
specifier|static
specifier|final
name|SqlRowOperator
name|ANONYMOUS_ROW_NO_PARENTHESES
init|=
operator|new
name|SqlRowOperator
argument_list|(
literal|"$ANONYMOUS_ROW_NO_PARENTHESES"
argument_list|)
block|{
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
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|FUN_CALL
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|operand
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/** "$THROW_UNLESS(condition, message)" throws an error with the given message    * if condition is not TRUE, otherwise returns TRUE. */
specifier|public
specifier|static
specifier|final
name|SqlInternalOperator
name|THROW_UNLESS
init|=
operator|new
name|SqlInternalOperator
argument_list|(
literal|"$THROW_UNLESS"
argument_list|,
name|SqlKind
operator|.
name|OTHER
argument_list|)
decl_stmt|;
comment|/** An IN operator for Druid.    *    *<p>Unlike the regular    * {@link SqlStdOperatorTable#IN} operator it may    * be used in {@link RexCall}. It does not require that    * its operands have consistent types. */
specifier|public
specifier|static
specifier|final
name|SqlInOperator
name|DRUID_IN
init|=
operator|new
name|SqlInOperator
argument_list|(
name|SqlKind
operator|.
name|DRUID_IN
argument_list|)
decl_stmt|;
comment|/** A NOT IN operator for Druid, analogous to {@link #DRUID_IN}. */
specifier|public
specifier|static
specifier|final
name|SqlInOperator
name|DRUID_NOT_IN
init|=
operator|new
name|SqlInOperator
argument_list|(
name|SqlKind
operator|.
name|DRUID_NOT_IN
argument_list|)
decl_stmt|;
comment|/** A BETWEEN operator for Druid, analogous to {@link #DRUID_IN}. */
specifier|public
specifier|static
specifier|final
name|SqlBetweenOperator
name|DRUID_BETWEEN
init|=
operator|new
name|SqlBetweenOperator
argument_list|(
name|SqlBetweenOperator
operator|.
name|Flag
operator|.
name|SYMMETRIC
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|DRUID_BETWEEN
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validRexOperands
parameter_list|(
name|int
name|count
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** Separator expression inside GROUP_CONCAT, e.g. '{@code SEPARATOR ','}'. */
specifier|public
specifier|static
specifier|final
name|SqlOperator
name|SEPARATOR
init|=
operator|new
name|SqlInternalOperator
argument_list|(
literal|"SEPARATOR"
argument_list|,
name|SqlKind
operator|.
name|SEPARATOR
argument_list|,
literal|20
argument_list|,
literal|false
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
name|ANY
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

