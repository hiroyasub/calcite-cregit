begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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

begin_comment
comment|/**  * Namespace for COLLECT and TABLE constructs.  *  *<p>Examples:  *  *<ul>  *<li><code>SELECT deptno, COLLECT(empno) FROM emp GROUP BY deptno</code>,  *<li><code>SELECT * FROM (TABLE getEmpsInDept(30))</code>.  *</ul>  *  *<p>NOTE: jhyde, 2006/4/24: These days, this class seems to be used  * exclusively for the<code>MULTISET</code> construct.  *  * @see CollectScope  */
end_comment

begin_class
specifier|public
class|class
name|CollectNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlCall
name|child
decl_stmt|;
specifier|private
specifier|final
name|SqlValidatorScope
name|scope
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a CollectNamespace.    *    * @param child         Parse tree node    * @param scope         Scope    * @param enclosingNode Enclosing parse tree node    */
name|CollectNamespace
parameter_list|(
name|SqlCall
name|child
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlNode
name|enclosingNode
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|SqlValidatorImpl
operator|)
name|scope
operator|.
name|getValidator
argument_list|()
argument_list|,
name|enclosingNode
argument_list|)
expr_stmt|;
name|this
operator|.
name|child
operator|=
name|child
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
assert|assert
name|child
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|MULTISET_VALUE_CONSTRUCTOR
operator|||
name|child
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|MULTISET_QUERY_CONSTRUCTOR
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|()
block|{
return|return
name|child
operator|.
name|getOperator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|child
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|child
return|;
block|}
specifier|public
name|SqlValidatorScope
name|getScope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
block|}
end_class

begin_comment
comment|// End CollectNamespace.java
end_comment

end_unit

