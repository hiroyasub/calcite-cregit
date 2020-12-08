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
name|jdbc
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
name|RelOptCluster
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
name|plan
operator|.
name|RelOptTable
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
name|plan
operator|.
name|RelTraitSet
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
name|RelNode
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
name|core
operator|.
name|TableScan
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of a table in a JDBC data source.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcTableScan
extends|extends
name|TableScan
implements|implements
name|JdbcRel
block|{
specifier|public
specifier|final
name|JdbcTable
name|jdbcTable
decl_stmt|;
specifier|protected
name|JdbcTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|JdbcTable
name|jdbcTable
parameter_list|,
name|JdbcConvention
name|jdbcConvention
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|jdbcConvention
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|jdbcTable
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|jdbcTable
argument_list|,
literal|"jdbcTable"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
operator|new
name|JdbcTableScan
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|table
argument_list|,
name|jdbcTable
argument_list|,
operator|(
name|JdbcConvention
operator|)
name|castNonNull
argument_list|(
name|getConvention
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|JdbcImplementor
operator|.
name|Result
name|implement
parameter_list|(
name|JdbcImplementor
name|implementor
parameter_list|)
block|{
return|return
name|implementor
operator|.
name|result
argument_list|(
name|jdbcTable
operator|.
name|tableName
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|JdbcImplementor
operator|.
name|Clause
operator|.
name|FROM
argument_list|)
argument_list|,
name|this
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

