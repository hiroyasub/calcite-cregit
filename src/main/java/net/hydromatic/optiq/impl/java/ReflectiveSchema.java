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
operator|.
name|java
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
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expressions
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|FunctionExpression
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|*
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link net.hydromatic.optiq.Schema} that exposes the public  * fields and methods in a Java object.  */
end_comment

begin_class
specifier|public
class|class
name|ReflectiveSchema
extends|extends
name|MapSchema
block|{
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|private
name|Object
name|target
decl_stmt|;
comment|/**      * Creates a ReflectiveSchema.      *      * @param target Object whose fields will be sub-objects      * @param typeFactory Type factory      */
specifier|public
name|ReflectiveSchema
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|Object
name|target
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|queryProvider
argument_list|,
name|typeFactory
argument_list|,
name|expression
argument_list|)
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|target
operator|.
name|getClass
argument_list|()
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|clazz
operator|.
name|getFields
argument_list|()
control|)
block|{
name|tableMap
operator|.
name|put
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldRelation
argument_list|(
name|field
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|putMulti
argument_list|(
name|membersMap
argument_list|,
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|methodMember
argument_list|(
name|method
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
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
name|ReflectiveSchema
name|schema
init|=
name|this
decl_stmt|;
specifier|final
name|Type
name|elementType
init|=
name|getElementType
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
decl_stmt|;
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
name|TableFunction
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Member {method="
operator|+
name|method
operator|+
literal|"}"
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|elementType
return|;
block|}
specifier|public
name|List
argument_list|<
name|Parameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Parameter
argument_list|>
argument_list|()
block|{
specifier|public
name|Parameter
name|get
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
operator|new
name|Parameter
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
parameter_list|()
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
name|Table
argument_list|<
name|T
argument_list|>
name|apply
parameter_list|(
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|argument
range|:
name|arguments
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
name|argument
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|schema
argument_list|,
name|arguments
operator|.
name|toArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReflectiveTable
argument_list|<
name|T
argument_list|>
argument_list|(
name|schema
argument_list|,
name|elementType
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|schema
operator|.
name|getExpression
argument_list|()
argument_list|,
name|method
argument_list|,
name|list
argument_list|)
argument_list|)
block|{
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable
init|=
name|toEnumerable
argument_list|(
name|o
argument_list|)
decl_stmt|;
return|return
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
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
block|}
return|;
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Table
argument_list|<
name|T
argument_list|>
name|fieldRelation
parameter_list|(
specifier|final
name|Field
name|field
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|Type
name|elementType
init|=
name|getElementType
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReflectiveTable
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|elementType
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|ReflectiveSchema
operator|.
name|this
operator|.
name|getExpression
argument_list|()
argument_list|,
name|field
argument_list|)
argument_list|)
block|{
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Relation {field="
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|"}"
return|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
try|try
block|{
name|Object
name|o
init|=
name|field
operator|.
name|get
argument_list|(
name|target
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable1
init|=
name|toEnumerable
argument_list|(
name|o
argument_list|)
decl_stmt|;
return|return
name|enumerable1
operator|.
name|enumerator
argument_list|()
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
literal|"Error while accessing field "
operator|+
name|field
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/** Deduces the element type of a collection;      * same logic as {@link #toEnumerable} */
specifier|private
specifier|static
name|Type
name|getElementType
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
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
name|Iterable
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
name|Object
operator|.
name|class
return|;
block|}
return|return
literal|null
return|;
comment|// not a collection/array/iterable
block|}
specifier|private
specifier|static
name|Enumerable
name|toEnumerable
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Object
index|[]
condition|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
return|;
block|}
comment|// TODO: adapter for primitive arrays, e.g. float[].
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Iterable
condition|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
operator|(
name|Iterable
operator|)
name|o
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot convert "
operator|+
name|o
operator|.
name|getClass
argument_list|()
operator|+
literal|" into a Enumerable"
argument_list|)
throw|;
block|}
specifier|private
specifier|static
specifier|abstract
class|class
name|ReflectiveTable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Extensions
operator|.
name|AbstractQueryable2
argument_list|<
name|T
argument_list|>
implements|implements
name|Table
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|ReflectiveSchema
name|schema
decl_stmt|;
specifier|public
name|ReflectiveTable
parameter_list|(
name|ReflectiveSchema
name|schema
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|schema
operator|.
name|getQueryProvider
argument_list|()
argument_list|,
name|elementType
argument_list|,
name|expression
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
specifier|public
name|DataContext
name|getDataContext
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ReflectiveSchema.java
end_comment

end_unit

