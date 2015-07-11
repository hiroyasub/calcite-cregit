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
name|sql
operator|.
name|SqlExplainLevel
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
comment|/**  * RelMdExplainVisibility supplies a default implementation of  * {@link RelMetadataQuery#isVisibleInExplain} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdExplainVisibility
block|{
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
name|BuiltInMethod
operator|.
name|EXPLAIN_VISIBILITY
operator|.
name|method
argument_list|,
operator|new
name|RelMdExplainVisibility
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdExplainVisibility
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.ExplainVisibility#isVisibleInExplain(SqlExplainLevel)},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#isVisibleInExplain(RelNode, SqlExplainLevel)    */
specifier|public
name|Boolean
name|isVisibleInExplain
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|SqlExplainLevel
name|explainLevel
parameter_list|)
block|{
comment|// no information available
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdExplainVisibility.java
end_comment

end_unit

