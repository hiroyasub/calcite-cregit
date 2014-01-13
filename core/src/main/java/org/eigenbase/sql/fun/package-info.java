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

begin_comment
comment|/**  * Defines the set of standard SQL row-level functions and  * operators.  *  *<p>The standard set of row-level functions and operators are declared in  * class {@link org.eigenbase.sql.fun.SqlStdOperatorTable}. Anonymous inner  * classes within that table are allowed only for specifying an operator's test  * function; if other custom code is needed for an operator, it should be  * implemented in a top-level class within this package instead.  Operators  * which are not row-level (e.g. select and join) should be defined in package  * {@link org.eigenbase.sql} instead.</p>  */
end_comment

begin_comment
comment|// End package-info.java
end_comment

end_unit

