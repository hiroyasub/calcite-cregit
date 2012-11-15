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
name|HashMap
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

begin_comment
comment|/**  * Enumeration of Java's primitive types.  *  *<p>There are fields for the native class (e.g.<code>int</code>, also  * known as {@link Integer#TYPE}) and the boxing class  * (e.g. {@link Integer}).</p> */
end_comment

begin_enum
specifier|public
enum|enum
name|Primitive
block|{
name|BOOLEAN
argument_list|(
name|Boolean
operator|.
name|TYPE
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
block|,
name|BYTE
argument_list|(
name|Byte
operator|.
name|TYPE
argument_list|,
name|Byte
operator|.
name|class
argument_list|)
block|,
name|CHARACTER
argument_list|(
name|Character
operator|.
name|TYPE
argument_list|,
name|Character
operator|.
name|class
argument_list|)
block|,
name|SHORT
argument_list|(
name|Short
operator|.
name|TYPE
argument_list|,
name|Short
operator|.
name|class
argument_list|)
block|,
name|INT
argument_list|(
name|Integer
operator|.
name|TYPE
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
block|,
name|LONG
argument_list|(
name|Long
operator|.
name|TYPE
argument_list|,
name|Long
operator|.
name|class
argument_list|)
block|,
name|FLOAT
argument_list|(
name|Float
operator|.
name|TYPE
argument_list|,
name|Float
operator|.
name|class
argument_list|)
block|,
name|DOUBLE
argument_list|(
name|Double
operator|.
name|TYPE
argument_list|,
name|Double
operator|.
name|class
argument_list|)
block|,
name|VOID
argument_list|(
name|Void
operator|.
name|TYPE
argument_list|,
name|Void
operator|.
name|class
argument_list|)
block|,
name|OTHER
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
block|;
specifier|public
specifier|final
name|Class
name|primitiveClass
decl_stmt|;
specifier|public
specifier|final
name|Class
name|boxClass
decl_stmt|;
specifier|public
specifier|final
name|String
name|primitiveName
decl_stmt|;
comment|// e.g. "int"
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|Primitive
argument_list|>
name|PRIMITIVE_MAP
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|Primitive
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|Primitive
argument_list|>
name|BOX_MAP
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|Primitive
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|Primitive
index|[]
name|values
init|=
name|Primitive
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Primitive
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|.
name|primitiveClass
operator|!=
literal|null
condition|)
block|{
name|PRIMITIVE_MAP
operator|.
name|put
argument_list|(
name|value
operator|.
name|primitiveClass
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|.
name|boxClass
operator|!=
literal|null
condition|)
block|{
name|BOX_MAP
operator|.
name|put
argument_list|(
name|value
operator|.
name|boxClass
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Primitive
parameter_list|(
name|Class
name|primitiveClass
parameter_list|,
name|Class
name|boxClass
parameter_list|)
block|{
name|this
operator|.
name|primitiveClass
operator|=
name|primitiveClass
expr_stmt|;
name|this
operator|.
name|primitiveName
operator|=
name|primitiveClass
operator|!=
literal|null
condition|?
name|primitiveClass
operator|.
name|getSimpleName
argument_list|()
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|boxClass
operator|=
name|boxClass
expr_stmt|;
block|}
comment|/** Returns the Primitive object for a given primitive class.      *      *<p>For example,<code>of(Long.TYPE)</code> or<code>of(long.class)</code>      * returns {@link #LONG}. */
specifier|public
specifier|static
name|Primitive
name|of
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
comment|//noinspection SuspiciousMethodCalls
return|return
name|PRIMITIVE_MAP
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/** Returns the Primitive object for a given boxing class.      *      *<p>For example,<code>ofBox(java.util.Long.class)</code>      * returns {@link #LONG}. */
specifier|public
specifier|static
name|Primitive
name|ofBox
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
comment|//noinspection SuspiciousMethodCalls
return|return
name|BOX_MAP
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End Primitive.java
end_comment

end_unit

