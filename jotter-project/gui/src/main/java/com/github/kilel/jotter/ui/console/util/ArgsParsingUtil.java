package com.github.kilel.jotter.ui.console.util;

import com.beust.jcommander.JCommander;
import com.github.kilel.jotter.log.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgsParsingUtil {

    private static JCommander createParser() {
        JCommander argsParser = new JCommander();
        argsParser.addCommand("get", new ConsoleCommandParams.Get());
        argsParser.addCommand("add", new ConsoleCommandParams.Add());
        argsParser.addCommand("remove", new ConsoleCommandParams.Remove());
        argsParser.addCommand("update", new ConsoleCommandParams.Update());
        argsParser.addCommand("list", new ConsoleCommandParams.List());
        argsParser.addCommand("stop", new ConsoleCommandParams.Stop());
        return argsParser;
    }

    public static JCommander parseCommand(String command) {
        final JCommander parser = createParser();
        final List<String> result = ArgsParsingUtil.parseCommandLine(command);
        parser.parse(result.toArray(new String[result.size()]));
        return parser;
    }

    public static List<String> parseCommandLine(String command) {
        final List<String> args = new ArrayList<>();
        if (command != null && !isEmpty(command)) {
            final Pattern pattern = Pattern.compile(
                    "(?:(?:([\"\'])(?:\\\\\\1|.)*?(?:\\1|$))|(?:\\\\[\"\'\\s]|[^\\s]))+");
            final Matcher matcher = pattern.matcher(command);

            while (matcher.find()) {
                String arg = command.substring(matcher.start(), matcher.end()).trim();
                if (!isEmpty(arg)) {
                    args.add(unquoteIfNeeded(arg));
                }
            }
        }
        LogManager.commonLog().debug(String.format("command converted from [%s] to [%s]", command, args));
        return args;
    }


    private static String unquoteIfNeeded(String arg) {
        final StringBuilder builder = new StringBuilder();
        if (arg != null && !isEmpty(arg)) {
            int beginIdx = arg.codePointCount(0, arg.length());
            int endIdx = -1;

            for (int i = 0; i < beginIdx; ++i) {
                int uchar = arg.codePointAt(i);
                if (uchar != 34 && uchar != 39) {
                    if (uchar == 92) {
                        ++i;
                        if (i < beginIdx) {
                            builder.appendCodePoint(arg.codePointAt(i));
                        }
                    } else {
                        builder.appendCodePoint(uchar);
                    }
                } else if (endIdx != -1) {
                    if (uchar == endIdx) {
                        endIdx = -1;
                    } else {
                        builder.appendCodePoint(uchar);
                    }
                } else {
                    endIdx = uchar;
                }
            }
        }
        return builder.toString();
    }

    private static boolean isEmpty(String var0) {
        return var0.length() == 0;
    }
}
