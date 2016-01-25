package com.github.wadahiro.bitbucket.branchauthor;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.event.repository.AbstractRepositoryRefsChangedEvent;
import com.atlassian.bitbucket.repository.RefChange;
import com.atlassian.bitbucket.repository.RefChangeType;
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

    /**
     * We must use AbstractRepositoryRefsChangedEvent for handling all ADD/DELETE branch events.
     * See https://jira.atlassian.com/browse/BSERV-4269.
     * Using AbstractRepositoryRefsChangedEvent, we can support following operations.
     * 
     * Branch creation from GUI
     * Branch deletion from GUI
     * Branch creation from Git client (git push origin <branch_name>)
     * Branch deletion from Git client (git push --delete origin <branch_name>)
     * Branch deletion by merged pull request(use source branch deletion)
     * Branch creation by fork syncing
     * Branch deletion by fork syncing
     * 
     * @param event
     */
    @EventListener
    public void onRefsChanged(AbstractRepositoryRefsChangedEvent event) {
        Repository repo = event.getRepository();
        ApplicationUser user = event.getUser();

        Integer repoId = repo.getId();
        Integer userId = user.getId();
        String userEmail = user.getEmailAddress();

        Collection<RefChange> refChanges = event.getRefChanges();

        for (RefChange refChange : refChanges) {
            RefChangeType type = refChange.getType();

            if (type == RefChangeType.ADD) {
                Date created = event.getDate();
                String branchRef = refChange.getRef().getId();
                event.getUser();

                log.info(
                        "AbstractRepositoryRefsChangedEvent: Save branch author. repoId={} created={}, branchRef={}, userId={}, userEmail={}",
                        repoId, created, branchRef, userId, userEmail);

                createBranchAuthor(repoId, created, branchRef, userId, userEmail);

            } else if (type == RefChangeType.DELETE) {
                String branchRef = refChange.getRef().getId();

                log.info("AbstractRepositoryRefsChangedEvent: Delete branch author. repoId={}, branchRef={}", repoId,
                        branchRef);

                deleteBranchAuthor(repoId, branchRef);
            }
        }
    }

    private void createBranchAuthor(Integer repoId, Date created, String branchRef, Integer userId, String userEmail) {
        try {
            BranchAuthorImpl.saveBranchAuthor(activeObjects, repoId, created, branchRef, userId, userEmail);
        } catch (SQLException e) {
            log.error("Saving branch author error. repoid={}, branchRef={}", repoId, branchRef, e);
        }
    }

    private void deleteBranchAuthor(Integer repoId, String branchRef) {
        try {
            BranchAuthorImpl.deleteBranchAuthor(activeObjects, repoId, branchRef);
        } catch (SQLException e) {
            log.error("Deleting branch author error. repoid={}, branchRef={}", repoId, branchRef, e);
        }
    }
}
