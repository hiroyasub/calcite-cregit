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
name|avatica
operator|.
name|util
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
name|avatica
operator|.
name|AvaticaUtils
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
name|avatica
operator|.
name|ColumnMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/** Implementation of JDBC {@link Array}. */
end_comment

begin_class
specifier|public
class|class
name|ArrayImpl
implements|implements
name|Array
block|{
specifier|private
specifier|final
name|List
name|list
decl_stmt|;
specifier|private
specifier|final
name|AbstractCursor
operator|.
name|ArrayAccessor
name|accessor
decl_stmt|;
specifier|public
name|ArrayImpl
parameter_list|(
name|List
name|list
parameter_list|,
name|AbstractCursor
operator|.
name|ArrayAccessor
name|accessor
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|accessor
operator|=
name|accessor
expr_stmt|;
block|}
specifier|public
name|String
name|getBaseTypeName
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|accessor
operator|.
name|componentType
operator|.
name|name
return|;
block|}
specifier|public
name|int
name|getBaseType
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|accessor
operator|.
name|componentType
operator|.
name|id
return|;
block|}
specifier|public
name|Object
name|getArray
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|getArray
argument_list|(
name|list
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|Iterator
name|iterator
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|"[]"
return|;
block|}
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|accessor
operator|.
name|componentSlotGetter
operator|.
name|slot
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
try|try
block|{
name|append
argument_list|(
name|buf
argument_list|,
name|accessor
operator|.
name|componentAccessor
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|accessor
operator|.
name|componentSlotGetter
operator|.
name|slot
operator|=
literal|null
expr_stmt|;
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|append
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
if|else if
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
name|append
argument_list|(
name|buf
argument_list|,
name|AvaticaUtils
operator|.
name|primitiveList
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Converts a list into an array.    *    *<p>If the elements of the list are primitives, converts to an array of    * primitives (e.g. {@code boolean[]}.</p>    *    * @param list List of objects    *    * @return array    * @throws ClassCastException   if any element is not of the box type    * @throws NullPointerException if any element is null    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|Object
name|getArray
parameter_list|(
name|List
name|list
parameter_list|)
throws|throws
name|SQLException
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|accessor
operator|.
name|componentType
operator|.
name|rep
condition|)
block|{
case|case
name|PRIMITIVE_DOUBLE
case|:
specifier|final
name|double
index|[]
name|doubles
init|=
operator|new
name|double
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|double
name|v
range|:
operator|(
name|List
argument_list|<
name|Double
argument_list|>
operator|)
name|list
control|)
block|{
name|doubles
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|doubles
return|;
case|case
name|PRIMITIVE_FLOAT
case|:
specifier|final
name|float
index|[]
name|floats
init|=
operator|new
name|float
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|float
name|v
range|:
operator|(
name|List
argument_list|<
name|Float
argument_list|>
operator|)
name|list
control|)
block|{
name|floats
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|floats
return|;
case|case
name|PRIMITIVE_INT
case|:
specifier|final
name|int
index|[]
name|ints
init|=
operator|new
name|int
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|v
range|:
operator|(
name|List
argument_list|<
name|Integer
argument_list|>
operator|)
name|list
control|)
block|{
name|ints
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|ints
return|;
case|case
name|PRIMITIVE_LONG
case|:
specifier|final
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|long
name|v
range|:
operator|(
name|List
argument_list|<
name|Long
argument_list|>
operator|)
name|list
control|)
block|{
name|longs
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|longs
return|;
case|case
name|PRIMITIVE_SHORT
case|:
specifier|final
name|short
index|[]
name|shorts
init|=
operator|new
name|short
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|short
name|v
range|:
operator|(
name|List
argument_list|<
name|Short
argument_list|>
operator|)
name|list
control|)
block|{
name|shorts
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|shorts
return|;
case|case
name|PRIMITIVE_BOOLEAN
case|:
specifier|final
name|boolean
index|[]
name|booleans
init|=
operator|new
name|boolean
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|boolean
name|v
range|:
operator|(
name|List
argument_list|<
name|Boolean
argument_list|>
operator|)
name|list
control|)
block|{
name|booleans
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|booleans
return|;
case|case
name|PRIMITIVE_BYTE
case|:
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|byte
name|v
range|:
operator|(
name|List
argument_list|<
name|Byte
argument_list|>
operator|)
name|list
control|)
block|{
name|bytes
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|bytes
return|;
case|case
name|PRIMITIVE_CHAR
case|:
specifier|final
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|char
name|v
range|:
operator|(
name|List
argument_list|<
name|Character
argument_list|>
operator|)
name|list
control|)
block|{
name|chars
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
return|return
name|chars
return|;
default|default:
comment|// fall through
block|}
specifier|final
name|Object
index|[]
name|objects
init|=
name|list
operator|.
name|toArray
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|accessor
operator|.
name|componentType
operator|.
name|id
condition|)
block|{
case|case
name|Types
operator|.
name|ARRAY
case|:
specifier|final
name|AbstractCursor
operator|.
name|ArrayAccessor
name|componentAccessor
init|=
operator|(
name|AbstractCursor
operator|.
name|ArrayAccessor
operator|)
name|accessor
operator|.
name|componentAccessor
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|objects
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
operator|new
name|ArrayImpl
argument_list|(
operator|(
name|List
operator|)
name|objects
index|[
name|i
index|]
argument_list|,
name|componentAccessor
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|objects
return|;
block|}
specifier|public
name|Object
name|getArray
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
specifier|public
name|Object
name|getArray
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getArray
argument_list|(
name|list
operator|.
name|subList
argument_list|(
operator|(
name|int
operator|)
name|index
argument_list|,
name|count
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Object
name|getArray
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|count
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
specifier|public
name|ResultSet
name|getResultSet
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|accessor
operator|.
name|factory
operator|.
name|create
argument_list|(
name|accessor
operator|.
name|componentType
argument_list|,
name|list
argument_list|)
return|;
block|}
specifier|public
name|ResultSet
name|getResultSet
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
specifier|public
name|ResultSet
name|getResultSet
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
specifier|public
name|ResultSet
name|getResultSet
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|count
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
specifier|public
name|void
name|free
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// nothing to do
block|}
comment|/** Factory that can create a result set based on a list of values. */
specifier|public
interface|interface
name|Factory
block|{
name|ResultSet
name|create
parameter_list|(
name|ColumnMetaData
operator|.
name|AvaticaType
name|elementType
parameter_list|,
name|Iterable
argument_list|<
name|Object
argument_list|>
name|iterable
parameter_list|)
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End ArrayImpl.java
end_comment

end_unit

