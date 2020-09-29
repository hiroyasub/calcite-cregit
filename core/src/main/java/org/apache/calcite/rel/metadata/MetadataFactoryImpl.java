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
name|rel
operator|.
name|metadata
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
name|rel
operator|.
name|RelNode
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
name|Pair
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|util
operator|.
name|concurrent
operator|.
name|UncheckedExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_comment
comment|/** Implementation of {@link MetadataFactory} that gets providers from a  * {@link RelMetadataProvider} and stores them in a cache.  *  *<p>The cache does not store metadata. It remembers which providers can  * provide which kinds of metadata, for which kinds of relational  * expressions.</p>  */
end_comment

begin_class
specifier|public
class|class
name|MetadataFactoryImpl
implements|implements
name|MetadataFactory
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
specifier|final
name|UnboundMetadata
argument_list|<
name|Metadata
argument_list|>
name|DUMMY
init|=
parameter_list|(
name|rel
parameter_list|,
name|mq
parameter_list|)
lambda|->
literal|null
decl_stmt|;
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Pair
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|,
name|Class
argument_list|<
name|Metadata
argument_list|>
argument_list|>
argument_list|,
name|UnboundMetadata
argument_list|<
name|Metadata
argument_list|>
argument_list|>
name|cache
decl_stmt|;
specifier|public
name|MetadataFactoryImpl
parameter_list|(
name|RelMetadataProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|loader
argument_list|(
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|CacheLoader
argument_list|<
name|Pair
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|,
name|Class
argument_list|<
name|Metadata
argument_list|>
argument_list|>
argument_list|,
name|UnboundMetadata
argument_list|<
name|Metadata
argument_list|>
argument_list|>
name|loader
parameter_list|(
specifier|final
name|RelMetadataProvider
name|provider
parameter_list|)
block|{
return|return
name|CacheLoader
operator|.
name|from
argument_list|(
name|key
lambda|->
block|{
specifier|final
name|UnboundMetadata
argument_list|<
name|Metadata
argument_list|>
name|function
init|=
name|provider
operator|.
name|apply
argument_list|(
name|key
operator|.
name|left
argument_list|,
name|key
operator|.
name|right
argument_list|)
decl_stmt|;
comment|// Return DUMMY, not null, so the cache knows to not ask again.
return|return
name|function
operator|!=
literal|null
condition|?
name|function
else|:
name|DUMMY
return|;
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
name|M
name|query
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|Class
argument_list|<
name|M
argument_list|>
name|metadataClazz
parameter_list|)
block|{
try|try
block|{
comment|//noinspection unchecked
specifier|final
name|Pair
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|,
name|Class
argument_list|<
name|Metadata
argument_list|>
argument_list|>
name|key
init|=
operator|(
name|Pair
operator|)
name|Pair
operator|.
name|of
argument_list|(
name|rel
operator|.
name|getClass
argument_list|()
argument_list|,
name|metadataClazz
argument_list|)
decl_stmt|;
specifier|final
name|Metadata
name|apply
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|bind
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|)
decl_stmt|;
return|return
name|metadataClazz
operator|.
name|cast
argument_list|(
name|apply
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UncheckedExecutionException
decl||
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|throwAsRuntime
argument_list|(
name|Util
operator|.
name|causeOrSelf
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

