begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
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
name|RelDataTypeFamily
import|;
end_import

begin_comment
comment|/**  * SQL array type.  */
end_comment

begin_class
specifier|public
class|class
name|ArraySqlType
extends|extends
name|AbstractSqlType
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataType
name|elementType
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an ArraySqlType. This constructor should only be called    * from a factory method.    */
specifier|public
name|ArraySqlType
parameter_list|(
name|RelDataType
name|elementType
parameter_list|,
name|boolean
name|isNullable
parameter_list|)
block|{
name|super
argument_list|(
name|SqlTypeName
operator|.
name|ARRAY
argument_list|,
name|isNullable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
assert|assert
name|elementType
operator|!=
literal|null
assert|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelDataTypeImpl
specifier|protected
name|void
name|generateTypeString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|boolean
name|withDetail
parameter_list|)
block|{
if|if
condition|(
name|withDetail
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|elementType
operator|.
name|getFullTypeString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|elementType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" ARRAY"
argument_list|)
expr_stmt|;
block|}
comment|// implement RelDataType
specifier|public
name|RelDataType
name|getComponentType
parameter_list|()
block|{
return|return
name|elementType
return|;
block|}
comment|// implement RelDataType
specifier|public
name|RelDataTypeFamily
name|getFamily
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

begin_comment
comment|// End ArraySqlType.java
end_comment

end_unit

