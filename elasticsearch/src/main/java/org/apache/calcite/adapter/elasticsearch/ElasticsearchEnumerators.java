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
name|elasticsearch
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
comment|/**  * Util functions which convert  * {@link ElasticsearchJson.SearchHit}  * into calcite specific return type (map, object[], list etc.)  */
end_comment

begin_class
class|class
name|ElasticsearchEnumerators
block|{
specifier|private
name|ElasticsearchEnumerators
parameter_list|()
block|{
block|}
specifier|private
specifier|static
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|,
name|Map
argument_list|>
name|mapGetter
parameter_list|()
block|{
return|return
operator|new
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|,
name|Map
argument_list|>
argument_list|()
block|{
specifier|public
name|Map
name|apply
parameter_list|(
name|ElasticsearchJson
operator|.
name|SearchHit
name|hits
parameter_list|)
block|{
return|return
name|hits
operator|.
name|sourceOrFields
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
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
operator|new
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|ElasticsearchJson
operator|.
name|SearchHit
name|hits
parameter_list|)
block|{
return|return
name|convert
argument_list|(
name|hits
operator|.
name|valueOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|fieldClass
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Function that extracts a given set of fields from elastic search result    * objects.    *    * @param fields List of fields to project    *    * @return function that converts the search result into a generic array    */
specifier|private
specifier|static
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
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
operator|new
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|,
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
index|[]
name|apply
parameter_list|(
name|ElasticsearchJson
operator|.
name|SearchHit
name|hit
parameter_list|)
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
specifier|final
name|Class
name|type
init|=
name|field
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|objects
index|[
name|i
index|]
operator|=
name|convert
argument_list|(
name|hit
operator|.
name|valueOrNull
argument_list|(
name|name
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
block|}
return|;
block|}
specifier|static
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
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
specifier|final
name|Function1
name|getter
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
literal|"_MAP"
operator|.
name|equals
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
argument_list|)
condition|)
block|{
comment|// select * from table
name|getter
operator|=
name|mapGetter
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// select foo from table
name|getter
operator|=
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
expr_stmt|;
block|}
else|else
block|{
comment|// select a, b, c from table
name|getter
operator|=
name|listGetter
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
return|return
name|getter
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

begin_comment
comment|// End ElasticsearchEnumerators.java
end_comment

end_unit

