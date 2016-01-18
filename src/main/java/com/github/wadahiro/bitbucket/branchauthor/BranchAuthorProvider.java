package com.github.wadahiro.bitbucket.branchauthor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.stash.repository.Ref;
import com.atlassian.stash.repository.RefMetadataContext;
import com.atlassian.stash.repository.RefMetadataProvider;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.atlassian.stash.util.DateFormatter;

/**
 * @author Hiroyuki Wada
 */
public class BranchAuthorProvider implements RefMetadataProvider<Map<String, Object>> {

    private static final Logger log = LoggerFactory.getLogger(BranchAuthorProvider.class);

    private final UserService userService;
    private final ActiveObjects activeObjects;
    private final DateFormatter dateFormatter;

    public BranchAuthorProvider(UserService userService, ActiveObjects activeObjects, DateFormatter dateFormatter) {
        this.userService = userService;
        this.activeObjects = activeObjects;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public Map<Ref, Map<String, Object>> getMetadata(RefMetadataContext context) {
        Integer repoId = context.getRepository().getId();

        BranchAuthor[] branchAuthors;
        try {
            branchAuthors = BranchAuthorImpl.getBbranchAuthors(activeObjects, repoId);

            Map<Ref, Map<String, Object>> authors = new HashMap<Ref, Map<String, Object>>(context.getRefs().size());
            for (Ref ref : context.getRefs()) {
                Map<String, Object> author = new HashMap<String, Object>();
                for (BranchAuthor branchAuthor : branchAuthors) {
                    if (branchAuthor.getBranchRef().equals(ref.getId())) {
                        StashUser user = userService.getUserById(branchAuthor.getUserId());
                        if (user == null) {
                            // Because the user is already deleted, we use
                            // email-address saved in BRANCH_AUTHOR table
                            author.put("author", branchAuthor.getUserEmail());
                        } else {
                            author.put("author", user.getDisplayName());
                        }
                        author.put("created", branchAuthor.getCreated());
                    }
                }
                authors.put(ref, author);
            }
            return authors;

        } catch (Exception e) {
            log.error("Retriving branch author error", e);
            return Collections.EMPTY_MAP;
        }
    }
}
