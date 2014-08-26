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
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The name-resolution context for expression inside a multiset call. The  * objects visible are multiset expressions, and those inherited from the parent  * scope.  *  * @see CollectNamespace  */
end_comment

begin_class
class|class
name|CollectScope
extends|extends
name|ListScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlValidatorScope
name|usingScope
decl_stmt|;
specifier|private
specifier|final
name|SqlCall
name|child
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|CollectScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlValidatorScope
name|usingScope
parameter_list|,
name|SqlCall
name|child
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|usingScope
operator|=
name|usingScope
expr_stmt|;
name|this
operator|.
name|child
operator|=
name|child
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|child
return|;
block|}
block|}
end_class

begin_comment
comment|// End CollectScope.java
end_comment

end_unit

