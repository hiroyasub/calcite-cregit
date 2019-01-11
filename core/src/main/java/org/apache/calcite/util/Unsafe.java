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
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_comment
comment|/**  * Contains methods that call JDK methods that the  *<a href="https://github.com/policeman-tools/forbidden-apis">forbidden  * APIs checker</a> does not approve of.  *  *<p>This class is excluded from the check, so methods called via this class  * will not fail the build.  */
end_comment

begin_class
specifier|public
class|class
name|Unsafe
block|{
specifier|private
name|Unsafe
parameter_list|()
block|{
block|}
comment|/** Calls {@link System#exit}. */
specifier|public
specifier|static
name|void
name|systemExit
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
comment|/** Calls {@link Object#notifyAll()}. */
specifier|public
specifier|static
name|void
name|notifyAll
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|o
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|/** Calls {@link Object#wait()}. */
specifier|public
specifier|static
name|void
name|wait
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|o
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
comment|/** Clears the contents of a {@link StringWriter}. */
specifier|public
specifier|static
name|void
name|clear
parameter_list|(
name|StringWriter
name|sw
parameter_list|)
block|{
comment|// Included in this class because StringBuffer is banned.
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Appends to {@link StringWriter}. */
specifier|public
specifier|static
name|void
name|append
parameter_list|(
name|StringWriter
name|sw
parameter_list|,
name|CharSequence
name|charSequence
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
comment|// Included in this class because StringBuffer is banned.
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|append
argument_list|(
name|charSequence
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End Unsafe.java
end_comment

end_unit

