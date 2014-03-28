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
name|Table
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
name|impl
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
name|*
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
comment|/**  * Implementation of {@link net.hydromatic.optiq.Schema} that exposes the public  * fields and methods in a Java object.  */
end_comment

begin_class
specifier|public
class|class
name|ReflectiveSchema
extends|extends
name|AbstractSchema
block|{
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|private
name|Object
name|target
decl_stmt|;
comment|/**    * Creates a ReflectiveSchema.    *    * @param parentSchema Parent schema    * @param name Name    * @param target Object whose fields will be sub-objects of the schema    */
specifier|public
name|ReflectiveSchema
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|target
parameter_list|)
block|{
name|super
argument_list|(
name|parentSchema
argument_list|,
name|name
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
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReflectiveSchema(target="
operator|+
name|target
operator|+
literal|")"
return|;
block|}
comment|/** Returns the wrapped object. (May not appear to be used, but is used in    * generated code via {@link BuiltinMethod#REFLECTIVE_SCHEMA_GET_TARGET}.) */
specifier|public
name|Object
name|getTarget
parameter_list|()
block|{
return|return
name|target
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
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
specifier|final
name|String
name|fieldName
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|fieldRelation
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|builder
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|table
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
annotation|@
name|Override
specifier|protected
name|Multimap
argument_list|<
name|String
argument_list|,
name|Function
argument_list|>
name|getFunctionMultimap
parameter_list|()
block|{
specifier|final
name|ImmutableMultimap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Function
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
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|Object
operator|.
name|class
operator|||
name|methodName
operator|.
name|equals
argument_list|(
literal|"toString"
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|TableMacro
name|tableMacro
init|=
name|methodMember
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|methodName
argument_list|,
name|tableMacro
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
specifier|private
name|TableMacro
name|methodMember
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|)
block|{
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
name|MethodTableMacro
argument_list|(
name|this
argument_list|,
name|method
argument_list|,
name|elementType
argument_list|,
name|parameterTypes
argument_list|)
return|;
block|}
comment|/** Returns an expression for the object wrapped by this schema (not the    * schema itself). */
name|Expression
name|getTargetExpression
parameter_list|()
block|{
return|return
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|target
operator|.
name|getClass
argument_list|()
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Schemas
operator|.
name|unwrap
argument_list|(
name|getExpression
argument_list|()
argument_list|,
name|ReflectiveSchema
operator|.
name|class
argument_list|)
argument_list|,
name|BuiltinMethod
operator|.
name|REFLECTIVE_SCHEMA_GET_TARGET
operator|.
name|method
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a table based on a particular field of this schema. If the    * field is not of the right type to be a relation, returns null. */
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Table
name|fieldRelation
parameter_list|(
specifier|final
name|Field
name|field
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
if|if
condition|(
name|elementType
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Object
name|o
decl_stmt|;
try|try
block|{
name|o
operator|=
name|field
operator|.
name|get
argument_list|(
name|target
argument_list|)
expr_stmt|;
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
operator|new
name|FieldTable
argument_list|<
name|T
argument_list|>
argument_list|(
name|field
argument_list|,
name|elementType
argument_list|,
name|enumerable
argument_list|)
return|;
block|}
comment|/** Deduces the element type of a collection;    * same logic as {@link #toEnumerable} */
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
name|isAssignableFrom
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
specifier|final
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
else|else
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Primitive
operator|.
name|asList
argument_list|(
name|o
argument_list|)
argument_list|)
return|;
block|}
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
comment|/** Table that is implemented by reading from a Java object. */
specifier|private
specifier|static
class|class
name|ReflectiveTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|Table
block|{
specifier|private
specifier|final
name|Type
name|elementType
decl_stmt|;
specifier|private
specifier|final
name|Enumerable
name|enumerable
decl_stmt|;
specifier|public
name|ReflectiveTable
parameter_list|(
name|Type
name|elementType
parameter_list|,
name|Enumerable
name|enumerable
parameter_list|)
block|{
name|super
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
name|this
operator|.
name|enumerable
operator|=
name|enumerable
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|createType
argument_list|(
name|elementType
argument_list|)
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|UNKNOWN
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|new
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|(
name|Enumerator
argument_list|<
name|T
argument_list|>
operator|)
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
comment|/** Factory that creates a schema by instantiating an object and looking at    * its public fields.    *    *<p>The following example instantiates a {@code FoodMart} object as a schema    * that contains tables called {@code EMPS} and {@code DEPTS} based on the    * object's fields.</p>    *    *<pre>    * {@code schemas: [    *     {    *       name: "foodmart",    *       type: "custom",    *       factory: "net.hydromatic.optiq.impl.java.ReflectiveSchema$Factory",    *       operand: {    *         class: "com.acme.FoodMart",    *         staticMethod: "instance"    *       }    *     }    *   ]    *    * class FoodMart {    *   public static final FoodMart instance() {    *     return new FoodMart();    *   }    *    *   Employee[] EMPS;    *   Department[] DEPTS;    * }    * }</pre>    * */
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|SchemaFactory
block|{
specifier|public
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
name|Class
name|clazz
decl_stmt|;
name|Object
name|target
decl_stmt|;
specifier|final
name|Object
name|className
init|=
name|operand
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
operator|(
name|String
operator|)
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error loading class "
operator|+
name|className
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Operand 'class' is required"
argument_list|)
throw|;
block|}
specifier|final
name|Object
name|methodName
init|=
name|operand
operator|.
name|get
argument_list|(
literal|"staticMethod"
argument_list|)
decl_stmt|;
if|if
condition|(
name|methodName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|//noinspection unchecked
name|Method
name|method
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
operator|(
name|String
operator|)
name|methodName
argument_list|)
decl_stmt|;
name|target
operator|=
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error invoking method "
operator|+
name|methodName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|target
operator|=
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error instantiating class "
operator|+
name|className
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|ReflectiveSchema
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
name|target
argument_list|)
return|;
block|}
block|}
comment|/** Table macro based on a Java method. */
specifier|private
specifier|static
class|class
name|MethodTableMacro
implements|implements
name|TableMacro
block|{
specifier|private
specifier|final
name|ReflectiveSchema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|Method
name|method
decl_stmt|;
specifier|private
specifier|final
name|Type
name|elementType
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
decl_stmt|;
specifier|public
name|MethodTableMacro
parameter_list|(
name|ReflectiveSchema
name|schema
parameter_list|,
name|Method
name|method
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
name|this
operator|.
name|parameterTypes
operator|=
name|parameterTypes
expr_stmt|;
block|}
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
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|createType
argument_list|(
name|elementType
argument_list|)
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
name|Table
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Enumerable
name|enumerable
init|=
name|toEnumerable
argument_list|(
name|o
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReflectiveTable
argument_list|(
name|elementType
argument_list|,
name|enumerable
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
operator|(
operator|(
name|ReflectiveSchema
operator|)
name|schema
operator|)
operator|.
name|getTargetExpression
argument_list|()
argument_list|,
name|method
argument_list|,
name|list
argument_list|)
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
comment|/** Table based on a Java field. */
specifier|private
specifier|static
class|class
name|FieldTable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ReflectiveTable
block|{
specifier|private
specifier|final
name|Field
name|field
decl_stmt|;
specifier|public
name|FieldTable
parameter_list|(
name|Field
name|field
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable
parameter_list|)
block|{
name|super
argument_list|(
name|elementType
argument_list|,
name|enumerable
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
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
annotation|@
name|Override
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|field
argument_list|(
name|schema
operator|.
name|unwrap
argument_list|(
name|ReflectiveSchema
operator|.
name|class
argument_list|)
operator|.
name|getTargetExpression
argument_list|()
argument_list|,
name|field
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ReflectiveSchema.java
end_comment

end_unit

