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
operator|.
name|impl
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
name|*
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
name|reltype
operator|.
name|RelDataTypeFactory
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
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

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Static
operator|.
name|*
import|;
end_import

begin_comment
comment|/** * Implementation of {@link net.hydromatic.optiq.ScalarFunction}. */
end_comment

begin_class
specifier|public
class|class
name|ScalarFunctionImpl
implements|implements
name|ScalarFunction
block|{
specifier|public
specifier|final
name|Method
name|method
decl_stmt|;
comment|/** Private constructor. */
specifier|private
name|ScalarFunctionImpl
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
block|}
comment|/** Creates a scalar function.    *    * @see TableMacroImpl#create(Class)    */
specifier|public
specifier|static
name|ScalarFunction
name|create
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
specifier|final
name|Method
name|method
init|=
name|findMethod
argument_list|(
name|clazz
argument_list|,
literal|"eval"
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
name|method
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|classHasPublicZeroArgsConstructor
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
throw|throw
name|RESOURCE
operator|.
name|requireDefaultConstructor
argument_list|(
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|ex
argument_list|()
throw|;
block|}
block|}
comment|// NOTE: scalar functions and table macros look similar. It is important
comment|// to check for table macros FIRST.
return|return
operator|new
name|ScalarFunctionImpl
argument_list|(
name|method
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|static
name|boolean
name|classHasPublicZeroArgsConstructor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
for|for
control|(
name|Constructor
argument_list|<
name|?
argument_list|>
name|constructor
range|:
name|clazz
operator|.
name|getConstructors
argument_list|()
control|)
block|{
if|if
condition|(
name|constructor
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|&&
operator|(
name|constructor
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|PUBLIC
operator|)
operator|!=
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|static
name|Method
name|findMethod
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Method
name|method
range|:
name|clazz
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|method
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractList
argument_list|<
name|FunctionParameter
argument_list|>
argument_list|()
block|{
specifier|public
name|FunctionParameter
name|get
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
operator|new
name|FunctionParameter
argument_list|()
block|{
specifier|public
name|int
name|getOrdinal
parameter_list|()
block|{
return|return
name|index
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"arg"
operator|+
name|index
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|parameterTypes
index|[
name|index
index|]
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|parameterTypes
operator|.
name|length
return|;
block|}
block|}
return|;
block|}
specifier|public
name|RelDataType
name|getReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ScalarFunctionImpl.java
end_comment

end_unit

