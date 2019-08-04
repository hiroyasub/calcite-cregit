begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|hint
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Experimental
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Base class for relational expressions with {@link RelHint}s.  *  *<p>Relational expressions that can attach hints should implement  * this interface.  *  *<p>This interface is experimental, currently, we make  * {@link org.apache.calcite.rel.core.Project}  * {@link org.apache.calcite.rel.core.Join}  * {@link org.apache.calcite.rel.core.TableScan}  * implement this interface and add an argument named "hints" to construct these  * relational expressions if hints are attached.  *  *<p>This design is not that elegant and mature, because we have to copy the hints whenever these  * relational expressions are copied or used to derive new relational expressions.  * Even though we have implemented the mechanism to propagate the hints, for large queries,  * there would be many cases where the hints are not copied to the right RelNode,  * and the effort/memory is wasted if we are copying the hint to a RelNode  * but the hint is not used.  */
end_comment

begin_interface
annotation|@
name|Experimental
specifier|public
interface|interface
name|Hintable
block|{
comment|/**    * Attach list of hints to this relational expression, should be overridden by    * every logical node that supports hint. This method is only for    * internal use during sql-to-rel conversion.    *    *<p>The sub-class should return a new copy of the relational expression. We make    * the default implementation return the relational expression directly only    * because not every kind of relational expression supports hints.    *    * @param hintList The hints to attach to this relational expression    * @return Relational expression with the hints {@code hintList} attached    */
specifier|default
name|RelNode
name|attachHints
parameter_list|(
name|List
argument_list|<
name|RelHint
argument_list|>
name|hintList
parameter_list|)
block|{
return|return
operator|(
name|RelNode
operator|)
name|this
return|;
block|}
comment|/**    * @return The hints list of this relational expressions    */
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|getHints
parameter_list|()
function_decl|;
comment|/**    * Merge this relation expression's hints with the given hint list.    *    *<p>The default behavior is to put them in one list and eliminate the duplicates.    *    * @param hintList Hints to be merged    * @return A merged hint list    */
specifier|default
name|List
argument_list|<
name|RelHint
argument_list|>
name|mergeHints
parameter_list|(
name|List
argument_list|<
name|RelHint
argument_list|>
name|hintList
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|hintList
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|RelHint
argument_list|>
name|hints
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|(
name|getHints
argument_list|()
argument_list|)
decl_stmt|;
name|hints
operator|.
name|addAll
argument_list|(
name|hintList
argument_list|)
expr_stmt|;
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|hints
argument_list|)
return|;
block|}
block|}
end_interface

begin_comment
comment|// End Hintable.java
end_comment

end_unit

