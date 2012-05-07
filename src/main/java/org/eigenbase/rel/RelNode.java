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
name|rel
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
name|rel
operator|.
name|metadata
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

begin_comment
comment|/**  * A<code>RelNode</code> is a relational expression. It is NOT an {@link  * openjava.ptree.Expression}.  *  *<p>If this type of relational expression has some particular rules, it should  * implement the<em>public static</em> method {@link  * AbstractRelNode#register}.</p>  *  *<p>When a relational expression comes to be implemented, the system allocates  * a {@link org.eigenbase.relopt.RelImplementor} to manage the process. Every  * implementable relational expression has a {@link RelTraitSet} describing its  * physical attributes. The RelTraitSet always contains a {@link  * CallingConvention} describing how the expression passes data to its consuming  * relational expression, but may contain other traits, including some applied  * externally. Because traits can be applied externally, implementaitons of  * RelNode should never assume the size or contents of their trait set (beyond  * those traits configured by the RelNode itself).</p>  *  *<p>For each calling-convention, there is a corresponding sub-interface of  * RelNode. For example, {@link org.eigenbase.oj.rel.JavaRel} has operations to  * manage the conversion to a graph of {@link CallingConvention#JAVA Java  * calling-convention}, and it interacts with a {@link  * org.eigenbase.oj.rel.JavaRelImplementor}.</p>  *  *<p>A relational expression is only required to implement its  * calling-convention's interface when it is actually implemented, that is,  * converted into a plan/program. This means that relational expressions which  * cannot be implemented, such as converters, are not required to implement  * their convention's interface.</p>  *  *<p>Every relational expression must derive from {@link AbstractRelNode}. (Why  * have the<code>RelNode</code> interface, then? We need a root interface,  * because an interface can only derive from an interface.)</p>  *  * @author jhyde  * @version $Id$  * @since May 24, 2004  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelNode
extends|extends
name|RelOptNode
extends|,
name|Cloneable
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns whether this relational expression is an access to<code>      * table</code>.      */
specifier|public
name|boolean
name|isAccessTo
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
function_decl|;
comment|/**      * Returns an array of this relational expression's child expressions (not      * including the inputs returned by {@link #getInputs}. If there are no      * child expressions, returns an empty array, not<code>null</code>.      */
specifier|public
name|RexNode
index|[]
name|getChildExps
parameter_list|()
function_decl|;
comment|/**      * Return the CallingConvention trait from this RelNode's {@link      * #getTraitSet() trait set}.      *      * @return this RelNode's CallingConvention      */
specifier|public
name|CallingConvention
name|getConvention
parameter_list|()
function_decl|;
comment|/**      * Sets the name of the variable which is to be implicitly set at runtime      * each time a row is returned from this relational expression      *      * @param correlVariable Name of correlating variable      */
specifier|public
name|void
name|setCorrelVariable
parameter_list|(
name|String
name|correlVariable
parameter_list|)
function_decl|;
comment|/**      * Returns the name of the variable which is to be implicitly set at runtime      * each time a row is returned from this relational expression; or null if      * there is no variable.      *      * @return Name of correlating variable, or null      */
specifier|public
name|String
name|getCorrelVariable
parameter_list|()
function_decl|;
comment|/**      * Returns whether the same value will not come out twice. Default value is      *<code>false</code>, derived classes should override.      */
specifier|public
name|boolean
name|isDistinct
parameter_list|()
function_decl|;
comment|/**      * Returns the<code>i</code><sup>th</sup> input relational expression.      *      * @param i Ordinal of input      *      * @return<code>i</code><sup>th</sup> input      */
specifier|public
name|RelNode
name|getInput
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
comment|/**      * Returns a variable with which to reference the current row of this      * relational expression as a correlating variable. Creates a variable if      * none exists.      */
specifier|public
name|String
name|getOrCreateCorrelVariable
parameter_list|()
function_decl|;
comment|/**      * Returns the sub-query this relational expression belongs to. A sub-query      * determines the scope for correlating variables (see {@link      * #setCorrelVariable(String)}).      *      * @return Sub-query      */
specifier|public
name|RelOptQuery
name|getQuery
parameter_list|()
function_decl|;
comment|/**      * Returns the type of the rows returned by this relational expression.      */
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
comment|/**      * Returns the type of the rows expected for an input. Defaults to {@link      * #getRowType}.      *      * @param ordinalInParent input's 0-based ordinal with respect to this      * parent rel      *      * @return expected row type      */
specifier|public
name|RelDataType
name|getExpectedInputRowType
parameter_list|(
name|int
name|ordinalInParent
parameter_list|)
function_decl|;
comment|/**      * Returns an array of this relational expression's inputs. If there are no      * inputs, returns an empty array, not<code>null</code>.      */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
function_decl|;
comment|/**      * Returns an estimate of the number of rows this relational expression will      * return.      *      *<p>NOTE jvs 29-Mar-2006: Don't call this method directly. Instead, use      * {@link RelMetadataQuery#getRowCount}, which gives plugins a chance to      * override the rel's default ideas about row count.      */
specifier|public
name|double
name|getRows
parameter_list|()
function_decl|;
comment|/**      * Returns the names of variables which are set in this relational      * expression but also used and therefore not available to parents of this      * relational expression.      *      *<p>By default, returns the empty set. Derived classes may override this      * method.</p>      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getVariablesStopped
parameter_list|()
function_decl|;
comment|/**      * Collects variables known to be used by this expression or its      * descendants. By default, no such information is available and must be      * derived by analyzing sub-expressions, but some optimizer implementations      * may insert special expressions which remember such information.      *      * @param variableSet receives variables used      */
specifier|public
name|void
name|collectVariablesUsed
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|variableSet
parameter_list|)
function_decl|;
comment|/**      * Collects variables set by this expression.      *      * @param variableSet receives variables known to be set by      */
specifier|public
name|void
name|collectVariablesSet
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|variableSet
parameter_list|)
function_decl|;
comment|/**      * Interacts with the {@link RelVisitor} in a {@link      * org.eigenbase.util.Glossary#VisitorPattern visitor pattern} to traverse      * the tree of relational expressions.      */
specifier|public
name|void
name|childrenAccept
parameter_list|(
name|RelVisitor
name|visitor
parameter_list|)
function_decl|;
comment|/**      * Returns the cost of this plan (not including children). The base      * implementation throws an error; derived classes should override.      *      *<p>NOTE jvs 29-Mar-2006: Don't call this method directly. Instead, use      * {@link RelMetadataQuery#getNonCumulativeCost}, which gives plugins a      * chance to override the rel's default ideas about cost.      */
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
function_decl|;
specifier|public
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
function_decl|;
comment|/**      * Receives notification that this expression is about to be registered. The      * implementation of this method must at least register all child      * expressions.      */
specifier|public
name|RelNode
name|onRegister
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
function_decl|;
comment|/**      * Computes the digest, assigns it, and returns it. For planner use only.      */
specifier|public
name|String
name|recomputeDigest
parameter_list|()
function_decl|;
comment|/**      * Registers a correlation variable.      *      * @see #getVariablesStopped      */
specifier|public
name|void
name|registerCorrelVariable
parameter_list|(
name|String
name|correlVariable
parameter_list|)
function_decl|;
comment|/**      * Replaces the<code>ordinalInParent</code><sup>th</sup> input. You must      * override this method if you override {@link #getInputs}.      */
specifier|public
name|void
name|replaceInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|RelNode
name|p
parameter_list|)
function_decl|;
comment|/**      * If this relational expression represents an access to a table, returns      * that table, otherwise returns null.      */
specifier|public
name|RelOptTable
name|getTable
parameter_list|()
function_decl|;
comment|/**      * Returns the name of this relational expression's class, sans package      * name, for use in {@link #explain}. For example, for a<code>      * org.eigenbase.rel.ArrayRel.ArrayReader</code>, this method returns      * "ArrayReader".      */
specifier|public
name|String
name|getRelTypeName
parameter_list|()
function_decl|;
comment|/**      * Returns whether this relational expression is valid.      *      *<p>If assertions are enabled, this method is typically called with<code>      * fail</code> =<code>true</code>, as follows:      *      *<blockquote>      *<pre>assert rel.isValid(true)</pre>      *</blockquote>      *      * This signals that the method can throw an {@link AssertionError} if it is      * not valid.      *      * @param fail Whether to fail if invalid      *      * @return Whether relational expression is valid      *      * @throws AssertionError if this relational expression is invalid and      * fail=true and assertions are enabled      */
specifier|public
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
function_decl|;
comment|/**      * Returns a description of the physical ordering (or orderings) of this      * relational expression.      *      * @post return != null      */
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
function_decl|;
comment|/**      * Clones this RelNode.      *      *<b>IMPORTANT.</b> This method must be overridden whenever an existing,      * concrete RelNode is extended. Otherwise, calling clone() will produce a      * differently typed RelNode, resulting in invalid or incorrect query plans.      * (Since many sub-classes implement this method by calling {@link #copy} or      * a variant of it, it is sufficient in those cases to override      * {@code copy}.)      *      * @return a clone of this RelNode      */
name|RelNode
name|clone
parameter_list|()
function_decl|;
comment|/**      * Creates a copy of this relational expression, perhaps changing traits and      * inputs.      *      *<p>Sub-classes with other important attributes are encouraged to create      * variants of this method with more parameters.</p>      *      * @param traitSet Trait set      * @param inputs Inputs      * @return Copy of this relational expression, substituting traits and      * inputs      */
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelNode.java
end_comment

end_unit

