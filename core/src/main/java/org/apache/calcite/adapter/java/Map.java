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
name|adapter
operator|.
name|java
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_comment
comment|/**  * Annotation that indicates that a field is a map type.  */
end_comment

begin_annotation_defn
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|FIELD
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
specifier|public
annotation_defn|@interface
name|Map
block|{
comment|/** Key type. */
name|Class
argument_list|<
name|?
argument_list|>
name|key
parameter_list|()
function_decl|;
comment|/** Value type. */
name|Class
argument_list|<
name|?
argument_list|>
name|value
parameter_list|()
function_decl|;
comment|/** Whether keys may be null. */
name|boolean
name|keyIsNullable
parameter_list|()
default|default
literal|true
function_decl|;
comment|/** Whether values may be null. */
name|boolean
name|valueIsNullable
parameter_list|()
default|default
literal|true
function_decl|;
block|}
end_annotation_defn

end_unit

