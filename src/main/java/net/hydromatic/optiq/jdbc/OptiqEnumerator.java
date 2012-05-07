begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
package|;
end_package

begin_comment
comment|/**  * Replica of LINQ's Enumerator interface.  *  *<p>Package-protected; not part of JDBC.</p>  */
end_comment

begin_interface
interface|interface
name|OptiqEnumerator
parameter_list|<
name|T
parameter_list|>
block|{
name|boolean
name|moveNext
parameter_list|()
function_decl|;
name|T
name|current
parameter_list|()
function_decl|;
name|void
name|reset
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End OptiqEnumerator.java
end_comment

end_unit

