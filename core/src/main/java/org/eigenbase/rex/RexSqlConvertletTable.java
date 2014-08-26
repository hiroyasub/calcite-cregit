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
name|rex
package|;
end_package

begin_comment
comment|/**  * Collection of {@link RexSqlConvertlet}s.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RexSqlConvertletTable
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the convertlet applicable to a given expression.    */
name|RexSqlConvertlet
name|get
parameter_list|(
name|RexCall
name|call
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RexSqlConvertletTable.java
end_comment

end_unit

