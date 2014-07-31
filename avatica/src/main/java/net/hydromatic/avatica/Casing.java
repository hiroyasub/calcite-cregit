begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|avatica
package|;
end_package

begin_comment
comment|/** Policy for converting case of identifiers before storing them.  *  *<p>A database often has policies for quoted versus unquoted identifiers.  * For example, Oracle converts unquoted identifiers to upper-case, but  * quoted identifiers are unchanged.</p> */
end_comment

begin_enum
specifier|public
enum|enum
name|Casing
block|{
comment|/** The case of identifiers is not changed. */
name|UNCHANGED
block|,
comment|/** Identifiers are converted to upper-case. */
name|TO_UPPER
block|,
comment|/** Identifiers are converted to lower-case. */
name|TO_LOWER
block|}
end_enum

begin_comment
comment|// End Casing.java
end_comment

end_unit

