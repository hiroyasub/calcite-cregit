begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * Factory for {@link org.eigenbase.sql.parser.SqlAbstractParserImpl} objects.  *  *<p>A parser factory allows you to include a custom parser in  * {@link net.hydromatic.optiq.tools.Planner} created through  * {@link net.hydromatic.optiq.tools.Frameworks}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlParserImplFactory
block|{
comment|/**    * Get the underlying parser implementation.    *    * @return {@link SqlAbstractParserImpl} object.    */
name|SqlAbstractParserImpl
name|getParser
parameter_list|(
name|Reader
name|stream
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

