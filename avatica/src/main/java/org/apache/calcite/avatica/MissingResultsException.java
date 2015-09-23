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
name|avatica
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
name|avatica
operator|.
name|Meta
operator|.
name|StatementHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_comment
comment|/**  * An Exception which denotes that a cached Statement is present but has no {@link ResultSet}.  */
end_comment

begin_class
specifier|public
class|class
name|MissingResultsException
extends|extends
name|Exception
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|private
specifier|final
name|StatementHandle
name|handle
decl_stmt|;
specifier|public
name|MissingResultsException
parameter_list|(
name|StatementHandle
name|handle
parameter_list|)
block|{
name|this
operator|.
name|handle
operator|=
name|handle
expr_stmt|;
block|}
specifier|public
name|StatementHandle
name|getHandle
parameter_list|()
block|{
return|return
name|handle
return|;
block|}
block|}
end_class

begin_comment
comment|// End MissingResultsException.java
end_comment

end_unit

