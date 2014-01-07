begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|stat
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sarg
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This class encapsulates statistics for a RelNode  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelStatSource
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the number of rows in a relation, as determined by statistics    *    * @return a row count, or null if one could not be determined    */
name|Double
name|getRowCount
parameter_list|()
function_decl|;
comment|/**    * Returns statistics pertaining to a column specified by the 0-based    * ordinal and the sargable predicates associated with that column. The    * second argument can be null if there are no sargable predicates on the    * column.    *    * @param ordinal   zero based column ordinal    * @param predicate associated predicates(s), evaluated as intervals    * @return filtered column statistics, or null if they could not be obtained    */
name|RelStatColumnStatistics
name|getColumnStatistics
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|SargIntervalSequence
name|predicate
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelStatSource.java
end_comment

end_unit

