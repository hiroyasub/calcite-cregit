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
name|linq4j
operator|.
name|Ord
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|util
operator|.
name|SqlBasicVisitor
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
name|util
operator|.
name|SqlVisitor
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorImpl
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
name|SqlValidatorScope
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * An operator describing a window function specification.  *  *<p>Operands are as follows:</p>  *  *<ul>  *<li>0: name of window function ({@link org.apache.calcite.sql.SqlCall})</li>  *  *<li>1: window name ({@link org.apache.calcite.sql.SqlLiteral}) or  * window in-line specification ({@link SqlWindow})</li>  *  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|SqlOverOperator
extends|extends
name|SqlBinaryOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlOverOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"OVER"
argument_list|,
name|SqlKind
operator|.
name|OVER
argument_list|,
literal|20
argument_list|,
literal|true
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_FORCE_NULLABLE
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|ANY_ANY
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|validateCall
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlValidatorScope
name|operandScope
parameter_list|)
block|{
assert|assert
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|this
assert|;
assert|assert
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|2
assert|;
name|SqlCall
name|aggCall
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|aggCall
operator|.
name|getOperator
argument_list|()
operator|.
name|isAggregator
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|aggCall
argument_list|,
name|RESOURCE
operator|.
name|overNonAggregate
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|SqlNode
name|window
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|validator
operator|.
name|validateWindow
argument_list|(
name|window
argument_list|,
name|scope
argument_list|,
name|aggCall
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
comment|// Validate type of the inner aggregate call
name|validateOperands
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
expr_stmt|;
comment|// Assume the first operand is an aggregate call and derive its type.
comment|// When we are sure the window is not empty, pass that information to the
comment|// aggregate's operator return type inference as groupCount=1
comment|// Otherwise pass groupCount=0 so the agg operator understands the window
comment|// can be empty
name|SqlNode
name|agg
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|agg
operator|instanceof
name|SqlCall
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Argument to SqlOverOperator"
operator|+
literal|" should be SqlCall, got "
operator|+
name|agg
operator|.
name|getClass
argument_list|()
operator|+
literal|": "
operator|+
name|agg
argument_list|)
throw|;
block|}
name|SqlNode
name|window
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SqlWindow
name|w
init|=
name|validator
operator|.
name|resolveWindow
argument_list|(
name|window
argument_list|,
name|scope
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|int
name|groupCount
init|=
name|w
operator|.
name|isAlwaysNonEmpty
argument_list|()
condition|?
literal|1
else|:
literal|0
decl_stmt|;
specifier|final
name|SqlCall
name|aggCall
init|=
operator|(
name|SqlCall
operator|)
name|agg
decl_stmt|;
name|SqlCallBinding
name|opBinding
init|=
operator|new
name|SqlCallBinding
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|aggCall
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getGroupCount
parameter_list|()
block|{
return|return
name|groupCount
return|;
block|}
block|}
decl_stmt|;
name|RelDataType
name|ret
init|=
name|aggCall
operator|.
name|getOperator
argument_list|()
operator|.
name|inferReturnType
argument_list|(
name|opBinding
argument_list|)
decl_stmt|;
comment|// Copied from validateOperands
operator|(
operator|(
name|SqlValidatorImpl
operator|)
name|validator
operator|)
operator|.
name|setValidatedNodeType
argument_list|(
name|call
argument_list|,
name|ret
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SqlValidatorImpl
operator|)
name|validator
operator|)
operator|.
name|setValidatedNodeType
argument_list|(
name|agg
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * Accepts a {@link SqlVisitor}, and tells it to visit each child.    *    * @param visitor Visitor    */
specifier|public
parameter_list|<
name|R
parameter_list|>
name|void
name|acceptCall
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|boolean
name|onlyExpressions
parameter_list|,
name|SqlBasicVisitor
operator|.
name|ArgHandler
argument_list|<
name|R
argument_list|>
name|argHandler
parameter_list|)
block|{
if|if
condition|(
name|onlyExpressions
condition|)
block|{
for|for
control|(
name|Ord
argument_list|<
name|SqlNode
argument_list|>
name|operand
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|call
operator|.
name|getOperandList
argument_list|()
argument_list|)
control|)
block|{
comment|// if the second param is an Identifier then it's supposed to
comment|// be a name from a window clause and isn't part of the
comment|// group by check
if|if
condition|(
name|operand
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|operand
operator|.
name|i
operator|==
literal|1
operator|&&
name|operand
operator|.
name|e
operator|instanceof
name|SqlIdentifier
condition|)
block|{
continue|continue;
block|}
name|argHandler
operator|.
name|visitChild
argument_list|(
name|visitor
argument_list|,
name|call
argument_list|,
name|operand
operator|.
name|i
argument_list|,
name|operand
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|acceptCall
argument_list|(
name|visitor
argument_list|,
name|call
argument_list|,
name|onlyExpressions
argument_list|,
name|argHandler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlOverOperator.java
end_comment

end_unit

