begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
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

begin_comment
comment|/**  * Static utilities for Java reflection.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReflectUtil
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
name|Map
argument_list|<
name|Class
argument_list|,
name|Class
argument_list|>
name|primitiveToBoxingMap
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|Class
argument_list|,
name|Method
argument_list|>
name|primitiveToByteBufferReadMethod
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|Class
argument_list|,
name|Method
argument_list|>
name|primitiveToByteBufferWriteMethod
decl_stmt|;
static|static
block|{
name|primitiveToBoxingMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|Class
argument_list|>
argument_list|()
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Boolean
operator|.
name|TYPE
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Byte
operator|.
name|TYPE
argument_list|,
name|Byte
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Character
operator|.
name|TYPE
argument_list|,
name|Character
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Double
operator|.
name|TYPE
argument_list|,
name|Double
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Float
operator|.
name|TYPE
argument_list|,
name|Float
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|TYPE
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Long
operator|.
name|TYPE
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToBoxingMap
operator|.
name|put
argument_list|(
name|Short
operator|.
name|TYPE
argument_list|,
name|Short
operator|.
name|class
argument_list|)
expr_stmt|;
name|primitiveToByteBufferReadMethod
operator|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|Method
argument_list|>
argument_list|()
expr_stmt|;
name|primitiveToByteBufferWriteMethod
operator|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|Method
argument_list|>
argument_list|()
expr_stmt|;
name|Method
index|[]
name|methods
init|=
name|ByteBuffer
operator|.
name|class
operator|.
name|getDeclaredMethods
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
name|methods
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Method
name|method
init|=
name|methods
index|[
name|i
index|]
decl_stmt|;
name|Class
index|[]
name|paramTypes
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|paramTypes
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
continue|continue;
block|}
name|primitiveToByteBufferReadMethod
operator|.
name|put
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|method
argument_list|)
expr_stmt|;
comment|// special case for Boolean:  treat as byte
if|if
condition|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|equals
argument_list|(
name|Byte
operator|.
name|TYPE
argument_list|)
condition|)
block|{
name|primitiveToByteBufferReadMethod
operator|.
name|put
argument_list|(
name|Boolean
operator|.
name|TYPE
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"put"
argument_list|)
condition|)
block|{
if|if
condition|(
name|paramTypes
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|paramTypes
index|[
literal|1
index|]
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|primitiveToByteBufferWriteMethod
operator|.
name|put
argument_list|(
name|paramTypes
index|[
literal|1
index|]
argument_list|,
name|method
argument_list|)
expr_stmt|;
comment|// special case for Boolean:  treat as byte
if|if
condition|(
name|paramTypes
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|Byte
operator|.
name|TYPE
argument_list|)
condition|)
block|{
name|primitiveToByteBufferWriteMethod
operator|.
name|put
argument_list|(
name|Boolean
operator|.
name|TYPE
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Uses reflection to find the correct java.nio.ByteBuffer "absolute get"      * method for a given primitive type.      *      * @param clazz the Class object representing the primitive type      *      * @return corresponding method      */
specifier|public
specifier|static
name|Method
name|getByteBufferReadMethod
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
assert|assert
operator|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
operator|)
assert|;
return|return
name|primitiveToByteBufferReadMethod
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
return|;
block|}
comment|/**      * Uses reflection to find the correct java.nio.ByteBuffer "absolute put"      * method for a given primitive type.      *      * @param clazz the Class object representing the primitive type      *      * @return corresponding method      */
specifier|public
specifier|static
name|Method
name|getByteBufferWriteMethod
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
assert|assert
operator|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
operator|)
assert|;
return|return
name|primitiveToByteBufferWriteMethod
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
return|;
block|}
comment|/**      * Gets the Java boxing class for a primitive class.      *      * @param primitiveClass representative class for primitive (e.g.      * java.lang.Integer.TYPE)      *      * @return corresponding boxing Class (e.g. java.lang.Integer)      */
specifier|public
specifier|static
name|Class
name|getBoxingClass
parameter_list|(
name|Class
name|primitiveClass
parameter_list|)
block|{
assert|assert
operator|(
name|primitiveClass
operator|.
name|isPrimitive
argument_list|()
operator|)
assert|;
return|return
name|primitiveToBoxingMap
operator|.
name|get
argument_list|(
name|primitiveClass
argument_list|)
return|;
block|}
comment|/**      * Gets the name of a class with no package qualifiers; if it's an inner      * class, it will still be qualified by the containing class (X$Y).      *      * @param c the class of interest      *      * @return the unqualified name      */
specifier|public
specifier|static
name|String
name|getUnqualifiedClassName
parameter_list|(
name|Class
name|c
parameter_list|)
block|{
name|String
name|className
init|=
name|c
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|lastDot
init|=
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDot
operator|<
literal|0
condition|)
block|{
return|return
name|className
return|;
block|}
return|return
name|className
operator|.
name|substring
argument_list|(
name|lastDot
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**      * Composes a string representing a human-readable method name (with neither      * exception nor return type information).      *      * @param declaringClass class on which method is defined      * @param methodName simple name of method without signature      * @param paramTypes method parameter types      *      * @return unmangled method name      */
specifier|public
specifier|static
name|String
name|getUnmangledMethodName
parameter_list|(
name|Class
name|declaringClass
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
index|[]
name|paramTypes
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|declaringClass
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paramTypes
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|paramTypes
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Composes a string representing a human-readable method name (with neither      * exception nor return type information).      *      * @param method method whose name is to be generated      *      * @return unmangled method name      */
specifier|public
specifier|static
name|String
name|getUnmangledMethodName
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
return|return
name|getUnmangledMethodName
argument_list|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
argument_list|,
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Implements the {@link Glossary#VisitorPattern} via reflection. The basic      * technique is taken from<a      * href="http://www.javaworld.com/javaworld/javatips/jw-javatip98.html">a      * Javaworld article</a>. For an example of how to use it, see {@code      * ReflectVisitorTest}. Visit method lookup follows the same rules as if      * compile-time resolution for VisitorClass.visit(VisiteeClass) were      * performed. An ambiguous match due to multiple interface inheritance      * results in an IllegalArgumentException. A non-match is indicated by      * returning false.      *      * @param visitor object whose visit method is to be invoked      * @param visitee object to be passed as a parameter to the visit method      * @param hierarchyRoot if non-null, visitor method will only be invoked if      * it takes a parameter whose type is a subtype of hierarchyRoot      * @param visitMethodName name of visit method, e.g. "visit"      *      * @return true if a matching visit method was found and invoked      */
specifier|public
specifier|static
name|boolean
name|invokeVisitor
parameter_list|(
name|ReflectiveVisitor
name|visitor
parameter_list|,
name|Object
name|visitee
parameter_list|,
name|Class
name|hierarchyRoot
parameter_list|,
name|String
name|visitMethodName
parameter_list|)
block|{
return|return
name|invokeVisitorInternal
argument_list|(
name|visitor
argument_list|,
name|visitee
argument_list|,
name|hierarchyRoot
argument_list|,
name|visitMethodName
argument_list|)
return|;
block|}
comment|/**      * Shared implementation of the two forms of invokeVisitor.      *      * @param visitor object whose visit method is to be invoked      * @param visitee object to be passed as a parameter to the visit method      * @param hierarchyRoot if non-null, visitor method will only be invoked if      * it takes a parameter whose type is a subtype of hierarchyRoot      * @param visitMethodName name of visit method, e.g. "visit"      *      * @return true if a matching visit method was found and invoked      */
specifier|private
specifier|static
name|boolean
name|invokeVisitorInternal
parameter_list|(
name|Object
name|visitor
parameter_list|,
name|Object
name|visitee
parameter_list|,
name|Class
name|hierarchyRoot
parameter_list|,
name|String
name|visitMethodName
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|visitorClass
init|=
name|visitor
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Class
name|visiteeClass
init|=
name|visitee
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Method
name|method
init|=
name|lookupVisitMethod
argument_list|(
name|visitorClass
argument_list|,
name|visiteeClass
argument_list|,
name|visitMethodName
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
literal|false
return|;
block|}
if|if
condition|(
name|hierarchyRoot
operator|!=
literal|null
condition|)
block|{
name|Class
name|paramType
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|hierarchyRoot
operator|.
name|isAssignableFrom
argument_list|(
name|paramType
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
try|try
block|{
name|method
operator|.
name|invoke
argument_list|(
name|visitor
argument_list|,
name|visitee
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|ex
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ex
parameter_list|)
block|{
comment|// visit methods aren't allowed to have throws clauses,
comment|// so the only exceptions which should come
comment|// to us are RuntimeExceptions and Errors
name|Throwable
name|t
init|=
name|ex
operator|.
name|getTargetException
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|t
throw|;
block|}
if|else if
condition|(
name|t
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|t
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|t
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Looks up a visit method.      *      * @param visitorClass class of object whose visit method is to be invoked      * @param visiteeClass class of object to be passed as a parameter to the      * visit method      * @param visitMethodName name of visit method      *      * @return method found, or null if none found      */
specifier|public
specifier|static
name|Method
name|lookupVisitMethod
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|visitorClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|visiteeClass
parameter_list|,
name|String
name|visitMethodName
parameter_list|)
block|{
return|return
name|lookupVisitMethod
argument_list|(
name|visitorClass
argument_list|,
name|visiteeClass
argument_list|,
name|visitMethodName
argument_list|,
name|Collections
operator|.
expr|<
name|Class
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Looks up a visit method taking additional parameters beyond the      * overloaded visitee type.      *      * @param visitorClass class of object whose visit method is to be invoked      * @param visiteeClass class of object to be passed as a parameter to the      * visit method      * @param visitMethodName name of visit method      * @param additionalParameterTypes list of additional parameter types      *      * @return method found, or null if none found      *      * @see #createDispatcher(Class,Class)      */
specifier|public
specifier|static
name|Method
name|lookupVisitMethod
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|visitorClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|visiteeClass
parameter_list|,
name|String
name|visitMethodName
parameter_list|,
name|List
argument_list|<
name|Class
argument_list|>
name|additionalParameterTypes
parameter_list|)
block|{
comment|// Prepare an array to re-use in recursive calls.  The first argument
comment|// will have the visitee class substituted into it.
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|paramTypes
init|=
operator|new
name|Class
index|[
literal|1
operator|+
name|additionalParameterTypes
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|iParam
init|=
literal|0
decl_stmt|;
name|paramTypes
index|[
name|iParam
operator|++
index|]
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|paramType
range|:
name|additionalParameterTypes
control|)
block|{
name|paramTypes
index|[
name|iParam
operator|++
index|]
operator|=
name|paramType
expr_stmt|;
block|}
comment|// Cache Class to candidate Methods, to optimize the case where
comment|// the original visiteeClass has a diamond-shaped interface inheritance
comment|// graph. (This is common, for example, in JMI.) The idea is to avoid
comment|// iterating over a single interface's method more than once in a call.
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Method
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|lookupVisitMethod
argument_list|(
name|visitorClass
argument_list|,
name|visiteeClass
argument_list|,
name|visitMethodName
argument_list|,
name|paramTypes
argument_list|,
name|cache
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Method
name|lookupVisitMethod
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|visitorClass
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|visiteeClass
parameter_list|,
specifier|final
name|String
name|visitMethodName
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|paramTypes
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Method
argument_list|>
name|cache
parameter_list|)
block|{
comment|// Use containsKey since the result for a Class might be null.
if|if
condition|(
name|cache
operator|.
name|containsKey
argument_list|(
name|visiteeClass
argument_list|)
condition|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|visiteeClass
argument_list|)
return|;
block|}
name|Method
name|candidateMethod
init|=
literal|null
decl_stmt|;
name|paramTypes
index|[
literal|0
index|]
operator|=
name|visiteeClass
expr_stmt|;
try|try
block|{
name|candidateMethod
operator|=
name|visitorClass
operator|.
name|getMethod
argument_list|(
name|visitMethodName
argument_list|,
name|paramTypes
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|visiteeClass
argument_list|,
name|candidateMethod
argument_list|)
expr_stmt|;
return|return
name|candidateMethod
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
comment|// not found:  carry on with lookup
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|superClass
init|=
name|visiteeClass
operator|.
name|getSuperclass
argument_list|()
decl_stmt|;
if|if
condition|(
name|superClass
operator|!=
literal|null
condition|)
block|{
name|candidateMethod
operator|=
name|lookupVisitMethod
argument_list|(
name|visitorClass
argument_list|,
name|superClass
argument_list|,
name|visitMethodName
argument_list|,
name|paramTypes
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|interfaces
init|=
name|visiteeClass
operator|.
name|getInterfaces
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
name|interfaces
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Method
name|method
init|=
name|lookupVisitMethod
argument_list|(
name|visitorClass
argument_list|,
name|interfaces
index|[
name|i
index|]
argument_list|,
name|visitMethodName
argument_list|,
name|paramTypes
argument_list|,
name|cache
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
name|candidateMethod
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|method
operator|.
name|equals
argument_list|(
name|candidateMethod
argument_list|)
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|c1
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|c2
init|=
name|candidateMethod
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|c1
operator|.
name|isAssignableFrom
argument_list|(
name|c2
argument_list|)
condition|)
block|{
comment|// c2 inherits from c1, so keep candidateMethod
comment|// (which is more specific than method)
continue|continue;
block|}
if|else if
condition|(
name|c2
operator|.
name|isAssignableFrom
argument_list|(
name|c1
argument_list|)
condition|)
block|{
comment|// c1 inherits from c2 (method is more specific
comment|// than candidate method), so fall through
comment|// to set candidateMethod = method
block|}
else|else
block|{
comment|// c1 and c2 are not directly related
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dispatch ambiguity between "
operator|+
name|candidateMethod
operator|+
literal|" and "
operator|+
name|method
argument_list|)
throw|;
block|}
block|}
block|}
name|candidateMethod
operator|=
name|method
expr_stmt|;
block|}
block|}
name|cache
operator|.
name|put
argument_list|(
name|visiteeClass
argument_list|,
name|candidateMethod
argument_list|)
expr_stmt|;
return|return
name|candidateMethod
return|;
block|}
comment|/**      * Creates a dispatcher for calls to {@link #lookupVisitMethod}. The      * dispatcher caches methods between invocations.      *      * @param visitorBaseClazz Visitor base class      * @param visiteeBaseClazz Visitee base class      *      * @return cache of methods      */
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|ReflectiveVisitor
parameter_list|,
name|E
parameter_list|>
name|ReflectiveVisitDispatcher
argument_list|<
name|R
argument_list|,
name|E
argument_list|>
name|createDispatcher
parameter_list|(
specifier|final
name|Class
argument_list|<
name|R
argument_list|>
name|visitorBaseClazz
parameter_list|,
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|visiteeBaseClazz
parameter_list|)
block|{
assert|assert
name|ReflectiveVisitor
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|visitorBaseClazz
argument_list|)
assert|;
assert|assert
name|Object
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|visiteeBaseClazz
argument_list|)
assert|;
return|return
operator|new
name|ReflectiveVisitDispatcher
argument_list|<
name|R
argument_list|,
name|E
argument_list|>
argument_list|()
block|{
specifier|final
name|Map
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|,
name|Method
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Method
name|lookupVisitMethod
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|R
argument_list|>
name|visitorClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|visiteeClass
parameter_list|,
name|String
name|visitMethodName
parameter_list|)
block|{
return|return
name|lookupVisitMethod
argument_list|(
name|visitorClass
argument_list|,
name|visiteeClass
argument_list|,
name|visitMethodName
argument_list|,
name|Collections
operator|.
expr|<
name|Class
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Method
name|lookupVisitMethod
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|R
argument_list|>
name|visitorClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|visiteeClass
parameter_list|,
name|String
name|visitMethodName
parameter_list|,
name|List
argument_list|<
name|Class
argument_list|>
name|additionalParameterTypes
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|key
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|visitorClass
argument_list|,
name|visiteeClass
argument_list|,
name|visitMethodName
argument_list|,
name|additionalParameterTypes
argument_list|)
decl_stmt|;
name|Method
name|method
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
comment|// We already looked for the method and found nothing.
block|}
else|else
block|{
name|method
operator|=
name|ReflectUtil
operator|.
name|lookupVisitMethod
argument_list|(
name|visitorClass
argument_list|,
name|visiteeClass
argument_list|,
name|visitMethodName
argument_list|,
name|additionalParameterTypes
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|method
return|;
block|}
specifier|public
name|boolean
name|invokeVisitor
parameter_list|(
name|R
name|visitor
parameter_list|,
name|E
name|visitee
parameter_list|,
name|String
name|visitMethodName
parameter_list|)
block|{
return|return
name|ReflectUtil
operator|.
name|invokeVisitor
argument_list|(
name|visitor
argument_list|,
name|visitee
argument_list|,
name|visiteeBaseClazz
argument_list|,
name|visitMethodName
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Creates a dispatcher for calls to a single multi-method on a particular      * object.      *      *<p>Calls to that multi-method are resolved by looking for a method on      * the runtime type of that object, with the required name, and with      * the correct type or a subclass for the first argument, and precisely the      * same types for other arguments.      *      *<p>For instance, a dispatcher created for the method      *      *<blockquote>String foo(Vehicle, int, List)</blockquote>      *      * could be used to call the methods      *      *<blockquote>String foo(Car, int, List)<br/>      * String foo(Bus, int, List)</blockquote>      *      * (because Car and Bus are subclasses of Vehicle, and they occur in the      * polymorphic first argument) but not the method      *      *<blockquote>String foo(Car, int, ArrayList)</blockquote>      *      * (only the first argument is polymorphic).      *      *<p>You must create an implementation of the method for the base class.      * Otherwise throws {@link IllegalArgumentException}.      *      * @param returnClazz Return type of method      * @param visitor Object on which to invoke the method      * @param methodName Name of method      * @param arg0Clazz Base type of argument zero      * @param otherArgClasses Types of remaining arguments      */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|,
name|T
parameter_list|>
name|MethodDispatcher
argument_list|<
name|T
argument_list|>
name|createMethodDispatcher
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|returnClazz
parameter_list|,
specifier|final
name|ReflectiveVisitor
name|visitor
parameter_list|,
specifier|final
name|String
name|methodName
parameter_list|,
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|arg0Clazz
parameter_list|,
specifier|final
name|Class
modifier|...
name|otherArgClasses
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Class
argument_list|>
name|otherArgClassList
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|otherArgClasses
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
specifier|final
name|ReflectiveVisitDispatcher
argument_list|<
name|ReflectiveVisitor
argument_list|,
name|E
argument_list|>
name|dispatcher
init|=
name|createDispatcher
argument_list|(
operator|(
name|Class
argument_list|<
name|ReflectiveVisitor
argument_list|>
operator|)
name|visitor
operator|.
name|getClass
argument_list|()
argument_list|,
name|arg0Clazz
argument_list|)
decl_stmt|;
return|return
operator|new
name|MethodDispatcher
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|T
name|invoke
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|Method
name|method
init|=
name|lookupMethod
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Object
name|o
init|=
name|method
operator|.
name|invoke
argument_list|(
name|visitor
argument_list|,
name|args
argument_list|)
decl_stmt|;
return|return
name|returnClazz
operator|.
name|cast
argument_list|(
name|o
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
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"While invoking method '"
operator|+
name|method
operator|+
literal|"'"
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
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"While invoking method '"
operator|+
name|method
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Method
name|lookupMethod
parameter_list|(
specifier|final
name|Object
name|arg0
parameter_list|)
block|{
if|if
condition|(
operator|!
name|arg0Clazz
operator|.
name|isInstance
argument_list|(
name|arg0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
name|Method
name|method
init|=
name|dispatcher
operator|.
name|lookupVisitMethod
argument_list|(
name|visitor
operator|.
name|getClass
argument_list|()
argument_list|,
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|E
argument_list|>
operator|)
name|arg0
operator|.
name|getClass
argument_list|()
argument_list|,
name|methodName
argument_list|,
name|otherArgClassList
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|Class
argument_list|>
name|classList
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
name|classList
operator|.
name|add
argument_list|(
name|arg0Clazz
argument_list|)
expr_stmt|;
name|classList
operator|.
name|addAll
argument_list|(
name|otherArgClassList
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Method not found: "
operator|+
name|methodName
operator|+
literal|"("
operator|+
name|classList
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|method
return|;
block|}
block|}
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Can invoke a method on an object of type E with return type T.      *      * @param<T> Return type of method      */
specifier|public
interface|interface
name|MethodDispatcher
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**          * Invokes method on an object with a given set of arguments.          *          * @param args Arguments to method          * @return Return value of method          */
name|T
name|invoke
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End ReflectUtil.java
end_comment

end_unit

