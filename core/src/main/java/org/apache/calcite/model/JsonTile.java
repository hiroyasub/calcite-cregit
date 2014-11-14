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
name|model
package|;
end_package

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
comment|/**  * Materialized view within a {@link org.apache.calcite.model.JsonLattice}.  *  *<p>A tile is defined in terms of its dimensionality (the grouping columns,  * drawn from the lattice) and measures (aggregate functions applied to  * lattice columns).  *  * @see JsonRoot Description of schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonTile
block|{
comment|/** List of grouping columns that define this tile.    *    *<p>Elements are either strings (column names unique within the lattice)    * or string lists (pairs of table alias and column name). */
specifier|public
specifier|final
name|List
name|dimensions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|/** List of measures in this tile.    *    *<p>If not specified, uses {@link JsonLattice#defaultMeasures}.    */
specifier|public
name|List
argument_list|<
name|JsonMeasure
argument_list|>
name|measures
decl_stmt|;
specifier|public
name|void
name|accept
parameter_list|(
name|ModelHandler
name|handler
parameter_list|)
block|{
name|handler
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JsonTile.java
end_comment

end_unit

