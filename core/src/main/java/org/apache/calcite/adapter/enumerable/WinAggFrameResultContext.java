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

begin_comment
comment|/**  * Provides information on the current window when computing the result of  * the aggregation.  */
end_comment

begin_interface
specifier|public
interface|interface
name|WinAggFrameResultContext
extends|extends
name|WinAggFrameContext
block|{
comment|/**    * Converts absolute index position of the given relative position.    * @param offset offset of the requested row    * @param seekType the type of offset (start of window, end of window, etc)    * @return absolute position of the requested row    */
name|Expression
name|computeIndex
parameter_list|(
name|Expression
name|offset
parameter_list|,
name|WinAggImplementor
operator|.
name|SeekType
name|seekType
parameter_list|)
function_decl|;
comment|/**    * Returns boolean the expression that checks if the given index is in    * the frame bounds.    * @param rowIndex index if the row to check    * @return expression that validates frame bounds for the given index    */
name|Expression
name|rowInFrame
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
function_decl|;
comment|/**    * Returns boolean the expression that checks if the given index is in    * the partition bounds.    * @param rowIndex index if the row to check    * @return expression that validates partition bounds for the given index    */
name|Expression
name|rowInPartition
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
function_decl|;
comment|/**    * Returns row translator for given absolute row position.    * @param rowIndex absolute index of the row.    * @return translator for the requested row    */
name|RexToLixTranslator
name|rowTranslator
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
function_decl|;
comment|/**    * Compares two rows given by absolute positions according to the order    * collation of the current window.    * @param a absolute index of the first row    * @param b absolute index of the second row    * @return result of comparison as as in {@link Comparable#compareTo}    */
name|Expression
name|compareRows
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

