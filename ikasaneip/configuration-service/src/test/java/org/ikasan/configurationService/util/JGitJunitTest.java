package org.ikasan.configurationService.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JGitJunitTest {

    @Test
    public void test() throws IOException, GitAPIException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
//        Repository repository = builder.setGitDir(new File("/my/git/directory"))
//            .readEnvironment() // scan environment GIT_* variables
//            .findGitDir() // scan up the file system tree
//            .build();

        Git git = Git.cloneRepository()
            .setURI( "https://github.com/mick-stewart73/test-jgit-repo.git" )
            .setDirectory( new File("./test-repo") )
            .call();

        FileOutputStream outputStream = new FileOutputStream("./test-repo/test.txt");
        outputStream.write("this is some stuff".getBytes());
        outputStream.close();

        git.add().addFilepattern(".").call();
        git.commit().setMessage("this is the commit message").call();

        git.push()
            .setCredentialsProvider( new UsernamePasswordCredentialsProvider( "mick-stewart73", "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" ))
            .call();
    }
}
