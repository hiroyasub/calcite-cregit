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
name|util
operator|.
name|mapping
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * A<dfn>Mapping</dfn> is a relationship between a source domain to target  * domain of integers.  *  *<p>This interface represents the most general possible mapping. Depending on  * the {@link MappingType} of a particular mapping, some of the operations may  * not be applicable. If you call the method, you will receive a runtime error.  * For instance:  *  *<ul>  *<li>If a target has more than one source, then the method  *     {@link #getSource(int)} will throw  *     {@link Mappings.TooManyElementsException}.  *<li>If a source has no targets, then the method {@link #getTarget} will throw  *     {@link Mappings.NoElementException}.  *</ul>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Mapping
extends|extends
name|Mappings
operator|.
name|FunctionMapping
extends|,
name|Mappings
operator|.
name|SourceMapping
extends|,
name|Mappings
operator|.
name|TargetMapping
extends|,
name|Iterable
argument_list|<
name|IntPair
argument_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns an iterator over the elements in this mapping.    *    *<p>This method is optional; implementations may throw    * {@link UnsupportedOperationException}.    */
name|Iterator
argument_list|<
name|IntPair
argument_list|>
name|iterator
parameter_list|()
function_decl|;
comment|/**    * Returns the number of sources. Valid sources will be in the range 0 ..    * sourceCount.    */
name|int
name|getSourceCount
parameter_list|()
function_decl|;
comment|/**    * Returns the number of targets. Valid targets will be in the range 0 ..    * targetCount.    */
name|int
name|getTargetCount
parameter_list|()
function_decl|;
name|MappingType
name|getMappingType
parameter_list|()
function_decl|;
comment|/**    * Returns whether this mapping is the identity.    */
name|boolean
name|isIdentity
parameter_list|()
function_decl|;
comment|/**    * Removes all elements in the mapping.    */
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * Returns the number of elements in the mapping.    */
name|int
name|size
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Mapping.java
end_comment

end_unit

