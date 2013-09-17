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
name|spark
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
name|apache
operator|.
name|spark
operator|.
name|api
operator|.
name|java
operator|.
name|JavaRDD
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|spark
operator|.
name|api
operator|.
name|java
operator|.
name|JavaSparkContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|spark
operator|.
name|api
operator|.
name|java
operator|.
name|function
operator|.
name|Function
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
name|ConverterRule
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
name|RelDataType
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
name|RelDataTypeField
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
name|RexLiteral
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
name|scala
operator|.
name|Tuple2
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
comment|/**  * Rules for the {@link SparkRel#CONVENTION Spark calling convention}.  *  * @see JdbcToSparkConverterRule  */
end_comment

begin_class
specifier|public
class|class
name|SparkRules
block|{
specifier|public
specifier|static
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|rules
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
expr|<
name|RelOptRule
operator|>
name|of
argument_list|(
name|EnumerableToSparkConverterRule
operator|.
name|INSTANCE
argument_list|,
name|SparkToEnumerableConverterRule
operator|.
name|INSTANCE
argument_list|,
name|SPARK_VALUES_RULE
argument_list|)
return|;
block|}
specifier|static
class|class
name|EnumerableToSparkConverterRule
extends|extends
name|ConverterRule
block|{
specifier|public
specifier|static
specifier|final
name|EnumerableToSparkConverterRule
name|INSTANCE
init|=
operator|new
name|EnumerableToSparkConverterRule
argument_list|()
decl_stmt|;
specifier|private
name|EnumerableToSparkConverterRule
parameter_list|()
block|{
name|super
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
name|SparkRel
operator|.
name|CONVENTION
argument_list|,
literal|"EnumerableToSparkConverterRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|EnumerableToSparkConverter
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|SparkRel
operator|.
name|CONVENTION
argument_list|)
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
specifier|static
class|class
name|SparkToEnumerableConverterRule
extends|extends
name|ConverterRule
block|{
specifier|public
specifier|static
specifier|final
name|SparkToEnumerableConverterRule
name|INSTANCE
init|=
operator|new
name|SparkToEnumerableConverterRule
argument_list|()
decl_stmt|;
specifier|private
name|SparkToEnumerableConverterRule
parameter_list|()
block|{
name|super
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|SparkRel
operator|.
name|CONVENTION
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"SparkToEnumerableConverterRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|SparkToEnumerableConverter
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
specifier|final
name|SparkValuesRule
name|SPARK_VALUES_RULE
init|=
operator|new
name|SparkValuesRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
class|class
name|SparkValuesRule
extends|extends
name|ConverterRule
block|{
specifier|private
name|SparkValuesRule
parameter_list|()
block|{
name|super
argument_list|(
name|ValuesRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|SparkRel
operator|.
name|CONVENTION
argument_list|,
literal|"SparkValuesRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|ValuesRel
name|valuesRel
init|=
operator|(
name|ValuesRel
operator|)
name|rel
decl_stmt|;
return|return
operator|new
name|SparkValuesRel
argument_list|(
name|valuesRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|valuesRel
operator|.
name|getRowType
argument_list|()
argument_list|,
name|valuesRel
operator|.
name|getTuples
argument_list|()
argument_list|,
name|valuesRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|getOutTrait
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** VALUES construct implemented in Spark. */
specifier|public
specifier|static
class|class
name|SparkValuesRel
extends|extends
name|ValuesRelBase
implements|implements
name|SparkRel
block|{
name|SparkValuesRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|rowType
argument_list|,
name|tuples
argument_list|,
name|traitSet
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
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
operator|new
name|SparkValuesRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|rowType
argument_list|,
name|tuples
argument_list|,
name|traitSet
argument_list|)
return|;
block|}
specifier|public
name|Result
name|implementSpark
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
comment|/*             return Linq4j.asSpark(                 new Object[][] {                     new Object[] {1, 2},                     new Object[] {3, 4}                 }); */
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
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
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
decl_stmt|;
specifier|final
name|Type
name|rowClass
init|=
name|physType
operator|.
name|getJavaRowType
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|tuple
range|:
name|tuples
control|)
block|{
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|literals
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RelDataTypeField
argument_list|,
name|RexLiteral
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|fields
argument_list|,
name|tuple
argument_list|)
control|)
block|{
name|literals
operator|.
name|add
argument_list|(
name|RexToLixTranslator
operator|.
name|translateLiteral
argument_list|(
name|pair
operator|.
name|right
argument_list|,
name|pair
operator|.
name|left
operator|.
name|getType
argument_list|()
argument_list|,
name|typeFactory
argument_list|,
name|RexImpTable
operator|.
name|NullAs
operator|.
name|NULL
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expressions
operator|.
name|add
argument_list|(
name|physType
operator|.
name|record
argument_list|(
name|literals
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|SparkMethod
operator|.
name|ARRAY_TO_RDD
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|SparkMethod
operator|.
name|GET_SPARK_CONTEXT
operator|.
name|method
argument_list|)
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Primitive
operator|.
name|box
argument_list|(
name|rowClass
argument_list|)
argument_list|,
name|expressions
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|builder
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|// Play area
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|JavaSparkContext
name|sc
init|=
operator|new
name|JavaSparkContext
argument_list|(
literal|"local[1]"
argument_list|,
literal|"optiq"
argument_list|)
decl_stmt|;
specifier|final
name|JavaRDD
argument_list|<
name|String
argument_list|>
name|file
init|=
name|sc
operator|.
name|textFile
argument_list|(
literal|"/usr/share/dict/words"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|file
operator|.
name|map
argument_list|(
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|distinct
argument_list|()
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|cache
argument_list|()
expr_stmt|;
name|String
name|s
init|=
name|file
operator|.
name|groupBy
argument_list|(
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|map
argument_list|(
operator|new
name|Function
argument_list|<
name|Tuple2
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|(
name|Tuple2
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|pair
parameter_list|)
block|{
return|return
name|pair
operator|.
name|_1
argument_list|()
operator|+
literal|":"
operator|+
name|pair
operator|.
name|_2
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|collect
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
specifier|final
name|JavaRDD
argument_list|<
name|Integer
argument_list|>
name|rdd
init|=
name|sc
operator|.
name|parallelize
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Integer
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"get("
operator|+
name|index
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|random
operator|.
name|nextInt
argument_list|(
literal|100
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"size"
argument_list|)
expr_stmt|;
return|return
literal|10
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rdd
operator|.
name|groupBy
argument_list|(
operator|new
name|Function
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|call
parameter_list|(
name|Integer
name|integer
parameter_list|)
block|{
return|return
name|integer
operator|%
literal|2
return|;
block|}
block|}
argument_list|)
operator|.
name|collect
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SparkRules.java
end_comment

end_unit

