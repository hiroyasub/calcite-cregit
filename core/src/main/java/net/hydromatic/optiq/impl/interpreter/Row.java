begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|interpreter
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

begin_comment
comment|/**  * Row.  */
end_comment

begin_class
specifier|public
class|class
name|Row
block|{
specifier|private
specifier|final
name|Object
index|[]
name|values
decl_stmt|;
specifier|public
name|Row
parameter_list|(
name|Object
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|values
argument_list|)
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
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Row
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
operator|(
operator|(
name|Row
operator|)
name|obj
operator|)
operator|.
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
return|;
block|}
specifier|public
name|Object
name|getObject
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|index
index|]
return|;
block|}
comment|// must stay package-protected
name|Object
index|[]
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
block|}
end_class

begin_comment
comment|// End Row.java
end_comment

end_unit

