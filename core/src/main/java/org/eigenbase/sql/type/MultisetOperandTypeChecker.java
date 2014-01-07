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
operator|.
name|type
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * Parameter type-checking strategy types must be [nullable] Multiset,  * [nullable] Multiset and the two types must have the same element type  *  * @see MultisetSqlType#getComponentType  */
end_comment

begin_class
specifier|public
class|class
name|MultisetOperandTypeChecker
implements|implements
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
name|SqlCall
name|call
init|=
name|callBinding
operator|.
name|getCall
argument_list|()
decl_stmt|;
name|SqlNode
name|op0
init|=
name|call
operator|.
name|operands
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeStrategies
operator|.
name|otcMultiset
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|op0
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SqlNode
name|op1
init|=
name|call
operator|.
name|operands
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeStrategies
operator|.
name|otcMultiset
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|op1
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// TODO: this won't work if element types are of ROW types and there is
comment|// a mismatch.
name|RelDataType
name|biggest
init|=
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|leastRestrictive
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|op0
argument_list|)
operator|.
name|getComponentType
argument_list|()
argument_list|,
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|op1
argument_list|)
operator|.
name|getComponentType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|biggest
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newError
argument_list|(
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|TypeNotComparable
operator|.
name|ex
argument_list|(
name|call
operator|.
name|operands
index|[
literal|0
index|]
operator|.
name|getParserPosition
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|call
operator|.
name|operands
index|[
literal|1
index|]
operator|.
name|getParserPosition
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
literal|2
argument_list|)
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
return|return
literal|"<MULTISET> "
operator|+
name|opName
operator|+
literal|"<MULTISET>"
return|;
block|}
block|}
end_class

begin_comment
comment|// End MultisetOperandTypeChecker.java
end_comment

end_unit

