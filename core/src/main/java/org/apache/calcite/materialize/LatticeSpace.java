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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|Util
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
name|AttributedDirectedGraph
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
name|mapping
operator|.
name|IntPair
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|initialization
operator|.
name|qual
operator|.
name|NotOnlyInitialized
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/** Space within which lattices exist. */
end_comment

begin_class
class|class
name|LatticeSpace
block|{
specifier|final
name|SqlStatisticProvider
name|statisticProvider
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|LatticeTable
argument_list|>
name|tableMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"assignment.type.incompatible"
argument_list|)
specifier|final
annotation|@
name|NotOnlyInitialized
name|AttributedDirectedGraph
argument_list|<
name|LatticeTable
argument_list|,
name|Step
argument_list|>
name|g
init|=
operator|new
name|AttributedDirectedGraph
argument_list|<>
argument_list|(
operator|new
name|Step
operator|.
name|Factory
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|String
argument_list|>
name|simpleTableNames
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|simpleNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Root nodes, indexed by digest. */
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|LatticeRootNode
argument_list|>
name|nodeMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|ImmutableList
argument_list|<
name|Step
argument_list|>
argument_list|,
name|Path
argument_list|>
name|pathMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|LatticeTable
argument_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
argument_list|>
name|tableExpressions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|LatticeSpace
parameter_list|(
name|SqlStatisticProvider
name|statisticProvider
parameter_list|)
block|{
name|this
operator|.
name|statisticProvider
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|statisticProvider
argument_list|)
expr_stmt|;
block|}
comment|/** Derives a unique name for a table, qualifying with schema name only if    * necessary. */
name|String
name|simpleName
parameter_list|(
name|LatticeTable
name|table
parameter_list|)
block|{
return|return
name|simpleName
argument_list|(
name|table
operator|.
name|t
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
return|;
block|}
name|String
name|simpleName
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
return|return
name|simpleName
argument_list|(
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
return|;
block|}
name|String
name|simpleName
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|table
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|simpleTableNames
operator|.
name|get
argument_list|(
name|table
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
specifier|final
name|String
name|name2
init|=
name|Util
operator|.
name|last
argument_list|(
name|table
argument_list|)
decl_stmt|;
if|if
condition|(
name|simpleNames
operator|.
name|add
argument_list|(
name|name2
argument_list|)
condition|)
block|{
name|simpleTableNames
operator|.
name|put
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|table
argument_list|)
argument_list|,
name|name2
argument_list|)
expr_stmt|;
return|return
name|name2
return|;
block|}
specifier|final
name|String
name|name3
init|=
name|table
operator|.
name|toString
argument_list|()
decl_stmt|;
name|simpleTableNames
operator|.
name|put
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|table
argument_list|)
argument_list|,
name|name3
argument_list|)
expr_stmt|;
return|return
name|name3
return|;
block|}
name|LatticeTable
name|register
parameter_list|(
name|RelOptTable
name|t
parameter_list|)
block|{
specifier|final
name|LatticeTable
name|table
init|=
name|tableMap
operator|.
name|get
argument_list|(
name|t
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
return|return
name|table
return|;
block|}
specifier|final
name|LatticeTable
name|table2
init|=
operator|new
name|LatticeTable
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|tableMap
operator|.
name|put
argument_list|(
name|t
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|table2
argument_list|)
expr_stmt|;
name|g
operator|.
name|addVertex
argument_list|(
name|table2
argument_list|)
expr_stmt|;
return|return
name|table2
return|;
block|}
name|Step
name|addEdge
parameter_list|(
name|LatticeTable
name|source
parameter_list|,
name|LatticeTable
name|target
parameter_list|,
name|List
argument_list|<
name|IntPair
argument_list|>
name|keys
parameter_list|)
block|{
name|keys
operator|=
name|sortUnique
argument_list|(
name|keys
argument_list|)
expr_stmt|;
specifier|final
name|Step
name|step
init|=
name|g
operator|.
name|addEdge
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
name|keys
argument_list|)
decl_stmt|;
if|if
condition|(
name|step
operator|!=
literal|null
condition|)
block|{
return|return
name|step
return|;
block|}
for|for
control|(
name|Step
name|step2
range|:
name|g
operator|.
name|getEdges
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
control|)
block|{
if|if
condition|(
name|step2
operator|.
name|keys
operator|.
name|equals
argument_list|(
name|keys
argument_list|)
condition|)
block|{
return|return
name|step2
return|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"addEdge failed, yet no edge present"
argument_list|)
throw|;
block|}
comment|/** Returns a list of {@link IntPair} that is sorted and unique. */
specifier|static
name|List
argument_list|<
name|IntPair
argument_list|>
name|sortUnique
parameter_list|(
name|List
argument_list|<
name|IntPair
argument_list|>
name|keys
parameter_list|)
block|{
if|if
condition|(
name|keys
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// list may not be sorted; sort it
name|keys
operator|=
name|IntPair
operator|.
name|ORDERING
operator|.
name|immutableSortedCopy
argument_list|(
name|keys
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|IntPair
operator|.
name|ORDERING
operator|.
name|isStrictlyOrdered
argument_list|(
name|keys
argument_list|)
condition|)
block|{
comment|// list may contain duplicates; sort and eliminate duplicates
specifier|final
name|Set
argument_list|<
name|IntPair
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|IntPair
operator|.
name|ORDERING
argument_list|)
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|keys
argument_list|)
expr_stmt|;
name|keys
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|keys
return|;
block|}
comment|/** Returns a list of {@link IntPair}, transposing source and target fields,    * and ensuring the result is sorted and unique. */
specifier|static
name|List
argument_list|<
name|IntPair
argument_list|>
name|swap
parameter_list|(
name|List
argument_list|<
name|IntPair
argument_list|>
name|keys
parameter_list|)
block|{
return|return
name|sortUnique
argument_list|(
name|Util
operator|.
name|transform
argument_list|(
name|keys
argument_list|,
name|x
lambda|->
name|IntPair
operator|.
name|of
argument_list|(
name|x
operator|.
name|target
argument_list|,
name|x
operator|.
name|source
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
name|Path
name|addPath
parameter_list|(
name|List
argument_list|<
name|Step
argument_list|>
name|steps
parameter_list|)
block|{
specifier|final
name|ImmutableList
argument_list|<
name|Step
argument_list|>
name|key
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|steps
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|path
init|=
name|pathMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
return|return
name|path
return|;
block|}
specifier|final
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
name|key
argument_list|,
name|pathMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|path2
argument_list|)
expr_stmt|;
return|return
name|path2
return|;
block|}
comment|/** Registers an expression as a derived column of a given table.    *    *<p>Its ordinal is the number of fields in the row type plus the ordinal    * of the extended expression. For example, if a table has 10 fields then its    * derived columns will have ordinals 10, 11, 12 etc. */
name|int
name|registerExpression
parameter_list|(
name|LatticeTable
name|table
parameter_list|,
name|RexNode
name|e
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|expressions
init|=
name|tableExpressions
operator|.
name|computeIfAbsent
argument_list|(
name|table
argument_list|,
name|t
lambda|->
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|table
operator|.
name|t
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
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
name|expressions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|expressions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|fieldCount
operator|+
name|i
return|;
block|}
block|}
specifier|final
name|int
name|result
init|=
name|fieldCount
operator|+
name|expressions
operator|.
name|size
argument_list|()
decl_stmt|;
name|expressions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Returns the name of field {@code field} of {@code table}.    *    *<p>If the field is derived (see    * {@link #registerExpression(LatticeTable, RexNode)}) its name is its    * {@link RexNode#toString()}. */
specifier|public
name|String
name|fieldName
parameter_list|(
name|LatticeTable
name|table
parameter_list|,
name|int
name|field
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
init|=
name|table
operator|.
name|t
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|fieldList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|<
name|fieldCount
condition|)
block|{
return|return
name|fieldList
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexNodes
init|=
name|tableExpressions
operator|.
name|get
argument_list|(
name|table
argument_list|)
decl_stmt|;
assert|assert
name|rexNodes
operator|!=
literal|null
operator|:
literal|"no expressions found for table "
operator|+
name|table
assert|;
return|return
name|rexNodes
operator|.
name|get
argument_list|(
name|field
operator|-
name|fieldCount
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

