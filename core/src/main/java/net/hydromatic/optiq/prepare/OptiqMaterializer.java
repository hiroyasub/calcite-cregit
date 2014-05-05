begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|StarTable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqPrepare
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqSchema
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|EnumerableConvention
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|EnumerableRel
import|;
end_import

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
name|relopt
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
name|sql
operator|.
name|SqlNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
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
name|eigenbase
operator|.
name|sql2rel
operator|.
name|SqlToRelConverter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/** * Context for populating a {@link Materialization}. */
end_comment

begin_class
class|class
name|OptiqMaterializer
extends|extends
name|OptiqPrepareImpl
operator|.
name|OptiqPreparingStmt
block|{
specifier|public
name|OptiqMaterializer
parameter_list|(
name|OptiqPrepare
operator|.
name|Context
name|context
parameter_list|,
name|CatalogReader
name|catalogReader
parameter_list|,
name|OptiqSchema
name|schema
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|super
argument_list|(
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
name|EnumerableConvention
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
comment|/** Converts a relational expression to use a    * {@link net.hydromatic.optiq.impl.StarTable} defined in {@code schema}.    * Uses the first star table that fits. */
specifier|private
name|void
name|useStar
parameter_list|(
name|OptiqSchema
name|schema
parameter_list|,
name|Materialization
name|materialization
parameter_list|)
block|{
name|List
argument_list|<
name|OptiqSchema
operator|.
name|TableEntry
argument_list|>
name|starTables
init|=
name|getStarTables
argument_list|(
name|schema
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
return|return;
block|}
specifier|final
name|RelNode
name|rel2
init|=
name|RelOptMaterialization
operator|.
name|toLeafJoinForm
argument_list|(
name|materialization
operator|.
name|queryRel
argument_list|)
decl_stmt|;
for|for
control|(
name|OptiqSchema
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
comment|// Success -- we found a star table that matches.
name|materialization
operator|.
name|materialize
argument_list|(
name|rel3
argument_list|,
name|starRelOptTable
argument_list|)
expr_stmt|;
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
return|return;
block|}
block|}
block|}
comment|/** Returns the star tables defined in a schema.    * @param schema Schema */
specifier|private
name|List
argument_list|<
name|OptiqSchema
operator|.
name|TableEntry
argument_list|>
name|getStarTables
parameter_list|(
name|OptiqSchema
name|schema
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|OptiqSchema
operator|.
name|TableEntry
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|OptiqSchema
operator|.
name|TableEntry
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: Assumes that star tables are all defined in a schema called
comment|// "mat". Instead, we should look for star tables that use a given set of
comment|// tables, regardless of schema.
specifier|final
name|OptiqSchema
name|matSchema
init|=
name|schema
operator|.
name|root
argument_list|()
operator|.
name|getSubSchema
argument_list|(
literal|"mat"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|matSchema
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|OptiqSchema
operator|.
name|TableEntry
name|tis
range|:
name|matSchema
operator|.
name|tableMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|tis
operator|.
name|getTable
argument_list|()
operator|.
name|getJdbcTableType
argument_list|()
operator|==
name|Schema
operator|.
name|TableType
operator|.
name|STAR
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|tis
argument_list|)
expr_stmt|;
block|}
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
name|TableAccessRelBase
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
name|TableFunctionRelBase
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
name|ValuesRel
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
name|FilterRel
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
name|ProjectRel
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
name|JoinRel
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
name|UnionRel
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
name|IntersectRel
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
name|MinusRel
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
name|AggregateRel
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
name|SortRel
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
name|RelNode
name|other
parameter_list|)
block|{
return|return
name|other
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End OptiqMaterializer.java
end_comment

end_unit

