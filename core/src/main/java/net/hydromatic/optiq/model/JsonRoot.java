begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|model
package|;
end_package

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

begin_comment
comment|/**  * Root schema element.  *  *<p>A POJO with fields of {@link Boolean}, {@link String}, {@link ArrayList},  * {@link java.util.LinkedHashMap}, per Jackson simple data binding.</p>  *  *<p>Schema structure is as follows:</p>  *  *<pre>{@code Root}  *   {@link JsonSchema} (in collection {@link JsonRoot#schemas schemas})  *     {@link JsonTable} (in collection {@link JsonMapSchema#tables tables})  *       {@link JsonColumn} (in collection {@link JsonTable#columns column}  *     {@link JsonView}  *     {@link JsonFunction}  (in collection {@link JsonMapSchema#functions functions})  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|JsonRoot
block|{
specifier|public
name|String
name|version
decl_stmt|;
specifier|public
name|String
name|defaultSchema
decl_stmt|;
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
argument_list|<
name|JsonSchema
argument_list|>
argument_list|()
decl_stmt|;
block|}
end_class

begin_comment
comment|// End JsonRoot.java
end_comment

end_unit

