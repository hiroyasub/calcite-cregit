begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|RelDataType
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
name|SqlTypeUtil
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
name|InvocationTargetException
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
name|Method
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
comment|/**  * Utility functions for schemas.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Schemas
block|{
specifier|private
name|Schemas
parameter_list|()
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"no instances!"
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|TableFunction
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|TableFunction
argument_list|>
name|tableFunctions
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argumentTypes
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|TableFunction
argument_list|>
name|matches
init|=
operator|new
name|ArrayList
argument_list|<
name|TableFunction
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TableFunction
name|member
range|:
name|tableFunctions
control|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|member
argument_list|,
name|argumentTypes
argument_list|)
condition|)
block|{
name|matches
operator|.
name|add
argument_list|(
name|member
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|matches
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|null
return|;
case|case
literal|1
case|:
return|return
name|matches
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"More than one match for "
operator|+
name|name
operator|+
literal|" with arguments "
operator|+
name|argumentTypes
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|matches
parameter_list|(
name|TableFunction
name|member
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argumentTypes
parameter_list|)
block|{
name|List
argument_list|<
name|Parameter
argument_list|>
name|parameters
init|=
name|member
operator|.
name|getParameters
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameters
operator|.
name|size
argument_list|()
operator|!=
name|argumentTypes
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|argumentTypes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataType
name|argumentType
init|=
name|argumentTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Parameter
name|parameter
init|=
name|parameters
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|canConvert
argument_list|(
name|argumentType
argument_list|,
name|parameter
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|boolean
name|canConvert
parameter_list|(
name|RelDataType
name|fromType
parameter_list|,
name|RelDataType
name|toType
parameter_list|)
block|{
return|return
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|toType
argument_list|,
name|fromType
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|TableFunction
argument_list|<
name|T
argument_list|>
name|methodMember
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|,
specifier|final
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Parameter
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<
name|Parameter
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|parameterType
range|:
name|method
operator|.
name|getParameterTypes
argument_list|()
control|)
block|{
name|parameters
operator|.
name|add
argument_list|(
operator|new
name|Parameter
argument_list|()
block|{
specifier|final
name|int
name|ordinal
init|=
name|parameters
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createType
argument_list|(
name|parameterType
argument_list|)
decl_stmt|;
specifier|public
name|int
name|getOrdinal
parameter_list|()
block|{
return|return
name|ordinal
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"a"
operator|+
name|ordinal
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TableFunction
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|Parameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
specifier|public
name|Table
argument_list|<
name|T
argument_list|>
name|apply
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|Table
operator|)
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|arguments
operator|.
name|toArray
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|method
operator|.
name|getReturnType
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End Schemas.java
end_comment

end_unit

