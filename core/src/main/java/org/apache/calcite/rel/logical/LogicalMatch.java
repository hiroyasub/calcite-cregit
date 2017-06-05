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
name|rel
operator|.
name|logical
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
name|Convention
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
name|RelCollation
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
name|Match
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
name|rex
operator|.
name|RexNode
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * Sub-class of {@link Match}  * not targeted at any particular engine or calling convention.  */
end_comment

begin_class
specifier|public
class|class
name|LogicalMatch
extends|extends
name|Match
block|{
comment|/**    * Creates a LogicalMatch.    *    * @param cluster cluster    * @param traitSet Trait set    * @param input Input relational expression    * @param pattern Regular Expression defining pattern variables    * @param strictStart Whether it is a strict start pattern    * @param strictEnd Whether it is a strict end pattern    * @param patternDefinitions Pattern definitions    * @param measures Measure definitions    * @param after After match definitions    * @param subsets Subset definitions    * @param allRows Whether all rows per match (false means one row per match)    * @param partitionKeys Partition by columns    * @param orderKeys Order by columns    * @param rowType Row type    */
specifier|private
name|LogicalMatch
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexNode
name|pattern
parameter_list|,
name|boolean
name|strictStart
parameter_list|,
name|boolean
name|strictEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
parameter_list|,
name|RexNode
name|after
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SortedSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|subsets
parameter_list|,
name|boolean
name|allRows
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefinitions
argument_list|,
name|measures
argument_list|,
name|after
argument_list|,
name|subsets
argument_list|,
name|allRows
argument_list|,
name|partitionKeys
argument_list|,
name|orderKeys
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a LogicalMatch.    */
specifier|public
specifier|static
name|LogicalMatch
name|create
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RexNode
name|pattern
parameter_list|,
name|boolean
name|strictStart
parameter_list|,
name|boolean
name|strictEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
parameter_list|,
name|RexNode
name|after
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|subsets
parameter_list|,
name|boolean
name|allRows
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalMatch
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefinitions
argument_list|,
name|measures
argument_list|,
name|after
argument_list|,
name|subsets
argument_list|,
name|allRows
argument_list|,
name|partitionKeys
argument_list|,
name|orderKeys
argument_list|,
name|rowType
argument_list|)
return|;
block|}
comment|//~ Methods ------------------------------------------------------
annotation|@
name|Override
specifier|public
name|Match
name|copy
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RexNode
name|pattern
parameter_list|,
name|boolean
name|strictStart
parameter_list|,
name|boolean
name|strictEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
parameter_list|,
name|RexNode
name|after
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SortedSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|subsets
parameter_list|,
name|boolean
name|allRows
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|getCluster
argument_list|()
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalMatch
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefinitions
argument_list|,
name|measures
argument_list|,
name|after
argument_list|,
name|subsets
argument_list|,
name|allRows
argument_list|,
name|partitionKeys
argument_list|,
name|orderKeys
argument_list|,
name|rowType
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalMatch.java
end_comment

end_unit

