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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|ObjectNode
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
name|Preconditions
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
name|AbstractSequentialIterator
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
name|Iterators
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
name|Iterator
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
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  *<p>"Iterator" which retrieves results lazily and in batches. Uses  *<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html">Elastic Scrolling API</a>  * to optimally consume large search results.  *  *<p>This class is<strong>not thread safe</strong>.  */
end_comment

begin_class
class|class
name|Scrolling
block|{
specifier|private
specifier|final
name|ElasticsearchTransport
name|transport
decl_stmt|;
specifier|private
specifier|final
name|int
name|fetchSize
decl_stmt|;
name|Scrolling
parameter_list|(
name|ElasticsearchTransport
name|transport
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|transport
argument_list|,
literal|"transport"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|fetchSize
init|=
name|transport
operator|.
name|fetchSize
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fetchSize
operator|>
literal|0
argument_list|,
literal|"invalid fetch size. Expected %s> 0"
argument_list|,
name|fetchSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|fetchSize
operator|=
name|fetchSize
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|>
name|query
parameter_list|(
name|ObjectNode
name|query
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|,
literal|"query"
argument_list|)
expr_stmt|;
specifier|final
name|long
name|limit
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|has
argument_list|(
literal|"size"
argument_list|)
condition|)
block|{
name|limit
operator|=
name|query
operator|.
name|get
argument_list|(
literal|"size"
argument_list|)
operator|.
name|asLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|fetchSize
operator|>
name|limit
condition|)
block|{
comment|// don't use scrolling when batch size is greater than limit
return|return
name|transport
operator|.
name|search
argument_list|()
operator|.
name|apply
argument_list|(
name|query
argument_list|)
operator|.
name|searchHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
else|else
block|{
name|limit
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|query
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|fetchSize
argument_list|)
expr_stmt|;
specifier|final
name|ElasticsearchJson
operator|.
name|Result
name|first
init|=
name|transport
operator|.
name|search
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"scroll"
argument_list|,
literal|"1m"
argument_list|)
argument_list|)
operator|.
name|apply
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|AutoClosingIterator
name|iterator
init|=
operator|new
name|AutoClosingIterator
argument_list|(
operator|new
name|SequentialIterator
argument_list|(
name|first
argument_list|,
name|transport
argument_list|,
name|limit
argument_list|)
argument_list|,
name|scrollId
lambda|->
name|transport
operator|.
name|closeScroll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|scrollId
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|>
name|result
init|=
name|flatten
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
comment|// apply limit
if|if
condition|(
name|limit
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|result
operator|=
name|Iterators
operator|.
name|limit
argument_list|(
name|result
argument_list|,
operator|(
name|int
operator|)
name|limit
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Combines lazily multiple {@link ElasticsearchJson.Result} into a single iterator of    * {@link ElasticsearchJson.SearchHit}.    */
specifier|private
specifier|static
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|>
name|flatten
parameter_list|(
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
name|results
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|>
argument_list|>
name|inputs
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|results
argument_list|,
name|input
lambda|->
name|input
operator|.
name|searchHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|inputs
argument_list|)
return|;
block|}
comment|/**    * Observes when existing iterator has ended and clears context (scroll) if any.    */
specifier|private
specifier|static
class|class
name|AutoClosingIterator
implements|implements
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
implements|,
name|AutoCloseable
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|Consumer
argument_list|<
name|String
argument_list|>
name|closer
decl_stmt|;
comment|/** Returns whether {@link #closer} consumer was already called. */
specifier|private
name|boolean
name|closed
decl_stmt|;
comment|/** Keeps last value of {@code scrollId} in memory so scroll can be released      * upon termination. */
specifier|private
name|String
name|scrollId
decl_stmt|;
specifier|private
name|AutoClosingIterator
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
name|delegate
parameter_list|,
specifier|final
name|Consumer
argument_list|<
name|String
argument_list|>
name|closer
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|closer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
operator|&&
name|scrollId
operator|!=
literal|null
condition|)
block|{
comment|// close once (if scrollId is present)
name|closer
operator|.
name|accept
argument_list|(
name|scrollId
argument_list|)
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
specifier|final
name|boolean
name|hasNext
init|=
name|delegate
operator|.
name|hasNext
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasNext
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|hasNext
return|;
block|}
annotation|@
name|Override
specifier|public
name|ElasticsearchJson
operator|.
name|Result
name|next
parameter_list|()
block|{
name|ElasticsearchJson
operator|.
name|Result
name|next
init|=
name|delegate
operator|.
name|next
argument_list|()
decl_stmt|;
name|next
operator|.
name|scrollId
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|id
lambda|->
name|scrollId
operator|=
name|id
argument_list|)
expr_stmt|;
return|return
name|next
return|;
block|}
block|}
comment|/**    * Iterator which consumes current {@code scrollId} until full search result is fetched    * or {@code limit} is reached.    */
specifier|private
specifier|static
class|class
name|SequentialIterator
extends|extends
name|AbstractSequentialIterator
argument_list|<
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
block|{
specifier|private
specifier|final
name|ElasticsearchTransport
name|transport
decl_stmt|;
specifier|private
specifier|final
name|long
name|limit
decl_stmt|;
specifier|private
name|long
name|count
decl_stmt|;
specifier|private
name|SequentialIterator
parameter_list|(
specifier|final
name|ElasticsearchJson
operator|.
name|Result
name|first
parameter_list|,
specifier|final
name|ElasticsearchTransport
name|transport
parameter_list|,
specifier|final
name|long
name|limit
parameter_list|)
block|{
name|super
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|limit
operator|>=
literal|0
argument_list|,
literal|"limit: %s>= 0"
argument_list|,
name|limit
argument_list|)
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ElasticsearchJson
operator|.
name|Result
name|computeNext
parameter_list|(
specifier|final
name|ElasticsearchJson
operator|.
name|Result
name|previous
parameter_list|)
block|{
specifier|final
name|int
name|hits
init|=
name|previous
operator|.
name|searchHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|hits
operator|==
literal|0
operator|||
name|count
operator|>=
name|limit
condition|)
block|{
comment|// stop (re-)requesting when limit is reached or no more results
return|return
literal|null
return|;
block|}
name|count
operator|+=
name|hits
expr_stmt|;
specifier|final
name|String
name|scrollId
init|=
name|previous
operator|.
name|scrollId
argument_list|()
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
operator|new
name|IllegalStateException
argument_list|(
literal|"scrollId has to be present"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|transport
operator|.
name|scroll
argument_list|()
operator|.
name|apply
argument_list|(
name|scrollId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

