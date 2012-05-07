begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|rel
operator|.
name|convert
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * CallingConventionTraitDef is a {@link RelTraitDef} that defines the  * calling-convention trait. A new set of conversion information is created for  * each planner that registers at least one {@link ConverterRule} instance.  *  *<p>Conversion data is held in a {@link WeakHashMap} so that the JVM's garbage  * collector may reclaim the conversion data after the planner itself has been  * garbage collected. The conversion information consists of a graph of  * conversions (from one calling convention to another) and a map of graph arcs  * to {@link ConverterRule}s.  *  * @author Stephan Zuercher  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|CallingConventionTraitDef
extends|extends
name|RelTraitDef
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|CallingConventionTraitDef
name|instance
init|=
operator|new
name|CallingConventionTraitDef
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * Weak-key map of RelOptPlanner to ConversionData. The idea is that when      * the planner goes away, so does the map entry.      */
specifier|private
specifier|final
name|WeakHashMap
argument_list|<
name|RelOptPlanner
argument_list|,
name|ConversionData
argument_list|>
name|plannerConversionMap
init|=
operator|new
name|WeakHashMap
argument_list|<
name|RelOptPlanner
argument_list|,
name|ConversionData
argument_list|>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|CallingConventionTraitDef
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelTraitDef
specifier|public
name|Class
name|getTraitClass
parameter_list|()
block|{
return|return
name|CallingConvention
operator|.
name|class
return|;
block|}
comment|// implement RelTraitDef
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
literal|"convention"
return|;
block|}
comment|// override RelTraitDef
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
name|Graph
argument_list|<
name|CallingConvention
argument_list|>
name|conversionGraph
init|=
name|conversionData
operator|.
name|conversionGraph
decl_stmt|;
specifier|final
name|MultiMap
argument_list|<
name|Graph
operator|.
name|Arc
argument_list|,
name|ConverterRule
argument_list|>
name|mapArcToConverterRule
init|=
name|conversionData
operator|.
name|mapArcToConverterRule
decl_stmt|;
specifier|final
name|Graph
operator|.
name|Arc
name|arc
init|=
name|conversionGraph
operator|.
name|createArc
argument_list|(
operator|(
name|CallingConvention
operator|)
name|converterRule
operator|.
name|getInTrait
argument_list|()
argument_list|,
operator|(
name|CallingConvention
operator|)
name|converterRule
operator|.
name|getOutTrait
argument_list|()
argument_list|)
decl_stmt|;
name|mapArcToConverterRule
operator|.
name|putMulti
argument_list|(
name|arc
argument_list|,
name|converterRule
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Graph
argument_list|<
name|CallingConvention
argument_list|>
name|conversionGraph
init|=
name|conversionData
operator|.
name|conversionGraph
decl_stmt|;
specifier|final
name|MultiMap
argument_list|<
name|Graph
operator|.
name|Arc
argument_list|,
name|ConverterRule
argument_list|>
name|mapArcToConverterRule
init|=
name|conversionData
operator|.
name|mapArcToConverterRule
decl_stmt|;
specifier|final
name|Graph
operator|.
name|Arc
name|arc
init|=
name|conversionGraph
operator|.
name|deleteArc
argument_list|(
operator|(
name|CallingConvention
operator|)
name|converterRule
operator|.
name|getInTrait
argument_list|()
argument_list|,
operator|(
name|CallingConvention
operator|)
name|converterRule
operator|.
name|getOutTrait
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|arc
operator|!=
literal|null
assert|;
name|mapArcToConverterRule
operator|.
name|removeMulti
argument_list|(
name|arc
argument_list|,
name|converterRule
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement RelTraitDef
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTrait
name|toTrait
parameter_list|,
name|boolean
name|allowInfiniteCostConverters
parameter_list|)
block|{
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
name|Graph
argument_list|<
name|CallingConvention
argument_list|>
name|conversionGraph
init|=
name|conversionData
operator|.
name|conversionGraph
decl_stmt|;
specifier|final
name|MultiMap
argument_list|<
name|Graph
operator|.
name|Arc
argument_list|,
name|ConverterRule
argument_list|>
name|mapArcToConverterRule
init|=
name|conversionData
operator|.
name|mapArcToConverterRule
decl_stmt|;
specifier|final
name|CallingConvention
name|fromConvention
init|=
name|rel
operator|.
name|getConvention
argument_list|()
decl_stmt|;
specifier|final
name|CallingConvention
name|toConvention
init|=
operator|(
name|CallingConvention
operator|)
name|toTrait
decl_stmt|;
name|Iterator
argument_list|<
name|Graph
operator|.
name|Arc
argument_list|<
name|CallingConvention
argument_list|>
index|[]
argument_list|>
name|conversionPaths
init|=
name|conversionGraph
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
while|while
condition|(
name|conversionPaths
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Graph
operator|.
name|Arc
index|[]
name|arcs
init|=
name|conversionPaths
operator|.
name|next
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|arcs
index|[
literal|0
index|]
operator|.
name|from
operator|==
name|fromConvention
operator|)
assert|;
assert|assert
operator|(
name|arcs
index|[
name|arcs
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|to
operator|==
name|toConvention
operator|)
assert|;
name|RelNode
name|converted
init|=
name|rel
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arcs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|planner
operator|.
name|getCost
argument_list|(
name|converted
argument_list|)
operator|.
name|isInfinite
argument_list|()
operator|&&
operator|!
name|allowInfiniteCostConverters
condition|)
block|{
continue|continue
name|loop
continue|;
block|}
name|converted
operator|=
name|changeConvention
argument_list|(
name|converted
argument_list|,
name|arcs
index|[
name|i
index|]
argument_list|,
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
name|Util
operator|.
name|newInternal
argument_list|(
literal|"Converter from "
operator|+
name|arcs
index|[
name|i
index|]
operator|.
name|from
operator|+
literal|" to "
operator|+
name|arcs
index|[
name|i
index|]
operator|.
name|to
operator|+
literal|" guaranteed that it could convert any relexp"
argument_list|)
throw|;
block|}
block|}
return|return
name|converted
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Tries to convert a relational expression to the target convention of an      * arc.      */
specifier|private
name|RelNode
name|changeConvention
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Graph
operator|.
name|Arc
name|arc
parameter_list|,
specifier|final
name|MultiMap
argument_list|<
name|Graph
operator|.
name|Arc
argument_list|,
name|ConverterRule
argument_list|>
name|mapArcToConverterRule
parameter_list|)
block|{
assert|assert
operator|(
name|arc
operator|.
name|from
operator|==
name|rel
operator|.
name|getConvention
argument_list|()
operator|)
assert|;
comment|// Try to apply each converter rule for this arc's source/target calling
comment|// conventions.
for|for
control|(
name|Iterator
argument_list|<
name|ConverterRule
argument_list|>
name|converterRuleIter
init|=
name|mapArcToConverterRule
operator|.
name|getMulti
argument_list|(
name|arc
argument_list|)
operator|.
name|iterator
argument_list|()
init|;
name|converterRuleIter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ConverterRule
name|converterRule
init|=
name|converterRuleIter
operator|.
name|next
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|converterRule
operator|.
name|getInTrait
argument_list|()
operator|==
name|arc
operator|.
name|from
operator|)
assert|;
assert|assert
operator|(
name|converterRule
operator|.
name|getOutTrait
argument_list|()
operator|==
name|arc
operator|.
name|to
operator|)
assert|;
name|RelNode
name|converted
init|=
name|converterRule
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
comment|// implement RelTraitDef
specifier|public
name|boolean
name|canConvert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelTrait
name|fromTrait
parameter_list|,
name|RelTrait
name|toTrait
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
name|CallingConvention
name|fromConvention
init|=
operator|(
name|CallingConvention
operator|)
name|fromTrait
decl_stmt|;
name|CallingConvention
name|toConvention
init|=
operator|(
name|CallingConvention
operator|)
name|toTrait
decl_stmt|;
return|return
name|conversionData
operator|.
name|conversionGraph
operator|.
name|getShortestPath
argument_list|(
name|fromConvention
argument_list|,
name|toConvention
argument_list|)
operator|!=
literal|null
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
if|if
condition|(
name|plannerConversionMap
operator|.
name|containsKey
argument_list|(
name|planner
argument_list|)
condition|)
block|{
return|return
name|plannerConversionMap
operator|.
name|get
argument_list|(
name|planner
argument_list|)
return|;
block|}
comment|// Create new, empty ConversionData
name|ConversionData
name|conversionData
init|=
operator|new
name|ConversionData
argument_list|()
decl_stmt|;
name|plannerConversionMap
operator|.
name|put
argument_list|(
name|planner
argument_list|,
name|conversionData
argument_list|)
expr_stmt|;
return|return
name|conversionData
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
specifier|static
specifier|final
class|class
name|ConversionData
block|{
specifier|final
name|Graph
argument_list|<
name|CallingConvention
argument_list|>
name|conversionGraph
init|=
operator|new
name|Graph
argument_list|<
name|CallingConvention
argument_list|>
argument_list|()
decl_stmt|;
comment|/**          * For a given source/target convention, there may be several possible          * conversion rules. Maps {@link org.eigenbase.util.Graph.Arc} to a          * collection of {@link ConverterRule} objects.          */
specifier|final
name|MultiMap
argument_list|<
name|Graph
operator|.
name|Arc
argument_list|,
name|ConverterRule
argument_list|>
name|mapArcToConverterRule
init|=
operator|new
name|MultiMap
argument_list|<
name|Graph
operator|.
name|Arc
argument_list|,
name|ConverterRule
argument_list|>
argument_list|()
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End CallingConventionTraitDef.java
end_comment

end_unit

