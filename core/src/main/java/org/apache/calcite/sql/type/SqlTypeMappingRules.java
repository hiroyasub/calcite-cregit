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
name|sql
operator|.
name|type
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
name|collect
operator|.
name|ImmutableSet
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
name|Sets
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
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_comment
comment|/**  * This class defines some utilities to build type mapping matrix  * which would then use to construct the {@link SqlTypeMappingRule} rules.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlTypeMappingRules
block|{
comment|/** Returns the {@link SqlTypeMappingRule} instance based on    * the specified {@code coerce} to indicate whether to return as a type coercion rule.    *    * @param  coerce Whether to return rules with type coercion    * @return {@link SqlTypeCoercionRule} instance if {@code coerce} is true; else    * returns a {@link SqlTypeAssignmentRule} instance    */
specifier|public
specifier|static
name|SqlTypeMappingRule
name|instance
parameter_list|(
name|boolean
name|coerce
parameter_list|)
block|{
if|if
condition|(
name|coerce
condition|)
block|{
return|return
name|SqlTypeCoercionRule
operator|.
name|instance
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|SqlTypeAssignmentRule
operator|.
name|instance
argument_list|()
return|;
block|}
block|}
comment|/** Returns a {@link Builder} to build the type mappings. */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/** Keeps state while building the type mappings. */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|final
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|map
decl_stmt|;
specifier|final
name|LoadingCache
argument_list|<
name|Set
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|sets
decl_stmt|;
comment|/** Creates an empty {@link Builder}. */
name|Builder
parameter_list|()
block|{
name|this
operator|.
name|map
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|sets
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|CacheLoader
operator|.
name|from
argument_list|(
name|set
lambda|->
name|Sets
operator|.
name|immutableEnumSet
argument_list|(
name|set
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Add a map entry to the existing {@link Builder} mapping. */
name|void
name|add
parameter_list|(
name|SqlTypeName
name|fromType
parameter_list|,
name|Set
argument_list|<
name|SqlTypeName
argument_list|>
name|toTypes
parameter_list|)
block|{
try|try
block|{
name|map
operator|.
name|put
argument_list|(
name|fromType
argument_list|,
name|sets
operator|.
name|get
argument_list|(
name|toTypes
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UncheckedExecutionException
decl||
name|ExecutionException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|throwIfUnchecked
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"populating SqlTypeAssignmentRules"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Put all the type mappings to the {@link Builder}. */
name|void
name|addAll
parameter_list|(
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|typeMapping
parameter_list|)
block|{
try|try
block|{
name|map
operator|.
name|putAll
argument_list|(
name|typeMapping
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UncheckedExecutionException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|throwIfUnchecked
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"populating SqlTypeAssignmentRules"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Copy the map values from key {@code typeName} and      * returns as a {@link ImmutableSet.Builder}. */
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|SqlTypeName
argument_list|>
name|copyValues
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
name|ImmutableSet
operator|.
expr|<
name|SqlTypeName
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|typeName
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

