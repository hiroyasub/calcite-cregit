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
name|rel
operator|.
name|type
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
name|type
operator|.
name|SqlTypeExplicitPrecedenceList
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
name|SqlTypeFamily
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
name|SqlTypeName
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
name|Pair
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
comment|/**  * Implementation of {@link RelDataType} for a dynamic table.  *  *<p>It's used during SQL validation, where the field list is mutable for  * the getField() call. After SQL validation, a normal {@link RelDataTypeImpl}  * with an immutable field list takes the place of the DynamicRecordTypeImpl  * instance.  */
end_comment

begin_class
specifier|public
class|class
name|DynamicRecordTypeImpl
extends|extends
name|DynamicRecordType
block|{
specifier|private
specifier|final
name|RelDataTypeHolder
name|holder
decl_stmt|;
comment|/** Creates a DynamicRecordTypeImpl. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"method.invocation.invalid"
argument_list|)
specifier|public
name|DynamicRecordTypeImpl
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|holder
operator|=
operator|new
name|RelDataTypeHolder
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getFieldList
parameter_list|()
block|{
return|return
name|holder
operator|.
name|getFieldList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
return|return
name|holder
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataTypeField
name|getField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|boolean
name|elideRecord
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|RelDataTypeField
argument_list|,
name|Boolean
argument_list|>
name|pair
init|=
name|holder
operator|.
name|getFieldOrInsert
argument_list|(
name|fieldName
argument_list|,
name|caseSensitive
argument_list|)
decl_stmt|;
comment|// If a new field is added, we should re-compute the digest.
if|if
condition|(
name|pair
operator|.
name|right
condition|)
block|{
name|computeDigest
argument_list|()
expr_stmt|;
block|}
return|return
name|pair
operator|.
name|left
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
block|{
return|return
name|holder
operator|.
name|getFieldNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlTypeName
name|getSqlTypeName
parameter_list|()
block|{
return|return
name|SqlTypeName
operator|.
name|ROW
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataTypePrecedenceList
name|getPrecedenceList
parameter_list|()
block|{
return|return
operator|new
name|SqlTypeExplicitPrecedenceList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|sb
operator|.
name|append
argument_list|(
literal|"(DynamicRecordRow"
argument_list|)
operator|.
name|append
argument_list|(
name|getFieldNames
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStruct
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataTypeFamily
name|getFamily
parameter_list|()
block|{
name|SqlTypeFamily
name|family
init|=
name|getSqlTypeName
argument_list|()
operator|.
name|getFamily
argument_list|()
decl_stmt|;
return|return
name|family
operator|!=
literal|null
condition|?
name|family
else|:
name|this
return|;
block|}
block|}
end_class

end_unit

