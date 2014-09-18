begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|interpreter
package|;
end_package

begin_comment
comment|/**  * Source of rows.  *  *<p>Corresponds to an input of a relational expression.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Source
block|{
comment|/** Reads a row. Null means end of data. */
name|Row
name|receive
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Source.java
end_comment

end_unit

