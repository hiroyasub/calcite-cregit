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
name|prepare
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRel
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
name|interpreter
operator|.
name|BindableConvention
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
name|jdbc
operator|.
name|CalcitePrepare
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
name|jdbc
operator|.
name|CalciteSchema
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
name|RelOptMaterialization
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
name|RelOptPlanner
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
name|RelOptUtil
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
name|RelShuttle
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
name|TableFunctionScan
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|logical
operator|.
name|LogicalAggregate
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
name|logical
operator|.
name|LogicalCorrelate
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
name|logical
operator|.
name|LogicalExchange
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
name|logical
operator|.
name|LogicalFilter
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
name|logical
operator|.
name|LogicalIntersect
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
name|logical
operator|.
name|LogicalJoin
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
name|logical
operator|.
name|LogicalMinus
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
name|logical
operator|.
name|LogicalProject
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
name|logical
operator|.
name|LogicalSort
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
name|logical
operator|.
name|LogicalUnion
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
name|logical
operator|.
name|LogicalValues
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
name|schema
operator|.
name|Schemas
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|StarTable
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
name|SqlNode
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
name|parser
operator|.
name|SqlParseException
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
name|parser
operator|.
name|SqlParser
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
name|sql2rel
operator|.
name|SqlToRelConverter
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
comment|/**  * Context for populating a {@link Prepare.Materialization}.  */
end_comment

