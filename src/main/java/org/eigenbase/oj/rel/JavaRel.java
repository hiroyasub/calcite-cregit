begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A relational expression of one of the the Java-based calling conventions.  *  *<p>Objects which implement this interface must:  *  *<ul>  *<li>extend {@link org.eigenbase.rel.AbstractRelNode}, and</li>  *<li>return one of the following calling-conventions from their {@link  * #getConvention} method:  *  *<ul>  *<li>{@link org.eigenbase.relopt.CallingConvention#ARRAY ARRAY},  *<li>{@link org.eigenbase.relopt.CallingConvention#ITERABLE ITERABLE},  *<li>{@link org.eigenbase.relopt.CallingConvention#ITERATOR ITERATOR},  *<li>{@link org.eigenbase.relopt.CallingConvention#COLLECTION COLLECTION},  *<li>{@link org.eigenbase.relopt.CallingConvention#MAP MAP},  *<li>{@link org.eigenbase.relopt.CallingConvention#VECTOR VECTOR},  *<li>{@link org.eigenbase.relopt.CallingConvention#HASHTABLE HASHTABLE},  *<li>{@link org.eigenbase.relopt.CallingConvention#JAVA JAVA},  *<li>{@link org.eigenbase.relopt.CallingConvention#ENUMERABLE ENUMERABLE}.  *</ul>  *</li>  *</ul>  *  *<p>For {@link org.eigenbase.relopt.CallingConvention#JAVA JAVA  * calling-convention}, see the sub-interface {@link JavaLoopRel}, and the  * auxilliary interface {@link JavaSelfRel}.  *  * @author jhyde  * @version $Id$  * @since Nov 22, 2003  */
end_comment

begin_interface
specifier|public
interface|interface
name|JavaRel
extends|extends
name|RelNode
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Creates a plan for this expression according to a calling convention.      *      * @param implementor implementor      */
name|ParseTree
name|implement
parameter_list|(
name|JavaRelImplementor
name|implementor
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End JavaRel.java
end_comment

end_unit

