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
name|rel
operator|.
name|externalize
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
name|RelNode
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
name|SqlExplainLevel
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
name|util
operator|.
name|Pair
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
name|util
operator|.
name|XmlOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * Callback for a relational expression to dump in XML format.  */
end_comment

begin_class
specifier|public
class|class
name|RelXmlWriter
extends|extends
name|RelWriterImpl
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|XmlOutput
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
name|RelXmlWriter
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
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|xmlOutput
operator|=
operator|new
name|XmlOutput
argument_list|(
name|pw
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
specifier|protected
name|void
name|explain_
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
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
name|values
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Generates generic XML (sometimes called 'element-oriented XML'). Like    * this:    *    *<blockquote>    *<code>    *&lt;RelNode id="1" type="Join"&gt;<br>    *&nbsp;&nbsp;&lt;Property name="condition"&gt;EMP.DEPTNO =    * DEPT.DEPTNO&lt;/Property&gt;<br>    *&nbsp;&nbsp;&lt;Inputs&gt;<br>    *&nbsp;&nbsp;&nbsp;&nbsp;&lt;RelNode id="2" type="Project"&gt;<br>    *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Property name="expr1"&gt;x +    * y&lt;/Property&gt;<br>    *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Property    * name="expr2"&gt;45&lt;/Property&gt;<br>    *&nbsp;&nbsp;&nbsp;&nbsp;&lt;/RelNode&gt;<br>    *&nbsp;&nbsp;&nbsp;&nbsp;&lt;RelNode id="3" type="TableAccess"&gt;<br>    *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Property    * name="table"&gt;SALES.EMP&lt;/Property&gt;<br>    *&nbsp;&nbsp;&nbsp;&nbsp;&lt;/RelNode&gt;<br>    *&nbsp;&nbsp;&lt;/Inputs&gt;<br>    *&lt;/RelNode&gt;</code>    *</blockquote>    *    * @param rel    Relational expression    * @param values List of term-value pairs    */
specifier|private
name|void
name|explainGeneric
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
parameter_list|)
block|{
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
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pair
range|:
name|values
control|)
block|{
if|if
condition|(
name|pair
operator|.
name|right
operator|instanceof
name|RelNode
condition|)
block|{
name|inputs
operator|.
name|add
argument_list|(
operator|(
name|RelNode
operator|)
name|pair
operator|.
name|right
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|pair
operator|.
name|right
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
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
name|pair
operator|.
name|left
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
name|pair
operator|.
name|right
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
name|xmlOutput
operator|.
name|beginTag
argument_list|(
literal|"Inputs"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|spacer
operator|.
name|add
argument_list|(
literal|2
argument_list|)
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
name|spacer
operator|.
name|subtract
argument_list|(
literal|2
argument_list|)
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
comment|/**    * Generates specific XML (sometimes called 'attribute-oriented XML'). Like    * this:    *    *<pre>    *&lt;Join condition="EMP.DEPTNO = DEPT.DEPTNO"&gt;    *&lt;Project expr1="x + y" expr2="42"&gt;    *&lt;TableAccess table="SALES.EMPS"&gt;    *&lt;/Join&gt;    *</pre>    *    * @param rel    Relational expression    * @param values List of term-value pairs    */
specifier|private
name|void
name|explainSpecific
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
parameter_list|)
block|{
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
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|.
name|right
operator|instanceof
name|RelNode
condition|)
block|{
continue|continue;
block|}
name|xmlOutput
operator|.
name|attribute
argument_list|(
name|value
operator|.
name|left
argument_list|,
name|value
operator|.
name|right
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|xmlOutput
operator|.
name|endBeginTag
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
name|spacer
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|rel
operator|.
name|getInputs
argument_list|()
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
name|spacer
operator|.
name|subtract
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelXmlWriter.java
end_comment

end_unit

