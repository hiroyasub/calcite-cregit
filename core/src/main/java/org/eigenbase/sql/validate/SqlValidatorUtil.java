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
name|nio
operator|.
name|charset
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
name|sql
operator|.
name|type
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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
operator|.
name|Prepare
import|;
end_import

begin_comment
comment|/**  * Utility methods related to validation.  *  * @author jhyde  * @version $Id$  * @since Mar 25, 2003  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorUtil
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Converts a {@link SqlValidatorScope} into a {@link RelOptTable}. This is      * only possible if the scope represents an identifier, such as "sales.emp".      * Otherwise, returns null.      *      * @param namespace Namespace      * @param catalogReader Schema      * @param datasetName Name of sample dataset to substitute, or null to use      * the regular table      * @param usedDataset Output parameter which is set to true if a sample      * dataset is found; may be null      */
specifier|public
specifier|static
name|RelOptTable
name|getRelOptTable
parameter_list|(
name|SqlValidatorNamespace
name|namespace
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
parameter_list|,
name|String
name|datasetName
parameter_list|,
name|boolean
index|[]
name|usedDataset
parameter_list|)
block|{
if|if
condition|(
name|namespace
operator|.
name|isWrapperFor
argument_list|(
name|IdentifierNamespace
operator|.
name|class
argument_list|)
condition|)
block|{
name|IdentifierNamespace
name|identifierNamespace
init|=
name|namespace
operator|.
name|unwrap
argument_list|(
name|IdentifierNamespace
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|identifierNamespace
operator|.
name|getId
argument_list|()
operator|.
name|names
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|datasetName
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|catalogReader
operator|instanceof
name|RelOptSchemaWithSampling
operator|)
condition|)
block|{
return|return
operator|(
operator|(
name|RelOptSchemaWithSampling
operator|)
name|catalogReader
operator|)
operator|.
name|getTableForMember
argument_list|(
name|names
argument_list|,
name|datasetName
argument_list|,
name|usedDataset
argument_list|)
return|;
block|}
else|else
block|{
comment|// Schema does not support substitution. Ignore the dataset,
comment|// if any.
return|return
name|catalogReader
operator|.
name|getTableForMember
argument_list|(
name|names
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Looks up a field with a given name and if found returns its type.      *      * @param rowType Row type      * @param columnName Field name      *      * @return Field's type, or null if not found      */
specifier|static
name|RelDataType
name|lookupFieldType
parameter_list|(
specifier|final
name|RelDataType
name|rowType
parameter_list|,
name|String
name|columnName
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rowType
operator|.
name|getFieldList
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
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
return|return
name|field
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
comment|// If record type is flagged as having "any field you ask for",
comment|// return a type. (TODO: Better way to mark accommodating types.)
name|RelDataTypeField
name|extra
init|=
name|rowType
operator|.
name|getField
argument_list|(
literal|"_extra"
argument_list|)
decl_stmt|;
if|if
condition|(
name|extra
operator|!=
literal|null
condition|)
block|{
return|return
name|extra
operator|.
name|getType
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Looks up a field with a given name, returning null if not found.      *      * @param rowType Row type      * @param columnName Field name      *      * @return Field, or null if not found      */
specifier|public
specifier|static
name|RelDataTypeField
name|lookupField
parameter_list|(
specifier|final
name|RelDataType
name|rowType
parameter_list|,
name|String
name|columnName
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rowType
operator|.
name|getFieldList
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
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
return|return
name|field
return|;
block|}
block|}
comment|// If record type is flagged as having "any field you ask for",
comment|// return a type. (TODO: Better way to mark accommodating types.)
name|RelDataTypeField
name|extra
init|=
name|rowType
operator|.
name|getField
argument_list|(
literal|"_extra"
argument_list|)
decl_stmt|;
if|if
condition|(
name|extra
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|columnName
argument_list|,
operator|-
literal|1
argument_list|,
name|extra
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|void
name|checkCharsetAndCollateConsistentIfCharType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
comment|//(every charset must have a default collation)
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|Charset
name|strCharset
init|=
name|type
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|Charset
name|colCharset
init|=
name|type
operator|.
name|getCollation
argument_list|()
operator|.
name|getCharset
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|null
operator|!=
name|strCharset
operator|)
assert|;
assert|assert
operator|(
literal|null
operator|!=
name|colCharset
operator|)
assert|;
if|if
condition|(
operator|!
name|strCharset
operator|.
name|equals
argument_list|(
name|colCharset
argument_list|)
condition|)
block|{
if|if
condition|(
literal|false
condition|)
block|{
comment|// todo: enable this checking when we have a charset to
comment|//   collation mapping
throw|throw
operator|new
name|Error
argument_list|(
name|type
operator|.
name|toString
argument_list|()
operator|+
literal|" was found to have charset '"
operator|+
name|strCharset
operator|.
name|name
argument_list|()
operator|+
literal|"' and a mismatched collation charset '"
operator|+
name|colCharset
operator|.
name|name
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**      * Converts an expression "expr" into "expr AS alias".      */
specifier|public
specifier|static
name|SqlNode
name|addAlias
parameter_list|(
name|SqlNode
name|expr
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
specifier|final
name|SqlParserPos
name|pos
init|=
name|expr
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
specifier|final
name|SqlIdentifier
name|id
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|alias
argument_list|,
name|pos
argument_list|)
decl_stmt|;
return|return
name|SqlStdOperatorTable
operator|.
name|asOperator
operator|.
name|createCall
argument_list|(
name|pos
argument_list|,
name|expr
argument_list|,
name|id
argument_list|)
return|;
block|}
comment|/**      * Derives an alias for a node. If it cannot derive an alias, returns null.      *      *<p>This method doesn't try very hard. It doesn't invent mangled aliases,      * and doesn't even recognize an AS clause. (See {@link #getAlias(SqlNode,      * int)} for that.) It just takes the last part of an identifier.      */
specifier|public
specifier|static
name|String
name|getAlias
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|SqlIdentifier
condition|)
block|{
name|String
index|[]
name|names
init|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|node
operator|)
operator|.
name|names
decl_stmt|;
return|return
name|names
index|[
name|names
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Derives an alias for a node, and invents a mangled identifier if it      * cannot.      *      *<p>Examples:      *      *<ul>      *<li>Alias: "1 + 2 as foo" yields "foo"      *<li>Identifier: "foo.bar.baz" yields "baz"      *<li>Anything else yields "expr$<i>ordinal</i>"      *</ul>      *      * @return An alias, if one can be derived; or a synthetic alias      * "expr$<i>ordinal</i>" if ordinal>= 0; otherwise null      */
specifier|public
specifier|static
name|String
name|getAlias
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|AS
case|:
comment|// E.g. "1 + 2 as foo" --> "foo"
return|return
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
index|[
literal|1
index|]
operator|.
name|toString
argument_list|()
return|;
case|case
name|OVER
case|:
comment|// E.g. "bids over w" --> "bids"
return|return
name|getAlias
argument_list|(
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|ordinal
argument_list|)
return|;
case|case
name|IDENTIFIER
case|:
comment|// E.g. "foo.bar" --> "bar"
specifier|final
name|String
index|[]
name|names
init|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|node
operator|)
operator|.
name|names
decl_stmt|;
return|return
name|names
index|[
name|names
operator|.
name|length
operator|-
literal|1
index|]
return|;
default|default:
if|if
condition|(
name|ordinal
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|SqlUtil
operator|.
name|deriveAliasFromOrdinal
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * Makes a name distinct from other names which have already been used, adds      * it to the list, and returns it.      *      * @param name Suggested name, may not be unique      * @param nameList Collection of names already used      * @param suggester Base for name when input name is null      * @return Unique name      */
specifier|public
specifier|static
name|String
name|uniquify
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nameList
parameter_list|,
name|Suggester
name|suggester
parameter_list|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nameList
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|name
return|;
block|}
block|}
specifier|final
name|String
name|originalName
init|=
name|name
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
condition|;
name|j
operator|++
control|)
block|{
name|name
operator|=
name|suggester
operator|.
name|apply
argument_list|(
name|originalName
argument_list|,
name|j
argument_list|,
name|nameList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|nameList
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|name
return|;
block|}
block|}
block|}
comment|/**      * Factory method for {@link SqlValidator}.      */
specifier|public
specifier|static
name|SqlValidatorWithHints
name|newValidator
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|new
name|SqlValidatorImpl
argument_list|(
name|opTab
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|SqlConformance
operator|.
name|Default
argument_list|)
return|;
block|}
comment|/**      * Makes sure that the names in a list are unique.      *      *<p>Does not modify the input list. Returns the input list if the strings      * are unique, otherwise allocates a new list.      *      * @param nameList List of strings      * @return List of unique strings      */
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|uniquify
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|nameList
parameter_list|)
block|{
return|return
name|uniquify
argument_list|(
name|nameList
argument_list|,
name|EXPR_SUGGESTER
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|uniquify
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|nameList
parameter_list|,
name|Suggester
name|suggester
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|used
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|changeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|nameList
control|)
block|{
name|String
name|uniqueName
init|=
name|uniquify
argument_list|(
name|name
argument_list|,
name|used
argument_list|,
name|suggester
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|uniqueName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
operator|++
name|changeCount
expr_stmt|;
block|}
block|}
return|return
name|changeCount
operator|==
literal|0
condition|?
name|nameList
else|:
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|used
argument_list|)
return|;
block|}
comment|/**      * Resolves a multi-part identifier such as "SCHEMA.EMP.EMPNO" to a      * namespace. The returned namespace may represent a schema, table, column,      * etc.      *      * @pre names.size()> 0      * @post return != null      */
specifier|public
specifier|static
name|SqlValidatorNamespace
name|lookup
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|Util
operator|.
name|pre
argument_list|(
name|names
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|,
literal|"names.size()> 0"
argument_list|)
expr_stmt|;
name|SqlValidatorNamespace
name|namespace
init|=
literal|null
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
name|names
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|namespace
operator|=
name|scope
operator|.
name|resolve
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|namespace
operator|=
name|namespace
operator|.
name|lookupChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|Util
operator|.
name|permAssert
argument_list|(
name|namespace
operator|!=
literal|null
argument_list|,
literal|"post: namespace != null"
argument_list|)
expr_stmt|;
return|return
name|namespace
return|;
block|}
specifier|public
specifier|static
name|void
name|getSchemaObjectMonikers
parameter_list|(
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|hints
parameter_list|)
block|{
comment|// Assume that the last name is 'dummy' or similar.
name|List
argument_list|<
name|String
argument_list|>
name|subNames
init|=
name|names
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|names
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|hints
operator|.
name|addAll
argument_list|(
name|catalogReader
operator|.
name|getAllSchemaObjectNames
argument_list|(
name|subNames
argument_list|)
argument_list|)
expr_stmt|;
comment|// If the name has length 0, try prepending the name of the default
comment|// schema. So, the empty name would yield a list of tables in the
comment|// default schema, as well as a list of schemas from the above code.
if|if
condition|(
name|subNames
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|hints
operator|.
name|addAll
argument_list|(
name|catalogReader
operator|.
name|getAllSchemaObjectNames
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|catalogReader
operator|.
name|getSchemaName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|SelectScope
name|getEnclosingSelectScope
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
while|while
condition|(
name|scope
operator|instanceof
name|DelegatingScope
condition|)
block|{
if|if
condition|(
name|scope
operator|instanceof
name|SelectScope
condition|)
block|{
return|return
operator|(
name|SelectScope
operator|)
name|scope
return|;
block|}
name|scope
operator|=
operator|(
operator|(
name|DelegatingScope
operator|)
name|scope
operator|)
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Derives the list of column names suitable for NATURAL JOIN. These are the      * columns that occur exactly once on each side of the join.      *      * @param leftRowType Row type of left input to the join      * @param rightRowType Row type of right input to the join      *      * @return List of columns that occur once on each side      */
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|deriveNaturalJoinColumnList
parameter_list|(
name|RelDataType
name|leftRowType
parameter_list|,
name|RelDataType
name|rightRowType
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|naturalColumnNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|leftNames
init|=
name|leftRowType
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|rightNames
init|=
name|rightRowType
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|leftNames
control|)
block|{
if|if
condition|(
operator|(
name|Collections
operator|.
name|frequency
argument_list|(
name|leftNames
argument_list|,
name|name
argument_list|)
operator|==
literal|1
operator|)
operator|&&
operator|(
name|Collections
operator|.
name|frequency
argument_list|(
name|rightNames
argument_list|,
name|name
argument_list|)
operator|==
literal|1
operator|)
condition|)
block|{
name|naturalColumnNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|naturalColumnNames
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Walks over an expression, copying every node, and fully-qualifying every      * identifier.      */
specifier|public
specifier|static
class|class
name|DeepCopier
extends|extends
name|SqlScopedShuttle
block|{
name|DeepCopier
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|super
argument_list|(
name|scope
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlNodeList
name|list
parameter_list|)
block|{
name|SqlNodeList
name|copy
init|=
operator|new
name|SqlNodeList
argument_list|(
name|list
operator|.
name|getParserPosition
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|node
range|:
name|list
control|)
block|{
name|copy
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
name|copy
return|;
block|}
comment|// Override to copy all arguments regardless of whether visitor changes
comment|// them.
specifier|protected
name|SqlNode
name|visitScoped
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
name|ArgHandler
argument_list|<
name|SqlNode
argument_list|>
name|argHandler
init|=
operator|new
name|CallCopyingArgHandler
argument_list|(
name|call
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|acceptCall
argument_list|(
name|this
argument_list|,
name|call
argument_list|,
literal|false
argument_list|,
name|argHandler
argument_list|)
expr_stmt|;
return|return
name|argHandler
operator|.
name|result
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
block|{
return|return
operator|(
name|SqlNode
operator|)
name|literal
operator|.
name|clone
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
block|{
return|return
name|getScope
argument_list|()
operator|.
name|fullyQualify
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlDataTypeSpec
name|type
parameter_list|)
block|{
return|return
operator|(
name|SqlNode
operator|)
name|type
operator|.
name|clone
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlDynamicParam
name|param
parameter_list|)
block|{
return|return
operator|(
name|SqlNode
operator|)
name|param
operator|.
name|clone
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
block|{
return|return
operator|(
name|SqlNode
operator|)
name|intervalQualifier
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
interface|interface
name|Suggester
block|{
name|String
name|apply
parameter_list|(
name|String
name|original
parameter_list|,
name|int
name|attempt
parameter_list|,
name|int
name|size
parameter_list|)
function_decl|;
block|}
specifier|public
specifier|static
specifier|final
name|Suggester
name|EXPR_SUGGESTER
init|=
operator|new
name|Suggester
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|original
parameter_list|,
name|int
name|attempt
parameter_list|,
name|int
name|size
parameter_list|)
block|{
return|return
name|Util
operator|.
name|first
argument_list|(
name|original
argument_list|,
literal|"EXPR$"
argument_list|)
operator|+
name|attempt
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Suggester
name|F_SUGGESTER
init|=
operator|new
name|Suggester
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|original
parameter_list|,
name|int
name|attempt
parameter_list|,
name|int
name|size
parameter_list|)
block|{
return|return
name|Util
operator|.
name|first
argument_list|(
name|original
argument_list|,
literal|"$f"
argument_list|)
operator|+
name|size
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

begin_comment
comment|// End SqlValidatorUtil.java
end_comment

end_unit

