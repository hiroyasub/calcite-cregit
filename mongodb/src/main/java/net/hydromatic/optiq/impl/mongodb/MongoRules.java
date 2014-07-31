begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|mongodb
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|RexImpTable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|RexToLixTranslator
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
name|rel
operator|.
name|convert
operator|.
name|ConverterRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
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
name|reltype
operator|.
name|RelDataType
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
name|SqlKind
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
name|SqlStdOperatorTable
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
name|type
operator|.
name|SqlTypeName
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
name|validate
operator|.
name|SqlValidatorUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|trace
operator|.
name|EigenbaseTrace
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
name|Bug
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Rules and relational operators for  * {@link MongoRel#CONVENTION MONGO}  * calling convention.  */
end_comment

begin_class
specifier|public
class|class
name|MongoRules
block|{
specifier|private
name|MongoRules
parameter_list|()
block|{
block|}
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|EigenbaseTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptRule
index|[]
name|RULES
init|=
block|{
name|MongoSortRule
operator|.
name|INSTANCE
block|,
name|MongoFilterRule
operator|.
name|INSTANCE
block|,
name|MongoProjectRule
operator|.
name|INSTANCE
block|,
name|MongoAggregateRule
operator|.
name|INSTANCE
block|,   }
decl_stmt|;
comment|/** Returns 'string' if it is a call to item['string'], null otherwise. */
specifier|static
name|String
name|isItem
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|!=
name|SqlStdOperatorTable
operator|.
name|ITEM
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RexNode
name|op0
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|op1
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|op0
operator|instanceof
name|RexInputRef
operator|&&
operator|(
operator|(
name|RexInputRef
operator|)
name|op0
operator|)
operator|.
name|getIndex
argument_list|()
operator|==
literal|0
operator|&&
name|op1
operator|instanceof
name|RexLiteral
operator|&&
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|mongoFieldNames
parameter_list|(
specifier|final
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"$"
argument_list|)
condition|?
literal|"_"
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
else|:
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|rowType
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|static
name|String
name|maybeQuote
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
operator|!
name|needsQuote
argument_list|(
name|s
argument_list|)
condition|)
block|{
return|return
name|s
return|;
block|}
return|return
name|quote
argument_list|(
name|s
argument_list|)
return|;
block|}
specifier|static
name|String
name|quote
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|"'"
operator|+
name|s
operator|+
literal|"'"
return|;
comment|// TODO: handle embedded quotes
block|}
specifier|private
specifier|static
name|boolean
name|needsQuote
parameter_list|(
name|String
name|s
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|s
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Character
operator|.
name|isJavaIdentifierPart
argument_list|(
name|c
argument_list|)
operator|||
name|c
operator|==
literal|'$'
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Translator from {@link RexNode} to strings in MongoDB's expression    * language. */
specifier|static
class|class
name|RexToMongoTranslator
extends|extends
name|RexVisitorImpl
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inFields
decl_stmt|;
specifier|protected
name|RexToMongoTranslator
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|inFields
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|inFields
operator|=
name|inFields
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
if|if
condition|(
name|literal
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
return|return
literal|"{$ifNull: [null, "
operator|+
name|RexToLixTranslator
operator|.
name|translateLiteral
argument_list|(
name|literal
argument_list|,
name|literal
operator|.
name|getType
argument_list|()
argument_list|,
name|typeFactory
argument_list|,
name|RexImpTable
operator|.
name|NullAs
operator|.
name|NOT_POSSIBLE
argument_list|)
operator|+
literal|"]}"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
return|return
name|maybeQuote
argument_list|(
literal|"$"
operator|+
name|inFields
operator|.
name|get
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
name|String
name|name
init|=
name|isItem
argument_list|(
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
return|return
literal|"'$"
operator|+
name|name
operator|+
literal|"'"
return|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|visitList
argument_list|(
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|CAST
condition|)
block|{
return|return
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|ITEM
condition|)
block|{
specifier|final
name|RexNode
name|op1
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|op1
operator|instanceof
name|RexLiteral
operator|&&
name|op1
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|INTEGER
condition|)
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|OPTIQ_194_FIXED
condition|)
block|{
return|return
literal|"'"
operator|+
name|stripQuotes
argument_list|(
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|"["
operator|+
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
operator|+
literal|"]'"
return|;
block|}
return|return
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|"["
operator|+
name|strings
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|+
literal|"]"
return|;
block|}
block|}
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
specifier|private
name|String
name|stripQuotes
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
operator|&&
name|s
operator|.
name|endsWith
argument_list|(
literal|"'"
argument_list|)
condition|?
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
else|:
name|s
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|visitList
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|list
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
block|}
comment|/** Base class for planner rules that convert a relational expression to    * MongoDB calling convention. */
specifier|abstract
specifier|static
class|class
name|MongoConverterRule
extends|extends
name|ConverterRule
block|{
specifier|protected
specifier|final
name|Convention
name|out
decl_stmt|;
specifier|public
name|MongoConverterRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|Convention
name|out
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
block|}
comment|/**    * Rule to convert a {@link org.eigenbase.rel.SortRel} to a    * {@link MongoSortRel}.    */
specifier|private
specifier|static
class|class
name|MongoSortRule
extends|extends
name|MongoConverterRule
block|{
specifier|public
specifier|static
specifier|final
name|MongoSortRule
name|INSTANCE
init|=
operator|new
name|MongoSortRule
argument_list|()
decl_stmt|;
specifier|private
name|MongoSortRule
parameter_list|()
block|{
name|super
argument_list|(
name|SortRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|MongoRel
operator|.
name|CONVENTION
argument_list|,
literal|"MongoSortRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|SortRel
name|sort
init|=
operator|(
name|SortRel
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|sort
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
operator|.
name|replace
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|MongoSortRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|sort
operator|.
name|getChild
argument_list|()
argument_list|,
name|traitSet
operator|.
name|replace
argument_list|(
name|RelCollationImpl
operator|.
name|EMPTY
argument_list|)
argument_list|)
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link org.eigenbase.rel.FilterRel} to a    * {@link MongoFilterRel}.    */
specifier|private
specifier|static
class|class
name|MongoFilterRule
extends|extends
name|MongoConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|MongoFilterRule
name|INSTANCE
init|=
operator|new
name|MongoFilterRule
argument_list|()
decl_stmt|;
specifier|private
name|MongoFilterRule
parameter_list|()
block|{
name|super
argument_list|(
name|FilterRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|MongoRel
operator|.
name|CONVENTION
argument_list|,
literal|"MongoFilterRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|FilterRel
name|filter
init|=
operator|(
name|FilterRel
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|MongoFilterRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|filter
operator|.
name|getChild
argument_list|()
argument_list|,
name|traitSet
argument_list|)
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link org.eigenbase.rel.ProjectRel} to a    * {@link MongoProjectRel}.    */
specifier|private
specifier|static
class|class
name|MongoProjectRule
extends|extends
name|MongoConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|MongoProjectRule
name|INSTANCE
init|=
operator|new
name|MongoProjectRule
argument_list|()
decl_stmt|;
specifier|private
name|MongoProjectRule
parameter_list|()
block|{
name|super
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|MongoRel
operator|.
name|CONVENTION
argument_list|,
literal|"MongoProjectRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|ProjectRel
name|project
init|=
operator|(
name|ProjectRel
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|project
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|MongoProjectRel
argument_list|(
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|project
operator|.
name|getChild
argument_list|()
argument_list|,
name|traitSet
argument_list|)
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|,
name|ProjectRel
operator|.
name|Flags
operator|.
name|BOXED
argument_list|)
return|;
block|}
block|}
comment|/*    /**    * Rule to convert a {@link CalcRel} to an    * {@link MongoCalcRel}.    o/   private static class MongoCalcRule       extends MongoConverterRule {     private MongoCalcRule(MongoConvention out) {       super(           CalcRel.class,           Convention.NONE,           out,           "MongoCalcRule");     }      public RelNode convert(RelNode rel) {       final CalcRel calc = (CalcRel) rel;        // If there's a multiset, let FarragoMultisetSplitter work on it       // first.       if (RexMultisetUtil.containsMultiset(calc.getProgram())) {         return null;       }        return new MongoCalcRel(           rel.getCluster(),           rel.getTraitSet().replace(out),           convert(               calc.getChild(),               calc.getTraitSet().replace(out)),           calc.getProgram(),           ProjectRelBase.Flags.Boxed);     }   }    public static class MongoCalcRel extends SingleRel implements MongoRel {     private final RexProgram program;      /**      * Values defined in {@link org.eigenbase.rel.ProjectRelBase.Flags}.      o/     protected int flags;      public MongoCalcRel(         RelOptCluster cluster,         RelTraitSet traitSet,         RelNode child,         RexProgram program,         int flags) {       super(cluster, traitSet, child);       assert getConvention() instanceof MongoConvention;       this.flags = flags;       this.program = program;       this.rowType = program.getOutputRowType();     }      public RelOptPlanWriter explainTerms(RelOptPlanWriter pw) {       return program.explainCalc(super.explainTerms(pw));     }      public double getRows() {       return FilterRel.estimateFilteredRows(           getChild(), program);     }      public RelOptCost computeSelfCost(RelOptPlanner planner) {       double dRows = RelMetadataQuery.getRowCount(this);       double dCpu =           RelMetadataQuery.getRowCount(getChild())               * program.getExprCount();       double dIo = 0;       return planner.makeCost(dRows, dCpu, dIo);     }      public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {       return new MongoCalcRel(           getCluster(),           traitSet,           sole(inputs),           program.copy(),           getFlags());     }      public int getFlags() {       return flags;     }      public RexProgram getProgram() {       return program;     }      public SqlString implement(MongoImplementor implementor) {       final SqlBuilder buf = new SqlBuilder(implementor.dialect);       buf.append("SELECT ");       if (isStar(program)) {         buf.append("*");       } else {         for (Ord<RexLocalRef> ref : Ord.zip(program.getProjectList())) {           buf.append(ref.i == 0 ? "" : ", ");           expr(buf, program, ref.e);           alias(buf, null, getRowType().getFieldNames().get(ref.i));         }       }       implementor.newline(buf)           .append("FROM ");       implementor.subquery(buf, 0, getChild(), "t");       if (program.getCondition() != null) {         implementor.newline(buf);         buf.append("WHERE ");         expr(buf, program, program.getCondition());       }       return buf.toSqlString();     }      private static boolean isStar(RexProgram program) {       int i = 0;       for (RexLocalRef ref : program.getProjectList()) {         if (ref.getIndex() != i++) {           return false;         }       }       return i == program.getInputRowType().getFieldCount();     }      private static void expr(         SqlBuilder buf, RexProgram program, RexNode rex) {       if (rex instanceof RexLocalRef) {         final int index = ((RexLocalRef) rex).getIndex();         expr(buf, program, program.getExprList().get(index));       } else if (rex instanceof RexInputRef) {         buf.identifier(             program.getInputRowType().getFieldNames().get(                 ((RexInputRef) rex).getIndex()));       } else if (rex instanceof RexLiteral) {         toSql(buf, (RexLiteral) rex);       } else if (rex instanceof RexCall) {         final RexCall call = (RexCall) rex;         switch (call.getOperator().getSyntax()) {         case Binary:           expr(buf, program, call.getOperands().get(0));           buf.append(' ')               .append(call.getOperator().toString())               .append(' ');           expr(buf, program, call.getOperands().get(1));           break;         default:           throw new AssertionError(call.getOperator());         }       } else {         throw new AssertionError(rex);       }     }   }    private static SqlBuilder toSql(SqlBuilder buf, RexLiteral rex) {     switch (rex.getTypeName()) {     case CHAR:     case VARCHAR:       return buf.append(           new NlsString(rex.getValue2().toString(), null, null)               .asSql(false, false));     default:       return buf.append(rex.getValue2().toString());     }   }  */
comment|/**    * Rule to convert an {@link org.eigenbase.rel.AggregateRel} to an    * {@link MongoAggregateRel}.    */
specifier|private
specifier|static
class|class
name|MongoAggregateRule
extends|extends
name|MongoConverterRule
block|{
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|INSTANCE
init|=
operator|new
name|MongoAggregateRule
argument_list|()
decl_stmt|;
specifier|private
name|MongoAggregateRule
parameter_list|()
block|{
name|super
argument_list|(
name|AggregateRel
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|MongoRel
operator|.
name|CONVENTION
argument_list|,
literal|"MongoAggregateRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|AggregateRel
name|agg
init|=
operator|(
name|AggregateRel
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|agg
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|MongoAggregateRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|agg
operator|.
name|getChild
argument_list|()
argument_list|,
name|traitSet
argument_list|)
argument_list|,
name|agg
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|agg
operator|.
name|getAggCallList
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidRelException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|warning
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/*   /**    * Rule to convert an {@link org.eigenbase.rel.UnionRel} to a    * {@link MongoUnionRel}.    o/   private static class MongoUnionRule       extends MongoConverterRule {     private MongoUnionRule(MongoConvention out) {       super(           UnionRel.class,           Convention.NONE,           out,           "MongoUnionRule");     }      public RelNode convert(RelNode rel) {       final UnionRel union = (UnionRel) rel;       final RelTraitSet traitSet =           union.getTraitSet().replace(out);       return new MongoUnionRel(           rel.getCluster(),           traitSet,           convertList(union.getInputs(), traitSet),           union.all);     }   }    public static class MongoUnionRel       extends UnionRelBase       implements MongoRel {     public MongoUnionRel(         RelOptCluster cluster,         RelTraitSet traitSet,         List<RelNode> inputs,         boolean all) {       super(cluster, traitSet, inputs, all);     }      public MongoUnionRel copy(         RelTraitSet traitSet, List<RelNode> inputs, boolean all) {       return new MongoUnionRel(getCluster(), traitSet, inputs, all);     }      @Override     public RelOptCost computeSelfCost(RelOptPlanner planner) {       return super.computeSelfCost(planner).multiplyBy(.1);     }      public SqlString implement(MongoImplementor implementor) {       return setOpSql(this, implementor, "UNION");     }   }    private static SqlString setOpSql(       SetOpRel setOpRel, MongoImplementor implementor, String op) {     final SqlBuilder buf = new SqlBuilder(implementor.dialect);     for (Ord<RelNode> input : Ord.zip(setOpRel.getInputs())) {       if (input.i> 0) {         implementor.newline(buf)             .append(op + (setOpRel.all ? " ALL " : ""));         implementor.newline(buf);       }       buf.append(implementor.visitChild(input.i, input.e));     }     return buf.toSqlString();   }    /**    * Rule to convert an {@link org.eigenbase.rel.IntersectRel} to an    * {@link MongoIntersectRel}.    o/   private static class MongoIntersectRule       extends MongoConverterRule {     private MongoIntersectRule(MongoConvention out) {       super(           IntersectRel.class,           Convention.NONE,           out,           "MongoIntersectRule");     }      public RelNode convert(RelNode rel) {       final IntersectRel intersect = (IntersectRel) rel;       if (intersect.all) {         return null; // INTERSECT ALL not implemented       }       final RelTraitSet traitSet =           intersect.getTraitSet().replace(out);       return new MongoIntersectRel(           rel.getCluster(),           traitSet,           convertList(intersect.getInputs(), traitSet),           intersect.all);     }   }    public static class MongoIntersectRel       extends IntersectRelBase       implements MongoRel {     public MongoIntersectRel(         RelOptCluster cluster,         RelTraitSet traitSet,         List<RelNode> inputs,         boolean all) {       super(cluster, traitSet, inputs, all);       assert !all;     }      public MongoIntersectRel copy(         RelTraitSet traitSet, List<RelNode> inputs, boolean all) {       return new MongoIntersectRel(getCluster(), traitSet, inputs, all);     }      public SqlString implement(MongoImplementor implementor) {       return setOpSql(this, implementor, " intersect ");     }   }    /**    * Rule to convert an {@link org.eigenbase.rel.MinusRel} to an    * {@link MongoMinusRel}.    o/   private static class MongoMinusRule       extends MongoConverterRule {     private MongoMinusRule(MongoConvention out) {       super(           MinusRel.class,           Convention.NONE,           out,           "MongoMinusRule");     }      public RelNode convert(RelNode rel) {       final MinusRel minus = (MinusRel) rel;       if (minus.all) {         return null; // EXCEPT ALL not implemented       }       final RelTraitSet traitSet =           rel.getTraitSet().replace(out);       return new MongoMinusRel(           rel.getCluster(),           traitSet,           convertList(minus.getInputs(), traitSet),           minus.all);     }   }    public static class MongoMinusRel       extends MinusRelBase       implements MongoRel {     public MongoMinusRel(         RelOptCluster cluster,         RelTraitSet traitSet,         List<RelNode> inputs,         boolean all) {       super(cluster, traitSet, inputs, all);       assert !all;     }      public MongoMinusRel copy(         RelTraitSet traitSet, List<RelNode> inputs, boolean all) {       return new MongoMinusRel(getCluster(), traitSet, inputs, all);     }      public SqlString implement(MongoImplementor implementor) {       return setOpSql(this, implementor, " minus ");     }   }    public static class MongoValuesRule extends MongoConverterRule {     private MongoValuesRule(MongoConvention out) {       super(           ValuesRel.class,           Convention.NONE,           out,           "MongoValuesRule");     }      @Override     public RelNode convert(RelNode rel) {       ValuesRel valuesRel = (ValuesRel) rel;       return new MongoValuesRel(           valuesRel.getCluster(),           valuesRel.getRowType(),           valuesRel.getTuples(),           valuesRel.getTraitSet().plus(out));     }   }    public static class MongoValuesRel       extends ValuesRelBase       implements MongoRel {     MongoValuesRel(         RelOptCluster cluster,         RelDataType rowType,         List<List<RexLiteral>> tuples,         RelTraitSet traitSet) {       super(cluster, rowType, tuples, traitSet);     }      @Override     public RelNode copy(         RelTraitSet traitSet, List<RelNode> inputs) {       assert inputs.isEmpty();       return new MongoValuesRel(           getCluster(), rowType, tuples, traitSet);     }      public SqlString implement(MongoImplementor implementor) {       throw new AssertionError(); // TODO:     }   } */
block|}
end_class

begin_comment
comment|// End MongoRules.java
end_comment

end_unit

