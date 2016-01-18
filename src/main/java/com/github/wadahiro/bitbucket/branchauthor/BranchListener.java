package com.github.wadahiro.bitbucket.branchauthor;

import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.event.branch.BranchCreatedEvent;
import com.atlassian.bitbucket.event.branch.BranchDeletedEvent;
import com.atlassian.bitbucket.repository.Branch;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.event.api.EventListener;

/**
 * @author Hiroyuki Wada
 */
public class BranchListener {

    private static final Logger log = LoggerFactory.getLogger(BranchListener.class);

    private final ActiveObjects activeObjects;

    public BranchListener(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    @EventListener
    public void onBranchCreated(BranchCreatedEvent event) {
        Repository repo = event.getRepository();
        ApplicationUser user = event.getUser();
        Branch branch = event.getBranch();

        Integer repoId = repo.getId();
        Date created = event.getDate();
        String branchRef = branch.getId();
        Integer userId = user.getId();
        String userEmail = user.getEmailAddress();

        log.info("Save branch author. repoId={} created={}, branchRef={}, userId={}, userEmail={}", repoId, created,
                branchRef, userId, userEmail);

        try {
            BranchAuthorImpl.saveBranchAuthor(activeObjects, repoId, created, branchRef, userId, userEmail);
        } catch (SQLException e) {
            log.error("Saving branch author error. repoid={}, branchRef={}", repoId, branchRef, e);
        }
    }

    @EventListener
    public void onBranchDeleted(BranchDeletedEvent event) {
        Repository repo = event.getRepository();
        Branch branch = event.getBranch();

        Integer repoId = repo.getId();
        String branchRef = branch.getId();

        log.info("Delete branch author. repoId={}, branchRef={}", repoId, branchRef);

        try {
            BranchAuthorImpl.deleteBranchAuthor(activeObjects, repoId, branchRef);
        } catch (SQLException e) {
            log.error("Deleting branch author error. repoid={}, branchRef={}", repoId, branchRef, e);
        }
    }
}
