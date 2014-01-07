begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SqlSetOperator represents a relational set theory operator (UNION, INTERSECT,  * MINUS). These are binary operators, but with an extra boolean attribute  * tacked on for whether to remove duplicates (e.g. UNION ALL does not remove  * duplicates).  */
end_comment

begin_class
specifier|public
class|class
name|SqlSetOperator
extends|extends
name|SqlBinaryOperator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|boolean
name|all
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlSetOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|prec
argument_list|,
literal|true
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiLeastRestrictive
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcSetop
argument_list|)
expr_stmt|;
name|this
operator|.
name|all
operator|=
name|all
expr_stmt|;
block|}
specifier|public
name|SqlSetOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|all
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|prec
argument_list|,
literal|true
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
name|this
operator|.
name|all
operator|=
name|all
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|isAll
parameter_list|()
block|{
return|return
name|all
return|;
block|}
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
operator|!
name|all
return|;
block|}
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
name|validator
operator|.
name|validateQuery
argument_list|(
name|call
argument_list|,
name|operandScope
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlSetOperator.java
end_comment

end_unit

