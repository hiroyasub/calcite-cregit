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

begin_comment
comment|/**  * This interface provides results based on column statistics. It may be used to  * summarize the results of applying a predicate to a column of a relational  * expression. Alternatively, it may be used to summarize aspects of the entire  * column.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelStatColumnStatistics
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Estimates the percentage of a relational expression's rows which satisfy    * a given condition. This corresponds to the metadata query {@link    * org.eigenbase.rel.metadata.RelMetadataQuery#getSelectivity}.    *    * @return an estimated percentage from 0.0 to 1.0 or null if no reliable    * estimate can be determined    */
specifier|public
name|Double
name|getSelectivity
parameter_list|()
function_decl|;
comment|/**    * Estimates the number of distinct values returned from a relational    * expression that satisfy a given condition.    *    * @return an estimate of the distinct values of a predicate or null if no    * reliable estimate can be determined    */
specifier|public
name|Double
name|getCardinality
parameter_list|()
function_decl|;
comment|/**    * Determine how many blocks on disk will be read from physical storage    * to retrieve the column values selected. This corresponds to an    * attribute set by the Broadbase server. This feature is deferred until    * we find a use for it    */
comment|// public Long getNumBlocks();
block|}
end_interface

begin_comment
comment|// End RelStatColumnStatistics.java
end_comment

end_unit

