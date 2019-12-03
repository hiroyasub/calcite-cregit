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
name|plan
operator|.
name|Convention
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
name|plan
operator|.
name|RelOptTable
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * Relational expression that uses Cassandra calling convention.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CassandraRel
extends|extends
name|RelNode
block|{
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
function_decl|;
comment|/** Calling convention for relational operations that occur in Cassandra. */
name|Convention
name|CONVENTION
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"CASSANDRA"
argument_list|,
name|CassandraRel
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Callback for the implementation process that converts a tree of    * {@link CassandraRel} nodes into a CQL query. */
class|class
name|Implementor
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|selectFields
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|whereClause
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|int
name|fetch
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|order
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptTable
name|table
decl_stmt|;
name|CassandraTable
name|cassandraTable
decl_stmt|;
comment|/** Adds newly projected fields and restricted predicates.      *      * @param fields New fields to be projected from a query      * @param predicates New predicates to be applied to the query      */
specifier|public
name|void
name|add
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|predicates
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|selectFields
operator|.
name|putAll
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|predicates
operator|!=
literal|null
condition|)
block|{
name|whereClause
operator|.
name|addAll
argument_list|(
name|predicates
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addOrder
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|newOrder
parameter_list|)
block|{
name|order
operator|.
name|addAll
argument_list|(
name|newOrder
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitChild
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
assert|assert
name|ordinal
operator|==
literal|0
assert|;
operator|(
operator|(
name|CassandraRel
operator|)
name|input
operator|)
operator|.
name|implement
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

