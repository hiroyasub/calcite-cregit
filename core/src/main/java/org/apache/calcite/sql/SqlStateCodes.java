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
package|;
end_package

begin_comment
comment|/**  * Contains {@link org.apache.calcite.util.Glossary#SQL2003} SQL state codes.  *  *<p>SQL State codes are defined in  *  *<blockquote><pre>&#64;sql.2003 Part 2 Section 23.1</pre></blockquote>  *  * @deprecated Use {@code org.apache.calcite.avatica.SqlState}  */
end_comment

begin_enum
annotation|@
name|Deprecated
comment|// will be removed before 2.0
specifier|public
enum|enum
name|SqlStateCodes
block|{
name|CARDINALITY_VIOLATION
argument_list|(
literal|"cardinality violation"
argument_list|,
literal|"21"
argument_list|,
literal|"000"
argument_list|)
block|,
name|NULL_VALUE_NOT_ALLOWED
argument_list|(
literal|"null value not allowed"
argument_list|,
literal|"22"
argument_list|,
literal|"004"
argument_list|)
block|,
name|NUMERIC_VALUE_OUT_OF_RANGE
argument_list|(
literal|"numeric value out of range"
argument_list|,
literal|"22"
argument_list|,
literal|"003"
argument_list|)
block|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
name|String
name|msg
decl_stmt|;
specifier|private
specifier|final
name|String
name|stateClass
decl_stmt|;
specifier|private
specifier|final
name|String
name|stateSubClass
decl_stmt|;
name|SqlStateCodes
parameter_list|(
name|String
name|msg
parameter_list|,
name|String
name|stateClass
parameter_list|,
name|String
name|stateSubClass
parameter_list|)
block|{
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
name|this
operator|.
name|stateClass
operator|=
name|stateClass
expr_stmt|;
name|this
operator|.
name|stateSubClass
operator|=
name|stateSubClass
expr_stmt|;
block|}
specifier|public
name|String
name|getStateClass
parameter_list|()
block|{
return|return
name|stateClass
return|;
block|}
specifier|public
name|String
name|getStateSubClass
parameter_list|()
block|{
return|return
name|stateSubClass
return|;
block|}
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|stateClass
operator|+
name|stateSubClass
return|;
block|}
block|}
end_enum

end_unit

