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
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Factory for JDBC objects.  *  *<p>There is an implementation for each supported JDBC version.</p>  */
end_comment

begin_interface
interface|interface
name|Factory
block|{
name|OptiqConnectionImpl
name|newConnection
parameter_list|(
name|UnregisteredDriver
name|driver
parameter_list|,
name|Factory
name|factory
parameter_list|,
name|String
name|url
parameter_list|,
name|Properties
name|info
parameter_list|)
function_decl|;
name|OptiqStatement
name|newStatement
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|)
function_decl|;
name|OptiqPreparedStatement
name|newPreparedStatement
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|,
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
function_decl|;
comment|/**      * Creates a result set. You will then need to call      * {@link net.hydromatic.optiq.jdbc.OptiqResultSet#execute()} on it.      *      * @param optiqStatement Statement      * @param prepareResult Result      * @return Result set      */
name|OptiqResultSet
name|newResultSet
parameter_list|(
name|OptiqStatement
name|optiqStatement
parameter_list|,
name|OptiqPrepare
operator|.
name|PrepareResult
name|prepareResult
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End Factory.java
end_comment

end_unit

