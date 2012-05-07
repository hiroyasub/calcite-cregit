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
name|relopt
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
name|*
import|;
end_import

begin_comment
comment|// TODO jvs 9-Mar-2006:  move this class to another package; it
end_comment

begin_comment
comment|// doesn't really belong here.  Also, use a proper class for table
end_comment

begin_comment
comment|// names instead of List<String>.
end_comment

begin_comment
comment|/**  *<code>TableAccessMap</code> represents the tables accessed by a query plan,  * with READ/WRITE information.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|TableAccessMap
block|{
comment|//~ Enums ------------------------------------------------------------------
specifier|public
specifier|static
enum|enum
name|Mode
block|{
comment|/**          * Table is not accessed at all.          */
name|NO_ACCESS
block|,
comment|/**          * Table is accessed for read only.          */
name|READ_ACCESS
block|,
comment|/**          * Table is accessed for write only.          */
name|WRITE_ACCESS
block|,
comment|/**          * Table is accessed for both read and write.          */
name|READWRITE_ACCESS
block|}
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Map
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Mode
argument_list|>
name|accessMap
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Constructs a permanently empty TableAccessMap.      */
specifier|public
name|TableAccessMap
parameter_list|()
block|{
name|accessMap
operator|=
name|Collections
operator|.
name|EMPTY_MAP
expr_stmt|;
block|}
comment|/**      * Constructs a TableAccessMap for all tables accessed by a RelNode and its      * descendants.      *      * @param rel the RelNode for which to build the map      */
specifier|public
name|TableAccessMap
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
comment|// NOTE jvs 9-Mar-2006: This method must NOT retain a reference to the
comment|// input rel, because we use it for cached statements, and we don't
comment|// want to retain any rel references after preparation completes.
name|accessMap
operator|=
operator|new
name|HashMap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Mode
argument_list|>
argument_list|()
expr_stmt|;
name|RelOptUtil
operator|.
name|go
argument_list|(
operator|new
name|TableRelVisitor
argument_list|()
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a TableAccessMap for a single table      *      * @param table fully qualified name of the table, represented as a list      * @param mode access mode for the table      */
specifier|public
name|TableAccessMap
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|table
parameter_list|,
name|Mode
name|mode
parameter_list|)
block|{
name|accessMap
operator|=
operator|new
name|HashMap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Mode
argument_list|>
argument_list|()
expr_stmt|;
name|accessMap
operator|.
name|put
argument_list|(
name|table
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * @return set of qualified names for all tables accessed      */
specifier|public
name|Set
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getTablesAccessed
parameter_list|()
block|{
return|return
name|accessMap
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**      * Determines whether a table is accessed at all.      *      * @param tableName qualified name of the table of interest      *      * @return true if table is accessed      */
specifier|public
name|boolean
name|isTableAccessed
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tableName
parameter_list|)
block|{
return|return
name|accessMap
operator|.
name|containsKey
argument_list|(
name|tableName
argument_list|)
return|;
block|}
comment|/**      * Determines whether a table is accessed for read.      *      * @param tableName qualified name of the table of interest      *      * @return true if table is accessed for read      */
specifier|public
name|boolean
name|isTableAccessedForRead
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tableName
parameter_list|)
block|{
name|Mode
name|mode
init|=
name|getTableAccessMode
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
return|return
operator|(
name|mode
operator|==
name|Mode
operator|.
name|READ_ACCESS
operator|)
operator|||
operator|(
name|mode
operator|==
name|Mode
operator|.
name|READWRITE_ACCESS
operator|)
return|;
block|}
comment|/**      * Determines whether a table is accessed for write.      *      * @param tableName qualified name of the table of interest      *      * @return true if table is accessed for write      */
specifier|public
name|boolean
name|isTableAccessedForWrite
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tableName
parameter_list|)
block|{
name|Mode
name|mode
init|=
name|getTableAccessMode
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
return|return
operator|(
name|mode
operator|==
name|Mode
operator|.
name|WRITE_ACCESS
operator|)
operator|||
operator|(
name|mode
operator|==
name|Mode
operator|.
name|READWRITE_ACCESS
operator|)
return|;
block|}
comment|/**      * Determines the access mode of a table.      *      * @param tableName qualified name of the table of interest      *      * @return access mode      */
specifier|public
name|Mode
name|getTableAccessMode
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tableName
parameter_list|)
block|{
name|Mode
name|mode
init|=
name|accessMap
operator|.
name|get
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|==
literal|null
condition|)
block|{
return|return
name|Mode
operator|.
name|NO_ACCESS
return|;
block|}
return|return
name|mode
return|;
block|}
comment|/**      * Constructs a qualified name for an optimizer table reference.      *      * @param table table of interest      *      * @return qualified name      */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
class|class
name|TableRelVisitor
extends|extends
name|RelVisitor
block|{
comment|// implement RelVisitor
specifier|public
name|void
name|visit
parameter_list|(
name|RelNode
name|p
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|parent
parameter_list|)
block|{
name|super
operator|.
name|visit
argument_list|(
name|p
argument_list|,
name|ordinal
argument_list|,
name|parent
argument_list|)
expr_stmt|;
name|RelOptTable
name|table
init|=
name|p
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|table
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Mode
name|newAccess
decl_stmt|;
comment|// FIXME jvs 1-Feb-2006:  Don't rely on object type here;
comment|// eventually someone is going to write a rule which transforms
comment|// to something which doesn't inherit TableModificationRelBase,
comment|// and this will break.  Need to make this explicit in
comment|// the RelNode interface.
if|if
condition|(
name|p
operator|instanceof
name|TableModificationRelBase
condition|)
block|{
name|newAccess
operator|=
name|Mode
operator|.
name|WRITE_ACCESS
expr_stmt|;
block|}
else|else
block|{
name|newAccess
operator|=
name|Mode
operator|.
name|READ_ACCESS
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|key
init|=
name|getQualifiedName
argument_list|(
name|table
argument_list|)
decl_stmt|;
name|Mode
name|oldAccess
init|=
name|accessMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|oldAccess
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|oldAccess
operator|!=
name|newAccess
operator|)
condition|)
block|{
name|newAccess
operator|=
name|Mode
operator|.
name|READWRITE_ACCESS
expr_stmt|;
block|}
name|accessMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|newAccess
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TableAccessMap.java
end_comment

end_unit

