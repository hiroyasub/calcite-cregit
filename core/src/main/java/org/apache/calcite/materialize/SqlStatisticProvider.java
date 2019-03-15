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
name|materialize
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
name|plan
operator|.
name|RelOptTable
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
comment|/**  * Estimates row counts for tables and columns, and whether combinations of  * columns form primary/unique and foreign keys.  *  *<p>Unlike {@link LatticeStatisticProvider}, works on raw tables and columns  * and does not need a {@link Lattice}.  *  *<p>It uses {@link org.apache.calcite.plan.RelOptTable} because that contains  * enough information to generate and execute SQL, while not being tied to a  * lattice.  *  *<p>The main implementation,  * {@link org.apache.calcite.statistic.QuerySqlStatisticProvider}, executes  * queries on a populated database. Implementations that use database statistics  * (from {@code ANALYZE TABLE}, etc.) and catalog information (e.g. primary and  * foreign key constraints) would also be possible.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlStatisticProvider
block|{
comment|/** Returns an estimate of the number of rows in {@code table}. */
name|double
name|tableCardinality
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
function_decl|;
comment|/** Returns whether a join is a foreign key; that is, whether every row in    * the referencing table is matched by at least one row in the referenced    * table.    *    *<p>For example, {@code isForeignKey(EMP, [DEPTNO], DEPT, [DEPTNO])}    * returns true.    *    *<p>To change "at least one" to "exactly one", you also need to call    * {@link #isKey}. */
name|boolean
name|isForeignKey
parameter_list|(
name|RelOptTable
name|fromTable
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fromColumns
parameter_list|,
name|RelOptTable
name|toTable
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|toColumns
parameter_list|)
function_decl|;
comment|/** Returns whether a collection of columns is a unique (or primary) key.    *    *<p>For example, {@code isKey(EMP, [DEPTNO]} returns true;    *<p>For example, {@code isKey(DEPT, [DEPTNO]} returns false. */
name|boolean
name|isKey
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|columns
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlStatisticProvider.java
end_comment

end_unit

