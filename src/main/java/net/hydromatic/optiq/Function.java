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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
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

begin_comment
comment|/**  * A named expression in a schema.  *  *<h2>Examples of functions</h2>  *  *<p>Several kinds of functions crop up in real life. They all implement the  * {@code Function} interface, but tend to be treated differently by the  * back-end system if not by OPTIQ.</p>  *  *<p>A function that has zero arguments and a type that is a collection of  * records is referred to as a<i>relation</i>. In schemas backed by a  * relational database, tables and views will appear as relations.</p>  *  *<p>A function that has one or more arguments and a type that is a collection  * of records is referred to as a<i>parameterized relation</i>. Some relational  * databases support these; for example, Oracle calls them "table  * functions".</p>  *  *<p>Functions may be also more typical of programming-language functions:  * they take zero or more arguments, and return a result of arbitrary type.</p>  *  *<p>From the above definitions, you can see that a relation is a special  * kind of function. This makes sense, because even though it has no  * arguments, it is "evaluated" each time it is used in a query.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Function
extends|extends
name|SchemaObject
block|{
comment|/**      * Returns the parameters of this function.      *      * @return Parameters; never null      */
name|List
argument_list|<
name|Parameter
argument_list|>
name|getParameters
parameter_list|()
function_decl|;
comment|/**      * Returns the type of this function's result.      *      * @return Type of result; never null      */
name|RelDataType
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Function.java
end_comment

end_unit

