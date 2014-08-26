begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

begin_comment
comment|/**  * Visitor that has methods for the common logical relational expressions.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelShuttle
block|{
name|RelNode
name|visit
parameter_list|(
name|TableAccessRelBase
name|scan
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|TableFunctionRelBase
name|scan
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|ValuesRel
name|values
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|FilterRel
name|filter
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|ProjectRel
name|project
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|JoinRel
name|join
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|CorrelatorRel
name|correlator
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|UnionRel
name|union
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|IntersectRel
name|intersect
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|MinusRel
name|minus
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|AggregateRel
name|aggregate
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|SortRel
name|sort
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|RelNode
name|other
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelShuttle.java
end_comment

end_unit

