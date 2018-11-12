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
name|RelRoot
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
name|type
operator|.
name|RelDataType
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Utilities for {@link RelOptTable.ViewExpander} and  * {@link RelOptTable.ToRelContext}.  */
end_comment

begin_class
annotation|@
name|Nonnull
specifier|public
specifier|abstract
class|class
name|ViewExpanders
block|{
specifier|private
name|ViewExpanders
parameter_list|()
block|{
block|}
comment|/** Converts a {@code ViewExpander} to a {@code ToRelContext}. */
specifier|public
specifier|static
name|RelOptTable
operator|.
name|ToRelContext
name|toRelContext
parameter_list|(
name|RelOptTable
operator|.
name|ViewExpander
name|viewExpander
parameter_list|,
name|RelOptCluster
name|cluster
parameter_list|)
block|{
if|if
condition|(
name|viewExpander
operator|instanceof
name|RelOptTable
operator|.
name|ToRelContext
condition|)
block|{
return|return
operator|(
name|RelOptTable
operator|.
name|ToRelContext
operator|)
name|viewExpander
return|;
block|}
return|return
operator|new
name|RelOptTable
operator|.
name|ToRelContext
argument_list|()
block|{
specifier|public
name|RelOptCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
specifier|public
name|RelRoot
name|expandView
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|queryString
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|)
block|{
return|return
name|viewExpander
operator|.
name|expandView
argument_list|(
name|rowType
argument_list|,
name|queryString
argument_list|,
name|schemaPath
argument_list|,
name|viewPath
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Creates a simple {@code ToRelContext} that cannot expand views. */
specifier|public
specifier|static
name|RelOptTable
operator|.
name|ToRelContext
name|simpleContext
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|)
block|{
return|return
operator|new
name|RelOptTable
operator|.
name|ToRelContext
argument_list|()
block|{
specifier|public
name|RelOptCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
specifier|public
name|RelRoot
name|expandView
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|queryString
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End ViewExpanders.java
end_comment

end_unit
