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
name|rex
package|;
end_package

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
comment|/** Can reduce expressions, writing a literal for each into a list. */
end_comment

begin_interface
specifier|public
interface|interface
name|RexExecutor
block|{
comment|/** Reduces expressions, and writes their results into {@code reducedValues}.    *    *<p>If an expression cannot be reduced, writes the original expression.    * For example, {@code CAST('abc' AS INTEGER)} gives an error when executed, so the executor    * ignores the error and writes the original expression.    *    * @param rexBuilder Rex builder    * @param constExps Expressions to be reduced    * @param reducedValues List to which reduced expressions are appended    */
name|void
name|reduce
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|constExps
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|reducedValues
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RexExecutor.java
end_comment

end_unit

