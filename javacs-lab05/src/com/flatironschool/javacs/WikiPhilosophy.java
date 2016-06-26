package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	final static String philosophyPage = "https://en.wikipedia.org/wiki/Philosophy";
	private static ArrayList<String> visitedLinks = new ArrayList<String>();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String startURL = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		visitedLinks.add(startURL);
		findLink(startURL);

	    for (String s : visitedLinks) {
	    	System.out.println(s);
	    }
	}

	private static void findLink(String url) throws IOException {
		Elements paragraphs = wf.fetchWikipedia(url);

		for (Element para : paragraphs) {
			Elements children = para.children();
			Deque<Character> parenStack = new ArrayDeque<Character>();

			Iterable<Node> iter = new WikiNodeIterable(para);
			for (Node node : iter) {
				if (node instanceof TextNode) {
					String text = ((TextNode)node).getWholeText();
					modifyParenStack(text, parenStack);
				} else {
					if (node instanceof Element) {
						Element eNode = (Element) node;
						if (eNode.tagName().equals("a") && validLink(eNode, parenStack)) {
							String newURL = eNode.absUrl("href");
							visitedLinks.add(newURL);
							if (!newURL.equals(philosophyPage)) {
								findLink(newURL);
							}
							return;
						}
					}
				}
			}
	    }		
	}

	private static boolean validLink(Element newLink, Deque<Character> parenStack) {
		if (visitedLinks.contains(newLink.absUrl("href")) || parenStack.size() > 0) {
			return false;
		}

		return true;
	}

	private static void modifyParenStack(String string, Deque<Character> parenStack) {
		for (int i = 0; i < string.length(); ++i) {
			if (string.charAt(i) == '(') {
				parenStack.push('(');
			} else if (string.charAt(i) == ')') {
				parenStack.pop();
			}
		}
	}
}