begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
package|;
end_package

begin_comment
comment|/**  * A member of a {@link Schema}.  *  *<p>May be a {@link Function} or an {@link Overload}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SchemaObject
block|{
comment|/**      * The name of this schema object.      */
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SchemaObject.java
end_comment

end_unit

