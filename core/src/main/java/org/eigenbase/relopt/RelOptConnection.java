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
name|relopt
package|;
end_package

begin_comment
comment|/**  * The planner's view of a connection to a database.  *  *<p>A connection contains a {@link RelOptSchema}, via which the query planner  * can access {@link RelOptTable} objects.</p>  *  *<p>If used in saffron, and if saffron is being used as a Java preprocessor,  * then every class which implements<code>RelOptConnection</code> must  * implement the method  *  *<blockquote>  *<pre>public static RelOptSchema getRelOptSchema()</pre>  *</blockquote>  *</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptConnection
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the schema underlying this connection. Non-abstract classes    * implementing this interface must also provide<code>public static Schema    * getRelOptSchemaStatic()</code>.    */
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelOptConnection.java
end_comment

end_unit

