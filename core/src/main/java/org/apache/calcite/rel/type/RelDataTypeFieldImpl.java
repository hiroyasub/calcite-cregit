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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link RelDataTypeField}.  */
end_comment

begin_class
specifier|public
class|class
name|RelDataTypeFieldImpl
implements|implements
name|RelDataTypeField
implements|,
name|Serializable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a RelDataTypeFieldImpl.    */
specifier|public
name|RelDataTypeFieldImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|index
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
assert|assert
name|name
operator|!=
literal|null
assert|;
assert|assert
name|type
operator|!=
literal|null
assert|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|index
operator|^
name|name
operator|.
name|hashCode
argument_list|()
operator|^
name|type
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|RelDataTypeFieldImpl
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RelDataTypeFieldImpl
name|that
init|=
operator|(
name|RelDataTypeFieldImpl
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|index
operator|==
name|that
operator|.
name|index
operator|&&
name|this
operator|.
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
argument_list|)
operator|&&
name|this
operator|.
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
argument_list|)
return|;
block|}
comment|// implement RelDataTypeField
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|// implement RelDataTypeField
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
comment|// implement RelDataTypeField
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|// implement Map.Entry
specifier|public
specifier|final
name|String
name|getKey
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
comment|// implement Map.Entry
specifier|public
specifier|final
name|RelDataType
name|getValue
parameter_list|()
block|{
return|return
name|getType
argument_list|()
return|;
block|}
comment|// implement Map.Entry
specifier|public
name|RelDataType
name|setValue
parameter_list|(
name|RelDataType
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// for debugging
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"#"
operator|+
name|index
operator|+
literal|": "
operator|+
name|name
operator|+
literal|" "
operator|+
name|type
return|;
block|}
specifier|public
name|boolean
name|isDynamicStar
parameter_list|()
block|{
return|return
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|DYNAMIC_STAR
return|;
block|}
block|}
end_class

end_unit

