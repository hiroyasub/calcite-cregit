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
name|linq4j
operator|.
name|expressions
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function1
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Functions
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
name|BuiltinMethod
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
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|rules
operator|.
name|java
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
name|RelNode
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
name|ConverterRelImpl
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
name|util
operator|.
name|Pair
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
comment|/**  * Relational expression representing a scan of a table in a Mongo data source.  */
end_comment

begin_class
specifier|public
class|class
name|MongoToEnumerableConverter
extends|extends
name|ConverterRelImpl
implements|implements
name|EnumerableRel
block|{
specifier|private
specifier|final
name|PhysType
name|physType
decl_stmt|;
specifier|protected
name|MongoToEnumerableConverter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|ConventionTraitDef
operator|.
name|instance
argument_list|,
name|traits
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|physType
operator|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
operator|(
name|JavaTypeFactory
operator|)
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
operator|(
name|EnumerableConvention
operator|)
name|getConvention
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
operator|new
name|MongoToEnumerableConverter
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|PhysType
name|getPhysType
parameter_list|()
block|{
return|return
name|physType
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
literal|.1
argument_list|)
return|;
block|}
specifier|public
name|BlockExpression
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
block|{
comment|// Generates a call to "find" or "aggregate", depending upon whether
comment|// an aggregate is present.
comment|//
comment|//   ((MongoTable) schema.getTable("zips")).find(
comment|//     "{state: 'CA'}",
comment|//     "{city: 1, zipcode: 1}")
comment|//
comment|//   ((MongoTable) schema.getTable("zips")).aggregate(
comment|//     "{$filter: {state: 'CA'}}",
comment|//     "{$group: {_id: '$city', c: {$sum: 1}, p: {$sum: "$pop"}}")
specifier|final
name|BlockBuilder
name|list
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|MongoRel
operator|.
name|Implementor
name|mongoImplementor
init|=
operator|new
name|MongoRel
operator|.
name|Implementor
argument_list|()
decl_stmt|;
name|mongoImplementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getChild
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|aggCount
init|=
literal|0
decl_stmt|;
name|int
name|findCount
init|=
literal|0
decl_stmt|;
name|String
name|project
init|=
literal|null
decl_stmt|;
name|String
name|filter
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|op
range|:
name|mongoImplementor
operator|.
name|list
control|)
block|{
if|if
condition|(
name|op
operator|.
name|left
operator|==
literal|null
condition|)
block|{
operator|++
name|aggCount
expr_stmt|;
block|}
if|if
condition|(
name|op
operator|.
name|right
operator|.
name|startsWith
argument_list|(
literal|"{$match:"
argument_list|)
condition|)
block|{
name|filter
operator|=
name|op
operator|.
name|left
expr_stmt|;
operator|++
name|findCount
expr_stmt|;
block|}
if|if
condition|(
name|op
operator|.
name|right
operator|.
name|startsWith
argument_list|(
literal|"{$project:"
argument_list|)
condition|)
block|{
name|project
operator|=
name|op
operator|.
name|left
expr_stmt|;
operator|++
name|findCount
expr_stmt|;
block|}
block|}
specifier|final
name|Expression
name|fields
init|=
name|list
operator|.
name|append
argument_list|(
literal|"fields"
argument_list|,
name|constantArrayList
argument_list|(
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|table
init|=
name|list
operator|.
name|append
argument_list|(
literal|"table"
argument_list|,
name|mongoImplementor
operator|.
name|table
operator|.
name|getExpression
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|enumerable
decl_stmt|;
if|if
condition|(
name|aggCount
operator|==
literal|0
operator|&&
name|findCount
operator|<=
literal|2
condition|)
block|{
name|enumerable
operator|=
name|list
operator|.
name|append
argument_list|(
literal|"enumerable"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|table
argument_list|,
literal|"find"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|filter
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|project
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|rowType
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
operator|&&
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"_MAP"
argument_list|)
condition|?
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|,
name|List
operator|.
name|class
argument_list|)
else|:
name|fields
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Expression
name|ops
init|=
name|list
operator|.
name|append
argument_list|(
literal|"ops"
argument_list|,
name|constantArrayList
argument_list|(
name|Pair
operator|.
name|right
argument_list|(
name|mongoImplementor
operator|.
name|list
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|enumerable
operator|=
name|list
operator|.
name|append
argument_list|(
literal|"enumerable"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|table
argument_list|,
literal|"aggregate"
argument_list|,
name|fields
argument_list|,
name|ops
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|enumerable
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|list
operator|.
name|toBlock
argument_list|()
return|;
block|}
comment|/** E.g. {@code constantArrayList("x", "y")} returns    * "Arrays.asList('x', 'y')". */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|MethodCallExpression
name|constantArrayList
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltinMethod
operator|.
name|ARRAYS_AS_LIST
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|constantList
argument_list|(
name|values
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/** E.g. {@code constantList("x", "y")} returns    * {@code {ConstantExpression("x"), ConstantExpression("y")}}. */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|Expression
argument_list|>
name|constantList
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
block|{
return|return
name|Functions
operator|.
name|apply
argument_list|(
name|values
argument_list|,
operator|new
name|Function1
argument_list|<
name|T
argument_list|,
name|Expression
argument_list|>
argument_list|()
block|{
specifier|public
name|Expression
name|apply
parameter_list|(
name|T
name|a0
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|constant
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End MongoToEnumerableConverter.java
end_comment

end_unit

