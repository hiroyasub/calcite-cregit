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
name|plan
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
name|jdbc
operator|.
name|JdbcRules
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
name|AbstractRelNode
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
name|externalize
operator|.
name|RelJson
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
name|logical
operator|.
name|LogicalProject
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|sameInstance
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link org.apache.calcite.rel.externalize.RelJson}.  */
end_comment

begin_class
class|class
name|RelOptPlanReaderTest
block|{
annotation|@
name|Test
name|void
name|testTypeToClass
parameter_list|()
block|{
name|RelJson
name|relJson
init|=
name|RelJson
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// in org.apache.calcite.rel package
name|assertThat
argument_list|(
name|relJson
operator|.
name|classToTypeName
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"LogicalProject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|relJson
operator|.
name|typeNameToClass
argument_list|(
literal|"LogicalProject"
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
operator|(
name|Class
operator|)
name|LogicalProject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// in org.apache.calcite.adapter.jdbc.JdbcRules outer class
name|assertThat
argument_list|(
name|relJson
operator|.
name|classToTypeName
argument_list|(
name|JdbcRules
operator|.
name|JdbcProject
operator|.
name|class
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"JdbcProject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|relJson
operator|.
name|typeNameToClass
argument_list|(
literal|"JdbcProject"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Class
operator|)
name|JdbcRules
operator|.
name|JdbcProject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
name|clazz
init|=
name|relJson
operator|.
name|typeNameToClass
argument_list|(
literal|"NonExistentRel"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"unknown type NonExistentRel"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Class
name|clazz
init|=
name|relJson
operator|.
name|typeNameToClass
argument_list|(
literal|"org.apache.calcite.rel.NonExistentRel"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"unknown type org.apache.calcite.rel.NonExistentRel"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// In this class; no special treatment. Note: '$MyRel' not '.MyRel'.
name|assertThat
argument_list|(
name|relJson
operator|.
name|classToTypeName
argument_list|(
name|MyRel
operator|.
name|class
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"org.apache.calcite.plan.RelOptPlanReaderTest$MyRel"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|relJson
operator|.
name|typeNameToClass
argument_list|(
name|MyRel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Class
operator|)
name|MyRel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Using canonical name (with '$'), not found
try|try
block|{
name|Class
name|clazz
init|=
name|relJson
operator|.
name|typeNameToClass
argument_list|(
name|MyRel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"unknown type org.apache.calcite.plan.RelOptPlanReaderTest.MyRel"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Dummy relational expression. */
specifier|static
class|class
name|MyRel
extends|extends
name|AbstractRelNode
block|{
name|MyRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

