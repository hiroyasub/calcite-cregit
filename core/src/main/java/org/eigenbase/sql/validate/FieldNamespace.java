begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
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
name|*
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
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|SqlValidatorNamespace
name|lookupChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|rowType
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
specifier|public
name|boolean
name|fieldExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End FieldNamespace.java
end_comment

end_unit

