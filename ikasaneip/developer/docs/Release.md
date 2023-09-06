[../](../../../Readme.md)
![Ikasan](quickstart-images/Ikasan-title-transparent.png)
# Release Instructions
The following are the standard instructions for an Ikasan binary release.
 
- Create new branch 'release/3.4.0' from the branch to be released. 
  ```
  git checkout -b release/3.4.0
  ```
- Check you are on the correct release/3.4.0 branch

  ```
  git branch -a -vvv
  * release/3.4.0  
  ```
  
- Update all references to 3.4.0-SNAPSHOT to version you are releasing ie. 3.4.0

   ```
   mvn versions:set -DnewVersion=3.4.0 -DprocessAllModules -DgenerateBackupPoms=false
   ```
- Update reference in md files by searching for 3.4.0-SNAPSHOT using an IDE's find/replaceAll.

  Check to confirm the expected changes in all files
    ```
    git status
    ```
    Here you should see all files that you expect to have been modified.

- Commit all changes 
  ```
    git commit -a -m 'Update references to 3.4.0'
   ``` 

- Tag the changes and push to the remote repository. 
  ```
    git tag -a ikasaneip-3.4.0 -m "tag 3.4.0"
    git tag --list
    git push origin ikasaneip-3.4.0
   ``` 
   This new tag will be picked up by Travis and built and deployed to OSS.

- Check Travis for successful build and deploy

- Check Sonotype OSS for the created Ikasan staging repository.
    Open in a browser and login to https://oss.sonatype.org/index.html#welcome
    Select Staging Repositories and find the Ikasan repository
    If not already closed, close it
    Finally select release
 
- Update to the next SNAPSHOT version
   Update references in pom.xmls
    ```
    mvn versions:set -DnewVersion=3.5.0-SNAPSHOT -DprocessAllModules -DgenerateBackupPoms=false
    ```
- Update reference in md files by searching for 3.4.0 using an IDE's find/replaceAll.

- Check to confirm the expected changes in all files
    ```
    git status
    ```

- Commit all changes
  ```
  git commit -a -m 'Update references to 3.5.0-SNAPSHOT'
  push
  ```

- In GitHub, rename the release/3.4.0 branch to 3.5.x and change to be the default branch
