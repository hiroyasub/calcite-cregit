begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|mongodb
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
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|relopt
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
name|reltype
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
name|rex
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
name|Pair
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link ProjectRel} relational expression in  * MongoDB.  */
end_comment

begin_class
specifier|public
class|class
name|MongoProjectRel
extends|extends
name|ProjectRelBase
implements|implements
name|MongoRel
block|{
specifier|public
name|MongoProjectRel
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
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|int
name|flags
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
name|exps
argument_list|,
name|rowType
argument_list|,
name|flags
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
name|ProjectRelBase
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|MongoProjectRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|exps
argument_list|,
name|rowType
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|0.1
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
name|getChild
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|MongoRules
operator|.
name|RexToMongoTranslator
name|translator
init|=
operator|new
name|MongoRules
operator|.
name|RexToMongoTranslator
argument_list|(
operator|(
name|JavaTypeFactory
operator|)
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|MongoRules
operator|.
name|mongoFieldNames
argument_list|(
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|items
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
name|pair
range|:
name|getNamedProjects
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|pair
operator|.
name|right
decl_stmt|;
specifier|final
name|String
name|expr
init|=
name|pair
operator|.
name|left
operator|.
name|accept
argument_list|(
name|translator
argument_list|)
decl_stmt|;
name|items
operator|.
name|add
argument_list|(
name|expr
operator|.
name|equals
argument_list|(
literal|"'$"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
condition|?
name|MongoRules
operator|.
name|maybeQuote
argument_list|(
name|name
argument_list|)
operator|+
literal|": 1"
else|:
name|MongoRules
operator|.
name|maybeQuote
argument_list|(
name|name
argument_list|)
operator|+
literal|": "
operator|+
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|findString
init|=
name|Util
operator|.
name|toString
argument_list|(
name|items
argument_list|,
literal|"{"
argument_list|,
literal|", "
argument_list|,
literal|"}"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|aggregateString
init|=
literal|"{$project: "
operator|+
name|findString
operator|+
literal|"}"
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|op
init|=
name|Pair
operator|.
name|of
argument_list|(
name|findString
argument_list|,
name|aggregateString
argument_list|)
decl_stmt|;
name|implementor
operator|.
name|add
argument_list|(
name|op
operator|.
name|left
argument_list|,
name|op
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MongoProjectRel.java
end_comment

end_unit

