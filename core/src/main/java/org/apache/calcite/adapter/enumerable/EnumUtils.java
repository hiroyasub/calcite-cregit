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
name|enumerable
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
name|adapter
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
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Ord
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
name|Function2
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
name|BlockStatement
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
name|ConstantUntypedNull
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
name|MethodDeclaration
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
name|ParameterExpression
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
name|core
operator|.
name|JoinRelType
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
name|RelDataTypeField
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
name|rex
operator|.
name|RexNode
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Lists
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
name|Type
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
comment|/**  * Utilities for generating programs in the Enumerable (functional)  * style.  */
end_comment

begin_class
specifier|public
class|class
name|EnumUtils
block|{
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|RexNode
argument_list|,
name|Type
argument_list|>
name|REX_TO_INTERNAL_TYPE
init|=
operator|new
name|Function
argument_list|<
name|RexNode
argument_list|,
name|Type
argument_list|>
argument_list|()
block|{
specifier|public
name|Type
name|apply
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|toInternal
argument_list|(
name|node
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|EnumUtils
parameter_list|()
block|{
block|}
specifier|static
specifier|final
name|boolean
name|BRIDGE_METHODS
init|=
literal|true
decl_stmt|;
specifier|static
specifier|final
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|NO_PARAMS
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|static
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|NO_EXPRS
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|LEFT_RIGHT
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"left"
argument_list|,
literal|"right"
argument_list|)
decl_stmt|;
comment|/** Declares a method that overrides another method. */
specifier|public
specifier|static
name|MethodDeclaration
name|overridingMethodDecl
parameter_list|(
name|Method
name|method
parameter_list|,
name|Iterable
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
parameter_list|,
name|BlockStatement
name|body
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
operator|&
operator|~
name|Modifier
operator|.
name|ABSTRACT
argument_list|,
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|parameters
argument_list|,
name|body
argument_list|)
return|;
block|}
specifier|static
name|Type
name|javaClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
specifier|final
name|Type
name|clazz
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|clazz
operator|instanceof
name|Class
condition|?
name|clazz
else|:
name|Object
index|[]
operator|.
name|class
return|;
block|}
specifier|static
name|Class
name|javaRowClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|isStruct
argument_list|()
operator|&&
name|type
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|type
operator|=
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Type
name|clazz
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|clazz
operator|instanceof
name|Class
condition|?
operator|(
name|Class
operator|)
name|clazz
else|:
name|Object
index|[]
operator|.
name|class
return|;
block|}
specifier|static
name|List
argument_list|<
name|Type
argument_list|>
name|fieldTypes
parameter_list|(
specifier|final
name|JavaTypeFactory
name|typeFactory
parameter_list|,
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|RelDataType
argument_list|>
name|inputTypes
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Type
argument_list|>
argument_list|()
block|{
specifier|public
name|Type
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|EnumUtils
operator|.
name|javaClass
argument_list|(
name|typeFactory
argument_list|,
name|inputTypes
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|inputTypes
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|static
name|List
argument_list|<
name|RelDataType
argument_list|>
name|fieldRowTypes
parameter_list|(
specifier|final
name|RelDataType
name|inputRowType
parameter_list|,
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|extraInputs
parameter_list|,
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|inputFields
init|=
name|inputRowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
block|{
specifier|public
name|RelDataType
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|int
name|arg
init|=
name|argList
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|arg
operator|<
name|inputFields
operator|.
name|size
argument_list|()
condition|?
name|inputFields
operator|.
name|get
argument_list|(
name|arg
argument_list|)
operator|.
name|getType
argument_list|()
else|:
name|extraInputs
operator|.
name|get
argument_list|(
name|arg
operator|-
name|inputFields
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|argList
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|static
name|Expression
name|joinSelector
parameter_list|(
name|JoinRelType
name|joinType
parameter_list|,
name|PhysType
name|physType
parameter_list|,
name|List
argument_list|<
name|PhysType
argument_list|>
name|inputPhysTypes
parameter_list|)
block|{
comment|// A parameter for each input.
specifier|final
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Generate all fields.
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|outputFieldCount
init|=
name|physType
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|PhysType
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|inputPhysTypes
argument_list|)
control|)
block|{
specifier|final
name|PhysType
name|inputPhysType
init|=
name|ord
operator|.
name|e
operator|.
name|makeNullable
argument_list|(
name|joinType
operator|.
name|generatesNullsOn
argument_list|(
name|ord
operator|.
name|i
argument_list|)
argument_list|)
decl_stmt|;
comment|// If input item is just a primitive, we do not generate specialized
comment|// primitive apply override since it won't be called anyway
comment|// Function<T> always operates on boxed arguments
specifier|final
name|ParameterExpression
name|parameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Primitive
operator|.
name|box
argument_list|(
name|inputPhysType
operator|.
name|getJavaRowType
argument_list|()
argument_list|)
argument_list|,
name|EnumUtils
operator|.
name|LEFT_RIGHT
operator|.
name|get
argument_list|(
name|ord
operator|.
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|parameters
operator|.
name|add
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
if|if
condition|(
name|expressions
operator|.
name|size
argument_list|()
operator|==
name|outputFieldCount
condition|)
block|{
comment|// For instance, if semi-join needs to return just the left inputs
break|break;
block|}
specifier|final
name|int
name|fieldCount
init|=
name|inputPhysType
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|expression
init|=
name|inputPhysType
operator|.
name|fieldReference
argument_list|(
name|parameter
argument_list|,
name|i
argument_list|,
name|physType
operator|.
name|getJavaFieldType
argument_list|(
name|expressions
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|joinType
operator|.
name|generatesNullsOn
argument_list|(
name|ord
operator|.
name|i
argument_list|)
condition|)
block|{
name|expression
operator|=
name|Expressions
operator|.
name|condition
argument_list|(
name|Expressions
operator|.
name|equal
argument_list|(
name|parameter
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|)
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
name|expressions
operator|.
name|add
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function2
operator|.
name|class
argument_list|,
name|physType
operator|.
name|record
argument_list|(
name|expressions
argument_list|)
argument_list|,
name|parameters
argument_list|)
return|;
block|}
comment|/** Converts from internal representation to JDBC representation used by    * arguments of user-defined functions. For example, converts date values from    * {@code int} to {@link java.sql.Date}. */
specifier|static
name|Expression
name|fromInternal
parameter_list|(
name|Expression
name|e
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|targetType
parameter_list|)
block|{
if|if
condition|(
name|e
operator|==
name|ConstantUntypedNull
operator|.
name|INSTANCE
condition|)
block|{
return|return
name|e
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|e
operator|.
name|getType
argument_list|()
operator|instanceof
name|Class
operator|)
condition|)
block|{
return|return
name|e
return|;
block|}
if|if
condition|(
name|targetType
operator|.
name|isAssignableFrom
argument_list|(
operator|(
name|Class
operator|)
name|e
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|e
return|;
block|}
if|if
condition|(
name|targetType
operator|==
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|INTERNAL_TO_DATE
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
if|if
condition|(
name|targetType
operator|==
name|java
operator|.
name|sql
operator|.
name|Time
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|INTERNAL_TO_TIME
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
if|if
condition|(
name|targetType
operator|==
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|INTERNAL_TO_TIMESTAMP
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
if|if
condition|(
name|Primitive
operator|.
name|is
argument_list|(
name|e
operator|.
name|type
argument_list|)
operator|&&
name|Primitive
operator|.
name|isBox
argument_list|(
name|targetType
argument_list|)
condition|)
block|{
comment|// E.g. e is "int", target is "Long", generate "(long) e".
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|e
argument_list|,
name|Primitive
operator|.
name|ofBox
argument_list|(
name|targetType
argument_list|)
operator|.
name|primitiveClass
argument_list|)
return|;
block|}
return|return
name|e
return|;
block|}
specifier|static
name|List
argument_list|<
name|Expression
argument_list|>
name|fromInternal
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|targetTypes
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
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
argument_list|<>
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
name|expressions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|fromInternal
argument_list|(
name|expressions
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|targetTypes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|static
name|Type
name|fromInternal
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|class
operator|||
name|type
operator|==
name|java
operator|.
name|sql
operator|.
name|Time
operator|.
name|class
condition|)
block|{
return|return
name|int
operator|.
name|class
return|;
block|}
if|if
condition|(
name|type
operator|==
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|.
name|class
condition|)
block|{
return|return
name|long
operator|.
name|class
return|;
block|}
return|return
name|type
return|;
block|}
specifier|static
name|Type
name|toInternal
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|DATE
case|:
case|case
name|TIME
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Integer
operator|.
name|class
else|:
name|int
operator|.
name|class
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Long
operator|.
name|class
else|:
name|long
operator|.
name|class
return|;
default|default:
return|return
literal|null
return|;
comment|// we don't care; use the default storage type
block|}
block|}
specifier|static
name|List
argument_list|<
name|Type
argument_list|>
name|internalTypes
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|operandList
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|transform
argument_list|(
name|operandList
argument_list|,
name|REX_TO_INTERNAL_TYPE
argument_list|)
return|;
block|}
specifier|static
name|Expression
name|enforce
parameter_list|(
specifier|final
name|Type
name|storageType
parameter_list|,
specifier|final
name|Expression
name|e
parameter_list|)
block|{
if|if
condition|(
name|storageType
operator|!=
literal|null
operator|&&
name|e
operator|.
name|type
operator|!=
name|storageType
condition|)
block|{
if|if
condition|(
name|e
operator|.
name|type
operator|==
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|class
condition|)
block|{
if|if
condition|(
name|storageType
operator|==
name|int
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|DATE_TO_INT
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
if|if
condition|(
name|storageType
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|DATE_TO_INT_OPTIONAL
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
if|else if
condition|(
name|e
operator|.
name|type
operator|==
name|java
operator|.
name|sql
operator|.
name|Time
operator|.
name|class
condition|)
block|{
if|if
condition|(
name|storageType
operator|==
name|int
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|TIME_TO_INT
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
if|if
condition|(
name|storageType
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|TIME_TO_INT_OPTIONAL
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
if|else if
condition|(
name|e
operator|.
name|type
operator|==
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|.
name|class
condition|)
block|{
if|if
condition|(
name|storageType
operator|==
name|long
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|TIMESTAMP_TO_LONG
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
if|if
condition|(
name|storageType
operator|==
name|Long
operator|.
name|class
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|TIMESTAMP_TO_LONG_OPTIONAL
operator|.
name|method
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|e
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumUtils.java
end_comment

end_unit

