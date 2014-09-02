begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|config
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|Casing
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|ConnectionConfigImpl
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|Quoting
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/** Implementation of {@link OptiqConnectionConfig}. */
end_comment

begin_class
specifier|public
class|class
name|OptiqConnectionConfigImpl
extends|extends
name|ConnectionConfigImpl
implements|implements
name|OptiqConnectionConfig
block|{
specifier|public
name|OptiqConnectionConfigImpl
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|super
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a copy of this configuration with one property changed. */
specifier|public
name|OptiqConnectionConfigImpl
name|set
parameter_list|(
name|OptiqConnectionProperty
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
specifier|final
name|Properties
name|properties1
init|=
operator|new
name|Properties
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|properties1
operator|.
name|setProperty
argument_list|(
name|property
operator|.
name|camelName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
operator|new
name|OptiqConnectionConfigImpl
argument_list|(
name|properties1
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|autoTemp
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|AUTO_TEMP
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|materializationsEnabled
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|MATERIALIZATIONS_ENABLED
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|createMaterializations
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|CREATE_MATERIALIZATIONS
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|String
name|model
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|MODEL
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
specifier|public
name|Lex
name|lex
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|LEX
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Lex
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|Quoting
name|quoting
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|QUOTING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Quoting
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|quoting
argument_list|)
return|;
block|}
specifier|public
name|Casing
name|unquotedCasing
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|UNQUOTED_CASING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Casing
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|unquotedCasing
argument_list|)
return|;
block|}
specifier|public
name|Casing
name|quotedCasing
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|QUOTED_CASING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Casing
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|quotedCasing
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|caseSensitive
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|CASE_SENSITIVE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|(
name|lex
argument_list|()
operator|.
name|caseSensitive
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|spark
parameter_list|()
block|{
return|return
name|OptiqConnectionProperty
operator|.
name|SPARK
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End OptiqConnectionConfigImpl.java
end_comment

end_unit

