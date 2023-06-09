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
name|adapter
operator|.
name|java
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
name|DataContext
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumUtils
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|QueryProvider
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
name|linq4j
operator|.
name|Queryable
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|linq4j
operator|.
name|tree
operator|.
name|Primitive
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
name|RelReferentialConstraint
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
name|ScannableTable
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
name|Schema
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
name|SchemaFactory
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
name|SchemaPlus
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
name|Schemas
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
name|Statistic
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
name|Statistics
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
name|Table
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
name|TableMacro
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
name|TranslatableTable
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
name|impl
operator|.
name|AbstractSchema
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
name|impl
operator|.
name|AbstractTableQueryable
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
name|impl
operator|.
name|ReflectiveFunctionBase
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
name|BuiltInMethod
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
name|Util
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
name|ImmutableMultimap
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
name|Iterables
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
name|Multimap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|MonotonicNonNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|Collections
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.schema.Schema} that exposes the  * public fields and methods in a Java object.  */
end_comment

begin_class
specifier|public
class|class
name|ReflectiveSchema
extends|extends
name|AbstractSchema
block|{
specifier|private
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|private
specifier|final
name|Object
name|target
decl_stmt|;
specifier|private
annotation|@
name|MonotonicNonNull
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|tableMap
decl_stmt|;
specifier|private
annotation|@
name|MonotonicNonNull
name|Multimap
argument_list|<
name|String
argument_list|,
name|Function
argument_list|>
name|functionMap
decl_stmt|;
comment|/**    * Creates a ReflectiveSchema.    *    * @param target Object whose fields will be sub-objects of the schema    */
specifier|public
name|ReflectiveSchema
parameter_list|(
name|Object
name|target
parameter_list|)
block|{
name|super
argument_list|()
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
comment|/** Returns the wrapped object.    *    *<p>May not appear to be used, but is used in generated code via    * {@link org.apache.calcite.util.BuiltInMethod#REFLECTIVE_SCHEMA_GET_TARGET}.    */
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
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
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
if|if
condition|(
name|tableMap
operator|==
literal|null
condition|)
block|{
name|tableMap
operator|=
name|createTableMap
argument_list|()
expr_stmt|;
block|}
return|return
name|tableMap
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|createTableMap
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
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|tableMap
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Unique-Key - Foreign-Key
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
if|if
condition|(
name|RelReferentialConstraint
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|RelReferentialConstraint
name|rc
decl_stmt|;
try|try
block|{
name|rc
operator|=
operator|(
name|RelReferentialConstraint
operator|)
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
name|requireNonNull
argument_list|(
name|rc
argument_list|,
parameter_list|()
lambda|->
literal|"field must not be null: "
operator|+
name|field
argument_list|)
expr_stmt|;
name|FieldTable
name|table
init|=
operator|(
name|FieldTable
operator|)
name|tableMap
operator|.
name|get
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|rc
operator|.
name|getSourceQualifiedName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|table
operator|!=
literal|null
assert|;
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|referentialConstraints
init|=
name|table
operator|.
name|getStatistic
argument_list|()
operator|.
name|getReferentialConstraints
argument_list|()
decl_stmt|;
if|if
condition|(
name|referentialConstraints
operator|==
literal|null
condition|)
block|{
comment|// This enables to keep the same Statistics.of below
name|referentialConstraints
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|statistic
operator|=
name|Statistics
operator|.
name|of
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|referentialConstraints
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|rc
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tableMap
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
if|if
condition|(
name|functionMap
operator|==
literal|null
condition|)
block|{
name|functionMap
operator|=
name|createFunctionMap
argument_list|()
expr_stmt|;
block|}
return|return
name|functionMap
return|;
block|}
specifier|private
name|Multimap
argument_list|<
name|String
argument_list|,
name|Function
argument_list|>
name|createFunctionMap
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
if|if
condition|(
name|TranslatableTable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|TableMacro
name|tableMacro
init|=
operator|new
name|MethodTableMacro
argument_list|(
name|this
argument_list|,
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
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Returns an expression for the object wrapped by this schema (not the    * schema itself). */
name|Expression
name|getTargetExpression
parameter_list|(
annotation|@
name|Nullable
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|EnumUtils
operator|.
name|convert
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|Schemas
operator|.
name|unwrap
argument_list|(
name|getExpression
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|)
argument_list|,
name|ReflectiveSchema
operator|.
name|class
argument_list|)
argument_list|,
name|BuiltInMethod
operator|.
name|REFLECTIVE_SCHEMA_GET_TARGET
operator|.
name|method
argument_list|)
argument_list|,
name|target
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns a table based on a particular field of this schema. If the    * field is not of the right type to be a relation, returns null. */
specifier|private
parameter_list|<
name|T
parameter_list|>
annotation|@
name|Nullable
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
name|requireNonNull
argument_list|(
name|o
argument_list|,
parameter_list|()
lambda|->
literal|"field "
operator|+
name|field
operator|+
literal|" is null for "
operator|+
name|target
argument_list|)
expr_stmt|;
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
argument_list|<>
argument_list|(
name|field
argument_list|,
name|elementType
argument_list|,
name|enumerable
argument_list|)
return|;
block|}
comment|/** Deduces the element type of a collection;    * same logic as {@link #toEnumerable}. */
specifier|private
specifier|static
annotation|@
name|Nullable
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
implements|,
name|ScannableTable
block|{
specifier|private
specifier|final
name|Enumerable
name|enumerable
decl_stmt|;
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
name|enumerable
operator|=
name|enumerable
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
if|if
condition|(
name|elementType
operator|==
name|Object
index|[]
operator|.
name|class
condition|)
block|{
comment|//noinspection unchecked
return|return
name|enumerable
return|;
block|}
else|else
block|{
comment|//noinspection unchecked
return|return
name|enumerable
operator|.
name|select
argument_list|(
operator|new
name|FieldSelector
argument_list|(
operator|(
name|Class
operator|)
name|elementType
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
comment|/** Factory that creates a schema by instantiating an object and looking at    * its public fields.    *    *<p>The following example instantiates a {@code FoodMart} object as a schema    * that contains tables called {@code EMPS} and {@code DEPTS} based on the    * object's fields.    *    *<blockquote><pre>    * schemas: [    *     {    *       name: "foodmart",    *       type: "custom",    *       factory: "org.apache.calcite.adapter.java.ReflectiveSchema$Factory",    *       operand: {    *         class: "com.acme.FoodMart",    *         staticMethod: "instance"    *       }    *     }    *   ]    *&nbsp;    * class FoodMart {    *   public static final FoodMart instance() {    *     return new FoodMart();    *   }    *&nbsp;    *   Employee[] EMPS;    *   Department[] DEPTS;    * }</pre></blockquote>    */
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|SchemaFactory
block|{
annotation|@
name|Override
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
argument_list|<
name|?
argument_list|>
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
name|requireNonNull
argument_list|(
name|target
argument_list|,
parameter_list|()
lambda|->
literal|"method "
operator|+
name|method
operator|+
literal|" returns null"
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
specifier|final
name|Constructor
argument_list|<
name|?
argument_list|>
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
name|target
operator|=
name|constructor
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
extends|extends
name|ReflectiveFunctionBase
implements|implements
name|TableMacro
block|{
specifier|private
specifier|final
name|ReflectiveSchema
name|schema
decl_stmt|;
name|MethodTableMacro
parameter_list|(
name|ReflectiveSchema
name|schema
parameter_list|,
name|Method
name|method
parameter_list|)
block|{
name|super
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
assert|assert
name|TranslatableTable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
operator|:
literal|"Method should return TranslatableTable so the macro can be "
operator|+
literal|"expanded"
assert|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|TranslatableTable
name|apply
argument_list|(
name|final
name|List
operator|<
condition|?
then|extends @
name|Nullable
name|Object
operator|>
name|arguments
argument_list|)
block|{
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
operator|.
name|getTarget
argument_list|()
argument_list|,
name|arguments
operator|.
name|toArray
argument_list|()
argument_list|)
decl_stmt|;
name|requireNonNull
argument_list|(
name|o
argument_list|,
parameter_list|()
lambda|->
literal|"method "
operator|+
name|method
operator|+
literal|" returned null for arguments "
operator|+
name|arguments
argument_list|)
expr_stmt|;
return|return
operator|(
name|TranslatableTable
operator|)
name|o
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
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
comment|/** Table based on a Java field.    *    * @param<T> element type */
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
specifier|private
name|Statistic
name|statistic
decl_stmt|;
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
name|this
argument_list|(
name|field
argument_list|,
name|elementType
argument_list|,
name|enumerable
argument_list|,
name|Statistics
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
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
parameter_list|,
name|Statistic
name|statistic
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
name|this
operator|.
name|statistic
operator|=
name|statistic
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
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|statistic
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
name|ReflectiveSchema
name|reflectiveSchema
init|=
name|requireNonNull
argument_list|(
name|schema
operator|.
name|unwrap
argument_list|(
name|ReflectiveSchema
operator|.
name|class
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"schema.unwrap(ReflectiveSchema.class) for "
operator|+
name|schema
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|field
argument_list|(
name|reflectiveSchema
operator|.
name|getTargetExpression
argument_list|(
name|schema
operator|.
name|getParentSchema
argument_list|()
argument_list|,
name|schema
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|field
argument_list|)
return|;
block|}
block|}
comment|/** Function that returns an array of a given object's field values. */
specifier|private
specifier|static
class|class
name|FieldSelector
implements|implements
name|Function1
argument_list|<
name|Object
argument_list|,
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|Field
index|[]
name|fields
decl_stmt|;
name|FieldSelector
parameter_list|(
name|Class
name|elementType
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|elementType
operator|.
name|getFields
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|Object
index|[]
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
try|try
block|{
specifier|final
annotation|@
name|Nullable
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|fields
operator|.
name|length
index|]
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|objects
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
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
block|}
block|}
block|}
end_class

end_unit

