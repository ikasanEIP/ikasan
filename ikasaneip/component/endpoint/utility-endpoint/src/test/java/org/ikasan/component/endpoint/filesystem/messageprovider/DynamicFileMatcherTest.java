package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.spec.component.endpoint.EndpointListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DynamicFileMatcherTest {

    @Mock
    private
    EndpointListener endpointListener;

    @Mock
    private
    Path path;

    @Mock
    private
    PathMatcher pathMatcher;

    @Mock
    private
    FileSystem fileSystem;


    @Test
    void should_call_spel_expression_and_replace_create_dynamic_file_name() {
        String spel = "#fileNamePattern.replace('xxx', 'abc')";
        String fileNamePattern = "blah.out.xxx.xml";
        String fileNamePatternOnDisc = "blah.out.abc.xml";
        Path fileNamePath = Path.of(fileNamePatternOnDisc);

        DynamicFileMatcher matcher
            = new DynamicFileMatcher(true, "parentPath", fileNamePattern, 1, endpointListener, spel);

        when(endpointListener.isActive()).thenReturn(true);
        when(path.getFileName()).thenReturn(fileNamePath);
        MockedStatic<FileSystems> fileSystemsMockedStatic;
        fileSystemsMockedStatic.when(FileSystems::getDefault).thenReturn(fileSystem);
        when(fileSystem.getPathMatcher("regex:blah.out.abc.xml")).thenReturn(pathMatcher);
        when(pathMatcher.matches(fileNamePath)).thenReturn(true);

        FileVisitResult result = matcher.match(path);

        assertEquals(FileVisitResult.CONTINUE, result);

        verify(endpointListener).isActive();
        verify(endpointListener).onMessage(any());
        verify(path).getFileName();
        verify(pathMatcher).matches(fileNamePath);

        fileSystemsMockedStatic.close();

        String fileNamePatternActual = (String) ReflectionTestUtils.getField(matcher, "fileNamePattern");
        assertEquals(fileNamePattern, fileNamePatternActual);
    }

    @Test
    void should_call_spel_expression_and_replace_create_dynamic_file_name_with_correlating_id_change() {
        String spel = "#fileNamePattern.replace('xxx', 'abc')";
        String fileNamePattern = "blah.out.xxx.xml";
        String fileNamePatternOnDisc = "blah.out.abc.xml";
        Path fileNamePath = Path.of(fileNamePatternOnDisc);

        DynamicFileMatcher matcher
            = new DynamicFileMatcher(true, "parentPath", fileNamePattern, 1, endpointListener, spel);
        matcher.setCorrelatingIdentifier(UUID.randomUUID().toString());
        when(endpointListener.isActive()).thenReturn(true);
        when(path.getFileName()).thenReturn(fileNamePath);
        MockedStatic<FileSystems> fileSystemsMockedStatic;
        fileSystemsMockedStatic.when(FileSystems::getDefault).thenReturn(fileSystem);
        when(fileSystem.getPathMatcher("regex:blah.out.abc.xml")).thenReturn(pathMatcher);
        when(pathMatcher.matches(fileNamePath)).thenReturn(true);

        FileVisitResult result = matcher.match(path);

        assertEquals(FileVisitResult.CONTINUE, result);

        verify(endpointListener).isActive();
        verify(endpointListener).onMessage(any());
        verify(path).getFileName();
        verify(pathMatcher).matches(fileNamePath);

        matcher.setCorrelatingIdentifier(UUID.randomUUID().toString());
        when(endpointListener.isActive()).thenReturn(true);
        when(path.getFileName()).thenReturn(fileNamePath);
        fileSystemsMockedStatic.when(FileSystems::getDefault).thenReturn(fileSystem);
        when(fileSystem.getPathMatcher("regex:blah.out.abc.xml")).thenReturn(pathMatcher);
        when(pathMatcher.matches(fileNamePath)).thenReturn(true);

        result = matcher.match(path);

        assertEquals(FileVisitResult.CONTINUE, result);

        verify(endpointListener, times(2)).isActive();
        verify(endpointListener, times(2)).onMessage(any());
        verify(path, times(2)).getFileName();
        verify(pathMatcher, times(2)).matches(fileNamePath);

        fileSystemsMockedStatic.close();

        String fileNamePatternActual = (String) ReflectionTestUtils.getField(matcher, "fileNamePattern");
        assertEquals(fileNamePattern, fileNamePatternActual);
    }

    @Test
    void complex_spel_with_logic_and_replace_create_dynamic_file_name() {
        String spel = "#fileNamePattern.contains('xxx') ? #fileNamePattern.replace('xxx', 'abc') : (#fileNamePattern.contains('yyy') ? #fileNamePattern.replace('yyy', 'abc') : #fileNamePattern)";
        String fileNamePattern = "blah.out.xxx.xml";
        String fileNamePatternOnDisc = "blah.out.abc.xml";
        Path fileNamePath = Path.of(fileNamePatternOnDisc);

        DynamicFileMatcher matcher
            = new DynamicFileMatcher(true, "parentPath", fileNamePattern, 1, endpointListener, spel);

        when(endpointListener.isActive()).thenReturn(true);
        when(path.getFileName()).thenReturn(fileNamePath);
        MockedStatic<FileSystems> fileSystemsMockedStatic;
        fileSystemsMockedStatic.when(FileSystems::getDefault).thenReturn(fileSystem);
        when(fileSystem.getPathMatcher("regex:blah.out.abc.xml")).thenReturn(pathMatcher);
        when(pathMatcher.matches(fileNamePath)).thenReturn(true);

        FileVisitResult result = matcher.match(path);

        assertEquals(FileVisitResult.CONTINUE, result);

        verify(endpointListener).isActive();
        verify(endpointListener).onMessage(any());
        verify(path).getFileName();
        verify(pathMatcher).matches(fileNamePath);

        fileSystemsMockedStatic.close();

        String fileNamePatternActual = (String) ReflectionTestUtils.getField(matcher, "fileNamePattern");
        assertEquals(fileNamePattern, fileNamePatternActual);
    }

    @Test
    void complex_spel_with_logic_and_replace_create_dynamic_file_name_no_replacement() {
        String spel = "#fileNamePattern.contains('xxx') ? #fileNamePattern.replace('xxx', 'abc') : (#fileNamePattern.contains('yyy') ? #fileNamePattern.replace('yyy', 'abc') : #fileNamePattern)";
        String fileNamePattern = "blah.out.zzz.xml";
        String fileNamePatternOnDisc = "blah.out.abc.xml";
        Path fileNamePath = Path.of(fileNamePatternOnDisc);

        DynamicFileMatcher matcher
            = new DynamicFileMatcher(true, "parentPath", fileNamePattern, 1, endpointListener, spel);

        when(endpointListener.isActive()).thenReturn(true);
        when(path.getFileName()).thenReturn(fileNamePath);
        MockedStatic<FileSystems> fileSystemsMockedStatic;
        fileSystemsMockedStatic.when(FileSystems::getDefault).thenReturn(fileSystem);
        when(fileSystem.getPathMatcher("regex:blah.out.zzz.xml")).thenReturn(pathMatcher);
        when(pathMatcher.matches(fileNamePath)).thenReturn(true);

        FileVisitResult result = matcher.match(path);

        assertEquals(FileVisitResult.CONTINUE, result);

        verify(endpointListener).isActive();
        verify(endpointListener).onMessage(any());
        verify(path).getFileName();
        verify(pathMatcher).matches(fileNamePath);

        fileSystemsMockedStatic.close();

        String fileNamePatternActual = (String) ReflectionTestUtils.getField(matcher, "fileNamePattern");
        assertEquals(fileNamePattern, fileNamePatternActual);
    }

    @Test
    void should_not_call_spel_expression_if_null_uses_unchanged_file_name() {
        String fileNamePattern = "blah.out.xxx.xml";
        Path fileNamePath = Path.of(fileNamePattern);

        DynamicFileMatcher matcher
            = new DynamicFileMatcher(true, "parentPath", fileNamePattern, 1, endpointListener, null);

        when(endpointListener.isActive()).thenReturn(true);
        when(path.getFileName()).thenReturn(fileNamePath);
        MockedStatic<FileSystems> fileSystemsMockedStatic;
        fileSystemsMockedStatic.when(FileSystems::getDefault).thenReturn(fileSystem);
        when(fileSystem.getPathMatcher("regex:blah.out.xxx.xml")).thenReturn(pathMatcher);
        when(pathMatcher.matches(fileNamePath)).thenReturn(true);

        FileVisitResult result = matcher.match(path);

        assertEquals(FileVisitResult.CONTINUE, result);

        verify(endpointListener).isActive();
        verify(endpointListener).onMessage(any());
        verify(path).getFileName();
        verify(pathMatcher).matches(fileNamePath);

        fileSystemsMockedStatic.close();

        String fileNamePatternActual = (String) ReflectionTestUtils.getField(matcher, "fileNamePattern");
        assertEquals(fileNamePattern, fileNamePatternActual);
    }

}