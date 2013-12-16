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
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>TableAccessRel</code> reads all the rows from a {@link RelOptTable}.  *  *<p>If the table is a<code>net.sf.saffron.ext.JdbcTable</code>, then this is  * literally possible. But for other kinds of tables, there may be many ways to  * read the data from the table. For some kinds of table, it may not even be  * possible to read all of the rows unless some narrowing constraint is applied.  *  *<p>In the example of the<code>net.sf.saffron.ext.ReflectSchema</code>  * schema,  *  *<blockquote>  *<pre>select from fields</pre>  *</blockquote>  *  * cannot be implemented, but  *  *<blockquote>  *<pre>select from fields as f  * where f.getClass().getName().equals("java.lang.String")</pre>  *</blockquote>  *  * can. It is the optimizer's responsibility to find these ways, by applying  * transformation rules.</p>  *  * @author jhyde  * @version $Id$  * @since 10 November, 2001  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TableAccessRel
extends|extends
name|TableAccessRelBase
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a TableAccessRel.      *      * @param cluster Cluster      * @param table Table      */
specifier|public
name|TableAccessRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a TableAccessRel by parsing serialized output. */
specifier|public
name|TableAccessRel
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|comprises
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|this
return|;
block|}
block|}
end_class

begin_comment
comment|// End TableAccessRel.java
end_comment

end_unit

