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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
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
comment|/** Helper that holds onto {@link AutoCloseable} resources and releases them  * when its {@code #close} method is called.  *  *<p>Similar to {@link com.google.common.io.Closer} but can deal with  * {@link AutoCloseable} and doesn't throw {@link IOException}. */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Closer
implements|implements
name|AutoCloseable
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|AutoCloseable
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Registers a resource. */
specifier|public
parameter_list|<
name|E
extends|extends
name|AutoCloseable
parameter_list|>
name|E
name|add
parameter_list|(
name|E
name|e
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|AutoCloseable
name|closeable
range|:
name|list
control|)
block|{
try|try
block|{
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End Closer.java
end_comment

end_unit

