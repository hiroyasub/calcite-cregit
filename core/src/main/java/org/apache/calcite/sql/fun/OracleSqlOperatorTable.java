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
name|fun
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
name|SqlFunction
import|;
end_import

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
name|util
operator|.
name|ReflectiveSqlOperatorTable
import|;
end_import

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

begin_comment
comment|/**  * Operator table that contains only Oracle-specific functions and operators.  *  * @deprecated Use  * {@link SqlLibraryOperatorTableFactory#getOperatorTable(SqlLibrary...)}  * instead, passing {@link SqlLibrary#ORACLE} as argument.  */
end_comment

begin_class
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
class|class
name|OracleSqlOperatorTable
extends|extends
name|ReflectiveSqlOperatorTable
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * The table of contains Oracle-specific operators.    */
specifier|private
specifier|static
annotation|@
name|Nullable
name|OracleSqlOperatorTable
name|instance
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|DECODE
init|=
name|SqlLibraryOperators
operator|.
name|DECODE
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|NVL
init|=
name|SqlLibraryOperators
operator|.
name|NVL
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|LTRIM
init|=
name|SqlLibraryOperators
operator|.
name|LTRIM
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|RTRIM
init|=
name|SqlLibraryOperators
operator|.
name|RTRIM
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|SUBSTR
init|=
name|SqlLibraryOperators
operator|.
name|SUBSTR_ORACLE
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|GREATEST
init|=
name|SqlLibraryOperators
operator|.
name|GREATEST
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|LEAST
init|=
name|SqlLibraryOperators
operator|.
name|LEAST
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|SqlFunction
name|TRANSLATE3
init|=
name|SqlLibraryOperators
operator|.
name|TRANSLATE3
decl_stmt|;
comment|/**    * Returns the Oracle operator table, creating it if necessary.    */
specifier|public
specifier|static
specifier|synchronized
name|OracleSqlOperatorTable
name|instance
parameter_list|()
block|{
name|OracleSqlOperatorTable
name|instance
init|=
name|OracleSqlOperatorTable
operator|.
name|instance
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
comment|// Creates and initializes the standard operator table.
comment|// Uses two-phase construction, because we can't initialize the
comment|// table until the constructor of the sub-class has completed.
name|instance
operator|=
operator|new
name|OracleSqlOperatorTable
argument_list|()
expr_stmt|;
name|instance
operator|.
name|init
argument_list|()
expr_stmt|;
name|OracleSqlOperatorTable
operator|.
name|instance
operator|=
name|instance
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
block|}
end_class

end_unit

