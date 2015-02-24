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
name|core
operator|.
name|Exchange
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
name|core
operator|.
name|TableScan
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
name|core
operator|.
name|Values
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

begin_comment
comment|/**  * Default implementations of the  * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Parallelism}  * metadata provider for the standard logical algebra.  *  * @see org.apache.calcite.rel.metadata.RelMetadataQuery#isPhaseTransition  * @see org.apache.calcite.rel.metadata.RelMetadataQuery#splitCount  */
end_comment

begin_class
specifier|public
class|class
name|RelMdParallelism
block|{
comment|/** Source for    * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Parallelism}. */
specifier|public
specifier|static
specifier|final
name|RelMetadataProvider
name|SOURCE
init|=
name|ReflectiveRelMetadataProvider
operator|.
name|reflectiveSource
argument_list|(
operator|new
name|RelMdParallelism
argument_list|()
argument_list|,
name|BuiltInMethod
operator|.
name|IS_PHASE_TRANSITION
operator|.
name|method
argument_list|,
name|BuiltInMethod
operator|.
name|SPLIT_COUNT
operator|.
name|method
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdParallelism
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Catch-all implementation for    * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Parallelism#isPhaseTransition()},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#isPhaseTransition    */
specifier|public
name|Boolean
name|isPhaseTransition
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Boolean
name|isPhaseTransition
parameter_list|(
name|TableScan
name|rel
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|Boolean
name|isPhaseTransition
parameter_list|(
name|Values
name|rel
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|Boolean
name|isPhaseTransition
parameter_list|(
name|Exchange
name|rel
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/** Catch-all implementation for    * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Parallelism#splitCount()},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#splitCount    */
specifier|public
name|Integer
name|splitCount
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdParallelism.java
end_comment

end_unit

