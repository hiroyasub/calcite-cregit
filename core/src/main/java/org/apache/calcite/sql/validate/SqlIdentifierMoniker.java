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
name|sql
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlIdentifier
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
comment|/**  * An implementation of {@link SqlMoniker} that encapsulates the normalized name  * information of a {@link SqlIdentifier}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlIdentifierMoniker
implements|implements
name|SqlMoniker
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlIdentifier
name|id
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an SqlIdentifierMoniker.    */
specifier|public
name|SqlIdentifierMoniker
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlMonikerType
name|getType
parameter_list|()
block|{
return|return
name|SqlMonikerType
operator|.
name|COLUMN
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFullyQualifiedNames
parameter_list|()
block|{
return|return
name|id
operator|.
name|names
return|;
block|}
specifier|public
name|SqlIdentifier
name|toIdentifier
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlIdentifierMoniker.java
end_comment

end_unit

