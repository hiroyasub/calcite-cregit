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
name|metadata
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
name|Multimap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_comment
comment|/**  * RelMetadataProvider defines an interface for obtaining metadata about  * relational expressions. This interface is weakly-typed and is not intended to  * be called directly in most contexts; instead, use a strongly-typed facade  * such as {@link RelMetadataQuery}.  *  *<p>For background and motivation, see<a  * href="http://wiki.eigenbase.org/RelationalExpressionMetadata">wiki</a>.  *  *<p>If your provider is not a singleton, we recommend that you implement  * {@link Object#equals(Object)} and {@link Object#hashCode()} methods. This  * makes the cache of {@link JaninoRelMetadataProvider} more effective.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelMetadataProvider
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Retrieves metadata of a particular type and for a particular sub-class    * of relational expression.    *    *<p>The object returned is a function. It can be applied to a relational    * expression of the given type to create a metadata object.</p>    *    *<p>For example, you might call</p>    *    *<blockquote><pre>    * RelMetadataProvider provider;    * LogicalFilter filter;    * RexNode predicate;    * Function&lt;RelNode, Metadata&gt; function =    *   provider.apply(LogicalFilter.class, Selectivity.class};    * Selectivity selectivity = function.apply(filter);    * Double d = selectivity.selectivity(predicate);    *</pre></blockquote>    *    * @deprecated Use {@link RelMetadataQuery}.    *    * @param relClass Type of relational expression    * @param metadataClass Type of metadata    * @return Function that will field a metadata instance; or null if this    *     provider cannot supply metadata of this type    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
operator|<
expr|@
name|Nullable
name|M
expr|extends @
name|Nullable
name|Metadata
operator|>
expr|@
name|Nullable
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|apply
argument_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|M
argument_list|>
name|metadataClass
argument_list|)
expr_stmt|;
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
name|Multimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|<
name|M
argument_list|>
argument_list|>
name|handlers
parameter_list|(
name|MetadataDef
argument_list|<
name|M
argument_list|>
name|def
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

