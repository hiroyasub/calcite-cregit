begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Object model for Java expressions.  *  *<p>This object model is used when the linq4j system is analyzing  * queries that have been submitted using methods on the  * {@link net.hydromatic.linq4j.Queryable} interface. The system attempts  * to understand the intent of the query and reorganize it for  * efficiency; for example, it may attempt to push down filters to the  * source SQL system.</p>  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
package|;
end_package

end_unit

