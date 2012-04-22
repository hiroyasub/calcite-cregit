begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
package|;
end_package

begin_comment
comment|/**  * Exposes the enumerator, which supports a simple iteration over a collection.  *  *<p>Analogous to LINQ's System.Collections.IEnumerable (both generic  * and non-generic variants).</p>  *  *<p>Also implements {@link Iterable}, to enable use in Java foreach loops.</p>  *  * @param<T> element type  */
end_comment

begin_interface
specifier|public
interface|interface
name|Enumerable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|RawEnumerable
argument_list|<
name|T
argument_list|>
extends|,
name|Iterable
argument_list|<
name|T
argument_list|>
extends|,
name|ExtendedEnumerable
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Converts this Enumerable to a Queryable.      *      * @see Extensions#asQueryable(Enumerable)      */
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Enumerable.java
end_comment

end_unit

