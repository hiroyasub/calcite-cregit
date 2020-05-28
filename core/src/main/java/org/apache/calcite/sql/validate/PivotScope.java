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
name|SqlPivot
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Scope for expressions in a {@code PIVOT} clause.  */
end_comment

begin_class
specifier|public
class|class
name|PivotScope
extends|extends
name|ListScope
block|{
comment|//~ Instance fields ---------------------------------------------
specifier|private
specifier|final
name|SqlPivot
name|pivot
decl_stmt|;
comment|/** Creates a PivotScope. */
specifier|public
name|PivotScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlPivot
name|pivot
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|pivot
operator|=
name|pivot
expr_stmt|;
block|}
comment|/** By analogy with    * {@link org.apache.calcite.sql.validate.ListScope#getChildren()}, but this    * scope only has one namespace, and it is anonymous. */
specifier|public
name|SqlValidatorNamespace
name|getChild
parameter_list|()
block|{
return|return
name|requireNonNull
argument_list|(
name|validator
operator|.
name|getNamespace
argument_list|(
name|pivot
operator|.
name|query
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"namespace for pivot.query "
operator|+
name|pivot
operator|.
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlPivot
name|getNode
parameter_list|()
block|{
return|return
name|pivot
return|;
block|}
block|}
end_class

end_unit

