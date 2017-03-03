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
name|avatica
operator|.
name|tck
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Contains methods that call JDK methods that the  *<a href="https://github.com/policeman-tools/forbidden-apis">forbidden  * APIs checker</a> does not approve of.  *  *<p>This class is excluded from the check, so methods called via this class  * will not fail the build.  */
end_comment

begin_class
class|class
name|Unsafe
block|{
specifier|private
name|Unsafe
parameter_list|()
block|{
block|}
comment|/** Calls {@link System#exit}. */
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
comment|/** Returns a {@link Calendar} with the local time zone and root    * locale. */
specifier|public
specifier|static
name|Calendar
name|localCalendar
parameter_list|()
block|{
return|return
name|Calendar
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Unsafe.java
end_comment

end_unit

