package com.doc.format.util;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2025/3/12 17:01
 */

import java.util.Stack;

public class JsonBracketCompleter {

    public static String completeBrackets(String json) {
        Stack<Character> bracketStack = new Stack<>();
        boolean inString = false;
        boolean escapeActive = false;

        for (char c : json.toCharArray()) {
            if (inString) {
                if (escapeActive) {
                    escapeActive = false;
                } else {
                    if (c == '\\') {
                        escapeActive = true;
                    } else if (c == '"') {
                        inString = false;
                    }
                }
            } else {
                switch (c) {
                    case '"':
                        inString = true;
                        break;
                    case '{':
                        bracketStack.push('}');
                        break;
                    case '[':
                        bracketStack.push(']');
                        break;
                    case '}':
                    case ']':
                        if (!bracketStack.isEmpty() && bracketStack.peek() == c) {
                            bracketStack.pop();
                        }
                        break;
                }
            }
        }

        StringBuilder completedJson = new StringBuilder(json);
        while (!bracketStack.isEmpty()) {
            completedJson.append(bracketStack.pop());
        }
        return completedJson.toString();
    }

    public static void main(String[] args) {
        String incompleteJson = "{\"name\":\"Alice\",\"hobbies\":[\"reading\",\"music\"]";
        System.out.println("补全前：\n" + incompleteJson);
        System.out.println("补全后：\n" + completeBrackets(incompleteJson));

        String test2 = "{\"data\":[[1,2,3,{\"key\":\"value\"}";
        System.out.println("\n补全前：\n" + test2);
        System.out.println("补全后：\n" + completeBrackets(test2));
    }
}