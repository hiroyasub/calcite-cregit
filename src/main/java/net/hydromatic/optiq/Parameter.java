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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_comment
comment|/**  * Parameter to a {@link net.hydromatic.optiq.Function}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Parameter
block|{
comment|/**      * Zero-based ordinal of this parameter within the function's parameter      * list.      *      * @return Parameter ordinal      */
name|int
name|getOrdinal
parameter_list|()
function_decl|;
comment|/**      * Name of the parameter.      *      * @return Parameter name      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns the type of this parameter.      *      * @return Parameter type.      */
name|RelDataType
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Parameter.java
end_comment

end_unit

