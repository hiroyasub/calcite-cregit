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
name|linq4j
operator|.
name|tree
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
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Represents an expression that has a constant value.  */
end_comment

begin_class
specifier|public
class|class
name|ConstantExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Object
name|value
decl_stmt|;
specifier|public
name|ConstantExpression
parameter_list|(
name|Type
name|type
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Constant
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|Class
condition|)
block|{
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|type
decl_stmt|;
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|primitive
operator|!=
literal|null
condition|)
block|{
name|clazz
operator|=
name|primitive
operator|.
name|boxClass
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|clazz
operator|.
name|isInstance
argument_list|(
name|value
argument_list|)
operator|&&
operator|!
operator|(
operator|(
name|clazz
operator|==
name|Float
operator|.
name|class
operator|||
name|clazz
operator|==
name|Double
operator|.
name|class
operator|)
operator|&&
name|value
operator|instanceof
name|BigDecimal
operator|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"value "
operator|+
name|value
operator|+
literal|" does not match type "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Evaluator
name|evaluator
parameter_list|)
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|accept
parameter_list|(
name|Shuttle
name|shuttle
parameter_list|)
block|{
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|Visitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|int
name|lprec
parameter_list|,
name|int
name|rprec
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|writer
operator|.
name|requireParentheses
argument_list|(
name|this
argument_list|,
name|lprec
argument_list|,
name|rprec
argument_list|)
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|") null"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|write
argument_list|(
name|writer
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|ExpressionWriter
name|write
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
specifier|final
name|Object
name|value
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|value
operator|.
name|getClass
argument_list|()
expr_stmt|;
if|if
condition|(
name|Primitive
operator|.
name|isBox
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|type
operator|=
name|Primitive
operator|.
name|ofBox
argument_list|(
name|type
argument_list|)
operator|.
name|primitiveClass
expr_stmt|;
block|}
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|escapeString
argument_list|(
name|writer
operator|.
name|getBuf
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
specifier|final
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
specifier|final
name|BigDecimal
name|bigDecimal
decl_stmt|;
if|if
condition|(
name|primitive
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|primitive
condition|)
block|{
case|case
name|BYTE
case|:
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"(byte)"
argument_list|)
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Byte
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
case|case
name|CHAR
case|:
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"(char)"
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|int
operator|)
operator|(
name|Character
operator|)
name|value
argument_list|)
return|;
case|case
name|SHORT
case|:
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"(short)"
argument_list|)
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Short
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
name|writer
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|append
argument_list|(
literal|"L"
argument_list|)
return|;
case|case
name|FLOAT
case|:
if|if
condition|(
name|value
operator|instanceof
name|BigDecimal
condition|)
block|{
name|bigDecimal
operator|=
operator|(
name|BigDecimal
operator|)
name|value
expr_stmt|;
block|}
else|else
block|{
name|bigDecimal
operator|=
name|BigDecimal
operator|.
name|valueOf
argument_list|(
operator|(
name|Float
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bigDecimal
operator|.
name|precision
argument_list|()
operator|>
literal|6
condition|)
block|{
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"Float.intBitsToFloat("
argument_list|)
operator|.
name|append
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|bigDecimal
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
return|return
name|writer
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|append
argument_list|(
literal|"F"
argument_list|)
return|;
case|case
name|DOUBLE
case|:
if|if
condition|(
name|value
operator|instanceof
name|BigDecimal
condition|)
block|{
name|bigDecimal
operator|=
operator|(
name|BigDecimal
operator|)
name|value
expr_stmt|;
block|}
else|else
block|{
name|bigDecimal
operator|=
name|BigDecimal
operator|.
name|valueOf
argument_list|(
operator|(
name|Double
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bigDecimal
operator|.
name|precision
argument_list|()
operator|>
literal|10
condition|)
block|{
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"Double.longBitsToDouble("
argument_list|)
operator|.
name|append
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|bigDecimal
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"L)"
argument_list|)
return|;
block|}
return|return
name|writer
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|append
argument_list|(
literal|"D"
argument_list|)
return|;
default|default:
return|return
name|writer
operator|.
name|append
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
specifier|final
name|Primitive
name|primitive2
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
name|primitive2
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|primitive2
operator|.
name|boxName
operator|+
literal|".valueOf("
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|writer
argument_list|,
name|value
argument_list|,
name|primitive2
operator|.
name|primitiveClass
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Enum
condition|)
block|{
return|return
name|writer
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Enum
operator|)
name|value
operator|)
operator|.
name|getDeclaringClass
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Enum
operator|)
name|value
operator|)
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|BigDecimal
condition|)
block|{
name|bigDecimal
operator|=
operator|(
operator|(
name|BigDecimal
operator|)
name|value
operator|)
operator|.
name|stripTrailingZeros
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|int
name|scale
init|=
name|bigDecimal
operator|.
name|scale
argument_list|()
decl_stmt|;
specifier|final
name|long
name|exact
init|=
name|bigDecimal
operator|.
name|scaleByPowerOfTen
argument_list|(
name|scale
argument_list|)
operator|.
name|longValueExact
argument_list|()
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"new java.math.BigDecimal("
argument_list|)
operator|.
name|append
argument_list|(
name|exact
argument_list|)
operator|.
name|append
argument_list|(
literal|"L"
argument_list|)
expr_stmt|;
if|if
condition|(
name|scale
operator|!=
literal|0
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|scale
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|e
parameter_list|)
block|{
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"new java.math.BigDecimal(\""
argument_list|)
operator|.
name|append
argument_list|(
name|bigDecimal
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\")"
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|value
operator|instanceof
name|BigInteger
condition|)
block|{
name|BigInteger
name|bigInteger
init|=
operator|(
name|BigInteger
operator|)
name|value
decl_stmt|;
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"new java.math.BigInteger(\""
argument_list|)
operator|.
name|append
argument_list|(
name|bigInteger
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\")"
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Class
condition|)
block|{
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|value
decl_stmt|;
return|return
name|writer
operator|.
name|append
argument_list|(
name|clazz
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".class"
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Types
operator|.
name|RecordType
condition|)
block|{
specifier|final
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
name|value
decl_stmt|;
return|return
name|writer
operator|.
name|append
argument_list|(
name|recordType
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".class"
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"new "
argument_list|)
operator|.
name|append
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getComponentType
argument_list|()
argument_list|)
expr_stmt|;
name|list
argument_list|(
name|writer
argument_list|,
name|Primitive
operator|.
name|asList
argument_list|(
name|value
argument_list|)
argument_list|,
literal|"[] {\n"
argument_list|,
literal|",\n"
argument_list|,
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|List
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|List
operator|)
name|value
operator|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"java.util.Collections.EMPTY_LIST"
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
name|list
argument_list|(
name|writer
argument_list|,
operator|(
name|List
operator|)
name|value
argument_list|,
literal|"java.util.Arrays.asList("
argument_list|,
literal|",\n"
argument_list|,
literal|")"
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
return|return
name|writeMap
argument_list|(
name|writer
argument_list|,
operator|(
name|Map
operator|)
name|value
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Set
condition|)
block|{
return|return
name|writeSet
argument_list|(
name|writer
argument_list|,
operator|(
name|Set
operator|)
name|value
argument_list|)
return|;
block|}
name|Constructor
name|constructor
init|=
name|matchingConstructor
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|constructor
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"new "
argument_list|)
operator|.
name|append
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|list
argument_list|(
name|writer
argument_list|,
name|Arrays
operator|.
name|stream
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getFields
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|field
lambda|->
block|{
try|try
block|{
return|return
name|field
operator|.
name|get
argument_list|(
name|value
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|,
literal|"(\n"
argument_list|,
literal|",\n"
argument_list|,
literal|")"
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
return|return
name|writer
operator|.
name|append
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|list
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|List
name|list
parameter_list|,
name|String
name|begin
parameter_list|,
name|String
name|sep
parameter_list|,
name|String
name|end
parameter_list|)
block|{
name|writer
operator|.
name|begin
argument_list|(
name|begin
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|value
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|sep
argument_list|)
operator|.
name|indent
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|writer
argument_list|,
name|value
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|end
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|ExpressionWriter
name|writeMap
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|Map
name|map
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"com.google.common.collect.ImmutableMap."
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"of()"
argument_list|)
return|;
block|}
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|<
literal|5
condition|)
block|{
return|return
name|map
argument_list|(
name|writer
argument_list|,
name|map
argument_list|,
literal|"of("
argument_list|,
literal|",\n"
argument_list|,
literal|")"
argument_list|)
return|;
block|}
return|return
name|map
argument_list|(
name|writer
argument_list|,
name|map
argument_list|,
literal|"builder().put("
argument_list|,
literal|")\n.put("
argument_list|,
literal|").build()"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ExpressionWriter
name|map
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|Map
name|map
parameter_list|,
name|String
name|begin
parameter_list|,
name|String
name|entrySep
parameter_list|,
name|String
name|end
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|begin
argument_list|)
expr_stmt|;
name|boolean
name|comma
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|comma
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|entrySep
argument_list|)
operator|.
name|indent
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|writer
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|writer
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|comma
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|writer
operator|.
name|append
argument_list|(
name|end
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ExpressionWriter
name|writeSet
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|Set
name|set
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"com.google.common.collect.ImmutableSet."
argument_list|)
expr_stmt|;
if|if
condition|(
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|writer
operator|.
name|append
argument_list|(
literal|"of()"
argument_list|)
return|;
block|}
if|if
condition|(
name|set
operator|.
name|size
argument_list|()
operator|<
literal|5
condition|)
block|{
return|return
name|set
argument_list|(
name|writer
argument_list|,
name|set
argument_list|,
literal|"of("
argument_list|,
literal|","
argument_list|,
literal|")"
argument_list|)
return|;
block|}
return|return
name|set
argument_list|(
name|writer
argument_list|,
name|set
argument_list|,
literal|"builder().add("
argument_list|,
literal|")\n.add("
argument_list|,
literal|").build()"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ExpressionWriter
name|set
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|Set
name|set
parameter_list|,
name|String
name|begin
parameter_list|,
name|String
name|entrySep
parameter_list|,
name|String
name|end
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|begin
argument_list|)
expr_stmt|;
name|boolean
name|comma
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|set
operator|.
name|toArray
argument_list|()
control|)
block|{
if|if
condition|(
name|comma
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|entrySep
argument_list|)
operator|.
name|indent
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|writer
argument_list|,
name|o
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|comma
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|writer
operator|.
name|append
argument_list|(
name|end
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Constructor
name|matchingConstructor
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
specifier|final
name|Field
index|[]
name|fields
init|=
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Constructor
argument_list|<
name|?
argument_list|>
name|constructor
range|:
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getConstructors
argument_list|()
control|)
block|{
if|if
condition|(
name|argsMatchFields
argument_list|(
name|fields
argument_list|,
name|constructor
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|constructor
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|boolean
name|argsMatchFields
parameter_list|(
name|Field
index|[]
name|fields
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
parameter_list|)
block|{
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|!=
name|fields
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
operator|!=
name|parameterTypes
index|[
name|i
index|]
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
specifier|private
specifier|static
name|void
name|escapeString
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
name|lastChar
init|=
literal|0
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
name|n
condition|;
operator|++
name|i
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\n'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\n"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\r'
case|:
if|if
condition|(
name|lastChar
operator|!=
literal|'\n'
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\\r"
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
break|break;
block|}
name|lastChar
operator|=
name|c
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
comment|// REVIEW: Should constants with the same value and different type
comment|// (e.g. 3L and 3) be considered equal.
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ConstantExpression
name|that
init|=
operator|(
name|ConstantExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|?
operator|!
name|value
operator|.
name|equals
argument_list|(
name|that
operator|.
name|value
argument_list|)
else|:
name|that
operator|.
name|value
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ConstantExpression.java
end_comment

end_unit

