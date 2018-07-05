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
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|algorithm
operator|.
name|Algorithm
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|algorithm
operator|.
name|Progress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|algorithm
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|algorithm
operator|.
name|impl
operator|.
name|MonteCarloAlgorithm
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|algorithm
operator|.
name|util
operator|.
name|ArgumentUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Aggregate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Dialect
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Dimension
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Measure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Schema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|StatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|pentaho
operator|.
name|aggdes
operator|.
name|model
operator|.
name|Table
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
comment|/**  * Algorithm that suggests a set of initial tiles (materialized aggregate views)  * for a given lattice.  */
end_comment

begin_class
specifier|public
class|class
name|TileSuggester
block|{
specifier|private
specifier|final
name|Lattice
name|lattice
decl_stmt|;
specifier|public
name|TileSuggester
parameter_list|(
name|Lattice
name|lattice
parameter_list|)
block|{
name|this
operator|.
name|lattice
operator|=
name|lattice
expr_stmt|;
block|}
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Lattice
operator|.
name|Tile
argument_list|>
name|tiles
parameter_list|()
block|{
specifier|final
name|Algorithm
name|algorithm
init|=
operator|new
name|MonteCarloAlgorithm
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
name|Util
operator|.
name|printWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
decl_stmt|;
specifier|final
name|Progress
name|progress
init|=
operator|new
name|ArgumentUtils
operator|.
name|TextProgress
argument_list|(
name|pw
argument_list|)
decl_stmt|;
specifier|final
name|StatisticsProvider
name|statisticsProvider
init|=
operator|new
name|StatisticsProviderImpl
argument_list|(
name|lattice
argument_list|)
decl_stmt|;
specifier|final
name|double
name|f
init|=
name|statisticsProvider
operator|.
name|getFactRowCount
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Parameter
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|lattice
operator|.
name|algorithmMaxMillis
operator|>=
literal|0
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|Algorithm
operator|.
name|ParameterEnum
operator|.
name|timeLimitSeconds
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
operator|(
name|lattice
operator|.
name|algorithmMaxMillis
operator|/
literal|1000L
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|Algorithm
operator|.
name|ParameterEnum
operator|.
name|aggregateLimit
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|Algorithm
operator|.
name|ParameterEnum
operator|.
name|costLimit
argument_list|,
name|f
operator|*
literal|5d
argument_list|)
expr_stmt|;
specifier|final
name|SchemaImpl
name|schema
init|=
operator|new
name|SchemaImpl
argument_list|(
name|lattice
argument_list|,
name|statisticsProvider
argument_list|)
decl_stmt|;
specifier|final
name|Result
name|result
init|=
name|algorithm
operator|.
name|run
argument_list|(
name|schema
argument_list|,
name|map
operator|.
name|build
argument_list|()
argument_list|,
name|progress
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Lattice
operator|.
name|Tile
argument_list|>
name|tiles
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Aggregate
name|aggregate
range|:
name|result
operator|.
name|getAggregates
argument_list|()
control|)
block|{
name|tiles
operator|.
name|add
argument_list|(
name|toTile
argument_list|(
name|aggregate
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|tiles
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|Lattice
operator|.
name|Tile
name|toTile
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|)
block|{
specifier|final
name|Lattice
operator|.
name|TileBuilder
name|tileBuilder
init|=
operator|new
name|Lattice
operator|.
name|TileBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Lattice
operator|.
name|Measure
name|measure
range|:
name|lattice
operator|.
name|defaultMeasures
control|)
block|{
name|tileBuilder
operator|.
name|addMeasure
argument_list|(
name|measure
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Attribute
name|attribute
range|:
name|aggregate
operator|.
name|getAttributes
argument_list|()
control|)
block|{
name|tileBuilder
operator|.
name|addDimension
argument_list|(
operator|(
operator|(
name|AttributeImpl
operator|)
name|attribute
operator|)
operator|.
name|column
argument_list|)
expr_stmt|;
block|}
return|return
name|tileBuilder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Implementation of {@link Schema} based on a {@link Lattice}. */
specifier|private
specifier|static
class|class
name|SchemaImpl
implements|implements
name|Schema
block|{
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
specifier|final
name|TableImpl
name|table
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|AttributeImpl
argument_list|>
name|attributes
decl_stmt|;
name|SchemaImpl
parameter_list|(
name|Lattice
name|lattice
parameter_list|,
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|statisticsProvider
operator|=
name|statisticsProvider
expr_stmt|;
name|this
operator|.
name|table
operator|=
operator|new
name|TableImpl
argument_list|()
expr_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AttributeImpl
argument_list|>
name|attributeBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Lattice
operator|.
name|Column
name|column
range|:
name|lattice
operator|.
name|columns
control|)
block|{
name|attributeBuilder
operator|.
name|add
argument_list|(
operator|new
name|AttributeImpl
argument_list|(
name|column
argument_list|,
name|table
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|attributes
operator|=
name|attributeBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|Table
argument_list|>
name|getTables
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|table
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Measure
argument_list|>
name|getMeasures
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|Dimension
argument_list|>
name|getDimensions
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
specifier|public
name|StatisticsProvider
name|getStatisticsProvider
parameter_list|()
block|{
return|return
name|statisticsProvider
return|;
block|}
specifier|public
name|Dialect
name|getDialect
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|String
name|generateAggregateSql
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|columnNameList
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/** Implementation of {@link Table} based on a {@link Lattice}.    * There is only one table (in this sense of table) in a lattice.    * The algorithm does not really care about tables. */
specifier|private
specifier|static
class|class
name|TableImpl
implements|implements
name|Table
block|{
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
literal|"TABLE"
return|;
block|}
specifier|public
name|Table
name|getParent
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Implementation of {@link Attribute} based on a {@link Lattice.Column}. */
specifier|private
specifier|static
class|class
name|AttributeImpl
implements|implements
name|Attribute
block|{
specifier|private
specifier|final
name|Lattice
operator|.
name|Column
name|column
decl_stmt|;
specifier|private
specifier|final
name|TableImpl
name|table
decl_stmt|;
specifier|private
name|AttributeImpl
parameter_list|(
name|Lattice
operator|.
name|Column
name|column
parameter_list|,
name|TableImpl
name|table
parameter_list|)
block|{
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getLabel
argument_list|()
return|;
block|}
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
name|column
operator|.
name|alias
return|;
block|}
specifier|public
name|Table
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
specifier|public
name|double
name|estimateSpace
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|String
name|getCandidateColumnName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getDatatype
parameter_list|(
name|Dialect
name|dialect
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|Attribute
argument_list|>
name|getAncestorAttributes
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
comment|/** Implementation of {@link org.pentaho.aggdes.model.StatisticsProvider}    * that asks the lattice. */
specifier|private
specifier|static
class|class
name|StatisticsProviderImpl
implements|implements
name|StatisticsProvider
block|{
specifier|private
specifier|final
name|Lattice
name|lattice
decl_stmt|;
name|StatisticsProviderImpl
parameter_list|(
name|Lattice
name|lattice
parameter_list|)
block|{
name|this
operator|.
name|lattice
operator|=
name|lattice
expr_stmt|;
block|}
specifier|public
name|double
name|getFactRowCount
parameter_list|()
block|{
return|return
name|lattice
operator|.
name|getFactRowCount
argument_list|()
return|;
block|}
specifier|public
name|double
name|getRowCount
parameter_list|(
name|List
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|)
block|{
return|return
name|lattice
operator|.
name|getRowCount
argument_list|(
name|Util
operator|.
name|transform
argument_list|(
name|attributes
argument_list|,
name|input
lambda|->
operator|(
operator|(
name|AttributeImpl
operator|)
name|input
operator|)
operator|.
name|column
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|double
name|getSpace
parameter_list|(
name|List
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|double
name|getLoadTime
parameter_list|(
name|List
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|)
block|{
return|return
name|getSpace
argument_list|(
name|attributes
argument_list|)
operator|*
name|getRowCount
argument_list|(
name|attributes
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TileSuggester.java
end_comment

end_unit

