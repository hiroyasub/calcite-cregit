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
name|cassandra
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
name|tree
operator|.
name|Types
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
name|ImmutableMap
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Builtin methods in the Cassandra adapter.  */
end_comment

begin_enum
specifier|public
enum|enum
name|CassandraMethod
block|{
name|CASSANDRA_QUERYABLE_QUERY
argument_list|(
name|CassandraTable
operator|.
name|CassandraQueryable
operator|.
name|class
argument_list|,
literal|"query"
argument_list|,
name|List
operator|.
name|class
argument_list|,
name|List
operator|.
name|class
argument_list|,
name|List
operator|.
name|class
argument_list|,
name|List
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
block|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ImmutableEnumChecker"
argument_list|)
specifier|public
specifier|final
name|Method
name|method
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|Method
argument_list|,
name|CassandraMethod
argument_list|>
name|MAP
decl_stmt|;
static|static
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Method
argument_list|,
name|CassandraMethod
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|CassandraMethod
name|value
range|:
name|CassandraMethod
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|value
operator|.
name|method
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|MAP
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|CassandraMethod
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
modifier|...
name|argumentTypes
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|clazz
argument_list|,
name|methodName
argument_list|,
name|argumentTypes
argument_list|)
expr_stmt|;
block|}
block|}
end_enum

end_unit

