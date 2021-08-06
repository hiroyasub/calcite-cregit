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
name|plan
operator|.
name|RelOptPlanner
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
name|RelNode
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
name|InvocationHandler
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
name|Proxy
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
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
comment|/**  * Implementation of the {@link RelMetadataProvider}  * interface that caches results from an underlying provider.  */
end_comment

begin_class
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
class|class
name|CachingRelMetadataProvider
implements|implements
name|RelMetadataProvider
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Map
argument_list|<
name|List
argument_list|,
name|CacheEntry
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RelMetadataProvider
name|underlyingProvider
decl_stmt|;
specifier|private
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|CachingRelMetadataProvider
parameter_list|(
name|RelMetadataProvider
name|underlyingProvider
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|this
operator|.
name|underlyingProvider
operator|=
name|underlyingProvider
expr_stmt|;
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Deprecated
comment|// to be removed before 2.0
annotation|@
name|Override
specifier|public
operator|<
expr|@
name|Nullable
name|M
expr|extends @
name|Nullable
name|Metadata
operator|>
expr|@
name|Nullable
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|apply
argument_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
argument_list|,
name|final
name|Class
argument_list|<
name|?
extends|extends
name|M
argument_list|>
name|metadataClass
argument_list|)
block|{
name|final
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|function
operator|=
name|underlyingProvider
operator|.
name|apply
argument_list|(
name|relClass
argument_list|,
name|metadataClass
argument_list|)
block|;
if|if
condition|(
name|function
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// TODO jvs 30-Mar-2006: Use meta-metadata to decide which metadata
comment|// query results can stay fresh until the next Ice Age.
return|return
parameter_list|(
name|rel
parameter_list|,
name|mq
parameter_list|)
lambda|->
block|{
specifier|final
name|Metadata
name|metadata
init|=
name|requireNonNull
argument_list|(
name|function
operator|.
name|bind
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"metadata must not be null, relClass="
operator|+
name|relClass
operator|+
literal|", metadataClass="
operator|+
name|metadataClass
argument_list|)
decl_stmt|;
return|return
name|metadataClass
operator|.
name|cast
argument_list|(
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|metadataClass
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|metadataClass
block|}
operator|,
operator|new
name|CachingInvocationHandler
argument_list|(
name|metadata
argument_list|)
block_content|)
block|)
class|;
end_class

begin_function
unit|};   }    @
name|Override
specifier|public
argument_list|<
name|M
extends|extends
name|Metadata
argument_list|>
name|Multimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|<
name|M
argument_list|>
argument_list|>
name|handlers
parameter_list|(
name|MetadataDef
argument_list|<
name|M
argument_list|>
name|def
parameter_list|)
block|{
return|return
name|underlyingProvider
operator|.
name|handlers
argument_list|(
name|def
argument_list|)
return|;
block|}
end_function

begin_comment
comment|//~ Inner Classes ----------------------------------------------------------
end_comment

begin_comment
comment|/** An entry in the cache. Consists of the cached object and the timestamp    * when the entry is valid. If read at a later timestamp, the entry will be    * invalid and will be re-computed as if it did not exist. The net effect is a    * lazy-flushing cache. */
end_comment

begin_class
specifier|private
specifier|static
class|class
name|CacheEntry
block|{
name|long
name|timestamp
decl_stmt|;
annotation|@
name|Nullable
name|Object
name|result
decl_stmt|;
block|}
end_class

begin_comment
comment|/** Implementation of {@link InvocationHandler} for calls to a    * {@link CachingRelMetadataProvider}. Each request first looks in the cache;    * if the cache entry is present and not expired, returns the cache entry,    * otherwise computes the value and stores in the cache. */
end_comment

begin_class
specifier|private
class|class
name|CachingInvocationHandler
implements|implements
name|InvocationHandler
block|{
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
name|CachingInvocationHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|requireNonNull
argument_list|(
name|metadata
argument_list|,
literal|"metadata"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
annotation|@
name|Nullable
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Compute hash key.
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Object
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|metadata
operator|.
name|rel
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|arg
range|:
name|args
control|)
block|{
comment|// Replace null values because ImmutableList does not allow them.
name|builder
operator|.
name|add
argument_list|(
name|NullSentinel
operator|.
name|mask
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Object
argument_list|>
name|key
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|long
name|timestamp
init|=
name|planner
operator|.
name|getRelMetadataTimestamp
argument_list|(
name|metadata
operator|.
name|rel
argument_list|()
argument_list|)
decl_stmt|;
comment|// Perform cache lookup.
name|CacheEntry
name|entry
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|timestamp
operator|==
name|entry
operator|.
name|timestamp
condition|)
block|{
return|return
name|entry
operator|.
name|result
return|;
block|}
block|}
comment|// Cache miss or stale.
try|try
block|{
name|Object
name|result
init|=
name|method
operator|.
name|invoke
argument_list|(
name|metadata
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
operator|new
name|CacheEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|entry
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|castNonNull
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

unit|}
end_unit

