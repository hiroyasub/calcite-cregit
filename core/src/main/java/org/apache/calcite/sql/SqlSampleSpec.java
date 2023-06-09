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
name|dialect
operator|.
name|CalciteSqlDialect
import|;
end_import

begin_comment
comment|/**  * Specification of a SQL sample.  *  *<p>For example, the query</p>  *  *<blockquote>  *<pre>SELECT *  * FROM emp TABLESAMPLE SUBSTITUTE('medium')</pre>  *</blockquote>  *  *<p>declares a sample which is created using {@link #createNamed}.</p>  *  *<p>A sample is not a {@link SqlNode}. To include it in a parse tree, wrap it  * as a literal, viz:  * {@link SqlLiteral#createSample(SqlSampleSpec, org.apache.calcite.sql.parser.SqlParserPos)}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlSampleSpec
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlSampleSpec
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Creates a sample which substitutes one relation for another.    */
specifier|public
specifier|static
name|SqlSampleSpec
name|createNamed
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SqlSubstitutionSampleSpec
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Creates a table sample without repeatability.    *    * @param isBernoulli      true if Bernoulli style sampling is to be used;    *                         false for implementation specific sampling    * @param samplePercentage likelihood of a row appearing in the sample    */
specifier|public
specifier|static
name|SqlSampleSpec
name|createTableSample
parameter_list|(
name|boolean
name|isBernoulli
parameter_list|,
name|float
name|samplePercentage
parameter_list|)
block|{
return|return
operator|new
name|SqlTableSampleSpec
argument_list|(
name|isBernoulli
argument_list|,
name|samplePercentage
argument_list|)
return|;
block|}
comment|/**    * Creates a table sample with repeatability.    *    * @param isBernoulli      true if Bernoulli style sampling is to be used;    *                         false for implementation specific sampling    * @param samplePercentage likelihood of a row appearing in the sample    * @param repeatableSeed   seed value used to reproduce the same sample    */
specifier|public
specifier|static
name|SqlSampleSpec
name|createTableSample
parameter_list|(
name|boolean
name|isBernoulli
parameter_list|,
name|float
name|samplePercentage
parameter_list|,
name|int
name|repeatableSeed
parameter_list|)
block|{
return|return
operator|new
name|SqlTableSampleSpec
argument_list|(
name|isBernoulli
argument_list|,
name|samplePercentage
argument_list|,
name|repeatableSeed
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/** Sample specification that orders substitution. */
specifier|public
specifier|static
class|class
name|SqlSubstitutionSampleSpec
extends|extends
name|SqlSampleSpec
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|SqlSubstitutionSampleSpec
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SUBSTITUTE("
operator|+
name|CalciteSqlDialect
operator|.
name|DEFAULT
operator|.
name|quoteStringLiteral
argument_list|(
name|name
argument_list|)
operator|+
literal|")"
return|;
block|}
block|}
comment|/** Sample specification. */
specifier|public
specifier|static
class|class
name|SqlTableSampleSpec
extends|extends
name|SqlSampleSpec
block|{
specifier|private
specifier|final
name|boolean
name|isBernoulli
decl_stmt|;
specifier|private
specifier|final
name|float
name|samplePercentage
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isRepeatable
decl_stmt|;
specifier|private
specifier|final
name|int
name|repeatableSeed
decl_stmt|;
specifier|private
name|SqlTableSampleSpec
parameter_list|(
name|boolean
name|isBernoulli
parameter_list|,
name|float
name|samplePercentage
parameter_list|)
block|{
name|this
operator|.
name|isBernoulli
operator|=
name|isBernoulli
expr_stmt|;
name|this
operator|.
name|samplePercentage
operator|=
name|samplePercentage
expr_stmt|;
name|this
operator|.
name|isRepeatable
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|repeatableSeed
operator|=
literal|0
expr_stmt|;
block|}
specifier|private
name|SqlTableSampleSpec
parameter_list|(
name|boolean
name|isBernoulli
parameter_list|,
name|float
name|samplePercentage
parameter_list|,
name|int
name|repeatableSeed
parameter_list|)
block|{
name|this
operator|.
name|isBernoulli
operator|=
name|isBernoulli
expr_stmt|;
name|this
operator|.
name|samplePercentage
operator|=
name|samplePercentage
expr_stmt|;
name|this
operator|.
name|isRepeatable
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|repeatableSeed
operator|=
name|repeatableSeed
expr_stmt|;
block|}
comment|/**      * Indicates Bernoulli vs. System sampling.      */
specifier|public
name|boolean
name|isBernoulli
parameter_list|()
block|{
return|return
name|isBernoulli
return|;
block|}
comment|/**      * Returns sampling percentage. Range is 0.0 to 1.0, exclusive      */
specifier|public
name|float
name|getSamplePercentage
parameter_list|()
block|{
return|return
name|samplePercentage
return|;
block|}
comment|/**      * Indicates whether repeatable seed should be used.      */
specifier|public
name|boolean
name|isRepeatable
parameter_list|()
block|{
return|return
name|isRepeatable
return|;
block|}
comment|/**      * Seed to produce repeatable samples.      */
specifier|public
name|int
name|getRepeatableSeed
parameter_list|()
block|{
return|return
name|repeatableSeed
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|isBernoulli
condition|?
literal|"BERNOULLI"
else|:
literal|"SYSTEM"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|samplePercentage
operator|*
literal|100.0
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
if|if
condition|(
name|isRepeatable
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" REPEATABLE("
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|repeatableSeed
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

