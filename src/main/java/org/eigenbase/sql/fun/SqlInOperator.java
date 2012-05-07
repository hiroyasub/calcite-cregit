begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|reltype
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
name|resource
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
comment|/**  * Definition of the SQL<code>IN</code> operator, which tests for a value's  * membership in a subquery or a list of values.  *  * @author jhyde  * @version $Id$  * @since April 17, 2006  */
end_comment

begin_class
specifier|public
class|class
name|SqlInOperator
extends|extends
name|SqlBinaryOperator
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * If true the call represents 'NOT IN'.      */
specifier|private
specifier|final
name|boolean
name|isNotIn
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a SqlInOperator      *      * @param isNotIn Whether this is the 'NOT IN' operator      */
name|SqlInOperator
parameter_list|(
name|boolean
name|isNotIn
parameter_list|)
block|{
name|super
argument_list|(
name|isNotIn
condition|?
literal|"NOT IN"
else|:
literal|"IN"
argument_list|,
name|SqlKind
operator|.
name|IN
argument_list|,
literal|30
argument_list|,
literal|true
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiNullableBoolean
argument_list|,
name|SqlTypeStrategies
operator|.
name|otiFirstKnown
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|isNotIn
operator|=
name|isNotIn
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns whether this is the 'NOT IN' operator      *      * @return whether this is the 'NOT IN' operator      */
specifier|public
name|boolean
name|isNotIn
parameter_list|()
block|{
return|return
name|isNotIn
return|;
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
specifier|final
name|SqlNode
index|[]
name|operands
init|=
name|call
operator|.
name|getOperands
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|operands
operator|.
name|length
operator|==
literal|2
operator|)
assert|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|RelDataType
name|leftType
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operands
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|RelDataType
name|rightType
decl_stmt|;
comment|// Derive type for RHS.
if|if
condition|(
name|call
operator|.
name|operands
index|[
literal|1
index|]
operator|instanceof
name|SqlNodeList
condition|)
block|{
comment|// Handle the 'IN (expr, ...)' form.
name|List
argument_list|<
name|RelDataType
argument_list|>
name|rightTypeList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
name|SqlNodeList
name|nodeList
init|=
operator|(
name|SqlNodeList
operator|)
name|call
operator|.
name|operands
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|node
init|=
name|nodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RelDataType
name|nodeType
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|rightTypeList
operator|.
name|add
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
name|RelDataType
index|[]
name|rightTypes
init|=
name|rightTypeList
operator|.
name|toArray
argument_list|(
operator|new
name|RelDataType
index|[
name|rightTypeList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|rightType
operator|=
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|rightTypes
argument_list|)
expr_stmt|;
comment|// First check that the expressions in the IN list are compatible
comment|// with each other. Same rules as the VALUES operator (per
comment|// SQL:2003 Part 2 Section 8.4,<in predicate>).
if|if
condition|(
literal|null
operator|==
name|rightType
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
operator|.
name|operands
index|[
literal|1
index|]
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|IncompatibleTypesInList
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
comment|// Record the RHS type for use by SqlToRelConverter.
name|validator
operator|.
name|setValidatedNodeType
argument_list|(
name|nodeList
argument_list|,
name|rightType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Handle the 'IN (query)' form.
name|rightType
operator|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operands
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Now check that the left expression is compatible with the
comment|// type of the list. Same strategy as the '=' operator.
comment|// Normalize the types on both sides to be row types
comment|// for the purposes of compatibility-checking.
name|RelDataType
name|leftRowType
init|=
name|SqlTypeUtil
operator|.
name|promoteToRowType
argument_list|(
name|typeFactory
argument_list|,
name|leftType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RelDataType
name|rightRowType
init|=
name|SqlTypeUtil
operator|.
name|promoteToRowType
argument_list|(
name|typeFactory
argument_list|,
name|rightType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|ComparableOperandTypeChecker
name|checker
init|=
operator|(
name|ComparableOperandTypeChecker
operator|)
name|SqlTypeStrategies
operator|.
name|otcComparableUnorderedX2
decl_stmt|;
if|if
condition|(
operator|!
name|checker
operator|.
name|checkOperandTypes
argument_list|(
operator|new
name|ExplicitOperatorBinding
argument_list|(
operator|new
name|SqlCallBinding
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
argument_list|,
operator|new
name|RelDataType
index|[]
block|{
name|leftRowType
operator|,
name|rightRowType
block|}
block_content|)
block|)
end_class

begin_block
unit|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|IncompatibleValueType
operator|.
name|ex
argument_list|(
name|SqlStdOperatorTable
operator|.
name|inOperator
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
end_block

begin_comment
comment|// Result is a boolean, nullable if there are any nullable types
end_comment

begin_comment
comment|// on either side.
end_comment

begin_decl_stmt
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
end_decl_stmt

begin_if_stmt
if|if
condition|(
name|leftType
operator|.
name|isNullable
argument_list|()
operator|||
name|rightType
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|type
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
end_if_stmt

begin_return
return|return
name|type
return|;
end_return

begin_function
unit|}      public
name|boolean
name|argumentMustBeScalar
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
comment|// Argument #0 must be scalar, argument #1 can be a list (1, 2) or
comment|// a query (select deptno from emp). So, only coerce argument #0 into
comment|// a scalar subquery. For example, in
comment|//  select * from emp
comment|//  where (select count(*) from dept) in (select deptno from dept)
comment|// we should coerce the LHS to a scalar.
return|return
name|ordinal
operator|==
literal|0
return|;
block|}
end_function

begin_comment
unit|}
comment|// End SqlInOperator.java
end_comment

end_unit

