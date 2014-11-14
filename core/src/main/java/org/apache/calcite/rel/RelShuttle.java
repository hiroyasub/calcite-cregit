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
name|core
operator|.
name|Correlator
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
name|Sort
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
name|TableFunctionScan
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
name|TableScan
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
name|logical
operator|.
name|LogicalAggregate
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
name|logical
operator|.
name|LogicalFilter
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
name|logical
operator|.
name|LogicalIntersect
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
name|logical
operator|.
name|LogicalJoin
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
name|logical
operator|.
name|LogicalMinus
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
name|logical
operator|.
name|LogicalProject
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
name|logical
operator|.
name|LogicalUnion
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
name|logical
operator|.
name|LogicalValues
import|;
end_import

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
name|TableScan
name|scan
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|TableFunctionScan
name|scan
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalValues
name|values
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalFilter
name|filter
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalProject
name|project
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalJoin
name|join
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|Correlator
name|correlator
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalUnion
name|union
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalIntersect
name|intersect
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalMinus
name|minus
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|LogicalAggregate
name|aggregate
parameter_list|)
function_decl|;
name|RelNode
name|visit
parameter_list|(
name|Sort
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

