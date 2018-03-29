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
name|StructKind
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
name|SqlMatchRecognize
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Scope for expressions in a {@code MATCH_RECOGNIZE} clause.  *  *<p>Defines variables and uses them as prefix of columns reference.  */
end_comment

begin_class
specifier|public
class|class
name|MatchRecognizeScope
extends|extends
name|ListScope
block|{
specifier|private
specifier|static
specifier|final
name|String
name|STAR
init|=
literal|"*"
decl_stmt|;
comment|//~ Instance fields ---------------------------------------------
specifier|private
specifier|final
name|SqlMatchRecognize
name|matchRecognize
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|patternVars
decl_stmt|;
comment|/** Creates a MatchRecognizeScope. */
specifier|public
name|MatchRecognizeScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlMatchRecognize
name|matchRecognize
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchRecognize
operator|=
name|matchRecognize
expr_stmt|;
name|patternVars
operator|=
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|nameMatcher
argument_list|()
operator|.
name|createSet
argument_list|()
expr_stmt|;
name|patternVars
operator|.
name|add
argument_list|(
name|STAR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|matchRecognize
return|;
block|}
specifier|public
name|SqlMatchRecognize
name|getMatchRecognize
parameter_list|()
block|{
return|return
name|matchRecognize
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPatternVars
parameter_list|()
block|{
return|return
name|patternVars
return|;
block|}
specifier|public
name|void
name|addPatternVar
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|patternVars
operator|.
name|add
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ScopeChild
argument_list|>
name|findQualifyingTableNames
parameter_list|(
name|String
name|columnName
parameter_list|,
name|SqlNode
name|ctx
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ScopeChild
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScopeChild
name|child
range|:
name|children
control|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|child
operator|.
name|namespace
operator|.
name|getRowType
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameMatcher
operator|.
name|field
argument_list|(
name|rowType
argument_list|,
name|columnName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|STAR
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|map
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|parent
operator|.
name|findQualifyingTableNames
argument_list|(
name|columnName
argument_list|,
name|ctx
argument_list|,
name|nameMatcher
argument_list|)
return|;
default|default:
return|return
name|map
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|,
name|boolean
name|deep
parameter_list|,
name|Resolved
name|resolved
parameter_list|)
block|{
if|if
condition|(
name|patternVars
operator|.
name|contains
argument_list|(
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
specifier|final
name|Step
name|path
init|=
operator|new
name|EmptyPath
argument_list|()
operator|.
name|plus
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|StructKind
operator|.
name|FULLY_QUALIFIED
argument_list|)
decl_stmt|;
specifier|final
name|ScopeChild
name|child
init|=
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|resolved
operator|.
name|found
argument_list|(
name|child
operator|.
name|namespace
argument_list|,
name|child
operator|.
name|nullable
argument_list|,
name|this
argument_list|,
name|path
argument_list|,
name|names
argument_list|)
expr_stmt|;
if|if
condition|(
name|resolved
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return;
block|}
block|}
name|super
operator|.
name|resolve
argument_list|(
name|names
argument_list|,
name|nameMatcher
argument_list|,
name|deep
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MatchRecognizeScope.java
end_comment

end_unit

