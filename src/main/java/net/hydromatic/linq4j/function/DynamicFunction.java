begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
package|;
end_package

begin_comment
comment|/**  * Function that can be dynamically invoked.  *  *<p>If a function does not support this interface, you can call it using  * {@link Functions#dynamicInvoke(Function, Object...)}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DynamicFunction
parameter_list|<
name|R
parameter_list|>
extends|extends
name|Function
argument_list|<
name|R
argument_list|>
block|{
name|R
name|dynamicInvoke
parameter_list|(
name|Object
modifier|...
name|arguments
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End DynamicFunction.java
end_comment

end_unit

