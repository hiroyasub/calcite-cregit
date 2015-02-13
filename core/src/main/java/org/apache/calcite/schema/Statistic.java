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
name|schema
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
name|RelCollation
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
name|RelDistribution
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
name|ImmutableBitSet
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
comment|/**  * Statistics about a {@link Table}.  *  *<p>Each of the methods may return {@code null} meaning "not known".</p>  *  * @see Statistics  */
end_comment

begin_interface
specifier|public
interface|interface
name|Statistic
block|{
comment|/** Returns the approximate number of rows in the table. */
name|Double
name|getRowCount
parameter_list|()
function_decl|;
comment|/** Returns whether the given set of columns is a unique key, or a superset    * of a unique key, of the table.    */
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
function_decl|;
comment|/** Returns the collections of columns on which this table is sorted. */
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollations
parameter_list|()
function_decl|;
comment|/** Returns the distribution of the data in this table. */
name|RelDistribution
name|getDistribution
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Statistic.java
end_comment

end_unit

