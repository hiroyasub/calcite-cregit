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
comment|/**  * Table that can be scanned, optionally applying supplied filter expressions,  * and projecting a given list of columns,  * without creating an intermediate relational expression.  *  *<p>If you wish to write a table that can apply projects but not filters,  * simply decline all filters.</p>  *  * @see ScannableTable  * @see FilterableTable  */
end_comment

begin_interface
specifier|public
interface|interface
name|ProjectableFilterableTable
extends|extends
name|Table
block|{
comment|/** Returns an enumerable over the rows in this Table.    *    *<p>Each row is represented as an array of its column values.    *    *<p>The list of filters is mutable.    * If the table can implement a particular filter, it should remove that    * filter from the list.    * If it cannot implement a filter, it should leave it in the list.    * Any filters remaining will be implemented by the consuming Calcite    * operator.    *    *<p>The projects are zero-based.</p>    *    * @param root Execution context    * @param filters Mutable list of filters. The method should remove from the    *                list any filters that it cannot apply.    * @param projects List of projects. Each is the 0-based ordinal of the column    *                 to project.    * @return Enumerable over all rows that match the accepted filters, returning    * for each row an array of column values, one value for each ordinal in    * {@code projects}.    */
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|filters
parameter_list|,
name|int
index|[]
name|projects
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End ProjectableFilterableTable.java
end_comment

end_unit

