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
name|test
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
name|SchemaPlus
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
name|runtime
operator|.
name|Hook
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
name|tools
operator|.
name|Frameworks
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
name|RelOptCluster
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
name|RelOptSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|test
operator|.
name|SqlToRelConverterTest
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
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * Runs {@link org.eigenbase.test.SqlToRelConverterTest} with extensions.  */
end_comment

begin_class
specifier|public
class|class
name|SqlToRelConverterExtendedTest
extends|extends
name|SqlToRelConverterTest
block|{
name|Hook
operator|.
name|Closeable
name|closeable
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|this
operator|.
name|closeable
operator|=
name|Hook
operator|.
name|CONVERTED
operator|.
name|addThread
argument_list|(
operator|new
name|Function
argument_list|<
name|RelNode
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|RelNode
name|a0
parameter_list|)
block|{
name|foo
argument_list|(
name|a0
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|closeable
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|closeable
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|foo
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
comment|// Convert rel tree to JSON.
specifier|final
name|RelJsonWriter
name|writer
init|=
operator|new
name|RelJsonWriter
argument_list|()
decl_stmt|;
name|rel
operator|.
name|explain
argument_list|(
name|writer
argument_list|)
expr_stmt|;
specifier|final
name|String
name|json
init|=
name|writer
operator|.
name|asString
argument_list|()
decl_stmt|;
comment|// Find the schema. If there are no tables in the plan, we won't need one.
specifier|final
name|RelOptSchema
index|[]
name|schemas
init|=
block|{
literal|null
block|}
decl_stmt|;
name|rel
operator|.
name|accept
argument_list|(
operator|new
name|RelShuttleImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableAccessRelBase
name|scan
parameter_list|)
block|{
name|schemas
index|[
literal|0
index|]
operator|=
name|scan
operator|.
name|getTable
argument_list|()
operator|.
name|getRelOptSchema
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|scan
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Convert JSON back to rel tree.
name|Frameworks
operator|.
name|withPlanner
argument_list|(
operator|new
name|Frameworks
operator|.
name|PlannerAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|)
block|{
specifier|final
name|RelJsonReader
name|reader
init|=
operator|new
name|RelJsonReader
argument_list|(
name|cluster
argument_list|,
name|schemas
index|[
literal|0
index|]
argument_list|,
name|rootSchema
argument_list|)
decl_stmt|;
try|try
block|{
name|RelNode
name|x
init|=
name|reader
operator|.
name|read
argument_list|(
name|json
argument_list|)
decl_stmt|;
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
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlToRelConverterExtendedTest.java
end_comment

end_unit

