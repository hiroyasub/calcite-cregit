begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
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
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|Convention
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|SqlDialect
import|;
end_import

begin_comment
comment|/**  * Calling convention for relational operations that occur in a JDBC  * database.  *  *<p>The convention is a slight misnomer. The operations occur in whatever  * data-flow architecture the database uses internally. Nevertheless, the result  * pops out in JDBC.</p>  *  *<p>This is the only convention, thus far, that is not a singleton. Each  * instance contains a JDBC schema (and therefore a data source). If Optiq is  * working with two different databases, it would even make sense to convert  * from "JDBC#A" convention to "JDBC#B", even though we don't do it currently.  * (That would involve asking database B to open a database link to database  * A.)</p>  *  *<p>As a result, converter rules from and two this convention need to be  * instantiated, at the start of planning, for each JDBC database in play.</p>  */
end_comment

begin_class
specifier|public
class|class
name|JdbcConvention
extends|extends
name|Convention
operator|.
name|Impl
block|{
specifier|public
specifier|final
name|SqlDialect
name|dialect
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|public
name|JdbcConvention
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
literal|"JDBC."
operator|+
name|name
argument_list|,
name|JdbcRel
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
block|}
specifier|public
specifier|static
name|JdbcConvention
name|of
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|JdbcConvention
argument_list|(
name|dialect
argument_list|,
name|expression
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcConvention.java
end_comment

end_unit

