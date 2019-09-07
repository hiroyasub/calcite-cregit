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
name|util
package|;
end_package

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
name|ImmutableMap
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
name|ImmutableSortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
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
name|InvocationHandler
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
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/** Utilities for creating immutable beans. */
end_comment

begin_class
specifier|public
class|class
name|ImmutableBeans
block|{
specifier|private
name|ImmutableBeans
parameter_list|()
block|{
block|}
comment|/** Creates an immutable bean that implements a given interface. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|create
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|beanClass
parameter_list|)
block|{
if|if
condition|(
operator|!
name|beanClass
operator|.
name|isInterface
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must be interface"
argument_list|)
throw|;
block|}
specifier|final
name|ImmutableSortedMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|propertyNameBuilder
init|=
name|ImmutableSortedMap
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Method
argument_list|,
name|Handler
argument_list|<
name|T
argument_list|>
argument_list|>
name|handlers
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|requiredPropertyNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// First pass, add "get" methods and build a list of properties.
for|for
control|(
name|Method
name|method
range|:
name|beanClass
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isPublic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|Property
name|property
init|=
name|method
operator|.
name|getAnnotation
argument_list|(
name|Property
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
specifier|final
name|boolean
name|hasNonnull
init|=
name|hasAnnotation
argument_list|(
name|method
argument_list|,
literal|"javax.annotation.Nonnull"
argument_list|)
decl_stmt|;
specifier|final
name|Mode
name|mode
decl_stmt|;
specifier|final
name|Object
name|defaultValue
init|=
name|getDefault
argument_list|(
name|method
argument_list|)
decl_stmt|;
specifier|final
name|String
name|methodName
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|propertyName
decl_stmt|;
if|if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|"get"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|mode
operator|=
name|Mode
operator|.
name|GET
expr_stmt|;
block|}
if|else if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"is"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|"is"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|mode
operator|=
name|Mode
operator|.
name|GET
expr_stmt|;
block|}
if|else if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"with"
argument_list|)
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|propertyName
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
name|methodName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mode
operator|=
name|Mode
operator|.
name|GET
expr_stmt|;
block|}
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|propertyType
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|getParameterCount
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"method '"
operator|+
name|methodName
operator|+
literal|"' has too many parameters"
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|required
init|=
name|property
operator|.
name|required
argument_list|()
operator|||
name|propertyType
operator|.
name|isPrimitive
argument_list|()
operator|||
name|hasNonnull
decl_stmt|;
if|if
condition|(
name|required
condition|)
block|{
name|requiredPropertyNames
operator|.
name|add
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
name|propertyNameBuilder
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|propertyType
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|defaultValue2
init|=
name|convertDefault
argument_list|(
name|defaultValue
argument_list|,
name|propertyName
argument_list|,
name|propertyType
argument_list|)
decl_stmt|;
name|handlers
operator|.
name|put
argument_list|(
name|method
argument_list|,
parameter_list|(
name|bean
parameter_list|,
name|args
parameter_list|)
lambda|->
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|GET
case|:
specifier|final
name|Object
name|v
init|=
name|bean
operator|.
name|map
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
return|return
name|v
return|;
block|}
if|if
condition|(
name|required
operator|&&
name|defaultValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"property '"
operator|+
name|propertyName
operator|+
literal|"' is required and has no default value"
argument_list|)
throw|;
block|}
return|return
name|defaultValue2
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// Second pass, add "with" methods if they correspond to a property.
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|propertyNames
init|=
name|propertyNameBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|beanClass
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isPublic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|Mode
name|mode
decl_stmt|;
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|final
name|String
name|methodName
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|else if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"is"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|else if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"with"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|"with"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|mode
operator|=
name|Mode
operator|.
name|WITH
expr_stmt|;
block|}
if|else if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|"set"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|mode
operator|=
name|Mode
operator|.
name|SET
expr_stmt|;
block|}
else|else
block|{
continue|continue;
block|}
specifier|final
name|Class
name|propertyClass
init|=
name|propertyNames
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyClass
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot find property '"
operator|+
name|propertyName
operator|+
literal|"' for method '"
operator|+
name|methodName
operator|+
literal|"'; maybe add a method 'get"
operator|+
name|propertyName
operator|+
literal|"'?'"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|WITH
case|:
if|if
condition|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|!=
name|beanClass
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"method '"
operator|+
name|methodName
operator|+
literal|"' should return the bean class '"
operator|+
name|beanClass
operator|+
literal|"', actually returns '"
operator|+
name|method
operator|.
name|getReturnType
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
break|break;
case|case
name|SET
case|:
if|if
condition|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|!=
name|void
operator|.
name|class
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"method '"
operator|+
name|methodName
operator|+
literal|"' should return void, actually returns '"
operator|+
name|method
operator|.
name|getReturnType
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|method
operator|.
name|getParameterCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"method '"
operator|+
name|methodName
operator|+
literal|"' should have one parameter, actually has "
operator|+
name|method
operator|.
name|getParameterCount
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Class
name|propertyType
init|=
name|propertyNames
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|propertyType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"method '"
operator|+
name|methodName
operator|+
literal|"' should have parameter of type "
operator|+
name|propertyType
operator|+
literal|", actually has "
operator|+
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|required
init|=
name|requiredPropertyNames
operator|.
name|contains
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|handlers
operator|.
name|put
argument_list|(
name|method
argument_list|,
parameter_list|(
name|bean
parameter_list|,
name|args
parameter_list|)
lambda|->
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|WITH
case|:
specifier|final
name|Object
name|v
init|=
name|bean
operator|.
name|map
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapBuilder
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
return|return
name|bean
operator|.
name|asBean
argument_list|()
return|;
block|}
comment|// the key already exists; painstakingly copy all entries
comment|// except the one with this key
name|mapBuilder
operator|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
expr_stmt|;
name|bean
operator|.
name|map
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
block|{
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|mapBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the key does not exist; put the whole map into the builder
name|mapBuilder
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|bean
operator|.
name|map
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
name|mapBuilder
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|required
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set required "
operator|+
literal|"property '"
operator|+
name|propertyName
operator|+
literal|"' to null"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|mapBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|bean
operator|.
name|withMap
argument_list|(
name|map
argument_list|)
operator|.
name|asBean
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|handlers
operator|.
name|put
argument_list|(
name|getMethod
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"toString"
argument_list|)
argument_list|,
parameter_list|(
name|bean
parameter_list|,
name|args
parameter_list|)
lambda|->
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|bean
operator|.
name|map
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|handlers
operator|.
name|put
argument_list|(
name|getMethod
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"hashCode"
argument_list|)
argument_list|,
parameter_list|(
name|bean
parameter_list|,
name|args
parameter_list|)
lambda|->
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|bean
operator|.
name|map
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|handlers
operator|.
name|put
argument_list|(
name|getMethod
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"equals"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
parameter_list|(
name|bean
parameter_list|,
name|args
parameter_list|)
lambda|->
name|bean
operator|==
name|args
index|[
literal|0
index|]
comment|// Use a little arg-swap trick because it's difficult to get inside
comment|// a proxy
operator|||
name|beanClass
operator|.
name|isInstance
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
operator|&&
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|bean
operator|.
name|map
argument_list|)
comment|// Strictly, a bean should not equal a Map but it's convenient
operator|||
name|args
index|[
literal|0
index|]
operator|instanceof
name|Map
operator|&&
name|bean
operator|.
name|map
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|makeBean
argument_list|(
name|beanClass
argument_list|,
name|handlers
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/** Looks for an annotation by class name.    * Useful if you don't want to depend on the class    * (e.g. "javax.annotation.Nonnull") at compile time. */
specifier|private
specifier|static
name|boolean
name|hasAnnotation
parameter_list|(
name|Method
name|method
parameter_list|,
name|String
name|className
parameter_list|)
block|{
for|for
control|(
name|Annotation
name|annotation
range|:
name|method
operator|.
name|getDeclaredAnnotations
argument_list|()
control|)
block|{
if|if
condition|(
name|annotation
operator|.
name|annotationType
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|className
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
specifier|private
specifier|static
name|Object
name|getDefault
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
name|Object
name|defaultValue
init|=
literal|null
decl_stmt|;
specifier|final
name|IntDefault
name|intDefault
init|=
name|method
operator|.
name|getAnnotation
argument_list|(
name|IntDefault
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|intDefault
operator|!=
literal|null
condition|)
block|{
name|defaultValue
operator|=
name|intDefault
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
specifier|final
name|BooleanDefault
name|booleanDefault
init|=
name|method
operator|.
name|getAnnotation
argument_list|(
name|BooleanDefault
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|booleanDefault
operator|!=
literal|null
condition|)
block|{
name|defaultValue
operator|=
name|booleanDefault
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
specifier|final
name|StringDefault
name|stringDefault
init|=
name|method
operator|.
name|getAnnotation
argument_list|(
name|StringDefault
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|stringDefault
operator|!=
literal|null
condition|)
block|{
name|defaultValue
operator|=
name|stringDefault
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
specifier|final
name|EnumDefault
name|enumDefault
init|=
name|method
operator|.
name|getAnnotation
argument_list|(
name|EnumDefault
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumDefault
operator|!=
literal|null
condition|)
block|{
name|defaultValue
operator|=
name|enumDefault
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
return|return
name|defaultValue
return|;
block|}
specifier|private
specifier|static
name|Object
name|convertDefault
parameter_list|(
name|Object
name|defaultValue
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|Class
name|propertyType
parameter_list|)
block|{
if|if
condition|(
name|defaultValue
operator|==
literal|null
operator|||
operator|!
name|propertyType
operator|.
name|isEnum
argument_list|()
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
for|for
control|(
name|Object
name|enumConstant
range|:
name|propertyType
operator|.
name|getEnumConstants
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|Enum
operator|)
name|enumConstant
operator|)
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|defaultValue
argument_list|)
condition|)
block|{
return|return
name|enumConstant
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"property '"
operator|+
name|propertyName
operator|+
literal|"' is an enum but its default value "
operator|+
name|defaultValue
operator|+
literal|" is not a valid enum constant"
argument_list|)
throw|;
block|}
specifier|private
specifier|static
name|Method
name|getMethod
parameter_list|(
name|Class
argument_list|<
name|Object
argument_list|>
name|aClass
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
modifier|...
name|parameterTypes
parameter_list|)
block|{
try|try
block|{
return|return
name|aClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
name|parameterTypes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|makeBean
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|beanClass
parameter_list|,
name|ImmutableMap
argument_list|<
name|Method
argument_list|,
name|Handler
argument_list|<
name|T
argument_list|>
argument_list|>
name|handlers
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
return|return
operator|new
name|BeanImpl
argument_list|<>
argument_list|(
name|beanClass
argument_list|,
name|handlers
argument_list|,
name|map
argument_list|)
operator|.
name|asBean
argument_list|()
return|;
block|}
comment|/** Is the method reading or writing? */
specifier|private
enum|enum
name|Mode
block|{
name|GET
block|,
name|SET
block|,
name|WITH
block|}
comment|/** Handler for a particular method call; called with "this" and arguments.    *    * @param<T> Bean type */
specifier|private
interface|interface
name|Handler
parameter_list|<
name|T
parameter_list|>
block|{
name|Object
name|apply
parameter_list|(
name|BeanImpl
argument_list|<
name|T
argument_list|>
name|bean
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
function_decl|;
block|}
comment|/** Property of a bean. Apply this annotation to the "get" method. */
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
specifier|public
annotation_defn|@interface
name|Property
block|{
comment|/** Whether the property is required.      *      *<p>Properties of type {@code int} and {@code boolean} are always      * required.      *      *<p>If a property is required, it cannot be set to null.      * If it has no default value, calling "get" will give a runtime exception.      */
name|boolean
name|required
parameter_list|()
default|default
literal|false
function_decl|;
block|}
comment|/** Default value of an int property. */
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
specifier|public
annotation_defn|@interface
name|IntDefault
block|{
name|int
name|value
parameter_list|()
function_decl|;
block|}
comment|/** Default value of a boolean property of a bean. */
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
specifier|public
annotation_defn|@interface
name|BooleanDefault
block|{
name|boolean
name|value
parameter_list|()
function_decl|;
block|}
comment|/** Default value of a String property of a bean. */
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
specifier|public
annotation_defn|@interface
name|StringDefault
block|{
name|String
name|value
parameter_list|()
function_decl|;
block|}
comment|/** Default value of an enum property of a bean. */
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
specifier|public
annotation_defn|@interface
name|EnumDefault
block|{
name|String
name|value
parameter_list|()
function_decl|;
block|}
comment|/** Default value of a String or enum property of a bean that is null. */
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
specifier|public
annotation_defn|@interface
name|NullDefault
block|{   }
comment|/** Implementation of an instance of a bean; stores property    * values in a map, and also implements {@code InvocationHandler}    * so that it can retrieve calls from a reflective proxy.    *    * @param<T> Bean type */
specifier|private
specifier|static
class|class
name|BeanImpl
parameter_list|<
name|T
parameter_list|>
implements|implements
name|InvocationHandler
block|{
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Method
argument_list|,
name|Handler
argument_list|<
name|T
argument_list|>
argument_list|>
name|handlers
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|beanClass
decl_stmt|;
name|BeanImpl
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|beanClass
parameter_list|,
name|ImmutableMap
argument_list|<
name|Method
argument_list|,
name|Handler
argument_list|<
name|T
argument_list|>
argument_list|>
name|handlers
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|beanClass
operator|=
name|beanClass
expr_stmt|;
name|this
operator|.
name|handlers
operator|=
name|handlers
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|Handler
name|handler
init|=
name|handlers
operator|.
name|get
argument_list|(
name|method
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no handler for method "
operator|+
name|method
argument_list|)
throw|;
block|}
return|return
name|handler
operator|.
name|apply
argument_list|(
name|this
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/** Returns a copy of this bean that has a different map. */
name|BeanImpl
argument_list|<
name|T
argument_list|>
name|withMap
parameter_list|(
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
return|return
operator|new
name|BeanImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|beanClass
argument_list|,
name|handlers
argument_list|,
name|map
argument_list|)
return|;
block|}
comment|/** Wraps this handler in a proxy that implements the required      * interface. */
name|T
name|asBean
parameter_list|()
block|{
return|return
name|beanClass
operator|.
name|cast
argument_list|(
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|beanClass
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|beanClass
block|}
argument_list|,
name|this
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

