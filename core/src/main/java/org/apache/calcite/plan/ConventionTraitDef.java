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
name|plan
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
name|convert
operator|.
name|ConverterRule
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|graph
operator|.
name|DefaultDirectedGraph
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
name|graph
operator|.
name|DefaultEdge
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
name|graph
operator|.
name|DirectedGraph
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
name|graph
operator|.
name|Graphs
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|HashMultimap
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
name|Multimap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|MonotonicNonNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Definition of the convention trait.  * A new set of conversion information is created for  * each planner that registers at least one {@link ConverterRule} instance.  *  *<p>Conversion data is held in a {@link LoadingCache}  * with weak keys so that the JVM's garbage  * collector may reclaim the conversion data after the planner itself has been  * garbage collected. The conversion information consists of a graph of  * conversions (from one calling convention to another) and a map of graph arcs  * to {@link ConverterRule}s.  */
end_comment

begin_class
specifier|public
class|class
name|ConventionTraitDef
extends|extends
name|RelTraitDef
argument_list|<
name|Convention
argument_list|>
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|ConventionTraitDef
name|INSTANCE
init|=
operator|new
name|ConventionTraitDef
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Weak-key cache of RelOptPlanner to ConversionData. The idea is that when    * the planner goes away, so does the cache entry.    */
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|RelOptPlanner
argument_list|,
name|ConversionData
argument_list|>
name|conversionCache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|weakKeys
argument_list|()
operator|.
name|build
argument_list|(
name|CacheLoader
operator|.
name|from
argument_list|(
name|ConversionData
operator|::
operator|new
argument_list|)
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|ConventionTraitDef
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelTraitDef
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|Convention
argument_list|>
name|getTraitClass
parameter_list|()
block|{
return|return
name|Convention
operator|.
name|class
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
literal|"convention"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Convention
name|getDefault
parameter_list|()
block|{
return|return
name|Convention
operator|.
name|NONE
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerConverterRule
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|ConverterRule
name|converterRule
parameter_list|)
block|{
if|if
condition|(
name|converterRule
operator|.
name|isGuaranteed
argument_list|()
condition|)
block|{
name|ConversionData
name|conversionData
init|=
name|getConversionData
argument_list|(
name|planner
argument_list|)
decl_stmt|;
specifier|final
name|Convention
name|inConvention
init|=
operator|(
name|Convention
operator|)
name|converterRule
operator|.
name|getInTrait
argument_list|()
decl_stmt|;
specifier|final
name|Convention
name|outConvention
init|=
operator|(
name|Convention
operator|)
name|converterRule
operator|.
name|getOutTrait
argument_list|()
decl_stmt|;
name|conversionData
operator|.
name|conversionGraph
operator|.
name|addVertex
argument_list|(
name|inConvention
argument_list|)
expr_stmt|;
name|conversionData
operator|.
name|conversionGraph
operator|.
name|addVertex
argument_list|(
name|outConvention
argument_list|)
expr_stmt|;
name|conversionData
operator|.
name|conversionGraph
operator|.
name|addEdge
argument_list|(
name|inConvention
argument_list|,
name|outConvention
argument_list|)
expr_stmt|;
name|conversionData
operator|.
name|mapArcToConverterRule
operator|.
name|put
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|inConvention
argument_list|,
name|outConvention
argument_list|)
argument_list|,
name|converterRule
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|deregisterConverterRule
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|ConverterRule
name|converterRule
parameter_list|)
block|{
if|if
condition|(
name|converterRule
operator|.
name|isGuaranteed
argument_list|()
condition|)
block|{
name|ConversionData
name|conversionData
init|=
name|getConversionData
argument_list|(
name|planner
argument_list|)
decl_stmt|;
specifier|final
name|Convention
name|inConvention
init|=
operator|(
name|Convention
operator|)
name|converterRule
operator|.
name|getInTrait
argument_list|()
decl_stmt|;
specifier|final
name|Convention
name|outConvention
init|=
operator|(
name|Convention
operator|)
name|converterRule
operator|.
name|getOutTrait
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|removed
init|=
name|conversionData
operator|.
name|conversionGraph
operator|.
name|removeEdge
argument_list|(
name|inConvention
argument_list|,
name|outConvention
argument_list|)
decl_stmt|;
assert|assert
name|removed
assert|;
name|conversionData
operator|.
name|mapArcToConverterRule
operator|.
name|remove
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|inConvention
argument_list|,
name|outConvention
argument_list|)
argument_list|,
name|converterRule
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement RelTraitDef
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelNode
name|convert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|Convention
name|toConvention
parameter_list|,
name|boolean
name|allowInfiniteCostConverters
parameter_list|)
block|{
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|ConversionData
name|conversionData
init|=
name|getConversionData
argument_list|(
name|planner
argument_list|)
decl_stmt|;
specifier|final
name|Convention
name|fromConvention
init|=
name|requireNonNull
argument_list|(
name|rel
operator|.
name|getConvention
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"convention is null for rel "
operator|+
name|rel
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|Convention
argument_list|>
argument_list|>
name|conversionPaths
init|=
name|conversionData
operator|.
name|getPaths
argument_list|(
name|fromConvention
argument_list|,
name|toConvention
argument_list|)
decl_stmt|;
name|loop
label|:
for|for
control|(
name|List
argument_list|<
name|Convention
argument_list|>
name|conversionPath
range|:
name|conversionPaths
control|)
block|{
assert|assert
name|conversionPath
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|fromConvention
assert|;
assert|assert
name|conversionPath
operator|.
name|get
argument_list|(
name|conversionPath
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
name|toConvention
assert|;
name|RelNode
name|converted
init|=
name|rel
decl_stmt|;
name|Convention
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Convention
name|arc
range|:
name|conversionPath
control|)
block|{
name|RelOptCost
name|cost
init|=
name|planner
operator|.
name|getCost
argument_list|(
name|converted
argument_list|,
name|mq
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|cost
operator|==
literal|null
operator|||
name|cost
operator|.
name|isInfinite
argument_list|()
operator|)
operator|&&
operator|!
name|allowInfiniteCostConverters
condition|)
block|{
continue|continue
name|loop
continue|;
block|}
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|converted
operator|=
name|changeConvention
argument_list|(
name|converted
argument_list|,
name|previous
argument_list|,
name|arc
argument_list|,
name|conversionData
operator|.
name|mapArcToConverterRule
argument_list|)
expr_stmt|;
if|if
condition|(
name|converted
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Converter from "
operator|+
name|previous
operator|+
literal|" to "
operator|+
name|arc
operator|+
literal|" guaranteed that it could convert any relexp"
argument_list|)
throw|;
block|}
block|}
name|previous
operator|=
name|arc
expr_stmt|;
block|}
return|return
name|converted
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Tries to convert a relational expression to the target convention of an    * arc.    */
specifier|private
annotation|@
name|Nullable
name|RelNode
name|changeConvention
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Convention
name|source
parameter_list|,
name|Convention
name|target
parameter_list|,
specifier|final
name|Multimap
argument_list|<
name|Pair
argument_list|<
name|Convention
argument_list|,
name|Convention
argument_list|>
argument_list|,
name|ConverterRule
argument_list|>
name|mapArcToConverterRule
parameter_list|)
block|{
assert|assert
name|source
operator|==
name|rel
operator|.
name|getConvention
argument_list|()
assert|;
comment|// Try to apply each converter rule for this arc's source/target calling
comment|// conventions.
specifier|final
name|Pair
argument_list|<
name|Convention
argument_list|,
name|Convention
argument_list|>
name|key
init|=
name|Pair
operator|.
name|of
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|ConverterRule
name|rule
range|:
name|mapArcToConverterRule
operator|.
name|get
argument_list|(
name|key
argument_list|)
control|)
block|{
assert|assert
name|rule
operator|.
name|getInTrait
argument_list|()
operator|==
name|source
assert|;
assert|assert
name|rule
operator|.
name|getOutTrait
argument_list|()
operator|==
name|target
assert|;
name|RelNode
name|converted
init|=
name|rule
operator|.
name|convert
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|converted
operator|!=
literal|null
condition|)
block|{
return|return
name|converted
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canConvert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|Convention
name|fromConvention
parameter_list|,
name|Convention
name|toConvention
parameter_list|)
block|{
name|ConversionData
name|conversionData
init|=
name|getConversionData
argument_list|(
name|planner
argument_list|)
decl_stmt|;
return|return
name|fromConvention
operator|.
name|canConvertConvention
argument_list|(
name|toConvention
argument_list|)
operator|||
name|conversionData
operator|.
name|getShortestDistance
argument_list|(
name|fromConvention
argument_list|,
name|toConvention
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
specifier|private
name|ConversionData
name|getConversionData
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|conversionCache
operator|.
name|getUnchecked
argument_list|(
name|planner
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/** Workspace for converting from one convention to another. */
specifier|private
specifier|static
specifier|final
class|class
name|ConversionData
block|{
specifier|final
name|DirectedGraph
argument_list|<
name|Convention
argument_list|,
name|DefaultEdge
argument_list|>
name|conversionGraph
init|=
name|DefaultDirectedGraph
operator|.
name|create
argument_list|()
decl_stmt|;
comment|/**      * For a given source/target convention, there may be several possible      * conversion rules. Maps {@link DefaultEdge} to a      * collection of {@link ConverterRule} objects.      */
specifier|final
name|Multimap
argument_list|<
name|Pair
argument_list|<
name|Convention
argument_list|,
name|Convention
argument_list|>
argument_list|,
name|ConverterRule
argument_list|>
name|mapArcToConverterRule
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
name|Graphs
operator|.
expr|@
name|MonotonicNonNull
name|FrozenGraph
argument_list|<
name|Convention
argument_list|,
name|DefaultEdge
argument_list|>
name|pathMap
expr_stmt|;
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|Convention
argument_list|>
argument_list|>
name|getPaths
parameter_list|(
name|Convention
name|fromConvention
parameter_list|,
name|Convention
name|toConvention
parameter_list|)
block|{
return|return
name|getPathMap
argument_list|()
operator|.
name|getPaths
argument_list|(
name|fromConvention
argument_list|,
name|toConvention
argument_list|)
return|;
block|}
specifier|private
name|Graphs
operator|.
name|FrozenGraph
argument_list|<
name|Convention
argument_list|,
name|DefaultEdge
argument_list|>
name|getPathMap
parameter_list|()
block|{
if|if
condition|(
name|pathMap
operator|==
literal|null
condition|)
block|{
name|pathMap
operator|=
name|Graphs
operator|.
name|makeImmutable
argument_list|(
name|conversionGraph
argument_list|)
expr_stmt|;
block|}
return|return
name|pathMap
return|;
block|}
specifier|public
name|int
name|getShortestDistance
parameter_list|(
name|Convention
name|fromConvention
parameter_list|,
name|Convention
name|toConvention
parameter_list|)
block|{
return|return
name|getPathMap
argument_list|()
operator|.
name|getShortestDistance
argument_list|(
name|fromConvention
argument_list|,
name|toConvention
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

