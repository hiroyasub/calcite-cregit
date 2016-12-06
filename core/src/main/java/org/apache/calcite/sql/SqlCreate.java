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
name|parser
operator|.
name|SqlParserPos
import|;
end_import

begin_comment
comment|/**  * Base class for an CREATE statements parse tree nodes. The portion of the  * statement covered by this class is "CREATE [ OR REPLACE ]". Subclasses handle  * whatever comes afterwards.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlCreate
extends|extends
name|SqlCall
block|{
comment|/** Whether "OR REPLACE" was specified. */
name|boolean
name|replace
decl_stmt|;
specifier|public
name|SqlCreate
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|boolean
name|replace
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|replace
operator|=
name|replace
expr_stmt|;
block|}
specifier|public
name|boolean
name|getReplace
parameter_list|()
block|{
return|return
name|replace
return|;
block|}
specifier|public
name|void
name|setReplace
parameter_list|(
name|boolean
name|replace
parameter_list|)
block|{
name|this
operator|.
name|replace
operator|=
name|replace
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCreate.java
end_comment

end_unit

