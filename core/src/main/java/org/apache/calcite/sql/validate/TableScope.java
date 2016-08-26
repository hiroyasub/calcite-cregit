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
operator|.
name|validate
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
name|sql
operator|.
name|SqlNode
import|;
end_import

begin_comment
comment|/**  * The name-resolution scope of a LATERAL TABLE clause.  *  *<p>The objects visible are those in the parameters found on the left side of  * the LATERAL TABLE clause, and objects inherited from the parent scope.  */
end_comment

begin_class
class|class
name|TableScope
extends|extends
name|ListScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlNode
name|node
decl_stmt|;
comment|// The expression inside the LATERAL can only see tables before it in the FROM clause.
comment|// We use this flag to indicate whether current table is before LATERAL.
specifier|private
name|boolean
name|beforeLateral
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a scope corresponding to a LATERAL TABLE clause.    *    * @param parent   Parent scope, or null    */
name|TableScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlNode
name|node
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|beforeLateral
operator|=
literal|true
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|addChild
parameter_list|(
name|SqlValidatorNamespace
name|ns
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
if|if
condition|(
name|beforeLateral
condition|)
block|{
name|super
operator|.
name|addChild
argument_list|(
name|ns
argument_list|,
name|alias
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
specifier|public
name|void
name|meetLateral
parameter_list|()
block|{
name|this
operator|.
name|beforeLateral
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End TableScope.java
end_comment

end_unit

