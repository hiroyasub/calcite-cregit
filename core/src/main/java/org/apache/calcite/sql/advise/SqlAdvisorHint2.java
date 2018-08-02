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
name|advise
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
name|validate
operator|.
name|SqlMoniker
import|;
end_import

begin_comment
comment|/**  * This class is used to return values for  * {@link SqlAdvisor#getCompletionHints (String, int, String[])}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlAdvisorHint2
extends|extends
name|SqlAdvisorHint
block|{
comment|/** Replacement string */
specifier|public
specifier|final
name|String
name|replacement
decl_stmt|;
specifier|public
name|SqlAdvisorHint2
parameter_list|(
name|String
name|id
parameter_list|,
name|String
index|[]
name|names
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|replacement
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|names
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
name|replacement
expr_stmt|;
block|}
specifier|public
name|SqlAdvisorHint2
parameter_list|(
name|SqlMoniker
name|id
parameter_list|,
name|String
name|replacement
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
name|replacement
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlAdvisorHint2.java
end_comment

end_unit

