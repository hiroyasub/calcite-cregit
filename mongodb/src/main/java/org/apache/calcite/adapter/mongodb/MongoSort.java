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
name|adapter
operator|.
name|mongodb
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
name|RelOptCost
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
name|RelOptPlanner
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
name|RelFieldCollation
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
name|Sort
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
name|RexLiteral
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
name|List
import|;
end_import

begin_comment
comment|/** * Implementation of {@link org.apache.calcite.rel.core.Sort} * relational expression in MongoDB. */
end_comment

begin_class
specifier|public
class|class
name|MongoSort
extends|extends
name|Sort
implements|implements
name|MongoRel
block|{
specifier|public
name|MongoSort
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelCollation
name|collation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|MongoRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|child
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|0.05
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
return|return
operator|new
name|MongoSort
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|fieldCollation
range|:
name|collation
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldCollation
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
name|name
operator|+
literal|": "
operator|+
name|direction
argument_list|(
name|fieldCollation
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
comment|// TODO: NULLS FIRST and NULLS LAST
switch|switch
condition|(
name|fieldCollation
operator|.
name|nullDirection
condition|)
block|{
case|case
name|FIRST
case|:
break|break;
case|case
name|LAST
case|:
break|break;
block|}
block|}
block|}
name|implementor
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|"{$sort: "
operator|+
name|Util
operator|.
name|toString
argument_list|(
name|keys
argument_list|,
literal|"{"
argument_list|,
literal|", "
argument_list|,
literal|"}"
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|!=
literal|null
condition|)
block|{
name|implementor
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|"{$skip: "
operator|+
operator|(
operator|(
name|RexLiteral
operator|)
name|offset
operator|)
operator|.
name|getValue
argument_list|()
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fetch
operator|!=
literal|null
condition|)
block|{
name|implementor
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|"{$limit: "
operator|+
operator|(
operator|(
name|RexLiteral
operator|)
name|fetch
operator|)
operator|.
name|getValue
argument_list|()
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|direction
parameter_list|(
name|RelFieldCollation
name|fieldCollation
parameter_list|)
block|{
switch|switch
condition|(
name|fieldCollation
operator|.
name|getDirection
argument_list|()
condition|)
block|{
case|case
name|DESCENDING
case|:
case|case
name|STRICTLY_DESCENDING
case|:
return|return
operator|-
literal|1
return|;
case|case
name|ASCENDING
case|:
case|case
name|STRICTLY_ASCENDING
case|:
default|default:
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

