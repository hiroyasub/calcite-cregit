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
name|SqlSelect
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a scope corresponding to a LATERAL TABLE clause.    *    * @param parent  Parent scope    */
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parent
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isWithin
parameter_list|(
name|SqlValidatorScope
name|scope2
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|scope2
condition|)
block|{
return|return
literal|true
return|;
block|}
name|SqlValidatorScope
name|s
init|=
name|getValidator
argument_list|()
operator|.
name|getSelectScope
argument_list|(
operator|(
name|SqlSelect
operator|)
name|node
argument_list|)
decl_stmt|;
return|return
name|s
operator|.
name|isWithin
argument_list|(
name|scope2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

