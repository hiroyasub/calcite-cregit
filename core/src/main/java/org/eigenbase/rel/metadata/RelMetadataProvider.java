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
name|*
import|;
end_import

begin_comment
comment|/**  * RelMetadataProvider defines an interface for obtaining metadata about  * relational expressions. This interface is weakly-typed and is not intended to  * be called directly in most contexts; instead, use a strongly-typed facade  * such as {@link RelMetadataQuery}.  *  *<p>For background and motivation, see<a  * href="http://wiki.eigenbase.org/RelationalExpressionMetadata">wiki</a>.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelMetadataProvider
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Retrieves metadata about a relational expression.      *      * @param rel relational expression of interest      * @param metadataQueryName name of metadata query to invoke      * @param args arguments to metadata query (expected number and type depend      * on query name; must have well-defined hashCode/equals for use by      * caching); null can be used instead of empty array      *      * @return metadata result (actual type depends on query name), or null if      * the provider cannot answer the given query/rel combination; it is better      * to return null than to return a possibly incorrect answer      */
specifier|public
name|Object
name|getRelMetadata
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
name|metadataQueryName
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelMetadataProvider.java
end_comment

end_unit

