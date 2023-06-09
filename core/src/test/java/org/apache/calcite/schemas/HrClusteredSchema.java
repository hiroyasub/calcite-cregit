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
name|schemas
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
name|DataContext
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
name|Enumerable
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
name|Linq4j
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
name|RelCollations
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|schema
operator|.
name|ScannableTable
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
name|schema
operator|.
name|Statistic
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
name|schema
operator|.
name|Statistics
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|AbstractSchema
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
name|schema
operator|.
name|impl
operator|.
name|AbstractTable
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * A typical HR schema with employees (emps) and departments (depts) tables that are naturally  * ordered based on their primary keys representing clustered tables.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|HrClusteredSchema
extends|extends
name|AbstractSchema
block|{
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|tables
decl_stmt|;
specifier|public
name|HrClusteredSchema
parameter_list|()
block|{
name|tables
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|Table
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"emps"
argument_list|,
operator|new
name|PkClusteredTable
argument_list|(
name|factory
lambda|->
operator|new
name|RelDataTypeFactory
operator|.
name|Builder
argument_list|(
name|factory
argument_list|)
operator|.
name|add
argument_list|(
literal|"empid"
argument_list|,
name|factory
operator|.
name|createJavaType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"deptno"
argument_list|,
name|factory
operator|.
name|createJavaType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|factory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"salary"
argument_list|,
name|factory
operator|.
name|createJavaType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"commission"
argument_list|,
name|factory
operator|.
name|createJavaType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|100
block|,
literal|10
block|,
literal|"Bill"
block|,
literal|10000
block|,
literal|1000
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|110
block|,
literal|10
block|,
literal|"Theodore"
block|,
literal|11500
block|,
literal|250
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|150
block|,
literal|10
block|,
literal|"Sebastian"
block|,
literal|7000
block|,
literal|null
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|200
block|,
literal|20
block|,
literal|"Eric"
block|,
literal|8000
block|,
literal|500
block|}
argument_list|)
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"depts"
argument_list|,
operator|new
name|PkClusteredTable
argument_list|(
name|factory
lambda|->
operator|new
name|RelDataTypeFactory
operator|.
name|Builder
argument_list|(
name|factory
argument_list|)
operator|.
name|add
argument_list|(
literal|"deptno"
argument_list|,
name|factory
operator|.
name|createJavaType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|factory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|10
block|,
literal|"Sales"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|30
block|,
literal|"Marketing"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|40
block|,
literal|"HR"
block|}
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
return|return
name|tables
return|;
block|}
comment|/**    * A table sorted (ascending direction and nulls last) on the primary key.    */
specifier|private
specifier|static
class|class
name|PkClusteredTable
extends|extends
name|AbstractTable
implements|implements
name|ScannableTable
block|{
specifier|private
specifier|final
name|ImmutableBitSet
name|pkColumns
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|data
decl_stmt|;
specifier|private
specifier|final
name|Function
argument_list|<
name|RelDataTypeFactory
argument_list|,
name|RelDataType
argument_list|>
name|typeBuilder
decl_stmt|;
name|PkClusteredTable
parameter_list|(
name|Function
argument_list|<
name|RelDataTypeFactory
argument_list|,
name|RelDataType
argument_list|>
name|dataTypeBuilder
parameter_list|,
name|ImmutableBitSet
name|pkColumns
parameter_list|,
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|typeBuilder
operator|=
name|dataTypeBuilder
expr_stmt|;
name|this
operator|.
name|pkColumns
operator|=
name|pkColumns
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|collationFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|key
range|:
name|pkColumns
control|)
block|{
name|collationFields
operator|.
name|add
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|key
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|,
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|LAST
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Statistics
operator|.
name|of
argument_list|(
name|data
operator|.
name|size
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|pkColumns
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
name|collationFields
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
specifier|final
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeBuilder
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
specifier|final
name|DataContext
name|root
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|data
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

