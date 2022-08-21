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
name|sql
operator|.
name|util
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
name|prepare
operator|.
name|CalciteCatalogReader
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
name|runtime
operator|.
name|SpatialTypeFunctions
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
name|sql
operator|.
name|SqlOperator
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
name|sql
operator|.
name|SqlOperatorTable
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
name|sql
operator|.
name|fun
operator|.
name|SqlSpatialTypeFunctions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Suppliers
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Utilities for {@link SqlOperatorTable}s.  */
end_comment

begin_class
specifier|public
class|class
name|SqlOperatorTables
block|{
specifier|private
name|SqlOperatorTables
parameter_list|()
block|{
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"FunctionalExpressionCanBeFolded"
argument_list|)
specifier|private
specifier|static
specifier|final
name|Supplier
argument_list|<
name|SqlOperatorTable
argument_list|>
name|SPATIAL
init|=
name|Suppliers
operator|.
name|memoize
argument_list|(
name|SqlOperatorTables
operator|::
name|createSpatial
argument_list|)
operator|::
name|get
decl_stmt|;
specifier|private
specifier|static
name|SqlOperatorTable
name|createSpatial
parameter_list|()
block|{
return|return
name|CalciteCatalogReader
operator|.
name|operatorTable
argument_list|(
name|SpatialTypeFunctions
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|SqlSpatialTypeFunctions
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the Spatial operator table, creating it if necessary. */
specifier|public
specifier|static
name|SqlOperatorTable
name|spatialInstance
parameter_list|()
block|{
return|return
name|SPATIAL
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Creates a composite operator table. */
specifier|public
specifier|static
name|SqlOperatorTable
name|chain
parameter_list|(
name|Iterable
argument_list|<
name|SqlOperatorTable
argument_list|>
name|tables
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|SqlOperatorTable
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlOperatorTable
name|table
range|:
name|tables
control|)
block|{
name|addFlattened
argument_list|(
name|list
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
operator|new
name|ChainedSqlOperatorTable
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StatementWithEmptyBody"
argument_list|)
specifier|private
specifier|static
name|void
name|addFlattened
parameter_list|(
name|List
argument_list|<
name|SqlOperatorTable
argument_list|>
name|list
parameter_list|,
name|SqlOperatorTable
name|table
parameter_list|)
block|{
if|if
condition|(
name|table
operator|instanceof
name|ChainedSqlOperatorTable
condition|)
block|{
name|ChainedSqlOperatorTable
name|chainedTable
init|=
operator|(
name|ChainedSqlOperatorTable
operator|)
name|table
decl_stmt|;
for|for
control|(
name|SqlOperatorTable
name|table2
range|:
name|chainedTable
operator|.
name|tableList
control|)
block|{
name|addFlattened
argument_list|(
name|list
argument_list|,
name|table2
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|table
operator|instanceof
name|ImmutableListSqlOperatorTable
operator|&&
name|table
operator|.
name|getOperatorList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Table is empty and will remain empty; don't add it.
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Creates a composite operator table from an array of tables. */
specifier|public
specifier|static
name|SqlOperatorTable
name|chain
parameter_list|(
name|SqlOperatorTable
modifier|...
name|tables
parameter_list|)
block|{
return|return
name|chain
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|tables
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates an operator table that contains an immutable list of operators. */
specifier|public
specifier|static
name|SqlOperatorTable
name|of
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|SqlOperator
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|new
name|ImmutableListSqlOperatorTable
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates an operator table that contains the given operator or    * operators. */
specifier|public
specifier|static
name|SqlOperatorTable
name|of
parameter_list|(
name|SqlOperator
modifier|...
name|operators
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|operators
argument_list|)
argument_list|)
return|;
block|}
comment|/** Subclass of {@link ListSqlOperatorTable} that is immutable.    * Operators cannot be added or removed after creation. */
specifier|private
specifier|static
class|class
name|ImmutableListSqlOperatorTable
extends|extends
name|ListSqlOperatorTable
block|{
name|ImmutableListSqlOperatorTable
parameter_list|(
name|ImmutableList
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
parameter_list|)
block|{
name|super
argument_list|(
name|operatorList
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

