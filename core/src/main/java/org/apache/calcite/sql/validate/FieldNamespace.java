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
name|validate
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link SqlValidatorNamespace} for a field of a record.  *  *<p>A field is not a very interesting namespace - except if the field has a  * record or multiset type - but this class exists to make fields behave  * similarly to other records for purposes of name resolution.  */
end_comment

begin_class
class|class
name|FieldNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a FieldNamespace.    *    * @param validator Validator    * @param dataType  Data type of field    */
name|FieldNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|RelDataType
name|dataType
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
literal|null
argument_list|)
expr_stmt|;
assert|assert
name|dataType
operator|!=
literal|null
assert|;
name|this
operator|.
name|rowType
operator|=
name|dataType
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|setType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
block|{
return|return
name|requireNonNull
argument_list|(
name|rowType
argument_list|,
literal|"rowType"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlValidatorNamespace
name|lookupChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|requireNonNull
argument_list|(
name|rowType
argument_list|,
literal|"rowType"
argument_list|)
operator|.
name|isStruct
argument_list|()
condition|)
block|{
return|return
name|validator
operator|.
name|lookupFieldNamespace
argument_list|(
name|rowType
argument_list|,
name|name
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataTypeField
name|field
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

