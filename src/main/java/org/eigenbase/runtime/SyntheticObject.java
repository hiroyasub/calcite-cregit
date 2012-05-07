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
name|runtime
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
comment|/**  *<code>SyntheticObject</code> is a base class for all objects built 'on the  * fly' by the saffron system. For example, if you write  *  *<blockquote>  *<pre>for (i in select {emp.firstName, emp.lastName} from emps as emp) {  *     ...  * }</pre>  *</blockquote>  *  * then<code>i</code>'s type will be a class, generated by the saffron  * compiler, something like this:  *  *<blockquote>  *<pre>class saffron.runtime.Dummy_a01bc65 extends {@link SyntheticObject} {  *     public String firstName;  *     public String lastName;  * }</pre>  *</blockquote>  *  * @author jhyde  * @version $Id$  * @since 23 April, 2002  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SyntheticObject
block|{
comment|//~ Fields ----------------------------------------------------------------
name|Field
index|[]
name|fields
init|=
literal|null
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the value of a given field.      */
specifier|public
name|Object
name|getFieldValue
parameter_list|(
name|int
name|i
parameter_list|)
block|{
try|try
block|{
name|Field
name|field
init|=
name|getFields
argument_list|()
index|[
name|i
index|]
decl_stmt|;
return|return
name|field
operator|.
name|get
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Returns the fields of this object, in the order they were specified in      * the original query.      */
specifier|public
name|Field
index|[]
name|getFields
parameter_list|()
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|getClass
argument_list|()
operator|.
name|getDeclaredFields
argument_list|()
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
block|}
end_class

begin_comment
comment|// End SyntheticObject.java
end_comment

end_unit

