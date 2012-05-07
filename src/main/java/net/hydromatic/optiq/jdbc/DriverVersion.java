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
comment|/**  * Driver version information.  *  *<p>Kept in a separate file so that that version information can be  * generated.</p>  */
end_comment

begin_class
class|class
name|DriverVersion
block|{
specifier|final
name|int
name|majorVersion
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|minorVersion
init|=
literal|1
decl_stmt|;
specifier|final
name|String
name|name
init|=
literal|"OPTIQ JDBC Driver"
decl_stmt|;
specifier|final
name|String
name|versionString
init|=
literal|"0.1"
decl_stmt|;
block|}
end_class

begin_comment
comment|// End DriverVersion.java
end_comment

end_unit

