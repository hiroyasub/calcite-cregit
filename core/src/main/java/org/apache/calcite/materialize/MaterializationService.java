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
name|adapter
operator|.
name|clone
operator|.
name|CloneSchema
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
name|avatica
operator|.
name|ColumnMetaData
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
name|config
operator|.
name|CalciteConnectionProperty
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
name|CalciteConnection
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
name|CalciteMetaImpl
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
name|linq4j
operator|.
name|AbstractQueryable
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
name|Enumerator
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
name|QueryProvider
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
name|tree
operator|.
name|Expression
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
name|prepare
operator|.
name|Prepare
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeImpl
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
name|Hook
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
name|util
operator|.
name|ImmutableBitSet
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
name|Pair
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
name|Util
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
name|Function
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
name|ImmutableMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_comment
comment|/**  * Manages the collection of materialized tables known to the system,  * and the process by which they become valid and invalid.  */
end_comment

begin_class
specifier|public
class|class
name|MaterializationService
block|{
specifier|private
specifier|static
specifier|final
name|MaterializationService
name|INSTANCE
init|=
operator|new
name|MaterializationService
argument_list|()
decl_stmt|;
comment|/** For testing. */
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|MaterializationService
argument_list|>
name|THREAD_INSTANCE
init|=
operator|new
name|ThreadLocal
argument_list|<
name|MaterializationService
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|MaterializationService
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|MaterializationService
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
argument_list|>
name|C
init|=
operator|new
name|Comparator
argument_list|<
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
name|o0
parameter_list|,
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
name|o1
parameter_list|)
block|{
comment|// We prefer rolling up from the table with the fewest rows.
specifier|final
name|Table
name|t0
init|=
name|o0
operator|.
name|left
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|t1
init|=
name|o1
operator|.
name|left
operator|.
name|getTable
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|Double
operator|.
name|compare
argument_list|(
name|t0
operator|.
name|getStatistic
argument_list|()
operator|.
name|getRowCount
argument_list|()
argument_list|,
name|t1
operator|.
name|getStatistic
argument_list|()
operator|.
name|getRowCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
comment|// Tie-break based on table name.
return|return
name|o0
operator|.
name|left
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|o1
operator|.
name|left
operator|.
name|name
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|MaterializationActor
name|actor
init|=
operator|new
name|MaterializationActor
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|DefaultTableFactory
name|tableFactory
init|=
operator|new
name|DefaultTableFactory
argument_list|()
decl_stmt|;
specifier|private
name|MaterializationService
parameter_list|()
block|{
block|}
comment|/** Defines a new materialization. Returns its key. */
specifier|public
name|MaterializationKey
name|defineMaterialization
parameter_list|(
specifier|final
name|CalciteSchema
name|schema
parameter_list|,
name|TileKey
name|tileKey
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewSchemaPath
parameter_list|,
specifier|final
name|String
name|suggestedTableName
parameter_list|,
name|boolean
name|create
parameter_list|,
name|boolean
name|existing
parameter_list|)
block|{
return|return
name|defineMaterialization
argument_list|(
name|schema
argument_list|,
name|tileKey
argument_list|,
name|viewSql
argument_list|,
name|viewSchemaPath
argument_list|,
name|suggestedTableName
argument_list|,
name|tableFactory
argument_list|,
name|create
argument_list|,
name|existing
argument_list|)
return|;
block|}
comment|/** Defines a new materialization. Returns its key. */
specifier|public
name|MaterializationKey
name|defineMaterialization
parameter_list|(
specifier|final
name|CalciteSchema
name|schema
parameter_list|,
name|TileKey
name|tileKey
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewSchemaPath
parameter_list|,
name|String
name|suggestedTableName
parameter_list|,
name|TableFactory
name|tableFactory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|boolean
name|existing
parameter_list|)
block|{
specifier|final
name|MaterializationActor
operator|.
name|QueryKey
name|queryKey
init|=
operator|new
name|MaterializationActor
operator|.
name|QueryKey
argument_list|(
name|viewSql
argument_list|,
name|schema
argument_list|,
name|viewSchemaPath
argument_list|)
decl_stmt|;
specifier|final
name|MaterializationKey
name|existingKey
init|=
name|actor
operator|.
name|keyBySql
operator|.
name|get
argument_list|(
name|queryKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingKey
operator|!=
literal|null
condition|)
block|{
return|return
name|existingKey
return|;
block|}
if|if
condition|(
operator|!
name|create
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|CalciteConnection
name|connection
init|=
name|CalciteMetaImpl
operator|.
name|connect
argument_list|(
name|schema
operator|.
name|root
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CalciteSchema
operator|.
name|TableEntry
name|tableEntry
decl_stmt|;
comment|// If the user says the materialization exists, first try to find a table
comment|// with the name and if none can be found, lookup a view in the schema
if|if
condition|(
name|existing
condition|)
block|{
name|tableEntry
operator|=
name|schema
operator|.
name|getTable
argument_list|(
name|suggestedTableName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|tableEntry
operator|==
literal|null
condition|)
block|{
name|tableEntry
operator|=
name|schema
operator|.
name|getTableBasedOnNullaryFunction
argument_list|(
name|suggestedTableName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|tableEntry
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|tableEntry
operator|==
literal|null
condition|)
block|{
name|tableEntry
operator|=
name|schema
operator|.
name|getTableBySql
argument_list|(
name|viewSql
argument_list|)
expr_stmt|;
block|}
name|RelDataType
name|rowType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tableEntry
operator|==
literal|null
condition|)
block|{
name|Table
name|table
init|=
name|tableFactory
operator|.
name|createTable
argument_list|(
name|schema
argument_list|,
name|viewSql
argument_list|,
name|viewSchemaPath
argument_list|)
decl_stmt|;
specifier|final
name|String
name|tableName
init|=
name|Schemas
operator|.
name|uniqueTableName
argument_list|(
name|schema
argument_list|,
name|Util
operator|.
name|first
argument_list|(
name|suggestedTableName
argument_list|,
literal|"m"
argument_list|)
argument_list|)
decl_stmt|;
name|tableEntry
operator|=
name|schema
operator|.
name|add
argument_list|(
name|tableName
argument_list|,
name|table
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|viewSql
argument_list|)
argument_list|)
expr_stmt|;
name|Hook
operator|.
name|CREATE_MATERIALIZATION
operator|.
name|run
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
name|rowType
operator|=
name|table
operator|.
name|getRowType
argument_list|(
name|connection
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rowType
operator|==
literal|null
condition|)
block|{
comment|// If we didn't validate the SQL by populating a table, validate it now.
specifier|final
name|CalcitePrepare
operator|.
name|ParseResult
name|parse
init|=
name|Schemas
operator|.
name|parse
argument_list|(
name|connection
argument_list|,
name|schema
argument_list|,
name|viewSchemaPath
argument_list|,
name|viewSql
argument_list|)
decl_stmt|;
name|rowType
operator|=
name|parse
operator|.
name|rowType
expr_stmt|;
block|}
specifier|final
name|MaterializationKey
name|key
init|=
operator|new
name|MaterializationKey
argument_list|()
decl_stmt|;
specifier|final
name|MaterializationActor
operator|.
name|Materialization
name|materialization
init|=
operator|new
name|MaterializationActor
operator|.
name|Materialization
argument_list|(
name|key
argument_list|,
name|schema
operator|.
name|root
argument_list|()
argument_list|,
name|tableEntry
argument_list|,
name|viewSql
argument_list|,
name|rowType
argument_list|,
name|viewSchemaPath
argument_list|)
decl_stmt|;
name|actor
operator|.
name|keyMap
operator|.
name|put
argument_list|(
name|materialization
operator|.
name|key
argument_list|,
name|materialization
argument_list|)
expr_stmt|;
name|actor
operator|.
name|keyBySql
operator|.
name|put
argument_list|(
name|queryKey
argument_list|,
name|materialization
operator|.
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|tileKey
operator|!=
literal|null
condition|)
block|{
name|actor
operator|.
name|keyByTile
operator|.
name|put
argument_list|(
name|tileKey
argument_list|,
name|materialization
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|key
return|;
block|}
comment|/** Checks whether a materialization is valid, and if so, returns the table    * where the data are stored. */
specifier|public
name|CalciteSchema
operator|.
name|TableEntry
name|checkValid
parameter_list|(
name|MaterializationKey
name|key
parameter_list|)
block|{
specifier|final
name|MaterializationActor
operator|.
name|Materialization
name|materialization
init|=
name|actor
operator|.
name|keyMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|materialization
operator|!=
literal|null
condition|)
block|{
return|return
name|materialization
operator|.
name|materializedTable
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Defines a tile.    *    *<p>Setting the {@code create} flag to false prevents a materialization    * from being created if one does not exist. Critically, it is set to false    * during the recursive SQL that populates a materialization. Otherwise a    * materialization would try to create itself to populate itself!    */
specifier|public
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
name|defineTile
parameter_list|(
name|Lattice
name|lattice
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measureList
parameter_list|,
name|CalciteSchema
name|schema
parameter_list|,
name|boolean
name|create
parameter_list|,
name|boolean
name|exact
parameter_list|)
block|{
return|return
name|defineTile
argument_list|(
name|lattice
argument_list|,
name|groupSet
argument_list|,
name|measureList
argument_list|,
name|schema
argument_list|,
name|create
argument_list|,
name|exact
argument_list|,
literal|"m"
operator|+
name|groupSet
argument_list|,
name|tableFactory
argument_list|)
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
name|defineTile
parameter_list|(
name|Lattice
name|lattice
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measureList
parameter_list|,
name|CalciteSchema
name|schema
parameter_list|,
name|boolean
name|create
parameter_list|,
name|boolean
name|exact
parameter_list|,
name|String
name|suggestedTableName
parameter_list|,
name|TableFactory
name|tableFactory
parameter_list|)
block|{
name|MaterializationKey
name|materializationKey
decl_stmt|;
specifier|final
name|TileKey
name|tileKey
init|=
operator|new
name|TileKey
argument_list|(
name|lattice
argument_list|,
name|groupSet
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|measureList
argument_list|)
argument_list|)
decl_stmt|;
comment|// Step 1. Look for an exact match for the tile.
name|materializationKey
operator|=
name|actor
operator|.
name|keyByTile
operator|.
name|get
argument_list|(
name|tileKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|materializationKey
operator|!=
literal|null
condition|)
block|{
specifier|final
name|CalciteSchema
operator|.
name|TableEntry
name|tableEntry
init|=
name|checkValid
argument_list|(
name|materializationKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|tableEntry
operator|!=
literal|null
condition|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|tableEntry
argument_list|,
name|tileKey
argument_list|)
return|;
block|}
block|}
comment|// Step 2. Look for a match of the tile with the same dimensionality and an
comment|// acceptable list of measures.
specifier|final
name|TileKey
name|tileKey0
init|=
operator|new
name|TileKey
argument_list|(
name|lattice
argument_list|,
name|groupSet
argument_list|,
name|ImmutableList
operator|.
expr|<
name|Lattice
operator|.
name|Measure
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|TileKey
name|tileKey1
range|:
name|actor
operator|.
name|tilesByDimensionality
operator|.
name|get
argument_list|(
name|tileKey0
argument_list|)
control|)
block|{
assert|assert
name|tileKey1
operator|.
name|dimensions
operator|.
name|equals
argument_list|(
name|groupSet
argument_list|)
assert|;
if|if
condition|(
name|allSatisfiable
argument_list|(
name|measureList
argument_list|,
name|tileKey1
argument_list|)
condition|)
block|{
name|materializationKey
operator|=
name|actor
operator|.
name|keyByTile
operator|.
name|get
argument_list|(
name|tileKey1
argument_list|)
expr_stmt|;
if|if
condition|(
name|materializationKey
operator|!=
literal|null
condition|)
block|{
specifier|final
name|CalciteSchema
operator|.
name|TableEntry
name|tableEntry
init|=
name|checkValid
argument_list|(
name|materializationKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|tableEntry
operator|!=
literal|null
condition|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|tableEntry
argument_list|,
name|tileKey1
argument_list|)
return|;
block|}
block|}
block|}
block|}
comment|// Step 3. There's nothing at the exact dimensionality. Look for a roll-up
comment|// from tiles that have a super-set of dimensions and all the measures we
comment|// need.
comment|//
comment|// If there are several roll-ups, choose the one with the fewest rows.
comment|//
comment|// TODO: Allow/deny roll-up based on a size factor. If the source is only
comment|// say 2x larger than the target, don't materialize, but if it is 3x, do.
comment|//
comment|// TODO: Use a partially-ordered set data structure, so we are not scanning
comment|// through all tiles.
if|if
condition|(
operator|!
name|exact
condition|)
block|{
specifier|final
name|PriorityQueue
argument_list|<
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|(
literal|1
argument_list|,
name|C
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TileKey
argument_list|,
name|MaterializationKey
argument_list|>
name|entry
range|:
name|actor
operator|.
name|keyByTile
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|TileKey
name|tileKey2
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|tileKey2
operator|.
name|lattice
operator|==
name|lattice
operator|&&
name|tileKey2
operator|.
name|dimensions
operator|.
name|contains
argument_list|(
name|groupSet
argument_list|)
operator|&&
operator|!
name|tileKey2
operator|.
name|dimensions
operator|.
name|equals
argument_list|(
name|groupSet
argument_list|)
operator|&&
name|allSatisfiable
argument_list|(
name|measureList
argument_list|,
name|tileKey2
argument_list|)
condition|)
block|{
name|materializationKey
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
specifier|final
name|CalciteSchema
operator|.
name|TableEntry
name|tableEntry
init|=
name|checkValid
argument_list|(
name|materializationKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|tableEntry
operator|!=
literal|null
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|tableEntry
argument_list|,
name|tileKey2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|queue
operator|.
name|peek
argument_list|()
return|;
block|}
block|}
comment|// What we need is not there. If we can't create, we're done.
if|if
condition|(
operator|!
name|create
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Step 4. Create the tile we need.
comment|//
comment|// If there were any tiles at this dimensionality, regardless of
comment|// whether they were current, create a wider tile that contains their
comment|// measures plus the currently requested measures. Then we can obsolete all
comment|// other tiles.
specifier|final
name|List
argument_list|<
name|TileKey
argument_list|>
name|obsolete
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|LinkedHashSet
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measureSet
init|=
name|Sets
operator|.
name|newLinkedHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|TileKey
name|tileKey1
range|:
name|actor
operator|.
name|tilesByDimensionality
operator|.
name|get
argument_list|(
name|tileKey0
argument_list|)
control|)
block|{
name|measureSet
operator|.
name|addAll
argument_list|(
name|tileKey1
operator|.
name|measures
argument_list|)
expr_stmt|;
name|obsolete
operator|.
name|add
argument_list|(
name|tileKey1
argument_list|)
expr_stmt|;
block|}
name|measureSet
operator|.
name|addAll
argument_list|(
name|measureList
argument_list|)
expr_stmt|;
specifier|final
name|TileKey
name|newTileKey
init|=
operator|new
name|TileKey
argument_list|(
name|lattice
argument_list|,
name|groupSet
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|measureSet
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|lattice
operator|.
name|sql
argument_list|(
name|groupSet
argument_list|,
name|newTileKey
operator|.
name|measures
argument_list|)
decl_stmt|;
name|materializationKey
operator|=
name|defineMaterialization
argument_list|(
name|schema
argument_list|,
name|newTileKey
argument_list|,
name|sql
argument_list|,
name|schema
operator|.
name|path
argument_list|(
literal|null
argument_list|)
argument_list|,
name|suggestedTableName
argument_list|,
name|tableFactory
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|materializationKey
operator|!=
literal|null
condition|)
block|{
specifier|final
name|CalciteSchema
operator|.
name|TableEntry
name|tableEntry
init|=
name|checkValid
argument_list|(
name|materializationKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|tableEntry
operator|!=
literal|null
condition|)
block|{
comment|// Obsolete all of the narrower tiles.
for|for
control|(
name|TileKey
name|tileKey1
range|:
name|obsolete
control|)
block|{
name|actor
operator|.
name|tilesByDimensionality
operator|.
name|remove
argument_list|(
name|tileKey0
argument_list|,
name|tileKey1
argument_list|)
expr_stmt|;
name|actor
operator|.
name|keyByTile
operator|.
name|remove
argument_list|(
name|tileKey1
argument_list|)
expr_stmt|;
block|}
name|actor
operator|.
name|tilesByDimensionality
operator|.
name|put
argument_list|(
name|tileKey0
argument_list|,
name|newTileKey
argument_list|)
expr_stmt|;
name|actor
operator|.
name|keyByTile
operator|.
name|put
argument_list|(
name|newTileKey
argument_list|,
name|materializationKey
argument_list|)
expr_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|tableEntry
argument_list|,
name|newTileKey
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|boolean
name|allSatisfiable
parameter_list|(
name|List
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measureList
parameter_list|,
name|TileKey
name|tileKey
parameter_list|)
block|{
comment|// A measure can be satisfied if it is contained in the measure list, or,
comment|// less obviously, if it is composed of grouping columns.
for|for
control|(
name|Lattice
operator|.
name|Measure
name|measure
range|:
name|measureList
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|tileKey
operator|.
name|measures
operator|.
name|contains
argument_list|(
name|measure
argument_list|)
operator|||
name|tileKey
operator|.
name|dimensions
operator|.
name|contains
argument_list|(
name|measure
operator|.
name|argBitSet
argument_list|()
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** Gathers a list of all materialized tables known within a given root    * schema. (Each root schema defines a disconnected namespace, with no overlap    * with the current schema. Especially in a test run, the contents of two    * root schemas may look similar.) */
specifier|public
name|List
argument_list|<
name|Prepare
operator|.
name|Materialization
argument_list|>
name|query
parameter_list|(
name|CalciteSchema
name|rootSchema
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Prepare
operator|.
name|Materialization
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
name|MaterializationActor
operator|.
name|Materialization
name|materialization
range|:
name|actor
operator|.
name|keyMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|materialization
operator|.
name|rootSchema
operator|.
name|schema
operator|==
name|rootSchema
operator|.
name|schema
operator|&&
name|materialization
operator|.
name|materializedTable
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Prepare
operator|.
name|Materialization
argument_list|(
name|materialization
operator|.
name|materializedTable
argument_list|,
name|materialization
operator|.
name|sql
argument_list|,
name|materialization
operator|.
name|viewSchemaPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
comment|/** De-registers all materialized tables in the system. */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|actor
operator|.
name|keyMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/** Used by tests, to ensure that they see their own service. */
specifier|public
specifier|static
name|void
name|setThreadLocal
parameter_list|()
block|{
name|THREAD_INSTANCE
operator|.
name|set
argument_list|(
operator|new
name|MaterializationService
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the instance of the materialization service. Usually the global    * one, but returns a thread-local one during testing (when    * {@link #setThreadLocal()} has been called by the current thread). */
specifier|public
specifier|static
name|MaterializationService
name|instance
parameter_list|()
block|{
name|MaterializationService
name|materializationService
init|=
name|THREAD_INSTANCE
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|materializationService
operator|!=
literal|null
condition|)
block|{
return|return
name|materializationService
return|;
block|}
return|return
name|INSTANCE
return|;
block|}
specifier|public
name|void
name|removeMaterialization
parameter_list|(
name|MaterializationKey
name|key
parameter_list|)
block|{
name|actor
operator|.
name|keyMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates tables that represent a materialized view.    */
specifier|public
interface|interface
name|TableFactory
block|{
name|Table
name|createTable
parameter_list|(
name|CalciteSchema
name|schema
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewSchemaPath
parameter_list|)
function_decl|;
block|}
comment|/**    * Default implementation of {@link TableFactory}.    * Creates a table using {@link CloneSchema}.    */
specifier|public
specifier|static
class|class
name|DefaultTableFactory
implements|implements
name|TableFactory
block|{
specifier|public
name|Table
name|createTable
parameter_list|(
name|CalciteSchema
name|schema
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewSchemaPath
parameter_list|)
block|{
specifier|final
name|CalciteConnection
name|connection
init|=
name|CalciteMetaImpl
operator|.
name|connect
argument_list|(
name|schema
operator|.
name|root
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableMap
argument_list|<
name|CalciteConnectionProperty
argument_list|,
name|String
argument_list|>
name|map
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CREATE_MATERIALIZATIONS
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
specifier|final
name|CalcitePrepare
operator|.
name|CalciteSignature
argument_list|<
name|Object
argument_list|>
name|calciteSignature
init|=
name|Schemas
operator|.
name|prepare
argument_list|(
name|connection
argument_list|,
name|schema
argument_list|,
name|viewSchemaPath
argument_list|,
name|viewSql
argument_list|,
name|map
argument_list|)
decl_stmt|;
return|return
name|CloneSchema
operator|.
name|createCloneTable
argument_list|(
name|connection
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|calciteSignature
operator|.
name|rowType
argument_list|)
argument_list|,
name|calciteSignature
operator|.
name|getCollationList
argument_list|()
argument_list|,
name|Lists
operator|.
name|transform
argument_list|(
name|calciteSignature
operator|.
name|columns
argument_list|,
operator|new
name|Function
argument_list|<
name|ColumnMetaData
argument_list|,
name|ColumnMetaData
operator|.
name|Rep
argument_list|>
argument_list|()
block|{
specifier|public
name|ColumnMetaData
operator|.
name|Rep
name|apply
parameter_list|(
name|ColumnMetaData
name|column
parameter_list|)
block|{
return|return
name|column
operator|.
name|type
operator|.
name|rep
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|AbstractQueryable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|DataContext
name|dataContext
init|=
name|Schemas
operator|.
name|createDataContext
argument_list|(
name|connection
argument_list|,
name|calciteSignature
operator|.
name|rootSchema
operator|.
name|plus
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|calciteSignature
operator|.
name|enumerable
argument_list|(
name|dataContext
argument_list|)
operator|.
name|enumerator
argument_list|()
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|Object
operator|.
name|class
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|QueryProvider
name|getProvider
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|DataContext
name|dataContext
init|=
name|Schemas
operator|.
name|createDataContext
argument_list|(
name|connection
argument_list|,
name|calciteSignature
operator|.
name|rootSchema
operator|.
name|plus
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|calciteSignature
operator|.
name|enumerable
argument_list|(
name|dataContext
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MaterializationService.java
end_comment

end_unit

