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
name|ArrayList
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
comment|/**  * Holding the expandable list of fields for dynamic table.  */
end_comment

begin_class
class|class
name|RelDataTypeHolder
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
name|RelDataTypeHolder
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getFieldList
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Get field if exists, otherwise inserts a new field. The new field by default will have "any"    * type, except for the dynamic star field.    *    * @param fieldName Request field name    * @param caseSensitive Case Sensitive    * @return A pair of RelDataTypeField and Boolean. Boolean indicates whether a new field is added    * to this holder.    */
name|Pair
argument_list|<
name|RelDataTypeField
argument_list|,
name|Boolean
argument_list|>
name|getFieldOrInsert
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
comment|// First check if this field name exists in our field list
for|for
control|(
name|RelDataTypeField
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
name|Util
operator|.
name|matches
argument_list|(
name|caseSensitive
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldName
argument_list|)
condition|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|f
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// A dynamic star field matches any field
if|if
condition|(
name|f
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|DYNAMIC_STAR
condition|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|f
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
specifier|final
name|SqlTypeName
name|typeName
init|=
name|DynamicRecordType
operator|.
name|isDynamicStarColName
argument_list|(
name|fieldName
argument_list|)
condition|?
name|SqlTypeName
operator|.
name|DYNAMIC_STAR
else|:
name|SqlTypeName
operator|.
name|ANY
decl_stmt|;
comment|// This field does not exist in our field list; add it
name|RelDataTypeField
name|newField
init|=
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|fieldName
argument_list|,
name|fields
operator|.
name|size
argument_list|()
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
comment|// Add the name to our list of field names
name|fields
operator|.
name|add
argument_list|(
name|newField
argument_list|)
expr_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|newField
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
block|{
return|return
name|Pair
operator|.
name|left
argument_list|(
name|fields
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelDataTypeHolder.java
end_comment

end_unit

