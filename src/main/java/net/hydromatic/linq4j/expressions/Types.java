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
name|linq4j
operator|.
name|expressions
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerator
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
name|*
import|;
end_import

begin_comment
comment|/**  * Utilities for converting between {@link Expression}, {@link Type} and  * {@link Class}.  *  * @see Primitive  */
end_comment

begin_class
specifier|public
class|class
name|Types
block|{
comment|/** Creates a type with generic parameters. */
specifier|public
specifier|static
name|Type
name|of
parameter_list|(
name|Type
name|type
parameter_list|,
name|Type
modifier|...
name|typeArguments
parameter_list|)
block|{
if|if
condition|(
name|typeArguments
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|type
return|;
block|}
return|return
operator|new
name|ParameterizedTypeImpl
argument_list|(
name|type
argument_list|,
name|toList
argument_list|(
name|typeArguments
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Returns the component type of a {@link Collection}, {@link Iterable}      * (including {@link net.hydromatic.linq4j.Queryable Queryable} and      * {@link net.hydromatic.linq4j.Enumerable Enumerable}), {@link Iterator},      * {@link Enumerator}, or an array.      *      *<p>Returns null if the type is not one of these.</p> */
specifier|public
specifier|static
name|Type
name|getComponentType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|Class
name|clazz
init|=
name|toClass
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|clazz
operator|.
name|getComponentType
argument_list|()
return|;
block|}
if|if
condition|(
name|Collection
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|Iterable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|Iterator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|Enumerator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|ParameterizedType
condition|)
block|{
return|return
operator|(
operator|(
name|ParameterizedType
operator|)
name|type
operator|)
operator|.
name|getActualTypeArguments
argument_list|()
index|[
literal|0
index|]
return|;
block|}
return|return
name|Object
operator|.
name|class
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns a list backed by a copy of an array. The contents of the list      * will not change even if the contents of the array are subsequently      * modified. */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|toList
parameter_list|(
name|T
index|[]
name|ts
parameter_list|)
block|{
switch|switch
condition|(
name|ts
operator|.
name|length
condition|)
block|{
case|case
literal|0
case|:
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
case|case
literal|1
case|:
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|ts
index|[
literal|0
index|]
argument_list|)
return|;
default|default:
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|ts
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|static
name|Field
name|getField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|Field
name|field
decl_stmt|;
try|try
block|{
name|field
operator|=
name|toClass
argument_list|(
name|type
argument_list|)
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while resolving field '"
operator|+
name|fieldName
operator|+
literal|"' in class "
operator|+
name|type
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|field
return|;
block|}
specifier|public
specifier|static
name|Class
name|toClass
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|Class
condition|)
block|{
return|return
operator|(
name|Class
operator|)
name|type
return|;
block|}
if|if
condition|(
name|type
operator|instanceof
name|ParameterizedType
condition|)
block|{
return|return
name|toClass
argument_list|(
operator|(
operator|(
name|ParameterizedType
operator|)
name|type
operator|)
operator|.
name|getRawType
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|instanceof
name|TypeVariable
condition|)
block|{
name|TypeVariable
name|typeVariable
init|=
operator|(
name|TypeVariable
operator|)
name|type
decl_stmt|;
return|return
name|toClass
argument_list|(
name|typeVariable
operator|.
name|getBounds
argument_list|()
index|[
literal|0
index|]
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unsupported type "
operator|+
name|type
argument_list|)
throw|;
comment|// TODO:
block|}
specifier|static
name|Class
index|[]
name|toClassArray
parameter_list|(
name|Collection
argument_list|<
name|Type
argument_list|>
name|types
parameter_list|)
block|{
name|List
argument_list|<
name|Class
argument_list|>
name|classes
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Type
name|type
range|:
name|types
control|)
block|{
name|classes
operator|.
name|add
argument_list|(
name|toClass
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|classes
operator|.
name|toArray
argument_list|(
operator|new
name|Class
index|[
name|classes
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|static
name|Class
index|[]
name|toClassArray
parameter_list|(
name|Iterable
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
block|{
name|List
argument_list|<
name|Class
argument_list|>
name|classes
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Expression
name|argument
range|:
name|arguments
control|)
block|{
name|classes
operator|.
name|add
argument_list|(
name|toClass
argument_list|(
name|argument
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|classes
operator|.
name|toArray
argument_list|(
operator|new
name|Class
index|[
name|classes
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Boxes a type, if it is primitive, and returns the type name.      * The type is abbreviated if it is in the "java.lang" package.      *      *<p>For example,      * boxClassName(int) returns "Integer";      * boxClassName(List&lt;String&gt;) returns "List&lt;String&gt;"</p>      *      * @param type Type      * @return Class name      */
specifier|static
name|String
name|boxClassName
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|type
operator|instanceof
name|Class
operator|)
condition|)
block|{
return|return
name|type
operator|.
name|toString
argument_list|()
return|;
block|}
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|primitive
operator|!=
literal|null
condition|)
block|{
return|return
name|primitive
operator|.
name|boxClass
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|className
argument_list|(
name|type
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|Type
name|box
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|primitive
operator|!=
literal|null
condition|)
block|{
return|return
name|primitive
operator|.
name|boxClass
return|;
block|}
else|else
block|{
return|return
name|type
return|;
block|}
block|}
specifier|public
specifier|static
name|Type
name|unbox
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|ofBox
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|primitive
operator|!=
literal|null
condition|)
block|{
return|return
name|primitive
operator|.
name|primitiveClass
return|;
block|}
else|else
block|{
return|return
name|type
return|;
block|}
block|}
specifier|static
name|String
name|className
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|type
operator|instanceof
name|Class
operator|)
condition|)
block|{
return|return
name|type
operator|.
name|toString
argument_list|()
return|;
block|}
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|type
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|className
argument_list|(
name|clazz
operator|.
name|getComponentType
argument_list|()
argument_list|)
operator|+
literal|"[]"
return|;
block|}
name|String
name|className
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|getPackage
argument_list|()
operator|==
name|Package
operator|.
name|getPackage
argument_list|(
literal|"java.lang"
argument_list|)
operator|&&
operator|!
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
return|return
name|className
operator|.
name|substring
argument_list|(
literal|"java.lang."
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
name|className
operator|.
name|replace
argument_list|(
literal|'$'
argument_list|,
literal|'.'
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isAssignableFrom
parameter_list|(
name|Type
name|type0
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
return|return
name|toClass
argument_list|(
name|type0
argument_list|)
operator|.
name|isAssignableFrom
argument_list|(
name|toClass
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isArray
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|toClass
argument_list|(
name|type
argument_list|)
operator|.
name|isArray
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|Field
name|nthField
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Type
name|clazz
parameter_list|)
block|{
return|return
name|Types
operator|.
name|toClass
argument_list|(
name|clazz
argument_list|)
operator|.
name|getFields
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
specifier|static
name|boolean
name|allAssignable
parameter_list|(
name|boolean
name|varArgs
parameter_list|,
name|Class
index|[]
name|parameterTypes
parameter_list|,
name|Class
index|[]
name|argumentTypes
parameter_list|)
block|{
if|if
condition|(
name|varArgs
condition|)
block|{
if|if
condition|(
name|argumentTypes
operator|.
name|length
operator|<
name|parameterTypes
operator|.
name|length
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|!=
name|argumentTypes
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Class
name|parameterType
init|=
operator|!
name|varArgs
operator|||
name|i
operator|<
name|parameterTypes
operator|.
name|length
operator|-
literal|1
condition|?
name|parameterTypes
index|[
name|i
index|]
else|:
name|Object
operator|.
name|class
decl_stmt|;
if|if
condition|(
operator|!
name|parameterType
operator|.
name|isAssignableFrom
argument_list|(
name|argumentTypes
index|[
name|i
index|]
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
comment|/**      * Finds a method of a given name that accepts a given set of arguments.      * Includes in its search inherited methods and methods with wider argument      * types.      *      * @param clazz Class against which method is invoked      * @param methodName Name of method      * @param argumentTypes Types of arguments      * @return A method with the given name that matches the arguments given      *      * @throws RuntimeException if method not found      */
specifier|public
specifier|static
name|Method
name|lookupMethod
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
modifier|...
name|argumentTypes
parameter_list|)
block|{
try|try
block|{
return|return
name|clazz
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
name|argumentTypes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
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
name|methodName
argument_list|)
operator|&&
name|allAssignable
argument_list|(
name|method
operator|.
name|isVarArgs
argument_list|()
argument_list|,
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|,
name|argumentTypes
argument_list|)
condition|)
block|{
return|return
name|method
return|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while resolving method '"
operator|+
name|methodName
operator|+
literal|"' in class "
operator|+
name|clazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Finds a constructor of a given class that accepts a given set of      * arguments. Includes in its search methods with wider argument types.      *      * @param type Class against which method is invoked      * @param argumentTypes Types of arguments      * @return A method with the given name that matches the arguments given      *      * @throws RuntimeException if method not found      */
specifier|public
specifier|static
name|Constructor
name|lookupConstructor
parameter_list|(
name|Type
name|type
parameter_list|,
name|Class
modifier|...
name|argumentTypes
parameter_list|)
block|{
specifier|final
name|Class
name|clazz
init|=
name|toClass
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|Constructor
index|[]
name|constructors
init|=
name|clazz
operator|.
name|getDeclaredConstructors
argument_list|()
decl_stmt|;
for|for
control|(
name|Constructor
name|constructor
range|:
name|constructors
control|)
block|{
if|if
condition|(
name|allAssignable
argument_list|(
name|constructor
operator|.
name|isVarArgs
argument_list|()
argument_list|,
name|constructor
operator|.
name|getParameterTypes
argument_list|()
argument_list|,
name|argumentTypes
argument_list|)
condition|)
block|{
return|return
name|constructor
return|;
block|}
block|}
if|if
condition|(
name|constructors
operator|.
name|length
operator|==
literal|0
operator|&&
name|argumentTypes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|Constructor
index|[]
name|constructors1
init|=
name|clazz
operator|.
name|getConstructors
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|clazz
operator|.
name|getConstructor
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while resolving constructor in class "
operator|+
name|type
operator|+
literal|" with types "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|argumentTypes
argument_list|)
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|boolean
name|isPrimitive
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|type
operator|instanceof
name|Class
operator|&&
operator|(
operator|(
name|Class
operator|)
name|type
operator|)
operator|.
name|isPrimitive
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|discard
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
literal|false
condition|)
block|{
name|discard
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns the most restrictive type that is assignable from all given      * types. */
specifier|static
name|Type
name|gcd
parameter_list|(
name|Type
modifier|...
name|types
parameter_list|)
block|{
comment|// TODO: improve this
if|if
condition|(
name|types
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|Object
operator|.
name|class
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
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
if|if
condition|(
name|types
index|[
name|i
index|]
operator|!=
name|types
index|[
literal|0
index|]
condition|)
block|{
return|return
name|Object
operator|.
name|class
return|;
block|}
block|}
return|return
name|types
index|[
literal|0
index|]
return|;
block|}
comment|/**      * Wraps an expression in a cast if it is not already of the desired type,      * or cannot be implicitly converted to it.      *      * @param returnType Desired type      * @param expression Expression      * @return Expression of desired type      */
specifier|public
specifier|static
name|Expression
name|castIfNecessary
parameter_list|(
name|Type
name|returnType
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|Types
operator|.
name|isAssignableFrom
argument_list|(
name|returnType
argument_list|,
name|expression
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|expression
return|;
block|}
if|if
condition|(
name|returnType
operator|instanceof
name|Class
operator|&&
name|Number
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
operator|(
name|Class
operator|)
name|returnType
argument_list|)
operator|&&
name|expression
operator|.
name|getType
argument_list|()
operator|instanceof
name|Class
operator|&&
name|Number
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
operator|(
name|Class
operator|)
name|expression
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
comment|// E.g.
comment|//   Integer foo(BigDecimal o) {
comment|//     return o.intValue();
comment|//   }
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|expression
argument_list|,
name|Primitive
operator|.
name|ofBox
argument_list|(
name|returnType
argument_list|)
operator|.
name|primitiveName
operator|+
literal|"Value"
argument_list|)
return|;
block|}
if|if
condition|(
name|Types
operator|.
name|isPrimitive
argument_list|(
name|returnType
argument_list|)
operator|&&
operator|!
name|Types
operator|.
name|isPrimitive
argument_list|(
name|expression
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
comment|// E.g.
comment|//   int foo(Object o) {
comment|//     return (Integer) o;
comment|//   }
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|expression
argument_list|,
name|Types
operator|.
name|box
argument_list|(
name|returnType
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|expression
argument_list|,
name|returnType
argument_list|)
return|;
block|}
specifier|static
class|class
name|ParameterizedTypeImpl
implements|implements
name|ParameterizedType
block|{
specifier|private
specifier|final
name|Type
name|rawType
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Type
argument_list|>
name|typeArguments
decl_stmt|;
specifier|private
specifier|final
name|Type
name|ownerType
decl_stmt|;
name|ParameterizedTypeImpl
parameter_list|(
name|Type
name|rawType
parameter_list|,
name|List
argument_list|<
name|Type
argument_list|>
name|typeArguments
parameter_list|,
name|Type
name|ownerType
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|rawType
operator|=
name|rawType
expr_stmt|;
name|this
operator|.
name|typeArguments
operator|=
name|typeArguments
expr_stmt|;
name|this
operator|.
name|ownerType
operator|=
name|ownerType
expr_stmt|;
assert|assert
name|rawType
operator|!=
literal|null
assert|;
for|for
control|(
name|Type
name|typeArgument
range|:
name|typeArguments
control|)
block|{
assert|assert
name|typeArgument
operator|!=
literal|null
assert|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|className
argument_list|(
name|rawType
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Type
name|typeArgument
range|:
name|typeArguments
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|className
argument_list|(
name|typeArgument
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|Type
index|[]
name|getActualTypeArguments
parameter_list|()
block|{
return|return
name|typeArguments
operator|.
name|toArray
argument_list|(
operator|new
name|Type
index|[
name|typeArguments
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
name|Type
name|getRawType
parameter_list|()
block|{
return|return
name|rawType
return|;
block|}
specifier|public
name|Type
name|getOwnerType
parameter_list|()
block|{
return|return
name|ownerType
return|;
block|}
block|}
specifier|public
interface|interface
name|RecordType
block|{
name|List
argument_list|<
name|RecordField
argument_list|>
name|getRecordFields
parameter_list|()
function_decl|;
block|}
specifier|public
interface|interface
name|RecordField
block|{
name|String
name|getName
parameter_list|()
function_decl|;
name|Type
name|getType
parameter_list|()
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End Types.java
end_comment

end_unit

