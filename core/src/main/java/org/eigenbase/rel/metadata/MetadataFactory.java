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
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_comment
comment|/**  * Source of metadata about relational expressions.  *  *<p>The metadata is typically various kinds of statistics used to estimate  * costs.</p>  *  *<p>Each kind of metadata has an interface that extends {@link Metadata} and  * has a method. Some examples: {@link BuiltInMetadata.Selectivity},  * {@link BuiltInMetadata.ColumnUniqueness}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|MetadataFactory
block|{
comment|/** Returns a metadata interface to get a particular kind of metadata    * from a particular relational expression. Returns null if that kind of    * metadata is not available. */
parameter_list|<
name|T
extends|extends
name|Metadata
parameter_list|>
name|T
name|query
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End MetadataFactory.java
end_comment

end_unit

