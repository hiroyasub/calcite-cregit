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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link SqlValidatorNamespace} that delegates all methods  * to an underlying object.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DelegatingNamespace
implements|implements
name|SqlValidatorNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|SqlValidatorNamespace
name|namespace
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a DelegatingNamespace.    *    * @param namespace Underlying namespace, to delegate to    */
specifier|protected
name|DelegatingNamespace
parameter_list|(
name|SqlValidatorNamespace
name|namespace
parameter_list|)
block|{
name|this
operator|.
name|namespace
operator|=
name|namespace
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getValidator
argument_list|()
return|;
block|}
specifier|public
name|SqlValidatorTable
name|getTable
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getTable
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getRowType
argument_list|()
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|namespace
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|getRowTypeSansSystemColumns
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getRowTypeSansSystemColumns
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|public
name|void
name|validate
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
block|{
name|namespace
operator|.
name|validate
argument_list|(
name|targetRowType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getNode
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|getEnclosingNode
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getEnclosingNode
argument_list|()
return|;
block|}
specifier|public
name|SqlValidatorNamespace
name|lookupChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|namespace
operator|.
name|lookupChild
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|fieldExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|namespace
operator|.
name|fieldExists
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|getMonotonicExprs
parameter_list|()
block|{
return|return
name|namespace
operator|.
name|getMonotonicExprs
argument_list|()
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
return|return
name|namespace
operator|.
name|getMonotonicity
argument_list|(
name|columnName
argument_list|)
return|;
block|}
specifier|public
name|void
name|makeNullable
parameter_list|()
block|{
name|namespace
operator|.
name|makeNullable
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|namespace
operator|.
name|translate
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|namespace
operator|.
name|unwrap
argument_list|(
name|clazz
argument_list|)
return|;
block|}
block|}
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
operator|||
name|namespace
operator|.
name|isWrapperFor
argument_list|(
name|clazz
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End DelegatingNamespace.java
end_comment

end_unit

