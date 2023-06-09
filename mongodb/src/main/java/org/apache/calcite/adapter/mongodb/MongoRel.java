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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rex
operator|.
name|RexBuilder
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
name|List
import|;
end_import

begin_comment
comment|/**  * Relational expression that uses Mongo calling convention.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MongoRel
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
comment|/** Calling convention for relational operations that occur in MongoDB. */
name|Convention
name|CONVENTION
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"MONGO"
argument_list|,
name|MongoRel
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Callback for the implementation process that converts a tree of    * {@link MongoRel} nodes into a MongoDB query. */
class|class
name|Implementor
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
name|RelOptTable
name|table
decl_stmt|;
name|MongoTable
name|mongoTable
decl_stmt|;
specifier|public
name|Implementor
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|String
name|findOp
parameter_list|,
name|String
name|aggOp
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|findOp
argument_list|,
name|aggOp
argument_list|)
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
name|MongoRel
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

