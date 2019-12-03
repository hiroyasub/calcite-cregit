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
name|elasticsearch
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRel
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRelImplementor
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
name|adapter
operator|.
name|enumerable
operator|.
name|JavaRowFormat
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
name|adapter
operator|.
name|enumerable
operator|.
name|PhysType
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
name|adapter
operator|.
name|enumerable
operator|.
name|PhysTypeImpl
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
name|linq4j
operator|.
name|tree
operator|.
name|BlockBuilder
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|linq4j
operator|.
name|tree
operator|.
name|MethodCallExpression
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
name|ConventionTraitDef
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
name|ConverterImpl
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
name|util
operator|.
name|BuiltInMethod
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
name|java
operator|.
name|util
operator|.
name|AbstractList
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of a table in an Elasticsearch data source.  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchToEnumerableConverter
extends|extends
name|ConverterImpl
implements|implements
name|EnumerableRel
block|{
name|ElasticsearchToEnumerableConverter
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
name|INSTANCE
argument_list|,
name|traits
argument_list|,
name|input
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
name|ElasticsearchToEnumerableConverter
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
literal|.1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|relImplementor
parameter_list|,
name|Prefer
name|prefer
parameter_list|)
block|{
specifier|final
name|BlockBuilder
name|block
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ElasticsearchRel
operator|.
name|Implementor
name|implementor
init|=
operator|new
name|ElasticsearchRel
operator|.
name|Implementor
argument_list|()
decl_stmt|;
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
specifier|final
name|RelDataType
name|rowType
init|=
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|relImplementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|rowType
argument_list|,
name|prefer
operator|.
name|prefer
argument_list|(
name|JavaRowFormat
operator|.
name|ARRAY
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|fields
init|=
name|block
operator|.
name|append
argument_list|(
literal|"fields"
argument_list|,
name|constantArrayList
argument_list|(
name|Pair
operator|.
name|zip
argument_list|(
name|ElasticsearchRules
operator|.
name|elasticsearchFieldNames
argument_list|(
name|rowType
argument_list|)
argument_list|,
operator|new
name|AbstractList
argument_list|<
name|Class
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Class
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|physType
operator|.
name|fieldClass
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|rowType
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Pair
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|table
init|=
name|block
operator|.
name|append
argument_list|(
literal|"table"
argument_list|,
name|implementor
operator|.
name|table
operator|.
name|getExpression
argument_list|(
name|ElasticsearchTable
operator|.
name|ElasticsearchQueryable
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|ops
init|=
name|block
operator|.
name|append
argument_list|(
literal|"ops"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|implementor
operator|.
name|list
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|sort
init|=
name|block
operator|.
name|append
argument_list|(
literal|"sort"
argument_list|,
name|constantArrayList
argument_list|(
name|implementor
operator|.
name|sort
argument_list|,
name|Pair
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|groupBy
init|=
name|block
operator|.
name|append
argument_list|(
literal|"groupBy"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|implementor
operator|.
name|groupBy
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|aggregations
init|=
name|block
operator|.
name|append
argument_list|(
literal|"aggregations"
argument_list|,
name|constantArrayList
argument_list|(
name|implementor
operator|.
name|aggregations
argument_list|,
name|Pair
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|mappings
init|=
name|block
operator|.
name|append
argument_list|(
literal|"mappings"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|implementor
operator|.
name|expressionItemMap
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|offset
init|=
name|block
operator|.
name|append
argument_list|(
literal|"offset"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|implementor
operator|.
name|offset
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|fetch
init|=
name|block
operator|.
name|append
argument_list|(
literal|"fetch"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|implementor
operator|.
name|fetch
argument_list|)
argument_list|)
decl_stmt|;
name|Expression
name|enumerable
init|=
name|block
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
name|ElasticsearchMethod
operator|.
name|ELASTICSEARCH_QUERYABLE_FIND
operator|.
name|method
argument_list|,
name|ops
argument_list|,
name|fields
argument_list|,
name|sort
argument_list|,
name|groupBy
argument_list|,
name|aggregations
argument_list|,
name|mappings
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
argument_list|)
decl_stmt|;
name|block
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
name|relImplementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|block
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
comment|/** E.g. {@code constantArrayList("x", "y")} returns    * "Arrays.asList('x', 'y')".    * @param values list of values    * @param clazz runtime class representing each element in the list    * @param<T> type of elements in the list    * @return method call which creates a list    */
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
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|ARRAYS_AS_LIST
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|clazz
argument_list|,
name|constantList
argument_list|(
name|values
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/** E.g. {@code constantList("x", "y")} returns    * {@code {ConstantExpression("x"), ConstantExpression("y")}}.    * @param values list of elements    * @param<T> type of elements inside this list    * @return list of constant expressions    */
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
name|values
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Expressions
operator|::
name|constant
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

