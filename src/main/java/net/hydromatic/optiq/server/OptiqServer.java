begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|server
package|;
end_package

begin_comment
comment|/**  * Server.  *  *<p>Represents shared state among connections, and will have monitoring and  * management facilities.  */
end_comment

begin_interface
specifier|public
interface|interface
name|OptiqServer
block|{
name|void
name|removeStatement
parameter_list|(
name|OptiqServerStatement
name|optiqServerStatement
parameter_list|)
function_decl|;
name|void
name|addStatement
parameter_list|(
name|OptiqServerStatement
name|optiqServerStatement
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End OptiqServer.java
end_comment

end_unit

