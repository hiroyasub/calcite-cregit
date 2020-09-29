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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|SqlIdentifier
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Represents the name-resolution context for expressions in an ORDER BY clause.  *  *<p>In some dialects of SQL, the ORDER BY clause can reference column aliases  * in the SELECT clause. For example, the query</p>  *  *<blockquote><code>SELECT empno AS x<br>  * FROM emp<br>  * ORDER BY x</code></blockquote>  *  *<p>is valid.</p>  */
end_comment

begin_class
specifier|public
class|class
name|OrderByScope
extends|extends
name|DelegatingScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlNodeList
name|orderList
decl_stmt|;
specifier|private
specifier|final
name|SqlSelect
name|select
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|OrderByScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlNodeList
name|orderList
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
name|orderList
operator|=
name|orderList
expr_stmt|;
name|this
operator|.
name|select
operator|=
name|select
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|orderList
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|findAllColumnNames
parameter_list|(
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
parameter_list|)
block|{
specifier|final
name|SqlValidatorNamespace
name|ns
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|select
argument_list|)
decl_stmt|;
name|addColumnNames
argument_list|(
name|ns
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlQualified
name|fullyQualify
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
block|{
comment|// If it's a simple identifier, look for an alias.
if|if
condition|(
name|identifier
operator|.
name|isSimple
argument_list|()
operator|&&
name|validator
operator|.
name|config
argument_list|()
operator|.
name|sqlConformance
argument_list|()
operator|.
name|isSortByAlias
argument_list|()
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|identifier
operator|.
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorNamespace
name|selectNs
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|select
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|selectNs
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|SqlNameMatcher
name|nameMatcher
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|nameMatcher
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeField
name|field
init|=
name|nameMatcher
operator|.
name|field
argument_list|(
name|rowType
argument_list|,
name|name
argument_list|)
decl_stmt|;
specifier|final
name|int
name|aliasCount
init|=
name|aliasCount
argument_list|(
name|nameMatcher
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliasCount
operator|>
literal|1
condition|)
block|{
comment|// More than one column has this alias.
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|identifier
argument_list|,
name|RESOURCE
operator|.
name|columnAmbiguous
argument_list|(
name|name
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|!
name|field
operator|.
name|isDynamicStar
argument_list|()
operator|&&
name|aliasCount
operator|==
literal|1
condition|)
block|{
comment|// if identifier is resolved to a dynamic star, use super.fullyQualify() for such case.
return|return
name|SqlQualified
operator|.
name|create
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
name|selectNs
argument_list|,
name|identifier
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|fullyQualify
argument_list|(
name|identifier
argument_list|)
return|;
block|}
comment|/** Returns the number of columns in the SELECT clause that have {@code name}    * as their implicit (e.g. {@code t.name}) or explicit (e.g.    * {@code t.c as name}) alias. */
specifier|private
name|int
name|aliasCount
parameter_list|(
name|SqlNameMatcher
name|nameMatcher
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SqlNode
name|s
range|:
name|select
operator|.
name|getSelectList
argument_list|()
control|)
block|{
specifier|final
name|String
name|alias
init|=
name|SqlValidatorUtil
operator|.
name|getAlias
argument_list|(
name|s
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|alias
operator|!=
literal|null
operator|&&
name|nameMatcher
operator|.
name|matches
argument_list|(
name|alias
argument_list|,
name|name
argument_list|)
condition|)
block|{
name|n
operator|++
expr_stmt|;
block|}
block|}
return|return
name|n
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|resolveColumn
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
block|{
specifier|final
name|SqlValidatorNamespace
name|selectNs
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|select
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|selectNs
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|SqlNameMatcher
name|nameMatcher
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|nameMatcher
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeField
name|field
init|=
name|nameMatcher
operator|.
name|field
argument_list|(
name|rowType
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|final
name|SqlValidatorScope
name|selectScope
init|=
name|validator
operator|.
name|getSelectScope
argument_list|(
name|select
argument_list|)
decl_stmt|;
return|return
name|selectScope
operator|.
name|resolveColumn
argument_list|(
name|name
argument_list|,
name|ctx
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|expandOrderExpr
argument_list|(
name|select
argument_list|,
name|expr
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

end_unit

