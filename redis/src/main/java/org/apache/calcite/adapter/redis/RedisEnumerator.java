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
name|redis
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
name|Linq4j
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|redis
operator|.
name|clients
operator|.
name|jedis
operator|.
name|Jedis
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link RedisEnumerator}.  */
end_comment

begin_class
class|class
name|RedisEnumerator
implements|implements
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
decl_stmt|;
name|RedisEnumerator
parameter_list|(
name|RedisConfig
name|redisConfig
parameter_list|,
name|RedisSchema
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|RedisTableFieldInfo
name|tableFieldInfo
init|=
name|schema
operator|.
name|getTableFieldInfo
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
name|RedisJedisManager
name|redisManager
init|=
operator|new
name|RedisJedisManager
argument_list|(
name|redisConfig
operator|.
name|getHost
argument_list|()
argument_list|,
name|redisConfig
operator|.
name|getPort
argument_list|()
argument_list|,
name|redisConfig
operator|.
name|getDatabase
argument_list|()
argument_list|,
name|redisConfig
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Jedis
name|jedis
init|=
name|redisManager
operator|.
name|getResource
argument_list|()
init|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|redisConfig
operator|.
name|getPassword
argument_list|()
argument_list|)
condition|)
block|{
name|jedis
operator|.
name|auth
argument_list|(
name|redisConfig
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RedisDataProcess
name|dataProcess
init|=
operator|new
name|RedisDataProcess
argument_list|(
name|jedis
argument_list|,
name|tableFieldInfo
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|objs
init|=
name|dataProcess
operator|.
name|read
argument_list|()
decl_stmt|;
name|enumerator
operator|=
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|objs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|deduceRowType
parameter_list|(
name|RedisTableFieldInfo
name|tableFieldInfo
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldBuilder
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|dataFormat
init|=
name|tableFieldInfo
operator|.
name|getDataFormat
argument_list|()
decl_stmt|;
name|RedisDataFormat
name|redisDataFormat
init|=
name|RedisDataFormat
operator|.
name|fromTypeName
argument_list|(
name|dataFormat
argument_list|)
decl_stmt|;
assert|assert
name|redisDataFormat
operator|!=
literal|null
assert|;
if|if
condition|(
name|redisDataFormat
operator|==
name|RedisDataFormat
operator|.
name|RAW
condition|)
block|{
name|fieldBuilder
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"key"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|field
range|:
name|tableFieldInfo
operator|.
name|getFields
argument_list|()
control|)
block|{
name|fieldBuilder
operator|.
name|put
argument_list|(
name|field
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|field
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fieldBuilder
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
return|return
name|enumerator
operator|.
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
name|enumerator
operator|.
name|moveNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|enumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

