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

begin_comment
comment|/**  * All available data type for Redis.  */
end_comment

begin_enum
specifier|public
enum|enum
name|RedisDataType
block|{
comment|/**    * Strings are the most basic kind of Redis value. Redis Strings are binary safe,    * this means that a Redis string can contain any kind of data, for instance a JPEG image    * or a serialized Ruby object.    * A String value can be at max 512 Megabytes in length.    */
name|STRING
argument_list|(
literal|"string"
argument_list|)
block|,
comment|/**    * Redis Hashes are maps between string fields and string values.    */
name|HASH
argument_list|(
literal|"hash"
argument_list|)
block|,
comment|/**    * Redis Lists are simply lists of strings, sorted by insertion order.    */
name|LIST
argument_list|(
literal|"list"
argument_list|)
block|,
comment|/**    * Redis Sets are an unordered collection of Strings.    */
name|SET
argument_list|(
literal|"set"
argument_list|)
block|,
comment|/**    * Redis Sorted Sets are, similarly to Redis Sets, non repeating collections of Strings.    * The difference is that every member of a Sorted Set is associated with score,    * that is used in order to take the sorted set ordered,    * from the smallest to the greatest score.    * While members are unique, scores may be repeated.    */
name|SORTED_SET
argument_list|(
literal|"zset"
argument_list|)
block|,
comment|/**    * HyperLogLog is a probabilistic data structure used in order to count unique things.    */
name|HYPER_LOG_LOG
argument_list|(
literal|"pfadd"
argument_list|)
block|,
comment|/**    * Redis implementation of publish and subscribe paradigm.    * Published messages are characterized into channels,    * without knowledge of what (if any) subscribers there may be.    * Subscribers express interest in one or more channels, and only receive messages    * that are of interest, without knowledge of what (if any) publishers there are.    */
name|PUBSUB
argument_list|(
literal|"publish"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|typeName
decl_stmt|;
name|RedisDataType
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
block|}
specifier|public
specifier|static
name|RedisDataType
name|fromTypeName
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
for|for
control|(
name|RedisDataType
name|type
range|:
name|RedisDataType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|getTypeName
argument_list|()
operator|.
name|equals
argument_list|(
name|typeName
argument_list|)
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|this
operator|.
name|typeName
return|;
block|}
block|}
end_enum

end_unit

