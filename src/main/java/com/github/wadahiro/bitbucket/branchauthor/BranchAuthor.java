package com.github.wadahiro.bitbucket.branchauthor;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Implementation;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

/**
 * @author Hiroyuki Wada
 */
@Table("BRANCH_AUTHOR")
@Preload
@Implementation(BranchAuthorImpl.class)
public interface BranchAuthor extends Entity {
    @NotNull
    @Accessor("REPO_ID")
    public Integer getRepoId();

    @Mutator("REPO_ID")
    public void setRepoId(Integer repoId);

    @NotNull
    @Accessor("CREATED")
    public Date getCreated();

    @Mutator("CREATED")
    public void setCreated(Date created);

    @NotNull
    @Accessor("REF")
    public String getBranchRef();

    @Mutator("REF")
    public void setBranchRef(String branchRef);

    @NotNull
    @Accessor("USER_ID")
    public Integer getUserId();

    @Mutator("USER_ID")
    public void setUserId(Integer userId);

    @NotNull
    @Accessor("USER_EMAIL")
    public String getUserEmail();

    @Mutator("USER_EMAIL")
    public void setUserEmail(String userEmail);
}
