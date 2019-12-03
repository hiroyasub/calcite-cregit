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
name|enumerable
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
name|Expression
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
name|tree
operator|.
name|ParameterExpression
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
name|RexCall
import|;
end_import

begin_comment
comment|/** Implementor of Functions used in MATCH_RECOGNIZE Context. */
end_comment

begin_interface
specifier|public
interface|interface
name|MatchImplementor
block|{
comment|/**    * Implements a call.    *    * @param translator Translator for the call    * @param call Call that should be implemented    * @param row Current Row    * @param rows All Rows that are traversed so far    * @param symbols All Symbols of the rows that were traversed so far    * @return Translated call    */
name|Expression
name|implement
parameter_list|(
name|RexToLixTranslator
name|translator
parameter_list|,
name|RexCall
name|call
parameter_list|,
name|ParameterExpression
name|row
parameter_list|,
name|ParameterExpression
name|rows
parameter_list|,
name|ParameterExpression
name|symbols
parameter_list|,
name|ParameterExpression
name|currentIndex
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

