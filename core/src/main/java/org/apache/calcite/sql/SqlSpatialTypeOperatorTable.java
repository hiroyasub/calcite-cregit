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
name|sql
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
name|config
operator|.
name|CalciteConnectionConfigImpl
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
name|jdbc
operator|.
name|CalciteSchema
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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|model
operator|.
name|ModelHandler
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
name|prepare
operator|.
name|CalciteCatalogReader
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
name|runtime
operator|.
name|SpatialTypeFunctions
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
name|SchemaPlus
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
name|AggregateFunctionImpl
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
name|sql
operator|.
name|fun
operator|.
name|SqlSpatialTypeFunctions
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
name|sql
operator|.
name|util
operator|.
name|SqlOperatorTables
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
name|sql
operator|.
name|validate
operator|.
name|SqlNameMatcher
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
name|List
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

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.sql.SqlSpatialTypeOperatorTable} containing  * the spatial operators and functions.  */
end_comment

begin_class
specifier|public
class|class
name|SqlSpatialTypeOperatorTable
implements|implements
name|SqlOperatorTable
block|{
specifier|private
specifier|final
name|SqlOperatorTable
name|operatorTable
decl_stmt|;
specifier|public
name|SqlSpatialTypeOperatorTable
parameter_list|()
block|{
comment|// Create a root schema to hold the spatial functions.
name|CalciteSchema
name|rootSchema
init|=
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SchemaPlus
name|schema
init|=
name|rootSchema
operator|.
name|plus
argument_list|()
decl_stmt|;
comment|// Register the spatial functions.
name|ModelHandler
operator|.
name|addFunctions
argument_list|(
name|schema
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|SpatialTypeFunctions
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Register the sql spatial functions.
name|ModelHandler
operator|.
name|addFunctions
argument_list|(
name|schema
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|SqlSpatialTypeFunctions
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Register the spatial aggregate functions.
name|schema
operator|.
name|add
argument_list|(
literal|"ST_UNION"
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|AggregateFunctionImpl
operator|.
name|create
argument_list|(
name|SpatialTypeFunctions
operator|.
name|Union
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"ST_ACCUM"
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|AggregateFunctionImpl
operator|.
name|create
argument_list|(
name|SpatialTypeFunctions
operator|.
name|Accum
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"ST_COLLECT"
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|AggregateFunctionImpl
operator|.
name|create
argument_list|(
name|SpatialTypeFunctions
operator|.
name|Collect
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a catalog reader to retrieve the operators.
name|CalciteCatalogReader
name|catalogReader
init|=
operator|new
name|CalciteCatalogReader
argument_list|(
name|rootSchema
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
operator|new
name|JavaTypeFactoryImpl
argument_list|()
argument_list|,
name|CalciteConnectionConfigImpl
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|operatorTable
operator|=
name|SqlOperatorTables
operator|.
name|of
argument_list|(
name|catalogReader
operator|.
name|getOperatorList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOperatorOverloads
parameter_list|(
name|SqlIdentifier
name|opName
parameter_list|,
annotation|@
name|Nullable
name|SqlFunctionCategory
name|category
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|,
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
name|operatorTable
operator|.
name|lookupOperatorOverloads
argument_list|(
name|opName
argument_list|,
name|category
argument_list|,
name|syntax
argument_list|,
name|operatorList
argument_list|,
name|nameMatcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|getOperatorList
parameter_list|()
block|{
return|return
name|operatorTable
operator|.
name|getOperatorList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

