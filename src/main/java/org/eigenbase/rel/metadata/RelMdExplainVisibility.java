begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * RelMdExplainVisibility supplies a default implementation of {@link  * RelMetadataQuery#isVisibleInExplain} for the standard logical algebra.  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RelMdExplainVisibility
extends|extends
name|ReflectiveRelMetadataProvider
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelMdExplainVisibility
parameter_list|()
block|{
comment|// Tell superclass reflection about parameter types expected
comment|// for various metadata queries.
comment|// This corresponds to isVisibileInExplain(RelNode, SqlExplainLevel);
comment|// note that we don't specify the rel type because we always overload
comment|// on that.
name|mapParameterTypes
argument_list|(
literal|"isVisibleInExplain"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|(
name|Class
operator|)
name|SqlExplainLevel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Boolean
name|isVisibleInExplain
parameter_list|(
name|RelNode
name|rel
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

