Source Control
==============

This doc describes how UI source code artifacts are stored. We use Git for daily source control tasks, but make sure that source code is "backed up" to TFS as well. THE TFS BACKUP MUST BE MAINTAINED WITH REGULARITY, BECAUSE THERE IS NO OFFSITE BACKUP OF THE GIT SERVER.

The canonical Git repos live at: ssh://dv2wlss-linux1.absolute.com/var/git, and the corresponding TFS repos live at: http://tfs:8080/tfs, $/Projects/UI

All code lives in the UI repo:

    # Read only
    git clone -b develop git://dv2wlss-linux1.absolute.com/ui.git

    # With checkin privileges
    git clone -b develop ssh://USERNAME@dv2wlss-linux1.absolute.com/var/git/ui.git

For the time being, all development teams are expected to check push their release ready code here.

Git Setup
---------

On a Mac:

1. Install XCode from the App Store
1. From XCode, select Preferences, then Downloads tab, and ensure that Command Line Tools are installed.
1. Consider installing [GitHub for Mac](http://mac.github.com/).

On a PC:

1. Install [MSys Git](http://msysgit.github.io/).
1. Consider installing [GitHub for Windows](http://windows.github.com/).

Branching Practice
------------------

We use Vincent Driessen's branching practice, often called [Git Flow](http://nvie.com/posts/a-successful-git-branching-model/).

The master branch is only for _released_ code. The develop branch is only for _releasable_ code. When a team is preparing a release, they create a new branch from develop and fix all of their blocking issues there. When it is time for the release to happen, the team merges it back to develop and master, and tags master with the same name as the release branch.

### Release Branches

Release branches and tags are named after the site being released. Hotfix releases have their own release branch and/or tag.

    release_<site>_<version>

    release_cc-classic_1.0a

    release_am-web-admin_2.0

### Topic Branches

On most occasions, it is ok for developers to check small changes directly into the develop branch. But, for riskier work or work larger in scope, topic branches are appropriate.

Topic branch names consist of the product, work item and a brief description of the work. Replace spaces with "-" in each part, then join them together with "\_".

At time of writing, valid products are:

- _cc-classic_, Customer Center Classic integration.
- _am-web-admin_, Absolute Manage Web Admin.
- _platform_, for work that is related to more than one product.
- _scratch_, for work that is never intended for merge or release.

Here is a sample checkout, creating a new topic branch from develop:

    # site: cc-classic, ticket: 12345, description: "Add support for aggregated columns in reports"
    git checkout -b cc-classic_12345_add-support-for-aggregated-columns-in-reports develop

Rebasing and Other Changes to History
-------------------------------------

The history of the master and develop branches on origin is sacred, and not to be changed under any circumstances. 

Only rebase unpushed commits in topic branches. 

Only amend unpushed commits in topic branches. 

Only adjust history of unpushed commits in topic branches. 

_Break these rules, and the origin repo will reject your pushes._

It is permissible, and often recommended to squash merges from topic branches into develop. However, once that is done, the lifetime of the topic branch is effectively over. If further changes are required, start a new topic branch from develop.

Development Workflow for Topic Branches
---------------------------------------

### Branch Creation

Given a work item, create a new topic branch for it. If the work item is blocking a release, then branch from the release branch. Otherwise, branch from develop.

    # Create the topic branch, in this case from "develop".
    git checkout -b cc-classic_12345_remove-jqgrid develop

### Branch Check-ins

*Every commit should have a comment that is prefixed with the Work Item Number in "[]", e.g. [12345].*

In the hopefully rare circumstance that there is more than one Work Item associated with a checkin, write the numbers in comment like this: [12345, 67890]:

Commits checked into scratch branches should be tagged [NOMERGE].

Commits that have no related Work Item should be tagged [NONE].

    # Commit some changes
    git commit -am '[12345] Kill jqgrid with fire'

### Sharing Topic Branches

If appropriate, push the topic branch to origin for other developers to collaborate on, and / or for QA to test.

    # Push new branch to origin
    git push -u origin cc-classic_12345_remove-jqgrid

### Tracking the Develop Mainline

Long lived topic branches should track the develop mainline through rebasing. If the topic branch has not been pushed, then rebase it directly:

    git checkout cc-classic_12345_remove-jqgrid
    git rebase develop

If the branch has been pushed, then all consumers of the topic branch must coordinate with each other. Development in then topic branch will end, and a new, rebased topic branch will be created and pushed.

    # Remove the defunct topic branch from origin
    git push origin :cc-classic_12345_remove-jqgrid

    # Get latest develop
    git checkout develop
    git pull

    # Create a new topic branch, based on the old one
    git checkout -b cc-classic_12345_remove-jqgrid-1 cc-classic_12345_remove-jqgrid

    # Rebase the new topic branch
    git rebase develop

    # Push the new topic branch
    git push -u origin cc-classic_12345_remove-jqgrid-1

### Merging Topic Branches

Once the work is ready to merge, the topic branch may be removed from origin. Rebase it again, following the recipe in the previous section, then merge. It should be a fast forward merge.

    # Ensure that develop is up to date with origin.
    git pull

    # Checkout develop, the target of the merge.
    git checkout develop

    # Merge in the topic branch. In this case, we squash the commits first and then we commit the squashed version. Use your discretion.
    # If you want to keep history of changes use this command:
    git merge --no-squash cc-classic_12345_remove-jqgrid
    # If you want to combine all the commits into one commit then merge it with develop, then:
    git merge --squash cc-classic_12345_remove-jqgrid
    git commit -m '[12345] jqGrid killed, with fire.'

    # Push our changes to develop
    git push origin develop

    # Remove our topic branch from origin
    git push origin :cc-classic_12345_remove-jqgrid

    # Remove stale remote-tracking branches (when someone deleted a remote branch it will be still visible in other people's origin/remotes. This is to clean up those branches
    # 1- First report what branches will be pruned
    git remote prune --dry-run origin
    # 2- Then prune those branches
    git remote prune origin
    
### Interactive Rebase (squashing commits into one)
 
This procedure is done if you have a lot of commits and you need to commit (AND/OR do an easy code review of) them as one big commit. 

    # Ensure that develop is up to date with origin.
    git pull --rebase
    
    # run "git log" and record the SHA of the last commit (probably made by someone else) that is *before* your first commit.
    git log
        
    # Then run interactive rebase
    git rebase -i <SHA_FROM_PREVIOUS_STEP>
    
    # When the text editor window opens you have to change the all the "pick" to "squash". *All except the first one*.   
    # After that, save and exit the test editor and the rebase should automatically finish without errors.       

Git Cookbook
------------

Some of these may require a UNIX environment.

### Initialization

    # Clone a particular branch. Most developers should NOT have a local clone of the master branch!
    git clone -b develop ssh://USERNAME@dv2wlss-linux1.absolute.com/var/git/ui.git

### Differences

    # See what's different between the filesystem and the index
    git diff

    # See what's different between HEAD and the index
    git diff --cached

### Add, Moving, Deleting Files

    # Add a file to the index
    git add foo.js

    # Enter interactive mode for managing the index
    git add -i

    # Add a "remove" operation to the index
    git rm foo.js

    # Add a "move" operation to the index
    git mv foo.js bar.js

### Committing Changes to the Local Repo

    # Commit the changes in the index (prefer this)
    git commit

    # Commit the changes, setting the associaged commit message without opening an editor (or prefer this)
    git commit -m 'Lorem ipsum blah blah'

    # Commit all change to files known to Git, with a commit message (do this carefully)
    git commit -am 'Do this cautiously'

### Committing Changes to a Different Branch

    # Change to a different branch without losing changes in progress
    git stash
    git checkout cc-classic_12345_fix-bug-foo
    git stash apply

### Overwrite the Working Tree with the Tree at a Given Changeset

    git checkout cc-classic_12345_fix-bug-foo -- .
    git reset

### Apply Existing Changesets to the Current Branch

    # This will fail on merge commits, but you aren't doing those in topic branches anyway, are you?
    #
    # Ok, maybe you did. For those commits, you will need to cherry-pick them individually, with "-t 1".
    git cherry-pick develop..cc-classic_12345_fix-bug-foo

### Reverting Changes

    # Revert a change that hasn't been added to the index
    git checkout foo.js

    # Remove a change from the index, but leave the changes intact
    git reset HEAD foo.js

    # Remove a change from the index, and revert the changes
    git reset --hard HEAD foo.js

    # Undo a commit, leaving the changes intact in the working tree (and index). This is history manipulation,
    # not to be performed on develop.
    git reset --soft 'HEAD^'

### Merge Changes

    # Merge changes from a topic branch into develop, squashing all commits into one. After this is done,
    # all development on the topic branch must cease. If further changes are required, create a new topic branch.
    git checkout develop
    git merge --commit --squash -m '[WI number] Brief description of the change'

    # Merge changes from a topic branch into develop, without squashing the commits. If your commit history on
    # the branch is meaningful (i.e. messages are tagged with work items, changesets are sane, etc.), then
    # an unsquashed merge is fine.
    git merge --commit -m '[WI number] Brief description of the change'

    # Undo a failed, conflicted merge
    git merge --abort

### Manage Local Branches

    # List local branches
    git branch

    # List local branches with additional info regarding which branches are remotely tracked
    git branch -a

    # Change to a different branch
    git checkout develop

    # Create a new local branch, from develop (never from master)
    git checkout -b cc-classic_12345_fix-bug-foo develop

    # Remove a local branch
    git branch -d cc-classic_12345_fix-bug-foo

    # Rename a local branch (i.e. create a new branch based on the old one and then remove it)
    git checkout -b new-name old-name
    git branch -d old-name

### Manage Remote Branches

    # List remote branches
    git ls-remote origin

    # Track an existing remote branch
    git checkout --track origin/develop

    # Create or update a remote branch
    git push origin cc-classic_12345_fix-bug-foo

    # Set the remote for an existing local branch (e.g. a branch that you originally pushed)
    git branch --set-upstream-to=origin/cc-classic_12345_fix-bug-foo

    # Remove a remote branch
    git push origin :cc-classic_12345_fix-bug-foo

    # Rename a remote branch (i.e. create new branch based on old one and then remove it)
    git checkout --track origin/old-name
    git checkout -b new-name old-name
    git branch -d old-name
    git push origin :old-name

    # Fetch changes from origin, merging them into the current branch
    git pull

    # Fetch changes from origin without merging them into local branches
    git fetch

    # Given unmerged changes that have been fetched from origin, see what's new
    git log develop..origin/develop

### Examining the Log and Describing Branch State

    # See all of the commits that are in one branch, but not in another
    git log develop..cc-classic_12345_bug-fix-foo

    # See a patch for a particular commit
    git log -u -1 b40f7aa1c9171e2ca60797979c4e38afc693e9c8

    # See all of the files touched in a particular commit
    git diff-tree --no-commit-id --name-only -r b40f7aa1c9171e2ca60797979c4e38afc693e9c8

    # See all of the files touched in all of the commits in the current branch, compared to develop
    git diff-tree --name-only -r $(git merge-base HEAD develop)..HEAD

    # See a diff of all of the changes in the current branch compared to develop
    git diff-tree -r -u $(git merge-base HEAD develop)..HEAD

    # Print a string that describes the currents state of the current branch, suitable for tagging a build
    echo "$(git symbolic-ref HEAD 2> /dev/null | cut -b 12-)-$(git log --pretty=format:"%h" -1)"

TFS
----

Use of Git is not officially sanctioned at Absolute. So, a "backup" of code in Git exists in TFS. See $/UI/ui/branch-name.

Release Procedure
-----------------

TBD, but:

1. Merge release branch back into develop.
1. Merge release branch into master.
1. Tag master with the same name as the release branch.
1. Back the release branch up to a folder in TFS: $/UI/ui/branch-name.
1. Remove the release branch.

To back up code to TFS
----------------------

There is a merge script, coded in Ruby, that can create a TFS checkin based on the difference between a TFS folder and a Git branch. 

### Setup

Here's what is needed to run the merge script:

1. Be using a UNIX environment, such as Mac OS or Linux.
1. Have a [modern Ruby](https://rvm.io/) installed. 
1. Install Java, if necessary.
  - On Mac OS or Windows, go to http://java.com and download Java 7.
  - On Debian or Ubuntu Linux:

          sudo apt-get install default-jre

1. Install the [TFS commandline client](http://www.microsoft.com/en-ca/download/details.aspx?id=30661).
  1. Click "Download" and choose "TEE-CLC-11.0.0.1306.zip".
  1. Extract the archive, and move TEE-CLC-11.0.0 to /usr/local.
  1. Ensure that the following lines are in ~/.bash\_profile:

          # Add TF
          export TF_CLC_HOME=/usr/local/TEE-CLC-11.0.0
          export PATH=$PATH:/usr/local/TEE-CLC-11.0.0

  1. On Linux, configure active directory integration (beyond the scope of this document). See:
    - http://technet.microsoft.com/en-us/magazine/2008.12.linux.aspx
    - https://help.ubuntu.com/community/ActiveDirectoryWinbindHowto
  1. Start a new shell instance (new terminal window, for example).
1. Have a copy of the merge script, which is $/UI/merge-scripts. When choosing a workspace name (for substitution whereever it says &lt;workspace name&gt; in the following steps), consider using something like the following form: &lt;user name&gt;-&lt;workstation description&gt;. For example: AOSIPOV-OSX.

        # Create and change to the directory that will hold the scripts
        mkdir -p ~/src-tfs/ui/merge-scripts
        cd ~/src-tfs/ui/merge-scripts

        # Setup the workspace. Replace <workspace name> appropriately.
        tf workspace -new <workspace name> -collection:http://tfs:8080/tfs/DefaultCollection

        # Setup the mapping for $/UI/ui/merge-scripts.
        tf workfold -map $/UI/merge-scripts -workspace:<workspace name> $(pwd)

        # Get latest
        tf get

### Configuration

The merge script takes a configuration file that describes the mapping between Git branches and TFS folders.

Here is a sample configuration file. It has a TFS workspace name and a list of repos. The workspace should be specifically setup for doing merges (i.e. don't use the same workspace for anything else). The script will create and destroy workspace mappings as needed over the course of execution. It will also attempt to create the workspace, so ignore complaints if and when it fails to so in the case that the workspace already exists (as it usually will).

    workspace: "DDUCHENE4-MERGE"
    repos:
      - name: "UI: Master"
        path: "ui/master"
        tfs: "$/UI/ui/master"
        git: 
          url: "git://dv2wlss-linux1.absolute.com/ui.git"
          branch: "master"

      - name: "UI: Develop"
        path: "ui/develop"
        tfs: "$/UI/ui/develop"
        git: 
          url: "git://dv2wlss-linux1.absolute.com/ui.git"
          branch: "develop"

### Invocation

See the following examples for how to invoke the merge script.

    # See commandline options
    ruby merge_to_tfs.rb --help

    # Merge all of the branches in the configuration to TFS
    ruby merge_to_tfs.rb --config config.dduchene.full.yml --working_path ../merge-working

    # Merge a particular branch in the configuration to TFS
    ruby merge_to_tfs.rb --config config.dduchene.full.yml --working_path ../merge-working --repo 'UI: Master'

Once execution has completed, the working folder will contain a TFS checkin, ready to commit. It is up to the person running the merge scripts to sanity check the pending commit, and then commit it. The following procedure must be performed for each branch with a pending commit (i.e. each repo in the configuration file that the merge script ran against).

    # Change to the working folder
    cd ../merge-working/ui/master/tfs

    # Sanity check the commit, then check it in
    tf commit -comment:'Merge Git to TFS' -recursive .
