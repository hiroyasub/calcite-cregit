begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|materialize
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
name|jdbc
operator|.
name|OptiqRootSchema
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|collect
operator|.
name|*
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
comment|/**  * Actor that manages the state of materializations in the system.  */
end_comment

begin_class
class|class
name|MaterializationActor
block|{
comment|// Not an actor yet -- TODO make members private and add request/response
comment|// queues
specifier|final
name|Map
argument_list|<
name|MaterializationKey
argument_list|,
name|Materialization
argument_list|>
name|keyMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|QueryKey
argument_list|,
name|MaterializationKey
argument_list|>
name|keyBySql
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|TileKey
argument_list|,
name|MaterializationKey
argument_list|>
name|keyByTile
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|/** Tiles grouped by dimensionality. We use a    *  {@link TileKey} with no measures to represent a    *  dimensionality. */
specifier|final
name|Multimap
argument_list|<
name|TileKey
argument_list|,
name|TileKey
argument_list|>
name|tilesByDimensionality
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|/** A query materialized in a table, so that reading from the table gives the    * same results as executing the query. */
specifier|static
class|class
name|Materialization
block|{
specifier|final
name|MaterializationKey
name|key
decl_stmt|;
specifier|final
name|OptiqRootSchema
name|rootSchema
decl_stmt|;
name|OptiqSchema
operator|.
name|TableEntry
name|materializedTable
decl_stmt|;
specifier|final
name|String
name|sql
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
comment|/** Creates a materialization.      *      * @param key  Unique identifier of this materialization      * @param materializedTable Table that currently materializes the query.      *                          That is, executing "select * from table" will      *                          give the same results as executing the query.      *                          May be null when the materialization is created;      *                          materialization service will change the value as      * @param sql  Query that is materialized      * @param rowType Row type      */
name|Materialization
parameter_list|(
name|MaterializationKey
name|key
parameter_list|,
name|OptiqRootSchema
name|rootSchema
parameter_list|,
name|OptiqSchema
operator|.
name|TableEntry
name|materializedTable
parameter_list|,
name|String
name|sql
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|rootSchema
operator|=
name|rootSchema
expr_stmt|;
name|this
operator|.
name|materializedTable
operator|=
name|materializedTable
expr_stmt|;
comment|// may be null
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
block|}
block|}
comment|/** A materialization can be re-used if it is the same SQL, on the same    * schema, with the same path for resolving functions. */
specifier|static
class|class
name|QueryKey
block|{
specifier|final
name|String
name|sql
decl_stmt|;
specifier|final
name|OptiqSchema
name|schema
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
decl_stmt|;
name|QueryKey
parameter_list|(
name|String
name|sql
parameter_list|,
name|OptiqSchema
name|schema
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|QueryKey
operator|&&
name|sql
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|QueryKey
operator|)
name|obj
operator|)
operator|.
name|sql
argument_list|)
operator|&&
name|schema
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|QueryKey
operator|)
name|obj
operator|)
operator|.
name|schema
argument_list|)
operator|&&
name|path
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|QueryKey
operator|)
name|obj
operator|)
operator|.
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Util
operator|.
name|hashV
argument_list|(
name|sql
argument_list|,
name|schema
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MaterializationActor.java
end_comment

end_unit

