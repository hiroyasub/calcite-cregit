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
name|oj
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|openjava
operator|.
name|ptree
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A relational expression which implements this interface can generate a java  * expression which represents the current row of the expression.  *  * @author jhyde  * @version $Id$  * @since May 24, 2004  */
end_comment

begin_interface
specifier|public
interface|interface
name|JavaSelfRel
extends|extends
name|JavaRel
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns a Java expression which yields the current row of this relational      * expression. This method is called by the {@link JavaRelImplementor} the      * first time a piece of Java code wants to refer to this relation. The      * implementor then uses this expression to initialize a variable.      *      *<p>If no code needs to refer to this relation, then the expression is      * never generated. This prevents generating useless code like      *      *<blockquote>      *<pre>Dummy_12f614.Ojp_1 oj_var8 = new Dummy_12f614.Ojp_1();</pre>      *</blockquote>      *      * .</p>      *      *<p>If a relational expression has one input relational expression which      * has the same row type, you may be able to share its variable. Call      * Implementor#bind(Rel,Rel) to do this.      *      * @see JavaRelImplementor#bind(org.eigenbase.rel.RelNode,org.eigenbase.rel.RelNode)      */
name|Expression
name|implementSelf
parameter_list|(
name|JavaRelImplementor
name|implementor
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End JavaSelfRel.java
end_comment

end_unit

