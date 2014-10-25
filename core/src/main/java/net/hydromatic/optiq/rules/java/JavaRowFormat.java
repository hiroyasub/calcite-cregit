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
name|rules
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
name|BuiltinMethod
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
name|java
operator|.
name|JavaTypeFactory
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
name|runtime
operator|.
name|FlatLists
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
name|runtime
operator|.
name|Unit
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
name|List
import|;
end_import

begin_comment
comment|/**  * How a row is represented as a Java value.  */
end_comment

begin_enum
specifier|public
enum|enum
name|JavaRowFormat
block|{
name|CUSTOM
block|{
name|Type
name|javaRowClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
assert|assert
name|type
operator|.
name|getFieldCount
argument_list|()
operator|>
literal|1
assert|;
return|return
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
name|Type
name|javaFieldClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|record
parameter_list|(
name|Type
name|javaRowClass
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
switch|switch
condition|(
name|expressions
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
assert|assert
name|javaRowClass
operator|==
name|Unit
operator|.
name|class
assert|;
return|return
name|Expressions
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|javaRowClass
argument_list|,
literal|"INSTANCE"
argument_list|)
return|;
default|default:
return|return
name|Expressions
operator|.
name|new_
argument_list|(
name|javaRowClass
argument_list|,
name|expressions
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|MemberExpression
name|field
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|,
name|Type
name|fieldType
parameter_list|)
block|{
specifier|final
name|Type
name|type
init|=
name|expression
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|Types
operator|.
name|RecordType
condition|)
block|{
name|Types
operator|.
name|RecordType
name|recordType
init|=
operator|(
name|Types
operator|.
name|RecordType
operator|)
name|type
decl_stmt|;
name|Types
operator|.
name|RecordField
name|recordField
init|=
name|recordType
operator|.
name|getRecordFields
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|field
argument_list|(
name|expression
argument_list|,
name|recordField
operator|.
name|getDeclaringClass
argument_list|()
argument_list|,
name|recordField
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Expressions
operator|.
name|field
argument_list|(
name|expression
argument_list|,
name|Types
operator|.
name|nthField
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|,
name|SCALAR
block|{
name|Type
name|javaRowClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
assert|assert
name|type
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
assert|;
return|return
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
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
argument_list|)
return|;
block|}
annotation|@
name|Override
name|Type
name|javaFieldClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|javaRowClass
argument_list|(
name|typeFactory
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|record
parameter_list|(
name|Type
name|javaRowClass
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
assert|assert
name|expressions
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|field
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|,
name|Type
name|fieldType
parameter_list|)
block|{
assert|assert
name|field
operator|==
literal|0
assert|;
return|return
name|expression
return|;
block|}
block|}
block|,
comment|/** A list that is comparable and immutable. Useful for records with 0 fields    * (empty list is a good singleton) but sometimes also for records with 2 or    * more fields that you need to be comparable, say as a key in a lookup. */
name|LIST
block|{
name|Type
name|javaRowClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|FlatLists
operator|.
name|ComparableList
operator|.
name|class
return|;
block|}
annotation|@
name|Override
name|Type
name|javaFieldClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|Object
operator|.
name|class
return|;
block|}
specifier|public
name|Expression
name|record
parameter_list|(
name|Type
name|javaRowClass
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
switch|switch
condition|(
name|expressions
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|Expressions
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|FlatLists
operator|.
name|class
argument_list|,
literal|"COMPARABLE_EMPTY_LIST"
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|BuiltinMethod
operator|.
name|LIST2
operator|.
name|method
argument_list|,
name|expressions
argument_list|)
argument_list|,
name|List
operator|.
name|class
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|BuiltinMethod
operator|.
name|LIST3
operator|.
name|method
argument_list|,
name|expressions
argument_list|)
argument_list|,
name|List
operator|.
name|class
argument_list|)
return|;
default|default:
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|BuiltinMethod
operator|.
name|ARRAYS_AS_LIST
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|expressions
argument_list|)
argument_list|)
argument_list|,
name|List
operator|.
name|class
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|field
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|,
name|Type
name|fieldType
parameter_list|)
block|{
return|return
name|RexToLixTranslator
operator|.
name|convert
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|expression
argument_list|,
name|BuiltinMethod
operator|.
name|LIST_GET
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
block|}
block|,
name|ARRAY
block|{
name|Type
name|javaRowClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
assert|assert
name|type
operator|.
name|getFieldCount
argument_list|()
operator|>
literal|1
assert|;
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
annotation|@
name|Override
name|Type
name|javaFieldClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|Object
operator|.
name|class
return|;
block|}
specifier|public
name|Expression
name|record
parameter_list|(
name|Type
name|javaRowClass
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|stripCasts
argument_list|(
name|expressions
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|comparer
parameter_list|()
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltinMethod
operator|.
name|ARRAY_COMPARER
operator|.
name|method
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|field
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|,
name|Type
name|fieldType
parameter_list|)
block|{
return|return
name|RexToLixTranslator
operator|.
name|convert
argument_list|(
name|Expressions
operator|.
name|arrayIndex
argument_list|(
name|expression
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
block|}
block|;
specifier|public
name|JavaRowFormat
name|optimize
parameter_list|(
name|RelDataType
name|rowType
parameter_list|)
block|{
switch|switch
condition|(
name|rowType
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|LIST
return|;
case|case
literal|1
case|:
return|return
name|SCALAR
return|;
default|default:
if|if
condition|(
name|this
operator|==
name|SCALAR
condition|)
block|{
return|return
name|LIST
return|;
block|}
return|return
name|this
return|;
block|}
block|}
specifier|abstract
name|Type
name|javaRowClass
argument_list|(
name|JavaTypeFactory
name|typeFactory
argument_list|,
name|RelDataType
name|type
argument_list|)
decl_stmt|;
comment|/**    * Returns the java class that is used to physically store the given field.    * For instance, a non-null int field can still be stored in a field of type    * {@code Object.class} in {@link JavaRowFormat#ARRAY} case.    *    * @param typeFactory type factory to resolve java types    * @param type row type    * @param index field index    * @return java type used to store the field    */
specifier|abstract
name|Type
name|javaFieldClass
argument_list|(
name|JavaTypeFactory
name|typeFactory
argument_list|,
name|RelDataType
name|type
argument_list|,
name|int
name|index
argument_list|)
decl_stmt|;
specifier|public
specifier|abstract
name|Expression
name|record
argument_list|(
name|Type
name|javaRowClass
argument_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|List
argument_list|<
name|Expression
argument_list|>
name|stripCasts
parameter_list|(
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Expression
argument_list|>
argument_list|()
block|{
specifier|public
name|Expression
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|Expression
name|expression
init|=
name|expressions
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
while|while
condition|(
name|expression
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Convert
condition|)
block|{
name|expression
operator|=
operator|(
operator|(
name|UnaryExpression
operator|)
name|expression
operator|)
operator|.
name|expression
expr_stmt|;
block|}
return|return
name|expression
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|expressions
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Expression
name|comparer
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
specifier|abstract
name|Expression
name|field
argument_list|(
name|Expression
name|expression
argument_list|,
name|int
name|field
argument_list|,
name|Type
name|fieldType
argument_list|)
decl_stmt|;
block|}
end_enum

begin_comment
comment|// End JavaRowFormat.java
end_comment

end_unit

