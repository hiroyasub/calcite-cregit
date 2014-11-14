begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
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
name|SqlNode
import|;
end_import

begin_comment
comment|/** Namespace based on a table from the catalog. */
end_comment

begin_class
class|class
name|TableNamespace
extends|extends
name|AbstractNamespace
block|{
specifier|private
specifier|final
name|SqlValidatorTable
name|table
decl_stmt|;
comment|/** Creates a TableNamespace. */
name|TableNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlValidatorTable
name|table
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
assert|assert
name|table
operator|!=
literal|null
assert|;
block|}
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|()
block|{
return|return
name|table
operator|.
name|getRowType
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
comment|// This is the only kind of namespace not based on a node in the parse tree.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlValidatorTable
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
block|}
end_class

begin_comment
comment|// End TableNamespace.java
end_comment

end_unit

