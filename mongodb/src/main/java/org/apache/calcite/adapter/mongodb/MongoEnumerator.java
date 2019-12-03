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
name|mongodb
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
name|util
operator|.
name|DateTimeUtils
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
name|Primitive
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCursor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
comment|/** Enumerator that reads from a MongoDB collection. */
end_comment

begin_class
class|class
name|MongoEnumerator
implements|implements
name|Enumerator
argument_list|<
name|Object
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Document
argument_list|>
name|cursor
decl_stmt|;
specifier|private
specifier|final
name|Function1
argument_list|<
name|Document
argument_list|,
name|Object
argument_list|>
name|getter
decl_stmt|;
specifier|private
name|Object
name|current
decl_stmt|;
comment|/** Creates a MongoEnumerator.    *    * @param cursor Mongo iterator (usually a {@link com.mongodb.DBCursor})    * @param getter Converts an object into a list of fields    */
name|MongoEnumerator
parameter_list|(
name|Iterator
argument_list|<
name|Document
argument_list|>
name|cursor
parameter_list|,
name|Function1
argument_list|<
name|Document
argument_list|,
name|Object
argument_list|>
name|getter
parameter_list|)
block|{
name|this
operator|.
name|cursor
operator|=
name|cursor
expr_stmt|;
name|this
operator|.
name|getter
operator|=
name|getter
expr_stmt|;
block|}
specifier|public
name|Object
name|current
parameter_list|()
block|{
return|return
name|current
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|cursor
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Document
name|map
init|=
name|cursor
operator|.
name|next
argument_list|()
decl_stmt|;
name|current
operator|=
name|getter
operator|.
name|apply
argument_list|(
name|map
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
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
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|cursor
operator|instanceof
name|MongoCursor
condition|)
block|{
operator|(
operator|(
name|MongoCursor
operator|)
name|cursor
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// AggregationOutput implements Iterator but not DBCursor. There is no
comment|// available close() method -- apparently there is no open resource.
block|}
specifier|static
name|Function1
argument_list|<
name|Document
argument_list|,
name|Map
argument_list|>
name|mapGetter
parameter_list|()
block|{
return|return
name|a0
lambda|->
operator|(
name|Map
operator|)
name|a0
return|;
block|}
specifier|static
name|Function1
argument_list|<
name|Document
argument_list|,
name|Object
argument_list|>
name|singletonGetter
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Class
name|fieldClass
parameter_list|)
block|{
return|return
name|a0
lambda|->
name|convert
argument_list|(
name|a0
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|fieldClass
argument_list|)
return|;
block|}
comment|/**    * @param fields List of fields to project; or null to return map    */
specifier|static
name|Function1
argument_list|<
name|Document
argument_list|,
name|Object
index|[]
argument_list|>
name|listGetter
parameter_list|(
specifier|final
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
return|return
name|a0
lambda|->
block|{
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|fields
operator|.
name|size
argument_list|()
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|field
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|objects
index|[
name|i
index|]
operator|=
name|convert
argument_list|(
name|a0
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|,
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
return|;
block|}
specifier|static
name|Function1
argument_list|<
name|Document
argument_list|,
name|Object
argument_list|>
name|getter
parameter_list|(
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|fields
operator|==
literal|null
condition|?
operator|(
name|Function1
operator|)
name|mapGetter
argument_list|()
else|:
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|singletonGetter
argument_list|(
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|,
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
else|:
operator|(
name|Function1
operator|)
name|listGetter
argument_list|(
name|fields
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Object
name|convert
parameter_list|(
name|Object
name|o
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
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
else|else
block|{
name|primitive
operator|=
name|Primitive
operator|.
name|ofBox
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
name|o
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Date
operator|&&
name|primitive
operator|!=
literal|null
condition|)
block|{
name|o
operator|=
operator|(
operator|(
name|Date
operator|)
name|o
operator|)
operator|.
name|getTime
argument_list|()
operator|/
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Number
operator|&&
name|primitive
operator|!=
literal|null
condition|)
block|{
return|return
name|primitive
operator|.
name|number
argument_list|(
operator|(
name|Number
operator|)
name|o
argument_list|)
return|;
block|}
return|return
name|o
return|;
block|}
block|}
end_class

end_unit

