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
name|metadata
operator|.
name|janino
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
name|metadata
operator|.
name|BuiltInMetadata
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
name|DefaultRelMetadataProvider
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
name|MetadataHandler
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
name|Sources
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
name|io
operator|.
name|CharStreams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test {@link RelMetadataHandlerGeneratorUtil}.  */
end_comment

begin_class
class|class
name|RelMetadataHandlerGeneratorUtilTest
block|{
specifier|private
specifier|static
specifier|final
name|Path
name|RESULT_DIR
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"build/metadata"
argument_list|)
decl_stmt|;
annotation|@
name|Test
name|void
name|testAllPredicatesGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|AllPredicates
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollationGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|Collation
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testColumnOriginGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|ColumnOrigin
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testColumnUniquenessGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCumulativeCostGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|CumulativeCost
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDistinctRowCountGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|DistinctRowCount
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDistributionGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|Distribution
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainVisibilityGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|ExplainVisibility
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExpressionLineageGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|ExpressionLineage
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLowerBoundCostGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|LowerBoundCost
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMaxRowCountGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|MaxRowCount
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMemoryGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|Memory
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMinRowCountGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|MinRowCount
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNodeTypesGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|NodeTypes
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNonCumulativeCostGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|NonCumulativeCost
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testParallelismGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|Parallelism
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPercentageOriginalRowsGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|PercentageOriginalRows
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPopulationSizeGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|PopulationSize
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPredicatesGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|Predicates
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRowCountGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|RowCount
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectivityGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|Selectivity
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSizeGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|Size
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableReferencesGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|TableReferences
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUniqueKeysGenerateHandler
parameter_list|()
block|{
name|checkGenerateHandler
argument_list|(
name|BuiltInMetadata
operator|.
name|UniqueKeys
operator|.
name|Handler
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Performance a regression test on the generated code for a given handler.    */
specifier|private
name|void
name|checkGenerateHandler
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
argument_list|>
name|handlerClass
parameter_list|)
block|{
name|RelMetadataHandlerGeneratorUtil
operator|.
name|HandlerNameAndGeneratedCode
name|nameAndGeneratedCode
init|=
name|RelMetadataHandlerGeneratorUtil
operator|.
name|generateHandler
argument_list|(
name|handlerClass
argument_list|,
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
operator|.
name|handlers
argument_list|(
name|handlerClass
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|resourcePath
init|=
name|nameAndGeneratedCode
operator|.
name|getHandlerName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"/"
argument_list|)
operator|+
literal|".java"
decl_stmt|;
name|writeActualResults
argument_list|(
name|resourcePath
argument_list|,
name|nameAndGeneratedCode
operator|.
name|getGeneratedCode
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
name|readResource
argument_list|(
name|resourcePath
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|expected
operator|.
name|contains
argument_list|(
literal|"\r"
argument_list|)
operator|:
literal|"Expected code should not contain \\r"
assert|;
assert|assert
operator|!
name|nameAndGeneratedCode
operator|.
name|getGeneratedCode
argument_list|()
operator|.
name|equals
argument_list|(
literal|"\r"
argument_list|)
operator|:
literal|"Generated code should not contain \\r"
assert|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|nameAndGeneratedCode
operator|.
name|getGeneratedCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|readResource
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
name|URL
name|url
init|=
name|castNonNull
argument_list|(
name|RelMetadataHandlerGeneratorUtilTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|resourceName
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|Reader
name|reader
init|=
name|Sources
operator|.
name|of
argument_list|(
name|url
argument_list|)
operator|.
name|reader
argument_list|()
init|)
block|{
return|return
name|CharStreams
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
operator|.
name|replace
argument_list|(
literal|"\r\n"
argument_list|,
literal|"\n"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|writeActualResults
parameter_list|(
name|String
name|resourceName
parameter_list|,
name|String
name|expectedResults
parameter_list|)
block|{
try|try
block|{
name|Path
name|target
init|=
name|RESULT_DIR
operator|.
name|resolve
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|target
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|Writer
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|target
argument_list|)
init|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|expectedResults
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

