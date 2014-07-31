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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Interface for looking up methods relating to reflective visitation. One  * possible implementation would cache the results.  *  *<p>Type parameter 'R' is the base class of visitoR class; type parameter 'E'  * is the base class of visiteE class.  *  *<p>TODO: obsolete {@link ReflectUtil#lookupVisitMethod}, and use caching in  * implementing that method.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ReflectiveVisitDispatcher
parameter_list|<
name|R
extends|extends
name|ReflectiveVisitor
parameter_list|,
name|E
parameter_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Looks up a visit method taking additional parameters beyond the    * overloaded visitee type.    *    * @param visitorClass             class of object whose visit method is to be    *                                 invoked    * @param visiteeClass             class of object to be passed as a parameter    *                                 to the visit method    * @param visitMethodName          name of visit method    * @param additionalParameterTypes list of additional parameter types    * @return method found, or null if none found    */
name|Method
name|lookupVisitMethod
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|R
argument_list|>
name|visitorClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|visiteeClass
parameter_list|,
name|String
name|visitMethodName
parameter_list|,
name|List
argument_list|<
name|Class
argument_list|>
name|additionalParameterTypes
parameter_list|)
function_decl|;
comment|/**    * Looks up a visit method.    *    * @param visitorClass    class of object whose visit method is to be invoked    * @param visiteeClass    class of object to be passed as a parameter to the    *                        visit method    * @param visitMethodName name of visit method    * @return method found, or null if none found    */
name|Method
name|lookupVisitMethod
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|R
argument_list|>
name|visitorClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|visiteeClass
parameter_list|,
name|String
name|visitMethodName
parameter_list|)
function_decl|;
comment|/**    * Implements the {@link Glossary#VISITOR_PATTERN} via reflection. The basic    * technique is taken from<a    * href="http://www.javaworld.com/javaworld/javatips/jw-javatip98.html">a    * Javaworld article</a>. For an example of how to use it, see    * {@code ReflectVisitorTest}.    *    *<p>Visit method lookup follows the same rules as if compile-time resolution    * for VisitorClass.visit(VisiteeClass) were performed. An ambiguous match due    * to multiple interface inheritance results in an IllegalArgumentException. A    * non-match is indicated by returning false.</p>    *    * @param visitor         object whose visit method is to be invoked    * @param visitee         object to be passed as a parameter to the visit    *                        method    * @param visitMethodName name of visit method, e.g. "visit"    * @return true if a matching visit method was found and invoked    */
name|boolean
name|invokeVisitor
parameter_list|(
name|R
name|visitor
parameter_list|,
name|E
name|visitee
parameter_list|,
name|String
name|visitMethodName
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End ReflectiveVisitDispatcher.java
end_comment

end_unit

