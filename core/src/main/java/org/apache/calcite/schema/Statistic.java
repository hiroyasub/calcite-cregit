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
name|rel
operator|.
name|RelReferentialConstraint
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
specifier|default
name|Double
name|getRowCount
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns whether the given set of columns is a unique key, or a superset    * of a unique key, of the table.    */
specifier|default
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/** Returns a list of unique keys, or null if no key exist. */
specifier|default
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getKeys
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns the collection of referential constraints (foreign-keys)    * for this table. */
specifier|default
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|getReferentialConstraints
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns the collections of columns on which this table is sorted. */
specifier|default
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollations
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Returns the distribution of the data in this table. */
specifier|default
name|RelDistribution
name|getDistribution
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_interface

end_unit

