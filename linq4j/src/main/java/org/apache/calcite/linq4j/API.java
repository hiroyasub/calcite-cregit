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
name|linq4j
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
name|Documented
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
name|Target
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|CONSTRUCTOR
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|FIELD
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|METHOD
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|TYPE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
operator|.
name|RUNTIME
import|;
end_import

begin_comment
comment|/**  * {@code @API} is used to annotate public types, methods, constructors, and  * fields within a framework or application in order to publish their  * {@link #status} and level of stability and to indicate how they are intended  * to be used by {@link #consumers} of the API.  *  *<p>If {@code @API} is present on a type, it is considered to hold for all  * public members of the type as well. However, a member of such an annotated  * type is allowed to declare a {@link Status} of lower stability. For example,  * a class annotated with {@code @API(status = STABLE)} may declare a constructor  * for internal usage that is annotated with {@code @API(status = INTERNAL)}.  *  *<p>This annotation is inspired by  *<a href="https://github.com/apiguardian-team/apiguardian/blob/master/src/main/java/org/apiguardian/api/API.java">apiguardian</a>,  * we move the annotation into our project because we don't want the project to have additional  * dependency jar which includes only a single Java file.  *  * @since 1.23.0  */
end_comment

begin_annotation_defn
annotation|@
name|Target
argument_list|(
block|{
name|TYPE
block|,
name|METHOD
block|,
name|CONSTRUCTOR
block|,
name|FIELD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RUNTIME
argument_list|)
annotation|@
name|Documented
specifier|public
annotation_defn|@interface
name|API
block|{
comment|/**    * The current {@linkplain Status status} of the API.    */
name|Status
name|status
parameter_list|()
function_decl|;
comment|/**    * The version of the API when the {@link #status} was last changed.    *    *<p>Defaults to an empty string, signifying that the<em>since</em>    * version is unknown.    */
name|String
name|since
parameter_list|()
default|default
literal|""
function_decl|;
comment|/**    * List of packages belonging to intended consumers.    *    *<p>The supplied packages can be fully qualified package names or    * patterns containing asterisks that will be used as wildcards.    *    *<p>Defaults to {@code "*"}, signifying that the API is intended to be    * consumed by any package.    */
name|String
index|[]
name|consumers
parameter_list|()
default|default
literal|"*"
function_decl|;
comment|/**    * Indicates the status of an API element and therefore its level of    * stability as well.    */
enum|enum
name|Status
block|{
comment|/**      * Must not be used by any external code. Might be removed without prior      * notice.      */
name|INTERNAL
block|,
comment|/**      * Should no longer be used. Might disappear in the next minor release.      */
name|DEPRECATED
block|,
comment|/**      * Intended for new, experimental features where the publisher of the      * API is looking for feedback.      *      *<p>Use with caution. Might be promoted to {@link #MAINTAINED} or      * {@link #STABLE} in the future, but might also be removed without      * prior notice.      */
name|EXPERIMENTAL
block|,
comment|/**      * Intended for features that will not be changed in a backwards-incompatible      * way for at least the next minor release of the current major version.      * If scheduled for removal, such a feature will be demoted to      * {@link #DEPRECATED} first.      */
name|MAINTAINED
block|,
comment|/**      * Intended for features that will not be changed in a backwards-incompatible      * way in the current major version.      */
name|STABLE
block|;    }
block|}
end_annotation_defn

end_unit

