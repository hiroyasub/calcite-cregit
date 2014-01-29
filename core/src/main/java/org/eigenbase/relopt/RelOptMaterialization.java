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
name|rules
operator|.
name|PullUpProjectsAboveJoinRule
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
name|hep
operator|.
name|HepPlanner
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
name|hep
operator|.
name|HepProgram
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
name|SqlExplainLevel
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
name|Util
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
name|mapping
operator|.
name|Mappings
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
name|Table
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
name|impl
operator|.
name|StarTable
import|;
end_import

begin_comment
comment|/**  * Records that a particular query is materialized by a particular table.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptMaterialization
block|{
specifier|public
specifier|final
name|RelNode
name|tableRel
decl_stmt|;
specifier|public
specifier|final
name|RelOptTable
name|starRelOptTable
decl_stmt|;
specifier|public
specifier|final
name|StarTable
name|starTable
decl_stmt|;
specifier|public
specifier|final
name|RelOptTable
name|table
decl_stmt|;
specifier|public
specifier|final
name|RelNode
name|queryRel
decl_stmt|;
comment|/**    * Creates a RelOptMaterialization.    */
specifier|public
name|RelOptMaterialization
parameter_list|(
name|RelNode
name|tableRel
parameter_list|,
name|RelNode
name|queryRel
parameter_list|,
name|RelOptTable
name|starRelOptTable
parameter_list|)
block|{
name|this
operator|.
name|tableRel
operator|=
name|tableRel
expr_stmt|;
name|this
operator|.
name|starRelOptTable
operator|=
name|starRelOptTable
expr_stmt|;
if|if
condition|(
name|starRelOptTable
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|starTable
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|starTable
operator|=
name|starRelOptTable
operator|.
name|unwrap
argument_list|(
name|StarTable
operator|.
name|class
argument_list|)
expr_stmt|;
assert|assert
name|starTable
operator|!=
literal|null
assert|;
block|}
name|this
operator|.
name|table
operator|=
name|tableRel
operator|.
name|getTable
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryRel
operator|=
name|queryRel
expr_stmt|;
block|}
comment|/**    * Converts a relational expression to one that uses a    * {@link net.hydromatic.optiq.impl.StarTable}.    * The relational expression is already in leaf-join-form, per    * {@link #toLeafJoinForm(org.eigenbase.rel.RelNode)}.    */
specifier|public
specifier|static
name|RelNode
name|tryUseStar
parameter_list|(
name|RelNode
name|rel
parameter_list|,
specifier|final
name|RelOptTable
name|starRelOptTable
parameter_list|)
block|{
specifier|final
name|StarTable
name|starTable
init|=
name|starRelOptTable
operator|.
name|unwrap
argument_list|(
name|StarTable
operator|.
name|class
argument_list|)
decl_stmt|;
assert|assert
name|starTable
operator|!=
literal|null
assert|;
return|return
name|rel
operator|.
name|accept
argument_list|(
operator|new
name|RelShuttleImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableAccessRelBase
name|scan
parameter_list|)
block|{
name|RelOptTable
name|relOptTable
init|=
name|scan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|relOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|.
name|equals
argument_list|(
name|starTable
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|starRelOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|relOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|CalcRel
operator|.
name|createProject
argument_list|(
operator|new
name|TableAccessRel
argument_list|(
name|scan
operator|.
name|getCluster
argument_list|()
argument_list|,
name|starRelOptTable
argument_list|)
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|mapping
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|scan
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|JoinRel
name|join
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|RelNode
name|rel
init|=
name|super
operator|.
name|visit
argument_list|(
name|join
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel
operator|==
name|join
operator|||
operator|!
operator|(
name|rel
operator|instanceof
name|JoinRel
operator|)
condition|)
block|{
return|return
name|rel
return|;
block|}
name|join
operator|=
operator|(
name|JoinRel
operator|)
name|rel
expr_stmt|;
specifier|final
name|RelNode
name|left
init|=
name|join
operator|.
name|getLeft
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|right
init|=
name|join
operator|.
name|getRight
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|left
operator|instanceof
name|TableAccessRelBase
operator|&&
name|right
operator|instanceof
name|TableAccessRelBase
condition|)
block|{
name|match
argument_list|(
name|left
argument_list|,
literal|null
argument_list|,
name|right
argument_list|,
literal|null
argument_list|,
name|join
operator|.
name|getCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isProjectedTable
argument_list|(
name|left
argument_list|)
operator|&&
name|right
operator|instanceof
name|TableAccessRelBase
condition|)
block|{
specifier|final
name|ProjectRel
name|leftProject
init|=
operator|(
name|ProjectRel
operator|)
name|left
decl_stmt|;
name|match
argument_list|(
name|leftProject
operator|.
name|getChild
argument_list|()
argument_list|,
name|leftProject
operator|.
name|getMapping
argument_list|()
argument_list|,
name|right
argument_list|,
literal|null
argument_list|,
name|join
operator|.
name|getCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|left
operator|instanceof
name|TableAccessRelBase
operator|&&
name|isProjectedTable
argument_list|(
name|right
argument_list|)
condition|)
block|{
specifier|final
name|ProjectRel
name|rightProject
init|=
operator|(
name|ProjectRel
operator|)
name|right
decl_stmt|;
name|match
argument_list|(
name|left
argument_list|,
literal|null
argument_list|,
name|rightProject
operator|.
name|getChild
argument_list|()
argument_list|,
name|rightProject
operator|.
name|getMapping
argument_list|()
argument_list|,
name|join
operator|.
name|getCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isProjectedTable
argument_list|(
name|left
argument_list|)
operator|&&
name|isProjectedTable
argument_list|(
name|right
argument_list|)
condition|)
block|{
specifier|final
name|ProjectRel
name|leftProject
init|=
operator|(
name|ProjectRel
operator|)
name|left
decl_stmt|;
specifier|final
name|ProjectRel
name|rightProject
init|=
operator|(
name|ProjectRel
operator|)
name|right
decl_stmt|;
name|match
argument_list|(
name|leftProject
operator|.
name|getChild
argument_list|()
argument_list|,
name|leftProject
operator|.
name|getMapping
argument_list|()
argument_list|,
name|rightProject
operator|.
name|getChild
argument_list|()
argument_list|,
name|rightProject
operator|.
name|getMapping
argument_list|()
argument_list|,
name|join
operator|.
name|getCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
return|return
operator|(
name|RelNode
operator|)
name|e
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
block|}
specifier|private
name|boolean
name|isProjectedTable
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|rel
operator|instanceof
name|ProjectRel
operator|&&
operator|(
operator|(
name|ProjectRel
operator|)
name|rel
operator|)
operator|.
name|isMapping
argument_list|()
operator|&&
operator|(
operator|(
name|ProjectRel
operator|)
name|rel
operator|)
operator|.
name|getChild
argument_list|()
operator|instanceof
name|TableAccessRelBase
return|;
block|}
comment|/** Throws a {@link org.eigenbase.util.Util.FoundOne} containing a            * {@link org.eigenbase.rel.TableAccessRel} on success.            * (Yes, an exception for normal operation.) */
specifier|private
name|void
name|match
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|leftMapping
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|rightMapping
parameter_list|,
name|RelOptCluster
name|cluster
parameter_list|)
block|{
if|if
condition|(
name|leftMapping
operator|==
literal|null
condition|)
block|{
name|leftMapping
operator|=
name|Mappings
operator|.
name|createIdentity
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rightMapping
operator|==
literal|null
condition|)
block|{
name|rightMapping
operator|=
name|Mappings
operator|.
name|createIdentity
argument_list|(
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelOptTable
name|leftRelOptTable
init|=
name|left
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|leftTable
init|=
name|leftRelOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelOptTable
name|rightRelOptTable
init|=
name|right
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|rightTable
init|=
name|rightRelOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftTable
operator|instanceof
name|StarTable
operator|&&
operator|(
operator|(
name|StarTable
operator|)
name|leftTable
operator|)
operator|.
name|tables
operator|.
name|contains
argument_list|(
name|rightTable
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"left: "
operator|+
name|leftMapping
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"right: "
operator|+
name|rightMapping
argument_list|)
expr_stmt|;
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|merge
argument_list|(
name|leftMapping
argument_list|,
name|Mappings
operator|.
name|offset
argument_list|(
name|rightMapping
argument_list|,
operator|(
operator|(
name|StarTable
operator|)
name|leftTable
operator|)
operator|.
name|columnOffset
argument_list|(
name|rightTable
argument_list|)
argument_list|,
name|leftRelOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|CalcRel
operator|.
name|createProject
argument_list|(
operator|new
name|TableAccessRel
argument_list|(
name|cluster
argument_list|,
name|leftRelOptTable
argument_list|)
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|mapping
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|rightTable
operator|instanceof
name|StarTable
operator|&&
operator|(
operator|(
name|StarTable
operator|)
name|rightTable
operator|)
operator|.
name|tables
operator|.
name|contains
argument_list|(
name|leftTable
argument_list|)
condition|)
block|{
assert|assert
literal|false
assert|;
comment|// TODO:
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|append
argument_list|(
name|leftMapping
argument_list|,
name|rightMapping
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|CalcRel
operator|.
name|createProject
argument_list|(
operator|new
name|TableAccessRel
argument_list|(
name|cluster
argument_list|,
name|rightRelOptTable
argument_list|)
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|mapping
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * Converts a relational expression to a form where    * {@link org.eigenbase.rel.JoinRel}s are    * as close to leaves as possible.    */
specifier|public
specifier|static
name|RelNode
name|toLeafJoinForm
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|HepProgram
name|program
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
operator|.
name|addRuleInstance
argument_list|(
name|PullUpProjectsAboveJoinRule
operator|.
name|RIGHT_PROJECT
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|PullUpProjectsAboveJoinRule
operator|.
name|LEFT_PROJECT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|program
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|"before"
argument_list|,
name|rel
argument_list|,
literal|false
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|rel2
init|=
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|"after"
argument_list|,
name|rel2
argument_list|,
literal|false
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rel2
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptMaterialization.java
end_comment

end_unit

