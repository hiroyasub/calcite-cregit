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
name|util
package|;
end_package

begin_comment
comment|/**  * Object which can be a target for a reflective visitation (see {@link  * ReflectUtil#invokeVisitor(ReflectiveVisitor, Object, Class, String)}.  *  *<p>This is a tagging interface: it has no methods, and is not even required  * in order to use reflective visitation, but serves to advise users of the  * class of the intended use of the class and refer them to auxilliary classes.  *  * @author Julian Hyde  * @version $Id$  */
end_comment

begin_interface
specifier|public
interface|interface
name|ReflectiveVisitor
block|{ }
end_interface

begin_comment
comment|// End ReflectiveVisitor.java
end_comment

end_unit

