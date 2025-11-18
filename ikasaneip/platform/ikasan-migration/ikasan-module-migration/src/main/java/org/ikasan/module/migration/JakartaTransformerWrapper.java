package org.ikasan.module.migration;

import org.eclipse.transformer.Transformer;
import org.eclipse.transformer.cli.JakartaTransformerCLI;
import org.eclipse.transformer.cli.TransformerCLI;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

public class JakartaTransformerWrapper {


    /**
     * Runs the Jakarta Transformer to transform the source directory to the target directory.
     *
     * @param src the source directory path
     * @param tgt the target directory path
     * @param moduleName the name of the module
     * @return {@link Transformer.ResultCode} representing the result code after transformation
     * @throws IOException if an I/O error occurs during the transformation process
     */
    public static Transformer.ResultCode run(String src, String tgt, String moduleName) throws IOException {
        String[] transformerArgs = {src+"/"+moduleName, tgt+"/"+moduleName};
        JakartaTransformerCLI cli = new JakartaTransformerCLI(System.out, System.err, transformerArgs);
        Transformer.ResultCode resultCode =  TransformerCLI.runWith(cli);
        copyDirectory(Path.of(tgt+"/"+moduleName), Path.of(src+"/"+moduleName));
        deleteDirectory(Path.of(tgt));
        return resultCode;
    }


    /**
     * Recursively copies a directory from a source to a destination path.
     *
     * @param source      the source directory path to copy
     * @param destination the destination directory path to copy to
     * @throws IOException if an I/O error occurs during the copying process
     */
    private static void copyDirectory(Path source, Path destination) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetPath = destination.resolve(source.relativize(dir));
                if (!Files.exists(targetPath)) {
                    Files.createDirectories(targetPath); // Create the directory if it doesn't exist
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, destination.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // Handle file visit failures, e.g., permissions issues
                System.err.println("Failed to visit file: " + file + " - " + exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Deletes a directory and all its contents recursively.
     *
     * @param directoryToDelete the path to the directory to be deleted
     * @throws IOException if an I/O error occurs during the deletion process
     */
    private static void deleteDirectory(Path directoryToDelete) throws IOException {
            // Traverse the directory in depth-first order, sorting in reverse to delete inner items first
            Files.walk(directoryToDelete)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

    }
}