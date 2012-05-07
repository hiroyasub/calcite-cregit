begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|org
operator|.
name|eigenbase
operator|.
name|rel
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
name|rex
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
name|xom
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Callback for a relational expression to dump in XML format.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptXmlPlanWriter
extends|extends
name|RelOptPlanWriter
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|XMLOutput
name|xmlOutput
decl_stmt|;
name|boolean
name|generic
init|=
literal|true
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|// TODO jvs 23-Dec-2005:  honor detail level.  The current inheritance
comment|// structure makes this difficult without duplication; need to factor
comment|// out the filtering of attributes before rendering.
specifier|public
name|RelOptXmlPlanWriter
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|SqlExplainLevel
name|detailLevel
parameter_list|)
block|{
name|super
argument_list|(
name|pw
argument_list|,
name|detailLevel
argument_list|)
expr_stmt|;
name|xmlOutput
operator|=
operator|new
name|XMLOutput
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|setGlob
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|setCompact
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|explain
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
index|[]
name|terms
parameter_list|,
name|Object
index|[]
name|values
parameter_list|)
block|{
if|if
condition|(
name|generic
condition|)
block|{
name|explainGeneric
argument_list|(
name|rel
argument_list|,
name|terms
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|explainSpecific
argument_list|(
name|rel
argument_list|,
name|terms
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Generates generic XML (sometimes called 'element-oriented XML'). Like      * this:      *      *<blockquote>      *<code>      *&lt;RelNode id="1" type="Join"&gt;<br/>      *&nbsp;&nbsp;&lt;Property name="condition"&gt;EMP.DEPTNO =      * DEPT.DEPTNO&lt;/Property&gt;<br/>      *&nbsp;&nbsp;&lt;Inputs&gt;<br/>      *&nbsp;&nbsp;&nbsp;&nbsp;&lt;RelNode id="2" type="Project"&gt;<br/>      *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Property name="expr1"&gt;x +      * y&lt;/Property&gt;<br/>      *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Property      * name="expr2"&gt;45&lt;/Property&gt;<br/>      *&nbsp;&nbsp;&nbsp;&nbsp;&lt;/RelNode&gt;<br/>      *&nbsp;&nbsp;&nbsp;&nbsp;&lt;RelNode id="3" type="TableAccess"&gt;<br/>      *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Property      * name="table"&gt;SALES.EMP&lt;/Property&gt;<br/>      *&nbsp;&nbsp;&nbsp;&nbsp;&lt;/RelNode&gt;<br/>      *&nbsp;&nbsp;&lt;/Inputs&gt;<br/>      *&lt;/RelNode&gt;<br/>      *</code>      *</blockquote>      *      * @param rel Relational expression      * @param terms Names of the attributes of the plan      * @param values Values of the attributes of the plan      */
specifier|private
name|void
name|explainGeneric
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
index|[]
name|terms
parameter_list|,
name|Object
index|[]
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|rel
operator|.
name|getInputs
argument_list|()
decl_stmt|;
name|RexNode
index|[]
name|children
init|=
name|rel
operator|.
name|getChildExps
argument_list|()
decl_stmt|;
assert|assert
name|terms
operator|.
name|length
operator|==
operator|(
name|inputs
operator|.
name|size
argument_list|()
operator|+
name|children
operator|.
name|length
operator|+
name|values
operator|.
name|length
operator|)
operator|:
literal|"terms.length="
operator|+
name|terms
operator|.
name|length
operator|+
literal|" inputs.length="
operator|+
name|inputs
operator|.
name|size
argument_list|()
operator|+
literal|" children.length="
operator|+
name|children
operator|.
name|length
operator|+
literal|" values.length="
operator|+
name|values
operator|.
name|length
assert|;
name|String
name|relType
init|=
name|rel
operator|.
name|getRelTypeName
argument_list|()
decl_stmt|;
name|xmlOutput
operator|.
name|beginBeginTag
argument_list|(
literal|"RelNode"
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|attribute
argument_list|(
literal|"type"
argument_list|,
name|relType
argument_list|)
expr_stmt|;
comment|//xmlOutput.attribute("id", rel.getId() + "");
name|xmlOutput
operator|.
name|endBeginTag
argument_list|(
literal|"RelNode"
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RexNode
name|child
range|:
name|children
control|)
block|{
name|xmlOutput
operator|.
name|beginBeginTag
argument_list|(
literal|"Property"
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|attribute
argument_list|(
literal|"name"
argument_list|,
name|terms
index|[
name|inputs
operator|.
name|size
argument_list|()
operator|+
name|j
operator|++
index|]
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|endBeginTag
argument_list|(
literal|"Property"
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|cdata
argument_list|(
name|child
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|endTag
argument_list|(
literal|"Property"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|xmlOutput
operator|.
name|beginBeginTag
argument_list|(
literal|"Property"
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|attribute
argument_list|(
literal|"name"
argument_list|,
name|terms
index|[
name|inputs
operator|.
name|size
argument_list|()
operator|+
name|j
operator|++
index|]
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|endBeginTag
argument_list|(
literal|"Property"
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|cdata
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|endTag
argument_list|(
literal|"Property"
argument_list|)
expr_stmt|;
block|}
block|}
name|xmlOutput
operator|.
name|beginTag
argument_list|(
literal|"Inputs"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|level
operator|++
expr_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|input
operator|.
name|explain
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|level
operator|--
expr_stmt|;
name|xmlOutput
operator|.
name|endTag
argument_list|(
literal|"Inputs"
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|endTag
argument_list|(
literal|"RelNode"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Generates specific XML (sometimes called 'attribute-oriented XML'). Like      * this:      *      *<pre>      *&lt;Join condition="EMP.DEPTNO = DEPT.DEPTNO"&gt;      *&lt;Project expr1="x + y" expr2="42"&gt;      *&lt;TableAccess table="SALES.EMPS"&gt;      *&lt;/Join&gt;      *</pre>      *      * @param rel Relational expression      * @param terms Names of the attributes of the plan      * @param values Values of the attributes of the plan      */
specifier|private
name|void
name|explainSpecific
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
index|[]
name|terms
parameter_list|,
name|Object
index|[]
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|rel
operator|.
name|getInputs
argument_list|()
decl_stmt|;
name|RexNode
index|[]
name|children
init|=
name|rel
operator|.
name|getChildExps
argument_list|()
decl_stmt|;
assert|assert
name|terms
operator|.
name|length
operator|==
operator|(
name|inputs
operator|.
name|size
argument_list|()
operator|+
name|children
operator|.
name|length
operator|+
name|values
operator|.
name|length
operator|)
operator|:
literal|"terms.length="
operator|+
name|terms
operator|.
name|length
operator|+
literal|" inputs.length="
operator|+
name|inputs
operator|.
name|size
argument_list|()
operator|+
literal|" children.length="
operator|+
name|children
operator|.
name|length
operator|+
literal|" values.length="
operator|+
name|values
operator|.
name|length
assert|;
name|String
name|tagName
init|=
name|rel
operator|.
name|getRelTypeName
argument_list|()
decl_stmt|;
name|xmlOutput
operator|.
name|beginBeginTag
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|attribute
argument_list|(
literal|"id"
argument_list|,
name|rel
operator|.
name|getId
argument_list|()
operator|+
literal|""
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RexNode
name|child
range|:
name|children
control|)
block|{
name|xmlOutput
operator|.
name|attribute
argument_list|(
name|terms
index|[
name|inputs
operator|.
name|size
argument_list|()
operator|+
name|j
operator|++
index|]
argument_list|,
name|child
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|xmlOutput
operator|.
name|attribute
argument_list|(
name|terms
index|[
name|inputs
operator|.
name|size
argument_list|()
operator|+
name|j
operator|++
index|]
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|xmlOutput
operator|.
name|endBeginTag
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
name|level
operator|++
expr_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|input
operator|.
name|explain
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|level
operator|--
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptXmlPlanWriter.java
end_comment

end_unit

