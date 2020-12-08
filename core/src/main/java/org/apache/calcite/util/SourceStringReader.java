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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Extension to {@link StringReader} that allows the original string to be  * recovered.  */
end_comment

begin_class
specifier|public
class|class
name|SourceStringReader
extends|extends
name|StringReader
block|{
specifier|private
specifier|final
name|String
name|s
decl_stmt|;
comment|/**    * Creates a source string reader.    *    * @param s String providing the character stream    */
specifier|public
name|SourceStringReader
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|s
argument_list|,
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
comment|/** Returns the source string. */
specifier|public
name|String
name|getSourceString
parameter_list|()
block|{
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

