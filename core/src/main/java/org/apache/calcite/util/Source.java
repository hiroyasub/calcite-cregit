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
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * Source of data.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Source
block|{
name|URL
name|url
parameter_list|()
function_decl|;
name|File
name|file
parameter_list|()
function_decl|;
name|String
name|path
parameter_list|()
function_decl|;
name|Reader
name|reader
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|InputStream
name|openStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|String
name|protocol
parameter_list|()
function_decl|;
comment|/** Looks for a suffix on a path and returns    * either the path with the suffix removed    * or the original path. */
name|Source
name|trim
parameter_list|(
name|String
name|suffix
parameter_list|)
function_decl|;
comment|/** Looks for a suffix on a path and returns    * either the path with the suffix removed    * or null. */
annotation|@
name|Nullable
name|Source
name|trimOrNull
parameter_list|(
name|String
name|suffix
parameter_list|)
function_decl|;
comment|/** Returns a source whose path concatenates this with a child.    *    *<p>For example,    *<ul>    *<li>source("/foo").append(source("bar"))    *   returns source("/foo/bar")    *<li>source("/foo").append(source("/bar"))    *   returns source("/bar")    *   because "/bar" was already absolute    *</ul>    */
name|Source
name|append
parameter_list|(
name|Source
name|child
parameter_list|)
function_decl|;
comment|/** Returns a relative source, if this source is a child of a given base.    *    *<p>For example,    *<ul>    *<li>source("/foo/bar").relative(source("/foo"))    *   returns source("bar")    *<li>source("/baz/bar").relative(source("/foo"))    *   returns source("/baz/bar")    *</ul>    */
name|Source
name|relative
parameter_list|(
name|Source
name|source
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

