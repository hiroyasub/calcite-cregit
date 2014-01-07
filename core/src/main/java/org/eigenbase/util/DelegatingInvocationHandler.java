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

begin_comment
comment|/**  * A class derived from<code>DelegatingInvocationHandler</code> handles a  * method call by looking for a method in itself with identical parameters. If  * no such method is found, it forwards the call to a fallback object, which  * must implement all of the interfaces which this proxy implements.  *  *<p>It is useful in creating a wrapper class around an interface which may  * change over time.</p>  *  *<p>Example:  *  *<blockquote>  *<pre>import java.sql.Connection;  * Connection connection = ...;  * Connection tracingConnection = (Connection) Proxy.newProxyInstance(  *     null,  *     new Class[] {Connection.class},  *     new DelegatingInvocationHandler() {  *         protected Object getTarget() {  *             return connection;  *         }  *         Statement createStatement() {  *             System.out.println("statement created");  *             return connection.createStatement();  *         }  *     });</pre>  *</blockquote>  *</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DelegatingInvocationHandler
implements|implements
name|InvocationHandler
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Class
name|clazz
init|=
name|getClass
argument_list|()
decl_stmt|;
name|Method
name|matchingMethod
decl_stmt|;
try|try
block|{
name|matchingMethod
operator|=
name|clazz
operator|.
name|getMethod
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|matchingMethod
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|matchingMethod
operator|=
literal|null
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|matchingMethod
operator|!=
literal|null
condition|)
block|{
comment|// Invoke the method in the derived class.
return|return
name|matchingMethod
operator|.
name|invoke
argument_list|(
name|this
argument_list|,
name|args
argument_list|)
return|;
block|}
else|else
block|{
comment|// Invoke the method on the proxy.
return|return
name|method
operator|.
name|invoke
argument_list|(
name|getTarget
argument_list|()
argument_list|,
name|args
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getTargetException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Returns the object to forward method calls to, should the derived class    * not implement the method. Generally, this object will be a member of the    * derived class, supplied as a parameter to its constructor.    */
specifier|protected
specifier|abstract
name|Object
name|getTarget
parameter_list|()
function_decl|;
block|}
end_class

begin_comment
comment|// End DelegatingInvocationHandler.java
end_comment

end_unit

