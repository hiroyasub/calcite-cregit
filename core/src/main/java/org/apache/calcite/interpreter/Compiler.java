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
name|interpreter
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
name|DataContext
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
name|Enumerable
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|RexNode
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
comment|/**  * Context while converting a tree of {@link RelNode} to a program  * that can be run by an {@link Interpreter}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Compiler
block|{
comment|/** Compiles an expression to an executable form. */
name|Scalar
name|compile
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|)
function_decl|;
name|RelDataType
name|combinedRowType
parameter_list|(
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
function_decl|;
name|Source
name|source
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/**    * Creates a Sink for a relational expression to write into.    *    *<p>This method is generally called from the constructor of a {@link Node}.    * But a constructor could instead call    * {@link #enumerable(RelNode, Enumerable)}.    *    * @param rel Relational expression    * @return Sink    */
name|Sink
name|sink
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/** Tells the interpreter that a given relational expression wishes to    * give its output as an enumerable.    *    *<p>This is as opposed to the norm, where a relational expression calls    * {@link #sink(RelNode)}, then its {@link Node#run()} method writes into that    * sink.    *    * @param rel Relational expression    * @param rowEnumerable Contents of relational expression    */
name|void
name|enumerable
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Enumerable
argument_list|<
name|Row
argument_list|>
name|rowEnumerable
parameter_list|)
function_decl|;
name|DataContext
name|getDataContext
parameter_list|()
function_decl|;
name|Context
name|createContext
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Compiler.java
end_comment

end_unit

