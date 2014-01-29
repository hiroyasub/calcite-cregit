begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Definition of the SQL:2003 standard ARRAY query constructor,<code>  * ARRAY (&lt;query&gt;)</code>.  */
end_comment

begin_class
specifier|public
class|class
name|SqlArrayQueryConstructor
extends|extends
name|SqlMultisetQueryConstructor
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlArrayQueryConstructor
parameter_list|()
block|{
name|super
argument_list|(
literal|"ARRAY"
argument_list|,
name|SqlKind
operator|.
name|MAP_QUERY_CONSTRUCTOR
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlArrayQueryConstructor.java
end_comment

end_unit