begin_class
class|class
name|CalciteMaterializer
extends|extends
name|CalcitePrepareImpl
operator|.
name|CalcitePreparingStmt
block|{
specifier|public
name|CalciteMaterializer
parameter_list|(
name|CalcitePrepareImpl
name|prepare
parameter_list|,
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|,
name|CatalogReader
name|catalogReader
parameter_list|,
name|CalciteSchema
name|schema
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|super
argument_list|(
name|prepare
argument_list|,
name|context
argument_list|,
name|catalogReader
argument_list|,
name|catalogReader
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|schema
argument_list|,
name|EnumerableRel
operator|.
name|Prefer
operator|.
name|ANY
argument_list|,
name|planner
argument_list|,
name|BindableConvention
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
comment|/** Populates a materialization record, converting a table path    * (essentially a list of strings, like ["hr", "sales"]) into a table object    * that can be used in the planning process. */
name|void
name|populate
parameter_list|(
name|Materialization
name|materialization
parameter_list|)
block|{
name|SqlParser
name|parser
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|materialization
operator|.
name|sql
argument_list|)
decl_stmt|;
name|SqlNode
name|node
decl_stmt|;
try|try
block|{
name|node
operator|=
name|parser
operator|.
name|parseStmt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"parse failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|SqlToRelConverter
name|sqlToRelConverter2
init|=
name|getSqlToRelConverter
argument_list|(
name|getSqlValidator
argument_list|()
argument_list|,
name|catalogReader
argument_list|)
decl_stmt|;
name|materialization
operator|.
name|queryRel
operator|=
name|sqlToRelConverter2
operator|.
name|convertQuery
argument_list|(
name|node
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|.
name|rel
expr_stmt|;
comment|// Identify and substitute a StarTable in queryRel.
comment|//
comment|// It is possible that no StarTables match. That is OK, but the
comment|// materialization patterns that are recognized will not be as rich.
comment|//
comment|// It is possible that more than one StarTable matches. TBD: should we
comment|// take the best (whatever that means), or all of them?
name|useStar
argument_list|(
name|schema
argument_list|,
name|materialization
argument_list|)
expr_stmt|;
name|RelOptTable
name|table
init|=
name|this
operator|.
name|catalogReader
operator|.
name|getTable
argument_list|(
name|materialization
operator|.
name|materializedTable
operator|.
name|path
argument_list|()
argument_list|)
decl_stmt|;
name|materialization
operator|.
name|tableRel
operator|=
name|sqlToRelConverter2
operator|.
name|toRel
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
comment|/** Converts a relational expression to use a    * {@link StarTable} defined in {@code schema}.    * Uses the first star table that fits. */
specifier|private
name|void
name|useStar
parameter_list|(
name|CalciteSchema
name|schema
parameter_list|,
name|Materialization
name|materialization
parameter_list|)
block|{
for|for
control|(
name|Callback
name|x
range|:
name|useStar
argument_list|(
name|schema
argument_list|,
name|materialization
operator|.
name|queryRel
argument_list|)
control|)
block|{
comment|// Success -- we found a star table that matches.
name|materialization
operator|.
name|materialize
argument_list|(
name|x
operator|.
name|rel
argument_list|,
name|x
operator|.
name|starRelOptTable
argument_list|)
expr_stmt|;
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Materialization "
operator|+
name|materialization
operator|.
name|materializedTable
operator|+
literal|" matched star table "
operator|+
name|x
operator|.
name|starTable
operator|+
literal|"; query after re-write: "
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|materialization
operator|.
name|queryRel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Converts a relational expression to use a    * {@link org.apache.calcite.schema.impl.StarTable} defined in {@code schema}.    * Uses the first star table that fits. */
specifier|private
name|Iterable
argument_list|<
name|Callback
argument_list|>
name|useStar
parameter_list|(
name|CalciteSchema
name|schema
parameter_list|,
name|RelNode
name|queryRel
parameter_list|)
block|{
name|List
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|>
name|starTables
init|=
name|Schemas
operator|.
name|getStarTables
argument_list|(
name|schema
operator|.
name|root
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|starTables
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Don't waste effort converting to leaf-join form.
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|final
name|List
argument_list|<
name|Callback
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel2
init|=
name|RelOptMaterialization
operator|.
name|toLeafJoinForm
argument_list|(
name|queryRel
argument_list|)
decl_stmt|;
for|for
control|(
name|CalciteSchema
operator|.
name|TableEntry
name|starTable
range|:
name|starTables
control|)
block|{
specifier|final
name|Table
name|table
init|=
name|starTable
operator|.
name|getTable
argument_list|()
decl_stmt|;
assert|assert
name|table
operator|instanceof
name|StarTable
assert|;
name|RelOptTableImpl
name|starRelOptTable
init|=
name|RelOptTableImpl
operator|.
name|create
argument_list|(
name|catalogReader
argument_list|,
name|table
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
argument_list|,
name|starTable
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rel3
init|=
name|RelOptMaterialization
operator|.
name|tryUseStar
argument_list|(
name|rel2
argument_list|,
name|starRelOptTable
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel3
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Callback
argument_list|(
name|rel3
argument_list|,
name|starTable
argument_list|,
name|starRelOptTable
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
comment|/** Implementation of {@link RelShuttle} that returns each relational    * expression unchanged. It does not visit children. */
specifier|static
class|class
name|RelNullShuttle
implements|implements
name|RelShuttle
block|{
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableScan
name|scan
parameter_list|)
block|{
return|return
name|scan
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableFunctionScan
name|scan
parameter_list|)
block|{
return|return
name|scan
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalValues
name|values
parameter_list|)
block|{
return|return
name|values
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalFilter
name|filter
parameter_list|)
block|{
return|return
name|filter
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalProject
name|project
parameter_list|)
block|{
return|return
name|project
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalJoin
name|join
parameter_list|)
block|{
return|return
name|join
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalCorrelate
name|correlate
parameter_list|)
block|{
return|return
name|correlate
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalUnion
name|union
parameter_list|)
block|{
return|return
name|union
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalIntersect
name|intersect
parameter_list|)
block|{
return|return
name|intersect
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalMinus
name|minus
parameter_list|)
block|{
return|return
name|minus
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalAggregate
name|aggregate
parameter_list|)
block|{
return|return
name|aggregate
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalSort
name|sort
parameter_list|)
block|{
return|return
name|sort
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalExchange
name|exchange
parameter_list|)
block|{
return|return
name|exchange
return|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|RelNode
name|other
parameter_list|)
block|{
return|return
name|other
return|;
block|}
block|}
comment|/** Called when we discover a star table that matches. */
specifier|static
class|class
name|Callback
block|{
specifier|public
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|public
specifier|final
name|CalciteSchema
operator|.
name|TableEntry
name|starTable
decl_stmt|;
specifier|public
specifier|final
name|RelOptTableImpl
name|starRelOptTable
decl_stmt|;
name|Callback
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|CalciteSchema
operator|.
name|TableEntry
name|starTable
parameter_list|,
name|RelOptTableImpl
name|starRelOptTable
parameter_list|)
block|{
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|starTable
operator|=
name|starTable
expr_stmt|;
name|this
operator|.
name|starRelOptTable
operator|=
name|starRelOptTable
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CalciteMaterializer.java
end_comment

end_unit

