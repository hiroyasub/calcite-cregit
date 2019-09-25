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
name|mutable
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
name|Objects
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

begin_comment
comment|/** Mutable equivalent of {@link org.apache.calcite.rel.core.Match}. */
end_comment

begin_class
specifier|public
class|class
name|MutableMatch
extends|extends
name|MutableSingleRel
block|{
specifier|public
specifier|final
name|RexNode
name|pattern
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|strictStart
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|strictEnd
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
decl_stmt|;
specifier|public
specifier|final
name|RexNode
name|after
decl_stmt|;
specifier|public
specifier|final
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
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|allRows
decl_stmt|;
specifier|public
specifier|final
name|ImmutableBitSet
name|partitionKeys
decl_stmt|;
specifier|public
specifier|final
name|RelCollation
name|orderKeys
decl_stmt|;
specifier|public
specifier|final
name|RexNode
name|interval
decl_stmt|;
specifier|private
name|MutableMatch
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
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
name|ImmutableBitSet
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RexNode
name|interval
parameter_list|)
block|{
name|super
argument_list|(
name|MutableRelType
operator|.
name|MATCH
argument_list|,
name|rowType
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|strictStart
operator|=
name|strictStart
expr_stmt|;
name|this
operator|.
name|strictEnd
operator|=
name|strictEnd
expr_stmt|;
name|this
operator|.
name|patternDefinitions
operator|=
name|patternDefinitions
expr_stmt|;
name|this
operator|.
name|measures
operator|=
name|measures
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|subsets
operator|=
name|subsets
expr_stmt|;
name|this
operator|.
name|allRows
operator|=
name|allRows
expr_stmt|;
name|this
operator|.
name|partitionKeys
operator|=
name|partitionKeys
expr_stmt|;
name|this
operator|.
name|orderKeys
operator|=
name|orderKeys
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
block|}
comment|/**    * Creates a MutableMatch.    *    */
specifier|public
specifier|static
name|MutableMatch
name|of
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
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
name|ImmutableBitSet
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RexNode
name|interval
parameter_list|)
block|{
return|return
operator|new
name|MutableMatch
argument_list|(
name|rowType
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
name|interval
argument_list|)
return|;
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
name|MutableMatch
operator|&&
name|pattern
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|pattern
argument_list|)
operator|&&
name|strictStart
operator|==
operator|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|strictStart
operator|)
operator|&&
name|strictEnd
operator|==
operator|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|strictEnd
operator|)
operator|&&
name|allRows
operator|==
operator|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|allRows
operator|)
operator|&&
name|patternDefinitions
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|patternDefinitions
argument_list|)
operator|&&
name|measures
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|measures
argument_list|)
operator|&&
name|after
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|after
argument_list|)
operator|&&
name|subsets
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|subsets
argument_list|)
operator|&&
name|partitionKeys
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|partitionKeys
argument_list|)
operator|&&
name|orderKeys
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|orderKeys
argument_list|)
operator|&&
name|interval
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|interval
argument_list|)
operator|&&
name|input
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableMatch
operator|)
name|obj
operator|)
operator|.
name|input
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
name|Objects
operator|.
name|hash
argument_list|(
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
name|interval
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|digest
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
block|{
return|return
name|buf
operator|.
name|append
argument_list|(
literal|"Match(pattern: "
argument_list|)
operator|.
name|append
argument_list|(
name|pattern
argument_list|)
operator|.
name|append
argument_list|(
literal|", strictStart: "
argument_list|)
operator|.
name|append
argument_list|(
name|strictStart
argument_list|)
operator|.
name|append
argument_list|(
literal|", strictEnd: "
argument_list|)
operator|.
name|append
argument_list|(
name|strictEnd
argument_list|)
operator|.
name|append
argument_list|(
literal|", patternDefinitions: "
argument_list|)
operator|.
name|append
argument_list|(
name|patternDefinitions
argument_list|)
operator|.
name|append
argument_list|(
literal|", measures: "
argument_list|)
operator|.
name|append
argument_list|(
name|measures
argument_list|)
operator|.
name|append
argument_list|(
literal|", after: "
argument_list|)
operator|.
name|append
argument_list|(
name|after
argument_list|)
operator|.
name|append
argument_list|(
literal|", subsets: "
argument_list|)
operator|.
name|append
argument_list|(
name|subsets
argument_list|)
operator|.
name|append
argument_list|(
literal|", allRows: "
argument_list|)
operator|.
name|append
argument_list|(
name|allRows
argument_list|)
operator|.
name|append
argument_list|(
literal|", partitionKeys: "
argument_list|)
operator|.
name|append
argument_list|(
name|partitionKeys
argument_list|)
operator|.
name|append
argument_list|(
literal|", orderKeys: "
argument_list|)
operator|.
name|append
argument_list|(
name|orderKeys
argument_list|)
operator|.
name|append
argument_list|(
literal|", interval: "
argument_list|)
operator|.
name|append
argument_list|(
name|interval
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableRel
name|clone
parameter_list|()
block|{
return|return
name|MutableMatch
operator|.
name|of
argument_list|(
name|rowType
argument_list|,
name|input
operator|.
name|clone
argument_list|()
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
name|interval
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End MutableMatch.java
end_comment

end_unit
