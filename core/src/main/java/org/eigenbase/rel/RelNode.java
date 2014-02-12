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
comment|/**  * A<code>RelNode</code> is a relational expression.  *  *<p>A relational expression is not a scalar expression; see  * {@link org.eigenbase.sql.SqlNode} and {@link RexNode}.</p>  *  *<p>If this type of relational expression has some particular planner rules,  * it should implement the<em>public static</em> method  * {@link AbstractRelNode#register}.</p>  *  *<p>When a relational expression comes to be implemented, the system allocates  * a {@link org.eigenbase.relopt.RelImplementor} to manage the process. Every  * implementable relational expression has a {@link RelTraitSet} describing its  * physical attributes. The RelTraitSet always contains a {@link Convention}  * describing how the expression passes data to its consuming  * relational expression, but may contain other traits, including some applied  * externally. Because traits can be applied externally, implementations of  * RelNode should never assume the size or contents of their trait set (beyond  * those traits configured by the RelNode itself).</p>  *  *<p>For each calling-convention, there is a corresponding sub-interface of  * RelNode. For example, {@code net.hydromatic.optiq.rules.java.EnumerableRel}  * has operations to manage the conversion to a graph of  * {@code net.hydromatic.optiq.rules.java.EnumerableConvention}  * calling-convention, and it interacts with a  * {@code EnumerableRelImplementor}.</p>  *  *<p>A relational expression is only required to implement its  * calling-convention's interface when it is actually implemented, that is,  * converted into a plan/program. This means that relational expressions which  * cannot be implemented, such as converters, are not required to implement  * their convention's interface.</p>  *  *<p>Every relational expression must derive from {@link AbstractRelNode}. (Why  * have the<code>RelNode</code> interface, then? We need a root interface,  * because an interface can only derive from an interface.)</p>  */
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
comment|/**    * Returns a list of this relational expression's child expressions.    * (These are scalar expressions, and so do not include the relational    * inputs that are returned by {@link #getInputs}.    *    *<p>The caller should treat the list as unmodifiable; typical    * implementations will return an immutable list. If there are no    * child expressions, returns an empty list, not<code>null</code>.    */
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
function_decl|;
comment|/**    * Return the CallingConvention trait from this RelNode's {@link    * #getTraitSet() trait set}.    *    * @return this RelNode's CallingConvention    */
name|Convention
name|getConvention
parameter_list|()
function_decl|;
comment|/**    * Sets the name of the variable which is to be implicitly set at runtime    * each time a row is returned from this relational expression    *    * @param correlVariable Name of correlating variable    */
name|void
name|setCorrelVariable
parameter_list|(
name|String
name|correlVariable
parameter_list|)
function_decl|;
comment|/**    * Returns the name of the variable which is to be implicitly set at runtime    * each time a row is returned from this relational expression; or null if    * there is no variable.    *    * @return Name of correlating variable, or null    */
name|String
name|getCorrelVariable
parameter_list|()
function_decl|;
comment|/**    * Returns whether the same value will not come out twice. Default value is    *<code>false</code>, derived classes should override.    */
name|boolean
name|isDistinct
parameter_list|()
function_decl|;
comment|/**    * Returns the<code>i</code><sup>th</sup> input relational expression.    *    * @param i Ordinal of input    * @return<code>i</code><sup>th</sup> input    */
name|RelNode
name|getInput
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
comment|/**    * Returns a variable with which to reference the current row of this    * relational expression as a correlating variable. Creates a variable if    * none exists.    */
name|String
name|getOrCreateCorrelVariable
parameter_list|()
function_decl|;
comment|/**    * Returns the sub-query this relational expression belongs to. A sub-query    * determines the scope for correlating variables (see {@link    * #setCorrelVariable(String)}).    *    * @return Sub-query    */
name|RelOptQuery
name|getQuery
parameter_list|()
function_decl|;
comment|/**    * Returns the type of the rows returned by this relational expression.    */
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
comment|/**    * Returns the type of the rows expected for an input. Defaults to {@link    * #getRowType}.    *    * @param ordinalInParent input's 0-based ordinal with respect to this    *                        parent rel    * @return expected row type    */
name|RelDataType
name|getExpectedInputRowType
parameter_list|(
name|int
name|ordinalInParent
parameter_list|)
function_decl|;
comment|/**    * Returns an array of this relational expression's inputs. If there are no    * inputs, returns an empty array, not<code>null</code>.    */
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
function_decl|;
comment|/**    * Returns an estimate of the number of rows this relational expression will    * return.    *    *<p>NOTE jvs 29-Mar-2006: Don't call this method directly. Instead, use    * {@link RelMetadataQuery#getRowCount}, which gives plugins a chance to    * override the rel's default ideas about row count.    */
name|double
name|getRows
parameter_list|()
function_decl|;
comment|/**    * Returns the names of variables which are set in this relational    * expression but also used and therefore not available to parents of this    * relational expression.    *    *<p>By default, returns the empty set. Derived classes may override this    * method.</p>    */
name|Set
argument_list|<
name|String
argument_list|>
name|getVariablesStopped
parameter_list|()
function_decl|;
comment|/**    * Collects variables known to be used by this expression or its    * descendants. By default, no such information is available and must be    * derived by analyzing sub-expressions, but some optimizer implementations    * may insert special expressions which remember such information.    *    * @param variableSet receives variables used    */
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
comment|/**    * Collects variables set by this expression.    *    * @param variableSet receives variables known to be set by    */
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
comment|/**    * Interacts with the {@link RelVisitor} in a {@link    * org.eigenbase.util.Glossary#VISITOR_PATTERN visitor pattern} to traverse    * the tree of relational expressions.    */
name|void
name|childrenAccept
parameter_list|(
name|RelVisitor
name|visitor
parameter_list|)
function_decl|;
comment|/**    * Returns the cost of this plan (not including children). The base    * implementation throws an error; derived classes should override.    *    *<p>NOTE jvs 29-Mar-2006: Don't call this method directly. Instead, use    * {@link RelMetadataQuery#getNonCumulativeCost}, which gives plugins a    * chance to override the rel's default ideas about cost.    */
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
function_decl|;
comment|/**    * Returns a metadata interface.    *    * @param metadataClass Metadata interface    * @param<M> Type of metadata being requested    * @return Metadata object that supplies the desired metadata (never null,    *     although if the information is not present the metadata object may    *     return null from all methods)    */
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
name|M
name|metadata
parameter_list|(
name|Class
argument_list|<
name|M
argument_list|>
name|metadataClass
parameter_list|)
function_decl|;
comment|/**    * Describes the inputs and attributes of this relational expression.    * Each node should call {@code super.explain}, then call the    * {@link RelWriterImpl#input(String, RelNode)}    * and {@link RelWriterImpl#item(String, Object)} methods for each input    * and attribute.    *    * @param pw Plan writer    */
name|void
name|explain
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
function_decl|;
comment|/**    * Receives notification that this expression is about to be registered. The    * implementation of this method must at least register all child    * expressions.    */
name|RelNode
name|onRegister
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
function_decl|;
comment|/**    * Computes the digest, assigns it, and returns it. For planner use only.    */
name|String
name|recomputeDigest
parameter_list|()
function_decl|;
comment|/**    * Registers a correlation variable.    *    * @see #getVariablesStopped    */
name|void
name|registerCorrelVariable
parameter_list|(
name|String
name|correlVariable
parameter_list|)
function_decl|;
comment|/**    * Replaces the<code>ordinalInParent</code><sup>th</sup> input. You must    * override this method if you override {@link #getInputs}.    */
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
comment|/**    * If this relational expression represents an access to a table, returns    * that table, otherwise returns null.    */
name|RelOptTable
name|getTable
parameter_list|()
function_decl|;
comment|/**    * Returns the name of this relational expression's class, sans package    * name, for use in explain. For example, for a<code>    * org.eigenbase.rel.ArrayRel.ArrayReader</code>, this method returns    * "ArrayReader".    */
name|String
name|getRelTypeName
parameter_list|()
function_decl|;
comment|/**    * Returns whether this relational expression is valid.    *    *<p>If assertions are enabled, this method is typically called with<code>    * fail</code> =<code>true</code>, as follows:    *    *<blockquote>    *<pre>assert rel.isValid(true)</pre>    *</blockquote>    *    * This signals that the method can throw an {@link AssertionError} if it is    * not valid.    *    * @param fail Whether to fail if invalid    * @return Whether relational expression is valid    * @throws AssertionError if this relational expression is invalid and    *                        fail=true and assertions are enabled    */
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
function_decl|;
comment|/**    * Returns a description of the physical ordering (or orderings) of this    * relational expression.    *    * @post return != null    */
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
function_decl|;
comment|/**    * Creates a copy of this relational expression, perhaps changing traits and    * inputs.    *    *<p>Sub-classes with other important attributes are encouraged to create    * variants of this method with more parameters.</p>    *    * @param traitSet Trait set    * @param inputs   Inputs    * @return Copy of this relational expression, substituting traits and    * inputs    */
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
comment|/**    * Registers any special rules specific to this kind of relational    * expression.    *    *<p>The planner calls this method this first time that it sees a    * relational expression of this class. The derived class should call {@link    * org.eigenbase.relopt.RelOptPlanner#addRule} for each rule, and then call    * {@code super.register}.</p>    */
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
function_decl|;
comment|/**    * Returns whether the result of this relational expression is uniquely    * identified by this columns with the given ordinals.    *    *<p>For example, if this relational expression is a TableAccessRel to    * T(A, B, C, D) whose key is (A, B), then isKey([0, 1]) yields true,    * and isKey([0]) and isKey([0, 2]) yields false.</p>    *    * @param columns Ordinals of key columns    * @return Whether the given columns are a key or a superset of a key    */
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
function_decl|;
comment|/**    * Accepts a visit from a shuttle.    *    * @param shuttle Shuttle    * @return A copy of this node incorporating changes made by the shuttle to    * this node's children    */
name|RelNode
name|accept
parameter_list|(
name|RelShuttle
name|shuttle
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelNode.java
end_comment

end_unit

