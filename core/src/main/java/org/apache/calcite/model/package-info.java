begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Provides model files, in JSON format, defining schemas and other metadata.  *  *<p>Models are specified using a<code>model=&lt;uri&gt;</code> parameter on  * the JDBC connect string. Calcite loads the model while initializing the  * connection. It first parses the JSON, then uses a  * {@link org.apache.calcite.model.ModelHandler} as visitor over the parse  * tree.</p>  *  *<p>There are standard implementations of schema and table, but the user can  * provide their own by implementing the  * {@link org.apache.calcite.schema.SchemaFactory}  * or {@link org.apache.calcite.schema.TableFactory}  * interfaces and including a custom schema in the model.</p>  *  *<p>There are several examples of schemas in the  *<a href="https://calcite.apache.org/docs/tutorial.html">tutorial</a>.  */
end_comment

begin_annotation
annotation|@
name|DefaultQualifier
argument_list|(
name|value
operator|=
name|NonNull
operator|.
name|class
argument_list|,
name|locations
operator|=
name|TypeUseLocation
operator|.
name|FIELD
argument_list|)
end_annotation

begin_annotation
annotation|@
name|DefaultQualifier
argument_list|(
name|value
operator|=
name|NonNull
operator|.
name|class
argument_list|,
name|locations
operator|=
name|TypeUseLocation
operator|.
name|PARAMETER
argument_list|)
end_annotation

begin_annotation
annotation|@
name|DefaultQualifier
argument_list|(
name|value
operator|=
name|NonNull
operator|.
name|class
argument_list|,
name|locations
operator|=
name|TypeUseLocation
operator|.
name|RETURN
argument_list|)
end_annotation

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
name|NonNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|framework
operator|.
name|qual
operator|.
name|DefaultQualifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|framework
operator|.
name|qual
operator|.
name|TypeUseLocation
import|;
end_import

end_unit

