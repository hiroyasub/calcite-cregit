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
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
operator|.
name|*
import|;
end_import

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
comment|/**  * The name-resolution scope of a SELECT clause. The objects visible are those  * in the FROM clause, and objects inherited from the parent scope.  *  *  *<p>This object is both a {@link SqlValidatorScope} and a {@link  * SqlValidatorNamespace}. In the query</p>  *  *<blockquote>  *<pre>SELECT name FROM (  *     SELECT *  *     FROM emp  *     WHERE gender = 'F')</pre></blockquote>  *  *<p>we need to use the {@link SelectScope} as a  * {@link SqlValidatorNamespace} when resolving 'name', and  * as a {@link SqlValidatorScope} when resolving 'gender'.</p>  *  *<h3>Scopes</h3>  *  *<p>In the query</p>  *  *<blockquote>  *<pre>  * SELECT expr1  * FROM t1,  *     t2,  *     (SELECT expr2 FROM t3) AS q3  * WHERE c1 IN (SELECT expr3 FROM t4)  * ORDER BY expr4</pre>  *</blockquote>  *  *<p>The scopes available at various points of the query are as follows:</p>  *  *<ul>  *<li>expr1 can see t1, t2, q3</li>  *<li>expr2 can see t3</li>  *<li>expr3 can see t4, t1, t2</li>  *<li>expr4 can see t1, t2, q3, plus (depending upon the dialect) any aliases  * defined in the SELECT clause</li>  *</ul>  *  *<h3>Namespaces</h3>  *  *<p>In the above query, there are 4 namespaces:</p>  *  *<ul>  *<li>t1</li>  *<li>t2</li>  *<li>(SELECT expr2 FROM t3) AS q3</li>  *<li>(SELECT expr3 FROM t4)</li>  *</ul>  *  * @see SelectNamespace  */
end_comment

begin_class
specifier|public
class|class
name|SelectScope
extends|extends
name|ListScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlSelect
name|select
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|windowNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|SqlNode
argument_list|>
name|expandedSelectList
init|=
literal|null
decl_stmt|;
comment|/**    * List of column names which sort this scope. Empty if this scope is not    * sorted. Null if has not been computed yet.    */
specifier|private
name|SqlNodeList
name|orderList
decl_stmt|;
comment|/**    * Scope to use to resolve windows    */
specifier|private
specifier|final
name|SqlValidatorScope
name|windowParent
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a scope corresponding to a SELECT clause.    *    * @param parent    Parent scope, must not be null    * @param winParent Scope for window parent, may be null    * @param select    Select clause    */
name|SelectScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlValidatorScope
name|winParent
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
name|select
operator|=
name|select
expr_stmt|;
name|this
operator|.
name|windowParent
operator|=
name|winParent
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlValidatorTable
name|getTable
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|SqlSelect
name|getNode
parameter_list|()
block|{
return|return
name|select
return|;
block|}
specifier|public
name|SqlWindow
name|lookupWindow
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|SqlNodeList
name|windowList
init|=
name|select
operator|.
name|getWindowList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|windowList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlWindow
name|window
init|=
operator|(
name|SqlWindow
operator|)
name|windowList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|SqlIdentifier
name|declId
init|=
name|window
operator|.
name|getDeclName
argument_list|()
decl_stmt|;
assert|assert
name|declId
operator|.
name|isSimple
argument_list|()
assert|;
if|if
condition|(
name|declId
operator|.
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|window
return|;
block|}
block|}
comment|// if not in the select scope, then check window scope
if|if
condition|(
name|windowParent
operator|!=
literal|null
condition|)
block|{
return|return
name|windowParent
operator|.
name|lookupWindow
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
name|SqlMonotonicity
name|monotonicity
init|=
name|expr
operator|.
name|getMonotonicity
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|monotonicity
operator|!=
name|SqlMonotonicity
operator|.
name|NotMonotonic
condition|)
block|{
return|return
name|monotonicity
return|;
block|}
comment|// TODO: compare fully qualified names
specifier|final
name|SqlNodeList
name|orderList
init|=
name|getOrderList
argument_list|()
decl_stmt|;
if|if
condition|(
name|orderList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|SqlNode
name|order0
init|=
name|orderList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|monotonicity
operator|=
name|SqlMonotonicity
operator|.
name|Increasing
expr_stmt|;
if|if
condition|(
operator|(
name|order0
operator|instanceof
name|SqlCall
operator|)
operator|&&
operator|(
operator|(
operator|(
name|SqlCall
operator|)
name|order0
operator|)
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|DESC
operator|)
condition|)
block|{
name|monotonicity
operator|=
name|monotonicity
operator|.
name|reverse
argument_list|()
expr_stmt|;
name|order0
operator|=
operator|(
operator|(
name|SqlCall
operator|)
name|order0
operator|)
operator|.
name|getOperands
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
block|}
if|if
condition|(
name|expr
operator|.
name|equalsDeep
argument_list|(
name|order0
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
name|monotonicity
return|;
block|}
block|}
return|return
name|SqlMonotonicity
operator|.
name|NotMonotonic
return|;
block|}
specifier|public
name|SqlNodeList
name|getOrderList
parameter_list|()
block|{
if|if
condition|(
name|orderList
operator|==
literal|null
condition|)
block|{
comment|// Compute on demand first call.
name|orderList
operator|=
operator|new
name|SqlNodeList
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
expr_stmt|;
if|if
condition|(
name|children
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|SqlValidatorNamespace
name|child
init|=
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|right
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|monotonicExprs
init|=
name|child
operator|.
name|getMonotonicExprs
argument_list|()
decl_stmt|;
if|if
condition|(
name|monotonicExprs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|orderList
operator|.
name|add
argument_list|(
name|monotonicExprs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|left
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|orderList
return|;
block|}
specifier|public
name|void
name|addWindowName
parameter_list|(
name|String
name|winName
parameter_list|)
block|{
name|windowNames
operator|.
name|add
argument_list|(
name|winName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|existingWindowName
parameter_list|(
name|String
name|winName
parameter_list|)
block|{
for|for
control|(
name|String
name|windowName
range|:
name|windowNames
control|)
block|{
if|if
condition|(
name|windowName
operator|.
name|equalsIgnoreCase
argument_list|(
name|winName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// if the name wasn't found then check the parent(s)
name|SqlValidatorScope
name|walker
init|=
name|parent
decl_stmt|;
while|while
condition|(
operator|(
literal|null
operator|!=
name|walker
operator|)
operator|&&
operator|!
operator|(
name|walker
operator|instanceof
name|EmptyScope
operator|)
condition|)
block|{
if|if
condition|(
name|walker
operator|instanceof
name|SelectScope
condition|)
block|{
specifier|final
name|SelectScope
name|parentScope
init|=
operator|(
name|SelectScope
operator|)
name|walker
decl_stmt|;
return|return
name|parentScope
operator|.
name|existingWindowName
argument_list|(
name|winName
argument_list|)
return|;
block|}
name|walker
operator|=
name|parent
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getExpandedSelectList
parameter_list|()
block|{
return|return
name|expandedSelectList
return|;
block|}
specifier|public
name|void
name|setExpandedSelectList
parameter_list|(
name|List
argument_list|<
name|SqlNode
argument_list|>
name|selectList
parameter_list|)
block|{
name|expandedSelectList
operator|=
name|selectList
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SelectScope.java
end_comment

end_unit

