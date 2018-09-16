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
name|spark
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
name|tree
operator|.
name|Types
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
name|FlatMapFunction
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
name|Method
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

begin_comment
comment|/**  * Built-in methods in the Spark adapter.  *  * @see org.apache.calcite.util.BuiltInMethod  */
end_comment

begin_enum
specifier|public
enum|enum
name|SparkMethod
block|{
name|AS_ENUMERABLE
argument_list|(
name|SparkRuntime
operator|.
name|class
argument_list|,
literal|"asEnumerable"
argument_list|,
name|JavaRDD
operator|.
name|class
argument_list|)
block|,
name|ARRAY_TO_RDD
argument_list|(
name|SparkRuntime
operator|.
name|class
argument_list|,
literal|"createRdd"
argument_list|,
name|JavaSparkContext
operator|.
name|class
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
block|,
name|CREATE_RDD
argument_list|(
name|SparkRuntime
operator|.
name|class
argument_list|,
literal|"createRdd"
argument_list|,
name|JavaSparkContext
operator|.
name|class
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|)
block|,
name|GET_SPARK_CONTEXT
argument_list|(
name|SparkRuntime
operator|.
name|class
argument_list|,
literal|"getSparkContext"
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
block|,
name|RDD_FLAT_MAP
argument_list|(
name|JavaRDD
operator|.
name|class
argument_list|,
literal|"flatMap"
argument_list|,
name|FlatMapFunction
operator|.
name|class
argument_list|)
block|,
name|FLAT_MAP_FUNCTION_CALL
argument_list|(
name|FlatMapFunction
operator|.
name|class
argument_list|,
literal|"call"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|;
specifier|public
specifier|final
name|Method
name|method
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|HashMap
argument_list|<
name|Method
argument_list|,
name|SparkMethod
argument_list|>
name|MAP
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|SparkMethod
name|method
range|:
name|SparkMethod
operator|.
name|values
argument_list|()
control|)
block|{
name|MAP
operator|.
name|put
argument_list|(
name|method
operator|.
name|method
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
block|}
name|SparkMethod
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
modifier|...
name|argumentTypes
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|clazz
argument_list|,
name|methodName
argument_list|,
name|argumentTypes
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|SparkMethod
name|lookup
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
return|return
name|MAP
operator|.
name|get
argument_list|(
name|method
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End SparkMethod.java
end_comment

end_unit

