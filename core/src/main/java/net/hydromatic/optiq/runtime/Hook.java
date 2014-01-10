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
name|optiq
operator|.
name|runtime
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function1
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  * Collection of hooks that can be set by observers and are executed at various  * parts of the query preparation process.  *  *<p>For testing and debugging rather than for end-users.</p>  */
end_comment

begin_enum
specifier|public
enum|enum
name|Hook
block|{
comment|/** Called with the generated Java plan, just before it is compiled by    * Janino. */
name|JAVA_PLAN
block|,
comment|/** Called with the output of sql-to-rel-converter. */
name|CONVERTED
block|,
comment|/** Called when a constant expression is being reduced. */
name|EXPRESSION_REDUCER
block|,
comment|/** Called with a query that has been generated to send to a back-end system.    * The query might be a SQL string (for the JDBC adapter), a list of Mongo    * pipeline expressions (for the MongoDB adapter), et cetera. */
name|QUERY_PLAN
block|;
specifier|private
specifier|final
name|List
argument_list|<
name|Function1
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|>
name|handlers
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|Function1
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Adds a handler for this Hook.    *    *<p>Returns a {@link Hook.Closeable} so that you can use the following    * try-finally pattern to prevent leaks:</p>    *    *<blockquote><pre>    *     final Hook.Closeable closeable = Hook.FOO.add(HANDLER);    *     try {    *         ...    *     } finally {    *         closeable.close();    *     }</pre>    *</blockquote>    */
specifier|public
name|Closeable
name|add
parameter_list|(
specifier|final
name|Function1
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|handler
parameter_list|)
block|{
name|handlers
operator|.
name|add
argument_list|(
name|handler
argument_list|)
expr_stmt|;
return|return
operator|new
name|Closeable
argument_list|()
block|{
specifier|public
name|void
name|close
parameter_list|()
block|{
name|remove
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** Removes a handler from this Hook. */
specifier|private
name|boolean
name|remove
parameter_list|(
name|Function1
name|handler
parameter_list|)
block|{
return|return
name|handlers
operator|.
name|remove
argument_list|(
name|handler
argument_list|)
return|;
block|}
comment|/** Runs all handlers registered for this Hook, with the given argument. */
specifier|public
name|void
name|run
parameter_list|(
name|Object
name|arg
parameter_list|)
block|{
for|for
control|(
name|Function1
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|handler
range|:
name|handlers
control|)
block|{
name|handler
operator|.
name|apply
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Removes a Hook after use.    *    *<p>Note: Although it would be convenient, this interface cannot extend    * {@code AutoCloseable} while Optiq maintains compatibility with    * JDK 1.6.</p>    */
specifier|public
interface|interface
name|Closeable
comment|/*extends AutoCloseable*/
block|{
name|void
name|close
parameter_list|()
function_decl|;
comment|// override, removing "throws"
block|}
block|}
end_enum

begin_comment
comment|// End Hook.java
end_comment

end_unit

