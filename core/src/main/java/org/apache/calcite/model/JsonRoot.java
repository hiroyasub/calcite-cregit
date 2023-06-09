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
name|model
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonCreator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
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
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Root schema element.  *  *<p>A POJO with fields of {@link Boolean}, {@link String}, {@link ArrayList},  * {@link java.util.LinkedHashMap LinkedHashMap}, per Jackson simple data  * binding.</p>  *  *<p>Schema structure is as follows:</p>  *  *<!-- CHECKSTYLE: OFF -->  *<pre>{@code Root}  *   {@link JsonSchema} (in collection {@link JsonRoot#schemas schemas})  *     {@link JsonType} (in collection {@link JsonMapSchema#types types})  *     {@link JsonTable} (in collection {@link JsonMapSchema#tables tables})  *       {@link JsonColumn} (in collection {@link JsonTable#columns columns})  *       {@link JsonStream} (in field {@link JsonTable#stream stream})  *     {@link JsonView}  *     {@link JsonFunction} (in collection {@link JsonMapSchema#functions functions})  *     {@link JsonLattice} (in collection {@link JsonSchema#lattices lattices})  *       {@link JsonMeasure} (in collection {@link JsonLattice#defaultMeasures defaultMeasures})  *       {@link JsonTile} (in collection {@link JsonLattice#tiles tiles})  *         {@link JsonMeasure} (in collection {@link JsonTile#measures measures})  *     {@link JsonMaterialization} (in collection {@link JsonSchema#materializations materializations})  *</pre>  *<!-- CHECKSTYLE: ON -->  *  *<p>See the<a href="http://calcite.apache.org/docs/model.html">JSON  * model reference</a>.  */
end_comment

begin_class
specifier|public
class|class
name|JsonRoot
block|{
comment|/** Schema model version number. Required, must have value "1.0". */
specifier|public
specifier|final
name|String
name|version
decl_stmt|;
comment|/** Name of the schema that will become the default schema for connections    * to Calcite that use this model.    *    *<p>Optional, case-sensitive. If specified, there must be a schema in this    * model with this name.    */
specifier|public
specifier|final
annotation|@
name|Nullable
name|String
name|defaultSchema
decl_stmt|;
comment|/** List of schema elements.    *    *<p>The list may be empty.    */
specifier|public
specifier|final
name|List
argument_list|<
name|JsonSchema
argument_list|>
name|schemas
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|JsonRoot
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"version"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
name|String
name|version
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"defaultSchema"
argument_list|)
annotation|@
name|Nullable
name|String
name|defaultSchema
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|requireNonNull
argument_list|(
name|version
argument_list|,
literal|"version"
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultSchema
operator|=
name|defaultSchema
expr_stmt|;
block|}
block|}
end_class

end_unit

