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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|MutableSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_comment
comment|/**  * Extension to OPTIQ's implementation of  * {@link java.sql.Connection JDBC connection} allows schemas to be defined  * dynamically.  *  *<p>You can start off with an empty connection (no schemas), define one  * or two schemas, and start querying them.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|OptiqConnection
extends|extends
name|Connection
block|{
comment|/**      * Returns the root schema.      *      *<p>You can define objects (such as relations) in this schema, and      * also nested schemas.</p>      *      * @return Root schema      */
name|MutableSchema
name|getRootSchema
parameter_list|()
function_decl|;
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End OptiqConnection.java
end_comment

end_unit

