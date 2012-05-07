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
comment|/**  * Schema that can be modified.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MutableSchema
extends|extends
name|Schema
block|{
name|void
name|add
parameter_list|(
name|SchemaObject
name|schemaObject
parameter_list|)
function_decl|;
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Schema
name|schema
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End MutableSchema.java
end_comment

end_unit

