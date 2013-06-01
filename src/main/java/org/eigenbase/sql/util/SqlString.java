begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|util
package|;
end_package

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
comment|/**  * String that represents a kocher SQL statement, expression, or fragment.  *  *<p>A SqlString just contains a regular Java string, but the SqlString wrapper  * indicates that the string has been created carefully guarding against all SQL  * dialect and injection issues.  *  *<p>The easiest way to do build a SqlString is to use a {@link SqlBuilder}.  *  * @version $Id$  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|SqlString
block|{
specifier|private
specifier|final
name|String
name|s
decl_stmt|;
specifier|private
name|SqlDialect
name|dialect
decl_stmt|;
comment|/**      * Creates a SqlString.      *      * @param s Contents of string      */
specifier|public
name|SqlString
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
assert|assert
name|s
operator|!=
literal|null
assert|;
assert|assert
name|dialect
operator|!=
literal|null
assert|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|s
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|instanceof
name|SqlString
operator|&&
name|s
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SqlString
operator|)
name|obj
operator|)
operator|.
name|s
argument_list|)
return|;
block|}
comment|/**      * {@inheritDoc}      *      *<p>Returns the SQL string.      *      * @see #getSql()      * @return SQL string      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|s
return|;
block|}
comment|/**      * Returns the SQL string.      * @return SQL string      */
specifier|public
name|String
name|getSql
parameter_list|()
block|{
return|return
name|s
return|;
block|}
comment|/**      * Returns the dialect.      */
specifier|public
name|SqlDialect
name|getDialect
parameter_list|()
block|{
return|return
name|dialect
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlString.java
end_comment

end_unit

