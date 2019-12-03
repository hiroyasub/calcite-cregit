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
name|rex
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
name|SqlKind
import|;
end_import

begin_comment
comment|/**  * Access to a field of a row-expression.  *  *<p>You might expect to use a<code>RexFieldAccess</code> to access columns of  * relational tables, for example, the expression<code>emp.empno</code> in the  * query  *  *<blockquote>  *<pre>SELECT emp.empno FROM emp</pre>  *</blockquote>  *  *<p>but there is a specialized expression {@link RexInputRef} for this  * purpose. So in practice,<code>RexFieldAccess</code> is usually used to  * access fields of correlating variables, for example the expression  *<code>emp.deptno</code> in  *  *<blockquote>  *<pre>SELECT ename  * FROM dept  * WHERE EXISTS (  *     SELECT NULL  *     FROM emp  *     WHERE emp.deptno = dept.deptno  *     AND gender = 'F')</pre>  *</blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|RexFieldAccess
extends|extends
name|RexNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexNode
name|expr
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeField
name|field
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|RexFieldAccess
parameter_list|(
name|RexNode
name|expr
parameter_list|,
name|RelDataTypeField
name|field
parameter_list|)
block|{
name|this
operator|.
name|expr
operator|=
name|expr
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|expr
operator|+
literal|"."
operator|+
name|field
operator|.
name|getName
argument_list|()
expr_stmt|;
assert|assert
name|expr
operator|.
name|getType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|field
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|==
name|field
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataTypeField
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|field
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|FIELD_ACCESS
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitFieldAccess
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
name|visitor
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitFieldAccess
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
return|;
block|}
comment|/**    * Returns the expression whose field is being accessed.    */
specifier|public
name|RexNode
name|getReferenceExpr
parameter_list|()
block|{
return|return
name|expr
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RexFieldAccess
name|that
init|=
operator|(
name|RexFieldAccess
operator|)
name|o
decl_stmt|;
return|return
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
operator|&&
name|expr
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|expr
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

