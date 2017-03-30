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
name|SqlNode
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
name|SqlNodeList
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
name|SqlSelect
import|;
end_import

begin_comment
comment|/**  * Represents the name-resolution context for expressions in an GROUP BY clause.  *  *<p>In some dialects of SQL, the GROUP BY clause can reference column aliases  * in the SELECT clause. For example, the query</p>  *  *<blockquote><code>SELECT empno AS x<br>  * FROM emp<br>  * GROUP BY x</code></blockquote>  *  *<p>is valid.</p>  */
end_comment

begin_class
specifier|public
class|class
name|GroupByScope
extends|extends
name|DelegatingScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlNodeList
name|groupByList
decl_stmt|;
specifier|private
specifier|final
name|SqlSelect
name|select
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|GroupByScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlNodeList
name|groupByList
parameter_list|,
name|SqlSelect
name|select
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupByList
operator|=
name|groupByList
expr_stmt|;
name|this
operator|.
name|select
operator|=
name|select
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|groupByList
return|;
block|}
specifier|public
name|void
name|validateExpr
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
name|SqlNode
name|expanded
init|=
name|validator
operator|.
name|expandGroupByOrHavingExpr
argument_list|(
name|expr
argument_list|,
name|this
argument_list|,
name|select
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// expression needs to be valid in parent scope too
name|parent
operator|.
name|validateExpr
argument_list|(
name|expanded
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End GroupByScope.java
end_comment

end_unit

