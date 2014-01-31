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
name|sql
operator|.
name|type
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The<code>UNNEST</code> operator.  */
end_comment

begin_class
specifier|public
class|class
name|SqlUnnestOperator
extends|extends
name|SqlFunctionalOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlUnnestOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"UNNEST"
argument_list|,
name|SqlKind
operator|.
name|UNNEST
argument_list|,
literal|200
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|MULTISET_OR_RECORD_MULTISET
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
name|RelDataType
name|type
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|type
operator|=
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|MultisetSqlType
name|t
init|=
operator|(
name|MultisetSqlType
operator|)
name|type
decl_stmt|;
return|return
name|t
operator|.
name|getComponentType
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|argumentMustBeScalar
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlUnnestOperator.java
end_comment

end_unit

