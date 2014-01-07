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
name|validate
package|;
end_package

begin_comment
comment|/**  * An enumeration of moniker types.  *  *<p>Used in {@link SqlMoniker}.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlMonikerType
block|{
name|Column
block|,
name|Table
block|,
name|View
block|,
name|Schema
block|,
name|Repository
block|,
name|Function
block|,
name|Keyword
block|; }
end_enum

begin_comment
comment|// End SqlMonikerType.java
end_comment

end_unit

