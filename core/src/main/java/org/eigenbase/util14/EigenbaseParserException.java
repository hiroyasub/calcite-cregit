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
name|util14
package|;
end_package

begin_comment
comment|/**  * This is a tagging interface to allow a {@link  * org.eigenbase.sql.parser.SqlParseException} to be identified without adding a  * dependency on it from client-side code.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_interface
specifier|public
interface|interface
name|EigenbaseParserException
block|{ }
end_interface

begin_comment
comment|// End EigenbaseParserException.java
end_comment

end_unit

