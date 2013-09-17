begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Defines the keywords which can occur immediately after the "INSERT" keyword.  * Standard SQL has no such keywords. This enumeration exists only to allow  * extension projects to define them.  */
end_comment

begin_class
specifier|public
class|class
name|SqlInsertKeyword
extends|extends
name|EnumeratedValues
operator|.
name|BasicValue
implements|implements
name|SqlLiteral
operator|.
name|SqlSymbol
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|EnumeratedValues
name|enumeration
init|=
operator|new
name|EnumeratedValues
argument_list|(
operator|new
name|SqlInsertKeyword
index|[]
block|{}
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlInsertKeyword
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|ordinal
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
specifier|public
name|int
name|ordinal
parameter_list|()
block|{
return|return
name|getOrdinal
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlInsertKeyword.java
end_comment

end_unit

