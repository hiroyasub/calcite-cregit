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
name|babel
operator|.
name|postgresql
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
name|Symbolizable
import|;
end_import

begin_comment
comment|/**  * Defines the keywords that can occur immediately after the "ROLLBACK" or "COMMIT" keywords.  *  * @see SqlCommit  * @see SqlRollback  * @see<a href="https://www.postgresql.org/docs/current/sql-commit.html">COMMIT specification</a>  * @see<a href="https://www.postgresql.org/docs/current/sql-rollback.html">ROLLBACK specification</a>  */
end_comment

begin_enum
specifier|public
enum|enum
name|AndChain
implements|implements
name|Symbolizable
block|{
name|AND_CHAIN
block|,
name|AND_NO_CHAIN
block|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|"_"
argument_list|,
literal|" "
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

