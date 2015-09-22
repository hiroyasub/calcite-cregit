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
name|tools
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
name|RelOptCluster
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
name|RelOptRule
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
name|RelOptSchema
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
name|core
operator|.
name|RelFactories
import|;
end_import

begin_comment
comment|/** A partially-created RelBuilder.  *  *<p>Add a cluster, and optionally a schema,  * when you want to create a builder.  *  *<p>A {@code ProtoRelBuilder} can be shared among queries, and thus can  * be inside a {@link RelOptRule}. It is a nice way to encapsulate the policy  * that this particular rule instance should create {@code DrillFilter}  * and {@code DrillProject} versus {@code HiveFilter} and {@code HiveProject}.  *  * @see RelFactories#LOGICAL_BUILDER  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelBuilderFactory
block|{
comment|/** Creates a RelBuilder. */
name|RelBuilder
name|create
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|schema
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelBuilderFactory.java
end_comment

end_unit

