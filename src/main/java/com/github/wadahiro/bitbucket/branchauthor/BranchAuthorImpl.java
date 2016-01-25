package com.github.wadahiro.bitbucket.branchauthor;

import java.sql.SQLException;
import java.util.Date;

import com.atlassian.activeobjects.external.ActiveObjects;

import net.java.ao.DBParam;
import net.java.ao.Query;

/**
 * @author Hiroyuki Wada
 */
public class BranchAuthorImpl {

    private final BranchAuthor branchAuthor;

    public BranchAuthorImpl(BranchAuthor branchAuthor) {
        this.branchAuthor = branchAuthor;
    }

    public static BranchAuthor[] getBbranchAuthors(ActiveObjects ao, Integer repoId) throws SQLException {
        BranchAuthor[] branchAuthors = ao.find(BranchAuthor.class, Query.select().where("REPO_ID = ?", repoId));
        return branchAuthors;
    }

    public static void saveBranchAuthor(ActiveObjects ao, Integer repoId, Date created, String branchRef,
            Integer userId, String userEmail) throws SQLException {

        BranchAuthor[] branchAuthors = ao.find(BranchAuthor.class,
                Query.select().where("REPO_ID = ? and REF = ?", repoId, branchRef));

        if (branchAuthors.length == 0) {
            BranchAuthor branchAuthor = ao.create(BranchAuthor.class, new DBParam("REPO_ID", repoId),
                    new DBParam("CREATED", created), new DBParam("REF", branchRef), new DBParam("USER_ID", userId),
                    new DBParam("USER_EMAIL", userEmail));
            branchAuthor.save();
            return;
        } else {
            // replace when same branch name is reused
            branchAuthors[0].setCreated(created);
            branchAuthors[0].setUserId(userId);
            branchAuthors[0].setUserEmail(userEmail);
            branchAuthors[0].save();
        }
    }

    public static void deleteBranchAuthor(ActiveObjects ao, Integer repoId, String branchRef) throws SQLException {
        BranchAuthor[] branchAuthors = ao.find(BranchAuthor.class,
                Query.select().where("REPO_ID = ? and REF = ?", repoId, branchRef));

        if (branchAuthors.length == 1) {
            ao.delete(branchAuthors[0]);
        }
    }

    public static void deleteAllBranchAuthor(ActiveObjects ao, Integer repoId) throws SQLException {
        BranchAuthor[] branchAuthors = ao.find(BranchAuthor.class, Query.select().where("REPO_ID = ?", repoId));

        for (BranchAuthor branchAuthor : branchAuthors) {
            ao.delete(branchAuthor);
        }
    }
}
