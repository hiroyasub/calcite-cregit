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
name|util
operator|.
name|format
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
name|linq4j
operator|.
name|function
operator|.
name|Experimental
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * A format element in a format string. Knows how to parse and unparse itself.  */
end_comment

begin_interface
annotation|@
name|Experimental
specifier|public
interface|interface
name|FormatElement
block|{
comment|/**    * Formats a date to its appropriate string representation for the element.    *    *<p>This API is subject to change. It might be more efficient if the    * signature was one of the following:    *    *<pre>    *   void format(StringBuilder, java.util.Date)    *   void format(StringBuilder, long)    *</pre>    */
name|String
name|format
parameter_list|(
name|java
operator|.
name|util
operator|.
name|Date
name|date
parameter_list|)
function_decl|;
comment|/**    * Returns the description of an element.    *    *<p>For example, {@code %H} in MySQL represents the hour in 24-hour format    * (e.g., 00..23). This method returns the string "The hour (24-hour clock) as    * a decimal number (00-23)", which is the description of    * {@link FormatElementEnum#HH24}.    */
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/**    * Applies a consumer to a format element.    */
specifier|default
name|void
name|flatten
parameter_list|(
name|Consumer
argument_list|<
name|FormatElement
argument_list|>
name|consumer
parameter_list|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_interface

end_unit

