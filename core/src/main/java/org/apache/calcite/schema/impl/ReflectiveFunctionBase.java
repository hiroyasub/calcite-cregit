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
name|schema
operator|.
name|impl
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
name|RelDataTypeFactory
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
name|schema
operator|.
name|Function
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
name|schema
operator|.
name|FunctionParameter
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
name|ReflectUtil
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|Modifier
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
comment|/**  * Implementation of a function that is based on a method.  * This class mainly solves conversion of method parameter types to {@code  * List<FunctionParameter>} form.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReflectiveFunctionBase
implements|implements
name|Function
block|{
comment|/** Method that implements the function. */
specifier|public
specifier|final
name|Method
name|method
decl_stmt|;
comment|/** Types of parameter for the function call. */
specifier|public
specifier|final
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|parameters
decl_stmt|;
comment|/**    * Creates a ReflectiveFunctionBase.    *    * @param method Method that is used to get type information from    */
specifier|public
name|ReflectiveFunctionBase
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
name|this
operator|.
name|parameters
operator|=
name|builder
argument_list|()
operator|.
name|addMethodParameters
argument_list|(
name|method
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the parameters of this function.    *    * @return Parameters; never null    */
specifier|public
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
comment|/**    * Verifies if given class has public constructor with zero arguments.    * @param clazz class to verify    * @return true if given class has public constructor with zero arguments    */
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
name|Modifier
operator|.
name|isPublic
argument_list|(
name|constructor
operator|.
name|getModifiers
argument_list|()
argument_list|)
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
comment|/**    * Finds a method in a given class by name.    * @param clazz class to search method in    * @param name name of the method to find    * @return the first method with matching name or null when no method found    */
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
operator|&&
operator|!
name|method
operator|.
name|isBridge
argument_list|()
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
comment|/** Creates a ParameterListBuilder. */
specifier|public
specifier|static
name|ParameterListBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|ParameterListBuilder
argument_list|()
return|;
block|}
comment|/** Helps build lists of    * {@link org.apache.calcite.schema.FunctionParameter}. */
specifier|public
specifier|static
class|class
name|ParameterListBuilder
block|{
specifier|final
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|builder
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|ImmutableList
argument_list|<
name|FunctionParameter
argument_list|>
name|build
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|builder
argument_list|)
return|;
block|}
specifier|public
name|ParameterListBuilder
name|add
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|type
argument_list|,
name|name
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|ParameterListBuilder
name|add
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|boolean
name|optional
parameter_list|)
block|{
specifier|final
name|int
name|ordinal
init|=
name|builder
operator|.
name|size
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|FunctionParameter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|ordinal
operator|+
literal|": "
operator|+
name|name
operator|+
literal|" "
operator|+
name|type
operator|.
name|getSimpleName
argument_list|()
operator|+
operator|(
name|optional
condition|?
literal|"?"
else|:
literal|""
operator|)
return|;
block|}
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
name|name
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
name|type
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isOptional
parameter_list|()
block|{
return|return
name|optional
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ParameterListBuilder
name|addMethodParameters
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|types
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|types
index|[
name|i
index|]
argument_list|,
name|ReflectUtil
operator|.
name|getParameterName
argument_list|(
name|method
argument_list|,
name|i
argument_list|)
argument_list|,
name|ReflectUtil
operator|.
name|isParameterOptional
argument_list|(
name|method
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

