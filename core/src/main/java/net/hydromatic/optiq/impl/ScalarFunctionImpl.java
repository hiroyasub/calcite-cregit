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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMultimap
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
extends|extends
name|ReflectiveFunctionBase
implements|implements
name|ScalarFunction
implements|,
name|ImplementableFunction
block|{
specifier|private
specifier|final
name|CallImplementor
name|implementor
decl_stmt|;
comment|/** Private constructor. */
specifier|private
name|ScalarFunctionImpl
parameter_list|(
name|Method
name|method
parameter_list|,
name|CallImplementor
name|implementor
parameter_list|)
block|{
name|super
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|this
operator|.
name|implementor
operator|=
name|implementor
expr_stmt|;
block|}
comment|/**    * Creates {@link net.hydromatic.optiq.ScalarFunction} for each method in a    * given class.    */
specifier|public
specifier|static
name|ImmutableMultimap
argument_list|<
name|String
argument_list|,
name|ScalarFunction
argument_list|>
name|createAll
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
specifier|final
name|ImmutableMultimap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|ScalarFunction
argument_list|>
name|builder
init|=
name|ImmutableMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|getDeclaringClass
argument_list|()
operator|==
name|Object
operator|.
name|class
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
operator|!
name|ScalarFunctionImpl
operator|.
name|classHasPublicZeroArgsConstructor
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ScalarFunction
name|function
init|=
name|create
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|function
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Creates {@link net.hydromatic.optiq.ScalarFunction} from given class.    *    *<p>If a method of the given name is not found or it does not suit,    * returns {@code null}.    *    * @param clazz class that is used to implement the function    * @param methodName Method name (typically "eval")    * @return created {@link ScalarFunction} or null    */
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
parameter_list|,
name|String
name|methodName
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
name|methodName
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|create
argument_list|(
name|method
argument_list|)
return|;
block|}
comment|/**    * Creates {@link net.hydromatic.optiq.ScalarFunction} from given method.    * When {@code eval} method does not suit, {@code null} is returned.    *    * @param method method that is used to implement the function    * @return created {@link ScalarFunction} or null    */
specifier|public
specifier|static
name|ScalarFunction
name|create
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|Class
name|clazz
init|=
name|method
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
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
name|CallImplementor
name|implementor
init|=
name|createImplementor
argument_list|(
name|method
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScalarFunctionImpl
argument_list|(
name|method
argument_list|,
name|implementor
argument_list|)
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
specifier|public
name|CallImplementor
name|getImplementor
parameter_list|()
block|{
return|return
name|implementor
return|;
block|}
specifier|private
specifier|static
name|CallImplementor
name|createImplementor
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|)
block|{
return|return
name|RexImpTable
operator|.
name|createImplementor
argument_list|(
operator|new
name|ReflectiveCallNotNullImplementor
argument_list|(
name|method
argument_list|)
argument_list|,
name|NullPolicy
operator|.
name|ANY
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ScalarFunctionImpl.java
end_comment

end_unit

