package pw.yumc.MiaoChat.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA
 *
 * @author 喵♂呜
 * Created on 2017/8/8 16:23.
 */
public class ChatRuleTest {
    private transient static Pattern FORMAT_PATTERN = Pattern.compile("[\\[]([^\\[\\]]+)[]]");

    @Test
    public void testSplit() {
        List<String> formats = new ArrayList<>();
        String format = "[mvp+][player]: ";
        Matcher m = FORMAT_PATTERN.matcher(format);
        LinkedList<String> temp = new LinkedList<>();
        while (m.find()) {
            temp.add(m.group(1));
        }
        String tempvar = format;
        for (String var : temp) {
            String[] args = tempvar.split(Pattern.quote("[" + var + "]"), 2);
            if (!"".equals(args[0])) {
                formats.add(args[0]);
            }
            formats.add(var);
            tempvar = args.length == 2 ? args[1] : "";
        }
        if (!tempvar.isEmpty()) {
            formats.add(tempvar);
        }
        formats.forEach(System.out::println);
    }
}