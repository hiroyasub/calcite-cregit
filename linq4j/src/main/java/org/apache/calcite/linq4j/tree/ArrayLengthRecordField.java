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
name|linq4j
operator|.
name|tree
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Represents a length field of a RecordType  */
end_comment

begin_class
specifier|public
class|class
name|ArrayLengthRecordField
implements|implements
name|Types
operator|.
name|RecordField
block|{
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
specifier|private
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|public
name|ArrayLengthRecordField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
assert|assert
name|fieldName
operator|!=
literal|null
operator|:
literal|"fieldName should not be null"
assert|;
assert|assert
name|clazz
operator|!=
literal|null
operator|:
literal|"clazz should not be null"
assert|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
block|}
specifier|public
name|boolean
name|nullable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|int
operator|.
name|class
return|;
block|}
specifier|public
name|int
name|getModifiers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IllegalAccessException
block|{
return|return
name|Array
operator|.
name|getLength
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|public
name|Type
name|getDeclaringClass
parameter_list|()
block|{
return|return
name|clazz
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ArrayLengthRecordField
name|that
init|=
operator|(
name|ArrayLengthRecordField
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|clazz
operator|.
name|equals
argument_list|(
name|that
operator|.
name|clazz
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|fieldName
argument_list|,
name|clazz
argument_list|)
return|;
block|}
block|}
end_class

end_unit

