begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
comment|/**  * A class derived from<code>BarfingInvocationHandler</code> handles a method  * call by looking for a method in itself with identical parameters. If no such  * method is found, it throws {@link UnsupportedOperationException}.  *  *<p>It is useful when you are prototyping code. You can rapidly create a  * prototype class which implements the important methods in an interface, then  * implement other methods as they are called.</p>  *  * @author jhyde  * @version $Id$  * @see DelegatingInvocationHandler  * @since Dec 23, 2002  */
end_comment

begin_class
specifier|public
class|class
name|BarfingInvocationHandler
implements|implements
name|InvocationHandler
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|BarfingInvocationHandler
parameter_list|()
block|{
block|}
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
init|=
literal|null
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
throw|throw
name|noMethod
argument_list|(
name|method
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
name|noMethod
argument_list|(
name|method
argument_list|)
throw|;
block|}
if|if
condition|(
name|matchingMethod
operator|.
name|getReturnType
argument_list|()
operator|!=
name|method
operator|.
name|getReturnType
argument_list|()
condition|)
block|{
throw|throw
name|noMethod
argument_list|(
name|method
argument_list|)
throw|;
block|}
comment|// Invoke the method in the derived class.
try|try
block|{
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
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
comment|/**      * Called when this class (or its derived class) does not have the required      * method from the interface.      */
specifier|protected
name|UnsupportedOperationException
name|noMethod
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Class
index|[]
name|parameterTypes
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parameterTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|parameterTypes
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|signature
init|=
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|"("
operator|+
name|buf
operator|.
name|toString
argument_list|()
operator|+
literal|")"
decl_stmt|;
return|return
operator|new
name|UnsupportedOperationException
argument_list|(
name|signature
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End BarfingInvocationHandler.java
end_comment

end_unit

