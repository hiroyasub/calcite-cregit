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
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/**  * Extension to {@link SubstitutionVisitor}.  */
end_comment

begin_class
annotation|@
name|Deprecated
comment|// Kept for backward compatibility and to be removed before 2.0
specifier|public
class|class
name|MaterializedViewSubstitutionVisitor
extends|extends
name|SubstitutionVisitor
block|{
specifier|public
name|MaterializedViewSubstitutionVisitor
parameter_list|(
name|RelNode
name|target_
parameter_list|,
name|RelNode
name|query_
parameter_list|)
block|{
name|super
argument_list|(
name|target_
argument_list|,
name|query_
argument_list|,
name|DEFAULT_RULES
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MaterializedViewSubstitutionVisitor
parameter_list|(
name|RelNode
name|target_
parameter_list|,
name|RelNode
name|query_
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|target_
argument_list|,
name|query_
argument_list|,
name|DEFAULT_RULES
argument_list|,
name|relBuilderFactory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MaterializedViewSubstitutionVisitor.java
end_comment

end_unit

